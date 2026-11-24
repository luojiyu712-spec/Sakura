package com.Luo_jiyv.SakuraMod.item;

import com.Luo_jiyv.SakuraMod.block.ModBlocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Moditems {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "sakura_mod");
   private static final FoodProperties SAKURA_COIN_FOOD = new Builder().m_38760_(2).m_38758_(3.6F).m_38767_();
   private static final FoodProperties SAKURA_INGOT_FOOD = new Builder()
      .m_38760_(4)
      .m_38758_(1.0F)
      .m_38765_()
      .effect(() -> new MobEffectInstance(MobEffects.f_19605_, 100), 1.0F)
      .effect(() -> new MobEffectInstance(MobEffects.f_19617_, 600), 1.0F)
      .m_38767_();
   public static final RegistryObject<Item> SAKURA_COIN = ITEMS.register(
      "sakura_coin", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.COMMON).m_41489_(SAKURA_COIN_FOOD))
   );
   public static final RegistryObject<Item> SAKURA_INGOT = ITEMS.register(
      "sakura_ingot", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.UNCOMMON).m_41489_(SAKURA_INGOT_FOOD))
   );
   public static final RegistryObject<Item> SAKURA_DIAMOND = ITEMS.register(
      "sakura_diamond", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.RARE).m_41489_(Foods.f_38830_))
   );
   public static final RegistryObject<Item> SAKURA_STAR = ITEMS.register(
      "sakura_star", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.RARE).m_41489_(Foods.f_38829_))
   );
   public static final RegistryObject<Item> SAKURA_CHECK_10K = ITEMS.register(
      "sakura_check_10k", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> SAKURA_CHECK_100K = ITEMS.register(
      "sakura_check_100k", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> SAKURA_CHECK_1M = ITEMS.register(
      "sakura_check_1m", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> SAKURA_CHECK_10M = ITEMS.register(
      "sakura_check_10m", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> SAKURA_CHECK_100M = ITEMS.register(
      "sakura_check_100m", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> SAKURA_CHECK_1B = ITEMS.register(
      "sakura_check_1b", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> SAKURA_CHECK_10B = ITEMS.register(
      "sakura_check_10b", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> SAKURA_CHECK_100B = ITEMS.register(
      "sakura_check_100b", () -> new SakuraMoneyItems(new Properties().m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> GRAVCORE = ITEMS.register("gravcore", () -> new GravCoreItem(new Properties().m_41497_(Rarity.EPIC).m_41487_(1)));
   public static final RegistryObject<Item> HENCOOP = ITEMS.register("hencoop", () -> new HencoopItem(new Properties().m_41487_(1)));
   public static final RegistryObject<Item> PVP_LICENSE = ITEMS.register(
      "pvp_license", () -> new SakuraPvpLicenseItem(new Properties().m_41487_(1).m_41497_(Rarity.RARE))
   );
   public static final RegistryObject<Item> SAKURA_BLOCK_ITEM = ITEMS.register(
      "sakura_block", () -> new SakuraBlockItem((Block)ModBlocks.SAKURA_BLOCK.get(), new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<Item> STRING_FARM_ITEM = ITEMS.register(
      "string_farm", () -> new BlockItem((Block)ModBlocks.STRING_FARM.get(), new Properties())
   );
   public static final RegistryObject<Item> CHICKEN_COOP_ITEM = ITEMS.register(
      "chicken_coop", () -> new ChickenCoopBlockItem((Block)ModBlocks.CHICKEN_COOP.get(), new Properties())
   );
   public static final RegistryObject<Item> BEE_BOX = ITEMS.register("bee_box", () -> new BeeBoxItem(new Properties().m_41487_(1)));
   public static final RegistryObject<Item> BEE_HONEY_BLOCK_ITEM = ITEMS.register(
      "bee_honey_block", () -> new BeeHoneyBlockItem((Block)ModBlocks.BEE_HONEY_BLOCK.get(), new Properties())
   );
   public static final RegistryObject<Item> SAKURA_REPAIR_KIT = ITEMS.register(
      "sakura_repair_kit", () -> new SakuraRepairKitItem(new Properties().m_41487_(64).m_41497_(Rarity.RARE))
   );
   public static final RegistryObject<Item> BLAZING_ROCK_ITEM = ITEMS.register(
      "blazing_rock", () -> new BlockItem((Block)ModBlocks.BLAZING_ROCK.get(), new Properties().m_41497_(Rarity.UNCOMMON))
   );
   public static final RegistryObject<Item> COBBLE_GENERATOR_ITEM = ITEMS.register(
      "cobble_generator", () -> new BlockItem((Block)ModBlocks.COBBLE_GENERATOR.get(), new Properties().m_41497_(Rarity.COMMON))
   );
   public static final RegistryObject<Item> SAKURA_AMULET = ITEMS.register(
      "sakura_amulet", () -> new SakuraAmuletItem(new Properties().m_41487_(1).m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<Item> SAKURA_HELMET = ITEMS.register(
      "sakura_helmet", () -> new SakuraArmorItem(ModArmorMaterials.SAKURA_STEEL, Type.HELMET, new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<Item> SAKURA_CHESTPLATE = ITEMS.register(
      "sakura_chestplate", () -> new SakuraArmorItem(ModArmorMaterials.SAKURA_STEEL, Type.CHESTPLATE, new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<Item> SAKURA_LEGGINGS = ITEMS.register(
      "sakura_leggings", () -> new SakuraArmorItem(ModArmorMaterials.SAKURA_STEEL, Type.LEGGINGS, new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<Item> SAKURA_BOOTS = ITEMS.register(
      "sakura_boots", () -> new SakuraArmorItem(ModArmorMaterials.SAKURA_STEEL, Type.BOOTS, new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<Item> FLIGHT_BRACELET = ITEMS.register(
      "flight_bracelet", () -> new FlightBraceletItem(new Properties().m_41487_(1).m_41497_(Rarity.EPIC))
   );
   public static final RegistryObject<Item> SAKURA_SWORD = ITEMS.register(
      "sakura_sword", () -> new SakuraSwordItem(ModToolTiers.SAKURA_STEEL, 3, -2.08F, new Properties().m_41497_(Rarity.RARE).m_41486_())
   );
   public static final RegistryObject<Item> SAKURA_AXE = ITEMS.register(
      "sakura_axe", () -> new SakuraAxeItem(ModToolTiers.SAKURA_STEEL, 6.0F, -2.8F, new Properties().m_41497_(Rarity.RARE).m_41486_())
   );
   public static final RegistryObject<Item> SAKURA_SHOVEL = ITEMS.register(
      "sakura_shovel", () -> new SakuraShovelItem(ModToolTiers.SAKURA_STEEL, 2.5F, -2.8F, new Properties().m_41497_(Rarity.RARE).m_41486_())
   );
   public static final RegistryObject<Item> SAKURA_PICKAXE = ITEMS.register(
      "sakura_pickaxe", () -> new SakuraPickaxeItem(ModToolTiers.SAKURA_STEEL, 2, -2.56F, new Properties().m_41497_(Rarity.RARE).m_41486_())
   );
   public static final RegistryObject<Item> SAKURA_HOE = ITEMS.register(
      "sakura_hoe", () -> new SakuraHoeItem(ModToolTiers.SAKURA_STEEL, -4, 0.8F, new Properties().m_41497_(Rarity.RARE).m_41486_())
   );
   public static final RegistryObject<Item> SAKURA_COMPONENT = ITEMS.register(
      "sakura_component", () -> new SakuraComponentItem(new Properties().m_41497_(Rarity.EPIC).m_41487_(64).m_41486_())
   );
   public static final RegistryObject<Item> RANDOM_TELEPORTER = ITEMS.register(
      "random_teleporter", () -> new RandomTeleporterItem(new Properties().m_41487_(1).m_41497_(Rarity.RARE))
   );
   public static final RegistryObject<Item> BARRIER_STONE_ITEM = ITEMS.register(
      "barrier_stone", () -> new BlockItem((Block)ModBlocks.BARRIER_STONE.get(), new Properties().m_41497_(Rarity.RARE))
   );
   public static final RegistryObject<Item> DISPEL_TALISMAN = ITEMS.register(
      "dispel_talisman", () -> new DispelTalismanItem(new Properties().m_41487_(1).m_41497_(Rarity.RARE))
   );
   public static final RegistryObject<Item> CONTROLLER_ITEM = ITEMS.register(
      "controller", () -> new BlockItem((Block)ModBlocks.CONTROLLER.get(), new Properties())
   );
   public static final RegistryObject<Item> MOLECULAR_ASSEMBLER_ITEM = ITEMS.register(
      "molecular_assembler", () -> new BlockItem((Block)ModBlocks.MOLECULAR_ASSEMBLER.get(), new Properties().m_41497_(Rarity.RARE))
   );
   public static final RegistryObject<Item> GOLDEN_CUBE = ITEMS.register(
      "golden_cube", () -> new GoldenCubeItem(new Properties().m_41487_(1).m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<Item> DARK_TRANSCENDENT_SHARD = ITEMS.register(
      "dark_transcendent_shard", () -> new DarkShardItem(new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<Item> ARCHITE_CAPSULE = ITEMS.register(
      "archite_capsule", () -> new ArchiteCapsuleItem(new Properties().m_41497_(Rarity.EPIC).m_41486_())
   );
   public static final RegistryObject<Item> TWISTED_MEAT_RAW = ITEMS.register(
      "twisted_meat_raw", () -> new TwistedMeatItem(false, new Properties().m_41489_(new Builder().m_38760_(2).m_38758_(1.0F).m_38766_().m_38767_()))
   );
   public static final RegistryObject<Item> TWISTED_MEAT_COOKED = ITEMS.register(
      "twisted_meat_cooked", () -> new TwistedMeatItem(true, new Properties().m_41489_(new Builder().m_38760_(2).m_38758_(2.0F).m_38766_().m_38767_()))
   );
   public static final RegistryObject<Item> NEOFORGE_ITEM = ITEMS.register(
      "neoforge", () -> new FoxAnvilBlockItem((Block)ModBlocks.NEOFORGE.get(), new Properties().m_41497_(Rarity.UNCOMMON))
   );
   public static final RegistryObject<Item> EMPTY_MUSIC_DISC = ITEMS.register(
      "empty_music_disc", () -> new RecordItem(1, () -> SoundEvents.f_271165_, new Properties().m_41487_(1).m_41497_(Rarity.RARE), 20)
   );

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }
}
