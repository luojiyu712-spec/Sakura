package com.Luo_jiyv.SakuraMod.loot;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class SakuraLootModifier extends LootModifier {
   public static final Supplier<Codec<SakuraLootModifier>> CODEC = Suppliers.memoize(SakuraLootModifier::createCodec);
   private final Item item;
   private final float chance;
   private final float luckBonus;
   private final float lootingBonus;
   private final int minCount;
   private final int maxCount;
   private final SakuraLootModifier.DropFrom dropFrom;

   private static Codec<SakuraLootModifier> createCodec() {
      return RecordCodecBuilder.create(
         inst -> codecStart(inst)
            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(SakuraLootModifier::getItem))
            .and(Codec.FLOAT.fieldOf("chance").forGetter(SakuraLootModifier::getChance))
            .and(Codec.FLOAT.optionalFieldOf("luck_bonus", 0.0F).forGetter(SakuraLootModifier::getLuckBonus))
            .and(Codec.FLOAT.optionalFieldOf("looting_bonus", 0.0F).forGetter(SakuraLootModifier::getLootingBonus))
            .and(Codec.INT.optionalFieldOf("min_count", 1).forGetter(SakuraLootModifier::getMinCount))
            .and(Codec.INT.optionalFieldOf("max_count", 1).forGetter(SakuraLootModifier::getMaxCount))
            .and(SakuraLootModifier.DropFrom.CODEC.optionalFieldOf("drop_from", SakuraLootModifier.DropFrom.ANY).forGetter(SakuraLootModifier::getDropFrom))
            .apply(inst, SakuraLootModifier::new)
      );
   }

   public SakuraLootModifier(
      LootItemCondition[] conditions,
      Item item,
      float chance,
      float luckBonus,
      float lootingBonus,
      int minCount,
      int maxCount,
      SakuraLootModifier.DropFrom dropFrom
   ) {
      super(conditions);
      if (chance < 0.0F || chance > 1.0F) {
         SakuraMod.LOGGER.warn("[SakuraLootModifier] chance 超出 [0,1]：原值={}，已限制到合法范围。item={}", chance, item);
         chance = Math.max(0.0F, Math.min(1.0F, chance));
      }

      if (minCount < 1) {
         minCount = 1;
      }

      if (maxCount < minCount) {
         maxCount = minCount;
      }

      this.item = item;
      this.chance = chance;
      this.luckBonus = luckBonus;
      this.lootingBonus = lootingBonus;
      this.minCount = minCount;
      this.maxCount = maxCount;
      this.dropFrom = dropFrom;
   }

   private Item getItem() {
      return this.item;
   }

   private float getChance() {
      return this.chance;
   }

   private float getLuckBonus() {
      return this.luckBonus;
   }

   private float getLootingBonus() {
      return this.lootingBonus;
   }

   private int getMinCount() {
      return this.minCount;
   }

   private int getMaxCount() {
      return this.maxCount;
   }

   private SakuraLootModifier.DropFrom getDropFrom() {
      return this.dropFrom;
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
      if (!this.matchesDropFrom(context)) {
         return loot;
      } else {
         float roll = this.chance + context.m_78945_() * this.luckBonus;
         Entity killer = (Entity)context.m_78953_(LootContextParams.f_81458_);
         if (this.lootingBonus > 0.0F && killer instanceof LivingEntity living) {
            int looting = EnchantmentHelper.m_44843_(Enchantments.f_44982_, living.m_21205_());
            roll += looting * this.lootingBonus;
         }

         roll = Math.max(0.0F, Math.min(roll, 1.0F));
         float randomValue = context.m_230907_().m_188501_();
         boolean passed = randomValue < roll;
         int count = 0;
         if (passed) {
            count = this.minCount;
            if (this.maxCount > this.minCount) {
               count += context.m_230907_().m_188503_(this.maxCount - this.minCount + 1);
            }

            if (count > 0) {
               loot.add(new ItemStack(this.item, count));
            }
         }

         if (SakuraMod.LOGGER.isDebugEnabled()) {
            SakuraMod.LOGGER
               .debug(
                  "[SakuraLoot] item={}, dropFrom={}, randomValue={}, passed={}, addedCount={}",
                  new Object[]{this.item, this.dropFrom, randomValue, passed, count}
               );
         }

         return loot;
      }
   }

   private boolean matchesDropFrom(LootContext context) {
      switch (this.dropFrom) {
         case ANY:
            return true;
         case MOB: {
            Entity thisEntity = (Entity)context.m_78953_(LootContextParams.f_81455_);
            return thisEntity instanceof Enemy;
         }
         case UNDEAD: {
            Entity thisEntity = (Entity)context.m_78953_(LootContextParams.f_81455_);
            return thisEntity instanceof LivingEntity living && living.m_6336_() == MobType.f_21641_ && !(living instanceof Player);
         }
         case WARDEN: {
            Entity thisEntity = (Entity)context.m_78953_(LootContextParams.f_81455_);
            return thisEntity instanceof Warden;
         }
         case ANCIENT_CITY:
            ResourceLocation tableIdxx = context.getQueriedLootTableId();
            return tableIdxx != null && isAncientCity(tableIdxx);
         case CHEST_PLAIN:
            ResourceLocation tableIdx = context.getQueriedLootTableId();
            if (tableIdx == null) {
               return false;
            } else {
               if (!tableIdx.m_135815_().startsWith("chests/")) {
                  return false;
               }

               return !isAncientCity(tableIdx);
            }
         case CHEST:
            ResourceLocation tableId = context.getQueriedLootTableId();
            if (tableId == null) {
               return false;
            } else if (!tableId.m_135815_().startsWith("chests/")) {
               return false;
            } else {
               if (Config.isSakuraLootChestOnlyVanilla()) {
                  return "minecraft".equals(tableId.m_135827_());
               }

               return true;
            }
         default:
            return true;
      }
   }

   private static boolean isAncientCity(ResourceLocation tableId) {
      return tableId.m_135815_().startsWith("chests/ancient_city");
   }

   public Codec<? extends IGlobalLootModifier> codec() {
      return CODEC.get();
   }

   public static enum DropFrom {
      ANY,
      MOB,
      UNDEAD,
      WARDEN,
      CHEST,
      CHEST_PLAIN,
      ANCIENT_CITY;

      public static final Codec<SakuraLootModifier.DropFrom> CODEC = Codec.STRING.xmap(s -> {
         if (s == null) {
            return ANY;
         } else {
            try {
               return valueOf(s.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException var2) {
               return ANY;
            }
         }
      }, d -> d.name().toLowerCase(Locale.ROOT));
   }
}
