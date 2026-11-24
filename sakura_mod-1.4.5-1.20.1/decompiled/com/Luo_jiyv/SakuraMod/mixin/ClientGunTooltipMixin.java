package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleData;
import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleStat;
import com.tacz.guns.client.tooltip.ClientGunTooltip;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   value = {ClientGunTooltip.class},
   remap = false
)
public class ClientGunTooltipMixin {
   @Redirect(
      method = {"getText"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/tacz/guns/util/AttachmentDataUtils;getDamageWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)D"
      ),
      require = 0,
      remap = false
   )
   private double sakura$capsuleDamage(ItemStack gun, GunData data) {
      double base = AttachmentDataUtils.getDamageWithAttachment(gun, data);
      if (!CapsuleData.hasCapsule(gun)) {
         return base;
      } else {
         return !CapsuleStat.DAMAGE.isEnabled() ? base : Math.max(0.0, CapsuleData.applyGunValue(gun, CapsuleStat.DAMAGE, base));
      }
   }

   @Redirect(
      method = {"getText"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/tacz/guns/util/AttachmentDataUtils;getArmorIgnoreWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)D"
      ),
      require = 0,
      remap = false
   )
   private double sakura$capsuleArmorIgnore(ItemStack gun, GunData data) {
      double base = AttachmentDataUtils.getArmorIgnoreWithAttachment(gun, data);
      if (!CapsuleData.hasCapsule(gun)) {
         return base;
      } else {
         return !CapsuleStat.ARMOR_IGNORE.isEnabled() ? base : Math.max(0.0, CapsuleData.applyGunValue(gun, CapsuleStat.ARMOR_IGNORE, base));
      }
   }

   @Redirect(
      method = {"getText"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/tacz/guns/util/AttachmentDataUtils;getHeadshotMultiplier(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)D"
      ),
      require = 0,
      remap = false
   )
   private double sakura$capsuleHeadshot(ItemStack gun, GunData data) {
      double base = AttachmentDataUtils.getHeadshotMultiplier(gun, data);
      if (!CapsuleData.hasCapsule(gun)) {
         return base;
      } else {
         return !CapsuleStat.HEAD_SHOT.isEnabled() ? base : Math.max(0.0, CapsuleData.applyGunValue(gun, CapsuleStat.HEAD_SHOT, base));
      }
   }

   @Redirect(
      method = {"getText"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/tacz/guns/util/AttachmentDataUtils;getWightWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)D"
      ),
      require = 0,
      remap = false
   )
   private double sakura$capsuleWeight(ItemStack gun, GunData data) {
      double base = AttachmentDataUtils.getWightWithAttachment(gun, data);
      if (!CapsuleData.hasCapsule(gun)) {
         return base;
      } else {
         return !CapsuleStat.WEIGHT.isEnabled() ? base : Math.max(0.0, CapsuleData.applyGunValue(gun, CapsuleStat.WEIGHT, base));
      }
   }
}
