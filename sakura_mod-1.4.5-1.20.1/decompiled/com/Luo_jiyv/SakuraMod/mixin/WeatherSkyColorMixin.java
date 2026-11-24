package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.client.WeatherVisuals;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientLevel.class})
public abstract class WeatherSkyColorMixin {
   @Inject(
      method = {"getSkyColor"},
      at = {@At("RETURN")},
      cancellable = true,
      require = 0
   )
   private void sakura$tintSky(Vec3 pos, float partialTick, CallbackInfoReturnable<Vec3> cir) {
      Vec3 original = (Vec3)cir.getReturnValue();
      Vec3 tinted = WeatherVisuals.tintSky(original, partialTick);
      if (tinted != original) {
         cir.setReturnValue(tinted);
      }
   }

   @Inject(
      method = {"getCloudColor"},
      at = {@At("RETURN")},
      cancellable = true,
      require = 0
   )
   private void sakura$tintClouds(float partialTick, CallbackInfoReturnable<Vec3> cir) {
      Vec3 original = (Vec3)cir.getReturnValue();
      Vec3 tinted = WeatherVisuals.tintSky(original, partialTick);
      if (tinted != original) {
         cir.setReturnValue(tinted);
      }
   }
}
