package com.Luo_jiyv.SakuraMod.weather;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.network.BloodRainStatePacket;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.PacketDistributor;

public final class BloodRainWeather {
   public static final int DEFAULT_DURATION = 6000;
   public static final int MAX_DURATION = 1000000;
   private static final String DATA_NAME = "sakura_blood_rain";
   private static volatile BloodRainStatePacket.Phase phase = BloodRainStatePacket.Phase.INACTIVE;
   private static long endTick;

   public static boolean isActive() {
      return phase == BloodRainStatePacket.Phase.ACTIVE;
   }

   public static long remainingTicks(long gameTime) {
      return isActive() ? Math.max(0L, endTick - gameTime) : 0L;
   }

   public static void start(MinecraftServer server, int durationTicks) {
      if (server != null) {
         if (Config.isBloodRainEnabled()) {
            int duration = Math.max(20, Math.min(1000000, durationTicks));
            boolean wasActive = isActive();
            MistWeather.stop(server);
            phase = BloodRainStatePacket.Phase.ACTIVE;
            ServerLevel overworld = server.m_129783_();
            endTick = overworld.m_46467_() + duration;
            overworld.m_8606_(0, duration, true, false);
            save(server);
            broadcast();
            if (!wasActive) {
               announce(server, "天空中降下了鲜血般的雨滴……");
            }
         }
      }
   }

   public static void stop(MinecraftServer server) {
      if (isActive()) {
         if (server == null) {
            finish(null, BloodRainStatePacket.Phase.INACTIVE);
         } else {
            server.m_129783_().m_8606_(12000, 0, false, false);
            finish(server, BloodRainStatePacket.Phase.CLEARING);
         }
      }
   }

   public static void stopForVanillaRain(MinecraftServer server) {
      if (phase != BloodRainStatePacket.Phase.INACTIVE) {
         finish(server, BloodRainStatePacket.Phase.INACTIVE);
      }
   }

   private static void finish(MinecraftServer server, BloodRainStatePacket.Phase next) {
      boolean wasActive = isActive();
      phase = next;
      endTick = 0L;
      if (server != null) {
         if (wasActive) {
            save(server);
         }

         broadcast();
         if (wasActive) {
            announce(server, "血雨停歇了");
         }
      }
   }

   public static void tick(MinecraftServer server) {
      if (server != null) {
         ServerLevel overworld = server.m_129783_();
         if (phase == BloodRainStatePacket.Phase.CLEARING && overworld.m_46722_(1.0F) <= 0.0F) {
            finish(server, BloodRainStatePacket.Phase.INACTIVE);
         }

         if (isActive()) {
            if (Config.isBloodRainEnabled() && overworld.m_46467_() < endTick) {
               if (!overworld.m_46471_()) {
                  long remaining = Math.max(20L, endTick - overworld.m_46467_());
                  overworld.m_8606_(0, (int)Math.min(2147483647L, remaining), true, false);
               }
            } else {
               stop(server);
            }
         }
      }
   }

   public static void init(MinecraftServer server) {
      if (server != null) {
         if (!Config.isBloodRainEnabled()) {
            BloodRainWeather.Data data = getData(server);
            boolean wasActive = data.active;
            phase = BloodRainStatePacket.Phase.INACTIVE;
            endTick = 0L;
            save(server);
            if (wasActive) {
               server.m_129783_().m_8606_(12000, 0, false, false);
            }
         } else {
            BloodRainWeather.Data data = getData(server);
            phase = data.active ? BloodRainStatePacket.Phase.ACTIVE : BloodRainStatePacket.Phase.INACTIVE;
            endTick = data.endTick;
            if (isActive()) {
               ServerLevel overworld = server.m_129783_();
               if (endTick <= overworld.m_46467_()) {
                  phase = BloodRainStatePacket.Phase.INACTIVE;
                  endTick = 0L;
                  save(server);
               } else {
                  long remaining = Math.max(20L, endTick - overworld.m_46467_());
                  overworld.m_8606_(0, (int)Math.min(2147483647L, remaining), true, false);
                  broadcast();
               }
            }
         }
      }
   }

   public static void syncTo(ServerPlayer player) {
      ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new BloodRainStatePacket(phase));
   }

   private static void broadcast() {
      ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new BloodRainStatePacket(phase));
   }

   private static void announce(MinecraftServer server, String text) {
      if (server != null) {
         server.m_6846_().m_240416_(Component.m_237113_(text), false);
      }
   }

   private static void save(MinecraftServer server) {
      if (server != null) {
         BloodRainWeather.Data data = getData(server);
         data.active = isActive();
         data.endTick = endTick;
         data.m_77762_();
      }
   }

   private static BloodRainWeather.Data getData(MinecraftServer server) {
      ServerLevel overworld = server.m_129783_();
      return (BloodRainWeather.Data)overworld.m_8895_().m_164861_(BloodRainWeather.Data::load, BloodRainWeather.Data::new, "sakura_blood_rain");
   }

   private BloodRainWeather() {
   }

   public static final class Data extends SavedData {
      private boolean active;
      private long endTick;

      public static BloodRainWeather.Data load(CompoundTag tag) {
         BloodRainWeather.Data data = new BloodRainWeather.Data();
         data.active = tag.m_128471_("Active");
         data.endTick = tag.m_128454_("EndTick");
         return data;
      }

      public CompoundTag m_7176_(CompoundTag tag) {
         tag.m_128379_("Active", this.active);
         tag.m_128356_("EndTick", this.endTick);
         return tag;
      }
   }
}
