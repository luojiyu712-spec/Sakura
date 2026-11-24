package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.ClientFlightTimeCache;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class FlightTimeSyncPacket {
   private final int flightTime;

   public FlightTimeSyncPacket(int flightTime) {
      this.flightTime = flightTime;
   }

   public static void encode(FlightTimeSyncPacket p, FriendlyByteBuf buf) {
      buf.writeInt(p.flightTime);
   }

   public static FlightTimeSyncPacket decode(FriendlyByteBuf buf) {
      return new FlightTimeSyncPacket(buf.readInt());
   }

   public static void handle(FlightTimeSyncPacket p, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientFlightTimeCache.set(p.flightTime)));
      ctx.setPacketHandled(true);
   }
}
