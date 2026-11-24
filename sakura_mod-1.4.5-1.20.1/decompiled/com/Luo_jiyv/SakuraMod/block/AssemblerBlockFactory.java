package com.Luo_jiyv.SakuraMod.block;

import com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntity;
import java.lang.reflect.Constructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;

public final class AssemblerBlockFactory {
   private static final String CREATE_BLOCK_CLASS = "com.Luo_jiyv.SakuraMod.block.MolecularAssemblerBlockCreate";
   private static final String MEKANISM_BE_CLASS = "com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntityMekanism";
   private static Constructor<?> createBlockCtor;
   private static boolean createBlockTried;
   private static Constructor<?> mekanismBeCtor;
   private static boolean mekanismBeTried;

   private AssemblerBlockFactory() {
   }

   public static Block createForRegistration() {
      if (ModList.get().isLoaded("create")) {
         Constructor<?> ctor = createCtor();
         if (ctor != null) {
            try {
               return (Block)ctor.newInstance();
            } catch (Throwable var2) {
            }
         }
      }

      return new MolecularAssemblerBlock();
   }

   public static MolecularAssemblerBlockEntity createEntity(BlockPos pos, BlockState state) {
      if (ModList.get().isLoaded("mekanism")) {
         Constructor<?> ctor = mekanismCtor();
         if (ctor != null) {
            try {
               return (MolecularAssemblerBlockEntity)ctor.newInstance(pos, state);
            } catch (Throwable var4) {
            }
         }
      }

      return new MolecularAssemblerBlockEntity(pos, state);
   }

   private static Constructor<?> createCtor() {
      if (createBlockTried) {
         return createBlockCtor;
      } else {
         createBlockTried = true;

         try {
            Class<?> clazz = Class.forName("com.Luo_jiyv.SakuraMod.block.MolecularAssemblerBlockCreate");
            createBlockCtor = clazz.getConstructor();
         } catch (Throwable var1) {
            createBlockCtor = null;
         }

         return createBlockCtor;
      }
   }

   private static Constructor<?> mekanismCtor() {
      if (mekanismBeTried) {
         return mekanismBeCtor;
      } else {
         mekanismBeTried = true;

         try {
            Class<?> clazz = Class.forName("com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntityMekanism");
            mekanismBeCtor = clazz.getConstructor(BlockPos.class, BlockState.class);
         } catch (Throwable var1) {
            mekanismBeCtor = null;
         }

         return mekanismBeCtor;
      }
   }
}
