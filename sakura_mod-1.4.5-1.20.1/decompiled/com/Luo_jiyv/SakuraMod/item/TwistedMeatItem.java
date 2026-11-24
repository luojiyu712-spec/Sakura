package com.Luo_jiyv.SakuraMod.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class TwistedMeatItem extends Item {
   private final boolean cooked;

   public TwistedMeatItem(boolean cooked, Properties properties) {
      super(properties);
      this.cooked = cooked;
   }

   public int m_8105_(ItemStack stack) {
      return 10;
   }

   public UseAnim m_6164_(ItemStack stack) {
      return UseAnim.EAT;
   }

   public void m_7373_(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(
         Component.m_237115_(this.cooked ? "item.sakura_mod.twisted_meat_cooked.desc" : "item.sakura_mod.twisted_meat_raw.desc").m_130940_(ChatFormatting.GRAY)
      );
   }
}
