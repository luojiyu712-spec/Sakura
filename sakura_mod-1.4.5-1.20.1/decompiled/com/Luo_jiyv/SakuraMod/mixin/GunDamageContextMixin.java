package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.damage.GunDamageContext;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({DamageSource.class})
public abstract class GunDamageContextMixin implements GunDamageContext {
   @Unique
   private GunDamageContext.Shot sakura$gunShot;

   @Override
   public GunDamageContext.Shot sakura$getGunShot() {
      return this.sakura$gunShot;
   }

   @Override
   public void sakura$setGunShot(GunDamageContext.Shot shot) {
      this.sakura$gunShot = shot;
   }
}
