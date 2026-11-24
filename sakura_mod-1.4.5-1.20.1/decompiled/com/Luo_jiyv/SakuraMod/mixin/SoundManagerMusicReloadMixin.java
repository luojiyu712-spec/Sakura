package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.client.music.MusicAudioReload;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SoundManager.class})
public abstract class SoundManagerMusicReloadMixin {
   @Inject(
      method = {"apply(Lnet/minecraft/client/sounds/SoundManager$Preparations;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sakura$applyMusicOnly(@Coerce Object prepared, ResourceManager resources, ProfilerFiller profiler, CallbackInfo ci) {
      if (resources instanceof MusicAudioReload.Scope scope) {
         SoundPreparationsAccessor access = (SoundPreparationsAccessor)prepared;
         scope.apply(access.sakura$getRegistry(), access.sakura$getFiles());
         ci.cancel();
      }
   }
}
