package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleTaczEvents;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.client.event.CameraSetupEvent;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {CameraSetupEvent.class},
   remap = false
)
public class TaczCameraRecoilMixin {
   private static final ThreadLocal<ItemStack> CURRENT_GUN = new ThreadLocal<>();

   @Inject(
      method = {"initialCameraRecoil"},
      at = {@At("HEAD")},
      require = 0,
      remap = false
   )
   private static void sakura$applyCapsuleRecoil(GunFireEvent event, CallbackInfo ci) {
      CURRENT_GUN.remove();
      if (event.getLogicalSide().isClient()) {
         CURRENT_GUN.set(event.getGunItemStack());
      }
   }

   @Inject(
      method = {"initialCameraRecoil"},
      at = {@At("RETURN")},
      require = 0,
      remap = false
   )
   private static void sakura$clearCapsuleRecoilContext(GunFireEvent event, CallbackInfo ci) {
      CURRENT_GUN.remove();
   }

   @Redirect(
      method = {"initialCameraRecoil"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;"
      ),
      require = 0,
      remap = false
   )
   private static Object sakura$useAdjustedRecoil(AttachmentCacheProperty cache, String key) {
      Object value = cache.getCache(key);
      if (!"recoil".equals(key)) {
         return value;
      } else {
         ItemStack gun = CURRENT_GUN.get();
         if (gun == null) {
            return value;
         } else {
            ParameterizedCachePair<Float, Float> adjusted = CapsuleTaczEvents.adjustRecoil(gun, value);
            return adjusted != null ? adjusted : value;
         }
      }
   }
}
