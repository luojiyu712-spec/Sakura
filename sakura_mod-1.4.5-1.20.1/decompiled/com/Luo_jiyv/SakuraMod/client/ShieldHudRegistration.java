package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.event.ShieldHudOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ShieldHudRegistration {
   @SubscribeEvent
   public static void registerOverlays(RegisterGuiOverlaysEvent event) {
      event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "shield_bar", new ShieldHudOverlay());
   }
}
