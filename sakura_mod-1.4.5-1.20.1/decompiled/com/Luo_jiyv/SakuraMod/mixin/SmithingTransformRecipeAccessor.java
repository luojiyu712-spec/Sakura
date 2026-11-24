package com.Luo_jiyv.SakuraMod.mixin;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({SmithingTransformRecipe.class})
public interface SmithingTransformRecipeAccessor {
   @Accessor("template")
   Ingredient sakura$template();

   @Accessor("base")
   Ingredient sakura$base();

   @Accessor("addition")
   Ingredient sakura$addition();
}
