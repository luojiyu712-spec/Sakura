package com.Luo_jiyv.SakuraMod.command;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.mixin.ChunkMapEntityAccessor;
import com.Luo_jiyv.SakuraMod.mixin.EntityRemovalReasonAccessor;
import com.Luo_jiyv.SakuraMod.mixin.ServerLevelEntityAccessor;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class DeleteCommand {
   private static final int TYPE_LOCK_RADIUS = 16;
   private static final int RETRY_INTERVAL = 5;
   private static final int WATCHDOG_CLEAN_TICKS = 20;
   private static final int WATCHDOG_MAX_TICKS = 600;
   private static final Set<UUID> LOCKED_UUIDS = ConcurrentHashMap.newKeySet();
   private static final Map<DeleteCommand.TypeInDimension, Set<BlockPos>> TYPE_LOCKS = new ConcurrentHashMap<>();
   private static final Map<UUID, DeleteCommand.Watchdog> WATCHDOGS = new HashMap<>();

   @SubscribeEvent
   public static void onRegisterCommands(RegisterCommandsEvent event) {
      event.getDispatcher()
         .register(
            (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("delete").requires(source -> source.m_6761_(3)))
                  .then(Commands.m_82127_("unlock").executes(ctx -> unlock((CommandSourceStack)ctx.getSource()))))
               .then(
                  ((RequiredArgumentBuilder)Commands.m_82129_("targets", EntityArgument.m_91460_()).executes(ctx -> run(ctx, false)))
                     .then(Commands.m_82127_("locktype").executes(ctx -> run(ctx, true)))
               )
         );
   }

   private static int unlock(CommandSourceStack source) {
      int uuids = LOCKED_UUIDS.size();
      int areas = TYPE_LOCKS.values().stream().mapToInt(Set::size).sum();
      clearLocks();
      source.m_288197_(() -> Component.m_237113_("已解除 " + uuids + " 个 UUID 锁定和 " + areas + " 个范围类型锁，并停止对应的复查"), true);
      return uuids + areas;
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      clearLocks();
   }

   private static void clearLocks() {
      LOCKED_UUIDS.clear();
      TYPE_LOCKS.clear();
      WATCHDOGS.clear();
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onEntityJoin(EntityJoinLevelEvent event) {
      if (isAdmissionBlocked(event.getEntity(), event.loadedFromDisk())) {
         event.setCanceled(true);
      }
   }

   public static boolean isAdmissionBlocked(Entity entity, boolean loadedFromDisk) {
      if (!((!LOCKED_UUIDS.isEmpty() || !TYPE_LOCKS.isEmpty()) && !(entity instanceof Player) && entity.m_9236_() instanceof ServerLevel level)) {
         return false;
      } else if (LOCKED_UUIDS.contains(entity.m_20148_())) {
         return true;
      } else if (!loadedFromDisk && !TYPE_LOCKS.isEmpty()) {
         Set<BlockPos> centers = TYPE_LOCKS.get(new DeleteCommand.TypeInDimension(level.m_46472_(), entity.m_6095_()));
         if (centers == null) {
            return false;
         } else {
            if (entity instanceof Mob mob) {
               MobSpawnType origin = mob.getSpawnType();
               if (origin != null && origin != MobSpawnType.COMMAND && origin != MobSpawnType.MOB_SUMMONED) {
                  return false;
               }
            }

            BlockPos position = entity.m_20183_();

            for (BlockPos center : centers) {
               if (center.m_123331_(position) <= 256.0) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private static int run(CommandContext<CommandSourceStack> context, boolean lockType) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      Collection<? extends Entity> selected = EntityArgument.m_91467_(context, "targets");
      if (selected.isEmpty()) {
         source.m_81352_(Component.m_237113_("没有匹配到任何实体"));
         return 0;
      } else {
         Set<Entity> targets = Collections.newSetFromMap(new IdentityHashMap<>());
         int skippedPlayers = 0;

         for (Entity found : selected) {
            Entity entity = normalize(found);
            if (entity instanceof Player) {
               skippedPlayers++;
            } else {
               targets.add(entity);
            }
         }

         int players = skippedPlayers;
         if (players > 0) {
            source.m_288197_(() -> Component.m_237113_("跳过 " + players + " 名玩家").m_130940_(ChatFormatting.YELLOW), false);
         }

         if (targets.isEmpty()) {
            return 0;
         } else {
            int tick = source.m_81377_().m_129921_();
            int typeTargets = 0;

            for (Entity entity : targets) {
               UUID uuid = entity.m_20148_();
               LOCKED_UUIDS.add(uuid);
               if (lockType && supportsTypeLock(entity.m_6095_()) && entity.m_9236_() instanceof ServerLevel level) {
                  DeleteCommand.TypeInDimension scope = new DeleteCommand.TypeInDimension(level.m_46472_(), entity.m_6095_());
                  TYPE_LOCKS.computeIfAbsent(scope, key -> ConcurrentHashMap.newKeySet()).add(entity.m_20183_().m_7949_());
                  typeTargets++;
               }

               DeleteCommand.Watchdog watch = WATCHDOGS.computeIfAbsent(uuid, key -> new DeleteCommand.Watchdog(uuid, tick));
               watch.targets.add(entity);
               watch.lastSeenTick = tick;
            }

            int removed = 0;

            for (Entity entity : targets) {
               if (hardRemove(entity)) {
                  removed++;
               } else {
                  SakuraMod.LOGGER.warn("[/delete] 实体仍在服务端登记，将继续复查 {}", describe(entity));
               }
            }

            int confirmed = removed;
            int pending = targets.size() - removed;
            String typeMessage = typeTargets > 0 ? "，已锁定目标所在维度、距目标 16 格内的同类异常生成" : "";
            String spawnProtection = lockType && typeTargets < targets.size() ? "，普通原版实体仅锁定 UUID" : "";
            source.m_288197_(
               () -> Component.m_237113_(
                  "已确认移除 "
                     + confirmed
                     + " 个实体"
                     + (pending == 0 ? "" : "，仍有 " + pending + " 个目标等待复查")
                     + "，已锁定目标 UUID"
                     + typeMessage
                     + spawnProtection
                     + "。用 /delete unlock 解锁"
               ),
               true
            );
            return confirmed;
         }
      }
   }

   private static boolean supportsTypeLock(EntityType<?> type) {
      return type == EntityType.f_20565_ || type == EntityType.f_20496_ || !"minecraft".equals(EntityType.m_20613_(type).m_135827_());
   }

   private static Entity normalize(Entity entity) {
      return entity instanceof PartEntity<?> part && part.getParent() != null ? part.getParent() : entity;
   }

   private static boolean isRegistered(Entity entity) {
      if (!(entity.m_9236_() instanceof ServerLevel level)) {
         return false;
      } else {
         Entity var5 = level.m_6815_(entity.m_19879_());
         Entity byUuid = level.m_8791_(entity.m_20148_());
         if (var5 != entity && byUuid != entity) {
            ServerLevelEntityAccessor access = (ServerLevelEntityAccessor)level;
            return var5 != null
                  || !access.sakura$getEntityTickList().m_156914_(entity)
                     && !((ChunkMapEntityAccessor)level.m_7726_().f_8325_).sakura$getTrackedEntities().containsKey(entity.m_19879_())
               ? byUuid == null && access.sakura$getEntityManager().m_157550_(entity.m_20148_())
               : true;
         } else {
            return true;
         }
      }
   }

   private static boolean hardRemove(Entity entity) {
      if (!isRegistered(entity)) {
         return true;
      } else {
         clearKnownBossProtection(entity);

         try {
            entity.m_142467_(RemovalReason.DISCARDED);
         } catch (RuntimeException var7) {
            SakuraMod.LOGGER.debug("[/delete] setRemoved 异常 {}", describe(entity), var7);
         }

         if (isRegistered(entity)) {
            try {
               entity.m_19877_();
            } catch (RuntimeException var6) {
               SakuraMod.LOGGER.debug("[/delete] 解绑乘骑异常 {}", describe(entity), var6);
            }
         }

         if (isRegistered(entity)) {
            EntityRemovalReasonAccessor access = (EntityRemovalReasonAccessor)entity;
            EntityInLevelCallback callback = access.sakura$getLevelCallback();
            if (callback != null && callback != EntityInLevelCallback.f_156799_) {
               try {
                  access.sakura$setRemovalReason(RemovalReason.DISCARDED);
                  callback.m_142472_(RemovalReason.DISCARDED);
               } catch (RuntimeException var5) {
                  SakuraMod.LOGGER.debug("[/delete] onRemove 异常 {}", describe(entity), var5);
               }
            }
         }

         if (isRegistered(entity)) {
            return false;
         } else {
            try {
               entity.invalidateCaps();
            } catch (RuntimeException var4) {
               SakuraMod.LOGGER.debug("[/delete] capability 清理异常 {}", describe(entity), var4);
            }

            return !isRegistered(entity);
         }
      }
   }

   private static void clearKnownBossProtection(Entity entity) {
      if ("iss_csw".equals(EntityType.m_20613_(entity.m_6095_()).m_135827_())) {
         for (Class<?> type = entity.getClass(); type != null; type = type.getSuperclass()) {
            if ("UnRemoveBossEntity".equals(type.getSimpleName())) {
               setKnownBossFlag(entity, "setCanKill", true);
               setKnownBossFlag(entity, "setCanDie", true);
               setKnownBossFlag(entity, "setAntiCheatMode", false);
               return;
            }
         }
      }
   }

   private static void setKnownBossFlag(Entity entity, String method, boolean value) {
      try {
         entity.getClass().getMethod(method, boolean.class).invoke(entity, value);
      } catch (NoSuchMethodException var4) {
      } catch (RuntimeException | ReflectiveOperationException var5) {
         SakuraMod.LOGGER.debug("[/delete] 无法调用 Boss 兼容方法 {}", method, var5);
      }
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END && !WATCHDOGS.isEmpty()) {
         MinecraftServer server = event.getServer();
         int tick = server.m_129921_();
         if (tick % 5 == 0) {
            for (DeleteCommand.Watchdog watch : List.copyOf(WATCHDOGS.values())) {
               if (WATCHDOGS.get(watch.uuid) == watch) {
                  retry(server, watch, tick);
               }
            }
         }
      }
   }

   private static void retry(MinecraftServer server, DeleteCommand.Watchdog watch, int tick) {
      for (ServerLevel level : server.m_129785_()) {
         Entity found = level.m_8791_(watch.uuid);
         if (found != null && !(found instanceof Player)) {
            watch.targets.add(found);
         }
      }

      watch.targets.removeIf(entityx -> !isRegistered(entityx));
      if (!watch.targets.isEmpty()) {
         watch.lastSeenTick = tick;
      }

      for (Entity entity : List.copyOf(watch.targets)) {
         if (WATCHDOGS.get(watch.uuid) != watch) {
            return;
         }

         if (hardRemove(entity)) {
            watch.targets.remove(entity);
         }
      }

      if (tick - watch.lastSeenTick >= 20 || tick - watch.startTick >= 600) {
         WATCHDOGS.remove(watch.uuid, watch);

         for (Entity entity : watch.targets) {
            SakuraMod.LOGGER.warn("[/delete] 复查到期，目标仍在服务端登记 {}", describe(entity));
         }
      }
   }

   private static String describe(Entity entity) {
      return EntityType.m_20613_(entity.m_6095_())
         + " uuid="
         + entity.m_20148_()
         + " "
         + entity.m_9236_().m_46472_().m_135782_()
         + "@"
         + entity.m_20183_().m_123344_()
         + " removalReason="
         + entity.m_146911_();
   }

   private DeleteCommand() {
   }

   private record TypeInDimension(ResourceKey<Level> dimension, EntityType<?> type) {
   }

   private static final class Watchdog {
      private final UUID uuid;
      private final int startTick;
      private final Set<Entity> targets = Collections.newSetFromMap(new IdentityHashMap<>());
      private int lastSeenTick;

      private Watchdog(UUID uuid, int tick) {
         this.uuid = uuid;
         this.startTick = tick;
         this.lastSeenTick = tick;
      }
   }
}
