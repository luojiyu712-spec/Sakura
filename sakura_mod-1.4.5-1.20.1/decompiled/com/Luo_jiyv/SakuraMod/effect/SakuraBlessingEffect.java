package com.Luo_jiyv.SakuraMod.effect;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SakuraBlessingEffect extends MobEffect {
   public static final int COLOR = 16758741;

   public SakuraBlessingEffect() {
      super(MobEffectCategory.BENEFICIAL, 16758741);
   }

   public boolean m_6584_(int duration, int amplifier) {
      int interval = Math.max(1, 12 >> amplifier);
      return duration % interval == 0;
   }

   public void m_6742_(LivingEntity entity, int amplifier) {
      if (entity.m_21223_() < entity.m_21233_()) {
         entity.m_5634_(1.0F);
      }

      if (entity instanceof Player player) {
         List<MobEffect> toRemove = new ArrayList<>();

         for (MobEffectInstance inst : player.m_21220_()) {
            if (!ModEffects.isGoldenEffect(inst.m_19544_()) && !SakuraBlessingFilter.isAllowed(inst.m_19544_())) {
               toRemove.add(inst.m_19544_());
            }
         }

         for (MobEffect e : toRemove) {
            player.m_21195_(e);
         }
      }
   }

   public boolean m_8093_() {
      return false;
   }
}
