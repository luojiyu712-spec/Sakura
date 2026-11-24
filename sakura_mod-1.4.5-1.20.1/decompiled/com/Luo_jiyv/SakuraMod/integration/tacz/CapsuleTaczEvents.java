package com.Luo_jiyv.SakuraMod.integration.tacz;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.Luo_jiyv.SakuraMod.damage.GunDamageContext;
import com.Luo_jiyv.SakuraMod.mixin.TaczRecoilPairInvoker;
import com.tacz.guns.api.GunProperty;
import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent.Pre;
import com.tacz.guns.api.modifier.ParameterizedCache;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair;
import it.unimi.dsi.fastutil.Pair;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.thread.EffectiveSide;

public class CapsuleTaczEvents {
   private static final GunDamageSourcePart[] SHIELD_DAMAGE_PARTS = new GunDamageSourcePart[]{
      GunDamageSourcePart.NON_ARMOR_PIERCING, GunDamageSourcePart.ARMOR_PIERCING
   };

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onGunHit(Pre event) {
      if (event.getLogicalSide().isServer() && event.getHurtEntity() instanceof Player && event.getGunId() != null && event.getBullet() != null) {
         GunDamageContext.Shot shot = new GunDamageContext.Shot(event.getGunId(), event.getBullet(), event.getAttacker());

         for (GunDamageSourcePart part : SHIELD_DAMAGE_PARTS) {
            if (event.getDamageSource(part) instanceof GunDamageContext context) {
               context.sakura$setGunShot(shot);
            }
         }
      }
   }

   @SubscribeEvent
   public void onAttachmentProperty(AttachmentPropertyEvent event) {
      try {
         ItemStack gun = event.getGunItem();
         if (!CapsuleData.hasCapsule(gun)) {
            return;
         }

         AttachmentCacheProperty cache = event.getCacheProperty();
         applyInt(cache, gun, CapsuleStat.FIRE_RATE, "rpm", 1);
         applyFloat(cache, gun, CapsuleStat.ADS_TIME, "ads", 0.0F);
         applyFloat(cache, gun, CapsuleStat.AMMO_SPEED, "ammo_speed", 0.0F);
         applyFloat(cache, gun, CapsuleStat.ARMOR_IGNORE, "armor_ignore", 0.0F);
         applyFloat(cache, gun, CapsuleStat.HEAD_SHOT, "head_shot", 0.0F);
         applyFloat(cache, gun, CapsuleStat.WEIGHT, "weight_modifier", 0.0F);
         applyFloat(cache, gun, CapsuleStat.KNOCKBACK, "knockback", 0.0F);
         applyInt(cache, gun, CapsuleStat.PENETRATION, "pierce", 0);
         applyInaccuracy(cache, gun);
         applyRecoil(cache, gun);
         if (EffectiveSide.get().isClient()) {
            applyDamage(cache, gun);
            applyFloat(cache, gun, CapsuleStat.EFFECTIVE_RANGE, "effective_range", 0.0F);
         }
      } catch (Throwable var4) {
         SakuraMod.LOGGER.warn("Archite capsule property hook failed: {}", var4.toString());
      }
   }

   private static void applyFloat(AttachmentCacheProperty cache, ItemStack gun, CapsuleStat stat, String cacheKey, float min) {
      if (stat.isEnabled()) {
         double value = CapsuleData.gunValue(gun, stat);
         if (value != 0.0) {
            if (cache.getCache(cacheKey) instanceof Float current) {
               double adjusted = Math.max((double)min, CapsuleData.apply(current.floatValue(), value, CapsuleData.gunFlat(gun, stat)));
               cache.setCache(GunProperty.of(cacheKey, Float.class), (float)adjusted);
            }
         }
      }
   }

   private static void applyInt(AttachmentCacheProperty cache, ItemStack gun, CapsuleStat stat, String cacheKey, int min) {
      if (stat.isEnabled()) {
         double value = CapsuleData.gunValue(gun, stat);
         if (value != 0.0) {
            if (cache.getCache(cacheKey) instanceof Integer current) {
               double adjusted = CapsuleData.apply(current.intValue(), value, CapsuleData.gunFlat(gun, stat));
               cache.setCache(GunProperty.of(cacheKey, Integer.class), (int)Math.max((long)min, Math.round(adjusted)));
            }
         }
      }
   }

   private static void applyDamage(AttachmentCacheProperty cache, ItemStack gun) {
      if (CapsuleStat.DAMAGE.isEnabled()) {
         double value = CapsuleData.gunValue(gun, CapsuleStat.DAMAGE);
         if (value != 0.0) {
            if (cache.getCache("damage") instanceof List<?> list) {
               boolean flat = CapsuleData.gunFlat(gun, CapsuleStat.DAMAGE);
               LinkedList adjusted = new LinkedList();

               for (Object element : list) {
                  if (!(element instanceof DistanceDamagePair pair)) {
                     return;
                  }

                  float damage = (float)Math.max(0.0, CapsuleData.apply(pair.getDamage(), value, flat));
                  adjusted.add(new DistanceDamagePair(pair.getDistance(), damage));
               }

               if (!adjusted.isEmpty()) {
                  cache.setCache(GunProperty.of("damage", Object.class), adjusted);
               }
            }
         }
      }
   }

   private static void applyInaccuracy(AttachmentCacheProperty cache, ItemStack gun) {
      if (cache.getCache("inaccuracy") instanceof Map<?, ?> map) {
         EnumMap adjusted = new EnumMap(InaccuracyType.class);
         boolean changed = false;

         for (Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() instanceof InaccuracyType type && entry.getValue() instanceof Float base) {
               float value = applyInaccuracyValue(gun, type, base);
               adjusted.put(type, value);
               if (Float.compare(value, base) != 0) {
                  changed = true;
               }
            }
         }

         if (changed) {
            cache.setCache(GunProperty.of("inaccuracy", Map.class), adjusted);
         }
      }
   }

   private static float applyInaccuracyValue(ItemStack gun, InaccuracyType type, float base) {
      CapsuleStat stat = switch (type) {
         case STAND -> CapsuleStat.HIPFIRE_SPREAD;
         case MOVE -> CapsuleStat.MOVE_SPREAD;
         case SNEAK -> CapsuleStat.SNEAK_SPREAD;
         case LIE -> CapsuleStat.PRONE_SPREAD;
         case AIM -> CapsuleStat.AIM_ACCURACY;
         default -> null;
      };
      if (stat == null) {
         return base;
      } else if (!stat.isEnabled()) {
         return base;
      } else {
         double value = CapsuleData.gunValue(gun, stat);
         if (value == 0.0) {
            return base;
         } else {
            double adjusted = type == InaccuracyType.AIM
               ? CapsuleData.applyInverse(base, value, CapsuleData.gunFlat(gun, stat))
               : CapsuleData.apply(base, value, CapsuleData.gunFlat(gun, stat));
            return (float)Math.max(0.0, adjusted);
         }
      }
   }

   private static void applyRecoil(AttachmentCacheProperty cache, ItemStack gun) {
      ParameterizedCachePair<Float, Float> adjusted = adjustRecoil(gun, cache.getCache("recoil"));
      if (adjusted != null) {
         cache.setCache(GunProperty.of("recoil", ParameterizedCachePair.class), adjusted);
      }
   }

   public static ParameterizedCachePair<Float, Float> adjustRecoil(ItemStack gun, Object current) {
      if (current instanceof ParameterizedCachePair<?, ?> currentPair) {
         ParameterizedCachePair original = currentPair;
         if (currentPair.left() instanceof CapsuleTaczEvents.CapsuleRecoil axis) {
            original = axis.original;
         } else if (currentPair.right() instanceof CapsuleTaczEvents.CapsuleRecoil axis) {
            original = axis.original;
         }

         if (!CapsuleData.hasCapsule(gun)) {
            return original;
         } else {
            boolean verticalEnabled = CapsuleStat.RECOIL_VERTICAL.isEnabled();
            boolean horizontalEnabled = CapsuleStat.RECOIL_HORIZONTAL.isEnabled();
            double vertical = verticalEnabled ? CapsuleData.gunValue(gun, CapsuleStat.RECOIL_VERTICAL) : 0.0;
            double horizontal = horizontalEnabled ? CapsuleData.gunValue(gun, CapsuleStat.RECOIL_HORIZONTAL) : 0.0;
            if (vertical == 0.0 && horizontal == 0.0) {
               return original;
            } else {
               ParameterizedCache<Float> pitchCache = original.left();
               ParameterizedCache<Float> yawCache = original.right();
               double pitchScale = recoilScale((Float)pitchCache.getDefaultValue(), vertical, CapsuleData.gunFlat(gun, CapsuleStat.RECOIL_VERTICAL));
               double yawScale = recoilScale((Float)yawCache.getDefaultValue(), horizontal, CapsuleData.gunFlat(gun, CapsuleStat.RECOIL_HORIZONTAL));
               return pitchScale == 1.0 && yawScale == 1.0
                  ? original
                  : TaczRecoilPairInvoker.sakura$create(
                     Pair.of(
                        pitchScale == 1.0 ? pitchCache : new CapsuleTaczEvents.CapsuleRecoil(original, pitchCache, pitchScale),
                        yawScale == 1.0 ? yawCache : new CapsuleTaczEvents.CapsuleRecoil(original, yawCache, yawScale)
                     )
                  );
            }
         }
      } else {
         return null;
      }
   }

   private static double recoilScale(float base, double value, boolean flat) {
      return value != 0.0 && (!flat || !(base <= 0.0F)) ? Math.max(0.0, 1.0 + value / (flat ? base : 100.0)) : 1.0;
   }

   private static final class CapsuleRecoil extends ParameterizedCache<Float> {
      private final ParameterizedCachePair<Float, Float> original;
      private final ParameterizedCache<Float> axis;
      private final double scale;

      private CapsuleRecoil(ParameterizedCachePair<Float, Float> original, ParameterizedCache<Float> axis, double scale) {
         super(List.of(), (Float)axis.getDefaultValue());
         this.original = original;
         this.axis = axis;
         this.scale = scale;
      }

      public double eval(double base) {
         return this.axis.eval(base) * this.scale;
      }

      public double eval(double base, double addend, double percent, double multiplier) {
         return this.axis.eval(base, addend, percent, multiplier) * this.scale;
      }
   }
}
