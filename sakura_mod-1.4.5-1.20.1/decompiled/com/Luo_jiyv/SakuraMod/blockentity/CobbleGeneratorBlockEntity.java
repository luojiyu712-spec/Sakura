package com.Luo_jiyv.SakuraMod.blockentity;

import com.Luo_jiyv.SakuraMod.menu.CobbleGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CobbleGeneratorBlockEntity extends BlockEntity implements Container, MenuProvider, IDroppable {
   public static final int WHEEL_SLOTS = 18;
   public static final int DRILL_SLOTS = 9;
   public static final int STORAGE_SLOTS = 27;
   public static final int TOTAL_SLOTS = 54;
   public static final int WHEEL_START = 0;
   public static final int DRILL_START = 18;
   public static final int STORAGE_START = 27;
   private NonNullList<ItemStack> items = NonNullList.m_122780_(54, ItemStack.f_41583_);
   private int tickCounter = 0;
   private boolean capacityDirty = true;
   private int cachedWheelCount = 0;
   private int cachedDrillCount = 0;
   private boolean storageFull = false;
   @Nullable
   private static Item waterWheelCache;
   @Nullable
   private static Item mechanicalDrillCache;
   private final IItemHandler pipeHandler = new IItemHandler() {
      public int getSlots() {
         return 54;
      }

      @NotNull
      public ItemStack getStackInSlot(int slot) {
         return slot >= 0 && slot < 54 ? (ItemStack)CobbleGeneratorBlockEntity.this.items.get(slot) : ItemStack.f_41583_;
      }

      @NotNull
      public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
         if (stack.m_41619_()) {
            return ItemStack.f_41583_;
         } else if (slot < 27) {
            return stack;
         } else if (!stack.m_150930_(Items.f_42594_)) {
            return stack;
         } else {
            ItemStack existing = (ItemStack)CobbleGeneratorBlockEntity.this.items.get(slot);
            int limit = 64;
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
                     CobbleGeneratorBlockEntity.this.items.set(slot, stack.m_255036_(toInsert));
                  } else {
                     existing.m_41769_(toInsert);
                  }

                  CobbleGeneratorBlockEntity.this.slotChanged(slot);
               }

               ItemStack remainder = stack.m_41777_();
               remainder.m_41774_(toInsert);
               return remainder;
            }
         }
      }

      @NotNull
      public ItemStack extractItem(int slot, int amount, boolean simulate) {
         if (slot < 0 || slot >= 54) {
            return ItemStack.f_41583_;
         } else if (amount <= 0) {
            return ItemStack.f_41583_;
         } else if (slot < 27) {
            return ItemStack.f_41583_;
         } else {
            ItemStack existing = (ItemStack)CobbleGeneratorBlockEntity.this.items.get(slot);
            if (existing.m_41619_()) {
               return ItemStack.f_41583_;
            } else {
               int toExtract = Math.min(amount, existing.m_41613_());
               ItemStack result = existing.m_255036_(toExtract);
               if (!simulate) {
                  existing.m_41774_(toExtract);
                  if (existing.m_41619_()) {
                     CobbleGeneratorBlockEntity.this.items.set(slot, ItemStack.f_41583_);
                  }

                  CobbleGeneratorBlockEntity.this.slotChanged(slot);
               }

               return result;
            }
         }
      }

      public int getSlotLimit(int slot) {
         return 64;
      }

      public boolean isItemValid(int slot, @NotNull ItemStack stack) {
         if (slot < 18) {
            Item wheel = CobbleGeneratorBlockEntity.getWaterWheel();
            return wheel != null && stack.m_150930_(wheel);
         } else if (slot >= 27) {
            return stack.m_150930_(Items.f_42594_);
         } else {
            Item drill = CobbleGeneratorBlockEntity.getMechanicalDrill();
            return drill != null && stack.m_150930_(drill);
         }
      }
   };
   private final LazyOptional<IItemHandler> lazyPipeHandler = LazyOptional.of(() -> this.pipeHandler);

   @Nullable
   private static Item getWaterWheel() {
      Item cached = waterWheelCache;
      if (cached == null) {
         cached = (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("create", "large_water_wheel"));
         waterWheelCache = cached;
      }

      return cached;
   }

   @Nullable
   private static Item getMechanicalDrill() {
      Item cached = mechanicalDrillCache;
      if (cached == null) {
         cached = (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("create", "mechanical_drill"));
         mechanicalDrillCache = cached;
      }

      return cached;
   }

   public CobbleGeneratorBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.COBBLE_GENERATOR.get(), pos, state);
   }

   public int m_6643_() {
      return 54;
   }

   public boolean m_7983_() {
      for (ItemStack s : this.items) {
         if (!s.m_41619_()) {
            return false;
         }
      }

      return true;
   }

   @NotNull
   public ItemStack m_8020_(int slot) {
      return slot >= 0 && slot < 54 ? (ItemStack)this.items.get(slot) : ItemStack.f_41583_;
   }

   @NotNull
   public ItemStack m_7407_(int slot, int amount) {
      ItemStack r = ContainerHelper.m_18969_(this.items, slot, amount);
      if (!r.m_41619_()) {
         this.slotChanged(slot);
      }

      return r;
   }

   @NotNull
   public ItemStack m_8016_(int slot) {
      ItemStack r = ContainerHelper.m_18966_(this.items, slot);
      if (slot < 27) {
         this.capacityDirty = true;
      } else {
         this.storageFull = false;
      }

      return r;
   }

   public void m_6836_(int slot, ItemStack stack) {
      if (slot >= 0 && slot < 54) {
         this.items.set(slot, stack);
         if (stack.m_41613_() > 64) {
            stack.m_41764_(64);
         }

         this.slotChanged(slot);
      }
   }

   public void slotChanged(int slot) {
      if (slot < 27) {
         this.capacityDirty = true;
      } else {
         this.storageFull = false;
      }

      this.m_6596_();
   }

   public boolean m_6542_(Player player) {
      return this.f_58857_ != null && this.f_58857_.m_7702_(this.f_58858_) == this
         ? player.m_20275_(this.f_58858_.m_123341_() + 0.5, this.f_58858_.m_123342_() + 0.5, this.f_58858_.m_123343_() + 0.5) <= 64.0
         : false;
   }

   public void m_6211_() {
      this.items.clear();
      this.capacityDirty = true;
      this.storageFull = false;
      this.m_6596_();
   }

   @NotNull
   public Component m_5446_() {
      return Component.m_237115_("block.sakura_mod.cobble_generator");
   }

   @Nullable
   public AbstractContainerMenu m_7208_(int id, @NotNull Inventory inv, @NotNull Player player) {
      return new CobbleGeneratorMenu(id, inv, this);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, CobbleGeneratorBlockEntity be) {
      be.tickCounter++;
      if (be.tickCounter >= 20) {
         be.tickCounter = 0;
         if (be.capacityDirty) {
            be.capacityDirty = false;
            int w = 0;

            for (int i = 0; i < 18; i++) {
               w += ((ItemStack)be.items.get(i)).m_41613_();
            }

            int d = 0;

            for (int i = 18; i < 27; i++) {
               d += ((ItemStack)be.items.get(i)).m_41613_();
            }

            be.cachedWheelCount = w;
            be.cachedDrillCount = d;
         }

         if (be.cachedDrillCount > 0) {
            if (be.cachedWheelCount >= be.cachedDrillCount * 2) {
               if (!be.storageFull) {
                  be.produceCobble();
               }
            }
         }
      }
   }

   private void produceCobble() {
      int remaining = this.cachedDrillCount;

      for (int i = 27; i < 54 && remaining > 0; i++) {
         ItemStack slot = (ItemStack)this.items.get(i);
         if (slot.m_41619_()) {
            int put = Math.min(remaining, 64);
            this.items.set(i, new ItemStack(Items.f_42594_, put));
            remaining -= put;
         } else if (slot.m_150930_(Items.f_42594_) && slot.m_41613_() < 64) {
            int space = 64 - slot.m_41613_();
            int put = Math.min(remaining, space);
            slot.m_41769_(put);
            remaining -= put;
         }
      }

      if (remaining < this.cachedDrillCount) {
         this.m_6596_();
      }

      if (remaining > 0) {
         this.storageFull = true;
      }
   }

   protected void m_183515_(@NotNull CompoundTag tag) {
      super.m_183515_(tag);
      ContainerHelper.m_18973_(tag, this.items);
      tag.m_128405_("TickCounter", this.tickCounter);
   }

   public void m_142466_(@NotNull CompoundTag tag) {
      super.m_142466_(tag);
      this.items = NonNullList.m_122780_(54, ItemStack.f_41583_);
      ContainerHelper.m_18980_(tag, this.items);
      this.tickCounter = tag.m_128451_("TickCounter");
      this.capacityDirty = true;
      this.storageFull = false;
   }

   @NotNull
   public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
      return cap == ForgeCapabilities.ITEM_HANDLER ? this.lazyPipeHandler.cast() : super.getCapability(cap, side);
   }

   public void invalidateCaps() {
      super.invalidateCaps();
      this.lazyPipeHandler.invalidate();
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   @Override
   public void drops() {
      if (this.f_58857_ != null) {
         SimpleContainer container = new SimpleContainer(this.items.size());

         for (int i = 0; i < this.items.size(); i++) {
            container.m_6836_(i, (ItemStack)this.items.get(i));
         }

         Containers.m_19002_(this.f_58857_, this.f_58858_, container);
      }
   }
}
