package com.Luo_jiyv.SakuraMod.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
   public static final ResourceKey<DamageType> SHIELD_SHOCKWAVE = ResourceKey.m_135785_(
      Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("sakura_mod", "shield_shockwave")
   );
   public static final ResourceKey<DamageType> GRAVITY_CRUSH = ResourceKey.m_135785_(
      Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("sakura_mod", "gravity_crush")
   );
   public static final ResourceKey<DamageType> SAKURA_SLASH = ResourceKey.m_135785_(
      Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("sakura_mod", "sakura_slash")
   );
   public static final ResourceKey<DamageType> GRAVITY_CANNON = ResourceKey.m_135785_(
      Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("sakura_mod", "gravity_cannon")
   );
   public static final ResourceKey<DamageType> GOLDEN_CUBE_MELTDOWN = ResourceKey.m_135785_(
      Registries.f_268580_, ResourceLocation.fromNamespaceAndPath("sakura_mod", "golden_cube_meltdown")
   );

   public static boolean isOurDamage(DamageSource source) {
      return source.m_276093_(GRAVITY_CRUSH) || source.m_276093_(SAKURA_SLASH) || source.m_276093_(GRAVITY_CANNON);
   }
}
