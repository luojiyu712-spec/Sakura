package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.client.ClientGravCoreModeCache;
import com.Luo_jiyv.SakuraMod.item.GravCoreItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public class GravCoreTooltipEvents {
   @SubscribeEvent
   public static void onItemTooltip(ItemTooltipEvent event) {
      if (event.getItemStack().m_41720_() instanceof GravCoreItem) {
         Player player = event.getEntity();
         if (player != null) {
            GravCoreItem.Mode mode = ClientGravCoreModeCache.getMode();
            event.getToolTip()
               .add(
                  Component.m_237110_(
                        "item.sakura_mod.gravcore.current_mode", new Object[]{Component.m_237115_(mode.translationKey()).m_130940_(ChatFormatting.AQUA)}
                     )
                     .m_130940_(ChatFormatting.GOLD)
               );
         }
      }
   }
}
