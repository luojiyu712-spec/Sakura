package com.Luo_jiyv.SakuraMod.block;

import com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntity;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;

public class MolecularAssemblerBlockCreate extends MolecularAssemblerBlock implements IWrenchable {
   public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
      if (context.m_43725_() instanceof ServerLevel level) {
         BlockPos var8 = context.m_8083_();
         Player player = context.m_43723_();
         if (player == null
            || !player.m_36326_()
            || !level.m_7966_(player, var8)
            || !(level.m_7702_(var8) instanceof MolecularAssemblerBlockEntity be && be.canConfigure(player))) {
            return InteractionResult.FAIL;
         } else if (!MinecraftForge.EVENT_BUS.post(new BreakEvent(level, var8, state, player)) && level.m_7702_(var8) == be) {
            List<ItemStack> drops = player.m_7500_() ? List.of() : Block.m_49874_(state, level, var8, be, player, context.m_43722_());
            be.dumpToPlayer(player);
            if (!level.m_46953_(var8, false, player)) {
               return InteractionResult.FAIL;
            } else {
               drops.forEach(player.m_150109_()::m_150079_);
               IWrenchable.playRemoveSound(level, var8);
               return InteractionResult.SUCCESS;
            }
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }
}
