package com.Luo_jiyv.SakuraMod.damage;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public interface GunDamageContext {
   GunDamageContext.Shot sakura$getGunShot();

   void sakura$setGunShot(GunDamageContext.Shot var1);

   public interface Projectile {
      ResourceLocation getGunId();
   }

   public record Shot(ResourceLocation gunId, Entity projectile, Entity shooter) {
   }
}
