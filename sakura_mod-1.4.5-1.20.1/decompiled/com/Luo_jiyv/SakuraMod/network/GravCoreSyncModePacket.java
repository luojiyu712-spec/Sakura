package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.ClientGravCoreModeCache;
import com.Luo_jiyv.SakuraMod.item.GravCoreItem;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class GravCoreSyncModePacket {
   private final String mode;

   public GravCoreSyncModePacket(String mode) {
      this.mode = mode;
   }

   public static void encode(GravCoreSyncModePacket p, FriendlyByteBuf buf) {
      buf.m_130070_(p.mode);
   }

   public static GravCoreSyncModePacket decode(FriendlyByteBuf buf) {
      return new GravCoreSyncModePacket(buf.m_130277_());
   }

   public static void handle(GravCoreSyncModePacket p, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientGravCoreModeCache.setMode(GravCoreItem.Mode.fromString(p.mode))));
      ctx.get().setPacketHandled(true);
   }
}
