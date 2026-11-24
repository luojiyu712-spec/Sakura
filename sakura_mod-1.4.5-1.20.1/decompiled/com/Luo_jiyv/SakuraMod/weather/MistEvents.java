package com.Luo_jiyv.SakuraMod.weather;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class MistEvents {
   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         MistWeather.tick(event.getServer());
      }
   }

   @SubscribeEvent
   public static void onServerStarted(ServerStartedEvent event) {
      MistWeather.init(event.getServer());
   }

   @SubscribeEvent
   public static void onLogin(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         MistWeather.syncTo(player);
      }
   }

   private MistEvents() {
   }
}
