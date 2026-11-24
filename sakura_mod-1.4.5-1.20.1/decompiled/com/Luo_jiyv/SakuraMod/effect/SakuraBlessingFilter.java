package com.Luo_jiyv.SakuraMod.effect;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SakuraBlessingFilter {
   private static volatile Set<MobEffect> modWhitelistCache;

   private SakuraBlessingFilter() {
   }

   private static Set<MobEffect> modWhitelist() {
      Set<MobEffect> cached = modWhitelistCache;
      if (cached != null) {
         return cached;
      } else {
         Set<MobEffect> set = new HashSet<>();
         addIfPresent(set, ModEffects.SAKURA_BLESSING);
         addIfPresent(set, ModEffects.ABSOLUTE_MATERIALISM);
         modWhitelistCache = set;
         return set;
      }
   }

   private static void addIfPresent(Set<MobEffect> set, RegistryObject<MobEffect> obj) {
      if (obj != null) {
         MobEffect effect = (MobEffect)obj.get();
         if (effect != null) {
            set.add(effect);
         }
      }
   }

   public static boolean isAllowed(MobEffect effect) {
      if (effect == null) {
         return false;
      } else if (effect == ModEffects.GRAVITY_CRUSH.get()) {
         return true;
      } else {
         ResourceLocation key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
         return key != null && "minecraft".equals(key.m_135827_()) && effect.m_19483_() == MobEffectCategory.BENEFICIAL
            ? true
            : modWhitelist().contains(effect);
      }
   }
}
