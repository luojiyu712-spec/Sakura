package com.Luo_jiyv.SakuraMod.blockentity;

import com.Luo_jiyv.SakuraMod.item.HencoopItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class ChickenCoopItemHandler implements IItemHandler {
   private final ChickenCoopBlockEntity blockEntity;

   public ChickenCoopItemHandler(ChickenCoopBlockEntity blockEntity) {
      this.blockEntity = blockEntity;
   }

   public int getSlots() {
      return 45;
   }

   @NotNull
   public ItemStack getStackInSlot(int slot) {
      return this.blockEntity.m_8020_(slot);
   }

   @NotNull
   public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
      if (stack.m_41619_()) {
         return ItemStack.f_41583_;
      } else if (!this.isItemValid(slot, stack)) {
         return stack;
      } else {
         ItemStack existing = this.blockEntity.m_8020_(slot);
         int limit = Math.min(this.getSlotLimit(slot), stack.m_41741_());
         if (!existing.m_41619_()) {
            if (!ItemStack.m_150942_(existing, stack)) {
               return stack;
            }

            limit -= existing.m_41613_();
         }

         if (limit <= 0) {
            return stack;
         } else {
            int toInsert = Math.min(stack.m_41613_(), limit);
            if (!simulate) {
               if (existing.m_41619_()) {
                  this.blockEntity.m_6836_(slot, stack.m_255036_(toInsert));
               } else {
                  existing.m_41769_(toInsert);
                  this.blockEntity.m_6836_(slot, existing);
               }
            }

            ItemStack remainder = stack.m_41777_();
            remainder.m_41774_(toInsert);
            return remainder;
         }
      }
   }

   @NotNull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (slot < 0 || slot >= this.getSlots()) {
         return ItemStack.f_41583_;
      } else if (slot < 18) {
         return ItemStack.f_41583_;
      } else if (amount <= 0) {
         return ItemStack.f_41583_;
      } else {
         ItemStack existing = this.blockEntity.m_8020_(slot);
         if (existing.m_41619_()) {
            return ItemStack.f_41583_;
         } else {
            int toExtract = Math.min(amount, existing.m_41613_());
            ItemStack result = existing.m_255036_(toExtract);
            if (!simulate) {
               this.blockEntity.m_7407_(slot, toExtract);
            }

            return result;
         }
      }
   }

   public int getSlotLimit(int slot) {
      return slot < 18 ? 1 : 16;
   }

   public boolean isItemValid(int slot, @NotNull ItemStack stack) {
      return slot < 18 ? stack.m_41720_() instanceof HencoopItem : stack.m_150930_(Items.f_42521_);
   }
}
