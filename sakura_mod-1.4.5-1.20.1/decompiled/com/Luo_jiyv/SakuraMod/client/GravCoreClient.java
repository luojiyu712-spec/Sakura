package com.Luo_jiyv.SakuraMod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GravCoreClient {
   public static void playChargeSound() {
      Minecraft mc = Minecraft.m_91087_();
      if (mc.f_91074_ != null) {
         mc.m_91106_().m_120367_(SimpleSoundInstance.m_119755_(SoundEvents.f_215772_, 1.0F, 1.5F));
      }
   }

   public static void playModeSwitchSound() {
      Minecraft mc = Minecraft.m_91087_();
      if (mc.f_91074_ != null) {
         mc.m_91106_().m_120367_(SimpleSoundInstance.m_119755_(SoundEvents.f_144243_, 1.2F, 1.8F));
      }
   }
}
