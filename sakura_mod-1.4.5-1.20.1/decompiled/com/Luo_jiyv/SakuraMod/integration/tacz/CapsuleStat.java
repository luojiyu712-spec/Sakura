package com.Luo_jiyv.SakuraMod.integration.tacz;

import com.Luo_jiyv.SakuraMod.Config;

public enum CapsuleStat {
   DAMAGE("damage", "SakuraCapsuleDamage", "damage"),
   FIRE_RATE("fire_rate", "SakuraCapsuleFireRate", "rpm"),
   ADS_TIME("ads_time", "SakuraCapsuleAdsTime", "ads"),
   AMMO_SPEED("ammo_speed", "SakuraCapsuleAmmoSpeed", "ammo_speed"),
   ARMOR_IGNORE("armor_ignore", "SakuraCapsuleArmorIgnore", "armor_ignore"),
   HEAD_SHOT("head_shot", "SakuraCapsuleHeadShot", "head_shot"),
   HIPFIRE_SPREAD("hipfire_spread", "SakuraCapsuleHipfireSpread", "inaccuracy"),
   MOVE_SPREAD("move_spread", "SakuraCapsuleMoveSpread", "inaccuracy"),
   SNEAK_SPREAD("sneak_spread", "SakuraCapsuleSneakSpread", "inaccuracy"),
   PRONE_SPREAD("prone_spread", "SakuraCapsuleProneSpread", "inaccuracy"),
   AIM_ACCURACY("aim_accuracy", "SakuraCapsuleAimAccuracy", "inaccuracy"),
   PENETRATION("penetration", "SakuraCapsulePenetration", "pierce"),
   RECOIL_VERTICAL("recoil_vertical", "SakuraCapsuleRecoilVertical", "recoil"),
   RECOIL_HORIZONTAL("recoil_horizontal", "SakuraCapsuleRecoilHorizontal", "recoil"),
   WEIGHT("weight", "SakuraCapsuleWeight", "weight_modifier"),
   KNOCKBACK("knockback", "SakuraCapsuleKnockback", "knockback"),
   EFFECTIVE_RANGE("effective_range", "SakuraCapsuleEffectiveRange", "effective_range");

   private final String id;
   private final String key;
   private final String cacheKey;

   private CapsuleStat(String id, String key, String cacheKey) {
      this.id = id;
      this.key = key;
      this.cacheKey = cacheKey;
   }

   public String id() {
      return this.id;
   }

   public String key() {
      return this.key;
   }

   public String flatKey() {
      return this.key + "Flat";
   }

   public String cacheKey() {
      return this.cacheKey;
   }

   public String translationKey() {
      return "screen.sakura_mod.capsule." + this.id;
   }

   public boolean isEnabled() {
      return switch (this) {
         case DAMAGE -> Config.isArchiteCapsuleDamageEnabled();
         case FIRE_RATE -> Config.isArchiteCapsuleFireRateEnabled();
         case ADS_TIME -> Config.isArchiteCapsuleAdsEnabled();
         case AMMO_SPEED -> Config.isArchiteCapsuleAmmoSpeedEnabled();
         case ARMOR_IGNORE -> Config.isArchiteCapsuleArmorIgnoreEnabled();
         case HEAD_SHOT -> Config.isArchiteCapsuleHeadShotEnabled();
         case HIPFIRE_SPREAD -> Config.isArchiteCapsuleHipfireSpreadEnabled();
         case MOVE_SPREAD -> Config.isArchiteCapsuleMoveSpreadEnabled();
         case SNEAK_SPREAD -> Config.isArchiteCapsuleSneakSpreadEnabled();
         case PRONE_SPREAD -> Config.isArchiteCapsuleProneSpreadEnabled();
         case AIM_ACCURACY -> Config.isArchiteCapsuleAimAccuracyEnabled();
         case PENETRATION -> Config.isArchiteCapsulePenetrationEnabled();
         case RECOIL_VERTICAL -> Config.isArchiteCapsuleRecoilVerticalEnabled();
         case RECOIL_HORIZONTAL -> Config.isArchiteCapsuleRecoilHorizontalEnabled();
         case WEIGHT -> Config.isArchiteCapsuleWeightEnabled();
         case KNOCKBACK -> Config.isArchiteCapsuleKnockbackEnabled();
         case EFFECTIVE_RANGE -> Config.isArchiteCapsuleEffectiveRangeEnabled();
      };
   }
}
