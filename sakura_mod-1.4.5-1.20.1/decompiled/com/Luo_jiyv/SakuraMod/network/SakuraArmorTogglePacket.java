package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.event.SakuraArmorEvents;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public class SakuraArmorTogglePacket {
   private final boolean toggleRevive;
   private final boolean toggleAutoRepair;

   public SakuraArmorTogglePacket(boolean toggleRevive, boolean toggleAutoRepair) {
      this.toggleRevive = toggleRevive;
      this.toggleAutoRepair = toggleAutoRepair;
   }

   public static void encode(SakuraArmorTogglePacket msg, FriendlyByteBuf buf) {
      buf.writeBoolean(msg.toggleRevive);
      buf.writeBoolean(msg.toggleAutoRepair);
   }

   public static SakuraArmorTogglePacket decode(FriendlyByteBuf buf) {
      return new SakuraArmorTogglePacket(buf.readBoolean(), buf.readBoolean());
   }

   public static void handle(SakuraArmorTogglePacket msg, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> {
         ServerPlayer player = ctx.get().getSender();
         if (player != null) {
            SakuraArmorEvents.handleTogglePacket(player, msg.toggleRevive, msg.toggleAutoRepair);
         }
      });
      ctx.get().setPacketHandled(true);
   }
}
