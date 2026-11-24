package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleData;
import com.Luo_jiyv.SakuraMod.item.Moditems;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class CapsuleInstallPacket {
   private final int slotIndex;
   private final boolean install;

   public CapsuleInstallPacket(int slotIndex, boolean install) {
      this.slotIndex = slotIndex;
      this.install = install;
   }

   public static void encode(CapsuleInstallPacket p, FriendlyByteBuf buf) {
      buf.m_130130_(p.slotIndex);
      buf.writeBoolean(p.install);
   }

   public static CapsuleInstallPacket decode(FriendlyByteBuf buf) {
      return new CapsuleInstallPacket(buf.m_130242_(), buf.readBoolean());
   }

   public static void handle(CapsuleInstallPacket p, Supplier<Context> ctxSup) {
      Context ctx = ctxSup.get();
      ctx.enqueueWork(
         () -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) {
               AbstractContainerMenu menu = player.f_36096_;
               if (p.slotIndex >= 0 && p.slotIndex < menu.f_38839_.size()) {
                  Slot slot = (Slot)menu.f_38839_.get(p.slotIndex);
                  ItemStack gun = slot.m_7993_();
                  if (CapsuleData.isTaczGun(gun)) {
                     if (p.install) {
                        if (CapsuleData.hasCapsule(gun)) {
                           return;
                        }

                        ItemStack carried = menu.m_142621_();
                        if (carried.m_41619_() || carried.m_41720_() != Moditems.ARCHITE_CAPSULE.get()) {
                           return;
                        }

                        CapsuleData.install(gun, carried);
                        ItemStack rest = carried.m_41777_();
                        rest.m_41774_(1);
                        menu.m_142503_(rest.m_41619_() ? ItemStack.f_41583_ : rest);
                     } else {
                        if (!CapsuleData.hasCapsule(gun)) {
                           return;
                        }

                        if (!menu.m_142621_().m_41619_()) {
                           return;
                        }

                        ItemStack capsule = CapsuleData.createCapsuleFromGun(gun);
                        CapsuleData.uninstall(gun);
                        if (!player.m_150109_().m_36054_(capsule)) {
                           player.m_36176_(capsule, false);
                        }
                     }

                     player.m_5661_(
                        Component.m_237115_(p.install ? "message.sakura_mod.capsule.installed" : "message.sakura_mod.capsule.removed")
                           .m_130940_(ChatFormatting.GOLD),
                        true
                     );
                     slot.m_6654_();
                     menu.m_38946_();
                     player.m_150109_().m_6596_();
                  }
               }
            }
         }
      );
      ctx.setPacketHandled(true);
   }
}
