package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.damage.ModDamageTypes;
import com.Luo_jiyv.SakuraMod.damage.SakuraSpecialDamage;
import com.Luo_jiyv.SakuraMod.effect.ModEffects;
import com.Luo_jiyv.SakuraMod.util.PlayerEquipCache;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class GravCoreAttackEvents {
   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      if (Config.isGravityAttackEnabled()) {
         if (!event.getSource().m_276093_(ModDamageTypes.GRAVITY_CRUSH) && !event.getSource().m_276093_(ModDamageTypes.GRAVITY_CANNON)) {
            if (event.getSource().m_7639_() instanceof Player player) {
               LivingEntity target = event.getEntity();
               if (target != player) {
                  if (!SakuraSpecialDamage.isImmuneTarget(target)) {
                     boolean holding = PlayerEquipCache.handHoldsGravCore(player);
                     if (holding) {
                        MobEffectInstance current = target.m_21124_((MobEffect)ModEffects.GRAVITY_CRUSH.get());
                        if (current != null) {
                           if (current.m_19564_() > 0) {
                              return;
                           }

                           if (current.m_19564_() == 0 && current.m_19557_() > 1180) {
                              return;
                           }
                        }

                        target.m_7292_(new MobEffectInstance((MobEffect)ModEffects.GRAVITY_CRUSH.get(), 1200, 0, false, false, true));
                     }
                  }
               }
            }
         }
      }
   }
}
