package com.Luo_jiyv.SakuraMod.block;

import com.Luo_jiyv.SakuraMod.blockentity.ModBlockEntities;
import com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MolecularAssemblerBlock extends AbstractMachineBlock<MolecularAssemblerBlockEntity> {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.f_54117_;
   public static final BooleanProperty LIT = BooleanProperty.m_61465_("lit");

   public MolecularAssemblerBlock() {
      super(Properties.m_284310_().m_284180_(MapColor.f_283906_).m_60913_(3.5F, 8.0F).m_60918_(SoundType.f_56743_).m_60999_().m_60955_());
      this.m_49959_((BlockState)((BlockState)this.m_49966_().m_61124_(FACING, Direction.NORTH)).m_61124_(LIT, false));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{FACING, LIT});
   }

   public BlockState m_5573_(BlockPlaceContext context) {
      return (BlockState)((BlockState)this.m_49966_().m_61124_(FACING, context.m_8125_().m_122424_())).m_61124_(LIT, false);
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return AssemblerBlockFactory.createEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState state, BlockEntityType<T> type) {
      return level.f_46443_ ? null : m_152132_(type, (BlockEntityType)ModBlockEntities.MOLECULAR_ASSEMBLER.get(), MolecularAssemblerBlockEntity::tick);
   }

   @Override
   public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (!level.m_5776_() && player.m_6144_() && player.m_21120_(hand).m_41619_() && level.m_7702_(pos) instanceof MolecularAssemblerBlockEntity be) {
         if (be.canConfigure(player)) {
            be.dumpToPlayer(player);
         }

         return InteractionResult.CONSUME;
      } else {
         return super.m_6227_(state, level, pos, player, hand, hit);
      }
   }

   public void m_6861_(BlockState state, Level level, BlockPos pos, Block neighbour, BlockPos neighbourPos, boolean moving) {
      if (!level.f_46443_ && level.m_7702_(pos) instanceof MolecularAssemblerBlockEntity be) {
         be.neighboursChanged();
      }

      super.m_6861_(state, level, pos, neighbour, neighbourPos, moving);
   }

   public int m_7753_(BlockState state, BlockGetter level, BlockPos pos) {
      return 0;
   }
}
