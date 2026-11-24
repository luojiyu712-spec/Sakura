package com.Luo_jiyv.SakuraMod.util;

import com.Luo_jiyv.SakuraMod.item.FlightBraceletItem;
import com.Luo_jiyv.SakuraMod.item.GravCoreItem;
import com.Luo_jiyv.SakuraMod.item.ModArmorMaterials;
import com.Luo_jiyv.SakuraMod.item.Moditems;
import com.Luo_jiyv.SakuraMod.item.SakuraAmuletItem;
import com.Luo_jiyv.SakuraMod.item.SakuraPvpLicenseItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class PlayerEquipCache {
   private static final Map<UUID, PlayerEquipCache.Entry> SERVER_CACHE = new ConcurrentHashMap<>();
   private static final Map<UUID, PlayerEquipCache.Entry> CLIENT_CACHE = new ConcurrentHashMap<>();

   private static Map<UUID, PlayerEquipCache.Entry> cacheFor(Player player) {
      return player.m_9236_().m_5776_() ? CLIENT_CACHE : SERVER_CACHE;
   }

   private PlayerEquipCache() {
   }

   public static void invalidate(Player player) {
      cacheFor(player).remove(player.m_20148_());
   }

   @SubscribeEvent
   public static void onLogout(PlayerLoggedOutEvent event) {
      UUID id = event.getEntity().m_20148_();
      SERVER_CACHE.remove(id);
      CLIENT_CACHE.remove(id);
   }

   @SubscribeEvent
   public static void onCurioChange(CurioChangeEvent event) {
      if (event.getEntity() instanceof Player player) {
         PlayerEquipCache.Entry e = cacheFor(player).get(player.m_20148_());
         if (e != null) {
            e.curiosDirty = true;
         }
      }
   }

   public static boolean hasFullSakuraArmor(Player player) {
      return get(player).hasFullSakuraArmor;
   }

   public static int countSakuraArmor(Player player) {
      return get(player).sakuraArmorCount;
   }

   public static boolean hasSakuraAmulet(Player player) {
      return get(player).hasSakuraAmulet;
   }

   public static boolean hasGravCore(Player player) {
      return get(player).hasGravCore;
   }

   public static boolean hasPvpLicense(Player player) {
      return get(player).hasPvpLicense;
   }

   public static boolean handHoldsGravCore(Player player) {
      PlayerEquipCache.Entry e = get(player);
      return e.mainHandGravCore || e.offHandGravCore;
   }

   public static boolean handHoldsSakuraSwordOrAxe(Player player) {
      PlayerEquipCache.Entry e = get(player);
      return e.mainHandSakuraSwordAxe || e.offHandSakuraSwordAxe;
   }

   public static boolean handHoldsSakuraTool(Player player) {
      PlayerEquipCache.Entry e = get(player);
      return e.mainHandSakuraTool || e.offHandSakuraTool;
   }

   public static boolean hasFlightBracelet(Player player) {
      return !get(player).bracelets.isEmpty();
   }

   public static boolean hasChargedFlightBracelet(Player player) {
      List<ItemStack> list = get(player).bracelets;

      for (int i = 0; i < list.size(); i++) {
         ItemStack s = list.get(i);
         if (!s.m_41619_() && FlightBraceletItem.getFlightTime(s) > 0) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasCoreAccessible(Player player) {
      PlayerEquipCache.Entry e = get(player);
      return e.mainHandGravCore || e.offHandGravCore || e.hasGravCore;
   }

   public static boolean hasGoldenCube(Player player) {
      PlayerEquipCache.Entry entry = get(player);
      long tick = player.m_9236_().m_46467_();
      if (entry.goldenCubeScanTick == Long.MIN_VALUE || tick - entry.goldenCubeScanTick >= 10L) {
         entry.goldenCubeScanTick = tick;
         entry.hasGoldenCube = scanGoldenCube(player);
      }

      return entry.hasGoldenCube;
   }

   private static boolean scanGoldenCube(Player player) {
      Item cube = (Item)Moditems.GOLDEN_CUBE.get();

      for (ItemStack stack : player.m_150109_().f_35974_) {
         if (!stack.m_41619_() && stack.m_41720_() == cube) {
            return true;
         }
      }

      return player.m_21206_().m_41720_() == cube;
   }

   private static PlayerEquipCache.Entry get(Player player) {
      Map<UUID, PlayerEquipCache.Entry> map = cacheFor(player);
      UUID id = player.m_20148_();
      PlayerEquipCache.Entry e = map.get(id);
      if (e == null) {
         e = new PlayerEquipCache.Entry();
         map.put(id, e);
      }

      long tick = player.m_9236_().m_46467_();
      if (e.lastUpdateTick != tick) {
         e.lastUpdateTick = tick;
         e.update(player);
      }

      return e;
   }

   private static boolean stackChanged(ItemStack current, ItemStack cached) {
      if (current.m_41619_() && cached.m_41619_()) {
         return false;
      } else {
         return !current.m_41619_() && !cached.m_41619_() ? !ItemStack.m_150942_(current, cached) : true;
      }
   }

   public static boolean isSakuraArmor(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else {
         return stack.m_41720_() instanceof ArmorItem armor ? armor.m_40401_() == ModArmorMaterials.SAKURA_STEEL : false;
      }
   }

   private static boolean isSakuraTool(Item item) {
      return item == Moditems.SAKURA_SWORD.get()
         || item == Moditems.SAKURA_AXE.get()
         || item == Moditems.SAKURA_SHOVEL.get()
         || item == Moditems.SAKURA_PICKAXE.get()
         || item == Moditems.SAKURA_HOE.get();
   }

   private static boolean isSakuraSwordOrAxe(Item item) {
      return item == Moditems.SAKURA_SWORD.get() || item == Moditems.SAKURA_AXE.get();
   }

   private static final class Entry {
      long lastUpdateTick = Long.MIN_VALUE;
      ItemStack cachedHead = ItemStack.f_41583_;
      ItemStack cachedChest = ItemStack.f_41583_;
      ItemStack cachedLegs = ItemStack.f_41583_;
      ItemStack cachedFeet = ItemStack.f_41583_;
      ItemStack cachedMainHand = ItemStack.f_41583_;
      ItemStack cachedOffHand = ItemStack.f_41583_;
      boolean hasFullSakuraArmor;
      int sakuraArmorCount;
      boolean hasSakuraAmulet;
      boolean hasGravCore;
      boolean hasPvpLicense;
      boolean hasGoldenCube;
      long goldenCubeScanTick = Long.MIN_VALUE;
      boolean mainHandGravCore;
      boolean offHandGravCore;
      boolean mainHandSakuraSwordAxe;
      boolean offHandSakuraSwordAxe;
      boolean mainHandSakuraTool;
      boolean offHandSakuraTool;
      final List<ItemStack> bracelets = new ArrayList<>();
      boolean curiosDirty = true;

      void update(Player player) {
         this.updateArmor(player);
         this.updateHands(player);
         if (this.curiosDirty) {
            this.curiosDirty = false;
            this.updateCurios(player);
         }
      }

      private void updateArmor(Player player) {
         ItemStack head = player.m_6844_(EquipmentSlot.HEAD);
         ItemStack chest = player.m_6844_(EquipmentSlot.CHEST);
         ItemStack legs = player.m_6844_(EquipmentSlot.LEGS);
         ItemStack feet = player.m_6844_(EquipmentSlot.FEET);
         boolean changed = PlayerEquipCache.stackChanged(head, this.cachedHead)
            || PlayerEquipCache.stackChanged(chest, this.cachedChest)
            || PlayerEquipCache.stackChanged(legs, this.cachedLegs)
            || PlayerEquipCache.stackChanged(feet, this.cachedFeet);
         if (changed) {
            this.cachedHead = head.m_41777_();
            this.cachedChest = chest.m_41777_();
            this.cachedLegs = legs.m_41777_();
            this.cachedFeet = feet.m_41777_();
            int count = 0;
            if (PlayerEquipCache.isSakuraArmor(head)) {
               count++;
            }

            if (PlayerEquipCache.isSakuraArmor(chest)) {
               count++;
            }

            if (PlayerEquipCache.isSakuraArmor(legs)) {
               count++;
            }

            if (PlayerEquipCache.isSakuraArmor(feet)) {
               count++;
            }

            this.sakuraArmorCount = count;
            this.hasFullSakuraArmor = count == 4;
         }
      }

      private void updateHands(Player player) {
         ItemStack main = player.m_21205_();
         ItemStack off = player.m_21206_();
         boolean changed = PlayerEquipCache.stackChanged(main, this.cachedMainHand) || PlayerEquipCache.stackChanged(off, this.cachedOffHand);
         if (changed) {
            this.cachedMainHand = main.m_41777_();
            this.cachedOffHand = off.m_41777_();
            this.mainHandGravCore = main.m_41720_() instanceof GravCoreItem;
            this.offHandGravCore = off.m_41720_() instanceof GravCoreItem;
            Item m = main.m_41720_();
            Item o = off.m_41720_();
            this.mainHandSakuraSwordAxe = PlayerEquipCache.isSakuraSwordOrAxe(m);
            this.offHandSakuraSwordAxe = PlayerEquipCache.isSakuraSwordOrAxe(o);
            this.mainHandSakuraTool = PlayerEquipCache.isSakuraTool(m);
            this.offHandSakuraTool = PlayerEquipCache.isSakuraTool(o);
         }
      }

      private void updateCurios(Player player) {
         boolean amulet = false;
         boolean core = false;
         boolean license = false;
         this.bracelets.clear();
         Optional<ICuriosItemHandler> handlerOpt = CuriosApi.getCuriosInventory(player).resolve();
         if (handlerOpt.isPresent()) {
            ICuriosItemHandler handler = handlerOpt.get();
            IItemHandlerModifiable equipped = handler.getEquippedCurios();

            for (int i = 0; i < equipped.getSlots(); i++) {
               ItemStack s = equipped.getStackInSlot(i);
               if (!s.m_41619_()) {
                  if (s.m_41720_() instanceof SakuraAmuletItem) {
                     amulet = true;
                  } else if (s.m_41720_() instanceof FlightBraceletItem) {
                     this.bracelets.add(s);
                  }
               }
            }

            Optional<ICurioStacksHandler> coreHandlerOpt = handler.getStacksHandler("core");
            if (coreHandlerOpt.isPresent()) {
               IDynamicStackHandler stacks = coreHandlerOpt.get().getStacks();

               for (int ix = 0; ix < stacks.getSlots(); ix++) {
                  ItemStack s = stacks.getStackInSlot(ix);
                  if (!s.m_41619_()) {
                     if (s.m_41720_() instanceof GravCoreItem) {
                        core = true;
                     }

                     if (s.m_41720_() instanceof SakuraPvpLicenseItem) {
                        license = true;
                     }
                  }
               }
            }
         }

         this.hasSakuraAmulet = amulet;
         this.hasGravCore = core;
         this.hasPvpLicense = license;
      }
   }
}
