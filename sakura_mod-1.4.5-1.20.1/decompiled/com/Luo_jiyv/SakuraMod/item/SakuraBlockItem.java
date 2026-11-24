package com.Luo_jiyv.SakuraMod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

public class SakuraBlockItem extends BlockItem {
   private static final FoodProperties FOOD = new Builder()
      .m_38760_(20)
      .m_38758_(0.5F)
      .m_38765_()
      .effect(() -> new MobEffectInstance(MobEffects.f_19605_, 1200, 4), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.f_19606_, 6000, 3), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.f_19607_, 6000, 0), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.f_19617_, 2400, 3), 1.0F)
      .m_38767_();

   public SakuraBlockItem(Block block, Properties properties) {
      super(block, properties.m_41489_(FOOD));
   }

   public boolean m_5812_(ItemStack stack) {
      return true;
   }
}
