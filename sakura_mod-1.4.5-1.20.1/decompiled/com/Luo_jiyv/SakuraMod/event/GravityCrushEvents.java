package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.effect.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class GravityCrushEvents {
   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      LivingEntity target = event.getEntity();
      MobEffectInstance inst = target.m_21124_((MobEffect)ModEffects.GRAVITY_CRUSH.get());
      if (inst != null) {
         int amplifier = inst.m_19564_();
         float bonus = Math.min(0.6F, 0.15F + 0.1F * amplifier);
         event.setAmount(event.getAmount() * (1.0F + bonus));
      }
   }

   @SubscribeEvent
   public static void onLivingHeal(LivingHealEvent event) {
      if (event.getEntity().m_21023_((MobEffect)ModEffects.GRAVITY_CRUSH.get())) {
         event.setCanceled(true);
      }
   }
}
