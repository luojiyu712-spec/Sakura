package com.Luo_jiyv.SakuraMod.util;

import com.Luo_jiyv.SakuraMod.Config;
import java.util.Set;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public final class RandomTeleporterSettings {
   public static final int DEFAULT_MIN_RADIUS = 200;
   public static final int DEFAULT_MAX_RADIUS = 5000;
   public static final int MODE_SURFACE = 0;
   public static final int MODE_SKY = 1;
   public static final int MODE_CAVE = 2;
   public static final int MODE_ANY = 3;

   public static int getMode(MinecraftServer server, UUID uuid) {
      return RandomTeleporterSettingsData.get(server).getMode(uuid);
   }

   public static void setMode(MinecraftServer server, UUID uuid, int mode) {
      RandomTeleporterSettingsData.get(server).setMode(uuid, mode);
   }

   private RandomTeleporterSettings() {
   }

   public static int[] get(MinecraftServer server, UUID uuid) {
      int[] r = RandomTeleporterSettingsData.get(server).getRadius(uuid);
      return clampRadius(r[0], r[1]);
   }

   public static void set(MinecraftServer server, UUID uuid, int min, int max) {
      int[] clamped = clampRadius(min, max);
      RandomTeleporterSettingsData.get(server).setRadius(uuid, clamped[0], clamped[1]);
   }

   private static int[] clampRadius(int min, int max) {
      int limit = Config.getRandomTeleporterMaxRadiusLimit();
      int lo = Math.max(0, Math.min(limit, min));
      int hi = Math.max(lo, Math.min(limit, max));
      return new int[]{lo, hi};
   }

   public static void remove(MinecraftServer server, UUID uuid) {
      RandomTeleporterSettingsData.get(server).remove(uuid);
   }

   public static Set<ResourceLocation> getAllowedBiomes(MinecraftServer server, UUID uuid) {
      return RandomTeleporterSettingsData.get(server).getAllowedBiomes(uuid);
   }

   public static void setAllowedBiomes(MinecraftServer server, UUID uuid, Set<ResourceLocation> biomes) {
      RandomTeleporterSettingsData.get(server).setAllowedBiomes(uuid, biomes);
   }

   public static Set<ResourceLocation> getAllowedStructures(MinecraftServer server, UUID uuid) {
      return RandomTeleporterSettingsData.get(server).getAllowedStructures(uuid);
   }

   public static void setAllowedStructures(MinecraftServer server, UUID uuid, Set<ResourceLocation> structures) {
      RandomTeleporterSettingsData.get(server).setAllowedStructures(uuid, structures);
   }
}
