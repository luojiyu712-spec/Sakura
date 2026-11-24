package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.event.SoulRetrievalEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ElytraItem.class})
public abstract class ElytraItemMixin {
   @Inject(
      method = {"elytraFlightTick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
         shift = Shift.AFTER,
         remap = true
      )},
      remap = false
   )
   private void sakura$onFlightWear(ItemStack stack, LivingEntity entity, int flightTicks, CallbackInfoReturnable<Boolean> cir) {
      if (entity instanceof ServerPlayer player && stack.m_41773_() == stack.m_41776_() - 1) {
         SoulRetrievalEvents.tryRepair(player, stack);
      }
   }
}
