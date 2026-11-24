package com.Luo_jiyv.SakuraMod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.event.level.BlockEvent.BlockToolModificationEvent;

public final class SakuraShovelItem extends ShovelItem {
   private static final String GRASS_TAG = "SakuraGrassConversion";
   public static final ToolAction GRASS_CONVERSION = ToolAction.get("sakura_grass_conversion");

   public SakuraShovelItem(Tier tier, float damage, float attackSpeed, Properties properties) {
      super(tier, damage, attackSpeed, properties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (player.m_6144_() && !player.m_5833_()) {
         if (!level.f_46443_) {
            toggleGrassConversion(player, stack);
         }

         return InteractionResultHolder.m_19092_(stack, level.f_46443_);
      } else {
         return super.m_7203_(level, player, hand);
      }
   }

   public InteractionResult m_6225_(UseOnContext context) {
      Player player = context.m_43723_();
      if (player != null && !player.m_5833_()) {
         Level level = context.m_43725_();
         ItemStack stack = context.m_43722_();
         if (player.m_6144_()) {
            if (!level.f_46443_) {
               toggleGrassConversion(player, stack);
            }

            return InteractionResult.m_19078_(level.f_46443_);
         } else if (!isGrassConversionEnabled(stack)) {
            return super.m_6225_(context);
         } else {
            BlockPos center = context.m_8083_();
            boolean changed = false;

            for (BlockPos cursor : BlockPos.m_121940_(center.m_7918_(-2, 0, -2), center.m_7918_(2, 0, 2))) {
               BlockPos pos = cursor.m_7949_();
               if (level.m_46739_(pos)
                  && level.m_46805_(pos)
                  && level.m_6857_().m_61937_(pos)
                  && level.m_7966_(player, pos)
                  && player.m_36204_(pos, Direction.UP, stack)) {
                  BlockState state = level.m_8055_(pos);
                  if (state.m_60713_(Blocks.f_50493_)) {
                     if (level.f_46443_) {
                        return InteractionResult.SUCCESS;
                     }

                     BlockState grass = (BlockState)Blocks.f_50440_
                        .m_49966_()
                        .m_61124_(SnowyDirtBlock.f_56637_, level.m_8055_(pos.m_7494_()).m_204336_(BlockTags.f_144279_));
                     UseOnContext targetContext = new UseOnContext(
                        level, player, context.m_43724_(), stack, new BlockHitResult(Vec3.m_82512_(pos), Direction.UP, pos, false)
                     );
                     BlockToolModificationEvent event = new BlockToolModificationEvent(state, targetContext, GRASS_CONVERSION, false);
                     event.setFinalState(grass);
                     if (!MinecraftForge.EVENT_BUS.post(event)
                        && event.getFinalState() != null
                        && event.getFinalState() != state
                        && level.m_8055_(pos) == state
                        && level.m_46597_(pos, event.getFinalState())) {
                        level.m_220407_(GameEvent.f_157792_, pos, Context.m_223719_(player, event.getFinalState()));
                        changed = true;
                     }
                  }
               }
            }

            if (!changed) {
               return InteractionResult.PASS;
            } else {
               level.m_5594_(null, center, SoundEvents.f_11991_, SoundSource.BLOCKS, 1.0F, 1.0F);
               stack.m_41622_(1, player, user -> user.m_21190_(context.m_43724_()));
               return InteractionResult.CONSUME;
            }
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   public static boolean isGrassConversionEnabled(ItemStack stack) {
      CompoundTag tag = stack.m_41783_();
      return tag != null && tag.m_128471_("SakuraGrassConversion");
   }

   private static void toggleGrassConversion(Player player, ItemStack stack) {
      boolean enabled = !isGrassConversionEnabled(stack);
      if (enabled) {
         stack.m_41784_().m_128379_("SakuraGrassConversion", true);
      } else {
         stack.m_41749_("SakuraGrassConversion");
      }

      player.m_5661_(Component.m_237115_(enabled ? "item.sakura_mod.sakura_shovel.grass.on" : "item.sakura_mod.sakura_shovel.grass.off"), true);
   }

   public boolean canPerformAction(ItemStack stack, ToolAction action) {
      return action == GRASS_CONVERSION || super.canPerformAction(stack, action);
   }
}
