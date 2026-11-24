package com.Luo_jiyv.SakuraMod.client;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public final class ClientRandomTeleporterSettings {
   public static final int MODE_SURFACE = 0;
   public static final int MODE_SKY = 1;
   public static final int MODE_CAVE = 2;
   public static final int MODE_ANY = 3;
   private static int minRadius = 200;
   private static int maxRadius = 5000;
   private static int mode = 0;
   private static Set<ResourceLocation> allowedBiomes = new HashSet<>();
   private static Set<ResourceLocation> structures = new HashSet<>();

   private ClientRandomTeleporterSettings() {
   }

   public static Set<ResourceLocation> getAllowedBiomes() {
      return new HashSet<>(allowedBiomes);
   }

   public static void setBiomes(Set<ResourceLocation> biomes) {
      allowedBiomes = biomes != null ? new HashSet<>(biomes) : new HashSet<>();
   }

   public static int getMinRadius() {
      return minRadius;
   }

   public static int getMaxRadius() {
      return maxRadius;
   }

   public static void set(int min, int max) {
      minRadius = min;
      maxRadius = max;
   }

   public static Set<ResourceLocation> getAllowedStructures() {
      return structures;
   }

   public static void setStructures(Set<ResourceLocation> s) {
      structures = s == null ? new HashSet<>() : new HashSet<>(s);
   }

   public static int getMode() {
      return mode;
   }

   public static void setMode(int m) {
      mode = m;
   }
}
