package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.attribute.ModAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class SakuraAmuletCombatEvents {
   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      if (event.getSource().m_7639_() instanceof Player player) {
         if (event.getEntity() != player) {
            if (!ModAttributes.hasSharedAmuletAttributes()) {
               double critChance = player.m_21133_((Attribute)ModAttributes.CRIT_CHANCE.get());
               if (!(critChance <= 0.0)) {
                  if (player.m_217043_().m_188500_() < critChance) {
                     double critDamage = player.m_21133_((Attribute)ModAttributes.CRIT_DAMAGE.get());
                     float damage = event.getAmount();
                     damage *= (float)(1.5 * (1.0 + critDamage));
                     event.setAmount(damage);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingHeal(LivingHealEvent event) {
      if (event.getEntity() instanceof Player player) {
         if (!ModAttributes.hasSharedAmuletAttributes()) {
            double regenBonus = player.m_21133_((Attribute)ModAttributes.REGEN_BONUS.get());
            if (!(regenBonus <= 0.0)) {
               event.setAmount(event.getAmount() * (float)(1.0 + regenBonus));
            }
         }
      }
   }
}
