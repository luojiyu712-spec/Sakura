package com.Luo_jiyv.SakuraMod.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FoxAnvilBlockEntity extends BlockEntity {
   private static final String KEY_USES = "Uses";
   private int uses = 25;

   public FoxAnvilBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.NEOFORGE.get(), pos, state);
   }

   public int getUses() {
      return this.uses;
   }

   public void setUses(int uses) {
      this.uses = Math.max(0, Math.min(25, uses));
      this.m_6596_();
   }

   protected void m_183515_(CompoundTag tag) {
      super.m_183515_(tag);
      tag.m_128405_("Uses", this.uses);
   }

   public void m_142466_(CompoundTag tag) {
      super.m_142466_(tag);
      if (tag.m_128441_("Uses")) {
         this.uses = Math.max(0, Math.min(25, tag.m_128451_("Uses")));
      }
   }
}
