package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.util.AdvancementUtils;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import org.jetbrains.annotations.Nullable;

final class CaptureEventHelper {
   private CaptureEventHelper() {
   }

   static <T extends Entity> void tryCapture(
      EntityInteract event,
      Class<T> targetClass,
      Predicate<ItemStack> isCaptureItem,
      Predicate<ItemStack> canStore,
      ToIntFunction<ItemStack> getCount,
      ObjIntConsumer<ItemStack> setCount,
      @Nullable String advancementId
   ) {
      Player player = event.getEntity();
      Entity target = event.getTarget();
      ItemStack stack = player.m_21120_(event.getHand());
      if (targetClass.isInstance(target)) {
         if (isCaptureItem.test(stack)) {
            if (!player.m_9236_().m_5776_()) {
               if (!player.m_6144_()) {
                  if (!canStore.test(stack)) {
                     event.setCanceled(true);
                  } else {
                     setCount.accept(stack, getCount.applyAsInt(stack) + 1);
                     target.m_146870_();
                     if (advancementId != null && player instanceof ServerPlayer sp) {
                        AdvancementUtils.award(sp, advancementId, "capture");
                     }

                     event.setCanceled(true);
                  }
               }
            }
         }
      }
   }
}
