package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.blockentity.ModBlockEntities;
import com.Luo_jiyv.SakuraMod.client.renderer.ControllerRenderer;
import com.Luo_jiyv.SakuraMod.client.renderer.MolecularAssemblerRenderer;
import com.Luo_jiyv.SakuraMod.network.GravCoreActivatePacket;
import com.Luo_jiyv.SakuraMod.network.GravCoreModeSwitchPacket;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.network.SakuraArmorTogglePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {
   public static void register(IEventBus modEventBus) {
      modEventBus.addListener(ClientEvents::onRegisterKeys);
      modEventBus.addListener(ClientEvents::onRegisterRenderers);
      MinecraftForge.EVENT_BUS.register(ClientEvents.class);
   }

   @SubscribeEvent
   public static void onLoggingOut(LoggingOut event) {
      ClientFlightTimeCache.reset();
   }

   private static void onRegisterKeys(RegisterKeyMappingsEvent event) {
      event.register(ModKeyMappings.GRAV_CORE_ACTIVATE);
      event.register(ModKeyMappings.GRAV_CORE_MODE_SWITCH);
      event.register(ModKeyMappings.EDIT_SHIELD_HUD);
      event.register(ModKeyMappings.TOGGLE_ARMOR_REVIVE);
      event.register(ModKeyMappings.TOGGLE_AUTO_REPAIR);
   }

   @SubscribeEvent
   public static void onRegisterRenderers(RegisterRenderers event) {
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntities.CONTROLLER.get(), ControllerRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntities.MOLECULAR_ASSEMBLER.get(), MolecularAssemblerRenderer::new);
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         GraphicsCompatibility.detectOnClientTick();
         Minecraft mc = Minecraft.m_91087_();
         GraphicsCompatibility.notifyOnce(mc.f_91074_);
         if (mc.f_91074_ != null && mc.f_91080_ == null) {
            while (ModKeyMappings.GRAV_CORE_ACTIVATE.m_90859_()) {
               ModNetwork.CHANNEL.sendToServer(new GravCoreActivatePacket());
            }

            while (ModKeyMappings.GRAV_CORE_MODE_SWITCH.m_90859_()) {
               GravCoreClient.playModeSwitchSound();
               ModNetwork.CHANNEL.sendToServer(new GravCoreModeSwitchPacket());
            }

            boolean toggleRevive = false;
            boolean toggleAutoRepair = false;

            while (ModKeyMappings.TOGGLE_ARMOR_REVIVE.m_90859_()) {
               toggleRevive = true;
            }

            while (ModKeyMappings.TOGGLE_AUTO_REPAIR.m_90859_()) {
               toggleAutoRepair = true;
            }

            if (toggleRevive || toggleAutoRepair) {
               ModNetwork.CHANNEL.sendToServer(new SakuraArmorTogglePacket(toggleRevive, toggleAutoRepair));
            }
         }
      }
   }
}
