package com.Luo_jiyv.SakuraMod.block;

import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "sakura_mod");
   public static final RegistryObject<Block> SAKURA_BLOCK = BLOCKS.register("sakura_block", SakuraEnchantingBlock::new);
   public static final RegistryObject<Block> STRING_FARM = BLOCKS.register(
      "string_farm", () -> (Block)(ModList.get().isLoaded("create") ? CreateWrenchBlocks.stringFarm() : new StringFarmBlock())
   );
   public static final RegistryObject<Block> CHICKEN_COOP = BLOCKS.register(
      "chicken_coop",
      () -> withCreateWrench(
         Properties.m_284310_().m_284180_(MapColor.f_283825_).m_60978_(2.5F).m_60918_(SoundType.f_56736_),
         ChickenCoopBlock::new,
         properties -> CreateWrenchBlocks.chickenCoop(properties)
      )
   );
   public static final RegistryObject<Block> BEE_HONEY_BLOCK = BLOCKS.register(
      "bee_honey_block",
      () -> withCreateWrench(
         Properties.m_284310_().m_284180_(MapColor.f_283825_).m_60978_(2.5F).m_60918_(SoundType.f_56736_),
         BeeHoneyBlock::new,
         properties -> CreateWrenchBlocks.beeHoney(properties)
      )
   );
   public static final RegistryObject<Block> BLAZING_ROCK = BLOCKS.register("blazing_rock", BlazingRockBlock::createForRegistration);
   public static final RegistryObject<Block> COBBLE_GENERATOR = BLOCKS.register(
      "cobble_generator",
      () -> withCreateWrench(
         Properties.m_284310_().m_284180_(MapColor.f_283947_).m_60978_(3.5F).m_60918_(SoundType.f_56742_).m_60999_(),
         CobbleGeneratorBlock::new,
         properties -> CreateWrenchBlocks.cobbleGenerator(properties)
      )
   );
   public static final RegistryObject<Block> BARRIER_STONE = BLOCKS.register(
      "barrier_stone",
      () -> new BarrierStoneBlock(
         Properties.m_284310_().m_284180_(MapColor.f_283889_).m_60913_(5.0F, 1200.0F).m_60918_(SoundType.f_154654_).m_60953_(s -> 7).m_60999_()
      )
   );
   public static final RegistryObject<Block> CONTROLLER = BLOCKS.register(
      "controller",
      () -> withCreateWrench(
         Properties.m_284310_().m_60913_(5.0F, 10.0F).m_60999_().m_60918_(SoundType.f_56743_).m_60955_(),
         ControllerBlock::new,
         properties -> CreateWrenchBlocks.controller(properties)
      )
   );
   public static final RegistryObject<Block> MOLECULAR_ASSEMBLER = BLOCKS.register("molecular_assembler", AssemblerBlockFactory::createForRegistration);
   public static final RegistryObject<Block> NEOFORGE = BLOCKS.register("neoforge", FoxAnvilBlock::new);

   private static Block withCreateWrench(Properties properties, Function<Properties, Block> base, Function<Properties, Block> withCreate) {
      return (ModList.get().isLoaded("create") ? withCreate : base).apply(properties);
   }

   public static void register(IEventBus eventBus) {
      BLOCKS.register(eventBus);
   }
}
