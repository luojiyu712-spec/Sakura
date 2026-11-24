package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.ClientRandomTeleporterSettings;
import com.Luo_jiyv.SakuraMod.util.RandomTeleporterSettings;
import io.netty.handler.codec.DecoderException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class RandomTeleporterSettingsPacket {
   private final int minRadius;
   private final int maxRadius;
   private final List<ResourceLocation> biomes;
   private final int mode;
   private final List<ResourceLocation> structures;
   public static final int MAX_ENTRIES = 8192;

   public RandomTeleporterSettingsPacket(int minRadius, int maxRadius, List<ResourceLocation> biomes) {
      this(minRadius, maxRadius, biomes, 0);
   }

   public RandomTeleporterSettingsPacket(int minRadius, int maxRadius, List<ResourceLocation> biomes, int mode) {
      this(minRadius, maxRadius, biomes, mode, List.of());
   }

   public RandomTeleporterSettingsPacket(int minRadius, int maxRadius, List<ResourceLocation> biomes, int mode, List<ResourceLocation> structures) {
      this.minRadius = minRadius;
      this.maxRadius = maxRadius;
      this.biomes = biomes != null ? biomes : List.of();
      this.mode = mode;
      this.structures = structures != null ? structures : List.of();
   }

   public static void encode(RandomTeleporterSettingsPacket p, FriendlyByteBuf buf) {
      buf.writeInt(p.minRadius);
      buf.writeInt(p.maxRadius);
      buf.m_130130_(p.mode);
      buf.m_130130_(p.biomes.size());

      for (ResourceLocation id : p.biomes) {
         buf.m_130085_(id);
      }

      buf.m_130130_(p.structures.size());

      for (ResourceLocation id : p.structures) {
         buf.m_130085_(id);
      }
   }

   public static RandomTeleporterSettingsPacket decode(FriendlyByteBuf buf) {
      int min = buf.readInt();
      int max = buf.readInt();
      int mode = buf.m_130242_();
      int size = buf.m_130242_();
      if (size >= 0 && size <= 8192) {
         List<ResourceLocation> biomes = new ArrayList<>(size);

         for (int i = 0; i < size; i++) {
            biomes.add(buf.m_130281_());
         }

         int ssize = buf.m_130242_();
         if (ssize >= 0 && ssize <= 8192) {
            List<ResourceLocation> structures = new ArrayList<>(ssize);

            for (int i = 0; i < ssize; i++) {
               structures.add(buf.m_130281_());
            }

            return new RandomTeleporterSettingsPacket(min, max, biomes, mode, structures);
         } else {
            throw new DecoderException("RandomTeleporterSettingsPacket: bad structure size " + ssize);
         }
      } else {
         throw new DecoderException("RandomTeleporterSettingsPacket: bad biome size " + size);
      }
   }

   public static void handle(RandomTeleporterSettingsPacket p, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> {
         if (ctx.getDirection().getReceptionSide().isServer()) {
            ServerPlayer sender = ctx.getSender();
            if (sender != null) {
               MinecraftServer server = sender.m_20194_();
               if (server != null) {
                  RandomTeleporterSettings.set(server, sender.m_20148_(), p.minRadius, p.maxRadius);
                  RandomTeleporterSettings.setAllowedBiomes(server, sender.m_20148_(), new HashSet<>(p.biomes));
                  RandomTeleporterSettings.setMode(server, sender.m_20148_(), p.mode);
                  RandomTeleporterSettings.setAllowedStructures(server, sender.m_20148_(), new HashSet<>(p.structures));
               }
            }
         } else {
            ClientRandomTeleporterSettings.set(p.minRadius, p.maxRadius);
            ClientRandomTeleporterSettings.setBiomes(new HashSet<>(p.biomes));
            ClientRandomTeleporterSettings.setMode(p.mode);
            ClientRandomTeleporterSettings.setStructures(new HashSet<>(p.structures));
         }
      });
      ctx.setPacketHandled(true);
   }
}
