package com.Luo_jiyv.SakuraMod.integration.tacz;

import com.Luo_jiyv.SakuraMod.item.Moditems;
import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fml.ModList;

public final class CapsuleData {
   public static final String DAMAGE = CapsuleStat.DAMAGE.key();
   public static final String FIRE_RATE = CapsuleStat.FIRE_RATE.key();
   public static final String INSTALLED = "SakuraCapsuleInstalled";

   private CapsuleData() {
   }

   public static double capsuleValue(ItemStack capsule, CapsuleStat stat) {
      return capsule.m_41782_() ? capsule.m_41783_().m_128459_(stat.key()) : 0.0;
   }

   public static boolean capsuleFlat(ItemStack capsule, CapsuleStat stat) {
      return capsule.m_41782_() && capsule.m_41783_().m_128471_(stat.flatKey());
   }

   public static void setCapsuleValue(ItemStack capsule, CapsuleStat stat, double value) {
      capsule.m_41784_().m_128347_(stat.key(), clamp(value));
   }

   public static void setCapsuleFlat(ItemStack capsule, CapsuleStat stat, boolean flat) {
      capsule.m_41784_().m_128379_(stat.flatKey(), flat);
   }

   public static double capsuleDamage(ItemStack capsule) {
      return capsuleValue(capsule, CapsuleStat.DAMAGE);
   }

   public static double capsuleFireRate(ItemStack capsule) {
      return capsuleValue(capsule, CapsuleStat.FIRE_RATE);
   }

   public static void setCapsuleValues(ItemStack capsule, double damagePct, double fireRatePct) {
      setCapsuleValue(capsule, CapsuleStat.DAMAGE, damagePct);
      setCapsuleValue(capsule, CapsuleStat.FIRE_RATE, fireRatePct);
   }

   public static void copyStats(ItemStack from, ItemStack to) {
      for (CapsuleStat stat : CapsuleStat.values()) {
         setCapsuleValue(to, stat, capsuleValue(from, stat));
         setCapsuleFlat(to, stat, capsuleFlat(from, stat));
      }
   }

   public static boolean hasCapsule(ItemStack gun) {
      return gun.m_41782_() && gun.m_41783_().m_128471_("SakuraCapsuleInstalled");
   }

   public static double gunValue(ItemStack gun, CapsuleStat stat) {
      return hasCapsule(gun) ? gun.m_41783_().m_128459_(stat.key()) : 0.0;
   }

   public static boolean gunFlat(ItemStack gun, CapsuleStat stat) {
      return hasCapsule(gun) && gun.m_41783_().m_128471_(stat.flatKey());
   }

   public static double gunDamage(ItemStack gun) {
      return gunValue(gun, CapsuleStat.DAMAGE);
   }

   public static double gunFireRate(ItemStack gun) {
      return gunValue(gun, CapsuleStat.FIRE_RATE);
   }

   public static double apply(double base, double value, boolean flat) {
      if (value == 0.0) {
         return base;
      } else {
         return flat ? base + value : base * (1.0 + value / 100.0);
      }
   }

   public static double applyInverse(double base, double value, boolean flat) {
      if (value == 0.0) {
         return base;
      } else {
         return flat ? base - value : base * (1.0 - value / 100.0);
      }
   }

   public static double applyGunValue(ItemStack gun, CapsuleStat stat, double base) {
      return apply(base, gunValue(gun, stat), gunFlat(gun, stat));
   }

   public static void install(ItemStack gun, ItemStack capsule) {
      CompoundTag tag = gun.m_41784_();
      tag.m_128379_("SakuraCapsuleInstalled", true);

      for (CapsuleStat stat : CapsuleStat.values()) {
         tag.m_128347_(stat.key(), clamp(capsuleValue(capsule, stat)));
         tag.m_128379_(stat.flatKey(), capsuleFlat(capsule, stat));
      }
   }

   public static void install(ItemStack gun, double damagePct, double fireRatePct) {
      CompoundTag tag = gun.m_41784_();
      tag.m_128379_("SakuraCapsuleInstalled", true);

      for (CapsuleStat stat : CapsuleStat.values()) {
         tag.m_128473_(stat.key());
         tag.m_128473_(stat.flatKey());
      }

      tag.m_128347_(CapsuleStat.DAMAGE.key(), clamp(damagePct));
      tag.m_128347_(CapsuleStat.FIRE_RATE.key(), clamp(fireRatePct));
   }

   public static void uninstall(ItemStack gun) {
      if (gun.m_41782_()) {
         CompoundTag tag = gun.m_41783_();
         tag.m_128473_("SakuraCapsuleInstalled");

         for (CapsuleStat stat : CapsuleStat.values()) {
            tag.m_128473_(stat.key());
            tag.m_128473_(stat.flatKey());
         }
      }
   }

   public static ItemStack createCapsuleFromGun(ItemStack gun) {
      ItemStack capsule = new ItemStack((ItemLike)Moditems.ARCHITE_CAPSULE.get());
      copyStats(gun, capsule);
      return capsule;
   }

   public static String formatValue(ItemStack stack, CapsuleStat stat) {
      double value = capsuleValue(stack, stat);
      return capsuleFlat(stack, stat) ? String.format(Locale.ROOT, "%+.2f", value) : String.format(Locale.ROOT, "%+.2f%%", value);
   }

   public static boolean isTaczGun(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else {
         for (Class<?> c = stack.m_41720_().getClass(); c != null; c = c.getSuperclass()) {
            for (Class<?> i : c.getInterfaces()) {
               if ("com.tacz.guns.api.item.IGun".equals(i.getName())) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static void refreshTaczCache(LivingEntity holder, ItemStack gun) {
      if (ModList.get().isLoaded("tacz")) {
         try {
            Class<?> manager = Class.forName("com.tacz.guns.resource.modifier.AttachmentPropertyManager");
            manager.getMethod("postChangeEvent", LivingEntity.class, ItemStack.class).invoke(null, holder, gun);
         } catch (Throwable var3) {
         }
      }
   }

   private static double clamp(double value) {
      return !Double.isFinite(value) ? 0.0 : Math.max(-100.0, Math.min(100.0, value));
   }
}
