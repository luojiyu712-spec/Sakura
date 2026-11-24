package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.item.FlightBraceletItem;
import com.Luo_jiyv.SakuraMod.item.GravCoreItem;
import com.Luo_jiyv.SakuraMod.util.PlayerEquipCache;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class GravCoreEvents {
   public static final String NBT_GRANTED = "SakuraFlightGranted";
   private static final int PARTICLE_INTERVAL = 3;

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END) {
         if (event.player instanceof ServerPlayer player) {
            if (updateFlightAbility(player, false) && player.m_150110_().f_35935_) {
               spawnFlightParticles(player);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onChangeDimension(PlayerChangedDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         refreshFlightAfterTransfer(player);
      }
   }

   @SubscribeEvent
   public static void onRespawn(PlayerRespawnEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         refreshFlightAfterTransfer(player);
      }
   }

   private static void refreshFlightAfterTransfer(ServerPlayer player) {
      PlayerEquipCache.invalidate(player);
      updateFlightAbility(player, true);
   }

   private static boolean updateFlightAbility(ServerPlayer player, boolean resync) {
      if (!player.m_7500_() && !player.m_5833_()) {
         boolean shouldFly = canProvideFlight(player);
         Abilities abilities = player.m_150110_();
         CompoundTag data = player.getPersistentData();
         if (shouldFly) {
            boolean needsGrant = !abilities.f_35936_;
            if (needsGrant) {
               abilities.f_35936_ = true;
               data.m_128379_("SakuraFlightGranted", true);
            }

            if (needsGrant || resync) {
               player.m_6885_();
            }
         } else if (data.m_128471_("SakuraFlightGranted")) {
            data.m_128473_("SakuraFlightGranted");
            if (abilities.f_35936_ || abilities.f_35935_) {
               abilities.f_35936_ = false;
               abilities.f_35935_ = false;
               player.m_6885_();
            }
         }

         return shouldFly;
      } else {
         return false;
      }
   }

   public static boolean canProvideFlight(Player player) {
      return PlayerEquipCache.hasGravCore(player) || FlightBraceletItem.canProvideFlight(player);
   }

   @SubscribeEvent
   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer sp) {
         GravCoreItem.syncModeToClient(sp);
      }
   }

   private static void spawnFlightParticles(ServerPlayer player) {
      if (player.m_9236_() instanceof ServerLevel serverLevel) {
         if (player.f_19797_ % 3 == 0) {
            boolean hasBracelet = FlightBraceletItem.canProvideFlight(player);
            boolean hasCore = PlayerEquipCache.hasGravCore(player);
            if (hasBracelet || hasCore) {
               double x = player.m_20185_();
               double y = player.m_20186_() + 0.1;
               double z = player.m_20189_();
               if (hasBracelet) {
                  serverLevel.m_8767_(ParticleTypes.f_276452_, x, y, z, 1, 0.2, 0.05, 0.2, 0.01);
               }

               if (hasCore) {
                  serverLevel.m_8767_(ParticleTypes.f_123810_, x, y, z, 1, 0.15, 0.05, 0.15, 0.01);
               }
            }
         }
      }
   }
}
