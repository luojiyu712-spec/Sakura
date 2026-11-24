package com.Luo_jiyv.SakuraMod.menu;

import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

final class ClientMenuHelper {
   private ClientMenuHelper() {
   }

   static <T extends BlockEntity> T resolve(
      Inventory playerInv, BlockPos pos, Class<T> type, BiFunction<BlockPos, BlockState, T> factory, BlockState fallbackState
   ) {
      BlockEntity be = playerInv.f_35978_.m_9236_().m_7702_(pos);
      return type.isInstance(be) ? type.cast(be) : factory.apply(pos, fallbackState);
   }
}
