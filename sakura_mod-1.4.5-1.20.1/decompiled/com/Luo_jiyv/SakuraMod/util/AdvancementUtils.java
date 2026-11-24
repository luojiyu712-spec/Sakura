package com.Luo_jiyv.SakuraMod.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementUtils {
   private static final Map<String, ResourceLocation> ID_CACHE = new ConcurrentHashMap<>();

   public static void award(ServerPlayer player, String advancementId, String criterion) {
      if (player.m_20194_() != null) {
         ResourceLocation id = ID_CACHE.computeIfAbsent(advancementId, ResourceLocation::parse);
         Advancement adv = player.m_20194_().m_129889_().m_136041_(id);
         if (adv != null) {
            player.m_8960_().m_135988_(adv, criterion);
         }
      }
   }
}
