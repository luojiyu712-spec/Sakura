package com.Luo_jiyv.SakuraMod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ReleaseHelper {
   private ReleaseHelper() {
   }

   static boolean spawn(EntityType<?> type, Level level, Player player, Vec3 pos) {
      Entity entity = type.m_20615_(level);
      if (entity != null) {
         entity.m_7678_(pos.f_82479_, pos.f_82480_, pos.f_82481_, level.f_46441_.m_188501_() * 360.0F, 0.0F);
         if (level.m_7967_(entity)) {
            return true;
         }
      }

      player.m_5661_(Component.m_237115_("item.sakura_mod.release_failed"), true);
      return false;
   }

   public static Vec3 findReleasePos(Player player) {
      Level level = player.m_9236_();
      BlockPos base = player.m_20183_();
      Direction facing = player.m_6350_();
      BlockPos front = base.m_121945_(facing);
      if (canPlaceAt(level, front)) {
         return Vec3.m_82539_(front);
      } else {
         BlockPos right = base.m_121945_(facing.m_122427_());
         if (canPlaceAt(level, right)) {
            return Vec3.m_82539_(right);
         } else {
            BlockPos left = base.m_121945_(facing.m_122428_());
            if (canPlaceAt(level, left)) {
               return Vec3.m_82539_(left);
            } else {
               BlockPos back = base.m_121945_(facing.m_122424_());
               return canPlaceAt(level, back) ? Vec3.m_82539_(back) : Vec3.m_82539_(base);
            }
         }
      }
   }

   private static boolean canPlaceAt(Level level, BlockPos pos) {
      if (!level.m_46805_(pos)) {
         return false;
      } else if (!level.m_8055_(pos).m_60795_()) {
         return false;
      } else {
         return !level.m_8055_(pos.m_7494_()).m_60795_() ? false : !level.m_8055_(pos.m_7495_()).m_60795_();
      }
   }
}
