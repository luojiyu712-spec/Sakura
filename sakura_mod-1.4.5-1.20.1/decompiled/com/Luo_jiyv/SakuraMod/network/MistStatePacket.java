package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.MistClientState;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class MistStatePacket {
   private final boolean active;

   public MistStatePacket(boolean active) {
      this.active = active;
   }

   public static void encode(MistStatePacket packet, FriendlyByteBuf buf) {
      buf.writeBoolean(packet.active);
   }

   public static MistStatePacket decode(FriendlyByteBuf buf) {
      return new MistStatePacket(buf.readBoolean());
   }

   public static void handle(MistStatePacket packet, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> {
         if (ctx.getDirection().getReceptionSide().isClient()) {
            MistClientState.setActive(packet.active);
         }
      });
      ctx.setPacketHandled(true);
   }
}
