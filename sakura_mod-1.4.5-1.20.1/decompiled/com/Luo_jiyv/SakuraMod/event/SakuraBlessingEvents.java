package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.effect.ModEffects;
import com.Luo_jiyv.SakuraMod.effect.SakuraBlessingFilter;
import java.util.Set;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent.Applicable;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class SakuraBlessingEvents {
   private static final float OVERKILL_MULTIPLIER = 20.0F;
   private static final Set<EntityType<?>> NEUTRAL_MOBS = Set.of(
      EntityType.f_20499_,
      EntityType.f_20460_,
      EntityType.f_20514_,
      EntityType.f_20507_,
      EntityType.f_20550_,
      EntityType.f_20559_,
      EntityType.f_147035_,
      EntityType.f_20466_,
      EntityType.f_20488_,
      EntityType.f_20566_,
      EntityType.f_20531_,
      EntityType.f_20511_,
      EntityType.f_20456_
   );

   @SubscribeEvent
   public static void onEffectApplicable(Applicable event) {
      LivingEntity entity = event.getEntity();
      if (entity.m_21023_((MobEffect)ModEffects.SAKURA_BLESSING.get())) {
         MobEffectInstance inst = event.getEffectInstance();
         if (inst != null) {
            if (!ModEffects.isGoldenEffect(inst.m_19544_())) {
               if (!SakuraBlessingFilter.isAllowed(inst.m_19544_())) {
                  event.setResult(Result.DENY);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END) {
         Player player = event.player;
         if (!player.m_9236_().m_5776_()) {
            if (player.m_21023_((MobEffect)ModEffects.SAKURA_BLESSING.get())) {
               if (player.m_146888_() > 0) {
                  player.m_146917_(0);
               }

               if (player.m_20094_() > 0) {
                  player.m_20095_();
               }

               if (player.m_20146_() < player.m_6062_()) {
                  player.m_20301_(player.m_6062_());
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingAttack(LivingAttackEvent event) {
      LivingEntity target = event.getEntity();
      if (target.m_21023_((MobEffect)ModEffects.SAKURA_BLESSING.get())) {
         DamageSource source = event.getSource();
         if (!source.m_276093_(DamageTypes.f_286979_)) {
            if (event.getAmount() > target.m_21233_() * 20.0F) {
               event.setCanceled(true);
            } else if (isInstantDamagePotion(source)) {
               event.setCanceled(true);
            } else {
               if (isEnvironmental(source)) {
                  event.setCanceled(true);
                  target.m_20095_();
               }
            }
         }
      }
   }

   private static boolean isEnvironmental(DamageSource source) {
      return source.m_269533_(DamageTypeTags.f_268745_)
         || source.m_269533_(DamageTypeTags.f_268725_)
         || source.m_269533_(DamageTypeTags.f_268581_)
         || source.m_269533_(DamageTypeTags.f_268419_)
         || source.m_276093_(DamageTypes.f_268546_)
         || source.m_276093_(DamageTypes.f_268434_)
         || source.m_276093_(DamageTypes.f_268585_)
         || source.m_276093_(DamageTypes.f_268469_)
         || source.m_276093_(DamageTypes.f_268671_)
         || source.m_276093_(DamageTypes.f_268612_);
   }

   private static boolean isInstantDamagePotion(DamageSource source) {
      return !source.m_276093_(DamageTypes.f_268515_) ? false : !(source.m_7639_() instanceof Player);
   }

   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      LivingEntity target = event.getEntity();
      DamageSource source = event.getSource();
      Entity attacker = source.m_7639_();
      float damage = event.getAmount();
      if (attacker instanceof Player player && player.m_21023_((MobEffect)ModEffects.SAKURA_BLESSING.get()) && target instanceof Mob mob) {
         if (isFriendly(mob)) {
            event.setAmount(damage * 0.5F);
         } else if (isHostile(mob)) {
            event.setAmount(damage * 1.2F);
         }
      } else if (target instanceof Player player) {
         if (player.m_21023_((MobEffect)ModEffects.SAKURA_BLESSING.get())) {
            MobEffectInstance blessing = player.m_21124_((MobEffect)ModEffects.SAKURA_BLESSING.get());
            int amplifier = blessing != null ? blessing.m_19564_() : 0;
            if ("outOfWorld".equals(source.m_19385_())) {
               float r = Math.min(0.75F, 0.15F + 0.3F * amplifier);
               event.setAmount(damage * (1.0F - r));
            }
         }
      }
   }

   private static boolean isFriendly(Mob mob) {
      if (NEUTRAL_MOBS.contains(mob.m_6095_())) {
         return false;
      } else {
         return mob instanceof Enemy ? false : mob instanceof Animal || mob instanceof Villager || mob instanceof AbstractVillager || mob instanceof SnowGolem;
      }
   }

   private static boolean isHostile(Mob mob) {
      return NEUTRAL_MOBS.contains(mob.m_6095_()) ? false : mob instanceof Enemy;
   }
}
