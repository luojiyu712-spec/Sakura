package com.Luo_jiyv.SakuraMod.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class NullificationRecipeSerializer implements RecipeSerializer<NullificationRecipe> {
   public NullificationRecipe fromJson(ResourceLocation id, JsonObject json) {
      Ingredient main = Ingredient.m_43917_(GsonHelper.m_13930_(json, "main_hand"));
      Ingredient off = Ingredient.m_43917_(GsonHelper.m_13930_(json, "off_hand"));
      ItemStack result = CraftingHelper.getItemStack(GsonHelper.m_13930_(json, "result"), true);
      return new NullificationRecipe(id, main, off, result);
   }

   public void toJson(JsonObject json, NullificationRecipe recipe) {
      json.add("main_hand", recipe.getMainHand().m_43942_());
      json.add("off_hand", recipe.getOffHand().m_43942_());
      ItemStack result = recipe.m_8043_(RegistryAccess.f_243945_);
      JsonObject resultJson = new JsonObject();
      ResourceLocation key = ForgeRegistries.ITEMS.getKey(result.m_41720_());
      resultJson.addProperty("item", key == null ? "minecraft:air" : key.toString());
      resultJson.addProperty("count", result.m_41613_());
      json.add("result", resultJson);
   }

   public NullificationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
      Ingredient main = Ingredient.m_43940_(buf);
      Ingredient off = Ingredient.m_43940_(buf);
      ItemStack result = buf.m_130267_();
      return new NullificationRecipe(id, main, off, result);
   }

   public void toNetwork(FriendlyByteBuf buf, NullificationRecipe recipe) {
      recipe.getMainHand().m_43923_(buf);
      recipe.getOffHand().m_43923_(buf);
      buf.m_130055_(recipe.getResult());
   }
}
