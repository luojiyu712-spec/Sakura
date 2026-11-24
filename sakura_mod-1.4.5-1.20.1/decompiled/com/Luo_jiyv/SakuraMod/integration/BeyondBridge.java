package com.Luo_jiyv.SakuraMod.integration;

import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import com.wintercogs.beyonddimensions.api.dimensionnet.UnifiedStorage;
import com.wintercogs.beyonddimensions.api.storage.handler.impl.AbstractUnorderedStackHandler.TypeBucket;
import com.wintercogs.beyonddimensions.api.storage.key.KeyAmount;
import com.wintercogs.beyonddimensions.api.storage.key.impl.EnergyStackKey;
import com.wintercogs.beyonddimensions.api.storage.key.impl.ItemStackKey;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

final class BeyondBridge implements BeyondIntegration.Network {
   private final UnifiedStorage storage;

   private BeyondBridge(UnifiedStorage storage) {
      this.storage = storage;
   }

   static int primaryNetId(Player player) {
      try {
         DimensionsNet net = DimensionsNet.getPrimaryNetFromPlayer(player);
         return canAccess(net, player.m_20148_()) ? net.getId() : -1;
      } catch (LinkageError | RuntimeException var2) {
         return -1;
      }
   }

   @Nullable
   static BeyondIntegration.Network open(int id, UUID player) {
      try {
         DimensionsNet net = DimensionsNet.getNetFromId(id);
         return canAccess(net, player) ? new BeyondBridge(net.getUnifiedStorage()) : null;
      } catch (LinkageError | RuntimeException var3) {
         return null;
      }
   }

   private static boolean canAccess(@Nullable DimensionsNet net, UUID player) {
      return net != null && !net.deleted && (net.isOwner(player) || net.isManager(player) || net.getPlayers().contains(player));
   }

   private static long amount(@Nullable KeyAmount result) {
      return result == null ? 0L : Math.max(0L, result.amount());
   }

   @Override
   public BeyondIntegration.ItemKey itemKey(ItemStack sample) {
      return new BeyondBridge.Key(new ItemStackKey(sample));
   }

   @Override
   public long count(BeyondIntegration.ItemKey key) {
      return amount(this.storage.getStackByKey(((BeyondBridge.Key)key).value()));
   }

   @Override
   public void forEachItem(BiConsumer<BeyondIntegration.ItemKey, ItemStack> visitor) {
      TypeBucket items = (TypeBucket)this.storage.getBucket(ItemStackKey.ID).orElse(null);
      if (items != null) {
         int i = 0;

         for (int size = items.size(); i < size; i++) {
            if (items.get(i) instanceof ItemStackKey key) {
               ItemStack stack = key.getReadOnlyStack();
               if (!stack.m_41619_()) {
                  visitor.accept(new BeyondBridge.Key(key), stack);
               }
            }
         }
      }
   }

   @Nullable
   @Override
   public BeyondIntegration.Withdrawal extract(BeyondIntegration.ItemKey key, int count) {
      ItemStackKey requested = ((BeyondBridge.Key)key).value();
      KeyAmount extracted = this.storage.extract(requested, count, false);
      if (extracted != null && !extracted.isEmpty() && extracted.key() instanceof ItemStackKey item) {
         BeyondIntegration.ItemKey actual = (BeyondIntegration.ItemKey)(item == requested ? key : new BeyondBridge.Key(item));
         return new BeyondIntegration.Withdrawal(actual, (int)Math.min(extracted.amount(), 2147483647L));
      } else {
         return null;
      }
   }

   @Override
   public int insert(ItemStack sample, int count) {
      KeyAmount remainder = this.storage.insert(new ItemStackKey(sample), count, false);
      return remainder == null ? 0 : count - (int)Math.min((long)count, amount(remainder));
   }

   @Override
   public long energy() {
      return amount(this.storage.getStackByKey(EnergyStackKey.INSTANCE));
   }

   @Override
   public int extractEnergy(int count, boolean simulate) {
      KeyAmount extracted = this.storage.extract(EnergyStackKey.INSTANCE, count, simulate);
      return extracted != null && extracted.key() == EnergyStackKey.INSTANCE ? (int)Math.min((long)count, amount(extracted)) : 0;
   }

   @Override
   public void refundEnergy(int count) {
      this.storage.insert(EnergyStackKey.INSTANCE, count, false);
   }

   private record Key(ItemStackKey value) implements BeyondIntegration.ItemKey {
      @Override
      public ItemStack copyStack(int count) {
         return this.value.copyStackWithCount(count);
      }
   }
}
