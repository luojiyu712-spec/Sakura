package com.Luo_jiyv.SakuraMod.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.ForgeEventFactory;

public final class SakuraHoeItem extends HoeItem {
   private static final TagKey<Block> ADDITIONAL_CROPS = TagKey.m_203882_(
      Registries.f_256747_, ResourceLocation.fromNamespaceAndPath("sakura_mod", "hoe_crops")
   );
   private static final TagKey<Block> HANGING_CROPS = TagKey.m_203882_(
      Registries.f_256747_, ResourceLocation.fromNamespaceAndPath("sakura_mod", "hoe_hanging_crops")
   );
   private static final TagKey<Block> HANGING_CROP_SUPPORTS = TagKey.m_203882_(
      Registries.f_256747_, ResourceLocation.fromNamespaceAndPath("sakura_mod", "hoe_hanging_crop_supports")
   );
   private static final int MAX_COLUMN_HEIGHT = 3;

   public SakuraHoeItem(Tier tier, int damage, float attackSpeed, Properties properties) {
      super(tier, damage, attackSpeed, properties);
   }

   public InteractionResult m_6225_(UseOnContext context) {
      Player player = context.m_43723_();
      if (player != null && !player.m_5833_()) {
         Level level = context.m_43725_();
         BlockPos clicked = context.m_8083_();
         BlockState clickedState = level.m_8055_(clicked);
         if (!isCrop(level, clicked, clickedState)) {
            return super.m_6225_(context);
         } else {
            BlockPos center = cropBase(level, clicked, clickedState);
            int pairedHeight = clickedState.m_204336_(HANGING_CROP_SUPPORTS) ? -1 : (clickedState.m_204336_(HANGING_CROPS) ? 1 : 0);
            TagKey<Block> pairedCrop = pairedHeight < 0 ? HANGING_CROPS : HANGING_CROP_SUPPORTS;
            ItemStack hoe = context.m_43722_();
            boolean applied = false;

            for (BlockPos cursor : BlockPos.m_121940_(center.m_7918_(-1, 0, -1), center.m_7918_(1, 0, 1))) {
               BlockPos pos = cursor.m_7949_();
               if (tryFertilize(player, hoe, pos, null) || pairedHeight != 0 && tryFertilize(player, hoe, pos.m_7918_(0, pairedHeight, 0), pairedCrop)) {
                  if (level.f_46443_) {
                     return InteractionResult.SUCCESS;
                  }

                  applied = true;
               }
            }

            if (applied) {
               hoe.m_41622_(1, player, user -> user.m_21190_(context.m_43724_()));
               return InteractionResult.CONSUME;
            } else {
               return super.m_6225_(context);
            }
         }
      } else {
         return super.m_6225_(context);
      }
   }

   private static boolean isCrop(Level level, BlockPos pos, BlockState state) {
      Block block = state.m_60734_();
      return !(block instanceof NetherWartBlock)
            && !isColumnCrop(block)
            && !state.m_204336_(BlockTags.f_13073_)
            && !state.m_204336_(ADDITIONAL_CROPS)
            && !(block instanceof IPlantable plant && plant.getPlantType(level, pos) == PlantType.CROP)
         ? block instanceof BonemealableBlock crop
            && !(block instanceof SaplingBlock)
            && !state.m_204336_(BlockTags.f_13104_)
            && block.m_49965_().m_61081_("age") instanceof IntegerProperty
            && crop.m_7370_(level, pos, state, level.f_46443_)
         : true;
   }

   private static boolean isColumnCrop(Block block) {
      return block instanceof CactusBlock || block instanceof SugarCaneBlock;
   }

   private static BlockPos cropBase(Level level, BlockPos pos, BlockState state) {
      Block block = state.m_60734_();
      if (state.m_61138_(BlockStateProperties.f_61401_) && state.m_61143_(BlockStateProperties.f_61401_) == DoubleBlockHalf.UPPER) {
         BlockPos lower = pos.m_7495_();
         BlockState lowerState = level.m_8055_(lower);
         if (lowerState.m_60713_(block) && lowerState.m_61143_(BlockStateProperties.f_61401_) == DoubleBlockHalf.LOWER) {
            return lower;
         }
      }

      if (isColumnCrop(block)) {
         for (int i = 1; i < 3 && level.m_8055_(pos.m_7495_()).m_60713_(block); i++) {
            pos = pos.m_7495_();
         }
      }

      return pos;
   }

   private static boolean canFertilize(Player player, ItemStack hoe, BlockPos pos) {
      return player.m_9236_().m_7966_(player, pos) && player.m_36204_(pos, Direction.UP, hoe);
   }

   private static boolean tryFertilize(Player player, ItemStack hoe, BlockPos pos, @Nullable TagKey<Block> requiredTag) {
      Level level = player.m_9236_();
      if (level.m_46739_(pos) && level.m_46805_(pos)) {
         BlockState state = level.m_8055_(pos);
         if (requiredTag == null ? isCrop(level, pos, state) : state.m_204336_(requiredTag)) {
            BlockPos base = cropBase(level, pos, state);
            if (canFertilize(player, hoe, base) && fertilize(player, hoe, base, level.m_8055_(base))) {
               if (!level.f_46443_) {
                  level.m_46796_(1505, base, 0);
               }

               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean fertilize(Player player, ItemStack hoe, BlockPos pos, BlockState state) {
      Level level = player.m_9236_();
      ItemStack fertilizer = new ItemStack(Items.f_42499_);
      if (!(state.m_60734_() instanceof NetherWartBlock)) {
         if (!isColumnCrop(state.m_60734_())) {
            return BoneMealItem.applyBonemeal(fertilizer, level, pos, player);
         } else if (level.m_8055_(pos.m_7495_()).m_60713_(state.m_60734_())) {
            return false;
         } else {
            BlockPos top = pos;

            for (int height = 1; level.m_8055_(top.m_7494_()).m_60713_(state.m_60734_()); top = top.m_7494_()) {
               if (++height >= 3) {
                  return false;
               }
            }

            BlockPos above = top.m_7494_();
            BlockState grown = state.m_60734_().m_49966_();
            if (level.m_46739_(above)
               && level.isAreaLoaded(above, 1)
               && level.m_46859_(above)
               && canFertilize(player, hoe, top)
               && canFertilize(player, hoe, above)
               && grown.m_60710_(level, above)) {
               BlockState topState = level.m_8055_(top);
               if (!grow(player, fertilizer, top, above, topState, grown)) {
                  return false;
               } else {
                  if (!level.f_46443_ && level.m_8055_(top) == topState) {
                     level.m_7731_(top, (BlockState)topState.m_61124_(BlockStateProperties.f_61410_, 0), 2);
                  }

                  return true;
               }
            } else {
               return false;
            }
         }
      } else {
         int age = (Integer)state.m_61143_(NetherWartBlock.f_54967_);
         return age < 3 && grow(player, fertilizer, pos, pos, state, (BlockState)state.m_61124_(NetherWartBlock.f_54967_, age + 1));
      }
   }

   private static boolean grow(Player player, ItemStack fertilizer, BlockPos pos, BlockPos destination, BlockState state, BlockState grown) {
      Level level = player.m_9236_();
      int result = ForgeEventFactory.onApplyBonemeal(player, level, pos, state, fertilizer);
      if (result != 0) {
         return result > 0;
      } else if (!(level instanceof ServerLevel)) {
         return true;
      } else if (!ForgeHooks.onCropsGrowPre(level, destination, state, true)) {
         return false;
      } else {
         BlockState previous = level.m_8055_(destination);
         if (!level.m_46597_(destination, grown)) {
            return false;
         } else {
            ForgeHooks.onCropsGrowPost(level, destination, previous);
            return true;
         }
      }
   }
}
