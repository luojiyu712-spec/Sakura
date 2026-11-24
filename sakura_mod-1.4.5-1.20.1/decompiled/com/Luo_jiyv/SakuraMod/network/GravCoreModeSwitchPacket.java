package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.item.GravCoreItem;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class GravCoreModeSwitchPacket {
   public static void encode(GravCoreModeSwitchPacket packet, FriendlyByteBuf buf) {
   }

   public static GravCoreModeSwitchPacket decode(FriendlyByteBuf buf) {
      return new GravCoreModeSwitchPacket();
   }

   public static void handle(GravCoreModeSwitchPacket packet, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> {
         try {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
               return;
            }

            GravCoreItem.toggleModeAndNotify(player);
         } catch (Throwable var2) {
            SakuraMod.LOGGER.error("[SakuraMod] GravCoreModeSwitchPacket.handle 失败", var2);
         }
      });
      ctx.get().setPacketHandled(true);
   }
}
