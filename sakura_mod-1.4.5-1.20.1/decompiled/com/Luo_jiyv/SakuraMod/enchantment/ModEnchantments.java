package com.Luo_jiyv.SakuraMod.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
   public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "sakura_mod");
   public static final EnchantmentCategory SOUL_RETRIEVAL_CATEGORY = EnchantmentCategory.create("soul_retrieval", item -> item.m_41465_());
   public static final RegistryObject<Enchantment> SOUL_RETRIEVAL = ENCHANTMENTS.register(
      "soul_retrieval",
      () -> new SoulRetrievalEnchantment(
         Rarity.VERY_RARE,
         SOUL_RETRIEVAL_CATEGORY,
         new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}
      )
   );

   public static void register(IEventBus bus) {
      ENCHANTMENTS.register(bus);
   }
}
