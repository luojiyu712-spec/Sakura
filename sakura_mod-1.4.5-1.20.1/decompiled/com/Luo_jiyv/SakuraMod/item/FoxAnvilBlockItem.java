package com.Luo_jiyv.SakuraMod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FoxAnvilBlockItem extends BlockItem {
   public FoxAnvilBlockItem(Block block, Properties properties) {
      super(block, properties);
   }

   protected SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, Player player) {
      return SoundEvents.f_11944_;
   }
}
