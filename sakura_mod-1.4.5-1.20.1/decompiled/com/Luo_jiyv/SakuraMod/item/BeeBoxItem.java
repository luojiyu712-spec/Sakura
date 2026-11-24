package com.Luo_jiyv.SakuraMod.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class BeeBoxItem extends Item {
   public static final String TAG_COUNT = "BeeCount";
   public static final int MAX_COUNT = 64;

   public BeeBoxItem(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (!player.m_6144_()) {
         return InteractionResultHolder.m_19098_(stack);
      } else {
         HitResult hit = player.m_19907_(5.0, 0.0F, false);
         if (hit.m_6662_() == Type.BLOCK) {
            return InteractionResultHolder.m_19098_(stack);
         } else {
            if (!level.m_5776_()) {
               int count = getCount(stack);
               if (count > 0) {
                  if (!ReleaseHelper.spawn(EntityType.f_20550_, level, player, ReleaseHelper.findReleasePos(player))) {
                     return InteractionResultHolder.m_19100_(stack);
                  }

                  setCount(stack, count - 1);
                  return InteractionResultHolder.m_19090_(stack);
               }
            }

            return InteractionResultHolder.m_19098_(stack);
         }
      }
   }

   public InteractionResult m_6225_(UseOnContext context) {
      Level level = context.m_43725_();
      Player player = context.m_43723_();
      ItemStack stack = context.m_43722_();
      if (player == null || level.m_5776_()) {
         return InteractionResult.PASS;
      } else if (!player.m_6144_()) {
         return InteractionResult.PASS;
      } else if (getCount(stack) <= 0) {
         return InteractionResult.PASS;
      } else {
         BlockPos spawnPos = context.m_8083_().m_121945_(context.m_43719_());
         if (!ReleaseHelper.spawn(EntityType.f_20550_, level, player, Vec3.m_82539_(spawnPos))) {
            return InteractionResult.FAIL;
         } else {
            setCount(stack, getCount(stack) - 1);
            return InteractionResult.SUCCESS;
         }
      }
   }

   public static int getCount(ItemStack stack) {
      CompoundTag tag = stack.m_41783_();
      return tag == null ? 0 : tag.m_128451_("BeeCount");
   }

   public static void setCount(ItemStack stack, int count) {
      stack.m_41784_().m_128405_("BeeCount", Math.max(0, count));
   }

   public static boolean canStore(ItemStack stack) {
      return getCount(stack) < 64;
   }

   public static float getModelState(ItemStack stack) {
      int count = getCount(stack);
      if (count <= 0) {
         return 0.0F;
      } else if (count == 64) {
         return 3.0F;
      } else {
         return count >= 32 ? 2.0F : 1.0F;
      }
   }

   public boolean m_5812_(ItemStack stack) {
      return false;
   }

   public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
      int count = getCount(stack);
      tooltip.add(Component.m_237110_("item.sakura_mod.tooltip.bee_count", new Object[]{count, 64}).m_130940_(ChatFormatting.GRAY));
      if (count > 0) {
         tooltip.add(Component.m_237115_("item.sakura_mod.tooltip.release_bee").m_130940_(ChatFormatting.DARK_GRAY));
      } else {
         tooltip.add(Component.m_237115_("item.sakura_mod.tooltip.capture_bee").m_130940_(ChatFormatting.DARK_GRAY));
      }

      super.m_7373_(stack, level, tooltip, flag);
   }
}
