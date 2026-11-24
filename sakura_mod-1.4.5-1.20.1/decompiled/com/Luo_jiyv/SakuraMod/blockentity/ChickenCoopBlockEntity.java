package com.Luo_jiyv.SakuraMod.blockentity;

import com.Luo_jiyv.SakuraMod.item.HencoopItem;
import com.Luo_jiyv.SakuraMod.menu.ChickenCoopMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChickenCoopBlockEntity extends BaseContainerBlockEntity implements IDroppable {
   public static final int HENCOOP_SLOTS = 18;
   public static final int EGG_SLOTS = 27;
   public static final int SLOT_COUNT = 45;
   private NonNullList<ItemStack> items = NonNullList.m_122780_(45, ItemStack.f_41583_);
   private int tickCounter = 0;
   private static final int GENERATE_INTERVAL = 200;
   private final RandomSource random = RandomSource.m_216327_();
   private int soundCooldown = 100 + this.random.m_188503_(201);
   private boolean needsRecount = true;
   private int cachedChickenCount = 0;
   private boolean eggsFull = false;
   private final ChickenCoopItemHandler itemHandler = new ChickenCoopItemHandler(this);
   private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> this.itemHandler);

   public ChickenCoopBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.CHICKEN_COOP.get(), pos, state);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, ChickenCoopBlockEntity be) {
      if (be.needsRecount) {
         be.needsRecount = false;
         be.cachedChickenCount = be.countChickens();
      }

      if (be.cachedChickenCount > 0) {
         if (!be.eggsFull) {
            if (--be.soundCooldown <= 0) {
               be.soundCooldown = 100 + be.random.m_188503_(201);
               if (be.hasEggSpace()) {
                  level.m_5594_(null, pos, SoundEvents.f_11750_, SoundSource.BLOCKS, 0.5F, 0.9F + be.random.m_188501_() * 0.2F);
               }
            }

            be.tickCounter++;
            if (be.tickCounter >= 200) {
               be.tickCounter = 0;
               be.generateEggs(level);
            }
         }
      }
   }

   private boolean hasEggSpace() {
      for (int i = 18; i < 45; i++) {
         ItemStack stack = (ItemStack)this.items.get(i);
         if (stack.m_41619_() || stack.m_150930_(Items.f_42521_) && stack.m_41613_() < 16) {
            return true;
         }
      }

      return false;
   }

   private int countChickens() {
      int n = 0;

      for (int i = 0; i < 18; i++) {
         ItemStack s = (ItemStack)this.items.get(i);
         if (!s.m_41619_() && s.m_41720_() instanceof HencoopItem) {
            n += HencoopItem.getCount(s);
         }
      }

      return n;
   }

   private void generateEggs(Level level) {
      if (!level.m_5776_()) {
         if (this.cachedChickenCount > 0) {
            RandomSource random = this.random;
            int eggsToAdd = 0;

            for (int c = 0; c < this.cachedChickenCount; c++) {
               if (random.m_188500_() < 0.05) {
                  eggsToAdd++;
               }
            }

            if (eggsToAdd > 0) {
               int remaining = eggsToAdd;

               for (int i = 18; i < 45 && remaining > 0; i++) {
                  ItemStack slot = (ItemStack)this.items.get(i);
                  if (slot.m_41619_()) {
                     int put = Math.min(remaining, 16);
                     this.items.set(i, new ItemStack(Items.f_42521_, put));
                     remaining -= put;
                  } else if (slot.m_150930_(Items.f_42521_) && slot.m_41613_() < 16) {
                     int put = Math.min(remaining, 16 - slot.m_41613_());
                     slot.m_41769_(put);
                     remaining -= put;
                  }
               }

               if (remaining < eggsToAdd) {
                  this.m_6596_();
               }

               if (remaining == eggsToAdd) {
                  this.eggsFull = true;
               }
            }
         }
      }
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public void setItems(NonNullList<ItemStack> items) {
      this.items = items;
      this.needsRecount = true;
      this.eggsFull = false;
   }

   public int m_6643_() {
      return 45;
   }

   public boolean m_7983_() {
      for (ItemStack stack : this.items) {
         if (!stack.m_41619_()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack m_8020_(int slot) {
      return slot >= 0 && slot < this.items.size() ? (ItemStack)this.items.get(slot) : ItemStack.f_41583_;
   }

   public ItemStack m_7407_(int slot, int amount) {
      ItemStack result = ContainerHelper.m_18969_(this.items, slot, amount);
      if (!result.m_41619_()) {
         if (slot < 18) {
            this.needsRecount = true;
         } else {
            this.eggsFull = false;
         }

         this.m_6596_();
      }

      return result;
   }

   public ItemStack m_8016_(int slot) {
      ItemStack result = ContainerHelper.m_18966_(this.items, slot);
      if (slot < 18) {
         this.needsRecount = true;
      } else {
         this.eggsFull = false;
      }

      return result;
   }

   public void m_6836_(int slot, ItemStack stack) {
      if (slot >= 0 && slot < this.items.size()) {
         this.items.set(slot, stack);
         if (stack.m_41613_() > this.m_6893_()) {
            stack.m_41764_(this.m_6893_());
         }

         if (slot < 18) {
            this.needsRecount = true;
         } else {
            this.eggsFull = false;
         }

         this.m_6596_();
      }
   }

   public void m_6211_() {
      this.items.clear();
      this.needsRecount = true;
      this.eggsFull = false;
      this.m_6596_();
   }

   @Override
   public void drops() {
      if (this.f_58857_ != null) {
         SimpleContainer container = new SimpleContainer(this.items.size());

         for (int i = 0; i < this.items.size(); i++) {
            container.m_6836_(i, (ItemStack)this.items.get(i));
         }

         Containers.m_19002_(this.f_58857_, this.f_58858_, container);
         this.items.clear();
         this.needsRecount = true;
         this.eggsFull = false;
      }
   }

   protected Component m_6820_() {
      return Component.m_237115_("block.sakura_mod.chicken_coop");
   }

   protected AbstractContainerMenu m_6555_(int id, Inventory playerInventory) {
      return new ChickenCoopMenu(id, playerInventory, this);
   }

   protected void m_183515_(CompoundTag tag) {
      super.m_183515_(tag);
      ContainerHelper.m_18973_(tag, this.items);
      tag.m_128405_("TickCounter", this.tickCounter);
   }

   public void m_142466_(CompoundTag tag) {
      super.m_142466_(tag);
      ContainerHelper.m_18980_(tag, this.items);
      this.tickCounter = tag.m_128451_("TickCounter");
      this.needsRecount = true;
      this.eggsFull = false;
   }

   public boolean m_6542_(Player player) {
      return this.f_58857_ != null && this.f_58857_.m_7702_(this.f_58858_) == this
         ? player.m_20275_(this.f_58858_.m_123341_() + 0.5, this.f_58858_.m_123342_() + 0.5, this.f_58858_.m_123343_() + 0.5) <= 64.0
         : false;
   }

   @NotNull
   public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
      return cap == ForgeCapabilities.ITEM_HANDLER ? this.lazyItemHandler.cast() : super.getCapability(cap, side);
   }

   public void invalidateCaps() {
      super.invalidateCaps();
      this.lazyItemHandler.invalidate();
   }

   public CompoundTag m_5995_() {
      return new CompoundTag();
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.m_195640_(this);
   }
}
