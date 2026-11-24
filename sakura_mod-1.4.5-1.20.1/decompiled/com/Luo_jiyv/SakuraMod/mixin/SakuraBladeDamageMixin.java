package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.damage.SakuraSpecialDamage;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({Player.class, LivingEntity.class})
public abstract class SakuraBladeDamageMixin {
   @ModifyVariable(
      method = {"hurt"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private DamageSource sakura$slashSource(DamageSource source, DamageSource original, float amount) {
      return SakuraSpecialDamage.convertSlash((LivingEntity)this, source, amount);
   }

   @ModifyVariable(
      method = {"hurt"},
      at = @At(
         value = "LOAD",
         ordinal = 0
      ),
      argsOnly = true,
      ordinal = 0
   )
   private float sakura$slashAmount(float amount, DamageSource source) {
      return SakuraSpecialDamage.slashAmount((LivingEntity)this, source, amount);
   }
}
