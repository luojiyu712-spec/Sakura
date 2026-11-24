package com.Luo_jiyv.SakuraMod.block;

import com.Luo_jiyv.SakuraMod.blockentity.BlazingRockBlockEntity;
import com.Luo_jiyv.SakuraMod.blockentity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class BlazingRockBlock extends BaseEntityBlock {
   static Block createForRegistration() {
      Properties properties = Properties.m_284310_()
         .m_284180_(MapColor.f_283820_)
         .m_60913_(3.0F, 6.0F)
         .m_60918_(SoundType.f_154663_)
         .m_60953_(state -> 15)
         .m_60955_()
         .m_60999_();
      return (Block)(ModList.get().isLoaded("create") ? CreateWrenchBlocks.blazingRock(properties) : new BlazingRockBlock(properties));
   }

   public BlazingRockBlock(Properties properties) {
      super(properties);
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new BlazingRockBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState state, BlockEntityType<T> type) {
      return level.m_5776_() ? null : m_152132_(type, (BlockEntityType)ModBlockEntities.BLAZING_ROCK.get(), BlazingRockBlockEntity::serverTick);
   }

   public RenderShape m_7514_(BlockState state) {
      return RenderShape.MODEL;
   }

   public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      ItemStack stack = player.m_21120_(hand);
      if (!stack.m_150930_(Items.f_42446_)) {
         return super.m_6227_(state, level, pos, player, hand, hit);
      } else {
         if (!level.m_5776_()) {
            ItemStack lavaBucket = new ItemStack(Items.f_42448_);
            if (player.m_7500_()) {
               player.m_150109_().m_150079_(lavaBucket);
            } else {
               player.m_21008_(hand, ItemUtils.m_41813_(stack, player, lavaBucket));
            }

            level.m_5594_(null, pos, SoundEvents.f_11783_, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         return InteractionResult.m_19078_(level.m_5776_());
      }
   }
}
