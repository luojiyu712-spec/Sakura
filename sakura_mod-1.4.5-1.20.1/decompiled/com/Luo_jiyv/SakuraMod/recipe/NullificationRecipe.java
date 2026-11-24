package com.Luo_jiyv.SakuraMod.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class NullificationRecipe implements Recipe<CraftingContainer> {
   private final ResourceLocation id;
   private final Ingredient mainHand;
   private final Ingredient offHand;
   private final ItemStack result;

   public NullificationRecipe(ResourceLocation id, Ingredient mainHand, Ingredient offHand, ItemStack result) {
      this.id = id;
      this.mainHand = mainHand;
      this.offHand = offHand;
      this.result = result;
   }

   public Ingredient getMainHand() {
      return this.mainHand;
   }

   public Ingredient getOffHand() {
      return this.offHand;
   }

   public boolean matchesHands(ItemStack main, ItemStack off) {
      return this.mainHand.test(main) && this.offHand.test(off);
   }

   public ItemStack getResult() {
      return this.result.m_41777_();
   }

   public boolean matches(CraftingContainer container, Level level) {
      return false;
   }

   public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
      return this.result.m_41777_();
   }

   public boolean m_8004_(int width, int height) {
      return false;
   }

   public ItemStack m_8043_(RegistryAccess access) {
      return this.result;
   }

   public NonNullList<Ingredient> m_7527_() {
      NonNullList<Ingredient> list = NonNullList.m_122779_();
      list.add(this.mainHand);
      list.add(this.offHand);
      return list;
   }

   public ResourceLocation m_6423_() {
      return this.id;
   }

   public RecipeSerializer<?> m_7707_() {
      return (RecipeSerializer<?>)ModRecipes.NULLIFICATION_SERIALIZER.get();
   }

   public RecipeType<?> m_6671_() {
      return (RecipeType<?>)ModRecipes.NULLIFICATION_TYPE.get();
   }
}
