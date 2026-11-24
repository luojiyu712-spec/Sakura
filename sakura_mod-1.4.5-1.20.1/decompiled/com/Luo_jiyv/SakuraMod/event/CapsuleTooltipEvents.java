package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class CapsuleTooltipEvents {
   @SubscribeEvent
   public static void onTooltip(ItemTooltipEvent event) {
      if (CapsuleData.hasCapsule(event.getItemStack())) {
         event.getToolTip().add(Component.m_237119_());
         event.getToolTip().add(Component.m_237115_("item.sakura_mod.archite_capsule.installed").m_130940_(ChatFormatting.GOLD));
      }
   }
}
