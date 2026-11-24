package com.Luo_jiyv.SakuraMod.integration.jei;

import com.Luo_jiyv.SakuraMod.menu.ModMenus;
import com.Luo_jiyv.SakuraMod.menu.MolecularAssemblerMenu;
import com.Luo_jiyv.SakuraMod.network.AssemblerSetRecipePacket;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import java.util.Optional;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;

public class AssemblerTransferHandler<R extends Recipe<?>> implements IRecipeTransferHandler<MolecularAssemblerMenu, R> {
   private final RecipeType<R> type;

   public AssemblerTransferHandler(RecipeType<R> type) {
      this.type = type;
   }

   public Class<? extends MolecularAssemblerMenu> getContainerClass() {
      return MolecularAssemblerMenu.class;
   }

   public Optional<MenuType<MolecularAssemblerMenu>> getMenuType() {
      return Optional.of((MenuType<MolecularAssemblerMenu>)ModMenus.MOLECULAR_ASSEMBLER.get());
   }

   public RecipeType<R> getRecipeType() {
      return this.type;
   }

   public IRecipeTransferError transferRecipe(
      MolecularAssemblerMenu menu, R recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer
   ) {
      if (!doTransfer) {
         return null;
      } else {
         ModNetwork.CHANNEL.sendToServer(new AssemblerSetRecipePacket(menu.getBlockEntity().m_58899_(), menu.f_38840_, recipe.m_6423_()));
         return null;
      }
   }
}
