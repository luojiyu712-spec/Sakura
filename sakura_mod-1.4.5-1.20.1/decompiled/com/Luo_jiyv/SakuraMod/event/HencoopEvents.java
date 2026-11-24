package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.item.HencoopItem;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class HencoopEvents {
   @SubscribeEvent
   public static void onEntityInteract(EntityInteract event) {
      CaptureEventHelper.tryCapture(
         event,
         Chicken.class,
         s -> s.m_41720_() instanceof HencoopItem,
         HencoopItem::canStore,
         HencoopItem::getCount,
         HencoopItem::setCount,
         "sakura_mod:functional/chicken_catcher"
      );
   }
}
