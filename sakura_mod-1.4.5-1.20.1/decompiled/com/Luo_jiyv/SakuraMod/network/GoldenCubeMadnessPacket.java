package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.GoldenCubeClientEffects;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkEvent.Context;

public class GoldenCubeMadnessPacket {
   private final int mode;

   public GoldenCubeMadnessPacket(int mode) {
      this.mode = mode;
   }

   public static void encode(GoldenCubeMadnessPacket p, FriendlyByteBuf buf) {
      buf.writeByte(p.mode);
   }

   public static GoldenCubeMadnessPacket decode(FriendlyByteBuf buf) {
      return new GoldenCubeMadnessPacket(buf.readUnsignedByte());
   }

   public static void handle(GoldenCubeMadnessPacket p, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> {
         if (ctx.getDirection().getReceptionSide().isClient() && FMLLoader.getDist().isClient()) {
            GoldenCubeClientEffects.setMode(p.mode);
         }
      });
      ctx.setPacketHandled(true);
   }
}
