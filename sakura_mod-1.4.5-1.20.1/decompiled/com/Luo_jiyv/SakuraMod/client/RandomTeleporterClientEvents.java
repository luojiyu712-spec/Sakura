package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.item.RandomTeleporterItem;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.network.RandomTeleporterSettingsPacket;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public class RandomTeleporterClientEvents {
   private static ResourceKey<Level> lastDim = null;

   @SubscribeEvent
   public static void onLeftClickEmpty(LeftClickEmpty event) {
      if (event.getItemStack().m_41720_() instanceof RandomTeleporterItem) {
         Minecraft mc = Minecraft.m_91087_();
         if (mc.f_91080_ == null) {
            mc.m_91152_(new RandomTeleporterScreen());
         }
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         Minecraft mc = Minecraft.m_91087_();
         if (mc.f_91073_ == null) {
            lastDim = null;
         } else {
            ResourceKey<Level> dim = mc.f_91073_.m_46472_();
            if (lastDim != null && !lastDim.equals(dim)) {
               ClientRandomTeleporterSettings.setStructures(Set.of());
               ClientRandomTeleporterSettings.setBiomes(Set.of());
               ModNetwork.CHANNEL
                  .sendToServer(
                     new RandomTeleporterSettingsPacket(
                        ClientRandomTeleporterSettings.getMinRadius(),
                        ClientRandomTeleporterSettings.getMaxRadius(),
                        List.of(),
                        ClientRandomTeleporterSettings.getMode(),
                        List.of()
                     )
                  );
               SakuraMod.LOGGER.debug("[SakuraTP] Client dim change {} -> {}, cleared local cache & notified server", lastDim.m_135782_(), dim.m_135782_());
            }

            lastDim = dim;
         }
      }
   }
}
