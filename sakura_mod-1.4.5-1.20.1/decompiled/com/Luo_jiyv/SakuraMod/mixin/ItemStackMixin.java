package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.event.SoulRetrievalEvents;
import com.Luo_jiyv.SakuraMod.util.AdvancementUtils;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})
public abstract class ItemStackMixin {
   @Inject(
      method = {"hurt"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void sakura$onHurtWouldBreak(int amount, RandomSource random, ServerPlayer user, CallbackInfoReturnable<Boolean> cir) {
      if (cir.getReturnValueZ() && SoulRetrievalEvents.tryRepair(user, (ItemStack)this)) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"hurtAndBreak"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V",
         shift = Shift.AFTER
      )}
   )
   private <T extends LivingEntity> void sakura$afterDurabilityBreak(int amount, T entity, Consumer<T> onBroken, CallbackInfo ci) {
      ItemStack stack = (ItemStack)this;
      if (entity instanceof ServerPlayer player && SoulRetrievalEvents.isSoulSpent(stack)) {
         SoulRetrievalEvents.clearSpent(stack);
         AdvancementUtils.award(player, "sakura_mod:functional/irreversible", "trigger");
      }
   }

   @Inject(
      method = {"enchant"},
      at = {@At("TAIL")}
   )
   private void sakura$afterEnchant(Enchantment enchantment, int level, CallbackInfo ci) {
      SoulRetrievalEvents.clearSpentIfReenchanted((ItemStack)this);
   }

   @Inject(
      method = {"addTagElement"},
      at = {@At("TAIL")}
   )
   private void sakura$afterTagElement(String key, Tag tag, CallbackInfo ci) {
      if ("Enchantments".equals(key)) {
         SoulRetrievalEvents.clearSpentIfReenchanted((ItemStack)this);
      }
   }

   @Inject(
      method = {"setTag"},
      at = {@At("TAIL")}
   )
   private void sakura$afterTag(CompoundTag tag, CallbackInfo ci) {
      SoulRetrievalEvents.clearSpentIfReenchanted((ItemStack)this);
   }
}
