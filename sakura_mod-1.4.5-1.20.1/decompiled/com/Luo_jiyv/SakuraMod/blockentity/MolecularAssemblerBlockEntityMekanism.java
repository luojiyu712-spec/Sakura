package com.Luo_jiyv.SakuraMod.blockentity;

import mekanism.api.IConfigurable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MolecularAssemblerBlockEntityMekanism extends MolecularAssemblerBlockEntity implements IConfigurable {
   public MolecularAssemblerBlockEntityMekanism(BlockPos pos, BlockState state) {
      super(pos, state);
   }

   public InteractionResult onRightClick(Player player) {
      if (this.f_58857_ == null || this.f_58857_.f_46443_) {
         return InteractionResult.SUCCESS;
      } else if (!this.canConfigure(player)) {
         return InteractionResult.PASS;
      } else {
         Vec3 look = player.m_20154_();
         Direction face = Direction.m_122366_(-look.f_82479_, -look.f_82480_, -look.f_82481_);
         this.cycleSideConfig(face.ordinal());
         return InteractionResult.SUCCESS;
      }
   }

   public InteractionResult onSneakRightClick(Player player) {
      return InteractionResult.PASS;
   }
}
