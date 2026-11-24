package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.attribute.ModAttributes;
import com.Luo_jiyv.SakuraMod.damage.GunDamageContext;
import com.Luo_jiyv.SakuraMod.damage.ModDamageTypes;
import com.Luo_jiyv.SakuraMod.damage.SakuraSpecialDamage;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.network.ShieldStateSyncPacket;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class ShieldMechanismEvents {
   public static final long RECOVERY_DELAY = 300L;
   public static final int RECOVERY_INTERVAL = 5;
   private static final ResourceLocation AXE_SLASH_ORIGIN = ResourceLocation.fromNamespaceAndPath("sakura_mod", "sakura_axe_slash");
   public static final long MERGE_WINDOW = 10L;
   private static final Object DIRECT_HEALTH_ORIGIN = new Object();
   private static final ThreadLocal<Integer> HEALTH_WRITE_DEPTH = ThreadLocal.withInitial(() -> 0);
   private static final float SMALL_THRESHOLD = 0.1F;
   private static final float MID_THRESHOLD = 0.2F;
   private static final float HIGH_THRESHOLD = 0.4F;
   private static final float BIG_THRESHOLD = 0.6F;
   private static final float LOW_HEALTH_DAMAGE = 2.0F;
   private static final int MAX_ADAPT_STEPS = 5;
   private static final int MAX_ADAPT_TYPES = 10;
   private static final long ADAPT_DELAY = 200L;
   private static final long ADAPT_DECAY_INTERVAL = 20L;
   private static final long NO_RECOVERY = -1L;
   private static final String CAPACITY_MIGRATED = "sakura_mod:shield_capacity_migrated";
   private static final Map<Player, ShieldMechanismEvents.State> STATES = new IdentityHashMap<>();

   private ShieldMechanismEvents() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onLivingAttack(LivingAttackEvent event) {
      if (event.getEntity() instanceof Player player && !player.m_9236_().m_5776_()) {
         DamageSource source = event.getSource();
         ShieldMechanismEvents.State state = STATES.get(player);
         if (state == null || state.replaySource != source) {
            if (!source.m_276093_(DamageTypes.f_286979_)) {
               float amount = event.getAmount();
               if (amount > 0.0F && Float.isFinite(amount)) {
                  long now = player.m_9236_().m_46467_();
                  boolean active = hasShieldCharge(player, state);
                  if (state != null && state.pending != null && (now >= state.pending.dueAt || !active)) {
                     settle(player, state, now);
                     active = hasShieldCharge(player, state);
                  }

                  if (active && !player.m_6673_(source) && !player.m_21275_(source)) {
                     if (!source.m_269533_(DamageTypeTags.f_268745_) || !player.m_21023_(MobEffects.f_19607_)) {
                        if (source.m_7986_()) {
                           amount = switch (player.m_9236_().m_46791_()) {
                              case PEACEFUL -> 0.0F;
                              case EASY -> Math.min(amount / 2.0F + 1.0F, amount);
                              case NORMAL -> amount;
                              case HARD -> (float)Math.min(amount * 1.5, Float.MAX_VALUE);
                              default -> throw new IncompatibleClassChangeError();
                           };
                        }

                        if (!(amount <= 0.0F)) {
                           if (state == null) {
                              state = new ShieldMechanismEvents.State();
                              STATES.put(player, state);
                           }

                           if (state.pending == null) {
                              state.pending = new ShieldMechanismEvents.Pending(now + 10L);
                           }

                           DamageSource original = SakuraSpecialDamage.originalSource(source);
                           GunDamageContext.Shot shot = original instanceof GunDamageContext gun ? gun.sakura$getGunShot() : null;
                           ShieldMechanismEvents.AttackType key = attackType(source, shot);
                           UUID slashId = SakuraSpecialDamage.axeSlashId(source);
                           Object direct = shot != null ? shot.projectile() : (slashId == null ? source.m_7640_() : slashId);
                           ShieldMechanismEvents.HitKey hit = new ShieldMechanismEvents.HitKey(
                              key, direct, source.m_7639_(), shot == null ? null : original.m_269150_(), false
                           );
                           state.pending.hits.computeIfAbsent(hit, ignored -> new ShieldMechanismEvents.HitGroup()).add(source, amount);
                           if (shot == null || state.pending.firstGunHit(shot.projectile())) {
                              int steps = growAdaptation(state, key, now);
                              if (steps != 0) {
                                 state.pending.adaptSteps.put(key, steps);
                              }
                           }

                           if (source.m_269533_(DamageTypeTags.f_268745_)) {
                              player.m_20095_();
                           }

                           event.setCanceled(true);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean interceptHealthSet(LivingEntity entity, float health) {
      if (entity instanceof Player player
         && !player.m_9236_().m_5776_()
         && HEALTH_WRITE_DEPTH.get() <= 0
         && !Float.isNaN(health)
         && health < player.m_21223_()
         && canUseShield(player)) {
         ShieldMechanismEvents.State state = STATES.get(player);
         if (state != null && state.replaySource != null) {
            return false;
         } else {
            float amount = player.m_21223_() - Math.max(0.0F, health);
            long now = player.m_9236_().m_46467_();
            boolean settled = false;
            boolean active = hasShieldCharge(player, state);
            if (state != null && state.pending != null && (now >= state.pending.dueAt || !active)) {
               settle(player, state, now);
               settled = true;
               active = hasShieldCharge(player, state);
            }

            if (!player.m_6084_()) {
               return true;
            } else if (!active) {
               if (!settled) {
                  return false;
               } else {
                  ShieldBreakEffects.applyHealthDamage(player, player.m_269291_().m_269264_(), amount);
                  return true;
               }
            } else {
               if (state == null) {
                  state = new ShieldMechanismEvents.State();
                  STATES.put(player, state);
               }

               if (state.pending == null) {
                  state.pending = new ShieldMechanismEvents.Pending(now + 10L);
               }

               DamageSource source = player.m_269291_().m_269264_();
               ShieldMechanismEvents.AttackType key = new ShieldMechanismEvents.AttackType(source.m_269150_(), DIRECT_HEALTH_ORIGIN, null);
               ShieldMechanismEvents.HitKey hit = new ShieldMechanismEvents.HitKey(key, null, null, null, true);
               state.pending.hits.computeIfAbsent(hit, ignored -> new ShieldMechanismEvents.HitGroup()).add(source, amount);
               int steps = growAdaptation(state, key, now);
               if (steps != 0) {
                  state.pending.adaptSteps.put(key, steps);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static void setHealthWithoutShield(LivingEntity entity, float health) {
      int depth = HEALTH_WRITE_DEPTH.get();
      HEALTH_WRITE_DEPTH.set(depth + 1);

      try {
         entity.m_21153_(health);
      } finally {
         if (depth == 0) {
            HEALTH_WRITE_DEPTH.remove();
         } else {
            HEALTH_WRITE_DEPTH.set(depth);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && !event.player.m_9236_().m_5776_()) {
         Player player = event.player;
         ShieldMechanismEvents.State state = STATES.get(player);
         if (!player.m_6084_()) {
            STATES.remove(player);
         } else {
            int capacity = getCapacity(player);
            if (state == null) {
               if (capacity == 0) {
                  return;
               }

               state = new ShieldMechanismEvents.State();
               STATES.put(player, state);
            }

            long now = player.m_9236_().m_46467_();
            if (state.pending != null && (now >= state.pending.dueAt || capacity <= state.spent || !canUseShield(player))) {
               settle(player, state, now);
               if (!player.m_6084_()) {
                  STATES.remove(player);
                  return;
               }

               capacity = getCapacity(player);
            }

            if (capacity <= state.spent) {
               state.adaptation.clear();
            } else {
               decayAdaptation(state, now);
            }

            if (state.spent > 0) {
               if (capacity > 0 && canUseShield(player)) {
                  recover(state, capacity, now);
               } else if (state.pausedAt == -1L) {
                  state.pausedAt = now;
               }
            }

            syncState(player, state, capacity);
            if (capacity == 0 && state.spent == 0 && state.pending == null) {
               STATES.remove(player);
            }
         }
      }
   }

   private static void settle(Player player, ShieldMechanismEvents.State state, long now) {
      ShieldMechanismEvents.Pending pending = state.pending;
      state.pending = null;
      if (pending != null && player.m_6084_()) {
         double total = 0.0;

         for (Entry<ShieldMechanismEvents.HitKey, ShieldMechanismEvents.HitGroup> entry : pending.hits.entrySet()) {
            if (pending.adaptSteps.getOrDefault(entry.getKey().attack(), 0) < 5) {
               total += entry.getValue().total;
            }
         }

         float maxHealth = player.m_21233_();
         if (!(total <= maxHealth * 0.1F)) {
            state.spent++;
            startRecovery(state, now + 300L);
            if (state.spent == getCapacity(player) && canUseShield(player)) {
               state.adaptation.clear();
               ShieldBreakEffects.release(player);
            }

            double capped = total;
            if (total > maxHealth * 0.6F) {
               float limit = maxHealth * 0.05F;
               capped = player.m_21223_() < limit ? 2.0 : limit;
            } else if (total > maxHealth * 0.4F) {
               capped = maxHealth * 0.1F;
            } else if (total > maxHealth * 0.2F) {
               capped = maxHealth * 0.2F;
            }

            double scale = Math.min(1.0, capped / total);

            for (Entry<ShieldMechanismEvents.HitKey, ShieldMechanismEvents.HitGroup> entryx : pending.hits.entrySet()) {
               ShieldMechanismEvents.HitGroup group = entryx.getValue();
               int steps = pending.adaptSteps.getOrDefault(entryx.getKey().attack(), 0);
               float applied = (float)Math.min(Float.MAX_VALUE, group.total * scale * (5 - steps) / 5.0);
               if (applied > 0.0F && player.m_6084_()) {
                  if (entryx.getKey().directHealth()) {
                     ShieldBreakEffects.applyHealthDamage(player, group.source, applied);
                  } else {
                     applySettledDamage(player, state, group.source, applied);
                  }
               }
            }
         }
      }
   }

   private static void applySettledDamage(Player player, ShieldMechanismEvents.State state, DamageSource source, float amount) {
      if (!player.m_7500_() && !player.m_5833_() && !SakuraArmorEvents.isReviveHandled(player)) {
         DamageSource replay = source;
         if (source.m_7986_()) {
            replay = new DamageSource(source.m_269150_(), source.m_7640_(), source.m_7639_(), source.m_7270_()) {
               public boolean m_7986_() {
                  return false;
               }
            };
         }

         int previousCooldown = player.f_19802_;
         DamageSource previousReplay = state.replaySource;
         state.replaySource = replay;

         try {
            player.f_19802_ = Math.min(previousCooldown, 10);
            player.m_6469_(replay, amount);
         } finally {
            player.f_19802_ = Math.max(previousCooldown, player.f_19802_);
            state.replaySource = previousReplay;
         }
      }
   }

   public static boolean isReplaying(LivingEntity entity, DamageSource source) {
      if (entity instanceof Player player && !player.m_9236_().m_5776_()) {
         ShieldMechanismEvents.State state = STATES.get(player);
         return state != null && state.replaySource == source;
      } else {
         return false;
      }
   }

   private static ShieldMechanismEvents.AttackType attackType(DamageSource source, GunDamageContext.Shot shot) {
      Entity direct = source.m_7640_();
      Entity attacker = source.m_7639_();
      if (shot != null) {
         return new ShieldMechanismEvents.AttackType(null, shot.gunId(), attackerKey(shot.shooter()));
      } else {
         if (attacker == null && direct instanceof LivingEntity) {
            attacker = direct;
         }

         Object origin = attackerKey(attacker);
         if (SakuraSpecialDamage.axeSlashId(source) != null) {
            return new ShieldMechanismEvents.AttackType(source.m_269150_(), AXE_SLASH_ORIGIN, origin);
         } else {
            Object weapon = !(direct instanceof LivingEntity) && direct != null ? direct.m_6095_() : null;
            if (direct instanceof GunDamageContext.Projectile bullet && bullet.getGunId() != null) {
               weapon = bullet.getGunId();
            } else if (direct instanceof AbstractArrow arrow && !(direct instanceof ThrownTrident)) {
               weapon = arrow.m_36795_() ? Items.f_42717_ : Items.f_42411_;
            } else if (direct instanceof LivingEntity wielder
               && (
                  source.m_276093_(DamageTypes.f_268464_)
                     || source.m_276093_(DamageTypes.f_268566_)
                     || source.m_276093_(DamageTypes.f_268511_)
                     || source.m_276093_(ModDamageTypes.SAKURA_SLASH)
               )) {
               weapon = wielder.m_21205_().m_41720_();
            }

            return new ShieldMechanismEvents.AttackType(source.m_269150_(), weapon, origin);
         }
      }
   }

   private static Object attackerKey(Entity attacker) {
      if (attacker instanceof Player) {
         return attacker.m_20148_();
      } else {
         return attacker == null ? null : attacker.m_6095_();
      }
   }

   private static int growAdaptation(ShieldMechanismEvents.State state, ShieldMechanismEvents.AttackType key, long now) {
      decayAdaptation(state, now);
      ShieldMechanismEvents.Adapt adapt = state.adaptation.get(key);
      if (adapt == null) {
         if (state.adaptation.size() >= 10) {
            return 0;
         }

         adapt = new ShieldMechanismEvents.Adapt();
         state.adaptation.put(key, adapt);
      }

      if (adapt.steps < 5) {
         adapt.steps++;
         adapt.nextDecay = now + 200L;
         state.nextAdaptationDecay = Math.min(state.nextAdaptationDecay, adapt.nextDecay);
      }

      return adapt.steps;
   }

   private static void decayAdaptation(ShieldMechanismEvents.State state, long now) {
      if (now >= state.nextAdaptationDecay) {
         state.nextAdaptationDecay = Long.MAX_VALUE;
         Iterator<ShieldMechanismEvents.Adapt> entries = state.adaptation.values().iterator();

         while (entries.hasNext()) {
            ShieldMechanismEvents.Adapt adapt = entries.next();
            if (now >= adapt.nextDecay) {
               long steps = 1L + (now - adapt.nextDecay) / 20L;
               if (steps >= adapt.steps) {
                  entries.remove();
                  continue;
               }

               adapt.steps -= (int)steps;
               adapt.nextDecay += steps * 20L;
            }

            state.nextAdaptationDecay = Math.min(state.nextAdaptationDecay, adapt.nextDecay);
         }
      }
   }

   private static void startRecovery(ShieldMechanismEvents.State state, long start) {
      state.recoveryStart = start;
      state.nextCharge = start + 5L;
      state.pausedAt = -1L;
   }

   private static void recover(ShieldMechanismEvents.State state, int capacity, long now) {
      if (state.pausedAt != -1L) {
         long paused = Math.max(0L, now - Math.max(state.pausedAt, state.recoveryStart));
         state.pausedAt = -1L;
         state.recoveryStart += paused;
         state.nextCharge += paused;
      }

      if (now >= state.nextCharge) {
         state.spent = Math.min(state.spent, capacity) - 1;
         if (state.spent == 0) {
            state.recoveryStart = -1L;
         } else if (now == state.nextCharge) {
            state.nextCharge += 5L;
         } else {
            startRecovery(state, now);
         }
      }
   }

   public static int getCapacity(Player player) {
      AttributeInstance attribute = player.m_21051_((Attribute)ModAttributes.SHIELD_CHARGES.get());
      return attribute == null ? 0 : (int)Math.ceil(attribute.m_22135_());
   }

   public static int getEnergy(Player player) {
      return energy(getCapacity(player), STATES.get(player));
   }

   private static int energy(int capacity, ShieldMechanismEvents.State state) {
      return state == null ? capacity : Math.max(0, capacity - state.spent);
   }

   public static void debugSetCharges(Player player, int points) {
      debugChangeCharges(player, points, false);
   }

   public static void debugAddCharges(Player player, int points) {
      debugChangeCharges(player, points, true);
   }

   private static void debugChangeCharges(Player player, int points, boolean relative) {
      if (!player.m_9236_().m_5776_()) {
         long now = player.m_9236_().m_46467_();
         ShieldMechanismEvents.State state = STATES.get(player);
         if (state != null) {
            settle(player, state, now);
         }

         int capacity = getCapacity(player);
         if (capacity != 0 && player.m_6084_()) {
            int value = Math.max(0, Math.min(capacity, (relative ? energy(capacity, state) : 0) + points));
            state = new ShieldMechanismEvents.State();
            state.spent = capacity - value;
            if (state.spent > 0) {
               startRecovery(state, now + 300L);
            }

            STATES.put(player, state);
            syncState(player, state, capacity);
         }
      }
   }

   private static boolean hasShieldCharge(Player player, ShieldMechanismEvents.State state) {
      return canUseShield(player) && energy(getCapacity(player), state) > 0;
   }

   private static void syncState(Player player, ShieldMechanismEvents.State state, int capacity) {
      if (player instanceof ServerPlayer sp && sp.f_8906_ != null && !sp.m_9232_()) {
         int energy = energy(capacity, state);
         long recoveryStart = state == null ? -1L : state.recoveryStart;
         ShieldStateSyncPacket previous = state == null ? null : state.lastSync;
         if (previous != null && previous.capacity() == capacity && previous.energy() == energy && previous.recoveryStart() == recoveryStart) {
            return;
         }

         ShieldStateSyncPacket packet = new ShieldStateSyncPacket(capacity, energy, recoveryStart);
         ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), packet);
         if (state != null) {
            state.lastSync = packet;
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onTravel(EntityTravelToDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         ShieldMechanismEvents.State state = STATES.get(player);
         if (state != null) {
            settle(player, state, player.m_9236_().m_46467_());
            if (!player.m_6084_()) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLogout(PlayerLoggedOutEvent event) {
      Player player = event.getEntity();
      ShieldMechanismEvents.State state = STATES.get(player);
      if (state != null) {
         try {
            settle(player, state, player.m_9236_().m_46467_());
         } finally {
            STATES.remove(player);
         }
      }
   }

   @SubscribeEvent
   public static void onLogin(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      if (!player.m_9236_().m_5776_()) {
         markCapacityMigrated(player);
         STATES.remove(player);
         syncState(player, null, getCapacity(player));
      }
   }

   public static void migrateSavedCapacity(Player player) {
      if (!player.m_9236_().m_5776_() && !player.getPersistentData().m_128469_("PlayerPersisted").m_128471_("sakura_mod:shield_capacity_migrated")) {
         AttributeInstance attribute = player.m_21051_((Attribute)ModAttributes.SHIELD_CHARGES.get());
         if (attribute != null) {
            attribute.m_22100_(((Attribute)ModAttributes.SHIELD_CHARGES.get()).m_22082_());
         }

         markCapacityMigrated(player);
      }
   }

   private static void markCapacityMigrated(Player player) {
      CompoundTag data = player.getPersistentData();
      CompoundTag persisted = data.m_128469_("PlayerPersisted");
      if (!persisted.m_128471_("sakura_mod:shield_capacity_migrated")) {
         persisted.m_128379_("sakura_mod:shield_capacity_migrated", true);
         data.m_128365_("PlayerPersisted", persisted);
      }
   }

   @SubscribeEvent
   public static void onClone(Clone event) {
      Player original = event.getOriginal();
      ShieldMechanismEvents.State state = STATES.remove(original);
      if (!event.isWasDeath() && state != null) {
         state.lastSync = null;
         STATES.put(event.getEntity(), state);
      }
   }

   @SubscribeEvent
   public static void onRespawn(PlayerRespawnEvent event) {
      Player player = event.getEntity();
      syncState(player, STATES.get(player), getCapacity(player));
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      STATES.clear();
   }

   public static boolean hasShieldMechanism(Player player) {
      return canUseShield(player) && getCapacity(player) > 0;
   }

   public static boolean canUseShield(Player player) {
      return player.m_6084_() && !player.m_7500_() && !player.m_5833_() && !player.m_150110_().f_35934_;
   }

   private static final class Adapt {
      int steps;
      long nextDecay;
   }

   private record AttackType(Holder<DamageType> damageType, Object weapon, Object attacker) {
   }

   private static final class HitGroup {
      DamageSource source;
      float largestHit;
      double total;

      void add(DamageSource source, float amount) {
         this.total += amount;
         if (amount > this.largestHit) {
            this.largestHit = amount;
            this.source = source;
         }
      }
   }

   private record HitKey(ShieldMechanismEvents.AttackType attack, Object direct, Entity attacker, Holder<DamageType> damagePart, boolean directHealth) {
   }

   private static final class Pending {
      final long dueAt;
      final Map<ShieldMechanismEvents.HitKey, ShieldMechanismEvents.HitGroup> hits = new LinkedHashMap<>();
      final Map<ShieldMechanismEvents.AttackType, Integer> adaptSteps = new HashMap<>();
      Set<Entity> gunProjectiles;

      Pending(long dueAt) {
         this.dueAt = dueAt;
      }

      boolean firstGunHit(Entity bullet) {
         if (this.gunProjectiles == null) {
            this.gunProjectiles = Collections.newSetFromMap(new IdentityHashMap<>());
         }

         return this.gunProjectiles.add(bullet);
      }
   }

   private static final class State {
      ShieldMechanismEvents.Pending pending;
      DamageSource replaySource;
      final Map<ShieldMechanismEvents.AttackType, ShieldMechanismEvents.Adapt> adaptation = new HashMap<>();
      long nextAdaptationDecay = Long.MAX_VALUE;
      long recoveryStart = -1L;
      long nextCharge;
      long pausedAt = -1L;
      int spent;
      ShieldStateSyncPacket lastSync;
   }
}
