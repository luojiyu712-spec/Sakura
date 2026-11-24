package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.client.ClientPacketHandlers;
import io.netty.handler.codec.DecoderException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class BiomeListPacket {
   private final List<ResourceLocation> biomes;
   public static final int MAX_ENTRIES = 4096;

   public BiomeListPacket(List<ResourceLocation> biomes) {
      this.biomes = biomes;
   }

   public static void encode(BiomeListPacket p, FriendlyByteBuf buf) {
      buf.m_130130_(p.biomes.size());

      for (ResourceLocation id : p.biomes) {
         buf.m_130085_(id);
      }
   }

   public static BiomeListPacket decode(FriendlyByteBuf buf) {
      int size = buf.m_130242_();
      if (size >= 0 && size <= 4096) {
         List<ResourceLocation> list = new ArrayList<>(size);

         for (int i = 0; i < size; i++) {
            list.add(buf.m_130281_());
         }

         return new BiomeListPacket(list);
      } else {
         throw new DecoderException("BiomeListPacket: bad size " + size);
      }
   }

   public static void handle(BiomeListPacket p, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> {
         if (ctx.getDirection().getReceptionSide().isClient()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleBiomeList(p));
         }
      });
      ctx.setPacketHandled(true);
   }

   public List<ResourceLocation> getBiomes() {
      return this.biomes;
   }
}
