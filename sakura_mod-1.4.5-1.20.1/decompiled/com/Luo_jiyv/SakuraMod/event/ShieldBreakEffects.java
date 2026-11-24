package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.damage.ModDamageTypes;
import com.Luo_jiyv.SakuraMod.particle.ModParticles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class ShieldBreakEffects {
   public static final double WAVE_RADIUS = 16.0;
   public static final int WAVE_TICKS = 10;
   public static final int STUN_TICKS = 40;
   private static final String STUN_UNTIL = "sakura_mod:shield_stun_until";
   private static final List<ShieldBreakEffects.Wave> WAVES = new ArrayList<>();

   private ShieldBreakEffects() {
   }

   public static void release(LivingEntity owner) {
      if (owner.m_9236_() instanceof ServerLevel level) {
         ShieldBreakEffects.Wave var3 = new ShieldBreakEffects.Wave(level, owner);
         WAVES.add(var3);
         level.m_5594_(null, owner.m_20183_(), SoundEvents.f_12347_, SoundSource.PLAYERS, 1.6F, 0.8F);
         level.m_5594_(null, owner.m_20183_(), SoundEvents.f_11983_, SoundSource.PLAYERS, 1.1F, 1.15F);
         level.m_5594_(null, owner.m_20183_(), SoundEvents.f_11738_, SoundSource.PLAYERS, 1.2F, 1.5F);
         level.m_8767_(
            (SimpleParticleType)ModParticles.SHIELD_SHATTER.get(), var3.center.f_82479_, var3.center.f_82480_, var3.center.f_82481_, 1, 0.0, 0.0, 0.0, 0.0
         );
         level.m_8767_(
            (SimpleParticleType)ModParticles.SHIELD_SHOCKWAVE.get(), var3.center.f_82479_, var3.center.f_82480_, var3.center.f_82481_, 1, 0.0, 0.0, 0.0, 0.0
         );
      }
   }

   @SubscribeEvent
   public static void onLevelTick(LevelTickEvent event) {
      if (event.phase == Phase.END && event.level instanceof ServerLevel level) {
         Iterator var12 = WAVES.iterator();

         while (var12.hasNext()) {
            ShieldBreakEffects.Wave wave = (ShieldBreakEffects.Wave)var12.next();
            if (wave.level == level) {
               long elapsed = level.m_46467_() - wave.startedAt + 1L;
               double radius = 16.0 * Math.min(10L, elapsed) / 10.0;
               AABB area = new AABB(wave.center, wave.center).m_82400_(radius);

               for (LivingEntity target : level.m_6443_(LivingEntity.class, area, entity -> entity != wave.owner && entity.m_6084_() && !entity.m_5833_())) {
                  if (!(target instanceof Player player && player.m_7500_())
                     && !(target.m_20191_().m_272282_(wave.center) > radius * radius)
                     && wave.hit.add(target.m_20148_())) {
                     applyHealthDamage(target, wave.source, Math.max(50.0F, target.m_21233_() * 0.2F));
                     if (target instanceof Mob mob && mob.m_6084_()) {
                        stun(mob, level.m_46467_());
                     }
                  }
               }

               if (elapsed >= 10L) {
                  var12.remove();
               }
            }
         }
      }
   }

   public static void applyHealthDamage(LivingEntity target, DamageSource source, float amount) {
      if (target.m_6084_() && amount > 0.0F) {
         if (source.m_7639_() instanceof LivingEntity attacker) {
            target.m_6703_(attacker);
            if (attacker instanceof Player player) {
               target.m_6598_(player);
            }
         }

         target.m_21231_().m_289194_(source, amount);
         ShieldMechanismEvents.setHealthWithoutShield(target, Math.max(0.0F, target.m_21223_() - amount));
         target.m_146850_(GameEvent.f_223706_);
         target.m_9236_().m_7605_(target, (byte)2);
         if (target.m_21223_() <= 0.0F) {
            target.m_6667_(source);
         }
      }
   }

   private static void stun(Mob mob, long now) {
      CompoundTag data = mob.getPersistentData();
      if (!mob.m_21525_() || data.m_128441_("sakura_mod:shield_stun_until")) {
         data.m_128356_("sakura_mod:shield_stun_until", now + 40L);
         mob.m_21573_().m_26573_();
         mob.m_6710_(null);
         mob.m_21557_(true);
      }
   }

   @SubscribeEvent
   public static void onLivingTick(LivingTickEvent event) {
      if (event.getEntity() instanceof Mob mob && !mob.m_9236_().m_5776_()) {
         CompoundTag data = mob.getPersistentData();
         if (data.m_128441_("sakura_mod:shield_stun_until")) {
            if (mob.m_9236_().m_46467_() >= data.m_128454_("sakura_mod:shield_stun_until")) {
               data.m_128473_("sakura_mod:shield_stun_until");
               mob.m_21557_(false);
            } else {
               mob.m_21557_(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      WAVES.clear();
   }

   private static final class Wave {
      final ServerLevel level;
      final LivingEntity owner;
      final Vec3 center;
      final long startedAt;
      final DamageSource source;
      final Set<UUID> hit = new HashSet<>();

      Wave(ServerLevel level, LivingEntity owner) {
         this.level = level;
         this.owner = owner;
         this.center = owner.m_20182_().m_82520_(0.0, owner.m_20206_() * 0.5, 0.0);
         this.startedAt = level.m_46467_();
         Reference<DamageType> type = level.m_9598_().m_175515_(Registries.f_268580_).m_246971_(ModDamageTypes.SHIELD_SHOCKWAVE);
         this.source = new DamageSource(type, owner);
      }
   }
}
