package com.Luo_jiyv.SakuraMod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public final class MistClientState {
   private static final float FADE_SECONDS = 4.0F;
   private static final float FADE_TICKS = 80.0F;
   private static final float FADE_STEP = 0.0125F;
   private static final int ASH_PER_TICK = 3;
   private static boolean targetActive;
   private static float intensity;

   public static boolean isActive() {
      return targetActive;
   }

   public static float getIntensity() {
      return intensity;
   }

   public static void setActive(boolean value) {
      targetActive = value;
   }

   private static void reset() {
      targetActive = false;
      intensity = 0.0F;
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         Minecraft mc = Minecraft.m_91087_();
         if (mc.f_91073_ == null) {
            reset();
         } else {
            boolean inOverworld = WeatherVisuals.inOverworld();
            float target = targetActive && inOverworld ? 1.0F : 0.0F;
            if (intensity < target) {
               intensity = Math.min(target, intensity + 0.0125F);
            } else if (intensity > target) {
               intensity = Math.max(target, intensity - 0.0125F);
            }

            if (inOverworld && intensity > 0.0F && SakuraClientConfig.getWeatherRendering() != SakuraClientConfig.WeatherRendering.OFF) {
               spawnAsh(mc, intensity);
            }
         }
      }
   }

   private static void spawnAsh(Minecraft mc, float strength) {
      if (mc.f_91074_ != null) {
         int count = Math.round(3.0F * strength);
         if (count > 0) {
            RandomSource random = mc.f_91073_.m_213780_();

            for (int i = 0; i < count; i++) {
               double x = mc.f_91074_.m_20185_() + (random.m_188500_() - 0.5) * 24.0;
               double y = mc.f_91074_.m_20186_() + 1.0 + random.m_188500_() * 10.0;
               double z = mc.f_91074_.m_20189_() + (random.m_188500_() - 0.5) * 24.0;
               mc.f_91073_.m_7106_(ParticleTypes.f_123790_, x, y, z, 0.0, -0.015 - random.m_188500_() * 0.02, 0.0);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLoggingOut(LoggingOut event) {
      reset();
   }

   private MistClientState() {
   }
}
