package com.Luo_jiyv.SakuraMod.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlazingRockBlockEntity extends BlockEntity {
   private static final int INFINITE_AMOUNT = Integer.MAX_VALUE;
   private final IFluidHandler fluidHandler = new IFluidHandler() {
      public int getTanks() {
         return 1;
      }

      @NotNull
      public FluidStack getFluidInTank(int tank) {
         return new FluidStack(Fluids.f_76195_, Integer.MAX_VALUE);
      }

      public int getTankCapacity(int tank) {
         return Integer.MAX_VALUE;
      }

      public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
         return false;
      }

      public int fill(FluidStack resource, FluidAction action) {
         return 0;
      }

      @NotNull
      public FluidStack drain(FluidStack resource, FluidAction action) {
         return !resource.isEmpty() && resource.getFluid() == Fluids.f_76195_
            ? new FluidStack(Fluids.f_76195_, Math.min(resource.getAmount(), Integer.MAX_VALUE))
            : FluidStack.EMPTY;
      }

      @NotNull
      public FluidStack drain(int maxDrain, FluidAction action) {
         return maxDrain <= 0 ? FluidStack.EMPTY : new FluidStack(Fluids.f_76195_, Math.min(maxDrain, Integer.MAX_VALUE));
      }
   };
   private final LazyOptional<IFluidHandler> lazyHandler = LazyOptional.of(() -> this.fluidHandler);

   public BlazingRockBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.BLAZING_ROCK.get(), pos, state);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, BlazingRockBlockEntity blockEntity) {
      if (!level.m_5776_() && level.m_276867_(pos)) {
         for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.m_121945_(direction);
            if (level.m_46805_(neighborPos)) {
               BlockEntity neighbor = level.m_7702_(neighborPos);
               if (neighbor != null) {
                  neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.m_122424_())
                     .ifPresent(handler -> handler.fill(new FluidStack(Fluids.f_76195_, Integer.MAX_VALUE), FluidAction.EXECUTE));
               }
            }
         }
      }
   }

   @NotNull
   public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
      return cap == ForgeCapabilities.FLUID_HANDLER ? this.lazyHandler.cast() : super.getCapability(cap, side);
   }

   public void invalidateCaps() {
      super.invalidateCaps();
      this.lazyHandler.invalidate();
   }
}
