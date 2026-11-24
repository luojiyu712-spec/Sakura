package com.Luo_jiyv.SakuraMod.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public class ShieldHudEditKeyHandler {
   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         while (ModKeyMappings.EDIT_SHIELD_HUD.m_90859_()) {
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91080_ == null && mc.f_91074_ != null) {
               mc.m_91152_(new ShieldHudEditScreen());
            }
         }
      }
   }
}
