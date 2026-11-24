package com.Luo_jiyv.SakuraMod.mixin;

import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin({Attributes.class})
public abstract class AttributesMixin {
   @ModifyConstant(
      method = {"<clinit>"},
      constant = {@Constant(
         doubleValue = 30.0
      )}
   )
   private static double sakura$armorLimit(double maximum) {
      return Math.max(maximum, 40.0);
   }

   @ModifyConstant(
      method = {"<clinit>"},
      constant = {@Constant(
         doubleValue = 20.0
      )},
      slice = {@Slice(
         from = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/entity/ai/attributes/Attributes;ARMOR:Lnet/minecraft/world/entity/ai/attributes/Attribute;"
         )
      )}
   )
   private static double sakura$toughnessLimit(double maximum) {
      return Math.max(maximum, 32.0);
   }
}
