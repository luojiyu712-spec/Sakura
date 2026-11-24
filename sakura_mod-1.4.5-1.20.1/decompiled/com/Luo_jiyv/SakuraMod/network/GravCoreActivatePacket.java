package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.item.GravCoreItem;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class GravCoreActivatePacket {
   public static void encode(GravCoreActivatePacket packet, FriendlyByteBuf buf) {
   }

   public static GravCoreActivatePacket decode(FriendlyByteBuf buf) {
      return new GravCoreActivatePacket();
   }

   public static void handle(GravCoreActivatePacket packet, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> {
         ServerPlayer player = ctx.get().getSender();
         if (player != null) {
            GravCoreItem.tryActivateFromCurio(player);
         }
      });
      ctx.get().setPacketHandled(true);
   }
}
