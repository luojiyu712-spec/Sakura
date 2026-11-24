package com.Luo_jiyv.SakuraMod.blockentity;

import com.Luo_jiyv.SakuraMod.util.BarrierStoneCache;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BarrierStoneBlockEntity extends BlockEntity {
   public BarrierStoneBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.BARRIER_STONE.get(), pos, state);
   }

   public void onLoad() {
      super.onLoad();
      if (this.f_58857_ instanceof ServerLevel sl) {
         BarrierStoneCache.register(sl.m_46472_(), this.f_58858_);
      }
   }

   public void m_7651_() {
      if (this.f_58857_ instanceof ServerLevel sl) {
         BarrierStoneCache.unregister(sl.m_46472_(), this.f_58858_);
      }

      super.m_7651_();
   }
}
