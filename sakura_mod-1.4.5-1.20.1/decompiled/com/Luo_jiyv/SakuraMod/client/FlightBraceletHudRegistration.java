package com.Luo_jiyv.SakuraMod.client;

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
public class FlightBraceletHudRegistration {
   @SubscribeEvent
   public static void registerOverlays(RegisterGuiOverlaysEvent event) {
      event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "flight_bracelet_bar", new FlightBraceletOverlay());
   }
}
