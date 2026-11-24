package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.item.Moditems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EnchantmentHelper.class})
public abstract class SakuraSteelFortuneMixin {
   private static final int BASE_BONUS = 2;

   @Inject(
      method = {"getItemEnchantmentLevel"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void sakura$boost(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
      if (stack != null && !stack.m_41619_()) {
         if (Config.isSakuraSteelFortuneBonusEnabled()) {
            Item item = stack.m_41720_();
            int actual = (Integer)cir.getReturnValue();
            if (enchantment == Enchantments.f_44987_ && isSakuraDigTool(item)) {
               cir.setReturnValue(actual + 2);
            } else {
               if (enchantment == Enchantments.f_44982_ && isSakuraWeapon(item)) {
                  cir.setReturnValue(actual + 2);
               }
            }
         }
      }
   }

   private static boolean isSakuraDigTool(Item item) {
      return item == Moditems.SAKURA_PICKAXE.get()
         || item == Moditems.SAKURA_SHOVEL.get()
         || item == Moditems.SAKURA_HOE.get()
         || item == Moditems.SAKURA_AXE.get();
   }

   private static boolean isSakuraWeapon(Item item) {
      return item == Moditems.SAKURA_SWORD.get() || item == Moditems.SAKURA_AXE.get();
   }
}
