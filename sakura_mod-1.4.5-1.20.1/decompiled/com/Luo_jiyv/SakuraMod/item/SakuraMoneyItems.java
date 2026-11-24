package com.Luo_jiyv.SakuraMod.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class SakuraMoneyItems extends Item {
   public SakuraMoneyItems(Properties properties) {
      super(properties);
   }

   public boolean m_5812_(ItemStack stack) {
      return true;
   }

   public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(Component.m_237115_("item.sakura_mod.sakura_money.tooltip").m_130940_(ChatFormatting.GRAY));
      super.m_7373_(stack, level, tooltip, flag);
   }
}
