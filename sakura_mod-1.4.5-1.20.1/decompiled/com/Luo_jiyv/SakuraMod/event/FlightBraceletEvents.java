package com.Luo_jiyv.SakuraMod.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class FlightBraceletEvents {
   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onBreakSpeed(BreakSpeed event) {
      Player player = event.getEntity();
      if (player != null) {
         if (!player.m_20096_() && player.m_150110_().f_35936_ && player.m_150110_().f_35935_) {
            if (GravCoreEvents.canProvideFlight(player)) {
               event.setNewSpeed(event.getNewSpeed() * 5.0F);
            }
         }
      }
   }
}
