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
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SakuraPvpLicenseItem extends Item implements ICurioItem {
   public SakuraPvpLicenseItem(Properties properties) {
      super(properties);
   }

   public boolean canEquip(SlotContext slotContext, ItemStack stack) {
      return "core".equals(slotContext.identifier());
   }

   public boolean m_5812_(ItemStack stack) {
      return true;
   }

   public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(Component.m_237115_("item.sakura_mod.pvp_license.tooltip.1").m_130940_(ChatFormatting.RED));
      tooltip.add(Component.m_237115_("item.sakura_mod.pvp_license.tooltip.2").m_130940_(ChatFormatting.DARK_GRAY));
      super.m_7373_(stack, level, tooltip, flag);
   }
}
