package com.Luo_jiyv.SakuraMod.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
   public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, "sakura_mod");
   public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "sakura_mod");
   public static final RegistryObject<RecipeType<NullificationRecipe>> NULLIFICATION_TYPE = TYPES.register(
      "nullification", () -> new RecipeType<NullificationRecipe>() {
         @Override
         public String toString() {
            return "sakura_mod:nullification";
         }
      }
   );
   public static final RegistryObject<RecipeSerializer<NullificationRecipe>> NULLIFICATION_SERIALIZER = SERIALIZERS.register(
      "nullification", NullificationRecipeSerializer::new
   );

   public static void register(IEventBus eventBus) {
      TYPES.register(eventBus);
      SERIALIZERS.register(eventBus);
   }
}
