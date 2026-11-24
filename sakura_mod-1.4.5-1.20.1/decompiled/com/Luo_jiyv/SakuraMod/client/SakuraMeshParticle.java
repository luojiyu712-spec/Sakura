package com.Luo_jiyv.SakuraMod.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;

public abstract class SakuraMeshParticle extends TextureSheetParticle {
   protected SakuraMeshParticle(ClientLevel level, double x, double y, double z) {
      super(level, x, y, z);
   }

   public final boolean shouldCull() {
      return false;
   }
}
