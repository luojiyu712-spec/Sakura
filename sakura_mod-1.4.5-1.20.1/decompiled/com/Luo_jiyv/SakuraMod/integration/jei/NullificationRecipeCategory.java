package com.Luo_jiyv.SakuraMod.integration.jei;

import com.Luo_jiyv.SakuraMod.item.Moditems;
import com.Luo_jiyv.SakuraMod.recipe.NullificationRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class NullificationRecipeCategory implements IRecipeCategory<NullificationRecipe> {
   public static final RecipeType<NullificationRecipe> TYPE = new RecipeType(
      ResourceLocation.fromNamespaceAndPath("sakura_mod", "nullification"), NullificationRecipe.class
   );
   private static final int WIDTH = 100;
   private static final int HEIGHT = 62;
   private final IDrawable background;
   private final IDrawable icon;
   private final IDrawable arrow;

   public NullificationRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createBlankDrawable(100, 62);
      this.icon = guiHelper.createDrawableItemStack(new ItemStack((ItemLike)Moditems.DARK_TRANSCENDENT_SHARD.get()));
      this.arrow = guiHelper.getRecipeArrow();
   }

   public RecipeType<NullificationRecipe> getRecipeType() {
      return TYPE;
   }

   public Component getTitle() {
      return Component.m_237115_("jei.sakura_mod.nullification");
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public int getWidth() {
      return 100;
   }

   public int getHeight() {
      return 62;
   }

   public void draw(NullificationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.arrow.draw(guiGraphics, 40, 22);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, NullificationRecipe recipe, IFocusGroup focuses) {
      builder.addInputSlot(16, 8).setStandardSlotBackground().addIngredients(recipe.getMainHand());
      builder.addInputSlot(16, 32).setStandardSlotBackground().addIngredients(recipe.getOffHand());
      builder.addOutputSlot(76, 20).setStandardSlotBackground().addItemStack(recipe.getResult());
   }
}
