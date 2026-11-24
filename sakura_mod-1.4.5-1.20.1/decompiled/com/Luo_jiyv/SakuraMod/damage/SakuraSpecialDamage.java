package com.Luo_jiyv.SakuraMod.damage;

import com.Luo_jiyv.SakuraMod.event.ShieldMechanismEvents;
import com.Luo_jiyv.SakuraMod.util.PlayerEquipCache;
import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class SakuraSpecialDamage {
   private SakuraSpecialDamage() {
   }

   public static boolean isImmuneTarget(LivingEntity target) {
      return target instanceof Player player
         ? player.m_7500_() || player.m_5833_() || !PlayerEquipCache.hasPvpLicense(player)
         : target instanceof OwnableEntity pet && pet.m_21805_() != null;
   }

   public static boolean isSlashActive(Player player) {
      return PlayerEquipCache.hasFullSakuraArmor(player) && PlayerEquipCache.handHoldsSakuraSwordOrAxe(player);
   }

   public static DamageSource convertSlash(LivingEntity target, DamageSource source, float amount) {
      return !target.m_9236_().m_5776_()
            && source.m_7639_() instanceof Player player
            && target != player
            && !ModDamageTypes.isOurDamage(source)
            && amount > 0.0F
            && Float.isFinite(amount)
            && isSlashActive(player)
            && !isImmuneTarget(target)
            && !ShieldMechanismEvents.isReplaying(target, source)
         ? slashSource(target, source, SakuraSpecialDamage.AttackBonuses.capture(player))
         : source;
   }

   public static DamageSource slashSource(LivingEntity target, DamageSource original, SakuraSpecialDamage.AttackBonuses bonuses) {
      Reference<DamageType> slash = target.m_9236_().m_9598_().m_175515_(Registries.f_268580_).m_246971_(ModDamageTypes.SAKURA_SLASH);
      return new SakuraSpecialDamage.ConvertedSlash(slash, original, bonuses);
   }

   public static DamageSource axeSlashSource(LivingEntity target, Player owner, Vec3 position, SakuraSpecialDamage.AttackBonuses bonuses, UUID slashId) {
      Reference<DamageType> slash = target.m_9236_().m_9598_().m_175515_(Registries.f_268580_).m_246971_(ModDamageTypes.SAKURA_SLASH);
      DamageSource original = new DamageSource(target.m_269291_().m_269075_(owner).m_269150_(), null, owner, position);
      return new SakuraSpecialDamage.ConvertedSlash(slash, original, bonuses, slashId);
   }

   @Nullable
   public static UUID axeSlashId(DamageSource source) {
      return source instanceof SakuraSpecialDamage.ConvertedSlash slash ? slash.axeSlashId : null;
   }

   public static DamageSource originalSource(DamageSource source) {
      return source instanceof SakuraSpecialDamage.ConvertedSlash slash ? slash.original : source;
   }

   public static float slashAmount(LivingEntity target, DamageSource source, float amount) {
      return source instanceof SakuraSpecialDamage.ConvertedSlash slash && !ShieldMechanismEvents.isReplaying(target, source)
         ? Math.max(amount, slash.bonuses.apply(target.m_21233_() * 0.2F))
         : amount;
   }

   public record AttackBonuses(double addition, double multiplier) {
      public static SakuraSpecialDamage.AttackBonuses capture(Player player) {
         AttributeInstance attribute = player.m_21051_(Attributes.f_22281_);
         Collection<AttributeModifier> weaponDefaults = player.m_21205_().m_41720_().m_7167_(EquipmentSlot.MAINHAND).get(Attributes.f_22281_);
         double addition = attribute.m_22115_() - 1.0;
         double multiplyBase = 1.0;
         double multiplyTotal = 1.0;

         for (AttributeModifier modifier : attribute.m_22122_()) {
            if (!weaponDefaults.contains(modifier)) {
               switch (modifier.m_22217_()) {
                  case ADDITION:
                     addition += modifier.m_22218_();
                     break;
                  case MULTIPLY_BASE:
                     multiplyBase += modifier.m_22218_();
                     break;
                  case MULTIPLY_TOTAL:
                     multiplyTotal *= 1.0 + modifier.m_22218_();
               }
            }
         }

         return new SakuraSpecialDamage.AttackBonuses(addition, multiplyBase * multiplyTotal);
      }

      private float apply(float baseDamage) {
         return (float)Math.max(0.0, (baseDamage + this.addition) * this.multiplier);
      }
   }

   private static final class ConvertedSlash extends DamageSource {
      private final DamageSource original;
      private final SakuraSpecialDamage.AttackBonuses bonuses;
      @Nullable
      private final UUID axeSlashId;

      private ConvertedSlash(Holder<DamageType> slash, DamageSource original, SakuraSpecialDamage.AttackBonuses bonuses) {
         this(slash, original, bonuses, null);
      }

      private ConvertedSlash(Holder<DamageType> slash, DamageSource original, SakuraSpecialDamage.AttackBonuses bonuses, @Nullable UUID axeSlashId) {
         super(slash, original.m_7640_(), original.m_7639_(), original.m_269181_());
         this.original = original;
         this.bonuses = bonuses;
         this.axeSlashId = axeSlashId;
      }

      public boolean m_269533_(TagKey<DamageType> tag) {
         return super.m_269533_(tag) || this.original.m_269533_(tag);
      }
   }
}
