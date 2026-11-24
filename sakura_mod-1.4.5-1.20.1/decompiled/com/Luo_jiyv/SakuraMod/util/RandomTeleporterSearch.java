package com.Luo_jiyv.SakuraMod.util;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.network.RandomTeleporterEffectPacket;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class RandomTeleporterSearch {
   static final int MAX_REQUESTS = 32;
   private static final int MAX_STEPS_PER_TICK = 128;
   private static final long TICK_BUDGET = TimeUnit.MILLISECONDS.toNanos(2L);
   private static final long REQUEST_TIMEOUT = TimeUnit.SECONDS.toNanos(90L);
   private static final String MESSAGE = "item.sakura_mod.random_teleporter.";
   private static final Map<UUID, RandomTeleporterSearch.Job> JOBS = new HashMap<>();
   private static final ArrayDeque<RandomTeleporterSearch.Job> READY = new ArrayDeque<>();
   @Nullable
   private static CompletableFuture<BlockPos> biomeWork;

   private RandomTeleporterSearch() {
   }

   public static boolean start(
      ServerLevel level, ServerPlayer player, Set<ResourceLocation> biomeIds, Set<ResourceLocation> structureIds, int minRadius, int maxRadius
   ) {
      if (JOBS.containsKey(player.m_20148_())) {
         tell(player, "already_searching", ChatFormatting.YELLOW);
         return false;
      } else if (JOBS.size() >= 32) {
         RandomTeleporterEffectPacket.sendTracking(player, RandomTeleporterEffectPacket.Phase.SHATTER, player.m_20182_());
         tell(player, "busy", ChatFormatting.YELLOW);
         return false;
      } else {
         WorldBorder border = level.m_6857_();
         RandomTeleporterSearch.Area area = new RandomTeleporterSearch.Area(
            player.m_20183_(), minRadius, maxRadius, border.m_61955_(), border.m_61956_(), border.m_61957_(), border.m_61958_()
         );
         RandomTeleporterSearch.Job job = new RandomTeleporterSearch.Job(level, player, area, biomeIds, structureIds);
         JOBS.put(player.m_20148_(), job);
         READY.addLast(job);
         job.syncSearchEffect();
         tell(player, "searching", ChatFormatting.AQUA);
         return true;
      }
   }

   static void cancel(UUID uuid) {
      RandomTeleporterSearch.Job job = JOBS.get(uuid);
      if (job != null) {
         finish(job);
      }
   }

   static void clear() {
      for (RandomTeleporterSearch.Job job : JOBS.values()) {
         job.cancelled = true;
      }

      JOBS.clear();
      READY.clear();
      biomeWork = null;
      RandomTeleporterQueue.clear();
      RandomTeleporterAsync.shutdown();
   }

   private static void finish(RandomTeleporterSearch.Job job) {
      if (JOBS.remove(job.player.m_20148_(), job)) {
         retire(job);
      }
   }

   private static void retire(RandomTeleporterSearch.Job job) {
      READY.remove(job);
      job.cancelled = true;
      RandomTeleporterQueue.withdraw(job.player.m_20148_());
      job.releaseChunk();
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         RandomTeleporterQueue.tick();
         if (biomeWork != null && biomeWork.isDone()) {
            biomeWork = null;
         }

         if (!JOBS.isEmpty()) {
            long now = System.nanoTime();
            MinecraftServer server = event.getServer();
            boolean refreshSearchEffects = server.m_129921_() % 20 == 0;
            Iterator<RandomTeleporterSearch.Job> iterator = JOBS.values().iterator();

            while (iterator.hasNext()) {
               RandomTeleporterSearch.Job job = iterator.next();
               boolean gone = server.m_6846_().m_11259_(job.player.m_20148_()) != job.player || job.player.m_284548_() != job.level || !job.player.m_6084_();
               if (gone || now - job.deadline >= 0L) {
                  iterator.remove();
                  retire(job);
                  if (!gone) {
                     job.showFailure("timeout");
                  }
               } else if (refreshSearchEffects) {
                  job.syncSearchEffect();
               }
            }

            int waiting = 0;

            for (int i = 0; i < 128 && waiting < READY.size() && System.nanoTime() - now < TICK_BUDGET && event.haveTime(); i++) {
               RandomTeleporterSearch.Job job = READY.removeFirst();

               try {
                  waiting = job.step() ? 0 : waiting + 1;
               } catch (RuntimeException var9) {
                  SakuraMod.LOGGER.error("Random teleporter failed for {}", job.player.m_20148_(), var9);
                  job.fail("fail");
                  waiting = 0;
               }

               if (!job.cancelled) {
                  READY.addLast(job);
               }
            }
         }
      }
   }

   private static void tell(ServerPlayer player, String key, ChatFormatting color) {
      player.m_5661_(Component.m_237115_("item.sakura_mod.random_teleporter." + key).m_130940_(color), true);
   }

   record Area(BlockPos origin, int minRadius, int maxRadius, double minX, double minZ, double maxX, double maxZ) {
      boolean insideBorder(long x, long z) {
         return x + 0.5 > this.minX && x + 0.5 < this.maxX && z + 0.5 > this.minZ && z + 0.5 < this.maxZ;
      }

      boolean inRing(BlockPos pos) {
         double dx = (double)pos.m_123341_() - this.origin.m_123341_();
         double dz = (double)pos.m_123343_() - this.origin.m_123343_();
         double distanceSquared = dx * dx + dz * dz;
         return distanceSquared >= (double)this.minRadius * this.minRadius && distanceSquared <= (double)this.maxRadius * this.maxRadius;
      }

      @Nullable
      BlockPos pointAt(double radius, double angle) {
         long x = this.origin.m_123341_() + Math.round(Math.cos(angle) * radius);
         long z = this.origin.m_123343_() + Math.round(Math.sin(angle) * radius);
         return this.insideBorder(x, z) ? new BlockPos((int)x, this.origin.m_123342_(), (int)z) : null;
      }
   }

   private static final class Job {
      private final ServerLevel level;
      private final ServerPlayer player;
      private final RandomTeleporterSearch.Area area;
      private final Set<ResourceLocation> biomes;
      private final Set<ResourceLocation> structures;
      private final int mode;
      private final long deadline = System.nanoTime() + RandomTeleporterSearch.REQUEST_TIMEOUT;
      private volatile boolean cancelled;
      private RandomTeleporterStructureSearch structureSearch;
      private RandomTeleporterStructureSearch.Candidate candidate;
      private int probes;
      private RandomTeleporterBiomeSearch biomeSearch;
      private boolean asyncBiome;
      private CompletableFuture<BlockPos> biomeResult;
      private BlockPos destination;
      private RandomTeleporterQueue.Request chunkRequest;
      private RandomTeleporterLanding landing;
      private BlockPos safeLanding;
      private long arrivalTick;

      private Job(ServerLevel level, ServerPlayer player, RandomTeleporterSearch.Area area, Set<ResourceLocation> biomes, Set<ResourceLocation> structures) {
         this.level = level;
         this.player = player;
         this.area = area;
         this.biomes = biomes;
         this.structures = structures;
         this.mode = RandomTeleporterSettings.getMode(level.m_7654_(), player.m_20148_());
      }

      boolean step() {
         if (this.destination != null) {
            return this.stepLanding();
         } else if (!this.structures.isEmpty()) {
            return this.stepStructure();
         } else if (!this.biomes.isEmpty()) {
            return this.stepBiome();
         } else {
            RandomSource random = RandomSource.m_216327_();

            for (int i = 0; i < 32; i++) {
               double fraction = random.m_188500_();
               double radius = this.area.minRadius() + fraction * fraction * (this.area.maxRadius() - this.area.minRadius());
               BlockPos pos = this.area.pointAt(radius, random.m_188500_() * Math.PI * 2.0);
               if (pos != null) {
                  this.prepareLanding(pos);
                  return true;
               }
            }

            this.fail("fail");
            return true;
         }
      }

      private boolean stepStructure() {
         if (this.structureSearch == null) {
            if (!this.level.m_215010_().m_220467_()) {
               this.fail("no_matching_structure");
               return true;
            }

            this.structureSearch = new RandomTeleporterStructureSearch(
               this.level, this.area, this.structures, RandomSource.m_216327_().m_188500_() * Math.PI * 2.0
            );
         }

         if (this.chunkRequest != null) {
            ChunkAccess chunk = this.chunkRequest.poll();
            if (chunk == null) {
               if (!this.chunkRequest.failedOrTimedOut()) {
                  return false;
               } else {
                  this.fail("timeout");
                  return true;
               }
            } else {
               boolean matches = this.structureSearch.matches(chunk);
               this.releaseChunk();
               if (matches) {
                  this.prepareLanding(this.candidate.destination());
               }

               this.candidate = null;
               return true;
            }
         } else {
            if (this.candidate == null) {
               if (this.probes >= 32 || this.structureSearch.isDone()) {
                  this.fail("no_matching_structure");
                  return true;
               }

               this.candidate = this.structureSearch.step();
               if (this.candidate == null) {
                  return true;
               }
            }

            if (!this.insideLiveBorder(this.candidate.destination())) {
               RandomTeleporterQueue.withdraw(this.player.m_20148_());
               this.candidate = null;
               return true;
            } else {
               this.chunkRequest = RandomTeleporterQueue.request(this.player.m_20148_(), this.level, this.candidate.chunk(), ChunkStatus.f_62315_);
               if (this.chunkRequest == null) {
                  return false;
               } else {
                  this.probes++;
                  return true;
               }
            }
         }
      }

      private boolean stepBiome() {
         if (this.biomeSearch == null) {
            ServerChunkCache source = this.level.m_7726_();
            ChunkGenerator generator = source.m_8481_();
            BiomeSource biomeSource = generator.m_62218_();
            this.asyncBiome = Config.isRandomTeleporterAsyncSearch()
               && generator.getClass() == NoiseBasedChunkGenerator.class
               && (
                  biomeSource.getClass() == MultiNoiseBiomeSource.class
                     || biomeSource.getClass() == TheEndBiomeSource.class
                     || biomeSource.getClass() == FixedBiomeSource.class
                     || biomeSource.getClass() == CheckerboardColumnBiomeSource.class
               );
            this.biomeSearch = new RandomTeleporterBiomeSearch(this.area, biomeSource, source.m_214994_().m_224579_(), this.biomes);
         }

         BlockPos pos;
         if (this.asyncBiome) {
            if (this.biomeResult == null) {
               if (RandomTeleporterSearch.biomeWork != null) {
                  return false;
               }

               this.biomeResult = CompletableFuture.supplyAsync(() -> this.biomeSearch.search(() -> this.cancelled), RandomTeleporterAsync.get());
               RandomTeleporterSearch.biomeWork = this.biomeResult;
               return true;
            }

            if (!this.biomeResult.isDone()) {
               return false;
            }

            pos = this.biomeResult.getNow(null);
         } else {
            this.biomeSearch.step();
            if (!this.biomeSearch.isDone()) {
               return true;
            }

            pos = this.biomeSearch.result();
         }

         if (pos == null) {
            this.fail("no_matching_biome");
         } else {
            this.prepareLanding(pos);
         }

         return true;
      }

      private void prepareLanding(BlockPos pos) {
         this.destination = pos;
         RandomTeleporterSearch.tell(this.player, "preparing", ChatFormatting.AQUA);
      }

      private boolean stepLanding() {
         if (!this.insideLiveBorder(this.destination)) {
            this.fail("fail");
            return true;
         } else if (this.safeLanding != null && this.level.m_46467_() < this.arrivalTick) {
            return false;
         } else if (this.chunkRequest == null) {
            this.chunkRequest = RandomTeleporterQueue.request(this.player.m_20148_(), this.level, new ChunkPos(this.destination), ChunkStatus.f_62326_);
            return this.chunkRequest != null;
         } else {
            ChunkAccess chunk = this.chunkRequest.poll();
            if (chunk == null) {
               if (!this.chunkRequest.failedOrTimedOut()) {
                  return false;
               } else {
                  this.fail("timeout");
                  return true;
               }
            } else if (this.safeLanding != null) {
               if (RandomTeleporterLanding.isSafe(chunk, this.safeLanding)) {
                  this.teleport();
               } else {
                  this.fail("fail");
               }

               return true;
            } else {
               if (this.landing == null) {
                  this.landing = new RandomTeleporterLanding(this.level, this.destination, this.player.m_146904_(), this.mode);
               }

               BlockPos safe = this.landing.step(chunk);
               if (safe != null) {
                  this.safeLanding = safe;
                  this.arrivalTick = this.level.m_46467_() + 13L;
                  RandomTeleporterEffectPacket.sendNear(this.player, RandomTeleporterEffectPacket.Phase.PREVIEW, Vec3.m_82539_(safe));
               } else if (this.landing.isDone()) {
                  this.fail("fail");
               }

               return true;
            }
         }
      }

      private void teleport() {
         Vec3 departure = this.player.m_20182_();
         RandomTeleporterQueue.staggerTeleports();
         RandomTeleporterLanding.teleport(this.level, this.player, this.safeLanding);
         RandomTeleporterSearch.finish(this);
         RandomTeleporterEffectPacket.sendTracking(this.player, RandomTeleporterEffectPacket.Phase.DEPARTURE, departure);
         RandomTeleporterEffectPacket.sendNear(this.player, RandomTeleporterEffectPacket.Phase.ARRIVAL, this.player.m_20182_());
      }

      private boolean insideLiveBorder(BlockPos pos) {
         return this.level.m_6857_().m_156093_(pos.m_123341_() + 0.5, pos.m_123343_() + 0.5);
      }

      private void releaseChunk() {
         if (this.chunkRequest != null) {
            this.chunkRequest.release();
            this.chunkRequest = null;
         }
      }

      private void fail(String message) {
         RandomTeleporterSearch.finish(this);
         this.showFailure(message);
      }

      private void syncSearchEffect() {
         RandomTeleporterEffectPacket.sendTracking(this.player, RandomTeleporterEffectPacket.Phase.SEARCH, this.player.m_20182_());
      }

      private void showFailure(String message) {
         RandomTeleporterEffectPacket.sendTracking(this.player, RandomTeleporterEffectPacket.Phase.SHATTER, this.player.m_20182_());
         RandomTeleporterSearch.tell(this.player, message, ChatFormatting.RED);
      }
   }
}
