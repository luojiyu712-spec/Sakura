package com.Luo_jiyv.SakuraMod.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
   targets = {"top.theillusivec4.curios.common.slottype.SlotType"},
   remap = false
)
public abstract class CuriosRingSlotsMixin {
   @Shadow
   @Final
   private String identifier;
   @Shadow
   @Final
   @Mutable
   private int size;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void sakura$fillRingSlots(CallbackInfo ci) {
      if ("ring".equals(this.identifier)) {
         this.size = Math.max(this.size, 10);
      }
   }
}
