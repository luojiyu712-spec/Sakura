package com.Luo_jiyv.SakuraMod.loot;

import com.Luo_jiyv.SakuraMod.weather.MistWeather;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class TwistedMeatLootModifier extends LootModifier {
   public static final Supplier<Codec<TwistedMeatLootModifier>> CODEC = Suppliers.memoize(TwistedMeatLootModifier::createCodec);
   private final Item item;
   private final float chance;
   private final float mistChance;
   private final int minCount;
   private final int maxCount;

   private static Codec<TwistedMeatLootModifier> createCodec() {
      return RecordCodecBuilder.create(
         inst -> codecStart(inst)
            .and(ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(m -> m.item))
            .and(Codec.FLOAT.optionalFieldOf("chance", 0.05F).forGetter(m -> m.chance))
            .and(Codec.FLOAT.optionalFieldOf("mist_chance", 0.6F).forGetter(m -> m.mistChance))
            .and(Codec.INT.optionalFieldOf("min_count", 1).forGetter(m -> m.minCount))
            .and(Codec.INT.optionalFieldOf("max_count", 3).forGetter(m -> m.maxCount))
            .apply(inst, TwistedMeatLootModifier::new)
      );
   }

   public TwistedMeatLootModifier(LootItemCondition[] conditions, Item item, float chance, float mistChance, int minCount, int maxCount) {
      super(conditions);
      this.item = item;
      this.chance = Math.max(0.0F, Math.min(1.0F, chance));
      this.mistChance = Math.max(0.0F, Math.min(1.0F, mistChance));
      this.minCount = Math.max(1, minCount);
      this.maxCount = Math.max(this.minCount, maxCount);
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
      Entity entity = (Entity)context.m_78953_(LootContextParams.f_81455_);
      if (entity instanceof LivingEntity && !(entity instanceof Player) && !(entity instanceof ArmorStand)) {
         EntityType<?> type = entity.m_6095_();
         boolean mist = MistWeather.isActive();
         if (mist) {
            if (entity instanceof AbstractSkeleton || entity instanceof SkeletonHorse || type == EntityType.f_20525_) {
               return loot;
            }
         } else if (type != EntityType.f_20501_ && type != EntityType.f_20531_ && type != EntityType.f_20562_ && type != EntityType.f_20458_) {
            return loot;
         }

         float actualChance = mist ? this.mistChance : this.chance;
         if (context.m_230907_().m_188501_() >= actualChance) {
            return loot;
         } else {
            int count = this.minCount;
            if (this.maxCount > this.minCount) {
               count = this.minCount + context.m_230907_().m_188503_(this.maxCount - this.minCount + 1);
            }

            loot.add(new ItemStack(this.item, count));
            return loot;
         }
      } else {
         return loot;
      }
   }

   public Codec<? extends IGlobalLootModifier> codec() {
      return CODEC.get();
   }
}
