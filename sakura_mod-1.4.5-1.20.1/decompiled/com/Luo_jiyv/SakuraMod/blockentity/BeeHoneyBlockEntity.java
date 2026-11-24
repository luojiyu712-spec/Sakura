package com.Luo_jiyv.SakuraMod.blockentity;

import com.Luo_jiyv.SakuraMod.item.BeeBoxItem;
import com.Luo_jiyv.SakuraMod.menu.BeeHoneyMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeeHoneyBlockEntity extends BlockEntity implements MenuProvider, IDroppable {
   public static final int BEE_SLOTS = 10;
   public static final int FLOWER_SLOTS = 5;
   public static final int TOTAL_SLOTS = 15;
   public static final int TANK_CAPACITY = 16000;
   public static final int TICK_INTERVAL = 40;
   public static final float BASE_CHANCE = 0.05F;
   public static final float FLOWER_BONUS = 0.01F;
   public static final int HONEY_PER_PRODUCTION = 50;
   private int tickCounter = 0;
   private final RandomSource random = RandomSource.m_216327_();
   private int soundCooldown = 100 + this.random.m_188503_(201);
   private boolean needsRecount = true;
   private int cachedBeeCount = 0;
   private Fluid cachedHoneyFluid = Fluids.f_76191_;
   private final ItemStackHandler itemHandler = new ItemStackHandler(15) {
      protected void onContentsChanged(int slot) {
         BeeHoneyBlockEntity.this.m_6596_();
         if (slot < 10) {
            BeeHoneyBlockEntity.this.needsRecount = true;
         }
      }

      public boolean isItemValid(int slot, @NotNull ItemStack stack) {
         return slot < 10 ? stack.m_41720_() instanceof BeeBoxItem : stack.m_204117_(ItemTags.f_13149_);
      }

      public int getSlotLimit(int slot) {
         return slot < 10 ? 1 : 64;
      }
   };
   private final FluidTank tank = new FluidTank(16000) {
      protected void onContentsChanged() {
         BeeHoneyBlockEntity.this.m_6596_();
      }
   };
   private final IFluidHandler outputOnlyHandler = new IFluidHandler() {
      public int getTanks() {
         return BeeHoneyBlockEntity.this.tank.getTanks();
      }

      @NotNull
      public FluidStack getFluidInTank(int t) {
         return BeeHoneyBlockEntity.this.tank.getFluidInTank(t);
      }

      public int getTankCapacity(int t) {
         return BeeHoneyBlockEntity.this.tank.getTankCapacity(t);
      }

      public boolean isFluidValid(int t, @NotNull FluidStack stack) {
         return false;
      }

      public int fill(FluidStack resource, FluidAction action) {
         return 0;
      }

      @NotNull
      public FluidStack drain(FluidStack resource, FluidAction action) {
         return BeeHoneyBlockEntity.this.tank.drain(resource, action);
      }

      @NotNull
      public FluidStack drain(int maxDrain, FluidAction action) {
         return BeeHoneyBlockEntity.this.tank.drain(maxDrain, action);
      }
   };
   private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> this.itemHandler);
   private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> this.outputOnlyHandler);
   @Nullable
   private static Fluid honeyFluidCache;

   public BeeHoneyBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.BEE_HONEY_BLOCK.get(), pos, state);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, BeeHoneyBlockEntity be) {
      if (be.needsRecount) {
         be.needsRecount = false;
         be.cachedBeeCount = be.countBees();
         be.cachedHoneyFluid = getHoneyFluid();
      }

      if (be.cachedBeeCount > 0) {
         if (be.cachedHoneyFluid != Fluids.f_76191_) {
            if (be.tank.getFluidAmount() < be.tank.getCapacity()) {
               if (--be.soundCooldown <= 0) {
                  be.soundCooldown = 100 + be.random.m_188503_(201);
                  level.m_5594_(null, pos, SoundEvents.f_11698_, SoundSource.BLOCKS, 0.6F, 0.9F + be.random.m_188501_() * 0.2F);
               }

               be.tickCounter++;
               if (be.tickCounter >= 40) {
                  be.tickCounter = 0;
                  be.produceHoney();
               }
            }
         }
      }
   }

   private int countBees() {
      int n = 0;

      for (int i = 0; i < 10; i++) {
         ItemStack s = this.itemHandler.getStackInSlot(i);
         if (!s.m_41619_() && s.m_41720_() instanceof BeeBoxItem) {
            n += BeeBoxItem.getCount(s);
         }
      }

      return n;
   }

   private void produceHoney() {
      if (this.f_58857_ != null && !this.f_58857_.m_5776_()) {
         if (this.cachedBeeCount > 0) {
            if (this.cachedHoneyFluid != Fluids.f_76191_) {
               int flowerCount = 0;

               for (int i = 10; i < 15; i++) {
                  ItemStack s = this.itemHandler.getStackInSlot(i);
                  if (!s.m_41619_() && s.m_204117_(ItemTags.f_13149_)) {
                     flowerCount += s.m_41613_();
                  }
               }

               float chance = Math.min(1.0F, 0.05F + flowerCount * 0.01F);
               Fluid honey = this.cachedHoneyFluid;
               RandomSource rand = this.random;
               int produced = 0;

               for (int b = 0; b < this.cachedBeeCount; b++) {
                  if (rand.m_188501_() < chance) {
                     produced += this.tank.fill(new FluidStack(honey, 50), FluidAction.EXECUTE);
                  }
               }

               if (produced > 0) {
                  this.m_6596_();
               }
            }
         }
      }
   }

   public static Fluid getHoneyFluid() {
      Fluid f = honeyFluidCache;
      if (f == null) {
         f = (Fluid)ForgeRegistries.FLUIDS.getValue(ResourceLocation.fromNamespaceAndPath("create", "honey"));
         honeyFluidCache = f;
      }

      return f != null ? f : Fluids.f_76191_;
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

   protected void m_183515_(CompoundTag tag) {
      super.m_183515_(tag);
      tag.m_128365_("Inventory", this.itemHandler.serializeNBT());
      tag.m_128365_("Tank", this.tank.writeToNBT(new CompoundTag()));
      tag.m_128405_("TickCounter", this.tickCounter);
   }

   public void m_142466_(CompoundTag tag) {
      super.m_142466_(tag);
      this.itemHandler.deserializeNBT(tag.m_128469_("Inventory"));
      this.tank.readFromNBT(tag.m_128469_("Tank"));
      this.tickCounter = tag.m_128451_("TickCounter");
      this.needsRecount = true;
   }

   @NotNull
   public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
      if (cap == ForgeCapabilities.ITEM_HANDLER) {
         return this.lazyItemHandler.cast();
      } else {
         return cap == ForgeCapabilities.FLUID_HANDLER ? this.lazyFluidHandler.cast() : super.getCapability(cap, side);
      }
   }

   public void invalidateCaps() {
      super.invalidateCaps();
      this.lazyItemHandler.invalidate();
      this.lazyFluidHandler.invalidate();
   }

   public Component m_5446_() {
      return Component.m_237115_("block.sakura_mod.bee_honey_block");
   }

   @Nullable
   public AbstractContainerMenu m_7208_(int id, Inventory playerInv, Player player) {
      return new BeeHoneyMenu(id, playerInv, this);
   }

   public CompoundTag m_5995_() {
      return new CompoundTag();
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.m_195640_(this);
   }

   public boolean stillValid(Player player) {
      return this.f_58857_ != null && this.f_58857_.m_7702_(this.f_58858_) == this
         ? player.m_20275_(this.f_58858_.m_123341_() + 0.5, this.f_58858_.m_123342_() + 0.5, this.f_58858_.m_123343_() + 0.5) <= 64.0
         : false;
   }

   public ItemStackHandler getItemHandler() {
      return this.itemHandler;
   }

   public FluidTank getTank() {
      return this.tank;
   }
}
