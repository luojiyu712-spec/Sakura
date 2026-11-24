package com.Luo_jiyv.SakuraMod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.f_279569_, "sakura_mod");
   public static final RegistryObject<CreativeModeTab> SAKURA_MONEY_TAB = CREATIVE_MODE_TABS.register(
      "sakura_money_tab",
      () -> CreativeModeTab.builder()
         .m_257737_(() -> new ItemStack((ItemLike)Moditems.SAKURA_INGOT.get()))
         .m_257941_(Component.m_237115_("itemGroup.sakura_tab"))
         .m_257501_((parameters, output) -> {
            output.m_246326_((ItemLike)Moditems.SAKURA_COIN.get());
            output.m_246326_((ItemLike)Moditems.SAKURA_INGOT.get());
            output.m_246326_((ItemLike)Moditems.SAKURA_DIAMOND.get());
            output.m_246326_((ItemLike)Moditems.SAKURA_STAR.get());
            output.m_246326_((ItemLike)Moditems.SAKURA_BLOCK_ITEM.get());
         })
         .m_257652_()
   );
   public static final RegistryObject<CreativeModeTab> SAKURA_CHECK_TAB = CREATIVE_MODE_TABS.register(
      "sakura_check_tab",
      () -> CreativeModeTab.builder()
         .m_257737_(() -> new ItemStack((ItemLike)Moditems.SAKURA_CHECK_1M.get()))
         .m_257941_(Component.m_237115_("itemGroup.sakura_check_tab"))
         .m_257501_((pParameters, pOutput) -> {
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_CHECK_10K.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_CHECK_100K.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_CHECK_1M.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_CHECK_10M.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_CHECK_100M.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_CHECK_1B.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_CHECK_10B.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_CHECK_100B.get());
         })
         .withTabsBefore(new ResourceKey[]{SAKURA_MONEY_TAB.getKey()})
         .m_257652_()
   );
   public static final RegistryObject<CreativeModeTab> SAKURA_FUNCTIONAL_TAB = CREATIVE_MODE_TABS.register(
      "sakura_functional_tab",
      () -> CreativeModeTab.builder()
         .m_257737_(() -> new ItemStack((ItemLike)Moditems.GRAVCORE.get()))
         .m_257941_(Component.m_237115_("itemGroup.sakura_functional_tab"))
         .m_257501_((pParameters, pOutput) -> {
            pOutput.m_246326_((ItemLike)Moditems.GRAVCORE.get());
            pOutput.m_246326_((ItemLike)Moditems.HENCOOP.get());
            pOutput.m_246326_((ItemLike)Moditems.BEE_BOX.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_COMPONENT.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_REPAIR_KIT.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_AMULET.get());
            pOutput.m_246326_((ItemLike)Moditems.RANDOM_TELEPORTER.get());
            pOutput.m_246326_((ItemLike)Moditems.FLIGHT_BRACELET.get());
            pOutput.m_246326_((ItemLike)Moditems.PVP_LICENSE.get());
            pOutput.m_246326_((ItemLike)Moditems.DISPEL_TALISMAN.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_HELMET.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_CHESTPLATE.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_LEGGINGS.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_BOOTS.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_SWORD.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_AXE.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_SHOVEL.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_PICKAXE.get());
            pOutput.m_246326_((ItemLike)Moditems.SAKURA_HOE.get());
            pOutput.m_246326_((ItemLike)Moditems.BLAZING_ROCK_ITEM.get());
            pOutput.m_246326_((ItemLike)Moditems.STRING_FARM_ITEM.get());
            pOutput.m_246326_((ItemLike)Moditems.CHICKEN_COOP_ITEM.get());
            pOutput.m_246326_((ItemLike)Moditems.BEE_HONEY_BLOCK_ITEM.get());
            pOutput.m_246326_((ItemLike)Moditems.COBBLE_GENERATOR_ITEM.get());
            pOutput.m_246326_((ItemLike)Moditems.BARRIER_STONE_ITEM.get());
            pOutput.m_246326_((ItemLike)Moditems.CONTROLLER_ITEM.get());
            pOutput.m_246326_((ItemLike)Moditems.MOLECULAR_ASSEMBLER_ITEM.get());
         })
         .withTabsBefore(new ResourceKey[]{SAKURA_CHECK_TAB.getKey()})
         .m_257652_()
   );
   public static final RegistryObject<CreativeModeTab> SAKURA_GOLDEN_CUBE_TAB = CREATIVE_MODE_TABS.register(
      "anomaly",
      () -> CreativeModeTab.builder()
         .m_257737_(() -> new ItemStack((ItemLike)Moditems.GOLDEN_CUBE.get()))
         .m_257941_(Component.m_237115_("itemGroup.sakura_mod.anomaly"))
         .m_257501_((parameters, output) -> {
            output.m_246326_((ItemLike)Moditems.GOLDEN_CUBE.get());
            output.m_246326_((ItemLike)Moditems.DARK_TRANSCENDENT_SHARD.get());
            output.m_246326_((ItemLike)Moditems.ARCHITE_CAPSULE.get());
            output.m_246326_((ItemLike)Moditems.TWISTED_MEAT_RAW.get());
            output.m_246326_((ItemLike)Moditems.TWISTED_MEAT_COOKED.get());
            output.m_246326_((ItemLike)Moditems.EMPTY_MUSIC_DISC.get());
         })
         .m_257652_()
   );

   public static void register(IEventBus eventBus) {
      CREATIVE_MODE_TABS.register(eventBus);
   }
}
