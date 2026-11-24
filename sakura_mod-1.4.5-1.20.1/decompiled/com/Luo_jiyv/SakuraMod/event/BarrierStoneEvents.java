package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.util.BarrierStoneCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.entity.living.MobSpawnEvent.PositionCheck;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class BarrierStoneEvents {
   @SubscribeEvent
   public static void onPositionCheck(PositionCheck event) {
      MobSpawnType type = event.getSpawnType();
      if (type == MobSpawnType.NATURAL || type == MobSpawnType.CHUNK_GENERATION) {
         if (event.getLevel() instanceof ServerLevel sl) {
            if (BarrierStoneCache.isBlocked(sl, event.getEntity().m_20183_())) {
               event.setResult(Result.DENY);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      BarrierStoneCache.clear();
   }
}
