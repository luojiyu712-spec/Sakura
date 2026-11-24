package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.ShieldClientTracker;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkEvent.Context;

public record ShieldStateSyncPacket(int capacity, int energy, long recoveryStart) {
   public static void encode(ShieldStateSyncPacket p, FriendlyByteBuf buf) {
      buf.m_130130_(p.capacity);
      buf.m_130130_(p.energy);
      buf.writeLong(p.recoveryStart);
   }

   public static ShieldStateSyncPacket decode(FriendlyByteBuf buf) {
      return new ShieldStateSyncPacket(buf.m_130242_(), buf.m_130242_(), buf.readLong());
   }

   public static void handle(ShieldStateSyncPacket p, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> {
         if (ctx.getDirection().getReceptionSide().isClient() && FMLLoader.getDist().isClient()) {
            ShieldClientTracker.onServerSync(p);
         }
      });
      ctx.setPacketHandled(true);
   }
}
