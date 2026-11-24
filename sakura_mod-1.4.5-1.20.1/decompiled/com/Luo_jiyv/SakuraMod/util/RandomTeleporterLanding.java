package com.Luo_jiyv.SakuraMod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

final class RandomTeleporterLanding {
   private final int x;
   private final int z;
   private final int minY;
   private final int maxY;
   private final int mode;
   private final int centerY;
   private int y;
   private int endY;
   private int direction;
   private boolean secondPass;
   private boolean done;

   RandomTeleporterLanding(ServerLevel level, BlockPos destination, int playerY, int mode) {
      this.x = destination.m_123341_();
      this.z = destination.m_123343_();
      this.minY = level.m_141937_() + 1;
      this.maxY = level.m_151558_() - 2;
      this.centerY = Mth.m_14045_(playerY, this.minY, this.maxY);
      this.mode = mode;
      switch (mode) {
         case 1:
            this.scan(this.maxY, this.minY, -1);
            break;
         case 2:
            this.scan(this.minY, this.maxY, 1);
            break;
         case 3:
            this.scan(this.centerY, this.maxY, 1);
            break;
         default:
            this.scan(Math.min(this.centerY + 20, this.maxY), Math.max(this.centerY - 20, this.minY), -1);
      }
   }

   @Nullable
   BlockPos step(ChunkAccess chunk) {
      BlockPos pos = new BlockPos(this.x, this.y, this.z);
      if (isSafe(chunk, pos)) {
         return pos;
      } else {
         if (this.y != this.endY) {
            this.y = this.y + this.direction;
         } else if (!this.secondPass && this.mode != 2 && this.mode != 1) {
            this.secondPass = true;
            if (this.mode != 3) {
               this.scan(this.maxY, this.minY, -1);
            } else if (this.centerY > this.minY) {
               this.scan(this.centerY - 1, this.minY, -1);
            } else {
               this.done = true;
            }
         } else {
            this.done = true;
         }

         return null;
      }
   }

   boolean isDone() {
      return this.done;
   }

   private void scan(int from, int to, int direction) {
      this.y = from;
      this.endY = to;
      this.direction = direction;
   }

   static boolean isSafe(ChunkAccess chunk, BlockPos pos) {
      BlockState below = chunk.m_8055_(pos.m_7495_());
      if (below.m_280296_() && !below.m_60713_(Blocks.f_50752_)) {
         BlockState at = chunk.m_8055_(pos);
         BlockState above = chunk.m_8055_(pos.m_7494_());
         if (!at.m_280296_() && !above.m_280296_()) {
            FluidState atFluid = at.m_60819_();
            FluidState aboveFluid = above.m_60819_();
            return (atFluid.m_76178_() || atFluid.m_205070_(FluidTags.f_13131_)) && (aboveFluid.m_76178_() || aboveFluid.m_205070_(FluidTags.f_13131_));
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   static void teleport(ServerLevel level, ServerPlayer player, BlockPos safe) {
      double oldX = player.m_20185_();
      double oldY = player.m_20186_();
      double oldZ = player.m_20189_();
      double x = safe.m_123341_() + 0.5;
      double z = safe.m_123343_() + 0.5;
      player.m_8999_(level, x, safe.m_123342_(), z, player.m_146908_(), player.m_146909_());
      level.m_6263_(null, oldX, oldY, oldZ, SoundEvents.f_11852_, SoundSource.PLAYERS, 1.0F, 1.0F);
      level.m_6263_(null, x, safe.m_123342_(), z, SoundEvents.f_11852_, SoundSource.PLAYERS, 1.0F, 1.0F);
   }
}
