package com.Luo_jiyv.SakuraMod.block;

import com.Luo_jiyv.SakuraMod.blockentity.ControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

public class ControllerBlock extends BaseEntityBlock {
   public static final EnumProperty<ControllerState> STATE = EnumProperty.m_61587_("state", ControllerState.class);
   public static final EnumProperty<ControllerType> TYPE = EnumProperty.m_61587_("type", ControllerType.class);

   public ControllerBlock(Properties properties) {
      super(properties);
      BlockState state = (BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(STATE, ControllerState.OFFLINE))
         .m_61124_(TYPE, ControllerType.BLOCK);
      this.m_49959_(state);
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{STATE});
      builder.m_61104_(new Property[]{TYPE});
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new ControllerBlockEntity(pos, state);
   }

   public RenderShape m_7514_(BlockState state) {
      return RenderShape.MODEL;
   }

   public void m_6861_(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos from, boolean moved) {
      if (!level.f_46443_ && !(neighbor instanceof ControllerBlock) && level.m_7702_(pos) instanceof ControllerBlockEntity be && be.network() != null) {
         be.network().wakeOutput();
      }
   }

   public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      ItemStack stack = player.m_21120_(hand);
      if (stack.getCapability(ForgeCapabilities.ENERGY).resolve().isEmpty()) {
         return InteractionResult.PASS;
      } else if (level.f_46443_) {
         return InteractionResult.SUCCESS;
      } else {
         return level.m_7702_(pos) instanceof ControllerBlockEntity be ? be.handleEnergyInteraction(stack) : InteractionResult.PASS;
      }
   }
}
