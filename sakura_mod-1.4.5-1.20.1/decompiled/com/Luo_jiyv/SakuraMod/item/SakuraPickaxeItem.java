package com.Luo_jiyv.SakuraMod.item;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags.Blocks;

public final class SakuraPickaxeItem extends PickaxeItem {
   private static final String VEIN_MINING_TAG = "SakuraVeinMining";
   private static final int MAX_VEIN_SIZE = 128;
   private final Set<ServerPlayer> miningVein = new HashSet<>();

   public SakuraPickaxeItem(Tier tier, int damage, float attackSpeed, Properties properties) {
      super(tier, damage, attackSpeed, properties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (player.m_6144_() && !player.m_5833_()) {
         if (!level.f_46443_) {
            toggleVeinMining(player, stack);
         }

         return InteractionResultHolder.m_19092_(stack, level.f_46443_);
      } else {
         return InteractionResultHolder.m_19098_(stack);
      }
   }

   public InteractionResult m_6225_(UseOnContext context) {
      Player player = context.m_43723_();
      if (player != null && player.m_6144_() && !player.m_5833_()) {
         if (!context.m_43725_().f_46443_) {
            toggleVeinMining(player, context.m_43722_());
         }

         return InteractionResult.m_19078_(context.m_43725_().f_46443_);
      } else {
         return InteractionResult.PASS;
      }
   }

   private static void toggleVeinMining(Player player, ItemStack stack) {
      boolean enabled = !isVeinMiningEnabled(stack);
      if (enabled) {
         stack.m_41784_().m_128379_("SakuraVeinMining", true);
      } else {
         stack.m_41749_("SakuraVeinMining");
      }

      player.m_5661_(Component.m_237115_(enabled ? "item.sakura_mod.sakura_pickaxe.vein.on" : "item.sakura_mod.sakura_pickaxe.vein.off"), true);
   }

   public static boolean isVeinMiningEnabled(ItemStack stack) {
      CompoundTag tag = stack.m_41783_();
      return tag != null && tag.m_128471_("SakuraVeinMining");
   }

   public boolean m_6813_(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
      if (miner instanceof ServerPlayer player) {
         if (this.miningVein.contains(player)) {
            return true;
         }

         this.mineVein(stack, player, pos, state);
      }

      return super.m_6813_(stack, level, state, pos, miner);
   }

   public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player miner) {
      if (miner instanceof ServerPlayer player && player.m_7500_()) {
         this.mineVein(stack, player, pos, player.m_284548_().m_8055_(pos));
      }

      return false;
   }

   private void mineVein(ItemStack stack, ServerPlayer player, BlockPos origin, BlockState ore) {
      ServerLevel level = player.m_284548_();
      if (isVeinMiningEnabled(stack)
         && player.m_21205_() == stack
         && ore.m_204336_(Blocks.ORES)
         && level.m_7966_(player, origin)
         && level.m_6857_().m_61937_(origin)
         && (player.m_7500_() || ore.canHarvestBlock(level, origin, player))
         && this.miningVein.add(player)) {
         try {
            List<TagKey<Block>> oreTypes = ore.m_204343_()
               .filter(tag -> tag.f_203868_().m_135827_().equals("forge") && tag.f_203868_().m_135815_().startsWith("ores/"))
               .toList();
            Set<BlockPos> visited = new HashSet<>();
            ArrayDeque<BlockPos> queue = new ArrayDeque<>();
            visited.add(origin);
            queue.add(origin);
            int mined = 1;

            while (!queue.isEmpty()) {
               BlockPos from = queue.removeFirst();

               for (BlockPos cursor : BlockPos.m_121940_(from.m_7918_(-1, -1, -1), from.m_7918_(1, 1, 1))) {
                  if (player.m_21205_() != stack || stack.m_41619_()) {
                     return;
                  }

                  BlockPos neighbor = cursor.m_7949_();
                  if (level.m_46739_(neighbor) && level.m_46805_(neighbor) && visited.add(neighbor)) {
                     BlockState state = level.m_8055_(neighbor);
                     if (state.m_204336_(Blocks.ORES)
                        && (state.m_60713_(ore.m_60734_()) || !oreTypes.stream().noneMatch(state::m_204336_))
                        && !(state.m_60800_(level, neighbor) < 0.0F)
                        && level.m_6857_().m_61937_(neighbor)
                        && level.m_7966_(player, neighbor)
                        && (player.m_7500_() || state.canHarvestBlock(level, neighbor, player))
                        && player.f_8941_.m_9280_(neighbor)
                        && !level.m_8055_(neighbor).m_60713_(state.m_60734_())) {
                        level.m_8767_(
                           ParticleTypes.f_276452_,
                           (from.m_123341_() + neighbor.m_123341_() + 1) * 0.5,
                           (from.m_123342_() + neighbor.m_123342_() + 1) * 0.5,
                           (from.m_123343_() + neighbor.m_123343_() + 1) * 0.5,
                           3,
                           0.18,
                           0.18,
                           0.18,
                           0.01
                        );
                        if (++mined >= 128) {
                           return;
                        }

                        queue.add(neighbor);
                     }
                  }
               }
            }
         } finally {
            this.miningVein.remove(player);
         }
      }
   }
}
