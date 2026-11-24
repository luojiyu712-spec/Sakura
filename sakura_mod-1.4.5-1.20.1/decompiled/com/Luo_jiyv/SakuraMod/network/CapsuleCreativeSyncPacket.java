package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleData;
import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleStat;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class CapsuleCreativeSyncPacket {
   private final int slotIndex;
   private final boolean install;
   private final CompoundTag stats;
   private final String gunId;

   public CapsuleCreativeSyncPacket(int slotIndex, boolean install, CompoundTag stats, String gunId) {
      this.slotIndex = slotIndex;
      this.install = install;
      this.stats = stats == null ? new CompoundTag() : stats;
      this.gunId = gunId == null ? "" : gunId;
   }

   public static void encode(CapsuleCreativeSyncPacket p, FriendlyByteBuf buf) {
      buf.m_130130_(p.slotIndex);
      buf.writeBoolean(p.install);
      buf.m_130079_(p.stats);
      buf.m_130070_(p.gunId);
   }

   public static CapsuleCreativeSyncPacket decode(FriendlyByteBuf buf) {
      return new CapsuleCreativeSyncPacket(buf.m_130242_(), buf.readBoolean(), buf.m_130260_(), buf.m_130277_());
   }

   public static void handle(CapsuleCreativeSyncPacket p, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(() -> {
         ServerPlayer player = ctx.getSender();
         if (player != null && player.m_7500_()) {
            if (p.slotIndex >= 0 && p.slotIndex < player.m_150109_().m_6643_()) {
               ItemStack gun = player.m_150109_().m_8020_(p.slotIndex);
               if (CapsuleData.isTaczGun(gun) && p.gunId.equals(gunId(gun))) {
                  if (p.install) {
                     for (CapsuleStat stat : CapsuleStat.values()) {
                        if (p.stats.m_128441_(stat.key())) {
                           CapsuleData.setCapsuleValue(gun, stat, p.stats.m_128459_(stat.key()));
                           CapsuleData.setCapsuleFlat(gun, stat, p.stats.m_128471_(stat.flatKey()));
                        }
                     }

                     gun.m_41784_().m_128379_("SakuraCapsuleInstalled", true);
                  } else {
                     CapsuleData.uninstall(gun);
                  }

                  CapsuleData.refreshTaczCache(player, gun);
                  player.f_36096_.m_38946_();
                  player.m_150109_().m_6596_();
               }
            }
         }
      });
      ctx.setPacketHandled(true);
   }

   private static String gunId(ItemStack gun) {
      return gun.m_41782_() ? gun.m_41783_().m_128461_("GunId") : "";
   }
}
