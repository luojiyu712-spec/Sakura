package com.Luo_jiyv.SakuraMod.weather;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class BloodRainEffects {
   private static final int CHECK_INTERVAL = 40;
   private static final int EFFECT_DURATION = 200;
   private static final int REFRESH_BELOW = 120;

   @SubscribeEvent
   public static void onLivingTick(LivingTickEvent event) {
      if (BloodRainWeather.isActive()) {
         LivingEntity entity = event.getEntity();
         if (!entity.m_9236_().m_5776_()) {
            if (entity.m_6084_()) {
               if (entity.f_19797_ % 40 == 0) {
                  if (isExposedToWeather(entity)) {
                     MobEffectInstance current = entity.m_21124_(MobEffects.f_19600_);
                     if (current == null || current.m_19557_() < 120) {
                        entity.m_7292_(new MobEffectInstance(MobEffects.f_19600_, 200, 0, false, false, true));
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean isExposedToWeather(LivingEntity entity) {
      Level level = entity.m_9236_();
      if (!level.m_46471_()) {
         return false;
      } else {
         BlockPos pos = entity.m_20183_();
         if (!level.m_45527_(pos)) {
            return false;
         } else {
            Precipitation precipitation = ((Biome)level.m_204166_(pos).m_203334_()).m_264600_(pos);
            return precipitation != Precipitation.NONE;
         }
      }
   }

   private BloodRainEffects() {
   }
}
