package com.Luo_jiyv.SakuraMod.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientFlightTimeCache {
   private static volatile int target = 0;
   private static volatile float displayed = 0.0F;
   private static volatile boolean initialized = false;

   private ClientFlightTimeCache() {
   }

   public static float getFloat() {
      return displayed;
   }

   public static int get() {
      return Math.round(displayed);
   }

   public static int getTarget() {
      return target;
   }

   public static void reset() {
      target = 0;
      displayed = 0.0F;
      initialized = false;
   }

   public static void set(int t) {
      target = t;
      if (!initialized) {
         displayed = t;
         initialized = true;
      }
   }

   public static void tick() {
      float diff = target - displayed;
      if (Math.abs(diff) < 0.05F) {
         displayed = target;
      } else {
         displayed += diff * 0.2F;
      }
   }
}
