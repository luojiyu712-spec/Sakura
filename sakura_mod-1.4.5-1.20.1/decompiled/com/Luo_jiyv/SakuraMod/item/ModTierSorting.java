package com.Luo_jiyv.SakuraMod.item;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(
   modid = "sakura_mod",
   bus = Bus.MOD
)
public class ModTierSorting {
   @SubscribeEvent
   public static void onCommonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(
         () -> TierSortingRegistry.registerTier(
            ModToolTiers.SAKURA_STEEL, ResourceLocation.fromNamespaceAndPath("sakura_mod", "sakura_steel"), List.of(Tiers.NETHERITE), List.of()
         )
      );
   }
}
