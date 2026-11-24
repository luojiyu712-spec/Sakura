package com.Luo_jiyv.SakuraMod.item;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ModArmorMaterials implements ArmorMaterial {
   private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
   public static final ModArmorMaterials SAKURA_STEEL = new ModArmorMaterials(
      "sakura_steel",
      814,
      new int[]{6, 12, 16, 6},
      15,
      SoundEvents.f_11679_,
      8.0F,
      0.2F,
      () -> Ingredient.m_43929_(new ItemLike[]{(ItemLike)Moditems.SAKURA_BLOCK_ITEM.get()})
   );
   private final String name;
   private final int durabilityMultiplier;
   private final int[] slotProtections;
   private final int enchantmentValue;
   private final SoundEvent sound;
   private final float toughness;
   private final float knockbackResistance;
   private final Supplier<Ingredient> repairIngredient;

   private ModArmorMaterials(
      String name,
      int durabilityMultiplier,
      int[] slotProtections,
      int enchantmentValue,
      SoundEvent sound,
      float toughness,
      float knockbackResistance,
      Supplier<Ingredient> repairIngredient
   ) {
      this.name = name;
      this.durabilityMultiplier = durabilityMultiplier;
      this.slotProtections = slotProtections;
      this.enchantmentValue = enchantmentValue;
      this.sound = sound;
      this.toughness = toughness;
      this.knockbackResistance = knockbackResistance;
      this.repairIngredient = repairIngredient;
   }

   public int m_266425_(Type type) {
      return HEALTH_PER_SLOT[type.m_266308_().m_20749_()] * this.durabilityMultiplier;
   }

   public int m_7366_(Type type) {
      return this.slotProtections[type.m_266308_().m_20749_()];
   }

   public int m_6646_() {
      return this.enchantmentValue;
   }

   public SoundEvent m_7344_() {
      return this.sound;
   }

   public Ingredient m_6230_() {
      return this.repairIngredient.get();
   }

   public String m_6082_() {
      return "sakura_mod:" + this.name;
   }

   public float m_6651_() {
      return this.toughness;
   }

   public float m_6649_() {
      return this.knockbackResistance;
   }
}
