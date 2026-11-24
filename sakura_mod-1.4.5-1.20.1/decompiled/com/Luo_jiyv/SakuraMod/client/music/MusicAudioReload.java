package com.Luo_jiyv.SakuraMod.client.music;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceFilterSection;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.util.profiling.InactiveProfiler;

public final class MusicAudioReload {
   private static final PreparationBarrier IMMEDIATE = new PreparationBarrier() {
      public <T> CompletableFuture<T> m_6769_(T prepared) {
         return CompletableFuture.completedFuture(prepared);
      }
   };
   private static MusicAudioReload.Audio audio;
   private static int latest;

   public static CompletableFuture<Integer> reload() {
      Minecraft mc = Minecraft.m_91087_();
      SoundManager soundManager = mc.m_91106_();
      Map<String, Path> synced = ClientDefaultMusicSync.syncedFiles();
      List<PackResources> packs = mc.m_91098_().m_7536_().filter(pack -> !ExternalMusicPack.audioPackIds().contains(pack.m_5542_())).toList();
      int request = ++latest;
      return CompletableFuture.<MusicAudioReload.Scope>supplyAsync(() -> new MusicAudioReload.Scope(request, synced, packs), Util.m_183992_())
         .thenCompose(
            scope -> soundManager.m_5540_(IMMEDIATE, scope, InactiveProfiler.f_18554_, InactiveProfiler.f_18554_, Util.m_183992_(), mc)
               .thenApply(unused -> scope.count)
         );
   }

   static void resetAfterFullReload() {
      latest++;
      audio = null;
   }

   static MusicAudioReload.Audio audio() {
      return audio;
   }

   static WeighedSoundEvents registration(SoundManager manager, ResourceLocation location) {
      return audio != null ? audio.registry().get(location) : manager.m_120384_(location);
   }

   private MusicAudioReload() {
   }

   record Audio(Map<ResourceLocation, WeighedSoundEvents> registry, SoundBufferLibrary buffers) {
   }

   public static final class Scope extends FallbackResourceManager {
      private final int request;
      private final List<MusicTrack> customTracks;
      private int count = -1;

      private Scope(int request, Map<String, Path> synced, List<PackResources> borrowedPacks) {
         super(PackType.CLIENT_RESOURCES, "sakura_mod");
         this.request = request;
         List<PackResources> local = ExternalMusicPack.scanPacks(synced);
         local.forEach(this::m_215377_);
         borrowedPacks.forEach(this::pushSakuraResources);
         this.customTracks = local.stream()
            .filter(LooseMusicResources.class::isInstance)
            .map(LooseMusicResources.class::cast)
            .flatMap(pack -> pack.tracks().stream())
            .filter(MusicTrack::isCustom)
            .toList();
      }

      private void pushSakuraResources(PackResources pack) {
         boolean hasNamespace = pack.m_5698_(PackType.CLIENT_RESOURCES).contains("sakura_mod");
         ResourceFilterSection filter = null;

         try {
            filter = (ResourceFilterSection)pack.m_5550_(ResourceFilterSection.f_244163_);
         } catch (IOException var5) {
            SakuraMod.LOGGER.debug("[Music] Could not read filters for {}", pack.m_5542_(), var5);
         }

         if (filter != null && filter.m_215523_("sakura_mod")) {
            ResourceFilterSection applicable = filter;
            if (hasNamespace) {
               this.m_215382_(pack, id -> applicable.m_215528_(id.m_135815_()));
            } else {
               this.m_215399_(pack.m_5542_(), id -> applicable.m_215528_(id.m_135815_()));
            }
         } else if (hasNamespace) {
            this.m_215377_(pack);
         }
      }

      public void apply(Map<ResourceLocation, WeighedSoundEvents> registry, Map<ResourceLocation, Resource> files) {
         if (this.request == MusicAudioReload.latest) {
            MusicAudioReload.audio = new MusicAudioReload.Audio(Map.copyOf(registry), new SoundBufferLibrary(ResourceProvider.m_247621_(Map.copyOf(files))));
            MusicTracks.setCustomTracks(this.customTracks);
            MusicPlayerClient.onMusicLibraryReloaded();
            this.count = (int)MusicTracks.all().stream().filter(track -> {
               WeighedSoundEvents event = registry.get(track.soundLocation());
               return event != null && event.m_7789_() > 0;
            }).count();
         }
      }
   }
}
