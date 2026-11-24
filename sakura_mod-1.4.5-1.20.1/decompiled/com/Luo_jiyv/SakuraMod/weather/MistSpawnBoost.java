package com.Luo_jiyv.SakuraMod.weather;

import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.MobSpawnEvent.SpawnPlacementCheck;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class MistSpawnBoost {
   private static final Set<EntityType<?>> UNDEAD = Set.of(
      EntityType.f_20501_,
      EntityType.f_20458_,
      EntityType.f_20562_,
      EntityType.f_20530_,
      EntityType.f_20524_,
      EntityType.f_20481_,
      EntityType.f_20497_,
      EntityType.f_20531_,
      EntityType.f_20500_,
      EntityType.f_20509_,
      EntityType.f_20525_,
      EntityType.f_20502_
   );

   @SubscribeEvent
   public static void onSpawnPlacementCheck(SpawnPlacementCheck event) {
      if (MistWeather.isActive()) {
         MobSpawnType spawnType = event.getSpawnType();
         if (spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION) {
            if (UNDEAD.contains(event.getEntityType())) {
               ServerLevel level = event.getLevel().m_6018_();
               if (Level.f_46428_.equals(level.m_46472_())) {
                  if (level.m_46791_() != Difficulty.PEACEFUL) {
                     if (!event.getDefaultResult()) {
                        event.setResult(Result.ALLOW);
                     }
                  }
               }
            }
         }
      }
   }

   private MistSpawnBoost() {
   }
}
