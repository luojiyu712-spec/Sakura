package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.weather.MistWeather;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Mob.class})
public abstract class MobSunBurnMixin {
   @Inject(
      method = {"isSunBurnTick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sakura$preventSunBurnDuringMist(CallbackInfoReturnable<Boolean> cir) {
      if (MistWeather.isActive()) {
         cir.setReturnValue(false);
      }
   }
}
