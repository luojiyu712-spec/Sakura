package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleData;
import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleStat;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.custom.EffectiveRangeModifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   value = {EffectiveRangeModifier.class},
   remap = false
)
public class TaczEffectiveRangeDisplayMixin {
   @Redirect(
      method = {"getPropertyDiagramsData"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"
      ),
      require = 3,
      allow = 3
   )
   private String sakura$displayFinalRange(String format, Object[] arguments, ItemStack gun, GunData data, AttachmentCacheProperty cache) {
      if (CapsuleData.hasCapsule(gun) && CapsuleStat.EFFECTIVE_RANGE.isEnabled() && cache.getCache("effective_range") instanceof Float range) {
         arguments = (Object[])arguments.clone();
         arguments[0] = Math.min(range, 1024.0F);
      }

      return String.format(format, arguments);
   }
}
