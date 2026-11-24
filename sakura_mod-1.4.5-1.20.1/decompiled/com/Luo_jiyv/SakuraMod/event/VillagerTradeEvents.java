package com.Luo_jiyv.SakuraMod.event;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "sakura_mod",
   bus = Bus.FORGE
)
public class VillagerTradeEvents {
   @SubscribeEvent
   public static void onVillagerTrades(VillagerTradesEvent event) {
      if (event.getType() == VillagerProfession.f_35594_) {
         ((List)event.getTrades().computeIfAbsent(1, k -> new ArrayList())).add(new SoulRetrievalTrade(1, 2, 12, 16, 5, 0.05F));
         ((List)event.getTrades().computeIfAbsent(2, k -> new ArrayList())).add(new SoulRetrievalTrade(2, 3, 20, 12, 10, 0.05F));
         ((List)event.getTrades().computeIfAbsent(4, k -> new ArrayList())).add(new SoulRetrievalTrade(4, 4, 45, 4, 20, 0.1F));
         ((List)event.getTrades().computeIfAbsent(5, k -> new ArrayList())).add(new SoulRetrievalTrade(5, 5, 64, 2, 30, 0.2F));
      }
   }
}
