package com.Luo_jiyv.SakuraMod.block;

import com.Luo_jiyv.SakuraMod.blockentity.ModBlockEntities;
import com.Luo_jiyv.SakuraMod.blockentity.StringFarmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class StringFarmBlock extends AbstractMachineBlock<StringFarmBlockEntity> {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.f_54117_;

   public StringFarmBlock() {
      super(Properties.m_60926_(Blocks.f_50617_));
      this.m_49959_((BlockState)this.m_49966_().m_61124_(FACING, Direction.NORTH));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{FACING});
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      return (BlockState)this.m_49966_().m_61124_(FACING, context.m_8125_().m_122424_());
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new StringFarmBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState state, BlockEntityType<T> type) {
      return level.f_46443_ ? null : m_152132_(type, (BlockEntityType)ModBlockEntities.STRING_FARM.get(), StringFarmBlockEntity::tick);
   }
}
