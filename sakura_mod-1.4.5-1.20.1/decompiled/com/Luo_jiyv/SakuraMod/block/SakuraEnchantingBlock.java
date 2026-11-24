package com.Luo_jiyv.SakuraMod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SakuraEnchantingBlock extends Block {
   public SakuraEnchantingBlock() {
      super(Properties.m_60926_(Blocks.f_50721_));
   }

   public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
      return 30.0F;
   }
}
