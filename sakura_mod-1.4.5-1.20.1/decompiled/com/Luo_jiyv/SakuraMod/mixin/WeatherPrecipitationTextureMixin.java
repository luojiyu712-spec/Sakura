package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.client.WeatherVisuals;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({LevelRenderer.class})
public abstract class WeatherPrecipitationTextureMixin {
   @ModifyVariable(
      method = {"renderSnowAndRain"},
      at = @At(
         value = "STORE",
         ordinal = 0
      ),
      ordinal = 1,
      require = 0
   )
   private float sakura$selectPrecipitationStrength(float strength) {
      return WeatherVisuals.precipitationStrength(strength);
   }

   @ModifyArg(
      method = {"renderSnowAndRain"},
      index = 1,
      require = 0,
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"
      )
   )
   private ResourceLocation sakura$selectPrecipitationTexture(ResourceLocation texture) {
      return WeatherVisuals.precipitationTexture(texture);
   }

   @ModifyArg(
      method = {"tickRain"},
      index = 0,
      require = 0,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
      )
   )
   private ParticleOptions sakura$selectPrecipitationSplash(ParticleOptions options) {
      return WeatherVisuals.precipitationSplash(options);
   }
}
