package com.Luo_jiyv.SakuraMod.menu;

import com.Luo_jiyv.SakuraMod.block.ModBlocks;
import com.Luo_jiyv.SakuraMod.blockentity.BeeHoneyBlockEntity;
import com.Luo_jiyv.SakuraMod.item.BeeBoxItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.SlotItemHandler;

public class BeeHoneyMenu extends AbstractContainerMenu {
   private final BeeHoneyBlockEntity blockEntity;
   private final ContainerData fluidData;
   private final boolean clientSide;

   public BeeHoneyMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
      this(
         id,
         playerInv,
         ClientMenuHelper.resolve(
            playerInv, buf.m_130135_(), BeeHoneyBlockEntity.class, BeeHoneyBlockEntity::new, ((Block)ModBlocks.BEE_HONEY_BLOCK.get()).m_49966_()
         )
      );
   }

   public BeeHoneyMenu(int id, Inventory playerInv, BeeHoneyBlockEntity be) {
      super((MenuType)ModMenus.BEE_HONEY_MENU.get(), id);
      this.blockEntity = be;
      this.clientSide = playerInv.f_35978_.m_9236_().m_5776_();
      final int[] synced = new int[2];
      this.fluidData = new ContainerData() {
         public int m_6413_(int index) {
            if (BeeHoneyMenu.this.clientSide) {
               return synced[index];
            } else {
               return switch (index) {
                  case 0 -> BeeHoneyMenu.this.blockEntity.getTank().getFluidAmount();
                  case 1 -> BeeHoneyMenu.this.blockEntity.getTank().getCapacity();
                  default -> 0;
               };
            }
         }

         public void m_8050_(int index, int value) {
            if (index >= 0 && index < synced.length) {
               synced[index] = value;
            }
         }

         public int m_6499_() {
            return 2;
         }
      };
      this.m_38884_(this.fluidData);

      for (int row = 0; row < 2; row++) {
         for (int col = 0; col < 5; col++) {
            int index = row * 5 + col;
            this.m_38897_(new SlotItemHandler(be.getItemHandler(), index, 62 + col * 18, 17 + row * 18) {
               public boolean m_5857_(ItemStack stack) {
                  return stack.m_41720_() instanceof BeeBoxItem;
               }
            });
         }
      }

      for (int col = 0; col < 5; col++) {
         int index = 10 + col;
         this.m_38897_(new SlotItemHandler(be.getItemHandler(), index, 62 + col * 18, 53) {
            public boolean m_5857_(ItemStack stack) {
               return stack.m_204117_(ItemTags.f_13149_);
            }
         });
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
         boolean moved = false;
         int blockSlots = 15;
         if (index < 15) {
            moved = this.m_38903_(stack, 15, 51, true);
         } else if (stack.m_41720_() instanceof BeeBoxItem) {
            moved = this.m_38903_(stack, 0, 10, false);
         } else if (stack.m_204117_(ItemTags.f_13149_)) {
            moved = this.m_38903_(stack, 10, 15, false);
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
      return this.blockEntity.stillValid(player);
   }

   public BeeHoneyBlockEntity getBlockEntity() {
      return this.blockEntity;
   }

   public ContainerData getFluidData() {
      return this.fluidData;
   }
}
