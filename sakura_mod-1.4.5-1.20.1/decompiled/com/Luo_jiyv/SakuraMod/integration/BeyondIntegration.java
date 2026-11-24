package com.Luo_jiyv.SakuraMod.integration;

import java.util.UUID;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

public final class BeyondIntegration {
   private static final boolean LOADED = ModList.get().isLoaded("beyonddimensions");

   private BeyondIntegration() {
   }

   public static boolean isLoaded() {
      return LOADED;
   }

   public static int getPrimaryNetId(Player player) {
      return LOADED ? BeyondBridge.primaryNetId(player) : -1;
   }

   @Nullable
   public static BeyondIntegration.Network open(int id, @Nullable UUID member) {
      return LOADED && id >= 0 && member != null ? BeyondBridge.open(id, member) : null;
   }

   public interface ItemKey {
      ItemStack copyStack(int var1);
   }

   public interface Network {
      BeyondIntegration.ItemKey itemKey(ItemStack var1);

      long count(BeyondIntegration.ItemKey var1);

      void forEachItem(BiConsumer<BeyondIntegration.ItemKey, ItemStack> var1);

      @Nullable
      BeyondIntegration.Withdrawal extract(BeyondIntegration.ItemKey var1, int var2);

      int insert(ItemStack var1, int var2);

      long energy();

      int extractEnergy(int var1, boolean var2);

      void refundEnergy(int var1);
   }

   public record Withdrawal(BeyondIntegration.ItemKey key, int count) {
      public ItemStack copyStack(int amount) {
         return this.key.copyStack(amount);
      }
   }
}
