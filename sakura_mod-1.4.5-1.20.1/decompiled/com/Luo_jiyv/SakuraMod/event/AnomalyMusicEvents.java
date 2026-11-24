package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.network.AnomalyMusicPacket;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class AnomalyMusicEvents {
   private static final ResourceLocation ANOMALY_ID = ResourceLocation.fromNamespaceAndPath("sakura_mod", "story/golden_cube_anomaly");

   @SubscribeEvent
   public static void onAdvancementEarn(AdvancementEarnEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         Advancement advancement = event.getAdvancement();
         if (advancement != null) {
            ResourceLocation id = advancement.m_138327_();
            if (id != null && ANOMALY_ID.equals(id)) {
               ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new AnomalyMusicPacket());
            }
         }
      }
   }

   private AnomalyMusicEvents() {
   }
}
