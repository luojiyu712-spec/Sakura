package com.Luo_jiyv.SakuraMod.block;

import com.Luo_jiyv.SakuraMod.blockentity.IDroppable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public abstract class AbstractMachineBlock<BE extends BlockEntity & MenuProvider> extends BaseEntityBlock {
   protected AbstractMachineBlock(Properties properties) {
      super(properties);
   }

   public RenderShape m_7514_(BlockState state) {
      return RenderShape.MODEL;
   }

   public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (!level.m_5776_() && player instanceof ServerPlayer sp && level.m_7702_(pos) instanceof MenuProvider provider) {
         NetworkHooks.openScreen(sp, provider, pos);
      }

      return InteractionResult.m_19078_(level.m_5776_());
   }

   public void m_6810_(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.m_60713_(newState.m_60734_()) && !level.f_46443_ && level.m_7702_(pos) instanceof IDroppable d) {
         d.drops();
      }

      super.m_6810_(state, level, pos, newState, isMoving);
   }
}
