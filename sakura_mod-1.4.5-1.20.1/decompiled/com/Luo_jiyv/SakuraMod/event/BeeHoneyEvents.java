package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.item.BeeBoxItem;
import net.minecraft.world.entity.animal.Bee;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class BeeHoneyEvents {
   @SubscribeEvent
   public static void onEntityInteract(EntityInteract event) {
      CaptureEventHelper.tryCapture(
         event,
         Bee.class,
         s -> s.m_41720_() instanceof BeeBoxItem,
         BeeBoxItem::canStore,
         BeeBoxItem::getCount,
         BeeBoxItem::setCount,
         "sakura_mod:functional/bee_catcher"
      );
   }
}
