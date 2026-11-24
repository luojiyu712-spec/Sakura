package com.Luo_jiyv.SakuraMod.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SakuraComponentItem extends Item {
   public SakuraComponentItem(Properties properties) {
      super(properties);
   }

   public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(
         Component.m_237115_("item.sakura_mod.sakura_component.tooltip").m_130944_(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
      );
      super.m_7373_(stack, level, tooltip, flag);
   }

   public boolean m_5812_(ItemStack stack) {
      return true;
   }
}
