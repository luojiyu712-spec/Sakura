package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.event.ShieldMechanismEvents;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class})
public abstract class ShieldHealthMixin {
   @Inject(
      method = {"setHealth"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sakura$shieldHealth(float health, CallbackInfo ci) {
      if (ShieldMechanismEvents.interceptHealthSet((LivingEntity)this, health)) {
         ci.cancel();
      }
   }

   @Redirect(
      method = {"actuallyHurt"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"
      )
   )
   private void sakura$vanillaDamageHealth(LivingEntity entity, float health) {
      ShieldMechanismEvents.setHealthWithoutShield(entity, health);
   }
}
