package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.item.GravCoreItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientGravCoreModeCache {
   private static GravCoreItem.Mode mode = GravCoreItem.Mode.BURST;

   public static GravCoreItem.Mode getMode() {
      return mode;
   }

   public static void setMode(GravCoreItem.Mode m) {
      mode = m;
   }

   public static void toggle() {
      mode = mode.next();
   }
}
