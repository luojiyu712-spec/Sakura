package com.Luo_jiyv.SakuraMod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
   private static final String VERSION = "15";
   public static final SimpleChannel CHANNEL = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath("sakura_mod", "main"))
      .networkProtocolVersion(() -> "15")
      .clientAcceptedVersions("15"::equals)
      .serverAcceptedVersions("15"::equals)
      .simpleChannel();

   public static void register() {
      DefaultMusicNetwork.register();
      int id = 0;
      CHANNEL.messageBuilder(GravCoreActivatePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(GravCoreActivatePacket::encode)
         .decoder(GravCoreActivatePacket::decode)
         .consumerMainThread(GravCoreActivatePacket::handle)
         .add();
      CHANNEL.messageBuilder(GravCoreModeSwitchPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(GravCoreModeSwitchPacket::encode)
         .decoder(GravCoreModeSwitchPacket::decode)
         .consumerMainThread(GravCoreModeSwitchPacket::handle)
         .add();
      CHANNEL.messageBuilder(RequestBiomeListPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(RequestBiomeListPacket::encode)
         .decoder(RequestBiomeListPacket::decode)
         .consumerMainThread(RequestBiomeListPacket::handle)
         .add();
      CHANNEL.messageBuilder(RequestStructureListPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(RequestStructureListPacket::encode)
         .decoder(RequestStructureListPacket::decode)
         .consumerMainThread(RequestStructureListPacket::handle)
         .add();
      CHANNEL.messageBuilder(SakuraArmorTogglePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(SakuraArmorTogglePacket::encode)
         .decoder(SakuraArmorTogglePacket::decode)
         .consumerMainThread(SakuraArmorTogglePacket::handle)
         .add();
      CHANNEL.messageBuilder(ShieldStateSyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(ShieldStateSyncPacket::encode)
         .decoder(ShieldStateSyncPacket::decode)
         .consumerMainThread(ShieldStateSyncPacket::handle)
         .add();
      CHANNEL.messageBuilder(GravCoreSyncModePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(GravCoreSyncModePacket::encode)
         .decoder(GravCoreSyncModePacket::decode)
         .consumerMainThread(GravCoreSyncModePacket::handle)
         .add();
      CHANNEL.messageBuilder(BiomeListPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(BiomeListPacket::encode)
         .decoder(BiomeListPacket::decode)
         .consumerMainThread(BiomeListPacket::handle)
         .add();
      CHANNEL.messageBuilder(StructureListPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(StructureListPacket::encode)
         .decoder(StructureListPacket::decode)
         .consumerMainThread(StructureListPacket::handle)
         .add();
      CHANNEL.messageBuilder(GoldenCubeMadnessPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(GoldenCubeMadnessPacket::encode)
         .decoder(GoldenCubeMadnessPacket::decode)
         .consumerMainThread(GoldenCubeMadnessPacket::handle)
         .add();
      CHANNEL.messageBuilder(CapsuleSettingsPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(CapsuleSettingsPacket::encode)
         .decoder(CapsuleSettingsPacket::decode)
         .consumerMainThread(CapsuleSettingsPacket::handle)
         .add();
      CHANNEL.messageBuilder(CapsuleInstallPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(CapsuleInstallPacket::encode)
         .decoder(CapsuleInstallPacket::decode)
         .consumerMainThread(CapsuleInstallPacket::handle)
         .add();
      CHANNEL.messageBuilder(CapsuleCreativeSyncPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(CapsuleCreativeSyncPacket::encode)
         .decoder(CapsuleCreativeSyncPacket::decode)
         .consumerMainThread(CapsuleCreativeSyncPacket::handle)
         .add();
      CHANNEL.registerMessage(
         id++,
         RandomTeleporterSettingsPacket.class,
         RandomTeleporterSettingsPacket::encode,
         RandomTeleporterSettingsPacket::decode,
         RandomTeleporterSettingsPacket::handle
      );
      CHANNEL.messageBuilder(FlightTimeSyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(FlightTimeSyncPacket::encode)
         .decoder(FlightTimeSyncPacket::decode)
         .consumerMainThread(FlightTimeSyncPacket::handle)
         .add();
      CHANNEL.messageBuilder(MistStatePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(MistStatePacket::encode)
         .decoder(MistStatePacket::decode)
         .consumerMainThread(MistStatePacket::handle)
         .add();
      CHANNEL.messageBuilder(AssemblerSettingsPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(AssemblerSettingsPacket::encode)
         .decoder(AssemblerSettingsPacket::decode)
         .consumerMainThread(AssemblerSettingsPacket::handle)
         .add();
      CHANNEL.messageBuilder(AssemblerSetRecipePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(AssemblerSetRecipePacket::encode)
         .decoder(AssemblerSetRecipePacket::decode)
         .consumerMainThread(AssemblerSetRecipePacket::handle)
         .add();
      CHANNEL.messageBuilder(AssemblerSideConfigPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(AssemblerSideConfigPacket::encode)
         .decoder(AssemblerSideConfigPacket::decode)
         .consumerMainThread(AssemblerSideConfigPacket::handle)
         .add();
      CHANNEL.messageBuilder(AnomalyMusicPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(AnomalyMusicPacket::encode)
         .decoder(AnomalyMusicPacket::decode)
         .consumerMainThread(AnomalyMusicPacket::handle)
         .add();
      CHANNEL.messageBuilder(BloodRainStatePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(BloodRainStatePacket::encode)
         .decoder(BloodRainStatePacket::decode)
         .consumerMainThread(BloodRainStatePacket::handle)
         .add();
      CHANNEL.messageBuilder(SakuraWeaponSwingPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(SakuraWeaponSwingPacket::encode)
         .decoder(SakuraWeaponSwingPacket::decode)
         .consumerMainThread(SakuraWeaponSwingPacket::handle)
         .add();
      CHANNEL.messageBuilder(SakuraAxeSlashPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(SakuraAxeSlashPacket::encode)
         .decoder(SakuraAxeSlashPacket::decode)
         .consumerMainThread(SakuraAxeSlashPacket::handle)
         .add();
      CHANNEL.messageBuilder(RandomTeleporterEffectPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(RandomTeleporterEffectPacket::encode)
         .decoder(RandomTeleporterEffectPacket::decode)
         .consumerMainThread(RandomTeleporterEffectPacket::handle)
         .add();
   }
}
