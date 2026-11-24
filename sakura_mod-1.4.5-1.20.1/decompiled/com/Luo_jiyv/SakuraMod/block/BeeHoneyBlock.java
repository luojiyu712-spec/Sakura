package com.Luo_jiyv.SakuraMod.block;

import com.Luo_jiyv.SakuraMod.blockentity.BeeHoneyBlockEntity;
import com.Luo_jiyv.SakuraMod.blockentity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class BeeHoneyBlock extends AbstractMachineBlock<BeeHoneyBlockEntity> {
   public BeeHoneyBlock(Properties properties) {
      super(properties);
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new BeeHoneyBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState state, BlockEntityType<T> type) {
      return level.m_5776_() ? null : m_152132_(type, (BlockEntityType)ModBlockEntities.BEE_HONEY_BLOCK.get(), BeeHoneyBlockEntity::serverTick);
   }
}
