package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.damage.ModDamageTypes;
import com.Luo_jiyv.SakuraMod.effect.ModEffects;
import com.Luo_jiyv.SakuraMod.item.GoldenCubeItem;
import com.Luo_jiyv.SakuraMod.item.Moditems;
import com.Luo_jiyv.SakuraMod.network.GoldenCubeMadnessPacket;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.util.AdvancementUtils;
import com.Luo_jiyv.SakuraMod.util.GoldenCubeData;
import com.Luo_jiyv.SakuraMod.util.PlayerEquipCache;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent.Applicable;
import net.minecraftforge.event.entity.living.MobEffectEvent.Remove;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class GoldenCubeEvents {
   private static final Random RNG = new Random();
   private static final UUID SLOW_UUID = GoldenCubeData.WITHDRAWAL_SLOW_UUID;
   private static final UUID LEGACY_WEAK_UUID = UUID.fromString("6a3f7c2e-1111-4a2b-9c3d-000000000002");
   private static final int RESIST_DURATION = 40;
   private static final int GOLDEN_DURATION = 6000;
   private static final int PLAY_COOLDOWN_TICKS = 40;
   private static final String POOL_NAME = "sakura_golden_cube";
   private static final ThreadLocal<Map<Player, Integer>> INTERNAL_REMOVALS = ThreadLocal.withInitial(IdentityHashMap::new);
   private static final Map<UUID, Integer> LAST_MODE = new HashMap<>();
   private static final Map<UUID, Integer> LAST_PENALTY_LEVEL = new HashMap<>();

   private static void beginInternalRemoval(Player player) {
      INTERNAL_REMOVALS.get().merge(player, 1, Integer::sum);
   }

   private static void endInternalRemoval(Player player) {
      Map<Player, Integer> removals = INTERNAL_REMOVALS.get();
      int depth = removals.getOrDefault(player, 0) - 1;
      if (depth <= 0) {
         removals.remove(player);
      } else {
         removals.put(player, depth);
      }

      if (removals.isEmpty()) {
         INTERNAL_REMOVALS.remove();
      }
   }

   public static void playWithCube(Player player) {
      if (!player.m_9236_().m_5776_()) {
         if (player.m_6084_() && isHoldingCube(player)) {
            if (!player.m_7500_() && !player.m_5833_()) {
               if (Config.isGoldenCubeEnabled()) {
                  if (!refuseCubeUse(player)) {
                     if (!player.m_36335_().m_41519_((Item)Moditems.GOLDEN_CUBE.get())) {
                        GoldenCubeData.setActivated(player, true);
                        long now = player.m_9236_().m_46467_();
                        boolean inWithdrawal = GoldenCubeData.withdrawal(player) > 0;

                        for (MobEffectInstance inst : player.m_21220_().stream().toList()) {
                           if (inst.m_19544_().m_19483_() == MobEffectCategory.HARMFUL && !ModEffects.isGoldenEffect(inst.m_19544_())) {
                              player.m_21195_(inst.m_19544_());
                           }
                        }

                        player.m_36324_().m_38717_(player.m_36324_().m_38702_());
                        player.m_7292_(new MobEffectInstance(MobEffects.f_19605_, 1200, 2, false, false, true));
                        float severity = Math.max(0.0F, GoldenCubeData.severity(player));
                        severity += 0.01F + RNG.nextFloat() * 0.015F;
                        GoldenCubeData.setSeverity(player, severity);
                        GoldenCubeData.setLastPlay(player, now);
                        updateAddictionEffect(player);
                        if (hasAddictionEffect(player)) {
                           GoldenCubeData.setPlayCount(player, GoldenCubeData.playCount(player) + 1);
                        }

                        GoldenCubeData.setNextDue(player, now + dueInterval(player));
                        resetClientMadness(player);
                        if (inWithdrawal) {
                           beginInternalRemoval(player);

                           try {
                              player.m_21195_((MobEffect)ModEffects.GOLDEN_WITHDRAWAL.get());
                           } finally {
                              endInternalRemoval(player);
                           }

                           GoldenCubeData.setWithdrawal(player, 0);
                           GoldenCubeData.setWithdrawalTicks(player, 0L);
                           GoldenCubeData.setLevel5Ticks(player, 0L);
                           clearAttributePenalties(player);
                           player.m_5661_(Component.m_237113_("……它回来了。").m_130944_(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}), true);
                           playSound(player, SoundEvents.f_144243_, 1.0F, 1.4F);
                        }

                        player.m_36335_().m_41524_((Item)Moditems.GOLDEN_CUBE.get(), 40);
                        playSound(player, SoundEvents.f_144243_, 1.0F, 1.2F);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean refuseCubeUse(Player player) {
      if (!GoldenCubeData.isImmune(player) && !GoldenCubeData.isRejected(player)) {
         return false;
      } else {
         player.m_5661_(Component.m_237113_("我不能使用这个邪恶的东西").m_130944_(new ChatFormatting[]{ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC}), true);
         return true;
      }
   }

   private static boolean hasAddictionEffect(Player player) {
      for (MobEffect effect : ModEffects.allAddictions()) {
         if (player.m_21023_(effect)) {
            return true;
         }
      }

      return false;
   }

   private static boolean grantAnomaly(Player player) {
      if (player.m_7500_() || player.m_5833_()) {
         return false;
      } else if (player instanceof ServerPlayer sp) {
         AdvancementUtils.award(sp, "sakura_mod:story/golden_cube_anomaly", "trigger");
         return true;
      } else {
         return false;
      }
   }

   private static boolean hasShard(Player player) {
      Item shard = (Item)Moditems.DARK_TRANSCENDENT_SHARD.get();

      for (ItemStack stack : player.m_150109_().f_35974_) {
         if (!stack.m_41619_() && stack.m_41720_() == shard) {
            return true;
         }
      }

      return player.m_21206_().m_41720_() == shard;
   }

   private static long dueInterval(Player player) {
      int plays = GoldenCubeData.playCount(player);
      return plays <= 200 ? 24000L : 24000L - 23400L * (plays - 200) / 800L;
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END) {
         Player player = event.player;
         if (!player.m_9236_().m_5776_()) {
            GoldenCubeData.checkpointTimers(player);
            if (player.m_6084_()) {
               if (!player.m_7500_() && !player.m_5833_() && Config.isGoldenCubeEnabled()) {
                  if (GoldenCubeData.isRejected(player)) {
                     if (player.f_19797_ % 20 == 0) {
                        if (hasGoldenState(player)) {
                           clearAllState(player);
                        }

                        dropCubes(player);
                     }
                  } else {
                     boolean activated = GoldenCubeData.isActivated(player);
                     if (!activated && player.f_19797_ % 20 == 0 && hasUnverifiedEffects(player)) {
                        clearUnverifiedState(player);
                     }

                     long now = player.m_9236_().m_46467_();
                     int withdrawal = GoldenCubeData.withdrawal(player);
                     boolean immune = GoldenCubeData.isImmune(player);
                     float severity = GoldenCubeData.severity(player);
                     if (player.f_19797_ % 10 == 0) {
                        syncMadness(player, now);
                        boolean hasCube = hasCube(player);
                        boolean owned = GoldenCubeData.ownedCube(player);
                        if (hasCube && !owned) {
                           GoldenCubeData.setOwnedCube(player, true);
                        } else if (!hasCube && owned) {
                           GoldenCubeData.setOwnedCube(player, false);
                        }

                        if (!immune && !GoldenCubeData.anomalyGranted(player) && (hasCube || hasShard(player)) && grantAnomaly(player)) {
                           GoldenCubeData.setAnomalyGranted(player, true);
                        }
                     }

                     if (!immune && isHoldingCube(player)) {
                        MobEffectInstance resist = player.m_21124_(MobEffects.f_19606_);
                        if (resist == null || resist.m_19557_() < 20) {
                           player.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 40, 0, false, false, false));
                        }
                     }

                     if (player.f_19797_ % 20 == 0 && activated && !immune && severity > 0.0F) {
                        refreshAddiction(player);
                     }

                     if (immune) {
                        if (player.f_19797_ % 20 == 0) {
                           clearAddictionEffects(player);
                        }
                     } else if (activated) {
                        if (withdrawal > 0) {
                           tickWithdrawal(player, now);
                        } else if (!(severity <= 0.0F)) {
                           long lastPlay = GoldenCubeData.lastPlay(player);
                           if (lastPlay <= 0L) {
                              GoldenCubeData.setLastPlay(player, now);
                              lastPlay = now;
                           }

                           long due = GoldenCubeData.nextDue(player);
                           if (due <= 0L) {
                              due = lastPlay + dueInterval(player);
                              GoldenCubeData.setNextDue(player, due);
                           }

                           long overdue = now - due;
                           if (overdue >= 0L) {
                              if (overdue >= 1200L) {
                                 GoldenCubeData.setWithdrawal(player, 1);
                                 GoldenCubeData.setWithdrawalTicks(player, 0L);
                                 GoldenCubeData.setLevel5Ticks(player, 0L);
                                 applyWithdrawalEffect(player, 1);
                                 syncMadness(player, now);
                              }
                           }
                        }
                     }
                  }
               } else {
                  if (player.f_19797_ % 20 == 0 && hasGoldenState(player)) {
                     clearAllState(player);
                  }
               }
            }
         }
      }
   }

   private static void syncMadness(Player player, long now) {
      int withdrawal = GoldenCubeData.withdrawal(player);
      int mode;
      if (!isCubeActive(player)) {
         mode = 0;
      } else if (withdrawal > 0) {
         mode = GoldenCubeData.isMeltingDown(player) ? 7 : withdrawal + 1;
      } else if (isOverdueForMadness(player, now)) {
         mode = 1;
      } else {
         mode = 0;
      }

      Integer last = LAST_MODE.get(player.m_20148_());
      if (last == null || last != mode) {
         LAST_MODE.put(player.m_20148_(), mode);
         if (player instanceof ServerPlayer sp) {
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new GoldenCubeMadnessPacket(mode));
         }
      }
   }

   private static boolean isOverdueForMadness(Player player, long now) {
      if (GoldenCubeData.isImmune(player)) {
         return false;
      } else if (GoldenCubeData.severity(player) <= 0.0F) {
         return false;
      } else {
         long due = GoldenCubeData.nextDue(player);
         if (due <= 0L) {
            return false;
         } else {
            long overdue = now - due;
            return overdue >= 0L && overdue < 1200L;
         }
      }
   }

   private static void tickWithdrawal(Player player, long now) {
      int level = GoldenCubeData.withdrawal(player);
      long ticks = GoldenCubeData.withdrawalTicks(player);
      if (ticks <= 0L && level > 1) {
         ticks = (level - 1) * 600L;
      }

      GoldenCubeData.setWithdrawalTicks(player, ++ticks);
      int expected = Math.min(5, 1 + (int)(ticks / 600L));
      if (expected > level) {
         level = expected;
         GoldenCubeData.setWithdrawal(player, expected);
         syncMadness(player, now);
      }

      if (now % 20L == 0L || LAST_PENALTY_LEVEL.getOrDefault(player.m_20148_(), 0) != level) {
         applyWithdrawalEffect(player, level);
      }

      if (level < 5) {
         if (GoldenCubeData.level5Ticks(player) != 0L) {
            GoldenCubeData.setLevel5Ticks(player, 0L);
         }
      } else {
         long level5Ticks = GoldenCubeData.level5Ticks(player) + 1L;
         GoldenCubeData.setLevel5Ticks(player, level5Ticks);
         if (level5Ticks >= 2600L) {
            survivedWithdrawal(player, now);
         } else if (level5Ticks >= 200L) {
            if (level5Ticks == 200L) {
               syncMadness(player, now);
            }

            int tier = GoldenCubeData.tierFor(GoldenCubeData.severity(player));
            if ((level5Ticks - 200L) % GoldenCubeData.meltdownInterval(tier) == 0L) {
               meltdown(player, tier);
            }
         }
      }
   }

   private static void meltdown(Player player, int tier) {
      Reference<DamageType> holder = player.m_9236_().m_9598_().m_175515_(Registries.f_268580_).m_246971_(ModDamageTypes.GOLDEN_CUBE_MELTDOWN);
      DamageSource source = new DamageSource(holder);
      float damage = player.m_21233_() * GoldenCubeData.meltdownDamageRatio(tier);
      player.m_21231_().m_289194_(source, damage);
      player.m_21153_(Math.max(0.0F, player.m_21223_() - damage));
      if (!player.m_6084_()) {
         player.m_6667_(source);
      }
   }

   private static void survivedWithdrawal(Player player, long now) {
      beginInternalRemoval(player);

      try {
         player.m_21195_((MobEffect)ModEffects.GOLDEN_WITHDRAWAL.get());

         for (MobEffect other : ModEffects.allAddictions()) {
            player.m_21195_(other);
         }
      } finally {
         endInternalRemoval(player);
      }

      GoldenCubeData.setWithdrawal(player, 0);
      GoldenCubeData.setWithdrawalTicks(player, 0L);
      GoldenCubeData.setLevel5Ticks(player, 0L);
      GoldenCubeData.setSeverity(player, 0.0F);
      GoldenCubeData.setImmunityUntil(player, now + 864000L);
      clearAttributePenalties(player);
      resetClientMadness(player);
      if (player instanceof ServerPlayer sp) {
         AdvancementUtils.award(sp, "sakura_mod:story/golden_cube_survivor", "trigger");
      }

      playSound(player, SoundEvents.f_12556_, 1.0F, 1.0F);
      player.m_5661_(Component.m_237113_("……你撑过来了。").m_130944_(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.BOLD}), true);
   }

   private static void updateAddictionEffect(Player player) {
      if (isCubeActive(player)) {
         int tier = GoldenCubeData.tierFor(GoldenCubeData.severity(player));
         MobEffect target = ModEffects.addictionFor(tier);
         beginInternalRemoval(player);

         try {
            for (MobEffect other : ModEffects.allAddictions()) {
               MobEffectInstance existing = player.m_21124_(other);
               if (other != target || existing != null && existing.m_19564_() != 0) {
                  player.m_21195_(other);
               }
            }
         } finally {
            endInternalRemoval(player);
         }

         player.m_7292_(new MobEffectInstance(target, 6000, 0, false, false, true));
         if (tier == 0 && !GoldenCubeData.announced(player) && player instanceof ServerPlayer sp && sp.m_20194_() != null) {
            GoldenCubeData.setAnnounced(player, true);
            sp.m_20194_().m_6846_().m_240416_(Component.m_237113_("<" + sp.m_36316_().getName() + "> 它真美。").m_130940_(ChatFormatting.WHITE), false);
         }
      }
   }

   private static void refreshAddiction(Player player) {
      int tier = GoldenCubeData.tierFor(GoldenCubeData.severity(player));
      MobEffect target = ModEffects.addictionFor(tier);
      MobEffectInstance current = player.m_21124_(target);
      if (current == null || current.m_19564_() != 0 || current.m_19557_() < 5800) {
         updateAddictionEffect(player);
      }
   }

   private static void applyWithdrawalEffect(Player player, int level) {
      if (!isCubeActive(player)) {
         clearAttributePenalties(player);
      } else {
         Integer last = LAST_PENALTY_LEVEL.get(player.m_20148_());
         MobEffectInstance current = player.m_21124_((MobEffect)ModEffects.GOLDEN_WITHDRAWAL.get());
         boolean wrongLevel = current != null && current.m_19564_() != level - 1;
         boolean needEffect = current == null || wrongLevel || current.m_19557_() < 4800;
         boolean needAttributes = last == null || last != level;
         if (needEffect || needAttributes) {
            LAST_PENALTY_LEVEL.put(player.m_20148_(), level);
            if (needEffect) {
               if (wrongLevel) {
                  beginInternalRemoval(player);

                  try {
                     player.m_21195_((MobEffect)ModEffects.GOLDEN_WITHDRAWAL.get());
                  } finally {
                     endInternalRemoval(player);
                  }
               }

               player.m_7292_(new MobEffectInstance((MobEffect)ModEffects.GOLDEN_WITHDRAWAL.get(), 6000, Math.max(0, level - 1), false, false, true));
            }

            if (needAttributes) {
               applyAttributePenalties(player, level);
            }
         }
      }
   }

   private static void applyAttributePenalties(Player player, int level) {
      double slowAmount = -0.1 * Math.min(5, level);
      AttributeInstance speed = player.m_21051_(Attributes.f_22279_);
      if (speed != null) {
         AttributeModifier existing = speed.m_22111_(SLOW_UUID);
         if (level <= 0) {
            if (existing != null) {
               speed.m_22120_(SLOW_UUID);
            }
         } else if (existing == null || Math.abs(existing.m_22218_() - slowAmount) > 0.001 || existing.m_22217_() != Operation.MULTIPLY_TOTAL) {
            speed.m_22120_(SLOW_UUID);
            speed.m_22118_(new AttributeModifier(SLOW_UUID, "golden_cube_slow", slowAmount, Operation.MULTIPLY_TOTAL));
         }
      }

      AttributeInstance attack = player.m_21051_(Attributes.f_22281_);
      if (attack != null) {
         attack.m_22120_(LEGACY_WEAK_UUID);
      }
   }

   private static void clearAttributePenalties(Player player) {
      LAST_PENALTY_LEVEL.remove(player.m_20148_());
      applyAttributePenalties(player, 0);
   }

   private static void clearAddictionEffects(Player player) {
      clearAttributePenalties(player);
      beginInternalRemoval(player);

      try {
         for (MobEffect other : ModEffects.allAddictions()) {
            player.m_21195_(other);
         }

         player.m_21195_((MobEffect)ModEffects.GOLDEN_WITHDRAWAL.get());
      } finally {
         endInternalRemoval(player);
      }
   }

   public static void clearAll(Player player) {
      clearAddictionEffects(player);
      GoldenCubeData.clearAll(player);
      GoldenCubeData.setRejected(player, false);
      resetClientMadness(player);
   }

   private static void clearAllState(Player player) {
      clearAddictionEffects(player);
      GoldenCubeData.clearAll(player);
      resetClientMadness(player);
   }

   private static void resetClientMadness(Player player) {
      LAST_MODE.remove(player.m_20148_());
      if (player instanceof ServerPlayer sp) {
         ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new GoldenCubeMadnessPacket(0));
      }
   }

   private static boolean hasGoldenState(Player player) {
      if (GoldenCubeData.withdrawal(player) <= 0
         && !(GoldenCubeData.severity(player) > 0.0F)
         && !GoldenCubeData.ownedCube(player)
         && !GoldenCubeData.isActivated(player)
         && GoldenCubeData.immunityUntil(player) == 0L
         && GoldenCubeData.nextDue(player) == 0L
         && GoldenCubeData.playCount(player) <= 0
         && LAST_MODE.getOrDefault(player.m_20148_(), 0) == 0
         && !LAST_PENALTY_LEVEL.containsKey(player.m_20148_())) {
         if (player.m_21124_((MobEffect)ModEffects.GOLDEN_WITHDRAWAL.get()) != null) {
            return true;
         } else {
            for (MobEffect effect : ModEffects.allAddictions()) {
               if (player.m_21124_(effect) != null) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   private static boolean hasUnverifiedEffects(Player player) {
      return GoldenCubeData.severity(player) > 0.0F
         || GoldenCubeData.withdrawal(player) > 0
         || GoldenCubeData.nextDue(player) != 0L
         || hasAddictionEffect(player)
         || player.m_21023_((MobEffect)ModEffects.GOLDEN_WITHDRAWAL.get())
         || LAST_PENALTY_LEVEL.containsKey(player.m_20148_());
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onEffectApplicable(Applicable event) {
      if (event.getEntity() instanceof Player player) {
         if (!player.m_9236_().m_5776_()) {
            MobEffectInstance inst = event.getEffectInstance();
            if (inst != null && ModEffects.isGoldenEffect(inst.m_19544_())) {
               boolean matchesState = inst.m_19544_() == ModEffects.GOLDEN_WITHDRAWAL.get()
                  ? GoldenCubeData.withdrawal(player) > 0 && inst.m_19564_() == GoldenCubeData.withdrawal(player) - 1
                  : GoldenCubeData.severity(player) > 0.0F
                     && inst.m_19564_() == 0
                     && inst.m_19544_() == ModEffects.addictionFor(GoldenCubeData.tierFor(GoldenCubeData.severity(player)));
               if (!isCubeActive(player) || !matchesState) {
                  event.setResult(Result.DENY);
               }
            }
         }
      }
   }

   private static boolean isCubeActive(Player player) {
      return Config.isGoldenCubeEnabled()
         && player.m_6084_()
         && !player.m_7500_()
         && !player.m_5833_()
         && GoldenCubeData.isActivated(player)
         && !GoldenCubeData.isRejected(player)
         && !GoldenCubeData.isImmune(player);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onRejectRightClickItem(RightClickItem event) {
      tryRejectCube(event);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onRejectRightClickBlock(RightClickBlock event) {
      tryRejectCube(event);
   }

   @SubscribeEvent
   public static void onRejectEntityInteract(EntityInteract event) {
      tryRejectCube(event);
   }

   private static void tryRejectCube(PlayerInteractEvent event) {
      Player player = event.getEntity();
      if (!player.m_9236_().m_5776_()) {
         if (isCubeActive(player)) {
            if (hasAddictionEffect(player)) {
               ItemStack main = player.m_21205_();
               ItemStack off = player.m_21206_();
               if (main.m_41720_() == Moditems.DARK_TRANSCENDENT_SHARD.get() && main.m_41613_() >= 64) {
                  if (off.m_41720_() == Items.f_42747_) {
                     event.setCanceled(true);
                     event.setCancellationResult(InteractionResult.SUCCESS);
                     main.m_41774_(64);
                     off.m_41774_(1);
                     player.m_21153_(Math.max(1.0F, player.m_21223_()));
                     applyRejection(player);
                     player.m_7292_(new MobEffectInstance(MobEffects.f_19617_, 100, 1, false, false, true));
                     player.m_7292_(new MobEffectInstance(MobEffects.f_19605_, 900, 1, false, false, true));
                     if (player.m_9236_() instanceof ServerLevel serverLevel) {
                        serverLevel.m_7605_(player, (byte)35);
                        serverLevel.m_8767_(ParticleTypes.f_123767_, player.m_20185_(), player.m_20186_() + 1.0, player.m_20189_(), 100, 1.0, 1.0, 1.0, 0.5);
                     }

                     player.m_9236_().m_5594_(null, player.m_20183_(), SoundEvents.f_12513_, SoundSource.PLAYERS, 1.0F, 1.0F);
                     player.m_5661_(Component.m_237113_("滚开！").m_130944_(new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD}), false);
                  }
               }
            }
         }
      }
   }

   private static void applyRejection(Player player) {
      clearAddictionEffects(player);
      GoldenCubeData.clearAll(player);
      GoldenCubeData.setRejected(player, true);
      resetClientMadness(player);
      dropCubes(player);
      if (player instanceof ServerPlayer sp) {
         AdvancementUtils.award(sp, "sakura_mod:story/golden_cube_rejected", "trigger");
      }
   }

   public static void debugSetSeverity(Player player, float severity) {
      if (severity > 0.0F) {
         GoldenCubeData.setActivated(player, true);
      }

      GoldenCubeData.setSeverity(player, severity);
      beginInternalRemoval(player);

      try {
         for (MobEffect effect : ModEffects.allAddictions()) {
            player.m_21195_(effect);
         }
      } finally {
         endInternalRemoval(player);
      }

      if (severity > 0.0F) {
         long now = player.m_9236_().m_46467_();
         GoldenCubeData.setLastPlay(player, now);
         GoldenCubeData.setNextDue(player, now + dueInterval(player));
         updateAddictionEffect(player);
      } else {
         debugSetWithdrawal(player, 0);
         GoldenCubeData.setNextDue(player, 0L);
         GoldenCubeData.setActivated(player, false);
      }
   }

   public static void debugSetImmunity(Player player, int seconds) {
      long until = player.m_9236_().m_46467_() + Math.max(0, seconds) * 20L;
      GoldenCubeData.setImmunityUntil(player, until);
      if (seconds > 0) {
         clearAddictionEffects(player);
         resetClientMadness(player);
      }
   }

   public static void debugSetPlayCount(Player player, int count) {
      GoldenCubeData.setPlayCount(player, count);
      if (GoldenCubeData.isActivated(player) && GoldenCubeData.severity(player) > 0.0F && GoldenCubeData.withdrawal(player) == 0) {
         GoldenCubeData.setNextDue(player, GoldenCubeData.lastPlay(player) + dueInterval(player));
         syncMadness(player, player.m_9236_().m_46467_());
      }
   }

   public static void debugSetWithdrawal(Player player, int level) {
      if (level <= 0) {
         beginInternalRemoval(player);

         try {
            player.m_21195_((MobEffect)ModEffects.GOLDEN_WITHDRAWAL.get());
         } finally {
            endInternalRemoval(player);
         }

         GoldenCubeData.setWithdrawal(player, 0);
         GoldenCubeData.setWithdrawalTicks(player, 0L);
         GoldenCubeData.setLevel5Ticks(player, 0L);
         clearAttributePenalties(player);
         GoldenCubeData.setNextDue(player, GoldenCubeData.severity(player) > 0.0F ? player.m_9236_().m_46467_() + dueInterval(player) : 0L);
         resetClientMadness(player);
      } else {
         int clamped = Math.min(5, level);
         GoldenCubeData.setActivated(player, true);
         GoldenCubeData.setWithdrawal(player, clamped);
         GoldenCubeData.setWithdrawalTicks(player, (clamped - 1) * 600L);
         GoldenCubeData.setLevel5Ticks(player, 0L);
         applyWithdrawalEffect(player, clamped);
         syncMadness(player, player.m_9236_().m_46467_());
      }
   }

   public static void debugReject(Player player) {
      applyRejection(player);
   }

   private static void dropCubes(Player player) {
      Inventory inventory = player.m_150109_();

      for (int i = 0; i < inventory.f_35974_.size(); i++) {
         ItemStack stack = (ItemStack)inventory.f_35974_.get(i);
         if (stack.m_41720_() instanceof GoldenCubeItem) {
            inventory.m_6836_(i, ItemStack.f_41583_);
            player.m_36176_(stack, false);
         }
      }

      ItemStack off = player.m_21206_();
      if (off.m_41720_() instanceof GoldenCubeItem) {
         player.m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
         player.m_36176_(off, false);
      }
   }

   @SubscribeEvent
   public static void onCubePickup(EntityItemPickupEvent event) {
      if (event.getItem().m_32055_().m_41720_() instanceof GoldenCubeItem) {
         if (GoldenCubeData.isRejected(event.getEntity())) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onEffectRemove(Remove event) {
      if (Config.isGoldenCubeEnabled()) {
         if (event.getEntity() instanceof Player player) {
            if (!player.m_9236_().m_5776_() && !INTERNAL_REMOVALS.get().containsKey(player)) {
               MobEffectInstance inst = event.getEffectInstance();
               if (inst != null) {
                  if (ModEffects.isGoldenEffect(inst.m_19544_())) {
                     if (isCubeActive(player)) {
                        if (!(GoldenCubeData.severity(player) <= 0.0F) || GoldenCubeData.withdrawal(player) > 0) {
                           if (event.isCancelable()) {
                              event.setCanceled(true);
                           }

                           event.setResult(Result.DENY);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof Player player) {
         if (!player.m_9236_().m_5776_() && Config.isGoldenCubeEnabled()) {
            if (GoldenCubeData.isActivated(player)) {
               GoldenCubeData.setActivated(player, true);
               GoldenCubeData.setPlayCount(player, GoldenCubeData.playCount(player) - 200);
               if (GoldenCubeData.withdrawal(player) > 0) {
                  beginInternalRemoval(player);

                  try {
                     player.m_21195_((MobEffect)ModEffects.GOLDEN_WITHDRAWAL.get());

                     for (MobEffect other : ModEffects.allAddictions()) {
                        player.m_21195_(other);
                     }
                  } finally {
                     endInternalRemoval(player);
                  }

                  GoldenCubeData.setWithdrawal(player, 0);
                  GoldenCubeData.setWithdrawalTicks(player, 0L);
                  GoldenCubeData.setLevel5Ticks(player, 0L);
                  clearAttributePenalties(player);
                  resetClientMadness(player);
                  long var10 = player.m_9236_().m_46467_();
                  GoldenCubeData.setLastPlay(player, var10);
                  GoldenCubeData.setNextDue(player, var10 + dueInterval(player));
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onDrops(LivingDropsEvent event) {
      if (event.getEntity() instanceof Player player) {
         if (!GoldenCubeData.isRejected(player)) {
            if (Config.isGoldenCubeEnabled()) {
               if (!player.m_9236_().m_5776_()) {
                  ListTag kept = player.getPersistentData().m_128437_("GoldenCubeKeptStacks", 10);
                  Iterator<ItemEntity> iterator = event.getDrops().iterator();

                  while (iterator.hasNext()) {
                     ItemEntity drop = iterator.next();
                     if (drop.m_32055_().m_41720_() instanceof GoldenCubeItem) {
                        kept.add(drop.m_32055_().m_41739_(new CompoundTag()));
                        iterator.remove();
                     }
                  }

                  if (!kept.isEmpty()) {
                     player.getPersistentData().m_128365_("GoldenCubeKeptStacks", kept);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onClone(Clone event) {
      Player oldPlayer = event.getOriginal();
      Player newPlayer = event.getEntity();
      if (GoldenCubeData.isRejected(oldPlayer)) {
         GoldenCubeData.setRejected(newPlayer, true);
      }

      if (!newPlayer.m_9236_().m_5776_()) {
         LAST_MODE.remove(newPlayer.m_20148_());
         LAST_PENALTY_LEVEL.remove(newPlayer.m_20148_());
         CompoundTag old = oldPlayer.getPersistentData();
         CompoundTag data = newPlayer.getPersistentData();

         for (String key : new String[]{
            "GoldenCubeSeverity",
            "GoldenCubeLastPlay",
            "GoldenCubeWithdrawal",
            "GoldenCubeWithdrawalTicks",
            "GoldenCubeLevel5Ticks",
            "GoldenCubeImmunityUntil",
            "GoldenCubeKept",
            "GoldenCubeKeptCount",
            "GoldenCubePlayCount",
            "GoldenCubeNextDue",
            "GoldenCubeAnnounced",
            "GoldenCubeOwned",
            "GoldenCubeAnomalyGranted",
            "GoldenCubeRejected",
            "GoldenCubeActivated",
            "GoldenCubeKeptStacks",
            "GoldenCubeTimerCheckpoint"
         }) {
            if (old.m_128441_(key)) {
               data.m_128365_(key, old.m_128423_(key).m_6426_());
            }
         }

         old.m_128473_("GoldenCubeKeptStacks");
         old.m_128473_("GoldenCubeKept");
         old.m_128473_("GoldenCubeKeptCount");
      }
   }

   @SubscribeEvent
   public static void onRespawn(PlayerRespawnEvent event) {
      Player player = event.getEntity();
      if (!player.m_9236_().m_5776_()) {
         returnKeptCubes(player);
         resetClientMadness(player);
      }
   }

   private static void returnKeptCubes(Player player) {
      if (player.m_6084_()) {
         CompoundTag tag = player.getPersistentData();
         ListTag stacks = tag.m_128437_("GoldenCubeKeptStacks", 10).m_6426_();
         if (stacks.isEmpty() && GoldenCubeData.keptCube(player)) {
            int count = Math.max(1, Math.min(64, GoldenCubeData.keptCubeCount(player)));

            for (int i = 0; i < count; i++) {
               stacks.add(new ItemStack((ItemLike)Moditems.GOLDEN_CUBE.get()).m_41739_(new CompoundTag()));
            }
         }

         tag.m_128473_("GoldenCubeKeptStacks");
         tag.m_128473_("GoldenCubeKept");
         tag.m_128473_("GoldenCubeKeptCount");

         for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = ItemStack.m_41712_(stacks.m_128728_(i));
            if (stack.m_41720_() instanceof GoldenCubeItem) {
               if (!GoldenCubeData.isRejected(player)) {
                  player.m_150109_().m_36054_(stack);
               }

               if (!stack.m_41619_()) {
                  player.m_36176_(stack, false);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onItemEntityJoin(EntityJoinLevelEvent event) {
      if (!event.getLevel().m_5776_()) {
         if (Config.isGoldenCubeEnabled()) {
            if (event.getEntity() instanceof ItemEntity item) {
               if (item.m_32055_().m_41720_() instanceof GoldenCubeItem) {
                  if (item.m_19749_() instanceof ServerPlayer owner) {
                     if (isCubeActive(owner)) {
                        if (!event.loadedFromDisk()) {
                           if (GoldenCubeData.tierFor(GoldenCubeData.severity(owner)) >= 2) {
                              ItemStack remaining = item.m_32055_().m_41777_();
                              owner.m_150109_().m_36054_(remaining);
                              item.m_32045_(remaining);
                              if (remaining.m_41619_()) {
                                 event.setCanceled(true);
                                 owner.m_5661_(
                                    Component.m_237113_("……它又回来了。").m_130944_(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.ITALIC}), true
                                 );
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onChangedDimension(PlayerChangedDimensionEvent event) {
      Player player = event.getEntity();
      if (!player.m_9236_().m_5776_()) {
         LAST_MODE.remove(player.m_20148_());
         syncMadness(player, player.m_9236_().m_46467_());
      }
   }

   @SubscribeEvent
   public static void onLoggedOut(PlayerLoggedOutEvent event) {
      try {
         Player player = event.getEntity();
         if (player.m_9236_().m_5776_()) {
            return;
         }

         GoldenCubeData.checkpointTimers(player);
         UUID id = player.m_20148_();
         LAST_MODE.remove(id);
         LAST_PENALTY_LEVEL.remove(id);
      } catch (Throwable var3) {
         SakuraMod.LOGGER.warn("Golden cube logout cleanup failed: {}", var3.toString());
      }
   }

   @SubscribeEvent
   public static void onLoggedIn(PlayerLoggedInEvent event) {
      try {
         handleLogin(event.getEntity());
      } catch (Throwable var2) {
         SakuraMod.LOGGER.warn("Golden cube login check failed: {}", var2.toString());
      }
   }

   private static void handleLogin(Player player) {
      if (!player.m_9236_().m_5776_()) {
         boolean legacyTimers = !player.getPersistentData().m_128441_("GoldenCubeTimerCheckpoint");
         GoldenCubeData.resumeTimers(player);
         returnKeptCubes(player);
         if (GoldenCubeData.isRejected(player)) {
            clearAllState(player);
         } else if (!Config.isGoldenCubeEnabled()) {
            clearAllState(player);
         } else {
            GoldenCubeData.setActivated(player, GoldenCubeData.isActivated(player));
            if (!GoldenCubeData.isActivated(player) && hasUnverifiedEffects(player)) {
               clearUnverifiedState(player);
            }

            if (GoldenCubeData.isImmune(player)) {
               clearAddictionEffects(player);
            }

            if (legacyTimers && GoldenCubeData.isActivated(player) && GoldenCubeData.severity(player) > 0.0F && GoldenCubeData.withdrawal(player) == 0) {
               long lastPlay = GoldenCubeData.lastPlay(player);
               if (lastPlay <= 0L) {
                  lastPlay = player.m_9236_().m_46467_();
               }

               GoldenCubeData.setNextDue(player, lastPlay + dueInterval(player));
            }

            LAST_PENALTY_LEVEL.remove(player.m_20148_());
            if (player.m_6084_() && isCubeActive(player)) {
               if (GoldenCubeData.severity(player) > 0.0F) {
                  refreshAddiction(player);
               }

               int withdrawal = GoldenCubeData.withdrawal(player);
               if (withdrawal > 0) {
                  applyWithdrawalEffect(player, withdrawal);
               }
            }

            resetClientMadness(player);
            syncMadness(player, player.m_9236_().m_46467_());
         }
      }
   }

   private static void clearUnverifiedState(Player player) {
      clearAddictionEffects(player);
      GoldenCubeData.setActivated(player, false);
      GoldenCubeData.setSeverity(player, 0.0F);
      GoldenCubeData.setWithdrawal(player, 0);
      GoldenCubeData.setWithdrawalTicks(player, 0L);
      GoldenCubeData.setLevel5Ticks(player, 0L);
      GoldenCubeData.setLastPlay(player, 0L);
      GoldenCubeData.setNextDue(player, 0L);
      GoldenCubeData.setPlayCount(player, 0);
      resetClientMadness(player);
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      LAST_MODE.clear();
      LAST_PENALTY_LEVEL.clear();
      INTERNAL_REMOVALS.remove();
   }

   @SubscribeEvent
   public static void onLootTableLoad(LootTableLoadEvent event) {
      ResourceLocation name = event.getName();
      if ("minecraft".equals(name.m_135827_())) {
         if (name.m_135815_().startsWith("chests/")) {
            if (Config.isGoldenCubeEnabled()) {
               double chance = name.m_135815_().startsWith("chests/ancient_city") ? 0.2 : Config.getGoldenCubeChestChance();
               if (!(chance <= 0.0)) {
                  if (event.getTable().getPool("sakura_golden_cube") == null) {
                     LootPool pool = LootPool.m_79043_()
                        .name("sakura_golden_cube")
                        .m_165133_(ConstantValue.m_165692_(1.0F))
                        .m_79076_(LootItem.m_79579_((ItemLike)Moditems.GOLDEN_CUBE.get()).m_79080_(LootItemRandomChanceCondition.m_81927_((float)chance)))
                        .m_79082_();
                     event.getTable().addPool(pool);
                  }
               }
            }
         }
      }
   }

   private static boolean hasCube(Player player) {
      return PlayerEquipCache.hasGoldenCube(player);
   }

   private static void playSound(Player player, SoundEvent sound, float volume, float pitch) {
      playSoundToSelf(player, sound, SoundSource.PLAYERS, volume, pitch);
   }

   private static void playSoundToSelf(Player player, SoundEvent sound, SoundSource source, float volume, float pitch) {
      if (player instanceof ServerPlayer sp) {
         sp.f_8906_
            .m_9829_(
               new ClientboundSoundPacket(
                  BuiltInRegistries.f_256894_.m_263177_(sound),
                  source,
                  player.m_20185_(),
                  player.m_20186_(),
                  player.m_20189_(),
                  volume,
                  pitch,
                  player.m_9236_().f_46441_.m_188505_()
               )
            );
      }
   }

   private static boolean isHoldingCube(Player player) {
      return player.m_21205_().m_41720_() == Moditems.GOLDEN_CUBE.get() || player.m_21206_().m_41720_() == Moditems.GOLDEN_CUBE.get();
   }
}
