package com.Luo_jiyv.SakuraMod.util;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class GoldenCubeData {
   public static final String KEY_SEVERITY = "GoldenCubeSeverity";
   public static final String KEY_ACTIVATED = "GoldenCubeActivated";
   public static final String KEY_KEPT_STACKS = "GoldenCubeKeptStacks";
   public static final String KEY_LAST_PLAY = "GoldenCubeLastPlay";
   public static final String KEY_WITHDRAWAL = "GoldenCubeWithdrawal";
   public static final String KEY_WITHDRAWAL_TICKS = "GoldenCubeWithdrawalTicks";
   public static final String KEY_LEVEL5_TICKS = "GoldenCubeLevel5Ticks";
   public static final String KEY_IMMUNITY_UNTIL = "GoldenCubeImmunityUntil";
   public static final String KEY_TIMER_CHECKPOINT = "GoldenCubeTimerCheckpoint";
   public static final String KEY_KEPT_CUBE = "GoldenCubeKept";
   public static final String KEY_KEPT_CUBE_COUNT = "GoldenCubeKeptCount";
   public static final String KEY_NEXT_DUE = "GoldenCubeNextDue";
   public static final String KEY_ANNOUNCED = "GoldenCubeAnnounced";
   public static final String KEY_OWNED = "GoldenCubeOwned";
   public static final String KEY_PLAY_COUNT = "GoldenCubePlayCount";
   public static final String KEY_ANOMALY_GRANTED = "GoldenCubeAnomalyGranted";
   public static final String KEY_REJECTED = "GoldenCubeRejected";
   public static final int MAX_PLAY_COUNT = 1000;
   public static final UUID WITHDRAWAL_SLOW_UUID = UUID.fromString("6a3f7c2e-1111-4a2b-9c3d-000000000001");
   public static final long DAY_TICKS = 24000L;
   public static final long GRACE_TICKS = 1200L;
   public static final long WITHDRAWAL_STEP = 600L;
   public static final long MELTDOWN_DELAY = 200L;
   public static final long MELTDOWN_DURATION = 2400L;
   public static final long LEVEL5_DURATION = 2600L;
   public static final long IMMUNITY_TICKS = 864000L;

   private GoldenCubeData() {
   }

   private static CompoundTag data(Player player) {
      return player.getPersistentData();
   }

   public static void checkpointTimers(Player player) {
      if (isActivated(player) || immunityUntil(player) > 0L || data(player).m_128441_("GoldenCubeTimerCheckpoint")) {
         data(player).m_128356_("GoldenCubeTimerCheckpoint", player.m_9236_().m_46467_());
      }
   }

   public static void resumeTimers(Player player) {
      CompoundTag tag = data(player);
      if (tag.m_128425_("GoldenCubeTimerCheckpoint", 4)) {
         long checkpoint = tag.m_128454_("GoldenCubeTimerCheckpoint");
         long elapsed = player.m_9236_().m_46467_() - checkpoint;
         if (isActivated(player)) {
            if (lastPlay(player) > 0L) {
               setLastPlay(player, lastPlay(player) + elapsed);
            }

            if (nextDue(player) > 0L) {
               setNextDue(player, nextDue(player) + elapsed);
            }
         }

         if (immunityUntil(player) > checkpoint) {
            setImmunityUntil(player, immunityUntil(player) + elapsed);
         }
      }

      checkpointTimers(player);
   }

   public static float severity(Player player) {
      return clampSeverity(data(player).m_128457_("GoldenCubeSeverity"));
   }

   public static void setSeverity(Player player, float value) {
      data(player).m_128350_("GoldenCubeSeverity", clampSeverity(value));
   }

   private static float clampSeverity(float value) {
      return Float.isFinite(value) ? Math.max(0.0F, Math.min(2.0F, value)) : 0.0F;
   }

   public static boolean isActivated(Player player) {
      CompoundTag tag = data(player);
      return tag.m_128441_("GoldenCubeActivated")
         ? tag.m_128471_("GoldenCubeActivated")
         : severity(player) > 0.0F && (lastPlay(player) > 0L || playCount(player) > 0);
   }

   public static void setActivated(Player player, boolean activated) {
      data(player).m_128379_("GoldenCubeActivated", activated);
   }

   public static int tierFor(float severity) {
      if (severity <= 0.1F) {
         return 0;
      } else if (severity <= 0.33F) {
         return 1;
      } else {
         return severity <= 0.66F ? 2 : 3;
      }
   }

   public static float meltdownDamageRatio(int tier) {
      return 0.05F * (1 + Math.max(0, Math.min(3, tier)));
   }

   public static int meltdownInterval(int tier) {
      return 20 - 5 * Math.max(0, Math.min(3, tier));
   }

   public static boolean isMeltingDown(Player player) {
      return withdrawal(player) == 5 && level5Ticks(player) >= 200L;
   }

   public static long lastPlay(Player player) {
      return data(player).m_128454_("GoldenCubeLastPlay");
   }

   public static void setLastPlay(Player player, long gameTime) {
      data(player).m_128356_("GoldenCubeLastPlay", gameTime);
   }

   public static int withdrawal(Player player) {
      return Math.max(0, Math.min(5, data(player).m_128451_("GoldenCubeWithdrawal")));
   }

   public static void setWithdrawal(Player player, int level) {
      data(player).m_128405_("GoldenCubeWithdrawal", Math.max(0, Math.min(5, level)));
   }

   public static long withdrawalTicks(Player player) {
      return Math.max(0L, Math.min(2400L, data(player).m_128454_("GoldenCubeWithdrawalTicks")));
   }

   public static void setWithdrawalTicks(Player player, long ticks) {
      data(player).m_128356_("GoldenCubeWithdrawalTicks", Math.max(0L, Math.min(2400L, ticks)));
   }

   public static long level5Ticks(Player player) {
      return Math.max(0L, Math.min(2600L, data(player).m_128454_("GoldenCubeLevel5Ticks")));
   }

   public static void setLevel5Ticks(Player player, long ticks) {
      data(player).m_128356_("GoldenCubeLevel5Ticks", Math.max(0L, Math.min(2600L, ticks)));
   }

   public static long immunityUntil(Player player) {
      return data(player).m_128454_("GoldenCubeImmunityUntil");
   }

   public static void setImmunityUntil(Player player, long gameTime) {
      data(player).m_128356_("GoldenCubeImmunityUntil", gameTime);
   }

   public static boolean isImmune(Player player) {
      return player.m_9236_().m_46467_() < immunityUntil(player);
   }

   public static boolean keptCube(Player player) {
      return data(player).m_128471_("GoldenCubeKept");
   }

   public static void setKeptCube(Player player, boolean kept) {
      data(player).m_128379_("GoldenCubeKept", kept);
   }

   public static int keptCubeCount(Player player) {
      return data(player).m_128451_("GoldenCubeKeptCount");
   }

   public static void setKeptCubeCount(Player player, int count) {
      data(player).m_128405_("GoldenCubeKeptCount", Math.max(0, count));
   }

   public static long nextDue(Player player) {
      return data(player).m_128454_("GoldenCubeNextDue");
   }

   public static void setNextDue(Player player, long gameTime) {
      data(player).m_128356_("GoldenCubeNextDue", gameTime);
   }

   public static boolean announced(Player player) {
      return data(player).m_128471_("GoldenCubeAnnounced");
   }

   public static void setAnnounced(Player player, boolean value) {
      data(player).m_128379_("GoldenCubeAnnounced", value);
   }

   public static boolean ownedCube(Player player) {
      return data(player).m_128471_("GoldenCubeOwned");
   }

   public static void setOwnedCube(Player player, boolean value) {
      data(player).m_128379_("GoldenCubeOwned", value);
   }

   public static int playCount(Player player) {
      return Math.max(0, Math.min(1000, data(player).m_128451_("GoldenCubePlayCount")));
   }

   public static void setPlayCount(Player player, int value) {
      data(player).m_128405_("GoldenCubePlayCount", Math.max(0, Math.min(1000, value)));
   }

   public static boolean isRejected(Player player) {
      return data(player).m_128471_("GoldenCubeRejected");
   }

   public static void setRejected(Player player, boolean value) {
      data(player).m_128379_("GoldenCubeRejected", value);
   }

   public static boolean anomalyGranted(Player player) {
      return data(player).m_128471_("GoldenCubeAnomalyGranted");
   }

   public static void setAnomalyGranted(Player player, boolean value) {
      data(player).m_128379_("GoldenCubeAnomalyGranted", value);
   }

   public static void clearAll(Player player) {
      CompoundTag tag = data(player);
      tag.m_128379_("GoldenCubeActivated", false);
      tag.m_128473_("GoldenCubeSeverity");
      tag.m_128473_("GoldenCubeLastPlay");
      tag.m_128473_("GoldenCubeWithdrawal");
      tag.m_128473_("GoldenCubeWithdrawalTicks");
      tag.m_128473_("GoldenCubeLevel5Ticks");
      tag.m_128473_("GoldenCubeWithdrawalStart");
      tag.m_128473_("GoldenCubeLevel5Start");
      tag.m_128473_("GoldenCubeImmunityUntil");
      tag.m_128473_("GoldenCubeTimerCheckpoint");
      tag.m_128473_("GoldenCubeNextDue");
      tag.m_128473_("GoldenCubeAnnounced");
      tag.m_128473_("GoldenCubeOwned");
      tag.m_128473_("GoldenCubePlayCount");
      tag.m_128473_("GoldenCubeAnomalyGranted");
   }
}
