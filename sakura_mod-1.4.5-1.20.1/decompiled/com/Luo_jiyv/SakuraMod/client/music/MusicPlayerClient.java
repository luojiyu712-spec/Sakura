package com.Luo_jiyv.SakuraMod.client.music;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.client.BloodRainClientState;
import com.Luo_jiyv.SakuraMod.client.MistClientState;
import com.Luo_jiyv.SakuraMod.client.SakuraClientConfig;
import com.Luo_jiyv.SakuraMod.client.WeatherVisuals;
import com.Luo_jiyv.SakuraMod.mixin.BossHealthOverlayAccessor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.Sound.Type;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.client.event.ScreenEvent.Closing;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public final class MusicPlayerClient {
   private static final int START_DELAY_TICKS = 20;
   private static final int TRACK_GAP_TICKS = 40;
   private static final int EXCLUSIVE_CHECK_INTERVAL = 10;
   private static final int EXCLUSIVE_SUSTAIN_CHECKS = 2;
   private static final int CONFIG_REFRESH_TICKS = 20;
   private static final int MAX_START_WAIT_TICKS = 100;
   private static final int EMPTY_RETRY_TICKS = 20;
   private static final int MIN_FULL_PLAY_TICKS = 200;
   private static final int INTERRUPT_BACKOFF_BASE = 100;
   private static final int INTERRUPT_BACKOFF_MAX = 200;
   private static final int MAX_EARLY_RETRIES = 2;
   private static final int FAILED_TRACK_COOLDOWN = 1200;
   private static final Map<String, Integer> TRACK_LENGTH_TICKS = Map.ofEntries(
      Map.entry("a_place_of_our_own", 4776),
      Map.entry("alive", 6507),
      Map.entry("ambition", 4869),
      Map.entry("ate_without_table", 4145),
      Map.entry("back_in_the_saddle", 3675),
      Map.entry("back_to_work", 6720),
      Map.entry("blue", 4969),
      Map.entry("bright_side_of_life", 4519),
      Map.entry("carry_on", 6245),
      Map.entry("code_red", 4797),
      Map.entry("cruel_world", 3948),
      Map.entry("delusion", 5469),
      Map.entry("everythings_going_to_be_okay", 5486),
      Map.entry("fair_glade", 4889),
      Map.entry("far_away", 3896),
      Map.entry("final_stand", 4752),
      Map.entry("hold_on", 3943),
      Map.entry("honor", 4192),
      Map.entry("illusion", 3846),
      Map.entry("legacy", 5520),
      Map.entry("lone_hunter", 7142),
      Map.entry("long_way_home", 5710),
      Map.entry("lovely_day", 3948),
      Map.entry("move", 3736),
      Map.entry("muffalo", 6897),
      Map.entry("my_friend", 4293),
      Map.entry("never_forgotten", 4656),
      Map.entry("nimbostratus", 3600),
      Map.entry("odyssey", 5483),
      Map.entry("on_the_road", 3423),
      Map.entry("one_can_dream_to_fly", 7260),
      Map.entry("one_thousand_stars", 3303),
      Map.entry("outlaw", 3519),
      Map.entry("passenger", 4800),
      Map.entry("protect_our_home", 5177),
      Map.entry("remember_our_past", 5463),
      Map.entry("rimmas", 5870),
      Map.entry("rimraid", 4383),
      Map.entry("rise", 3240),
      Map.entry("rustic_ranching", 4953),
      Map.entry("serene_night", 5064),
      Map.entry("serenity", 4705),
      Map.entry("simple_life", 3197),
      Map.entry("solitude", 2456),
      Map.entry("spacekattommy", 3459),
      Map.entry("stay_strong", 5174),
      Map.entry("strange_feeling", 1337),
      Map.entry("sunlight", 5420),
      Map.entry("the_fallen", 1872),
      Map.entry("tough_life", 3017),
      Map.entry("triumph", 7159),
      Map.entry("trust_me", 7344),
      Map.entry("united", 4224),
      Map.entry("valiant", 4721),
      Map.entry("we_will", 3291),
      Map.entry("what_we_once_had", 3670),
      Map.entry("a_twisted_path", 6081),
      Map.entry("blood_rain_falling", 7626),
      Map.entry("have_they_come_for_us", 9513)
   );
   private static final int TRACK_WATCHDOG_MARGIN = 400;
   private static final long NIGHT_START = 13000L;
   private static final long NIGHT_END = 23000L;
   private static final RandomSource RANDOM = RandomSource.m_216327_();
   private static final Deque<MusicTrack> PRIORITY = new ArrayDeque<>();
   private static final List<MusicTrack> QUEUE = new ArrayList<>();
   private static final Set<String> WARNED = new HashSet<>();
   private static final Map<MusicTrack, Long> retryAfter = new HashMap<>();
   private static long playlistTick;
   @Nullable
   private static SoundInstance current;
   @Nullable
   private static MusicTrack currentTrack;
   @Nullable
   private static MusicTrack lastPlayed;
   @Nullable
   private static MusicTrack lastBattleTrack;
   private static int gapTicks = -1;
   private static int currentWaitTicks;
   private static boolean currentStarted;
   private static int activeTicks;
   private static int interruptStreak;
   @Nullable
   private static MusicTrack interruptedTrack;
   private static boolean initialized;
   private static int nightSustain;
   private static int coldSustain;
   private static boolean nightFired;
   private static boolean coldFired;
   private static int exclusiveCheckTimer;
   private static boolean backgroundAvailable;
   private static boolean battleAvailable;
   private static boolean configCacheValid;
   private static int configRefreshTimer;
   private static Set<String> disabledDimensionTracks = Set.of();
   @Nullable
   private static ResourceLocation currentDimension;
   @Nullable
   private static SoundInstance previewSound;
   @Nullable
   private static MusicTrack previewTrack;
   private static boolean previewIdle;
   private static boolean yieldingMusic;
   private static boolean advancementRequested;
   private static boolean holdingForBossBar;

   public static boolean controlsMusicManager(Minecraft mc) {
      return previewIdle || current != null || mc.f_91073_ != null && mc.f_91074_ != null && isConfigActive(mc);
   }

   public static boolean isEventMusicPlaying() {
      return currentTrack != null && currentTrack.isEventTrack();
   }

   public static void requestAdvancementMusic() {
      advancementRequested = true;
   }

   public static boolean externalAudioPlaying(Minecraft mc) {
      return MusicCompatibility.externalAudioPlaying(mc);
   }

   static boolean ownsSound(SoundInstance sound) {
      return sound instanceof MusicPlayerClient.PlayerSound;
   }

   private static boolean shouldYieldMusic(Minecraft mc) {
      return mc.f_91073_ != null && mc.f_91074_ != null && MusicCompatibility.preferredSituationalMusic(mc) != null || externalAudioPlaying(mc);
   }

   private static boolean isConfigActive(Minecraft mc) {
      if (!configCacheValid) {
         boolean enabled = SakuraClientConfig.isCustomMusicEnabled();
         backgroundAvailable = enabled
            && MusicTracks.all()
               .stream()
               .anyMatch(
                  track -> !track.isEventTrack()
                     && track.tag() != MusicTrack.Tag.BATTLE
                     && SakuraClientConfig.isMusicTrackEnabled(track)
                     && hasTrackAudio(mc, track)
               );
         battleAvailable = enabled
            && MusicTracks.battle().stream().anyMatch(track -> SakuraClientConfig.isMusicTrackEnabled(track) && hasTrackAudio(mc, track));
         disabledDimensionTracks = SakuraClientConfig.getDisabledDimensionTracks();
         configCacheValid = true;
      }

      return backgroundAvailable || battleAvailable && bossBarVisible(mc);
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         Minecraft mc = Minecraft.m_91087_();
         if (mc.m_91106_() != null && mc.f_91066_ != null) {
            if (mc.f_91073_ != null && mc.f_91074_ != null) {
               if (mc.m_91104_()) {
                  tickPreview(mc);
               } else {
                  playlistTick++;
                  if (++configRefreshTimer >= 20) {
                     configRefreshTimer = 0;
                     configCacheValid = false;
                  }

                  if (SakuraClientConfig.isCustomMusicEnabled()
                     && !(mc.f_91066_.m_92147_(SoundSource.MUSIC) <= 0.0F)
                     && !(mc.f_91066_.m_92147_(SoundSource.MASTER) <= 0.0F)) {
                     boolean backgroundEnabled = isConfigActive(mc);
                     if (!tickEventMusic(mc) && !tickPreview(mc)) {
                        if (!backgroundEnabled) {
                           if (initialized || current != null) {
                              goIdle(mc);
                           }
                        } else {
                           if (!initialized) {
                              initialized = true;
                              gapTicks = 20;
                           }

                           ResourceLocation dimension = mc.f_91073_.m_46472_().m_135782_();
                           if (!dimension.equals(currentDimension)) {
                              onDimensionChanged(mc, dimension);
                           }

                           boolean bossBar = battleAvailable && bossBarVisible(mc);
                           if (bossBar != holdingForBossBar) {
                              if (bossBar) {
                                 requeueCurrent(mc);
                              } else {
                                 if (isBattleTrack(currentTrack)) {
                                    stopCurrent(mc);
                                 }

                                 PRIORITY.removeIf(MusicPlayerClient::isBattleTrack);
                              }

                              holdingForBossBar = bossBar;
                              interruptStreak = 0;
                              interruptedTrack = null;
                              gapTicks = 0;
                           }

                           if (shouldYieldMusic(mc)) {
                              if (!yieldingMusic) {
                                 SakuraMod.LOGGER.debug("[Music] 等待优先音乐结束");
                                 yieldingMusic = true;
                              }

                              requeueCurrent(mc);
                              gapTicks = 0;
                           } else {
                              if (yieldingMusic) {
                                 yieldingMusic = false;
                                 gapTicks = 20;
                              }

                              if (!bossBar && ++exclusiveCheckTimer >= 10) {
                                 exclusiveCheckTimer = 0;
                                 updateExclusiveConditions(mc);
                              }

                              if (current != null) {
                                 if (!isTrackEnabledHere(mc, currentTrack)) {
                                    stopCurrent(mc);
                                    gapTicks = 20;
                                 } else {
                                    tickCurrent(mc);
                                 }
                              } else if (gapTicks > 0) {
                                 gapTicks--;
                              } else {
                                 if (gapTicks == 0) {
                                    gapTicks = -1;
                                 }

                                 if (bossBar) {
                                    startBattle(mc);
                                 } else {
                                    startNext(mc);
                                 }
                              }
                           }
                        }
                     }
                  } else {
                     advancementRequested = false;
                     if (initialized || current != null) {
                        goIdle(mc);
                     }

                     tickPreview(mc);
                  }
               }
            } else {
               if (initialized || current != null) {
                  shutdown(mc);
               }

               advancementRequested = false;
               configCacheValid = false;
               currentDimension = null;
               tickPreview(mc);
            }
         }
      }
   }

   private static boolean tickEventMusic(Minecraft mc) {
      boolean wantAdvancement = advancementRequested && canTryTrack(mc, MusicTracks.ANOMALY);
      if (!wantAdvancement) {
         advancementRequested = false;
      }

      MusicTrack weatherTrack = activeWeatherTrack(mc);
      if (!wantAdvancement && weatherTrack == null && !isEventMusicPlaying()) {
         return false;
      } else if (shouldYieldMusic(mc)) {
         if (isEventMusicPlaying()) {
            stopEventMusic(mc);
         }

         return false;
      } else if (wantAdvancement) {
         advancementRequested = false;
         startEventMusic(mc, MusicTracks.ANOMALY);
         return true;
      } else if (currentTrack == MusicTracks.ANOMALY && isTrackEnabledHere(mc, currentTrack)) {
         tickCurrent(mc);
         return true;
      } else if (weatherTrack != null && currentTrack == weatherTrack) {
         tickCurrent(mc);
         return true;
      } else {
         if (isEventMusicPlaying()) {
            stopEventMusic(mc);
         }

         if (weatherTrack == null) {
            return false;
         } else {
            startEventMusic(mc, weatherTrack);
            return true;
         }
      }
   }

   @Nullable
   private static MusicTrack activeWeatherTrack(Minecraft mc) {
      if (!WeatherVisuals.inOverworld()) {
         return null;
      } else if (BloodRainClientState.isActive() && canTryTrack(mc, MusicTracks.BLOOD_RAIN)) {
         return MusicTracks.BLOOD_RAIN;
      } else {
         return (MistClientState.isActive() || currentTrack == MusicTracks.MIST && MistClientState.getIntensity() > 0.0F) && canTryTrack(mc, MusicTracks.MIST)
            ? MusicTracks.MIST
            : null;
      }
   }

   private static void startEventMusic(Minecraft mc, MusicTrack track) {
      requeueCurrent(mc);
      stopPreviewSound(mc);
      stopScheduledMusic(mc);
      playTrack(mc, track);
   }

   private static void stopEventMusic(Minecraft mc) {
      stopCurrent(mc);
      gapTicks = 20;
   }

   private static void tickCurrent(Minecraft mc) {
      if (mc.m_91106_().m_120403_(current)) {
         currentStarted = true;
         activeTicks++;
         Integer expected = currentTrack != null && !ClientDefaultMusicSync.syncedFiles().containsKey(currentTrack.id())
            ? TRACK_LENGTH_TICKS.get(currentTrack.id())
            : null;
         if (expected != null && activeTicks > expected + 400) {
            SakuraMod.LOGGER.warn("[Music] '{}' 超过预期时长仍未结束（{} tick），强制切歌", currentTrack == null ? "?" : currentTrack.id(), activeTicks);
            finishCurrent(mc, false, false);
         }
      } else if (!currentStarted) {
         if (++currentWaitTicks > 100) {
            finishCurrent(mc, true, true);
         }
      } else {
         finishCurrent(
            mc,
            currentTrack != null && !currentTrack.isCustom() && !ClientDefaultMusicSync.syncedFiles().containsKey(currentTrack.id()) && activeTicks < 200,
            false
         );
      }
   }

   private static void finishCurrent(Minecraft mc, boolean interrupted, boolean failedToStart) {
      MusicTrack playedTrack = currentTrack;
      int playedTicks = activeTicks;
      if (current != null) {
         mc.m_91106_().m_120399_(current);
      }

      current = null;
      currentTrack = null;
      currentStarted = false;
      currentWaitTicks = 0;
      activeTicks = 0;
      if (playedTrack != null && playedTrack.isEventTrack()) {
         if (interrupted) {
            retryAfter.put(playedTrack, playlistTick + 1200L);
         }

         gapTicks = 40;
      } else {
         if (interrupted && playedTrack != null) {
            if (interruptedTrack != playedTrack) {
               interruptStreak = 0;
            }

            interruptedTrack = playedTrack;
            interruptStreak++;
            if (failedToStart || interruptStreak > 2) {
               retryAfter.put(playedTrack, playlistTick + 1200L);
               PRIORITY.remove(playedTrack);
               if (playedTrack.tag() == MusicTrack.Tag.NIGHT || playedTrack.tag() == MusicTrack.Tag.COLD) {
                  PRIORITY.addLast(playedTrack);
               }

               interruptStreak = 0;
               interruptedTrack = null;
               gapTicks = 40;
               SakuraMod.LOGGER.warn("[Music] '{}' failed to play; trying another track", playedTrack.id());
               return;
            }

            int shift = Math.min(interruptStreak - 1, 4);
            gapTicks = Math.min(200, 100 << shift);
            if (!PRIORITY.contains(playedTrack)) {
               PRIORITY.addFirst(playedTrack);
            }

            SakuraMod.LOGGER.info("[Music] '{}' 被中断（只播了 {} tick），稍后重试（连续第 {} 次）", new Object[]{playedTrack.id(), playedTicks, interruptStreak});
         } else {
            if (!interrupted) {
               interruptStreak = 0;
               interruptedTrack = null;
            }

            gapTicks = 40;
            if (playedTrack != null) {
               SakuraMod.LOGGER.info("[Music] '{}' 播放结束（{} tick）", playedTrack.id(), playedTicks);
            }
         }
      }
   }

   private static void stopCurrent(Minecraft mc) {
      if (current != null) {
         mc.m_91106_().m_120399_(current);
      }

      current = null;
      currentTrack = null;
      currentStarted = false;
      currentWaitTicks = 0;
      activeTicks = 0;
   }

   private static void requeueCurrent(Minecraft mc) {
      if (current != null) {
         MusicTrack track = currentTrack;
         stopCurrent(mc);
         if (track != null && !track.isEventTrack() && !PRIORITY.contains(track)) {
            PRIORITY.addFirst(track);
         }
      }
   }

   public static boolean isPreviewing(MusicTrack track) {
      return previewSound != null && previewTrack == track;
   }

   public static void togglePreview(MusicTrack track) {
      Minecraft mc = Minecraft.m_91087_();
      SoundManager soundManager = mc.m_91106_();
      if (soundManager != null) {
         boolean sameTrack = previewTrack == track && previewSound != null;
         stopPreviewSound(mc);
         if (!sameTrack && !isEventMusicPlaying() && !shouldYieldMusic(mc)) {
            if (!hasTrackAudio(mc, track)) {
               warnOnce(track);
            } else {
               requeueCurrent(mc);
               stopScheduledMusic(mc);
               previewIdle = true;
               previewTrack = track;
               previewSound = new MusicPlayerClient.PlayerSound(track);
               soundManager.m_120367_(previewSound);
               SakuraMod.LOGGER.info("[Music] 试听 '{}'", track.id());
            }
         }
      }
   }

   private static boolean tickPreview(Minecraft mc) {
      if (previewSound == null) {
         if (previewIdle && mc.f_91080_ == null) {
            previewIdle = false;
            gapTicks = 20;
         }

         return previewIdle;
      } else {
         if (shouldYieldMusic(mc) || !mc.m_91106_().m_120403_(previewSound)) {
            stopPreviewSound(mc);
         }

         return true;
      }
   }

   private static void stopScheduledMusic(Minecraft mc) {
      if (mc.m_91397_() instanceof MusicCompatibility.ScheduledMusic scheduler && scheduler.sakura$getScheduledMusic() != null) {
         mc.m_91397_().m_120186_();
      }
   }

   private static void stopPreviewSound(Minecraft mc) {
      if (previewSound != null && mc.m_91106_() != null) {
         mc.m_91106_().m_120399_(previewSound);
      }

      previewSound = null;
      previewTrack = null;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onPlaySound(PlaySoundEvent event) {
      SoundInstance sound = event.getSound();
      if (sound != null && MusicCompatibility.isIssCswMusic(sound) && SakuraClientConfig.isIssCswMusicDisabled()) {
         event.setSound(null);
      } else {
         SoundInstance original = event.getOriginalSound();
         if (sound == null
            && original != null
            && (original == current || original == previewSound)
            && ModList.get().isLoaded("iss_csw")
            && !shouldYieldMusic(Minecraft.m_91087_())) {
            event.setSound(original);
         }
      }
   }

   private static boolean bossBarVisible(Minecraft mc) {
      if (mc.f_91065_ == null) {
         return false;
      } else {
         return mc.f_91065_.m_93090_() instanceof BossHealthOverlayAccessor accessor ? !accessor.sakura$getEvents().isEmpty() : false;
      }
   }

   private static boolean isBattleTrack(@Nullable MusicTrack track) {
      return track != null && track.tag() == MusicTrack.Tag.BATTLE;
   }

   private static void startBattle(Minecraft mc) {
      MusicTrack queued = null;
      Iterator<MusicTrack> iterator = PRIORITY.iterator();

      while (iterator.hasNext()) {
         MusicTrack candidate = iterator.next();
         if (isBattleTrack(candidate)) {
            iterator.remove();
            if (canTryTrack(mc, candidate)) {
               queued = candidate;
            }
            break;
         }
      }

      MusicTrack track = queued != null ? queued : pickBattleTrack(mc);
      if (track != null) {
         playTrack(mc, track);
      } else {
         gapTicks = 20;
      }
   }

   @Nullable
   private static MusicTrack pickBattleTrack(Minecraft mc) {
      List<MusicTrack> candidates = new ArrayList<>();

      for (MusicTrack track : MusicTracks.battle()) {
         if (canTryTrack(mc, track) && track != lastBattleTrack) {
            candidates.add(track);
         }
      }

      if (candidates.isEmpty()) {
         for (MusicTrack trackx : MusicTracks.battle()) {
            if (canTryTrack(mc, trackx)) {
               candidates.add(trackx);
            }
         }
      }

      if (candidates.isEmpty()) {
         return null;
      } else {
         MusicTrack chosen = candidates.get(RANDOM.m_188503_(candidates.size()));
         lastBattleTrack = chosen;
         return chosen;
      }
   }

   private static void startNext(Minecraft mc) {
      Iterator<MusicTrack> it = PRIORITY.iterator();

      while (it.hasNext()) {
         MusicTrack track = it.next();
         if (!isTrackEnabledHere(mc, track)) {
            it.remove();
         } else if (canTryTrack(mc, track)) {
            it.remove();
            playTrack(mc, track);
            return;
         }
      }

      if (QUEUE.isEmpty()) {
         refillQueue(mc);
      }

      while (!QUEUE.isEmpty()) {
         MusicTrack track = QUEUE.remove(0);
         if (canTryTrack(mc, track)) {
            playTrack(mc, track);
            return;
         }
      }

      gapTicks = 20;
   }

   private static void refillQueue(Minecraft mc) {
      QUEUE.clear();

      for (MusicTrack track : MusicTracks.normal()) {
         if (canTryTrack(mc, track)) {
            QUEUE.add(track);
         }
      }

      for (int i = QUEUE.size() - 1; i > 0; i--) {
         Collections.swap(QUEUE, i, RANDOM.m_188503_(i + 1));
      }

      if (QUEUE.size() > 1 && QUEUE.get(0) == lastPlayed) {
         Collections.swap(QUEUE, 0, 1 + RANDOM.m_188503_(QUEUE.size() - 1));
      }
   }

   private static void playTrack(Minecraft mc, MusicTrack track) {
      current = new MusicPlayerClient.PlayerSound(track);
      currentTrack = track;
      currentStarted = false;
      currentWaitTicks = 0;
      activeTicks = 0;
      if (!track.isEventTrack()) {
         lastPlayed = track;
      }

      mc.m_91106_().m_120367_(current);
      SakuraMod.LOGGER.info("[Music] 开始播放 '{}'", track.id());
   }

   private static boolean hasTrackAudio(Minecraft mc, MusicTrack track) {
      WeighedSoundEvents registration = MusicAudioReload.registration(mc.m_91106_(), track.soundLocation());
      if (registration == null) {
         return false;
      } else {
         Sound sound = registration.m_213718_(RANDOM);
         return sound != SoundManager.f_120344_ && sound != SoundManager.f_271451_;
      }
   }

   private static void warnOnce(MusicTrack track) {
      if (WARNED.add(track.id())) {
         SakuraMod.LOGGER
            .warn("[Music] Track '{}' unavailable; check {}", track.displayName(), track.isCustom() ? "sakura_music/custom" : "sakura_music/default");
      }
   }

   private static boolean isTrackEnabledHere(Minecraft mc, MusicTrack track) {
      if (!SakuraClientConfig.isMusicTrackEnabled(track)) {
         return false;
      } else if (disabledDimensionTracks.isEmpty()) {
         return true;
      } else {
         ResourceLocation dimension = mc.f_91073_.m_46472_().m_135782_();
         return !disabledDimensionTracks.contains(SakuraClientConfig.dimensionTrackKey(dimension, track));
      }
   }

   private static boolean canTryTrack(Minecraft mc, MusicTrack track) {
      return isTrackEnabledHere(mc, track) && playlistTick >= retryAfter.getOrDefault(track, 0L) && hasTrackAudio(mc, track);
   }

   private static void onDimensionChanged(Minecraft mc, ResourceLocation dimension) {
      currentDimension = dimension;
      PRIORITY.clear();
      QUEUE.clear();
      nightSustain = 0;
      coldSustain = 0;
      nightFired = false;
      coldFired = false;
      exclusiveCheckTimer = 0;
      if (currentTrack != null && !isTrackEnabledHere(mc, currentTrack)) {
         stopCurrent(mc);
         gapTicks = 20;
      }
   }

   private static void updateExclusiveConditions(Minecraft mc) {
      if (isNight(mc.f_91073_)) {
         if (!nightFired && ++nightSustain >= 2) {
            nightFired = true;
            enqueueExclusive(mc, MusicTracks.SERENE_NIGHT);
         }
      } else {
         nightSustain = 0;
         nightFired = false;
      }

      if (isCold(mc)) {
         if (!coldFired && ++coldSustain >= 2) {
            coldFired = true;
            enqueueExclusive(mc, MusicTracks.RIMMAS);
         }
      } else {
         coldSustain = 0;
         coldFired = false;
      }
   }

   private static void enqueueExclusive(Minecraft mc, MusicTrack track) {
      if (isTrackEnabledHere(mc, track)) {
         if (track != currentTrack) {
            if (!PRIORITY.contains(track)) {
               PRIORITY.addLast(track);
               if (current == null && gapTicks > 0) {
                  gapTicks = 0;
               }
            }
         }
      }
   }

   private static boolean isNight(Level level) {
      if (!level.m_6042_().f_223549_()) {
         return false;
      } else if (level.m_6042_().m_63967_()) {
         return false;
      } else {
         long time = level.m_46468_() % 24000L;
         return time >= 13000L && time < 23000L;
      }
   }

   private static boolean isCold(Minecraft mc) {
      BlockPos pos = mc.f_91074_.m_20183_();
      Holder<Biome> biome = mc.f_91073_.m_204166_(pos);
      return ((Biome)biome.m_203334_()).m_198904_(pos);
   }

   private static void clearPlayback() {
      retryAfter.clear();
      playlistTick = 0L;
      PRIORITY.clear();
      QUEUE.clear();
      current = null;
      currentTrack = null;
      currentStarted = false;
      currentWaitTicks = 0;
      activeTicks = 0;
      interruptStreak = 0;
      interruptedTrack = null;
      holdingForBossBar = false;
      yieldingMusic = false;
      gapTicks = -1;
      exclusiveCheckTimer = 0;
      nightSustain = 0;
      coldSustain = 0;
      nightFired = false;
      coldFired = false;
      initialized = false;
   }

   private static void goIdle(Minecraft mc) {
      stopCurrent(mc);
      clearPlayback();
   }

   private static void shutdown(Minecraft mc) {
      goIdle(mc);
      currentDimension = null;
   }

   static void onMusicLibraryReloaded() {
      configCacheValid = false;
      Minecraft mc = Minecraft.m_91087_();
      if (mc != null && mc.m_91106_() != null) {
         goIdle(mc);
         stopPreviewSound(mc);
         previewIdle = false;
      }
   }

   @SubscribeEvent
   public static void onScreenClosing(Closing event) {
      if (previewSound != null || previewIdle) {
         Minecraft mc = Minecraft.m_91087_();
         stopPreviewSound(mc);
         previewIdle = false;
         gapTicks = 20;
      }
   }

   @SubscribeEvent
   public static void onLoggingOut(LoggingOut event) {
      Minecraft mc = Minecraft.m_91087_();
      if (mc.m_91106_() != null) {
         stopCurrent(mc);
      }

      stopPreviewSound(mc);
      previewIdle = false;
      clearPlayback();
      currentDimension = null;
      configCacheValid = false;
      advancementRequested = false;
   }

   private MusicPlayerClient() {
   }

   private static final class PlayerSound extends SimpleSoundInstance {
      private final MusicAudioReload.Audio audio = MusicAudioReload.audio();

      PlayerSound(MusicTrack track) {
         super(track.soundLocation(), SoundSource.MUSIC, 1.0F, 1.0F, SoundInstance.m_235150_(), false, 0, Attenuation.NONE, 0.0, 0.0, 0.0, true);
      }

      public WeighedSoundEvents m_6775_(SoundManager manager) {
         if (this.audio == null) {
            return super.m_6775_(manager);
         } else {
            WeighedSoundEvents registration = this.audio.registry().get(this.m_7904_());
            this.f_119570_ = registration == null ? SoundManager.f_120344_ : registration.m_213718_(this.f_235066_);
            if (this.f_119570_ != SoundManager.f_120344_ && this.f_119570_ != SoundManager.f_271451_ && !this.f_119570_.m_119796_()) {
               this.f_119570_ = new Sound(
                  this.f_119570_.m_119787_().toString(),
                  this.f_119570_.m_235146_(),
                  this.f_119570_.m_235147_(),
                  this.f_119570_.m_7789_(),
                  Type.FILE,
                  true,
                  false,
                  this.f_119570_.m_119798_()
               );
            }

            return registration;
         }
      }

      public CompletableFuture<AudioStream> getStream(SoundBufferLibrary buffers, Sound selected, boolean loop) {
         return this.audio == null ? super.getStream(buffers, selected, loop) : this.audio.buffers().m_120204_(selected.m_119790_(), loop);
      }
   }
}
