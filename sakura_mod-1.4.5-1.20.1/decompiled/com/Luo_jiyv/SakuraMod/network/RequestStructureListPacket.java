package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.util.RandomTeleporterSettings;
import com.Luo_jiyv.SakuraMod.util.RequestThrottle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent.Context;

public class RequestStructureListPacket {
   public static void encode(RequestStructureListPacket p, FriendlyByteBuf buf) {
   }

   public static RequestStructureListPacket decode(FriendlyByteBuf buf) {
      return new RequestStructureListPacket();
   }

   public static void handle(RequestStructureListPacket p, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(
         () -> {
            ServerPlayer sender = ctx.getSender();
            if (sender != null) {
               MinecraftServer server = sender.m_20194_();
               if (server != null) {
                  if (RequestThrottle.allow(sender, sender.m_284548_().m_46467_())) {
                     ServerLevel level = sender.m_284548_();
                     Registry<Structure> registry = level.m_9598_().m_175515_(Registries.f_256944_);
                     Set<ResourceKey<Biome>> dimBiomes = new HashSet<>();
                     BiomeSource biomeSource = level.m_7726_().m_8481_().m_62218_();

                     for (Holder<Biome> h : biomeSource.m_207840_()) {
                        h.m_203543_().ifPresent(dimBiomes::add);
                     }

                     List<ResourceLocation> list = registry.m_203611_()
                        .filter(holder -> {
                           for (Holder<Biome> b : ((Structure)holder.m_203334_()).m_226559_()) {
                              ResourceKey<Biome> key = (ResourceKey<Biome>)b.m_203543_().orElse(null);
                              if (key != null && dimBiomes.contains(key)) {
                                 return true;
                              }
                           }

                           return false;
                        })
                        .map(h -> h.m_203543_().map(ResourceKey::m_135782_).orElse(null))
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(ResourceLocation::toString))
                        .toList();
                     ArrayList<ResourceLocation> allowed = new ArrayList<>(RandomTeleporterSettings.getAllowedStructures(server, sender.m_20148_()));
                     ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender), new StructureListPacket(list, allowed));
                  }
               }
            }
         }
      );
      ctx.setPacketHandled(true);
   }
}
