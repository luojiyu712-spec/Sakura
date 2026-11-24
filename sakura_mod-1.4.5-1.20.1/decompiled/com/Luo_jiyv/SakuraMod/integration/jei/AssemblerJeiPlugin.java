package com.Luo_jiyv.SakuraMod.integration.jei;

import com.Luo_jiyv.SakuraMod.recipe.ModRecipes;
import com.Luo_jiyv.SakuraMod.recipe.NullificationRecipe;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

@JeiPlugin
public class AssemblerJeiPlugin implements IModPlugin {
   private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("sakura_mod", "molecular_assembler");

   public ResourceLocation getPluginUid() {
      return UID;
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new NullificationRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      Minecraft mc = Minecraft.m_91087_();
      if (mc.f_91073_ != null) {
         List<NullificationRecipe> recipes = mc.f_91073_.m_7465_().m_44013_((RecipeType)ModRecipes.NULLIFICATION_TYPE.get());
         registration.addRecipes(NullificationRecipeCategory.TYPE, List.copyOf(recipes));
      }
   }

   public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
      registration.addRecipeTransferHandler(new AssemblerTransferHandler(RecipeTypes.CRAFTING), RecipeTypes.CRAFTING);
      registration.addRecipeTransferHandler(new AssemblerTransferHandler(RecipeTypes.STONECUTTING), RecipeTypes.STONECUTTING);
      registration.addRecipeTransferHandler(new AssemblerTransferHandler(RecipeTypes.SMITHING), RecipeTypes.SMITHING);
   }
}
