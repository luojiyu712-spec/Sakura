package com.Luo_jiyv.SakuraMod.blockentity;

import com.Luo_jiyv.SakuraMod.menu.StringFarmMenu;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class StringFarmBlockEntity extends BlockEntity implements MenuProvider, IDroppable {
   private static final int PRODUCTION_INTERVAL = 20;
   private static final int STRING_PER_PRODUCTION = 64;
   private final ItemStackHandler itemHandler = new ItemStackHandler(27) {
      protected void onContentsChanged(int slot) {
         StringFarmBlockEntity.this.inventoryChanged();
      }
   };
   private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> this.itemHandler);
   private Boolean canAcceptOutput = null;

   public StringFarmBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.STRING_FARM.get(), pos, state);
   }

   public void inventoryChanged() {
      this.canAcceptOutput = null;
      this.m_6596_();
   }

   public static void tick(Level level, BlockPos pos, BlockState state, StringFarmBlockEntity be) {
      if ((level.m_46467_() + pos.m_121878_()) % 20L == 0L) {
         if (!level.m_276867_(pos)) {
            if (be.canAcceptOutput == null) {
               be.canAcceptOutput = be.computeCanAcceptOutput();
            }

            if (be.canAcceptOutput) {
               ItemStack stack = new ItemStack(Items.f_42401_, 64);

               for (int i = 0; i < be.itemHandler.getSlots(); i++) {
                  stack = be.itemHandler.insertItem(i, stack, false);
                  if (stack.m_41619_()) {
                     break;
                  }
               }

               if (!stack.m_41619_()) {
                  be.canAcceptOutput = false;
                  Containers.m_18992_(level, pos.m_123341_() + 0.5, pos.m_123342_() + 0.5, pos.m_123343_() + 0.5, stack);
               }
            }
         }
      }
   }

   private boolean computeCanAcceptOutput() {
      ItemStack probe = new ItemStack(Items.f_42401_, 1);

      for (int i = 0; i < this.itemHandler.getSlots(); i++) {
         if (this.itemHandler.insertItem(i, probe, true).m_41619_()) {
            return true;
         }
      }

      return false;
   }

   @NotNull
   public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
      return cap == ForgeCapabilities.ITEM_HANDLER ? this.lazyItemHandler.cast() : super.getCapability(cap, side);
   }

   public void invalidateCaps() {
      super.invalidateCaps();
      this.lazyItemHandler.invalidate();
   }

   protected void m_183515_(CompoundTag tag) {
      tag.m_128365_("inventory", this.itemHandler.serializeNBT());
      super.m_183515_(tag);
   }

   public void m_142466_(CompoundTag tag) {
      super.m_142466_(tag);
      this.itemHandler.deserializeNBT(tag.m_128469_("inventory"));
      this.canAcceptOutput = null;
   }

   @Override
   public void drops() {
      if (this.f_58857_ != null) {
         SimpleContainer container = new SimpleContainer(this.itemHandler.getSlots());

         for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            container.m_6836_(i, this.itemHandler.getStackInSlot(i));
         }

         Containers.m_19002_(this.f_58857_, this.f_58858_, container);
      }
   }

   public Component m_5446_() {
      return Component.m_237115_("block.sakura_mod.string_farm");
   }

   @Nullable
   public AbstractContainerMenu m_7208_(int id, Inventory playerInv, Player player) {
      return new StringFarmMenu(id, playerInv, this);
   }

   public ItemStackHandler getItemHandler() {
      return this.itemHandler;
   }
}
