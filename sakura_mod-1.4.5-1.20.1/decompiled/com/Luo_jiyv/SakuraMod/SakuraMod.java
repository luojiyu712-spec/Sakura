package com.Luo_jiyv.SakuraMod;

import com.Luo_jiyv.SakuraMod.attribute.ModAttributes;
import com.Luo_jiyv.SakuraMod.block.ModBlocks;
import com.Luo_jiyv.SakuraMod.blockentity.ModBlockEntities;
import com.Luo_jiyv.SakuraMod.effect.ModEffects;
import com.Luo_jiyv.SakuraMod.enchantment.ModEnchantments;
import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleTaczEvents;
import com.Luo_jiyv.SakuraMod.item.ModCreativeModeTabs;
import com.Luo_jiyv.SakuraMod.item.Moditems;
import com.Luo_jiyv.SakuraMod.loot.ModLootModifiers;
import com.Luo_jiyv.SakuraMod.menu.ModMenus;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.particle.ModParticles;
import com.Luo_jiyv.SakuraMod.recipe.ModRecipes;
import com.mojang.logging.LogUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.MissingMappingsEvent.Mapping;
import org.slf4j.Logger;

@Mod("sakura_mod")
public class SakuraMod {
   public static final String MOD_ID = "sakura_mod";
   public static final Logger LOGGER = LogUtils.getLogger();

   public SakuraMod(FMLJavaModLoadingContext context) {
      IEventBus modEventBus = context.getModEventBus();
      modEventBus.addListener(this::commonSetup);
      ModCreativeModeTabs.register(modEventBus);
      ModBlocks.register(modEventBus);
      ModBlockEntities.register(modEventBus);
      ModMenus.register(modEventBus);
      Moditems.register(modEventBus);
      ModEffects.register(modEventBus);
      ModAttributes.register(modEventBus);
      ModEnchantments.register(modEventBus);
      ModParticles.register(modEventBus);
      ModLootModifiers.register(modEventBus);
      ModRecipes.register(modEventBus);
      ModNetwork.register();
      MinecraftForge.EVENT_BUS.register(this);
      if (ModList.get().isLoaded("tacz")) {
         MinecraftForge.EVENT_BUS.register(new CapsuleTaczEvents());
      }

      modEventBus.addListener(this::addCreative);
      context.registerConfig(Type.COMMON, Config.SPEC);
      if (FMLLoader.getDist().isClient()) {
         try {
            Class<?> clazz = Class.forName("com.Luo_jiyv.SakuraMod.client.SakuraClientSetup");
            clazz.getMethod("init", FMLJavaModLoadingContext.class).invoke(null, context);
         } catch (Exception var4) {
            LOGGER.error("Failed to init Sakura client", var4);
         }
      }
   }

   @SubscribeEvent
   public void migrateRemovedDiscs(MissingMappingsEvent event) {
      for (Mapping<Item> mapping : event.getMappings(Keys.ITEMS, "sakura_mod")) {
         if (isLegacyDisc(mapping.getKey().m_135815_())) {
            mapping.remap((Item)Moditems.EMPTY_MUSIC_DISC.get());
         }
      }

      for (Mapping<SoundEvent> mappingx : event.getMappings(Keys.SOUND_EVENTS, "sakura_mod")) {
         if (isLegacyDisc(mappingx.getKey().m_135815_())) {
            mappingx.ignore();
         }
      }
   }

   @SubscribeEvent
   public void migrateRemovedSlashEntity(MissingMappingsEvent event) {
      for (Mapping<EntityType<?>> mapping : event.getMappings(Keys.ENTITY_TYPES, "sakura_mod")) {
         if (mapping.getKey().m_135815_().equals("sakura_axe_slash")) {
            mapping.ignore();
         }
      }
   }

   private static boolean isLegacyDisc(String path) {
      return path.matches("music_disc_(0[1-9]|1[01])");
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      LOGGER.info("HELLO FROM COMMON SETUP");
   }

   private void addCreative(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey() == CreativeModeTabs.f_256791_) {
         event.m_245282_((ItemLike)Moditems.NEOFORGE_ITEM.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
      }
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
      LOGGER.info("HELLO from server starting");
   }
}
