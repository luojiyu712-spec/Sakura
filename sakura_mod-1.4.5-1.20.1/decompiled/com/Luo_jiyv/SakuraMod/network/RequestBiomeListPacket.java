package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.util.RequestThrottle;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent.Context;

public class RequestBiomeListPacket {
   public static void encode(RequestBiomeListPacket p, FriendlyByteBuf buf) {
   }

   public static RequestBiomeListPacket decode(FriendlyByteBuf buf) {
      return new RequestBiomeListPacket();
   }

   public static void handle(RequestBiomeListPacket p, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(
         () -> {
            ServerPlayer sender = ctx.getSender();
            if (sender != null) {
               MinecraftServer server = sender.m_20194_();
               if (server != null) {
                  if (RequestThrottle.allow(sender, sender.m_284548_().m_46467_())) {
                     ServerLevel level = sender.m_284548_();
                     BiomeSource biomeSource = level.m_7726_().m_8481_().m_62218_();
                     List<ResourceLocation> list = biomeSource.m_207840_()
                        .stream()
                        .map(h -> h.m_203543_().map(k -> k.m_135782_()).orElse(null))
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted(Comparator.comparing(ResourceLocation::toString))
                        .toList();
                     ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender), new BiomeListPacket(list));
                  }
               }
            }
         }
      );
      ctx.setPacketHandled(true);
   }
}
