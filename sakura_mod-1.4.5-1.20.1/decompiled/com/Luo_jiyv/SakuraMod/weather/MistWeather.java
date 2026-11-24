package com.Luo_jiyv.SakuraMod.weather;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.network.MistStatePacket;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.network.PacketDistributor;

public final class MistWeather {
   public static final int DEFAULT_DURATION = 24000;
   private static final int NATURAL_MIN_DURATION = 24000;
   private static final int NATURAL_MAX_DURATION = 48000;
   private static final int NATURAL_CHECK_INTERVAL = 1200;
   private static final int NATURAL_CHANCE = 120;
   private static final int NATURAL_COOLDOWN = 24000;
   public static final int MAX_DURATION = 1000000;
   private static final String DATA_NAME = "sakura_mist_weather";
   private static volatile boolean active;
   private static long endTick;
   private static long naturalCooldownUntil;

   public static boolean isActive() {
      return active;
   }

   public static long remainingTicks(long gameTime) {
      return active ? Math.max(0L, endTick - gameTime) : 0L;
   }

   public static void start(MinecraftServer server, int durationTicks) {
      if (server != null) {
         if (Config.isMistWeatherEnabled()) {
            BloodRainWeather.stop(server);
            int duration = Math.max(20, Math.min(1000000, durationTicks));
            boolean wasActive = active;
            active = true;
            endTick = server.m_129783_().m_46467_() + duration;
            save(server);
            broadcast(server);
            if (!wasActive) {
               announce(server, "一股粗粝的雾气滞留在空气中。气味腐烂而带有酸性。");
            }
         }
      }
   }

   public static void stop(MinecraftServer server) {
      if (active) {
         active = false;
         endTick = 0L;
         if (server != null) {
            naturalCooldownUntil = server.m_129783_().m_46467_() + 24000L;
         }

         save(server);
         broadcast(server);
         announce(server, "灰棘迷雾散去");
      }
   }

   public static void tick(MinecraftServer server) {
      if (server != null) {
         if (!Config.isMistWeatherEnabled()) {
            if (active) {
               stop(server);
            }
         } else if (active) {
            ServerLevel overworld = server.m_129783_();
            if (overworld.m_46467_() >= endTick) {
               stop(server);
            } else {
               if (overworld.m_46471_() || overworld.m_46470_()) {
                  overworld.m_8606_(12000, 0, false, false);
               }
            }
         } else {
            tickNatural(server);
         }
      }
   }

   private static void tickNatural(MinecraftServer server) {
      if (Config.isMistWeatherEnabled()) {
         if (server.m_129921_() % 1200 == 0) {
            if (server.m_6846_().m_11309_() != 0) {
               ServerLevel overworld = server.m_129783_();
               if (overworld.m_46467_() >= naturalCooldownUntil) {
                  if (!overworld.m_46471_() && !overworld.m_46470_()) {
                     if (overworld.f_46441_.m_188503_(120) == 0) {
                        int duration = 24000 + overworld.f_46441_.m_188503_(24001);
                        start(server, duration);
                     }
                  }
               }
            }
         }
      }
   }

   public static void init(MinecraftServer server) {
      if (server != null) {
         if (!Config.isMistWeatherEnabled()) {
            active = false;
            endTick = 0L;
            save(server);
         } else {
            MistWeather.Data data = getData(server);
            active = data.active;
            endTick = data.endTick;
            naturalCooldownUntil = data.naturalCooldownUntil;
            if (active) {
               if (endTick <= server.m_129783_().m_46467_()) {
                  active = false;
                  endTick = 0L;
                  save(server);
               } else {
                  broadcast(server);
               }
            }
         }
      }
   }

   public static void syncTo(ServerPlayer player) {
      ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MistStatePacket(active));
   }

   private static void broadcast(MinecraftServer server) {
      if (server != null) {
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new MistStatePacket(active));
      }
   }

   private static void announce(MinecraftServer server, String text) {
      if (server != null) {
         server.m_6846_().m_240416_(Component.m_237113_(text), false);
      }
   }

   private static void save(MinecraftServer server) {
      if (server != null) {
         MistWeather.Data data = getData(server);
         data.active = active;
         data.endTick = endTick;
         data.naturalCooldownUntil = naturalCooldownUntil;
         data.m_77762_();
      }
   }

   private static MistWeather.Data getData(MinecraftServer server) {
      ServerLevel overworld = server.m_129783_();
      return (MistWeather.Data)overworld.m_8895_().m_164861_(MistWeather.Data::load, MistWeather.Data::new, "sakura_mist_weather");
   }

   private MistWeather() {
   }

   public static final class Data extends SavedData {
      private boolean active;
      private long endTick;
      private long naturalCooldownUntil;

      public static MistWeather.Data load(CompoundTag tag) {
         MistWeather.Data data = new MistWeather.Data();
         data.active = tag.m_128471_("Active");
         data.endTick = tag.m_128454_("EndTick");
         data.naturalCooldownUntil = tag.m_128454_("NaturalCooldownUntil");
         return data;
      }

      public CompoundTag m_7176_(CompoundTag tag) {
         tag.m_128379_("Active", this.active);
         tag.m_128356_("EndTick", this.endTick);
         tag.m_128356_("NaturalCooldownUntil", this.naturalCooldownUntil);
         return tag;
      }
   }
}
