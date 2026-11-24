package com.Luo_jiyv.SakuraMod.menu;

import com.Luo_jiyv.SakuraMod.block.ModBlocks;
import com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MolecularAssemblerMenu extends AbstractContainerMenu {
   public static final int WIDTH = 176;
   public static final int HEIGHT = 199;
   public static final int TAB_W = 23;
   public static final int TAB_H = 26;
   public static final int PATTERN_X = 126;
   public static final int PATTERN_Y = 16;
   public static final int INPUT_X = 29;
   public static final int INPUT_Y = 31;
   public static final int INPUT_PITCH = 18;
   public static final int OUTPUT_X = 126;
   public static final int OUTPUT_Y = 49;
   public static final int INV_X = 8;
   public static final int INV_Y = 115;
   public static final int HOTBAR_Y = 173;
   private final MolecularAssemblerBlockEntity blockEntity;
   private long nextConfigAt;
   private long configTick = Long.MIN_VALUE;
   private int configActions;
   private final SimpleContainer ghostContainer = new SimpleContainer(1);
   public static final int MACHINE_SLOTS = 2;

   public MolecularAssemblerMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
      this(
         id,
         playerInv,
         ClientMenuHelper.resolve(
            playerInv,
            buf.m_130135_(),
            MolecularAssemblerBlockEntity.class,
            MolecularAssemblerBlockEntity::new,
            ((Block)ModBlocks.MOLECULAR_ASSEMBLER.get()).m_49966_()
         )
      );
   }

   public MolecularAssemblerMenu(int id, Inventory playerInv, MolecularAssemblerBlockEntity be) {
      super((MenuType)ModMenus.MOLECULAR_ASSEMBLER.get(), id);
      this.blockEntity = be;
      IItemHandler items = be.getItems();
      this.ghostContainer.m_6836_(0, be.getGhostPattern());
      this.m_38897_(new MolecularAssemblerMenu.PatternSlot(this.ghostContainer, 0, 126, 16));
      this.m_38897_(new MolecularAssemblerMenu.OutputSlot(items, 27, 126, 49));

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.m_38897_(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 115 + row * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.m_38897_(new Slot(playerInv, col, 8 + col * 18, 173));
      }

      this.m_38884_(be.getContainerData());
   }

   public void m_150399_(int slotId, int button, ClickType clickType, Player player) {
      if (slotId != 0) {
         super.m_150399_(slotId, button, clickType, player);
      } else {
         if (clickType == ClickType.PICKUP
            && (button == 0 || button == 1)
            && player instanceof ServerPlayer
            && this.m_6875_(player)
            && this.allowConfiguration(player)) {
            this.blockEntity.setGhostPattern(this.m_142621_());
         }
      }
   }

   public void m_38946_() {
      ItemStack ghost = this.blockEntity.getGhostPattern();
      if (!ItemStack.m_41728_(this.ghostContainer.m_8020_(0), ghost)) {
         this.ghostContainer.m_6836_(0, ghost);
      }

      super.m_38946_();
   }

   public MolecularAssemblerBlockEntity getBlockEntity() {
      return this.blockEntity;
   }

   public ItemStack m_7648_(Player player, int index) {
      if (index != 1) {
         return ItemStack.f_41583_;
      } else {
         Slot slot = (Slot)this.f_38839_.get(index);
         if (!slot.m_6657_()) {
            return ItemStack.f_41583_;
         } else {
            ItemStack result = slot.m_7993_().m_41777_();
            ItemStack stack = result.m_41777_();
            if (!this.m_38903_(stack, 2, this.f_38839_.size(), true)) {
               return ItemStack.f_41583_;
            } else {
               slot.m_5852_(stack.m_41619_() ? ItemStack.f_41583_ : stack);
               slot.m_142406_(player, stack);
               return result;
            }
         }
      }
   }

   public boolean allowConfiguration(Player player) {
      if (player.m_5833_()) {
         return false;
      } else {
         long now = player.m_9236_().m_46467_();
         if (now < this.nextConfigAt) {
            return false;
         } else {
            if (this.configTick != now) {
               this.configTick = now;
               this.configActions = 0;
            }

            if (++this.configActions > 4) {
               this.nextConfigAt = now + 5L;
               return false;
            } else {
               return this.blockEntity.canConfigure(player);
            }
         }
      }
   }

   public boolean m_6875_(Player player) {
      BlockPos pos = this.blockEntity.m_58899_();
      Level level = player.m_9236_();
      return !this.blockEntity.m_58901_() && !(player.m_20275_(pos.m_123341_() + 0.5, pos.m_123342_() + 0.5, pos.m_123343_() + 0.5) > 64.0)
         ? level.f_46443_ || this.blockEntity.m_58904_() == level && level.m_46805_(pos) && level.m_7702_(pos) == this.blockEntity
         : false;
   }

   private static class OutputSlot extends SlotItemHandler {
      OutputSlot(IItemHandler handler, int index, int x, int y) {
         super(handler, index, x, y);
      }

      public boolean m_5857_(ItemStack stack) {
         return false;
      }
   }

   private static class PatternSlot extends Slot {
      PatternSlot(Container container, int index, int x, int y) {
         super(container, index, x, y);
      }

      public boolean m_5857_(ItemStack stack) {
         return false;
      }

      public boolean m_8010_(Player player) {
         return false;
      }

      public ItemStack m_6201_(int amount) {
         return ItemStack.f_41583_;
      }
   }
}
