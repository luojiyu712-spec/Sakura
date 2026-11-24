package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.effect.ModEffects;
import com.Luo_jiyv.SakuraMod.util.PlayerEquipCache;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class SakuraAmuletEvents {
   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END) {
         Player player = event.player;
         if (!player.m_9236_().m_5776_()) {
            if (player.f_19797_ % 10 == 0) {
               if (!PlayerEquipCache.hasSakuraAmulet(player)) {
                  tryRemoveBlessing(player);
               } else {
                  boolean fullSet = PlayerEquipCache.hasFullSakuraArmor(player);
                  int amplifier = fullSet ? 2 : 0;
                  MobEffectInstance current = player.m_21124_((MobEffect)ModEffects.SAKURA_BLESSING.get());
                  boolean needRefresh = current == null || current.m_19564_() < amplifier || current.m_19564_() == amplifier && current.m_19557_() < 220;
                  if (needRefresh) {
                     player.m_7292_(new MobEffectInstance((MobEffect)ModEffects.SAKURA_BLESSING.get(), 240, amplifier, false, false, true));
                  }
               }
            }
         }
      }
   }

   private static void tryRemoveBlessing(Player player) {
      if (PlayerEquipCache.countSakuraArmor(player) < 2) {
         MobEffectInstance current = player.m_21124_((MobEffect)ModEffects.SAKURA_BLESSING.get());
         if (current != null) {
            if (current.m_19564_() <= 2) {
               player.m_21195_((MobEffect)ModEffects.SAKURA_BLESSING.get());
            }
         }
      }
   }
}
