package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.RandomTeleporterParticle;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.PacketDistributor.TargetPoint;

public record RandomTeleporterEffectPacket(ResourceLocation dimension, int playerId, RandomTeleporterEffectPacket.Phase phase, Vec3 position) {
   public static final int PREVIEW_TICKS = 13;

   public static void sendTracking(ServerPlayer player, RandomTeleporterEffectPacket.Phase phase, Vec3 position) {
      if (player.f_8906_ != null) {
         RandomTeleporterEffectPacket packet = new RandomTeleporterEffectPacket(player.m_9236_().m_46472_().m_135782_(), player.m_19879_(), phase, position);
         ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), packet);
      }
   }

   public static void sendNear(ServerPlayer player, RandomTeleporterEffectPacket.Phase phase, Vec3 position) {
      if (player.f_8906_ != null) {
         ResourceKey<Level> dimension = player.m_9236_().m_46472_();
         RandomTeleporterEffectPacket packet = new RandomTeleporterEffectPacket(dimension.m_135782_(), player.m_19879_(), phase, position);
         ModNetwork.CHANNEL
            .send(PacketDistributor.NEAR.with(() -> new TargetPoint(position.f_82479_, position.f_82480_, position.f_82481_, 64.0, dimension)), packet);
      }
   }

   static void encode(RandomTeleporterEffectPacket packet, FriendlyByteBuf buf) {
      buf.m_130085_(packet.dimension);
      buf.m_130130_(packet.playerId);
      buf.m_130068_(packet.phase);
      buf.writeDouble(packet.position.f_82479_);
      buf.writeDouble(packet.position.f_82480_);
      buf.writeDouble(packet.position.f_82481_);
   }

   static RandomTeleporterEffectPacket decode(FriendlyByteBuf buf) {
      return new RandomTeleporterEffectPacket(
         buf.m_130281_(),
         buf.m_130242_(),
         (RandomTeleporterEffectPacket.Phase)buf.m_130066_(RandomTeleporterEffectPacket.Phase.class),
         new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
      );
   }

   static void handle(RandomTeleporterEffectPacket packet, Supplier<Context> context) {
      RandomTeleporterParticle.handle(packet);
      context.get().setPacketHandled(true);
   }

   public static enum Phase {
      SEARCH,
      SHATTER,
      DEPARTURE,
      ARRIVAL,
      PREVIEW;
   }
}
