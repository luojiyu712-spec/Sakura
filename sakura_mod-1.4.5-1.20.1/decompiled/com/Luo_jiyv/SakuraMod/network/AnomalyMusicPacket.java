package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.music.MusicPlayerClient;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class AnomalyMusicPacket {
   public static void encode(AnomalyMusicPacket packet, FriendlyByteBuf buf) {
   }

   public static AnomalyMusicPacket decode(FriendlyByteBuf buf) {
      return new AnomalyMusicPacket();
   }

   public static void handle(AnomalyMusicPacket packet, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> {
         if (ctx.getDirection().getReceptionSide().isClient()) {
            MusicPlayerClient.requestAdvancementMusic();
         }
      });
      ctx.setPacketHandled(true);
   }
}
