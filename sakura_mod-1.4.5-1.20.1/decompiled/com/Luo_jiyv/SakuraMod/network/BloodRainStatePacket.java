package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.BloodRainClientState;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public record BloodRainStatePacket(BloodRainStatePacket.Phase phase) {
   public static void encode(BloodRainStatePacket packet, FriendlyByteBuf buf) {
      buf.m_130068_(packet.phase);
   }

   public static BloodRainStatePacket decode(FriendlyByteBuf buf) {
      return new BloodRainStatePacket((BloodRainStatePacket.Phase)buf.m_130066_(BloodRainStatePacket.Phase.class));
   }

   public static void handle(BloodRainStatePacket packet, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> {
         if (ctx.getDirection().getReceptionSide().isClient()) {
            BloodRainClientState.setPhase(packet.phase);
         }
      });
      ctx.setPacketHandled(true);
   }

   public static enum Phase {
      INACTIVE,
      ACTIVE,
      CLEARING;
   }
}
