package com.Luo_jiyv.SakuraMod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.slf4j.LoggerFactory;

public class Config {
   private static final Builder BUILDER = new Builder();
   public static final BooleanValue GRAVITY_CANNON_BREAK_BLOCKS = BUILDER.comment("重力炮是否破坏沿途方块。").define("gravityCannonBreakBlocks", true);
   public static final BooleanValue GRAVITY_ATTACK_ENABLED = BUILDER.comment(new String[]{"是否开启逆重核心的攻击能力。", "关闭后：右键/左键/Curios 触发都不会造成任何伤害，", "只保留飞行能力和模式切换。"})
      .define("gravityAttackEnabled", true);
   public static final BooleanValue SAKURA_LOOT_CHEST_ONLY_VANILLA = BUILDER.comment(
         new String[]{"战利品箱掉落 sakura_component 时，是否只对原版宝箱生效。", "true  = 只匹配 minecraft:chests/*（原版结构宝箱）", "false = 兼容所有模组的 chests/*（推荐）"}
      )
      .define("sakuraLootChestOnlyVanilla", false);
   public static final BooleanValue SAKURA_STEEL_FORTUNE_BONUS = BUILDER.comment("樱花钢工具是否自带时运 II / 抢夺 II 的基础加成。").define("sakuraSteelFortuneBonus", true);
   public static final IntValue RANDOM_TELEPORTER_MAX_RADIUS_LIMIT = BUILDER.comment("随机传送器可调节的最大半径上限（UI 滑条的最大值）")
      .defineInRange("randomTeleporterMaxRadiusLimit", 100000, 1000, 300000000);
   public static final BooleanValue RANDOM_TELEPORTER_ASYNC_SEARCH = BUILDER.comment("允许原版噪声生成器的群系采样在后台执行。关闭时在服务端按 tick 分批采样。")
      .define("randomTeleporterAsyncSearch", true);
   public static final BooleanValue GOLDEN_CUBE_ENABLED = BUILDER.comment(new String[]{"是否启用金色魔方（沉迷 / 戒断 / 掉落 / 进度）。", "false 时：不再生成、不触发沉迷，已有状态会在登录时被清理。"})
      .define("goldenCubeEnabled", true);
   public static final DoubleValue GOLDEN_CUBE_CHEST_CHANCE = BUILDER.comment("金色魔方在世界宝箱中的出现概率（0 ~ 1）").defineInRange("goldenCubeChestChance", 0.01, 0.0, 1.0);
   public static final BooleanValue MIST_WEATHER_ENABLED = BUILDER.comment("是否启用灰棘迷雾（自然生成、/weather mist、刷怪加成、睡眠结束等）。").define("mistWeatherEnabled", true);
   public static final BooleanValue BLOOD_RAIN_ENABLED = BUILDER.comment("是否启用血怒之雨（/weather bloodrain、降雨/染红/力量加成等）。").define("bloodRainEnabled", true);
   public static final BooleanValue GOLDEN_CUBE_VOID_TELEPORT = BUILDER.comment("金色魔方掉进虚空时，是否优先送回丢出者，其次已激活魔方的玩家，否则回出生点。")
      .define("goldenCubeVoidTeleport", true);
   public static final BooleanValue GOLDEN_CUBE_INDESTRUCTIBLE = BUILDER.comment(new String[]{"金色魔方掉落物是否无法被摧毁（无敌、发光、永不消失、防火）。", "关闭后掉落物会正常受火焰/岩浆/消失计时影响。"})
      .define("goldenCubeIndestructible", true);
   public static final BooleanValue GOLDEN_CUBE_UNDROPPABLE = BUILDER.comment(new String[]{"是否禁止玩家主动丢弃金色魔方。", "不影响死亡掉落保护；只是手动物品丢弃会被拒绝。"})
      .define("goldenCubeUndroppable", false);
   public static final BooleanValue ARCHITE_CAPSULE_FIRE_RATE_ENABLED = BUILDER.comment("超凡胶囊的射速调整是否生效（需要安装 TACZ 才有实际效果）。")
      .define("architeCapsuleFireRateEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_DAMAGE_ENABLED = BUILDER.comment("超凡胶囊的伤害调整是否生效（需要安装 TACZ 才有实际效果）。")
      .define("architeCapsuleDamageEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_ADS_ENABLED = BUILDER.comment("超凡胶囊的开镜时间调整是否生效。").define("architeCapsuleAdsEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_AMMO_SPEED_ENABLED = BUILDER.comment("超凡胶囊的弹速调整是否生效。").define("architeCapsuleAmmoSpeedEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_ARMOR_IGNORE_ENABLED = BUILDER.comment("超凡胶囊的穿甲倍率调整是否生效。").define("architeCapsuleArmorIgnoreEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_HEAD_SHOT_ENABLED = BUILDER.comment("超凡胶囊的爆头倍率调整是否生效。").define("architeCapsuleHeadShotEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_HIPFIRE_SPREAD_ENABLED = BUILDER.comment("超凡胶囊的腰际射击扩散调整是否生效。")
      .define("architeCapsuleHipfireSpreadEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_MOVE_SPREAD_ENABLED = BUILDER.comment("超凡胶囊的移动射击扩散调整是否生效。").define("architeCapsuleMoveSpreadEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_SNEAK_SPREAD_ENABLED = BUILDER.comment("超凡胶囊的潜行射击扩散调整是否生效。")
      .define("architeCapsuleSneakSpreadEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_PRONE_SPREAD_ENABLED = BUILDER.comment("超凡胶囊的趴伏射击扩散调整是否生效。")
      .define("architeCapsuleProneSpreadEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_AIM_ACCURACY_ENABLED = BUILDER.comment("超凡胶囊的瞄准精度调整是否生效。").define("architeCapsuleAimAccuracyEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_PENETRATION_ENABLED = BUILDER.comment("超凡胶囊的穿透力调整是否生效。").define("architeCapsulePenetrationEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_RECOIL_VERTICAL_ENABLED = BUILDER.comment("超凡胶囊的垂直后坐力调整是否生效。")
      .define("architeCapsuleRecoilVerticalEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_RECOIL_HORIZONTAL_ENABLED = BUILDER.comment("超凡胶囊的水平后坐力调整是否生效。")
      .define("architeCapsuleRecoilHorizontalEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_WEIGHT_ENABLED = BUILDER.comment("超凡胶囊的重量调整是否生效。").define("architeCapsuleWeightEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_KNOCKBACK_ENABLED = BUILDER.comment("超凡胶囊的击退调整是否生效。").define("architeCapsuleKnockbackEnabled", true);
   public static final BooleanValue ARCHITE_CAPSULE_EFFECTIVE_RANGE_ENABLED = BUILDER.comment("超凡胶囊的优势射程调整是否生效。")
      .define("architeCapsuleEffectiveRangeEnabled", true);
   public static final ForgeConfigSpec SPEC = BUILDER.build();

   public static void save() {
      try {
         SPEC.save();
      } catch (Exception var1) {
         LoggerFactory.getLogger("sakura_mod").warn("Failed to save common config: {}", var1.getMessage());
      }
   }

   public static boolean isGravityAttackEnabled() {
      try {
         return (Boolean)GRAVITY_ATTACK_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isGravityCannonBreakBlocks() {
      try {
         return (Boolean)GRAVITY_CANNON_BREAK_BLOCKS.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isRandomTeleporterAsyncSearch() {
      try {
         return (Boolean)RANDOM_TELEPORTER_ASYNC_SEARCH.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static int getRandomTeleporterMaxRadiusLimit() {
      try {
         return (Integer)RANDOM_TELEPORTER_MAX_RADIUS_LIMIT.get();
      } catch (RuntimeException var1) {
         return 100000;
      }
   }

   public static boolean isSakuraLootChestOnlyVanilla() {
      try {
         return (Boolean)SAKURA_LOOT_CHEST_ONLY_VANILLA.get();
      } catch (RuntimeException var1) {
         return false;
      }
   }

   public static boolean isGoldenCubeEnabled() {
      try {
         return (Boolean)GOLDEN_CUBE_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static double getGoldenCubeChestChance() {
      try {
         return (Double)GOLDEN_CUBE_CHEST_CHANCE.get();
      } catch (RuntimeException var1) {
         return 0.01;
      }
   }

   public static boolean isSakuraSteelFortuneBonusEnabled() {
      try {
         return (Boolean)SAKURA_STEEL_FORTUNE_BONUS.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isMistWeatherEnabled() {
      try {
         return (Boolean)MIST_WEATHER_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isBloodRainEnabled() {
      try {
         return (Boolean)BLOOD_RAIN_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isGoldenCubeVoidTeleportEnabled() {
      try {
         return (Boolean)GOLDEN_CUBE_VOID_TELEPORT.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isGoldenCubeIndestructible() {
      try {
         return (Boolean)GOLDEN_CUBE_INDESTRUCTIBLE.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isGoldenCubeUndroppable() {
      try {
         return (Boolean)GOLDEN_CUBE_UNDROPPABLE.get();
      } catch (RuntimeException var1) {
         return false;
      }
   }

   public static boolean isArchiteCapsuleFireRateEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_FIRE_RATE_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleDamageEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_DAMAGE_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleAdsEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_ADS_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleAmmoSpeedEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_AMMO_SPEED_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleArmorIgnoreEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_ARMOR_IGNORE_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleHeadShotEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_HEAD_SHOT_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleHipfireSpreadEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_HIPFIRE_SPREAD_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleMoveSpreadEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_MOVE_SPREAD_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleSneakSpreadEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_SNEAK_SPREAD_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleProneSpreadEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_PRONE_SPREAD_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleAimAccuracyEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_AIM_ACCURACY_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsulePenetrationEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_PENETRATION_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleRecoilVerticalEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_RECOIL_VERTICAL_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleRecoilHorizontalEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_RECOIL_HORIZONTAL_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleWeightEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_WEIGHT_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleKnockbackEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_KNOCKBACK_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }

   public static boolean isArchiteCapsuleEffectiveRangeEnabled() {
      try {
         return (Boolean)ARCHITE_CAPSULE_EFFECTIVE_RANGE_ENABLED.get();
      } catch (RuntimeException var1) {
         return true;
      }
   }
}
