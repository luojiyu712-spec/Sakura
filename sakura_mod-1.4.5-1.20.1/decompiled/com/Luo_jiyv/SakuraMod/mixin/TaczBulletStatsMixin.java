package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.damage.GunDamageContext;
import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleData;
import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleStat;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair;
import java.util.LinkedList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {EntityKineticBullet.class},
   remap = false
)
public abstract class TaczBulletStatsMixin implements GunDamageContext.Projectile {
   @Shadow
   private float distanceAmount;
   @Shadow
   private LinkedList<DistanceDamagePair> damageAmount;

   @Inject(
      method = {"<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V"},
      at = {@At("RETURN")}
   )
   private void sakura$applyCapsuleStats(
      EntityType<? extends Projectile> type,
      Level level,
      LivingEntity shooter,
      ItemStack gun,
      ResourceLocation ammoId,
      ResourceLocation gunId,
      ResourceLocation displayId,
      boolean tracer,
      GunData gunData,
      BulletData bulletData,
      CallbackInfo ci
   ) {
      if (!level.m_5776_() && CapsuleData.hasCapsule(gun)) {
         CapsuleStat range = CapsuleStat.EFFECTIVE_RANGE;
         if (range.isEnabled() && CapsuleData.gunValue(gun, range) != 0.0) {
            this.distanceAmount = (float)Math.max(0.0, CapsuleData.applyGunValue(gun, range, this.distanceAmount));
         }

         CapsuleStat damage = CapsuleStat.DAMAGE;
         if (damage.isEnabled() && CapsuleData.gunValue(gun, damage) != 0.0 && this.damageAmount != null && !this.damageAmount.isEmpty()) {
            LinkedList<DistanceDamagePair> adjusted = new LinkedList<>();

            for (DistanceDamagePair pair : this.damageAmount) {
               float value = (float)Math.max(0.0, CapsuleData.applyGunValue(gun, damage, pair.getDamage()));
               adjusted.add(new DistanceDamagePair(pair.getDistance(), value));
            }

            this.damageAmount = adjusted;
         }
      }
   }
}
