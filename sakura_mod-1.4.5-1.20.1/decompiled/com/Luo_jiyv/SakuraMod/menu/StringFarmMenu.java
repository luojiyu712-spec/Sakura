package com.Luo_jiyv.SakuraMod.menu;

import com.Luo_jiyv.SakuraMod.block.ModBlocks;
import com.Luo_jiyv.SakuraMod.blockentity.StringFarmBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.SlotItemHandler;

public class StringFarmMenu extends AbstractContainerMenu {
   private final StringFarmBlockEntity blockEntity;

   public StringFarmMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
      this(
         id,
         playerInv,
         ClientMenuHelper.resolve(
            playerInv, buf.m_130135_(), StringFarmBlockEntity.class, StringFarmBlockEntity::new, ((Block)ModBlocks.STRING_FARM.get()).m_49966_()
         )
      );
   }

   public StringFarmMenu(int id, Inventory playerInv, final StringFarmBlockEntity be) {
      super((MenuType)ModMenus.STRING_FARM_MENU.get(), id);
      this.blockEntity = be;

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.m_38897_(new SlotItemHandler(be.getItemHandler(), col + row * 9, 8 + col * 18, 18 + row * 18) {
               public void m_6654_() {
                  be.inventoryChanged();
               }
            });
         }
      }

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.m_38897_(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.m_38897_(new Slot(playerInv, col, 8 + col * 18, 142));
      }
   }

   public ItemStack m_7648_(Player player, int index) {
      Slot slot = (Slot)this.f_38839_.get(index);
      if (slot != null && slot.m_6657_()) {
         ItemStack stack = slot.m_7993_();
         ItemStack result = stack.m_41777_();
         boolean moved;
         if (index < 27) {
            moved = this.m_38903_(stack, 27, 63, true);
         } else {
            moved = this.m_38903_(stack, 0, 27, false);
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
      return !this.blockEntity.m_58901_()
         && player.m_20275_(
               this.blockEntity.m_58899_().m_123341_() + 0.5, this.blockEntity.m_58899_().m_123342_() + 0.5, this.blockEntity.m_58899_().m_123343_() + 0.5
            )
            <= 64.0;
   }

   public StringFarmBlockEntity getBlockEntity() {
      return this.blockEntity;
   }
}
