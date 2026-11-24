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

public class StructureListPacket {
   private final List<ResourceLocation> structures;
   private final List<ResourceLocation> allowedStructures;
   public static final int MAX_ENTRIES = 8192;

   public StructureListPacket(List<ResourceLocation> structures, List<ResourceLocation> allowedStructures) {
      this.structures = structures;
      this.allowedStructures = allowedStructures != null ? allowedStructures : List.of();
   }

   public static void encode(StructureListPacket p, FriendlyByteBuf buf) {
      buf.m_130130_(p.structures.size());

      for (ResourceLocation id : p.structures) {
         buf.m_130085_(id);
      }

      buf.m_130130_(p.allowedStructures.size());

      for (ResourceLocation id : p.allowedStructures) {
         buf.m_130085_(id);
      }
   }

   public static StructureListPacket decode(FriendlyByteBuf buf) {
      int size = buf.m_130242_();
      if (size >= 0 && size <= 8192) {
         List<ResourceLocation> list = new ArrayList<>(size);

         for (int i = 0; i < size; i++) {
            list.add(buf.m_130281_());
         }

         int asize = buf.m_130242_();
         if (asize >= 0 && asize <= 8192) {
            List<ResourceLocation> allowed = new ArrayList<>(asize);

            for (int i = 0; i < asize; i++) {
               allowed.add(buf.m_130281_());
            }

            return new StructureListPacket(list, allowed);
         } else {
            throw new DecoderException("StructureListPacket: bad allowed size " + asize);
         }
      } else {
         throw new DecoderException("StructureListPacket: bad size " + size);
      }
   }

   public static void handle(StructureListPacket p, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> {
         if (ctx.getDirection().getReceptionSide().isClient()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlers.handleStructureList(p));
         }
      });
      ctx.setPacketHandled(true);
   }

   public List<ResourceLocation> getStructures() {
      return this.structures;
   }

   public List<ResourceLocation> getAllowedStructures() {
      return this.allowedStructures;
   }
}
