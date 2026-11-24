package com.Luo_jiyv.SakuraMod.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemEntity.class})
public abstract class ItemEntityMixin {
   @Unique
   private static final TagKey<Item> SAKURA_EXPLOSION_RESISTANT = TagKey.m_203882_(
      Registries.f_256913_, ResourceLocation.fromNamespaceAndPath("sakura_mod", "explosion_resistant_drops")
   );

   @Shadow
   public abstract ItemStack m_32055_();

   @Inject(
      method = {"hurt"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sakura$protectDropFromExplosion(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
      if (source.m_269533_(DamageTypeTags.f_268415_) && this.m_32055_().m_204117_(SAKURA_EXPLOSION_RESISTANT)) {
         callback.setReturnValue(false);
      }
   }
}
