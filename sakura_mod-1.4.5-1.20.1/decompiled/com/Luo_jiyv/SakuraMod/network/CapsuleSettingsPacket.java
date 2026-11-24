package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleData;
import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleStat;
import com.Luo_jiyv.SakuraMod.item.Moditems;
import io.netty.handler.codec.DecoderException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class CapsuleSettingsPacket {
   private static final int MAX_STATS = 64;
   private final int handOrdinal;
   private final Map<CapsuleStat, Double> values;
   private final Map<CapsuleStat, Boolean> modes;

   public CapsuleSettingsPacket(InteractionHand hand, Map<CapsuleStat, Double> values, Map<CapsuleStat, Boolean> modes) {
      this.handOrdinal = hand.ordinal();
      this.values = new EnumMap<>(CapsuleStat.class);
      this.values.putAll(values);
      this.modes = new EnumMap<>(CapsuleStat.class);
      this.modes.putAll(modes);
   }

   private CapsuleSettingsPacket(int handOrdinal, Map<CapsuleStat, Double> values, Map<CapsuleStat, Boolean> modes) {
      this.handOrdinal = handOrdinal;
      this.values = values;
      this.modes = modes;
   }

   public static void encode(CapsuleSettingsPacket p, FriendlyByteBuf buf) {
      buf.writeByte(p.handOrdinal);
      buf.m_130130_(p.values.size());

      for (Entry<CapsuleStat, Double> entry : p.values.entrySet()) {
         buf.writeByte(entry.getKey().ordinal());
         buf.writeDouble(entry.getValue() == null ? 0.0 : entry.getValue());
         buf.writeBoolean(Boolean.TRUE.equals(p.modes.get(entry.getKey())));
      }
   }

   public static CapsuleSettingsPacket decode(FriendlyByteBuf buf) {
      int hand = buf.readByte();
      int count = buf.m_130242_();
      if (count >= 0 && count <= 64) {
         Map<CapsuleStat, Double> values = new EnumMap<>(CapsuleStat.class);
         Map<CapsuleStat, Boolean> modes = new EnumMap<>(CapsuleStat.class);
         CapsuleStat[] all = CapsuleStat.values();

         for (int i = 0; i < count; i++) {
            int ordinal = buf.readUnsignedByte();
            if (ordinal >= all.length) {
               throw new DecoderException("CapsuleSettingsPacket: bad stat ordinal " + ordinal);
            }

            CapsuleStat stat = all[ordinal];
            values.put(stat, buf.readDouble());
            modes.put(stat, buf.readBoolean());
         }

         return new CapsuleSettingsPacket(hand, values, modes);
      } else {
         throw new DecoderException("CapsuleSettingsPacket: bad stat count " + count);
      }
   }

   public static void handle(CapsuleSettingsPacket p, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> {
         ServerPlayer player = ctx.getSender();
         if (player != null) {
            InteractionHand hand = InteractionHand.values()[Math.max(0, Math.min(InteractionHand.values().length - 1, p.handOrdinal))];
            ItemStack stack = player.m_21120_(hand);
            if (stack.m_41720_() == Moditems.ARCHITE_CAPSULE.get()) {
               for (Entry<CapsuleStat, Double> entry : p.values.entrySet()) {
                  if (entry.getValue() != null) {
                     CapsuleData.setCapsuleValue(stack, entry.getKey(), entry.getValue());
                     CapsuleData.setCapsuleFlat(stack, entry.getKey(), Boolean.TRUE.equals(p.modes.get(entry.getKey())));
                  }
               }

               player.m_150109_().m_6596_();
            }
         }
      });
      ctx.setPacketHandled(true);
   }
}
