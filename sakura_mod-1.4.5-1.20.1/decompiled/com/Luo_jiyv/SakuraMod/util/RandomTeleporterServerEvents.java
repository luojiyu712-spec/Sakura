package com.Luo_jiyv.SakuraMod.util;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.network.RandomTeleporterSettingsPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class RandomTeleporterServerEvents {
   @SubscribeEvent
   public static void onLogin(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         MinecraftServer server = player.m_20194_();
         if (server != null) {
            int[] s = RandomTeleporterSettings.get(server, player.m_20148_());
            int mode = RandomTeleporterSettings.getMode(server, player.m_20148_());
            ArrayList<ResourceLocation> biomes = new ArrayList<>(RandomTeleporterSettings.getAllowedBiomes(server, player.m_20148_()));
            ArrayList<ResourceLocation> structures = new ArrayList<>(RandomTeleporterSettings.getAllowedStructures(server, player.m_20148_()));
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new RandomTeleporterSettingsPacket(s[0], s[1], biomes, mode, structures));
         }
      }
   }

   @SubscribeEvent
   public static void onChangeDimension(PlayerChangedDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         RandomTeleporterSearch.cancel(player.m_20148_());
         MinecraftServer server = player.m_20194_();
         if (server != null) {
            RandomTeleporterSettings.setAllowedBiomes(server, player.m_20148_(), Set.of());
            RandomTeleporterSettings.setAllowedStructures(server, player.m_20148_(), Set.of());
            server.execute(
               () -> {
                  if (!player.m_213877_()) {
                     int[] s = RandomTeleporterSettings.get(server, player.m_20148_());
                     int mode = RandomTeleporterSettings.getMode(server, player.m_20148_());
                     ModNetwork.CHANNEL
                        .send(PacketDistributor.PLAYER.with(() -> player), new RandomTeleporterSettingsPacket(s[0], s[1], List.of(), mode, List.of()));
                  }
               }
            );
         }
      }
   }

   @SubscribeEvent
   public static void onServerStopping(ServerStoppingEvent event) {
      RandomTeleporterSearch.clear();
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      RandomTeleporterSearch.clear();
   }

   @SubscribeEvent
   public static void onLogout(PlayerLoggedOutEvent event) {
      try {
         if (event.getEntity() instanceof ServerPlayer player) {
            RandomTeleporterSearch.cancel(player.m_20148_());
            pruneDefaultSettings(player);
         }
      } catch (Throwable var3) {
         SakuraMod.LOGGER.warn("Failed to clear random teleporter queue for {}: {}", event.getEntity().m_20148_(), var3.toString());
      }
   }

   private static void pruneDefaultSettings(ServerPlayer player) {
      MinecraftServer server = player.m_20194_();
      if (server != null) {
         UUID id = player.m_20148_();
         int[] radius = RandomTeleporterSettings.get(server, id);
         boolean defaultRadius = radius[0] == 200 && radius[1] == 5000;
         boolean defaultMode = RandomTeleporterSettings.getMode(server, id) == 0;
         boolean noFilters = RandomTeleporterSettings.getAllowedBiomes(server, id).isEmpty()
            && RandomTeleporterSettings.getAllowedStructures(server, id).isEmpty();
         if (defaultRadius && defaultMode && noFilters) {
            RandomTeleporterSettings.remove(server, id);
         }
      }
   }
}
