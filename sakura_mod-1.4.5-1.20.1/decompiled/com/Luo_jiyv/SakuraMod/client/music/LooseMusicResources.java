package com.Luo_jiyv.SakuraMod.client.music;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.resources.IoSupplier;

final class LooseMusicResources extends AbstractPackResources {
   static final String CUSTOM_ID = "sakura_mod:custom_music";
   static final String SYNCED_ID = "sakura_mod:server_music";
   private static final byte[] METADATA = "{\"pack\":{\"pack_format\":15,\"description\":\"Sakura music\"}}".getBytes(StandardCharsets.UTF_8);
   private final List<MusicTrack> tracks;
   private final Map<ResourceLocation, Path> audioFiles;
   private final byte[] soundDefinitions;

   private LooseMusicResources(String packId, Map<MusicTrack, Path> sources) {
      super(packId, false);
      this.tracks = List.copyOf(sources.keySet());
      Map<ResourceLocation, Path> files = new HashMap<>();
      JsonObject definitions = new JsonObject();

      for (MusicTrack track : this.tracks) {
         files.put(ResourceLocation.fromNamespaceAndPath("sakura_mod", "sounds/" + track.soundLocation().m_135815_() + ".ogg"), sources.get(track));
         JsonObject source = new JsonObject();
         source.addProperty("name", track.soundLocation().toString());
         source.addProperty("stream", true);
         JsonArray sounds = new JsonArray();
         sounds.add(source);
         JsonObject definition = new JsonObject();
         definition.add("sounds", sounds);
         definitions.add(track.soundLocation().m_135815_(), definition);
      }

      this.audioFiles = Map.copyOf(files);
      this.soundDefinitions = definitions.toString().getBytes(StandardCharsets.UTF_8);
   }

   static LooseMusicResources scanCustom(Path directory) {
      Map<MusicTrack, Path> sources = new LinkedHashMap<>();
      scanCategory(directory.resolve("background"), MusicTrack.Tag.NORMAL, sources);
      scanCategory(directory.resolve("boss"), MusicTrack.Tag.BATTLE, sources);
      return new LooseMusicResources("sakura_mod:custom_music", sources);
   }

   static LooseMusicResources synced(Map<String, Path> paths) {
      Map<MusicTrack, Path> sources = new LinkedHashMap<>();

      for (MusicTrack track : MusicTracks.ALL) {
         Path file = paths.get(track.id());
         if (file != null) {
            sources.put(track, file);
         }
      }

      return new LooseMusicResources("sakura_mod:server_music", sources);
   }

   private static void scanCategory(Path directory, MusicTrack.Tag tag, Map<MusicTrack, Path> sources) {
      try {
         Files.createDirectories(directory);

         try (Stream<Path> files = Files.list(directory)) {
            for (Path file : files.filter(x$0 -> Files.isRegularFile(x$0)).sorted().toList()) {
               String filename = file.getFileName().toString();
               if (filename.toLowerCase(Locale.ROOT).endsWith(".ogg") && filename.length() > 4) {
                  String title = filename.substring(0, filename.length() - 4);
                  String key = UUID.nameUUIDFromBytes(filename.getBytes(StandardCharsets.UTF_8)).toString();
                  String id = "custom/" + directory.getFileName() + "/" + key;
                  sources.put(new MusicTrack(id, title, tag), file);
               }
            }
         }
      } catch (UncheckedIOException | IOException var12) {
         SakuraMod.LOGGER.warn("[Music] Could not scan custom music in {}", directory, var12);
      }
   }

   List<MusicTrack> tracks() {
      return this.tracks;
   }

   public IoSupplier<InputStream> m_8017_(String... elements) {
      return elements.length == 1 && "pack.mcmeta".equals(elements[0]) ? () -> new ByteArrayInputStream(METADATA) : null;
   }

   public IoSupplier<InputStream> m_214146_(PackType type, ResourceLocation location) {
      if (type != PackType.CLIENT_RESOURCES || !"sakura_mod".equals(location.m_135827_())) {
         return null;
      } else if ("sounds.json".equals(location.m_135815_())) {
         return () -> new ByteArrayInputStream(this.soundDefinitions);
      } else {
         Path file = this.audioFiles.get(location);
         return file == null ? null : () -> Files.newInputStream(file);
      }
   }

   public void m_8031_(PackType type, String namespace, String path, ResourceOutput output) {
      if (type == PackType.CLIENT_RESOURCES && "sakura_mod".equals(namespace)) {
         String prefix = path.isEmpty() ? "" : path + "/";
         this.audioFiles.forEach((id, file) -> {
            if (id.m_135815_().startsWith(prefix)) {
               output.accept(id, (IoSupplier)() -> Files.newInputStream(file));
            }
         });
      }
   }

   public Set<String> m_5698_(PackType type) {
      return type == PackType.CLIENT_RESOURCES ? Set.of("sakura_mod") : Set.of();
   }

   public void close() {
   }
}
