package com.Luo_jiyv.SakuraMod.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.util.RandomSource;

public final class GoldenCubeWhispers {
   private static final String[] POOLS = new String[]{"reminder", "unease", "craving", "fracture", "delirium", "collapse", "pain"};
   private static final int[] SIZES = new int[]{24, 24, 24, 24, 24, 40, 48};
   private final RandomSource random;
   private final List<String> remaining = new ArrayList<>();
   private final ArrayDeque<String> recent = new ArrayDeque<>();
   private int mode;

   public GoldenCubeWhispers(RandomSource random) {
      this.random = random;
   }

   public static int poolSize(int mode) {
      return SIZES[checkedIndex(mode)];
   }

   private static int checkedIndex(int mode) {
      if (mode >= 1 && mode <= 7) {
         return mode - 1;
      } else {
         throw new IllegalArgumentException("Inactive whisper mode: " + mode);
      }
   }

   public void reset() {
      this.mode = 0;
      this.remaining.clear();
      this.recent.clear();
   }

   public String nextKey(int newMode, Set<String> visible) {
      checkedIndex(newMode);
      if (this.mode != newMode) {
         this.reset();
         this.mode = newMode;
      }

      for (int attempt = 0; attempt < 2; attempt++) {
         if (this.remaining.isEmpty()) {
            this.refill();
         }

         for (int i = this.remaining.size() - 1; i >= 0; i--) {
            String key = this.remaining.get(i);
            if (!visible.contains(key) && !this.recent.contains(key)) {
               this.remaining.remove(i);
               this.recent.addLast(key);
               if (this.recent.size() > 8) {
                  this.recent.removeFirst();
               }

               return key;
            }
         }

         this.refill();
      }

      throw new IllegalStateException("Whisper pool exhausted by visible texts");
   }

   private void refill() {
      this.remaining.clear();

      for (int i = 1; i <= poolSize(this.mode); i++) {
         this.remaining.add("message.sakura_mod.golden_whisper." + POOLS[this.mode - 1] + "." + i);
      }

      for (int i = this.remaining.size() - 1; i > 0; i--) {
         int j = this.random.m_188503_(i + 1);
         String key = this.remaining.get(i);
         this.remaining.set(i, this.remaining.get(j));
         this.remaining.set(j, key);
      }
   }
}
