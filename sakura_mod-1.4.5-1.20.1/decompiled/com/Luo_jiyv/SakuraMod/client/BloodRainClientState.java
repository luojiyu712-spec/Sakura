package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.network.BloodRainStatePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public final class BloodRainClientState {
   private static BloodRainStatePacket.Phase phase = BloodRainStatePacket.Phase.INACTIVE;

   private BloodRainClientState() {
   }

   public static boolean isActive() {
      return phase == BloodRainStatePacket.Phase.ACTIVE;
   }

   public static boolean hasBloodPrecipitation() {
      return phase != BloodRainStatePacket.Phase.INACTIVE;
   }

   public static void setPhase(BloodRainStatePacket.Phase value) {
      phase = value;
   }

   @SubscribeEvent
   public static void onLoggingOut(LoggingOut event) {
      phase = BloodRainStatePacket.Phase.INACTIVE;
   }
}
