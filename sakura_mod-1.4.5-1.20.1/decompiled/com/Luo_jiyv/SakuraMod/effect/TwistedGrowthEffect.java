package com.Luo_jiyv.SakuraMod.effect;

import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class TwistedGrowthEffect extends MobEffect {
   public static final int COLOR = 9121326;
   private static final UUID ATTACK_UUID = UUID.fromString("7b1e9d2a-3f4c-4a6e-9c1d-2e3f4a5b6c71");
   private static final UUID SPEED_UUID = UUID.fromString("8c2f0e3b-4a5d-4b7f-8d2e-3f4a5b6c7d82");
   private static final UUID HEALTH_UUID = UUID.fromString("9d3a1f4c-5b6e-4c80-9e3f-4a5b6c7d8e93");

   public TwistedGrowthEffect() {
      super(MobEffectCategory.BENEFICIAL, 9121326);
   }

   public void m_6385_(LivingEntity entity, AttributeMap attributes, int amplifier) {
      add(attributes, Attributes.f_22281_, ATTACK_UUID, "twisted_growth_attack", 0.5, Operation.MULTIPLY_TOTAL);
      add(attributes, Attributes.f_22279_, SPEED_UUID, "twisted_growth_speed", 0.2, Operation.MULTIPLY_TOTAL);
      AttributeInstance health = attributes.m_22146_(Attributes.f_22276_);
      if (health != null) {
         health.m_22120_(HEALTH_UUID);
         double base = health.m_22115_();
         double addition = 0.0;
         double multiplyBase = 0.0;
         double multiplyTotal = 0.0;

         for (AttributeModifier modifier : health.m_22122_()) {
            if (!HEALTH_UUID.equals(modifier.m_22209_())) {
               switch (modifier.m_22217_()) {
                  case ADDITION:
                     addition += modifier.m_22218_();
                     break;
                  case MULTIPLY_BASE:
                     multiplyBase += modifier.m_22218_();
                     break;
                  case MULTIPLY_TOTAL:
                     multiplyTotal += modifier.m_22218_();
               }
            }
         }

         double factor = (1.0 + multiplyBase) * (1.0 + multiplyTotal);
         double otherMax = (base + addition) * factor;
         double desired = otherMax * 1.1 + 20.0;
         double bonus = factor > 1.0E-6 ? desired / factor - base : desired - otherMax;
         health.m_22118_(new AttributeModifier(HEALTH_UUID, "twisted_growth_health", bonus, Operation.ADDITION));
      }
   }

   public void m_6386_(LivingEntity entity, AttributeMap attributes, int amplifier) {
      remove(attributes, Attributes.f_22281_, ATTACK_UUID);
      remove(attributes, Attributes.f_22279_, SPEED_UUID);
      remove(attributes, Attributes.f_22276_, HEALTH_UUID);
   }

   public boolean m_6584_(int duration, int amplifier) {
      return true;
   }

   public void m_6742_(LivingEntity entity, int amplifier) {
      if (!entity.m_9236_().m_5776_()) {
         if (entity.f_19797_ % 50 == 0 && entity.m_21223_() < entity.m_21233_()) {
            entity.m_5634_(1.0F);
         }

         if (entity.f_19797_ % 20 == 0 && entity.m_9236_() instanceof ServerLevel level) {
            for (Mob mob : level.m_45976_(Mob.class, entity.m_20191_().m_82400_(16.0))) {
               if (mob != entity && mob.m_6084_() && !mob.m_7307_(entity) && mob.m_6779_(entity) && (mob.m_5448_() == null || mob.m_5448_() != entity)) {
                  mob.m_6710_(entity);
               }
            }
         }
      }
   }

   private static void add(AttributeMap attributes, Attribute attribute, UUID uuid, String name, double amount, Operation operation) {
      AttributeInstance instance = attributes.m_22146_(attribute);
      if (instance != null) {
         instance.m_22120_(uuid);
         instance.m_22118_(new AttributeModifier(uuid, name, amount, operation));
      }
   }

   private static void remove(AttributeMap attributes, Attribute attribute, UUID uuid) {
      AttributeInstance instance = attributes.m_22146_(attribute);
      if (instance != null) {
         instance.m_22120_(uuid);
      }
   }
}
