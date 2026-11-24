package com.Luo_jiyv.SakuraMod.menu;

import com.Luo_jiyv.SakuraMod.block.ModBlocks;
import com.Luo_jiyv.SakuraMod.blockentity.CobbleGeneratorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class CobbleGeneratorMenu extends AbstractContainerMenu {
   private final CobbleGeneratorBlockEntity blockEntity;

   private static Item getWaterWheel() {
      return (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("create", "large_water_wheel"));
   }

   private static Item getDrill() {
      return (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("create", "mechanical_drill"));
   }

   public CobbleGeneratorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
      this(
         id,
         playerInv,
         ClientMenuHelper.resolve(
            playerInv, buf.m_130135_(), CobbleGeneratorBlockEntity.class, CobbleGeneratorBlockEntity::new, ((Block)ModBlocks.COBBLE_GENERATOR.get()).m_49966_()
         )
      );
   }

   public CobbleGeneratorMenu(int id, Inventory playerInv, CobbleGeneratorBlockEntity be) {
      super((MenuType)ModMenus.COBBLE_GENERATOR.get(), id);
      this.blockEntity = be;

      for (int row = 0; row < 2; row++) {
         for (int col = 0; col < 9; col++) {
            int idx = row * 9 + col;
            this.m_38897_(new CobbleGeneratorMenu.MachineSlot(be, idx, 8 + col * 18, 18 + row * 18) {
               public boolean m_5857_(ItemStack stack) {
                  Item wheel = CobbleGeneratorMenu.getWaterWheel();
                  return wheel != null && stack.m_150930_(wheel);
               }
            });
         }
      }

      for (int col = 0; col < 9; col++) {
         int idx = 18 + col;
         this.m_38897_(new CobbleGeneratorMenu.MachineSlot(be, idx, 8 + col * 18, 54) {
            public boolean m_5857_(ItemStack stack) {
               Item drill = CobbleGeneratorMenu.getDrill();
               return drill != null && stack.m_150930_(drill);
            }
         });
      }

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            int idx = 27 + row * 9 + col;
            this.m_38897_(new CobbleGeneratorMenu.MachineSlot(be, idx, 8 + col * 18, 72 + row * 18) {
               public boolean m_5857_(ItemStack stack) {
                  return stack.m_150930_(Items.f_42594_);
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
         int blockSlots = 54;
         if (index < 54) {
            moved = this.m_38903_(stack, 54, 90, true);
         } else {
            Item wheel = getWaterWheel();
            Item drill = getDrill();
            if (wheel != null && stack.m_150930_(wheel)) {
               moved = this.m_38903_(stack, 0, 18, false);
            } else if (drill != null && stack.m_150930_(drill)) {
               moved = this.m_38903_(stack, 18, 27, false);
            } else if (stack.m_150930_(Items.f_42594_)) {
               moved = this.m_38903_(stack, 27, 54, false);
            }
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

   public CobbleGeneratorBlockEntity getBlockEntity() {
      return this.blockEntity;
   }

   private static class MachineSlot extends Slot {
      private final CobbleGeneratorBlockEntity machine;

      MachineSlot(CobbleGeneratorBlockEntity machine, int index, int x, int y) {
         super(machine, index, x, y);
         this.machine = machine;
      }

      public void m_6654_() {
         this.machine.slotChanged(this.m_150661_());
      }
   }
}
