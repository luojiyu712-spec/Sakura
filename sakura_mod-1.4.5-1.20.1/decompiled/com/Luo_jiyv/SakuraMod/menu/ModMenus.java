package com.Luo_jiyv.SakuraMod.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
   public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "sakura_mod");
   public static final RegistryObject<MenuType<StringFarmMenu>> STRING_FARM_MENU = MENUS.register(
      "string_farm_menu", () -> IForgeMenuType.create(StringFarmMenu::new)
   );
   public static final RegistryObject<MenuType<ChickenCoopMenu>> CHICKEN_COOP = MENUS.register(
      "chicken_coop", () -> IForgeMenuType.create(ChickenCoopMenu::new)
   );
   public static final RegistryObject<MenuType<BeeHoneyMenu>> BEE_HONEY_MENU = MENUS.register("bee_honey_block", () -> IForgeMenuType.create(BeeHoneyMenu::new));
   public static final RegistryObject<MenuType<CobbleGeneratorMenu>> COBBLE_GENERATOR = MENUS.register(
      "cobble_generator", () -> IForgeMenuType.create(CobbleGeneratorMenu::new)
   );
   public static final RegistryObject<MenuType<MolecularAssemblerMenu>> MOLECULAR_ASSEMBLER = MENUS.register(
      "molecular_assembler", () -> IForgeMenuType.create(MolecularAssemblerMenu::new)
   );

   public static void register(IEventBus bus) {
      MENUS.register(bus);
   }
}
