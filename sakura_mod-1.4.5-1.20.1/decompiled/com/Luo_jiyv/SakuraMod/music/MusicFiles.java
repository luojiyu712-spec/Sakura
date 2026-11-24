package com.Luo_jiyv.SakuraMod.music;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraftforge.fml.loading.FMLPaths;

public final class MusicFiles {
   public static final int MAX_TRACK_BYTES = 33554432;
   public static final long MAX_PACK_BYTES = 536870912L;
   private static final String SOUNDS = "assets/sakura_mod/sounds/music";
   private static final Pattern SHA256 = Pattern.compile("[a-f0-9]{64}");
   public static final Set<String> DEFAULT_IDS = loadDefaultIds();

   public static Path root() {
      return FMLPaths.GAMEDIR.get().resolve("sakura_music");
   }

   public static Path defaults() {
      return root().resolve("default");
   }

   public static Path defaultAudio(String id) {
      return defaults().resolve("assets/sakura_mod/sounds/music").resolve(id + ".ogg");
   }

   public static Path custom() {
      return root().resolve("custom");
   }

   public static Path cache() {
      return defaults().resolve("server-cache");
   }

   public static synchronized void prepareDefaults() throws IOException {
      Path target = defaults();
      Files.createDirectories(target);
      Path oldMetadata = root().resolve("pack.mcmeta");
      Path oldAssets = root().resolve("assets");
      if (Files.isRegularFile(oldMetadata)
         && Files.isDirectory(oldAssets)
         && !Files.exists(target.resolve("pack.mcmeta"))
         && !Files.exists(target.resolve("assets"))) {
         Files.move(oldAssets, target.resolve("assets"));

         try {
            Files.move(oldMetadata, target.resolve("pack.mcmeta"));
         } catch (IOException var6) {
            try {
               Files.move(target.resolve("assets"), oldAssets);
            } catch (IOException var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }
      }
   }

   public static void prepareCustom() throws IOException {
      Path oldCustom = FMLPaths.GAMEDIR.get().resolve("sakura_music_custom");

      for (String category : List.of("background", "boss")) {
         Path target = custom().resolve(category);
         Path previous = oldCustom.resolve(category);
         if (Files.isDirectory(previous) && !Files.exists(target)) {
            Files.createDirectories(custom());
            Files.move(previous, target);
         } else {
            Files.createDirectories(target);
         }
      }
   }

   public static boolean validHash(String hash) {
      return SHA256.matcher(hash).matches();
   }

   public static MessageDigest digest() {
      try {
         return MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException var1) {
         throw new IllegalStateException(var1);
      }
   }

   public static String hash(Path file) throws IOException {
      MessageDigest digest = digest();

      try (DigestInputStream stream = new DigestInputStream(Files.newInputStream(file), digest)) {
         stream.transferTo(OutputStream.nullOutputStream());
      }

      return HexFormat.of().formatHex(digest.digest());
   }

   private static Set<String> loadDefaultIds() {
      try {
         Set var1;
         try (InputStream input = MusicFiles.class.getResourceAsStream("/sakura_default_music_catalog.json")) {
            if (input == null) {
               throw new IOException("Missing default music catalogue");
            }

            var1 = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8))
               .getAsJsonObject()
               .keySet()
               .stream()
               .filter(key -> key.matches("music/[a-z0-9_]+"))
               .map(key -> key.substring("music/".length()))
               .collect(Collectors.toUnmodifiableSet());
         }

         return var1;
      } catch (IOException var5) {
         throw new IllegalStateException("Cannot load the default music catalogue", var5);
      }
   }

   private MusicFiles() {
   }
}
