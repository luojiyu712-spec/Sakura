package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.SakuraAxeSlashParticle;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

public record SakuraAxeSlashPacket(ResourceLocation dimension, UUID id, Vec3 position, float pitch, float yaw, boolean stopped) {
   public static void encode(SakuraAxeSlashPacket packet, FriendlyByteBuf buf) {
      buf.m_130085_(packet.dimension);
      buf.m_130077_(packet.id);
      buf.writeBoolean(packet.stopped);
      if (!packet.stopped) {
         buf.writeDouble(packet.position.f_82479_);
         buf.writeDouble(packet.position.f_82480_);
         buf.writeDouble(packet.position.f_82481_);
         buf.writeFloat(packet.pitch);
         buf.writeFloat(packet.yaw);
      }
   }

   public static SakuraAxeSlashPacket decode(FriendlyByteBuf buf) {
      ResourceLocation dimension = buf.m_130281_();
      UUID id = buf.m_130259_();
      boolean stopped = buf.readBoolean();
      return stopped
         ? new SakuraAxeSlashPacket(dimension, id, Vec3.f_82478_, 0.0F, 0.0F, true)
         : new SakuraAxeSlashPacket(dimension, id, new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readFloat(), buf.readFloat(), false);
   }

   public static void handle(SakuraAxeSlashPacket packet, Supplier<Context> context) {
      SakuraAxeSlashParticle.handle(packet);
      context.get().setPacketHandled(true);
   }
}
