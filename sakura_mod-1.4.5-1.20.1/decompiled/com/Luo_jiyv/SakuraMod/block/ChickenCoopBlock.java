package com.Luo_jiyv.SakuraMod.block;

import com.Luo_jiyv.SakuraMod.blockentity.ChickenCoopBlockEntity;
import com.Luo_jiyv.SakuraMod.blockentity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class ChickenCoopBlock extends AbstractMachineBlock<ChickenCoopBlockEntity> {
   public ChickenCoopBlock(Properties properties) {
      super(properties);
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new ChickenCoopBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState state, BlockEntityType<T> type) {
      return level.m_5776_() ? null : m_152132_(type, (BlockEntityType)ModBlockEntities.CHICKEN_COOP.get(), ChickenCoopBlockEntity::serverTick);
   }
}
