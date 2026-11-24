package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.weather.MistWeather;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Level.class})
public abstract class LevelMistSkyDarkenMixin {
   private static final int MIST_SKY_DARKEN = 11;

   @Inject(
      method = {"getSkyDarken"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void sakura$mistSkyDarken(CallbackInfoReturnable<Integer> cir) {
      if (this instanceof ServerLevel serverLevel) {
         if (Level.f_46428_.equals(serverLevel.m_46472_())) {
            if (MistWeather.isActive()) {
               if (cir.getReturnValueI() < 11) {
                  cir.setReturnValue(11);
               }
            }
         }
      }
   }
}
