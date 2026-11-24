package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.item.SakuraAxeItem;
import com.Luo_jiyv.SakuraMod.item.SakuraSwordItem;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.network.SakuraWeaponSwingPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public final class SakuraWeaponClientEvents {
   private SakuraWeaponClientEvents() {
   }

   @SubscribeEvent
   public static void onLeftClickEmpty(LeftClickEmpty event) {
      ItemStack stack = event.getItemStack();
      if (stack.m_41720_() instanceof SakuraSwordItem || stack.m_41720_() instanceof SakuraAxeItem && SakuraAxeItem.isSlashEnabled(stack)) {
         ModNetwork.CHANNEL.sendToServer(new SakuraWeaponSwingPacket());
      }
   }
}
