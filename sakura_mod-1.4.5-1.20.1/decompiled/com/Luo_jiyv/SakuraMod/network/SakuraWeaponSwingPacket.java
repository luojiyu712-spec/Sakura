package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.item.SakuraAxeItem;
import com.Luo_jiyv.SakuraMod.item.SakuraSwordItem;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;

public record SakuraWeaponSwingPacket() {
   public static void encode(SakuraWeaponSwingPacket packet, FriendlyByteBuf buffer) {
   }

   public static SakuraWeaponSwingPacket decode(FriendlyByteBuf buffer) {
      return new SakuraWeaponSwingPacket();
   }

   public static void handle(SakuraWeaponSwingPacket packet, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      ServerPlayer player = context.getSender();
      if (player != null) {
         if (player.m_21205_().m_41720_() instanceof SakuraAxeItem) {
            SakuraAxeItem.launchSlash(player, null);
         } else {
            SakuraSwordItem.sweep(player, null);
         }
      }

      context.setPacketHandled(true);
   }
}
