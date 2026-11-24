package com.Luo_jiyv.SakuraMod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class CapsuleClient {
   private CapsuleClient() {
   }

   public static void openScreen(InteractionHand hand) {
      Minecraft.m_91087_().m_91152_(new CapsuleScreen(hand));
   }
}
