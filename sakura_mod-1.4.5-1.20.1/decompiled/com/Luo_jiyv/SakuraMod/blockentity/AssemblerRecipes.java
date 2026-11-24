package com.Luo_jiyv.SakuraMod.blockentity;

import com.Luo_jiyv.SakuraMod.mixin.SmithingTransformRecipeAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class AssemblerRecipes {
   private static final Map<RecipeManager, AssemblerRecipes> INDEXES = new WeakHashMap<>();
   private static final AbstractContainerMenu CRAFTING_MENU = new AbstractContainerMenu(null, -1) {
      public ItemStack m_7648_(Player player, int index) {
         return ItemStack.f_41583_;
      }

      public boolean m_6875_(Player player) {
         return false;
      }
   };
   private final Map<Item, List<AssemblerRecipes.Entry>> byItem = new HashMap<>();
   private final Map<ResourceLocation, AssemblerRecipes.Entry> byId = new HashMap<>();

   static AssemblerRecipes get(Level level) {
      RecipeManager manager = level.m_7465_();
      AssemblerRecipes index = INDEXES.get(manager);
      if (index == null) {
         index = new AssemblerRecipes(level, manager);
         INDEXES.put(manager, index);
      }

      return index;
   }

   @SubscribeEvent
   public static void onDatapackSync(OnDatapackSyncEvent event) {
      if (event.getPlayer() == null) {
         INDEXES.remove(event.getPlayerList().m_7873_().m_129894_());
      }
   }

   private AssemblerRecipes(Level level, RecipeManager manager) {
      for (Recipe<?> recipe : manager.m_44013_(RecipeType.f_44107_)) {
         this.add(level, recipe);
      }

      for (Recipe<?> recipe : manager.m_44013_(RecipeType.f_44112_)) {
         this.add(level, recipe);
      }

      for (Recipe<?> recipe : manager.m_44013_(RecipeType.f_44113_)) {
         this.add(level, recipe);
      }
   }

   private void add(Level level, Recipe<?> recipe) {
      if (!recipe.m_5598_() && recipe.m_8004_(3, 3)) {
         if (!(recipe instanceof ShapedRecipe shaped && (shaped.m_44220_() <= 0 || shaped.m_44220_() > 3 || shaped.m_44221_() <= 0 || shaped.m_44221_() > 3))) {
            List<Ingredient> ingredients;
            if (recipe instanceof SmithingTransformRecipeAccessor smithing) {
               ingredients = List.of(smithing.sakura$template(), smithing.sakura$base(), smithing.sakura$addition());
            } else {
               ingredients = recipe.m_7527_();
            }

            if (!ingredients.isEmpty() && ingredients.size() <= 9) {
               if (!(recipe instanceof ShapedRecipe shapedx && ingredients.size() != shapedx.m_44220_() * shapedx.m_44221_())) {
                  int size = recipe.m_6671_() == RecipeType.f_44113_ ? 3 : (recipe.m_6671_() == RecipeType.f_44112_ ? 1 : 9);
                  if (size == 9 || ingredients.size() == size) {
                     ItemStack result = recipe.m_8043_(level.m_9598_());
                     if (!result.m_41619_()) {
                        ArrayList<Ingredient> inputs = new ArrayList<>(ingredients.size());
                        int[] positions = new int[ingredients.size()];

                        for (int i = 0; i < ingredients.size(); i++) {
                           Ingredient ingredient = ingredients.get(i);
                           if (ingredient.m_43947_()) {
                              if (size != 9) {
                                 return;
                              }
                           } else {
                              positions[inputs.size()] = recipe instanceof ShapedRecipe shapedxx ? i / shapedxx.m_44220_() * 3 + i % shapedxx.m_44220_() : i;
                              inputs.add(ingredient);
                           }
                        }

                        if (!inputs.isEmpty()) {
                           Class<?> type = recipe.getClass();
                           boolean batchable = type == ShapedRecipe.class
                              || type == ShapelessRecipe.class
                              || type == StonecutterRecipe.class
                              || type == SmithingTransformRecipe.class;
                           boolean itemOnlyIngredients = batchable && ingredients.stream().allMatch(input -> input.getClass() == Ingredient.class);
                           if (!itemOnlyIngredients || !(recipe instanceof ShapelessRecipe) || inputs.size() == ingredients.size()) {
                              AssemblerRecipes.Entry entry = new AssemblerRecipes.Entry(
                                 recipe, result.m_41777_(), inputs.toArray(Ingredient[]::new), positions, batchable, itemOnlyIngredients
                              );
                              this.byItem.computeIfAbsent(result.m_41720_(), item -> new ArrayList<>()).add(entry);
                              this.byId.put(entry.id(), entry);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   AssemblerRecipes.Entry byId(ResourceLocation id) {
      return this.byId.get(id);
   }

   List<AssemblerRecipes.Entry> candidates(ItemStack pattern) {
      List<AssemblerRecipes.Entry> all = this.byItem.getOrDefault(pattern.m_41720_(), List.of());
      if (all.isEmpty()) {
         return all;
      } else {
         List<AssemblerRecipes.Entry> exact = new ArrayList<>();

         for (AssemblerRecipes.Entry entry : all) {
            if (ItemStack.m_150942_(entry.result(), pattern)) {
               exact.add(entry);
            }
         }

         return exact.isEmpty() ? all : exact;
      }
   }

   static NonNullList<ItemStack> emptyGrid() {
      return NonNullList.m_122780_(9, ItemStack.f_41583_);
   }

   static Container craftingGrid(NonNullList<ItemStack> craftingItems) {
      return new TransientCraftingContainer(CRAFTING_MENU, 3, 3, craftingItems);
   }

   static ItemStack assemble(AssemblerRecipes.Entry entry, Container grid, Level level) {
      return assembleChecked(entry.recipe(), grid, level);
   }

   private static <C extends Container> ItemStack assembleChecked(Recipe<C> recipe, Container grid, Level level) {
      return recipe.m_5818_(grid, level) ? recipe.m_5874_(grid, level.m_9598_()) : ItemStack.f_41583_;
   }

   static <C extends Container> List<ItemStack> remainders(Recipe<C> recipe, Container grid) {
      return recipe.m_7457_(grid);
   }

   record Entry(Recipe<?> recipe, ItemStack result, Ingredient[] inputs, int[] positions, boolean batchable, boolean itemOnlyIngredients) {
      ResourceLocation id() {
         return this.recipe.m_6423_();
      }

      boolean hasFixedResult() {
         return this.itemOnlyIngredients && this.recipe.m_6671_() != RecipeType.f_44113_;
      }
   }
}
