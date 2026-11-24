package com.Luo_jiyv.SakuraMod.util;

import java.util.Map;
import java.util.WeakHashMap;

public final class RequestThrottle {
   private static final long MIN_INTERVAL = 40L;
   private static final Map<Object, Long> LAST_SERVED = new WeakHashMap<>();

   private RequestThrottle() {
   }

   public static boolean allow(Object player, long gameTime) {
      Long last = LAST_SERVED.get(player);
      if (last != null && gameTime - last < 40L) {
         return false;
      } else {
         LAST_SERVED.put(player, gameTime);
         return true;
      }
   }
}
