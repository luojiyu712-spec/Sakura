package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.enchantment.ModEnchantments;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.Nullable;

public class SoulRetrievalTrade implements ItemListing {
   private final int minLevel;
   private final int maxLevel;
   private final int baseEmeraldCost;
   private final int maxUses;
   private final int villagerXp;
   private final float priceMultiplier;

   public SoulRetrievalTrade(int minLevel, int maxLevel, int baseEmeraldCost, int maxUses, int villagerXp, float priceMultiplier) {
      this.minLevel = minLevel;
      this.maxLevel = maxLevel;
      this.baseEmeraldCost = baseEmeraldCost;
      this.maxUses = maxUses;
      this.villagerXp = villagerXp;
      this.priceMultiplier = priceMultiplier;
   }

   @Nullable
   public MerchantOffer m_213663_(Entity trader, RandomSource random) {
      Enchantment enchantment = (Enchantment)ModEnchantments.SOUL_RETRIEVAL.get();
      if (enchantment == null) {
         return null;
      } else {
         int maxPossible = Math.min(this.maxLevel, enchantment.m_6586_());
         int minPossible = Math.min(this.minLevel, maxPossible);
         if (minPossible > maxPossible) {
            return null;
         } else {
            int level = minPossible + random.m_188503_(maxPossible - minPossible + 1);
            int emeraldCost = this.baseEmeraldCost + (level - 1) * 5;
            ItemStack book = new ItemStack(Items.f_42690_);
            EnchantedBookItem.m_41153_(book, new EnchantmentInstance(enchantment, level));
            return new MerchantOffer(
               new ItemStack(Items.f_42616_, emeraldCost), new ItemStack(Items.f_42517_, 1), book, this.maxUses, this.villagerXp, this.priceMultiplier
            );
         }
      }
   }
}
