package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.client.music.ExternalMusicPack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class SakuraClientSetup {
   public static void init(FMLJavaModLoadingContext context) {
      SakuraMod.LOGGER.info("HELLO FROM CLIENT SETUP");
      ClientEvents.register(context.getModEventBus());
      context.getModEventBus().addListener(SakuraSlashParticle::register);
      context.getModEventBus().addListener(SakuraAxeSlashParticle::register);
      context.getModEventBus().addListener(RandomTeleporterParticle::register);
      context.getModEventBus().addListener(ShieldEnergyParticle::register);
      context.getModEventBus().addListener(ExternalMusicPack::register);
      context.getModEventBus().addListener(ExternalMusicPack::registerReloadListener);
      context.registerConfig(Type.CLIENT, SakuraClientConfig.SPEC);
      if (ModList.get().isLoaded("cloth_config")) {
         context.registerExtensionPoint(
            ConfigScreenFactory.class, () -> new ConfigScreenFactory((minecraft, parentScreen) -> SakuraConfigScreen.create(parentScreen))
         );
      } else {
         SakuraMod.LOGGER.info("Cloth Config not installed - in-game config screen disabled (config file still works).");
      }
   }
}
