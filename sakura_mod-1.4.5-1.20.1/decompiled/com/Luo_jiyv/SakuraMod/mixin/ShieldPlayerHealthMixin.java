package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.event.ShieldMechanismEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Player.class})
public abstract class ShieldPlayerHealthMixin {
   @Inject(
      method = {"readAdditionalSaveData"},
      at = {@At("TAIL")}
   )
   private void sakura$migrateShieldCapacity(CompoundTag tag, CallbackInfo ci) {
      ShieldMechanismEvents.migrateSavedCapacity((Player)this);
   }

   @Redirect(
      method = {"actuallyHurt"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;setHealth(F)V"
      )
   )
   private void sakura$vanillaPlayerDamageHealth(Player player, float health) {
      ShieldMechanismEvents.setHealthWithoutShield(player, health);
   }
}
