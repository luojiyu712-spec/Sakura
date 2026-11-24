package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.network.BiomeListPacket;
import com.Luo_jiyv.SakuraMod.network.StructureListPacket;
import java.util.HashSet;
import net.minecraft.client.Minecraft;

public final class ClientPacketHandlers {
   private ClientPacketHandlers() {
   }

   public static void handleBiomeList(BiomeListPacket p) {
      Minecraft mc = Minecraft.m_91087_();
      mc.execute(() -> {
         if (mc.f_91080_ instanceof RandomTeleporterScreen parent) {
            mc.m_91152_(new BiomeSelectScreen(parent, p.getBiomes(), ClientRandomTeleporterSettings.getAllowedBiomes()));
         }
      });
   }

   public static void handleStructureList(StructureListPacket p) {
      Minecraft mc = Minecraft.m_91087_();
      mc.execute(() -> {
         ClientRandomTeleporterSettings.setStructures(new HashSet<>(p.getAllowedStructures()));
         if (mc.f_91080_ instanceof RandomTeleporterScreen parent) {
            mc.m_91152_(new StructureSelectScreen(parent, p.getStructures(), new HashSet<>(p.getAllowedStructures())));
         }
      });
   }
}
