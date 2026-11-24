package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.weather.MistWeather;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NaturalSpawner.AfterSpawnCallback;
import net.minecraft.world.level.NaturalSpawner.SpawnPredicate;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NaturalSpawner.class})
public abstract class NaturalSpawnerMistMixin {
   private static final int EXTRA_SURFACE_CYCLES = 2;
   private static final float MAX_TICK_TIME_MS = 45.0F;

   @Inject(
      method = {"spawnCategoryForChunk"},
      at = {@At("HEAD")}
   )
   private static void sakura$extraSurfaceSpawnCycles(
      MobCategory category, ServerLevel level, LevelChunk chunk, SpawnPredicate filter, AfterSpawnCallback callback, CallbackInfo ci
   ) {
      if (category == MobCategory.MONSTER) {
         if (MistWeather.isActive()) {
            if (Level.f_46428_.equals(level.m_46472_())) {
               if (level.m_46791_() != Difficulty.PEACEFUL) {
                  MinecraftServer server = level.m_7654_();
                  if (server == null || !(server.m_129903_() > 45.0F)) {
                     RandomSource random = level.m_213780_();
                     ChunkPos chunkPos = chunk.m_7697_();

                     for (int i = 0; i < 2; i++) {
                        int x = chunkPos.m_45604_() + random.m_188503_(16);
                        int z = chunkPos.m_45605_() + random.m_188503_(16);
                        int y = level.m_6924_(Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                        NaturalSpawner.m_47038_(category, level, chunk, new BlockPos(x, y, z), filter, callback);
                     }
                  }
               }
            }
         }
      }
   }
}
