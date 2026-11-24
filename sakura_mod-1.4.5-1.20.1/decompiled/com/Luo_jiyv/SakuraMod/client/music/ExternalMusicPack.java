package com.Luo_jiyv.SakuraMod.client.music;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.music.MusicFiles;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddPackFindersEvent;

public final class ExternalMusicPack {
   private static final String LOCAL_ID = "sakura_mod:external_music";
   private static final Map<String, String> TITLES = Map.of(
      "sakura_mod:external_music",
      "config.sakura_mod.music.pack",
      "sakura_mod:server_music",
      "config.sakura_mod.music.synced.pack",
      "sakura_mod:custom_music",
      "config.sakura_mod.music.custom.pack"
   );

   public static void register(AddPackFindersEvent event) {
      if (event.getPackType() == PackType.CLIENT_RESOURCES) {
         event.addRepositorySource(
            accept -> {
               for (PackResources resources : scanPacks(ClientDefaultMusicSync.syncedFiles())) {
                  Pack pack = Pack.m_245429_(
                     resources.m_5542_(),
                     Component.m_237115_(TITLES.get(resources.m_5542_())),
                     true,
                     id -> resources,
                     PackType.CLIENT_RESOURCES,
                     Position.BOTTOM,
                     PackSource.f_10528_
                  );
                  if (pack != null) {
                     accept.accept(pack);
                  }
               }
            }
         );
      }
   }

   static List<PackResources> scanPacks(Map<String, Path> synced) {
      try {
         MusicFiles.prepareDefaults();
         MusicFiles.prepareCustom();
      } catch (IOException var3) {
         SakuraMod.LOGGER.warn("[Music] Could not prepare music folders in {}", MusicFiles.root(), var3);
      }

      List<PackResources> packs = new ArrayList<>();
      if (!synced.isEmpty()) {
         packs.add(LooseMusicResources.synced(synced));
      } else if (Files.isRegularFile(MusicFiles.defaults().resolve("pack.mcmeta"))) {
         packs.add(new PathPackResources("sakura_mod:external_music", MusicFiles.defaults(), false));
      }

      LooseMusicResources custom = LooseMusicResources.scanCustom(MusicFiles.custom());
      if (!custom.tracks().isEmpty()) {
         packs.add(custom);
      }

      return packs;
   }

   static Set<String> audioPackIds() {
      return TITLES.keySet();
   }

   public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
      event.registerReloadListener(
         (ResourceManagerReloadListener)resources -> {
            MusicAudioReload.resetAfterFullReload();
            MusicTracks.setCustomTracks(
               resources.m_7536_()
                  .filter(LooseMusicResources.class::isInstance)
                  .map(LooseMusicResources.class::cast)
                  .flatMap(pack -> pack.tracks().stream())
                  .filter(MusicTrack::isCustom)
                  .toList()
            );
            MusicPlayerClient.onMusicLibraryReloaded();
         }
      );
   }

   private ExternalMusicPack() {
   }
}
