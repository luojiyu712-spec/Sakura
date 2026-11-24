package com.Luo_jiyv.SakuraMod.client.music;

import com.Luo_jiyv.SakuraMod.client.SakuraClientConfig;
import com.Luo_jiyv.SakuraMod.mixin.SoundEngineAccessor;
import com.Luo_jiyv.SakuraMod.mixin.SoundManagerAccessor;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.client.sounds.ChannelAccess.ChannelHandle;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MusicCompatibility {
   @Nullable
   public static ResourceLocation preferredSituationalMusic(Minecraft mc) {
      Music music = mc.m_91107_();
      ResourceLocation soundId = ((SoundEvent)music.m_263193_().m_203334_()).m_11660_();
      if (soundId.equals(((SoundEvent)Musics.f_11648_.m_263193_().m_203334_()).m_11660_())) {
         return soundId;
      } else {
         boolean modSound = isForeign(soundId);
         Holder<Biome> biome = mc.f_91073_.m_204166_(mc.f_91074_.m_20183_());
         boolean modBiome = biome.m_203543_().map(key -> isForeign(key.m_135782_())).orElse(false);
         if (modSound || modBiome) {
            Music biomeMusic = (Music)((Biome)biome.m_203334_()).m_47566_().orElse(null);
            if (biomeMusic != null && ((SoundEvent)biomeMusic.m_263193_().m_203334_()).m_11660_().equals(soundId)) {
               return SakuraClientConfig.shouldOverrideModBiomeMusic() ? null : soundId;
            }

            if (modSound) {
               return soundId;
            }
         }

         return null;
      }
   }

   private static boolean isForeign(ResourceLocation id) {
      String namespace = id.m_135827_();
      return !"minecraft".equals(namespace) && !"sakura_mod".equals(namespace);
   }

   static boolean externalAudioPlaying(Minecraft mc) {
      if (mc.m_91106_() instanceof SoundManagerAccessor access && access.sakura$getSoundEngine() instanceof SoundEngineAccessor engine) {
         MusicCompatibility.ScheduledMusic scheduler = mc.m_91397_() instanceof MusicCompatibility.ScheduledMusic accessMusic ? accessMusic : null;
         if (mc.f_91066_.m_92147_(SoundSource.MASTER) <= 0.0F) {
            return false;
         } else {
            for (Entry<SoundInstance, ChannelHandle> entry : engine.sakura$getActiveChannels().entrySet()) {
               SoundInstance sound = entry.getKey();
               SoundSource source = sound.m_8070_();
               if (!MusicPlayerClient.ownsSound(sound)
                  && (scheduler == null || !scheduler.sakura$ownsScheduledMusic(sound))
                  && !entry.getValue().m_120151_()
                  && (source == SoundSource.MUSIC || source == SoundSource.RECORDS || isIssCswMusic(sound))
                  && mc.f_91066_.m_92147_(source) > 0.0F
                  && audibleAtCamera(mc, sound)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean audibleAtCamera(Minecraft mc, SoundInstance sound) {
      float volume = sound.m_7769_();
      if (volume <= 0.0F) {
         return false;
      } else if (sound.m_7438_() == Attenuation.NONE) {
         return true;
      } else {
         Sound resolved = sound.m_5891_();
         if (resolved == null) {
            return false;
         } else {
            Camera camera = mc.f_91063_.m_109153_();
            double radius = Math.max(1.0F, volume) * resolved.m_119798_();
            Vec3 listener = sound.m_7796_() ? Vec3.f_82478_ : (camera.m_90593_() ? camera.m_90583_() : (mc.f_91074_ != null ? mc.f_91074_.m_146892_() : null));
            if (listener == null) {
               return false;
            } else {
               double distanceSq = listener.m_82531_(sound.m_7772_(), sound.m_7780_(), sound.m_7778_());
               return distanceSq < radius * radius;
            }
         }
      }
   }

   static boolean isIssCswMusic(SoundInstance sound) {
      ResourceLocation location = sound.m_7904_();
      return "iss_csw".equals(location.m_135827_()) && location.m_135815_().contains("nameless_wizard");
   }

   private MusicCompatibility() {
   }

   public interface ScheduledMusic {
      @Nullable
      SoundInstance sakura$getScheduledMusic();

      boolean sakura$ownsScheduledMusic(SoundInstance var1);
   }
}
