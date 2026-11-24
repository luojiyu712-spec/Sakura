package com.Luo_jiyv.SakuraMod.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticles {
   private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "sakura_mod");
   public static final RegistryObject<ParticleType<SakuraSlashOptions>> SAKURA_SLASH = PARTICLES.register(
      "sakura_slash", () -> new ParticleType<SakuraSlashOptions>(false, SakuraSlashOptions.DESERIALIZER) {
         public Codec<SakuraSlashOptions> m_7652_() {
            return SakuraSlashOptions.CODEC;
         }
      }
   );
   public static final RegistryObject<SimpleParticleType> SAKURA_AXE_SLASH = PARTICLES.register("sakura_axe_slash", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> RANDOM_TELEPORTER = PARTICLES.register("random_teleporter", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> SHIELD_SHATTER = PARTICLES.register("shield_shatter", () -> new SimpleParticleType(true));
   public static final RegistryObject<SimpleParticleType> SHIELD_SHOCKWAVE = PARTICLES.register("shield_shockwave", () -> new SimpleParticleType(true));

   private ModParticles() {
   }

   public static void register(IEventBus bus) {
      PARTICLES.register(bus);
   }
}
