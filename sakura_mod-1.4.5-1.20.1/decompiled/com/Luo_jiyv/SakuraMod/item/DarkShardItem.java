package com.Luo_jiyv.SakuraMod.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class DarkShardItem extends Item {
   public DarkShardItem(Properties properties) {
      super(properties);
   }

   public void m_7373_(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
      super.m_7373_(stack, level, tooltip, flag);
      tooltip.add(Component.m_237115_("item.sakura_mod.dark_transcendent_shard.desc.1").m_130940_(ChatFormatting.DARK_PURPLE));
      tooltip.add(Component.m_237115_("item.sakura_mod.dark_transcendent_shard.desc.2").m_130940_(ChatFormatting.DARK_PURPLE));
      tooltip.add(
         Component.m_237115_("item.sakura_mod.dark_transcendent_shard.desc.3").m_130944_(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
      );
   }
}
