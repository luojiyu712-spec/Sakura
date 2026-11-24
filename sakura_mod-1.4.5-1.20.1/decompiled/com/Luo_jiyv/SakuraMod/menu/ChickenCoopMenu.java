package com.Luo_jiyv.SakuraMod.menu;

import com.Luo_jiyv.SakuraMod.block.ModBlocks;
import com.Luo_jiyv.SakuraMod.blockentity.ChickenCoopBlockEntity;
import com.Luo_jiyv.SakuraMod.item.HencoopItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ChickenCoopMenu extends AbstractContainerMenu {
   private final ChickenCoopBlockEntity blockEntity;

   public ChickenCoopMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
      this(
         id,
         playerInv,
         ClientMenuHelper.resolve(
            playerInv, buf.m_130135_(), ChickenCoopBlockEntity.class, ChickenCoopBlockEntity::new, ((Block)ModBlocks.CHICKEN_COOP.get()).m_49966_()
         )
      );
   }

   public ChickenCoopMenu(int id, Inventory playerInv, ChickenCoopBlockEntity be) {
      super((MenuType)ModMenus.CHICKEN_COOP.get(), id);
      this.blockEntity = be;

      for (int row = 0; row < 2; row++) {
         for (int col = 0; col < 9; col++) {
            int index = row * 9 + col;
            this.m_38897_(new Slot(be, index, 8 + col * 18, 18 + row * 18) {
               public boolean m_5857_(ItemStack stack) {
                  return stack.m_41720_() instanceof HencoopItem;
               }
            });
         }
      }

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            int index = 18 + row * 9 + col;
            this.m_38897_(new Slot(be, index, 8 + col * 18, 72 + row * 18) {
               public boolean m_5857_(ItemStack stack) {
                  return stack.m_150930_(Items.f_42521_);
               }
            });
         }
      }

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.m_38897_(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.m_38897_(new Slot(playerInv, col, 8 + col * 18, 198));
      }
   }

   public ItemStack m_7648_(Player player, int index) {
      Slot slot = (Slot)this.f_38839_.get(index);
      if (slot != null && slot.m_6657_()) {
         ItemStack stack = slot.m_7993_();
         ItemStack result = stack.m_41777_();
         boolean moved = false;
         int blockSlots = 45;
         if (index < blockSlots) {
            moved = this.m_38903_(stack, blockSlots, blockSlots + 36, true);
         } else if (stack.m_41720_() instanceof HencoopItem) {
            moved = this.m_38903_(stack, 0, 18, false);
         } else if (stack.m_150930_(Items.f_42521_)) {
            moved = this.m_38903_(stack, 18, 45, false);
         }

         if (!moved) {
            return ItemStack.f_41583_;
         } else {
            if (stack.m_41619_()) {
               slot.m_5852_(ItemStack.f_41583_);
            } else {
               slot.m_6654_();
            }

            if (stack.m_41613_() == result.m_41613_()) {
               return ItemStack.f_41583_;
            } else {
               slot.m_142406_(player, stack);
               return result;
            }
         }
      } else {
         return ItemStack.f_41583_;
      }
   }

   public boolean m_6875_(Player player) {
      return this.blockEntity.m_6542_(player);
   }

   public ChickenCoopBlockEntity getBlockEntity() {
      return this.blockEntity;
   }
}
