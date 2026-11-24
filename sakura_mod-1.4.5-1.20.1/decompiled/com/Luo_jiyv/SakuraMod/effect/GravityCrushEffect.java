package com.Luo_jiyv.SakuraMod.effect;

import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class GravityCrushEffect extends MobEffect {
   public static final int COLOR = 5909122;
   private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("b1c2d3e4-f5a6-7890-bcde-f12345678901");
   private static final String SPEED_MODIFIER_NAME = "gravity_crush_speed";

   public GravityCrushEffect() {
      super(MobEffectCategory.BENEFICIAL, 5909122);
   }

   public boolean m_6584_(int duration, int amplifier) {
      return false;
   }

   public void m_6385_(LivingEntity entity, AttributeMap attributes, int amplifier) {
      AttributeInstance speed = attributes.m_22146_(Attributes.f_22279_);
      if (speed != null) {
         double reduction = Math.min(1.0, 0.2 * (amplifier + 1));
         AttributeModifier existing = speed.m_22111_(SPEED_MODIFIER_UUID);
         if (existing != null) {
            if (Math.abs(existing.m_22218_() + reduction) < 1.0E-6) {
               return;
            }

            speed.m_22120_(SPEED_MODIFIER_UUID);
         }

         speed.m_22118_(new AttributeModifier(SPEED_MODIFIER_UUID, "gravity_crush_speed", -reduction, Operation.MULTIPLY_TOTAL));
      }
   }

   public void m_6386_(LivingEntity entity, AttributeMap attributes, int amplifier) {
      AttributeInstance speed = attributes.m_22146_(Attributes.f_22279_);
      if (speed != null) {
         speed.m_22120_(SPEED_MODIFIER_UUID);
      }
   }
}
