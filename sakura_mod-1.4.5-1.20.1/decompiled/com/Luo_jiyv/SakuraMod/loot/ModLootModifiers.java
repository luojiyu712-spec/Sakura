package com.Luo_jiyv.SakuraMod.loot;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class ModLootModifiers {
   public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(
      Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "sakura_mod"
   );

   public static void register(IEventBus bus) {
      LOOT_MODIFIERS.register("sakura_loot", SakuraLootModifier.CODEC);
      LOOT_MODIFIERS.register("twisted_meat_drop", TwistedMeatLootModifier.CODEC);
      LOOT_MODIFIERS.register("sakura_vein_drop", SakuraVeinLootModifier.CODEC);
      LOOT_MODIFIERS.register(bus);
   }
}
