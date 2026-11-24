package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.recipe.ModRecipes;
import com.Luo_jiyv.SakuraMod.recipe.NullificationRecipe;
import com.Luo_jiyv.SakuraMod.util.AdvancementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class NullificationEvents {
   @SubscribeEvent
   public static void onRightClickItem(RightClickItem event) {
      tryCraft(event);
   }

   @SubscribeEvent
   public static void onRightClickBlock(RightClickBlock event) {
      tryCraft(event);
   }

   private static void tryCraft(PlayerInteractEvent event) {
      Player player = event.getEntity();
      if (!player.m_9236_().m_5776_()) {
         ItemStack main = player.m_21205_();
         ItemStack off = player.m_21206_();
         if (!main.m_41619_() && !off.m_41619_()) {
            RecipeManager manager = player.m_9236_().m_7465_();

            for (NullificationRecipe recipe : manager.m_44013_((RecipeType)ModRecipes.NULLIFICATION_TYPE.get())) {
               if (recipe.matchesHands(main, off)) {
                  ItemStack result = recipe.getResult();
                  if (!player.m_7500_()) {
                     main.m_41774_(1);
                     off.m_41774_(1);
                  }

                  if (!player.m_150109_().m_36054_(result)) {
                     player.m_36176_(result, false);
                  }

                  player.m_9236_().m_5594_(null, player.m_20183_(), SoundEvents.f_11887_, SoundSource.PLAYERS, 0.7F, 0.55F);
                  if (player instanceof ServerPlayer sp
                     && recipe.m_6423_().equals(ResourceLocation.fromNamespaceAndPath("sakura_mod", "nullification_golden_cube"))) {
                     AdvancementUtils.award(sp, "sakura_mod:story/legendary_investigator", "trigger");
                  }

                  event.setCanceled(true);
                  event.setCancellationResult(InteractionResult.SUCCESS);
                  return;
               }
            }
         }
      }
   }
}
