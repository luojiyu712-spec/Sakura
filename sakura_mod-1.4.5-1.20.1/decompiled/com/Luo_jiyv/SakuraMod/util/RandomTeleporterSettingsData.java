package com.Luo_jiyv.SakuraMod.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class RandomTeleporterSettingsData extends SavedData {
   private static final String DATA_NAME = "sakura_random_teleporter_settings";
   private final Map<UUID, int[]> radius = new HashMap<>();
   private final Map<UUID, Set<ResourceLocation>> allowedBiomes = new HashMap<>();
   private final Map<UUID, Set<ResourceLocation>> allowedStructures = new HashMap<>();
   private final Map<UUID, Integer> modes = new HashMap<>();

   public static RandomTeleporterSettingsData get(MinecraftServer server) {
      ServerLevel overworld = server.m_129783_();
      return (RandomTeleporterSettingsData)overworld.m_8895_()
         .m_164861_(RandomTeleporterSettingsData::load, RandomTeleporterSettingsData::new, "sakura_random_teleporter_settings");
   }

   public static RandomTeleporterSettingsData load(CompoundTag tag) {
      RandomTeleporterSettingsData data = new RandomTeleporterSettingsData();
      ListTag list = tag.m_128437_("entries", 10);

      for (int i = 0; i < list.size(); i++) {
         CompoundTag e = list.m_128728_(i);

         try {
            UUID uuid = e.m_128342_("uuid");
            data.radius.put(uuid, new int[]{e.m_128451_("min"), e.m_128451_("max")});
            data.modes.put(uuid, e.m_128451_("mode"));
            ListTag bl = e.m_128437_("biomes", 8);
            Set<ResourceLocation> set = new HashSet<>();

            for (int j = 0; j < bl.size(); j++) {
               ResourceLocation id = ResourceLocation.m_135820_(bl.m_128778_(j));
               if (id != null) {
                  set.add(id);
               }
            }

            if (!set.isEmpty()) {
               data.allowedBiomes.put(uuid, set);
            }

            ListTag sl = e.m_128437_("structures", 8);
            Set<ResourceLocation> sset = new HashSet<>();

            for (int jx = 0; jx < sl.size(); jx++) {
               ResourceLocation id = ResourceLocation.m_135820_(sl.m_128778_(jx));
               if (id != null) {
                  sset.add(id);
               }
            }

            if (!sset.isEmpty()) {
               data.allowedStructures.put(uuid, sset);
            }
         } catch (Exception var12) {
         }
      }

      return data;
   }

   public CompoundTag m_7176_(CompoundTag tag) {
      ListTag list = new ListTag();
      Set<UUID> uuids = new HashSet<>();
      uuids.addAll(this.radius.keySet());
      uuids.addAll(this.allowedBiomes.keySet());
      uuids.addAll(this.allowedStructures.keySet());
      uuids.addAll(this.modes.keySet());

      for (UUID uuid : uuids) {
         CompoundTag e = new CompoundTag();
         e.m_128362_("uuid", uuid);
         int[] r = this.radius.getOrDefault(uuid, new int[]{200, 5000});
         e.m_128405_("min", r[0]);
         e.m_128405_("max", r[1]);
         e.m_128405_("mode", this.modes.getOrDefault(uuid, 0));
         Set<ResourceLocation> biomes = this.allowedBiomes.get(uuid);
         if (biomes != null && !biomes.isEmpty()) {
            ListTag bl = new ListTag();

            for (ResourceLocation id : biomes) {
               bl.add(StringTag.m_129297_(id.toString()));
            }

            e.m_128365_("biomes", bl);
         }

         Set<ResourceLocation> structures = this.allowedStructures.get(uuid);
         if (structures != null && !structures.isEmpty()) {
            ListTag sl = new ListTag();

            for (ResourceLocation id : structures) {
               sl.add(StringTag.m_129297_(id.toString()));
            }

            e.m_128365_("structures", sl);
         }

         list.add(e);
      }

      tag.m_128365_("entries", list);
      return tag;
   }

   public int[] getRadius(UUID uuid) {
      int[] r = this.radius.get(uuid);
      return r == null ? new int[]{200, 5000} : new int[]{r[0], r[1]};
   }

   public void setRadius(UUID uuid, int min, int max) {
      this.radius.put(uuid, new int[]{min, max});
      this.m_77762_();
   }

   public Set<ResourceLocation> getAllowedBiomes(UUID uuid) {
      Set<ResourceLocation> s = this.allowedBiomes.get(uuid);
      return s == null ? Collections.emptySet() : Collections.unmodifiableSet(s);
   }

   public void setAllowedBiomes(UUID uuid, Set<ResourceLocation> biomes) {
      if (biomes != null && !biomes.isEmpty()) {
         this.allowedBiomes.put(uuid, new HashSet<>(biomes));
      } else {
         this.allowedBiomes.remove(uuid);
      }

      this.m_77762_();
   }

   public Set<ResourceLocation> getAllowedStructures(UUID uuid) {
      Set<ResourceLocation> s = this.allowedStructures.get(uuid);
      return s == null ? Collections.emptySet() : Collections.unmodifiableSet(s);
   }

   public void setAllowedStructures(UUID uuid, Set<ResourceLocation> structures) {
      if (structures != null && !structures.isEmpty()) {
         this.allowedStructures.put(uuid, new HashSet<>(structures));
      } else {
         this.allowedStructures.remove(uuid);
      }

      this.m_77762_();
   }

   public int getMode(UUID uuid) {
      return this.modes.getOrDefault(uuid, 0);
   }

   public void setMode(UUID uuid, int mode) {
      this.modes.put(uuid, mode);
      this.m_77762_();
   }

   public void remove(UUID uuid) {
      boolean changed = this.radius.remove(uuid) != null;
      changed |= this.allowedBiomes.remove(uuid) != null;
      changed |= this.allowedStructures.remove(uuid) != null;
      changed |= this.modes.remove(uuid) != null;
      if (changed) {
         this.m_77762_();
      }
   }
}
