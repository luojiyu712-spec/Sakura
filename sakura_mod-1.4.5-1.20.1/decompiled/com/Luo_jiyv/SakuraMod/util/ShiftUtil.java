package com.Luo_jiyv.SakuraMod.util;

import java.lang.reflect.Method;

public final class ShiftUtil {
   private static Method hasShiftDownMethod;

   private ShiftUtil() {
   }

   public static boolean isShiftDown() {
      if (hasShiftDownMethod == null) {
         return false;
      } else {
         try {
            return (Boolean)hasShiftDownMethod.invoke(null);
         } catch (Throwable var1) {
            return false;
         }
      }
   }

   static {
      try {
         Class<?> screenCls = Class.forName("net.minecraft.client.gui.screens.Screen");
         hasShiftDownMethod = screenCls.getMethod("hasShiftDown");
      } catch (Throwable var1) {
         hasShiftDownMethod = null;
      }
   }
}
