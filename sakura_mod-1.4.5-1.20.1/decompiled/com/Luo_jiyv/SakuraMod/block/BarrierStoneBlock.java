package com.Luo_jiyv.SakuraMod.block;

import com.Luo_jiyv.SakuraMod.blockentity.BarrierStoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class BarrierStoneBlock extends BaseEntityBlock {
   public BarrierStoneBlock(Properties properties) {
      super(properties);
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new BarrierStoneBlockEntity(pos, state);
   }

   public RenderShape m_7514_(BlockState state) {
      return RenderShape.MODEL;
   }
}
