package com.Luo_jiyv.SakuraMod.blockentity;

import com.Luo_jiyv.SakuraMod.multiblock.ControllerStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ControllerBlockEntity extends BlockEntity {
   public static final int CAPACITY = 18000000;
   private int localEnergy;
   @Nullable
   private ControllerStructure.Network network;
   private boolean capsActive = true;
   private LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> new ControllerBlockEntity.EnergyView());

   public ControllerBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.CONTROLLER.get(), pos, state);
   }

   public int localEnergy() {
      return this.localEnergy;
   }

   public void setLocalEnergy(int value) {
      this.localEnergy = value;
      if (this.f_58857_ instanceof ServerLevel server) {
         LevelChunk chunk = server.m_7726_().m_7131_(this.f_58858_.m_123341_() >> 4, this.f_58858_.m_123343_() >> 4);
         if (chunk != null) {
            chunk.m_8092_(true);
         }
      }
   }

   @Nullable
   public ControllerStructure.Network network() {
      return this.network;
   }

   public void setNetwork(@Nullable ControllerStructure.Network network) {
      this.network = network;
   }

   public boolean hasValidatedNetwork() {
      return this.network != null && this.network.validated();
   }

   public void onLoad() {
      super.onLoad();
      if (!this.capsActive) {
         this.reviveCaps();
      }

      if (this.f_58857_ instanceof ServerLevel server && !this.m_58901_()) {
         ControllerStructure.register(server, this);
      }
   }

   public void m_7651_() {
      if (this.f_58857_ instanceof ServerLevel server) {
         ControllerStructure.unregister(server, this);
      }

      super.m_7651_();
   }

   public void onChunkUnloaded() {
      if (this.f_58857_ instanceof ServerLevel server) {
         ControllerStructure.unregister(server, this);
      }

      super.onChunkUnloaded();
   }

   public void m_142466_(CompoundTag tag) {
      super.m_142466_(tag);
      int energy = Mth.m_14045_(tag.m_128451_("Energy"), 0, 18000000);
      if (this.localEnergy != energy) {
         this.localEnergy = energy;
         if (this.f_58857_ instanceof ServerLevel server) {
            ControllerStructure.reloadEnergy(server, this);
         }
      }
   }

   protected void m_183515_(CompoundTag tag) {
      super.m_183515_(tag);
      tag.m_128405_("Energy", this.localEnergy);
   }

   public CompoundTag m_5995_() {
      return this.m_187482_();
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.m_195640_(this);
   }

   @NotNull
   public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
      if (cap != ForgeCapabilities.ENERGY) {
         return super.getCapability(cap, side);
      } else {
         return this.capsActive && !this.m_58901_() ? this.energyCap.cast() : LazyOptional.empty();
      }
   }

   public void invalidateCaps() {
      this.capsActive = false;
      super.invalidateCaps();
      this.energyCap.invalidate();
   }

   public void reviveCaps() {
      super.reviveCaps();
      if (!this.capsActive) {
         this.energyCap = LazyOptional.of(() -> new ControllerBlockEntity.EnergyView());
         this.capsActive = true;
      }
   }

   public InteractionResult handleEnergyInteraction(ItemStack stack) {
      if (this.network != null && this.network.available() && this.capsActive) {
         IEnergyStorage item = (IEnergyStorage)stack.getCapability(ForgeCapabilities.ENERGY).resolve().orElse(null);
         return item != null && this.network.interact(item) ? InteractionResult.SUCCESS : InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

   private final class EnergyView implements IEnergyStorage {
      private final LazyOptional<IEnergyStorage> owner = ControllerBlockEntity.this.energyCap;

      private boolean active() {
         return ControllerBlockEntity.this.capsActive && ControllerBlockEntity.this.energyCap == this.owner && !ControllerBlockEntity.this.m_58901_();
      }

      public boolean canReceive() {
         return this.canExtract();
      }

      public boolean canExtract() {
         return this.active() && ControllerBlockEntity.this.network != null && ControllerBlockEntity.this.network.available();
      }

      public int getEnergyStored() {
         if (!this.active()) {
            return 0;
         } else {
            return ControllerBlockEntity.this.network != null
                  && ControllerBlockEntity.this.network.validated()
                  && ControllerBlockEntity.this.network.capacity() != 0L
               ? (int)Math.min(2147483647L, ControllerBlockEntity.this.network.energy())
               : ControllerBlockEntity.this.localEnergy;
         }
      }

      public int getMaxEnergyStored() {
         if (!this.active()) {
            return 0;
         } else {
            return ControllerBlockEntity.this.network != null && ControllerBlockEntity.this.network.validated()
               ? (int)Math.min(2147483647L, Math.max(18000000L, ControllerBlockEntity.this.network.capacity()))
               : 18000000;
         }
      }

      public int receiveEnergy(int amount, boolean simulate) {
         return this.canReceive() ? ControllerBlockEntity.this.network.receive(amount, simulate) : 0;
      }

      public int extractEnergy(int amount, boolean simulate) {
         return this.canExtract() ? ControllerBlockEntity.this.network.extract(amount, simulate) : 0;
      }
   }
}
