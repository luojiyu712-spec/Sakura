package com.Luo_jiyv.SakuraMod.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class SoulRetrievalEnchantment extends Enchantment {
   public SoulRetrievalEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
      super(rarity, category, slots);
   }

   public int m_6586_() {
      return 5;
   }

   public int m_44702_() {
      return 1;
   }

   public int m_6183_(int level) {
      return 1 + (level - 1) * 10;
   }

   public int m_6175_(int level) {
      return this.m_6183_(level) + 50;
   }

   public boolean m_6591_() {
      return true;
   }

   public boolean m_6594_() {
      return true;
   }

   public boolean m_6592_() {
      return true;
   }

   public boolean m_5975_(Enchantment other) {
      return true;
   }
}
