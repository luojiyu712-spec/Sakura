package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.item.BeeBoxItem;
import com.Luo_jiyv.SakuraMod.item.HencoopItem;
import com.Luo_jiyv.SakuraMod.item.Moditems;
import com.Luo_jiyv.SakuraMod.menu.BeeHoneyScreen;
import com.Luo_jiyv.SakuraMod.menu.ChickenCoopScreen;
import com.Luo_jiyv.SakuraMod.menu.CobbleGeneratorScreen;
import com.Luo_jiyv.SakuraMod.menu.ModMenus;
import com.Luo_jiyv.SakuraMod.menu.MolecularAssemblerScreen;
import com.Luo_jiyv.SakuraMod.menu.StringFarmScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ClientSetup {
   @SubscribeEvent
   public static void onRegisterShaders(RegisterShadersEvent event) {
      WeatherFogRenderer.registerShader(event);
   }

   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      event.enqueueWork(
         () -> {
            MenuScreens.m_96206_((MenuType)ModMenus.STRING_FARM_MENU.get(), StringFarmScreen::new);
            MenuScreens.m_96206_((MenuType)ModMenus.CHICKEN_COOP.get(), ChickenCoopScreen::new);
            MenuScreens.m_96206_((MenuType)ModMenus.BEE_HONEY_MENU.get(), BeeHoneyScreen::new);
            MenuScreens.m_96206_((MenuType)ModMenus.COBBLE_GENERATOR.get(), CobbleGeneratorScreen::new);
            MenuScreens.m_96206_((MenuType)ModMenus.MOLECULAR_ASSEMBLER.get(), MolecularAssemblerScreen::new);
            ItemProperties.register(
               (Item)Moditems.BEE_BOX.get(),
               ResourceLocation.fromNamespaceAndPath("sakura_mod", "bee_box_state"),
               (stack, level, entity, seed) -> BeeBoxItem.getModelState(stack)
            );
            ItemProperties.register(
               (Item)Moditems.HENCOOP.get(),
               ResourceLocation.fromNamespaceAndPath("sakura_mod", "chicken_coop_state"),
               (stack, level, entity, seed) -> HencoopItem.getModelState(stack)
            );
         }
      );
   }
}
