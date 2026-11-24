package com.Luo_jiyv.SakuraMod.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class BarrierStoneCache {
   public static final int CHUNK_RADIUS = 5;
   private static final Map<ResourceKey<Level>, Set<BlockPos>> STONES = new ConcurrentHashMap<>();

   private BarrierStoneCache() {
   }

   public static void register(ResourceKey<Level> dim, BlockPos pos) {
      STONES.computeIfAbsent(dim, k -> ConcurrentHashMap.newKeySet()).add(pos.m_7949_());
   }

   public static void unregister(ResourceKey<Level> dim, BlockPos pos) {
      Set<BlockPos> set = STONES.get(dim);
      if (set != null) {
         set.remove(pos);
         if (set.isEmpty()) {
            STONES.remove(dim, set);
         }
      }
   }

   public static boolean hasStones(ResourceKey<Level> dim) {
      Set<BlockPos> set = STONES.get(dim);
      return set != null && !set.isEmpty();
   }

   public static List<BlockPos> stones(ResourceKey<Level> dim) {
      Set<BlockPos> set = STONES.get(dim);
      return set != null && !set.isEmpty() ? List.copyOf(set) : List.of();
   }

   public static boolean isBlocked(ServerLevel level, BlockPos pos) {
      Set<BlockPos> set = STONES.get(level.m_46472_());
      if (set != null && !set.isEmpty()) {
         int cx = pos.m_123341_() >> 4;
         int cz = pos.m_123343_() >> 4;

         for (BlockPos stone : set) {
            int sx = stone.m_123341_() >> 4;
            int sz = stone.m_123343_() >> 4;
            if (Math.abs(cx - sx) <= 5 && Math.abs(cz - sz) <= 5) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static void clear() {
      STONES.clear();
   }
}
