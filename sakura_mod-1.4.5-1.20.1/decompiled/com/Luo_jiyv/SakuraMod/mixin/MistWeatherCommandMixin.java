package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.weather.BloodRainWeather;
import com.Luo_jiyv.SakuraMod.weather.MistWeather;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.WeatherCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({WeatherCommand.class})
public abstract class MistWeatherCommandMixin {
   @Inject(
      method = {"setClear"},
      at = {@At("HEAD")}
   )
   private static void sakura$clearWeather(CommandSourceStack source, int duration, CallbackInfoReturnable<Integer> cir) {
      MistWeather.stop(source.m_81377_());
      BloodRainWeather.stop(source.m_81377_());
   }

   @Inject(
      method = {"setRain"},
      at = {@At("HEAD")}
   )
   private static void sakura$rainClearsCustom(CommandSourceStack source, int duration, CallbackInfoReturnable<Integer> cir) {
      MistWeather.stop(source.m_81377_());
      BloodRainWeather.stopForVanillaRain(source.m_81377_());
   }

   @Inject(
      method = {"setThunder"},
      at = {@At("HEAD")}
   )
   private static void sakura$thunderClearsCustom(CommandSourceStack source, int duration, CallbackInfoReturnable<Integer> cir) {
      MistWeather.stop(source.m_81377_());
      BloodRainWeather.stopForVanillaRain(source.m_81377_());
   }
}
