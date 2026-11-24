package com.Luo_jiyv.SakuraMod.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

public record SakuraWeaponAttack(float charge, float damage, boolean critical, int fireAspect, double knockback) {
   public static SakuraWeaponAttack capture(Player player) {
      float charge = player.m_36403_(0.5F);
      float damage = (float)player.m_21133_(Attributes.f_22281_) * (0.2F + charge * charge * 0.8F);
      boolean critical = charge > 0.9F
         && player.f_19789_ > 0.0F
         && !player.m_20096_()
         && !player.m_6147_()
         && !player.m_20069_()
         && !player.m_21023_(MobEffects.f_19610_)
         && !player.m_20159_()
         && !player.m_20142_();
      double knockback = 0.5 * (player.m_21133_(Attributes.f_22282_) + EnchantmentHelper.m_44894_(player) + (player.m_20142_() && charge > 0.9F ? 1 : 0));
      return new SakuraWeaponAttack(charge, damage, critical, EnchantmentHelper.m_44914_(player), knockback);
   }

   public void hit(Player player, LivingEntity target, ItemStack weapon, DamageSource source, Vec3 away, double additionalKnockback) {
      float enchantmentDamage = this.charge * EnchantmentHelper.m_44833_(weapon, target.m_6336_());
      if (!(this.damage <= 0.0F) || !(enchantmentDamage <= 0.0F)) {
         CriticalHitEvent criticalHit = ForgeHooks.getCriticalHit(player, target, this.critical, this.critical ? 1.5F : 1.0F);
         float baseDamage = criticalHit == null ? this.damage : this.damage * criticalHit.getDamageModifier();
         boolean litForHit = this.fireAspect > 0 && !target.m_6060_();
         if (litForHit) {
            target.m_20254_(1);
         }

         if (!target.m_6469_(source, baseDamage + enchantmentDamage)) {
            if (litForHit) {
               target.m_20095_();
            }
         } else {
            double strength = additionalKnockback + this.knockback;
            if (strength > 0.0) {
               target.m_147240_(strength, -away.f_82479_, -away.f_82481_);
            }

            if (enchantmentDamage > 0.0F) {
               player.m_5700_(target);
            }

            player.m_21335_(target);
            EnchantmentHelper.m_44823_(target, player);
            if (source.m_7640_() == player) {
               EnchantmentHelper.m_44896_(player, target);
            } else {
               EnchantmentHelper.m_44831_(weapon).forEach((enchantment, level) -> enchantment.m_7677_(player, target, level));
            }

            if (this.fireAspect > 0) {
               target.m_20254_(this.fireAspect * 4);
            }
         }
      }
   }
}
