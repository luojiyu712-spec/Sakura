package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.client.music.MusicCompatibility;
import com.Luo_jiyv.SakuraMod.client.music.MusicPlayerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MusicManager.class})
public abstract class MusicPlayerMixin implements MusicCompatibility.ScheduledMusic {
   @Shadow
   private SoundInstance f_120179_;
   @Shadow
   private int f_120180_;
   @Unique
   private SoundInstance sakura$scheduledMusic;
   @Unique
   private SoundInstance sakura$musicBeforeTick;

   @Shadow
   public abstract void m_120186_();

   @Override
   public SoundInstance sakura$getScheduledMusic() {
      return this.f_120179_ == this.sakura$scheduledMusic ? this.f_120179_ : null;
   }

   @Override
   public boolean sakura$ownsScheduledMusic(SoundInstance sound) {
      return sound == this.sakura$scheduledMusic;
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sakura$controlMusic(CallbackInfo ci) {
      Minecraft mc = Minecraft.m_91087_();
      this.sakura$musicBeforeTick = this.f_120179_;
      boolean scheduled = this.f_120179_ != null && this.f_120179_ == this.sakura$scheduledMusic;
      if (MusicPlayerClient.controlsMusicManager(mc)) {
         boolean external = MusicPlayerClient.externalAudioPlaying(mc);
         ResourceLocation preferred = mc.f_91073_ != null && mc.f_91074_ != null ? MusicCompatibility.preferredSituationalMusic(mc) : null;
         if (scheduled && (external || !this.f_120179_.m_7904_().equals(preferred))) {
            this.m_120186_();
            this.sakura$musicBeforeTick = null;
            if (preferred != null) {
               this.f_120180_ = 0;
            }
         }

         if (preferred == null || external) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void sakura$rememberScheduledMusic(CallbackInfo ci) {
      if (this.f_120179_ != this.sakura$musicBeforeTick) {
         this.sakura$scheduledMusic = this.f_120179_;
      }
   }
}
