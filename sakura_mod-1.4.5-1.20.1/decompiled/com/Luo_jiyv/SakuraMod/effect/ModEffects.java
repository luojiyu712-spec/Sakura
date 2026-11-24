package com.Luo_jiyv.SakuraMod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
   public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "sakura_mod");
   public static final RegistryObject<MobEffect> SAKURA_BLESSING = MOB_EFFECTS.register("sakura_blessing", SakuraBlessingEffect::new);
   public static final RegistryObject<MobEffect> GRAVITY_CRUSH = MOB_EFFECTS.register("gravity_crush", GravityCrushEffect::new);
   public static final RegistryObject<MobEffect> ABSOLUTE_MATERIALISM = MOB_EFFECTS.register("absolute_materialism", AbsoluteMaterialismEffect::new);
   public static final RegistryObject<MobEffect> GOLDEN_ADDICTION_0 = MOB_EFFECTS.register("golden_addiction_0", GoldenAddictionEffect::new);
   public static final RegistryObject<MobEffect> GOLDEN_ADDICTION_1 = MOB_EFFECTS.register("golden_addiction_1", GoldenAddictionEffect::new);
   public static final RegistryObject<MobEffect> GOLDEN_ADDICTION_2 = MOB_EFFECTS.register("golden_addiction_2", GoldenAddictionEffect::new);
   public static final RegistryObject<MobEffect> GOLDEN_ADDICTION_3 = MOB_EFFECTS.register("golden_addiction_3", GoldenAddictionEffect::new);
   public static final RegistryObject<MobEffect> GOLDEN_WITHDRAWAL = MOB_EFFECTS.register("golden_withdrawal", GoldenWithdrawalEffect::new);
   public static final RegistryObject<MobEffect> TWISTED_GROWTH = MOB_EFFECTS.register("twisted_growth", TwistedGrowthEffect::new);
   private static MobEffect[] addictionCache;
   private static MobEffect[] goldenCache;

   public static MobEffect addictionFor(int tier) {
      return switch (Math.max(0, Math.min(3, tier))) {
         case 1 -> (MobEffect)GOLDEN_ADDICTION_1.get();
         case 2 -> (MobEffect)GOLDEN_ADDICTION_2.get();
         case 3 -> (MobEffect)GOLDEN_ADDICTION_3.get();
         default -> (MobEffect)GOLDEN_ADDICTION_0.get();
      };
   }

   public static MobEffect[] allAddictions() {
      MobEffect[] cache = addictionCache;
      if (cache == null) {
         cache = new MobEffect[]{
            (MobEffect)GOLDEN_ADDICTION_0.get(), (MobEffect)GOLDEN_ADDICTION_1.get(), (MobEffect)GOLDEN_ADDICTION_2.get(), (MobEffect)GOLDEN_ADDICTION_3.get()
         };
         addictionCache = cache;
      }

      return cache;
   }

   public static boolean isGoldenEffect(MobEffect effect) {
      if (effect == null) {
         return false;
      } else {
         MobEffect[] cache = goldenCache;
         if (cache == null) {
            MobEffect[] addictions = allAddictions();
            cache = new MobEffect[addictions.length + 1];
            System.arraycopy(addictions, 0, cache, 0, addictions.length);
            cache[addictions.length] = (MobEffect)GOLDEN_WITHDRAWAL.get();
            goldenCache = cache;
         }

         for (MobEffect candidate : cache) {
            if (candidate == effect) {
               return true;
            }
         }

         return false;
      }
   }

   public static void register(IEventBus bus) {
      MOB_EFFECTS.register(bus);
   }
}
