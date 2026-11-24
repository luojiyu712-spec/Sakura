package com.Luo_jiyv.SakuraMod.blockentity;

import com.Luo_jiyv.SakuraMod.block.AssemblerBlockFactory;
import com.Luo_jiyv.SakuraMod.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "sakura_mod");
   public static final RegistryObject<BlockEntityType<StringFarmBlockEntity>> STRING_FARM = BLOCK_ENTITIES.register(
      "string_farm", () -> Builder.m_155273_(StringFarmBlockEntity::new, new Block[]{(Block)ModBlocks.STRING_FARM.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<ChickenCoopBlockEntity>> CHICKEN_COOP = BLOCK_ENTITIES.register(
      "chicken_coop", () -> Builder.m_155273_(ChickenCoopBlockEntity::new, new Block[]{(Block)ModBlocks.CHICKEN_COOP.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<BeeHoneyBlockEntity>> BEE_HONEY_BLOCK = BLOCK_ENTITIES.register(
      "bee_honey_block", () -> Builder.m_155273_(BeeHoneyBlockEntity::new, new Block[]{(Block)ModBlocks.BEE_HONEY_BLOCK.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<BlazingRockBlockEntity>> BLAZING_ROCK = BLOCK_ENTITIES.register(
      "blazing_rock", () -> Builder.m_155273_(BlazingRockBlockEntity::new, new Block[]{(Block)ModBlocks.BLAZING_ROCK.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<CobbleGeneratorBlockEntity>> COBBLE_GENERATOR = BLOCK_ENTITIES.register(
      "cobble_generator", () -> Builder.m_155273_(CobbleGeneratorBlockEntity::new, new Block[]{(Block)ModBlocks.COBBLE_GENERATOR.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<BarrierStoneBlockEntity>> BARRIER_STONE = BLOCK_ENTITIES.register(
      "barrier_stone", () -> Builder.m_155273_(BarrierStoneBlockEntity::new, new Block[]{(Block)ModBlocks.BARRIER_STONE.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<ControllerBlockEntity>> CONTROLLER = BLOCK_ENTITIES.register(
      "controller", () -> Builder.m_155273_(ControllerBlockEntity::new, new Block[]{(Block)ModBlocks.CONTROLLER.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<MolecularAssemblerBlockEntity>> MOLECULAR_ASSEMBLER = BLOCK_ENTITIES.register(
      "molecular_assembler",
      () -> Builder.m_155273_(AssemblerBlockFactory::createEntity, new Block[]{(Block)ModBlocks.MOLECULAR_ASSEMBLER.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<FoxAnvilBlockEntity>> NEOFORGE = BLOCK_ENTITIES.register(
      "neoforge", () -> Builder.m_155273_(FoxAnvilBlockEntity::new, new Block[]{(Block)ModBlocks.NEOFORGE.get()}).m_58966_(null)
   );

   public static void register(IEventBus bus) {
      BLOCK_ENTITIES.register(bus);
   }
}
