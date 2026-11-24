package com.Luo_jiyv.SakuraMod.weather;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class MistSleepEvents {
   @SubscribeEvent
   public static void onSleepingTimeCheck(SleepingTimeCheckEvent event) {
      if (MistWeather.isActive()) {
         if (event.getEntity().m_9236_() instanceof ServerLevel level) {
            if (Level.f_46428_.equals(level.m_46472_())) {
               event.setResult(Result.ALLOW);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onWakeUp(PlayerWakeUpEvent event) {
      if (MistWeather.isActive()) {
         if (!event.getEntity().m_9236_().m_5776_()) {
            if (event.getEntity() instanceof ServerPlayer player) {
               if (Level.f_46428_.equals(player.m_9236_().m_46472_())) {
                  if (!event.wakeImmediately()) {
                     if (player.m_9236_().m_46469_().m_46207_(GameRules.f_46150_)) {
                        MistWeather.stop(player.m_20194_());
                     }
                  }
               }
            }
         }
      }
   }

   private MistSleepEvents() {
   }
}
