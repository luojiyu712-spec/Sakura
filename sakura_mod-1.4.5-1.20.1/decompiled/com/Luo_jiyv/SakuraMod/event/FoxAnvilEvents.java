package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.block.FoxAnvilBlock;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class FoxAnvilEvents {
   private FoxAnvilEvents() {
   }

   @SubscribeEvent
   public static void onLeftClick(LeftClickBlock event) {
      if (!event.getLevel().m_5776_()) {
         if (event.getAction() == Action.START) {
            if (event.getLevel().m_8055_(event.getPos()).m_60734_() instanceof FoxAnvilBlock) {
               event.getLevel().m_5594_(null, event.getPos(), SoundEvents.f_11948_, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
         }
      }
   }
}
