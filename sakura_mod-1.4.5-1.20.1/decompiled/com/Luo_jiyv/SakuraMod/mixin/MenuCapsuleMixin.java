package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleData;
import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleStat;
import com.Luo_jiyv.SakuraMod.item.Moditems;
import com.Luo_jiyv.SakuraMod.network.CapsuleCreativeSyncPacket;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractContainerMenu.class})
public class MenuCapsuleMixin {
   @Inject(
      method = {"clicked"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sakura$capsuleClick(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
      if (player != null) {
         if (button == 1 && clickType == ClickType.PICKUP) {
            AbstractContainerMenu self = (AbstractContainerMenu)this;
            if (slotId >= 0 && slotId < self.f_38839_.size()) {
               Slot slot = (Slot)self.f_38839_.get(slotId);
               ItemStack gun = slot.m_7993_();
               if (CapsuleData.isTaczGun(gun)) {
                  ItemStack carried = self.m_142621_();
                  boolean install = !carried.m_41619_() && carried.m_41720_() == Moditems.ARCHITE_CAPSULE.get() && !CapsuleData.hasCapsule(gun);
                  boolean remove = carried.m_41619_() && CapsuleData.hasCapsule(gun);
                  if (install || remove) {
                     if (player.m_9236_().m_5776_()) {
                        if (player.m_7500_() && slot.f_40218_ == player.m_150109_()) {
                           apply(self, slot, gun, player, carried, install);
                           syncCreative(slot, gun, install);
                           ci.cancel();
                        }
                     } else {
                        ci.cancel();
                        apply(self, slot, gun, player, carried, install);
                     }
                  }
               }
            }
         }
      }
   }

   private static void apply(AbstractContainerMenu menu, Slot slot, ItemStack gun, Player player, ItemStack carried, boolean install) {
      if (install) {
         CapsuleData.install(gun, carried);
         ItemStack rest = carried.m_41777_();
         rest.m_41774_(1);
         menu.m_142503_(rest.m_41619_() ? ItemStack.f_41583_ : rest);
      } else {
         ItemStack capsule = CapsuleData.createCapsuleFromGun(gun);
         CapsuleData.uninstall(gun);
         menu.m_142503_(capsule);
      }

      CapsuleData.refreshTaczCache(player, gun);
      slot.m_6654_();
      menu.m_38946_();
      player.m_150109_().m_6596_();
      player.m_5661_(
         Component.m_237115_(install ? "message.sakura_mod.capsule.installed" : "message.sakura_mod.capsule.removed").m_130940_(ChatFormatting.GOLD), true
      );
   }

   private static void syncCreative(Slot slot, ItemStack gun, boolean install) {
      CompoundTag stats = new CompoundTag();
      if (install && gun.m_41782_()) {
         for (CapsuleStat stat : CapsuleStat.values()) {
            if (gun.m_41783_().m_128441_(stat.key())) {
               stats.m_128347_(stat.key(), gun.m_41783_().m_128459_(stat.key()));
               stats.m_128379_(stat.flatKey(), gun.m_41783_().m_128471_(stat.flatKey()));
            }
         }
      }

      int index = slot.getSlotIndex();
      String gunId = gun.m_41782_() ? gun.m_41783_().m_128461_("GunId") : "";
      ModNetwork.CHANNEL.sendToServer(new CapsuleCreativeSyncPacket(index, install, stats, gunId));
   }
}
