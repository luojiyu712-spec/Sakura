package com.Luo_jiyv.SakuraMod.item;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DispelTalismanItem extends Item {
   public static final double BEAM_RANGE = 32.0;
   public static final double BEAM_RADIUS = 1.5;

   public DispelTalismanItem(Properties properties) {
      super(properties.m_41503_(10));
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (level.m_5776_()) {
         return InteractionResultHolder.m_19090_(stack);
      } else {
         Vec3 eye = player.m_146892_();
         Vec3 look = player.m_20154_().m_82541_();
         LivingEntity target = findNearestTargetInLine(level, player, eye, look);
         spawnRuneBeam(level, eye, look);
         if (target != null) {
            clearAllEffects(target);
            level.m_5594_(null, target.m_20183_(), SoundEvents.f_11862_, SoundSource.PLAYERS, 1.0F, 1.6F);
         }

         stack.m_41622_(1, player, p -> p.m_21190_(hand));
         return InteractionResultHolder.m_19090_(stack);
      }
   }

   private static LivingEntity findNearestTargetInLine(Level level, Player player, Vec3 eye, Vec3 look) {
      LivingEntity nearest = null;
      double nearestDistSq = Double.MAX_VALUE;
      AABB searchBox = new AABB(eye.f_82479_ - 32.0, eye.f_82480_ - 32.0, eye.f_82481_ - 32.0, eye.f_82479_ + 32.0, eye.f_82480_ + 32.0, eye.f_82481_ + 32.0);

      for (Entity entity : level.m_45933_(player, searchBox)) {
         if (entity instanceof LivingEntity living && living != player && !living.m_5833_()) {
            Vec3 center = living.m_20182_().m_82520_(0.0, living.m_20206_() * 0.5, 0.0);
            Vec3 toCenter = center.m_82546_(eye);
            double proj = toCenter.m_82526_(look);
            if (!(proj < 0.0) && !(proj > 32.0)) {
               Vec3 closest = eye.m_82549_(look.m_82490_(proj));
               double radius = 1.5 + living.m_20205_() * 0.5;
               if (!(center.m_82557_(closest) > radius * radius)) {
                  double distSq = proj * proj;
                  if (distSq < nearestDistSq) {
                     nearestDistSq = distSq;
                     nearest = living;
                  }
               }
            }
         }
      }

      return nearest;
   }

   private static void clearAllEffects(LivingEntity target) {
      for (MobEffectInstance effect : new ArrayList(target.m_21220_())) {
         target.m_21195_(effect.m_19544_());
      }
   }

   private static void spawnRuneBeam(Level level, Vec3 eye, Vec3 look) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 up = Math.abs(look.f_82480_) > 0.99 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
         Vec3 right = look.m_82537_(up).m_82541_();
         Vec3 realUp = right.m_82537_(look).m_82541_();
         double step = 0.12;

         for (double d = 0.5; d <= 32.0; d += step) {
            Vec3 p = eye.m_82549_(look.m_82490_(d));
            serverLevel.m_8767_(ParticleTypes.f_123809_, p.f_82479_, p.f_82480_, p.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
            double radius = 0.05 + 0.03 * Math.sin(d * 3.5);
            double angle = d * 5.0;
            Vec3 offset = right.m_82490_(Math.cos(angle) * radius).m_82549_(realUp.m_82490_(Math.sin(angle) * radius));
            Vec3 rp = p.m_82549_(offset);
            serverLevel.m_8767_(ParticleTypes.f_123809_, rp.f_82479_, rp.f_82480_, rp.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
         }

         Vec3 end = eye.m_82549_(look.m_82490_(32.0));
         serverLevel.m_8767_(ParticleTypes.f_123810_, end.f_82479_, end.f_82480_, end.f_82481_, 2, 0.05, 0.05, 0.05, 0.0);
      }
   }

   public boolean m_8120_(ItemStack stack) {
      return false;
   }

   public boolean m_5812_(ItemStack stack) {
      return true;
   }

   public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(Component.m_237115_("item.sakura_mod.dispel_talisman.tooltip.1").m_130940_(ChatFormatting.GRAY));
      tooltip.add(Component.m_237115_("item.sakura_mod.dispel_talisman.tooltip.2").m_130940_(ChatFormatting.DARK_GRAY));
      super.m_7373_(stack, level, tooltip, flag);
   }
}
