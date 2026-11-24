package com.Luo_jiyv.SakuraMod.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

final class CreateWrenchBlocks {
   private CreateWrenchBlocks() {
   }

   static Block blazingRock(Properties properties) {
      return new CreateWrenchBlocks.BlazingRock(properties);
   }

   static Block stringFarm() {
      return new CreateWrenchBlocks.StringFarm();
   }

   static Block chickenCoop(Properties properties) {
      return new CreateWrenchBlocks.ChickenCoop(properties);
   }

   static Block beeHoney(Properties properties) {
      return new CreateWrenchBlocks.BeeHoney(properties);
   }

   static Block cobbleGenerator(Properties properties) {
      return new CreateWrenchBlocks.CobbleGenerator(properties);
   }

   static Block controller(Properties properties) {
      return new CreateWrenchBlocks.Controller(properties);
   }

   private static final class BeeHoney extends BeeHoneyBlock implements CreateWrenchBlocks.Pickup {
      BeeHoney(Properties properties) {
         super(properties);
      }
   }

   private static final class BlazingRock extends BlazingRockBlock implements CreateWrenchBlocks.Pickup {
      BlazingRock(Properties properties) {
         super(properties);
      }
   }

   private static final class ChickenCoop extends ChickenCoopBlock implements CreateWrenchBlocks.Pickup {
      ChickenCoop(Properties properties) {
         super(properties);
      }
   }

   private static final class CobbleGenerator extends CobbleGeneratorBlock implements CreateWrenchBlocks.Pickup {
      CobbleGenerator(Properties properties) {
         super(properties);
      }
   }

   private static final class Controller extends ControllerBlock implements CreateWrenchBlocks.Pickup {
      Controller(Properties properties) {
         super(properties);
      }
   }

   private interface Pickup extends IWrenchable {
      default InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
         Player player = context.m_43723_();
         return player != null && player.m_36326_() && context.m_43725_().m_7966_(player, context.m_8083_())
            ? super.onSneakWrenched(state, context)
            : InteractionResult.FAIL;
      }
   }

   private static final class StringFarm extends StringFarmBlock implements CreateWrenchBlocks.Pickup {
   }
}
