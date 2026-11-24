package com.Luo_jiyv.SakuraMod.loot;

import com.Luo_jiyv.SakuraMod.item.Moditems;
import com.Luo_jiyv.SakuraMod.item.SakuraPickaxeItem;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public final class SakuraVeinLootModifier extends LootModifier {
   public static final Supplier<Codec<SakuraVeinLootModifier>> CODEC = Suppliers.memoize(
      () -> RecordCodecBuilder.create(instance -> codecStart(instance).apply(instance, SakuraVeinLootModifier::new))
   );

   public SakuraVeinLootModifier(LootItemCondition[] conditions) {
      super(conditions);
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
      ItemStack tool = (ItemStack)context.m_78953_(LootContextParams.f_81463_);
      BlockState state = (BlockState)context.m_78953_(LootContextParams.f_81461_);
      if (tool != null
         && tool.m_150930_((Item)Moditems.SAKURA_PICKAXE.get())
         && SakuraPickaxeItem.isVeinMiningEnabled(tool)
         && state != null
         && state.m_204336_(Blocks.ORES)
         && context.m_78953_(LootContextParams.f_81455_) instanceof ServerPlayer player
         && !player.m_7500_()
         && !player.m_5833_()
         && EnchantmentHelper.m_44843_(Enchantments.f_44985_, tool) <= 0
         && !loot.isEmpty()
         && !(context.m_230907_().m_188501_() >= 0.5F)) {
         int size = loot.size();

         for (int i = 0; i < size; i++) {
            loot.add(((ItemStack)loot.get(i)).m_41777_());
         }

         return loot;
      } else {
         return loot;
      }
   }

   public Codec<? extends IGlobalLootModifier> codec() {
      return CODEC.get();
   }
}
