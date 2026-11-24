package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.util.BarrierStoneCache;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent.FinalizeSpawn;
import net.minecraftforge.event.entity.living.MobSpawnEvent.PositionCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class GuaniaoSpawnBlocker {
   public static final String BLOCKED_NAMESPACE = "guaniao";
   private static volatile Set<EntityType<?>> guaniaoTypesCache;

   private GuaniaoSpawnBlocker() {
   }

   private static Set<EntityType<?>> guaniaoTypes() {
      Set<EntityType<?>> cached = guaniaoTypesCache;
      if (cached != null) {
         return cached;
      } else {
         Set<EntityType<?>> set = new HashSet<>();

         for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(type);
            if (key != null && "guaniao".equals(key.m_135827_())) {
               set.add(type);
            }
         }

         guaniaoTypesCache = set;
         return set;
      }
   }

   private static boolean isGuaniao(EntityType<?> type) {
      return type != null && guaniaoTypes().contains(type);
   }

   private static boolean inBarrierRange(LevelAccessor level, BlockPos pos) {
      ServerLevel sl = null;
      if (level instanceof ServerLevel s) {
         sl = s;
      } else if (level instanceof ServerLevelAccessor sla) {
         sl = sla.m_6018_();
      }

      return sl != null && pos != null ? BarrierStoneCache.isBlocked(sl, pos) : false;
   }

   @SubscribeEvent
   public static void onPositionCheck(PositionCheck event) {
      if (isGuaniao(event.getEntity().m_6095_())) {
         if (inBarrierRange(event.getLevel(), event.getEntity().m_20183_())) {
            event.setResult(Result.DENY);
         }
      }
   }

   @SubscribeEvent
   public static void onFinalizeSpawn(FinalizeSpawn event) {
      if (isGuaniao(event.getEntity().m_6095_())) {
         BlockPos spawnPos = BlockPos.m_274561_(event.getX(), event.getY(), event.getZ());
         if (inBarrierRange(event.getLevel(), spawnPos)) {
            event.setSpawnCancelled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onEntityJoin(EntityJoinLevelEvent event) {
      if (!event.getLevel().m_5776_()) {
         if (isGuaniao(event.getEntity().m_6095_())) {
            BlockPos p = BlockPos.m_274561_(event.getEntity().m_20185_(), event.getEntity().m_20186_(), event.getEntity().m_20189_());
            if (inBarrierRange(event.getLevel(), p)) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerInteract(PlayerInteractEvent event) {
      if (!event.getLevel().m_5776_()) {
         if (event.getItemStack().m_41720_() instanceof SpawnEggItem egg) {
            EntityType<?> type = egg.m_43228_(event.getItemStack().m_41783_());
            if (isGuaniao(type)) {
               if (inBarrierRange(event.getLevel(), event.getPos())) {
                  event.setCanceled(true);
                  event.setCancellationResult(InteractionResult.FAIL);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         MinecraftServer server = event.getServer();
         if (server.m_129921_() % 20 == 0) {
            for (ServerLevel level : server.m_129785_()) {
               List<BlockPos> stones = BarrierStoneCache.stones(level.m_46472_());
               if (!stones.isEmpty()) {
                  int minY = level.m_141937_();
                  int maxY = level.m_151558_();

                  for (BlockPos stone : stones) {
                     int chunkX = stone.m_123341_() >> 4;
                     int chunkZ = stone.m_123343_() >> 4;
                     AABB area = new AABB(chunkX - 5 << 4, minY, chunkZ - 5 << 4, chunkX + 5 + 1 << 4, maxY, chunkZ + 5 + 1 << 4);

                     for (Entity e : level.m_6249_((Entity)null, area, ent -> isGuaniao(ent.m_6095_()))) {
                        if (!e.m_213877_()) {
                           e.m_146870_();
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
