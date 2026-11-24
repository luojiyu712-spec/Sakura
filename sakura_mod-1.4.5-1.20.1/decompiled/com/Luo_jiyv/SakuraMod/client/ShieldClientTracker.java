package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.network.ShieldStateSyncPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public final class ShieldClientTracker {
   private static final ShieldStateSyncPacket EMPTY = new ShieldStateSyncPacket(0, 0, -1L);
   private static ShieldStateSyncPacket shield = EMPTY;
   private static final ShieldEnergyAnimation ENERGY = new ShieldEnergyAnimation(5);
   private static Player trackedPlayer;
   private static Level trackedLevel;

   private ShieldClientTracker() {
   }

   public static void onServerSync(ShieldStateSyncPacket state) {
      if (shield.capacity() != state.capacity()) {
         ENERGY.reset();
      }

      shield = state;
   }

   public static ShieldStateSyncPacket getShield() {
      return shield;
   }

   public static float getClientRemainingCooldown(Player player, float partialTick) {
      return shield.recoveryStart() < 0L ? 0.0F : Math.max(0.0F, (float)(shield.recoveryStart() - player.m_9236_().m_46467_()) - partialTick);
   }

   public static float getClientDisplayedCharges(Player player, float partialTick) {
      if (trackedPlayer != player || trackedLevel != player.m_9236_()) {
         trackedPlayer = player;
         trackedLevel = player.m_9236_();
         ENERGY.reset();
      }

      return (float)ENERGY.sample(shield.energy(), (double)player.m_9236_().m_46467_() + partialTick);
   }

   @SubscribeEvent
   public static void onLogout(LoggingOut event) {
      shield = EMPTY;
      trackedPlayer = null;
      trackedLevel = null;
      ENERGY.reset();
   }
}
