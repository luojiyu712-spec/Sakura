package com.Luo_jiyv.SakuraMod.blockentity;

import java.util.Arrays;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

final class AssemblerOutputRouter {
   static final int INSERTS_PER_TICK = 96;
   private static final int PROBES_PER_TICK = 128;
   private static final int RETRY_INTERVAL = 5;
   private static final int ROUTE_COUNT = 12;
   private final AssemblerOutputRouter.Route[] routes = new AssemblerOutputRouter.Route[12];
   private final LazyOptional<?>[] capabilities = new LazyOptional[6];
   private final int[] slotCounts = new int[6];
   private long tick = Long.MIN_VALUE;
   private int probesLeft;
   private int insertsLeft;
   private int totalSlots;
   private int nextRoute;
   private boolean layoutDirty = true;
   private boolean budgetHit;
   private boolean invalidTarget;

   void beginFlush(long now) {
      if (this.tick != now) {
         this.tick = now;
         this.probesLeft = 128;
         this.insertsLeft = 96;
         this.layoutDirty = true;
      }

      this.budgetHit = false;
      this.invalidTarget = false;
   }

   void changedTargets() {
      this.layoutDirty = true;
      Arrays.fill(this.capabilities, null);
      this.resetRoutes();
   }

   private void resetRoutes() {
      for (AssemblerOutputRouter.Route route : this.routes) {
         if (route != null) {
            route.cursor = route.failedProbes = 0;
            route.retryAt = 0L;
         }
      }
   }

   boolean budgetHit() {
      return this.budgetHit;
   }

   boolean invalidTarget() {
      return this.invalidTarget;
   }

   int push(List<LazyOptional<IItemHandler>> targets, ItemStack stack, int remaining) {
      if (!targets.isEmpty() && remaining > 0) {
         if (this.layoutDirty) {
            this.readLayout(targets);
         }

         if (this.totalSlots == 0) {
            return remaining;
         } else {
            AssemblerOutputRouter.Route route = this.routeFor(stack);
            if (this.tick < route.retryAt) {
               return remaining;
            } else {
               if (route.cursor >= this.totalSlots) {
                  route.cursor = 0;
               }

               int maxStack = stack.m_41741_();
               int targetIndex = 0;
               int start = 0;
               int end = this.slotCounts[0];
               IItemHandler target = null;

               while (remaining > 0 && route.failedProbes < this.totalSlots) {
                  if (this.probesLeft != 0 && this.insertsLeft != 0) {
                     if (route.cursor < start) {
                        start = 0;
                        targetIndex = 0;
                        end = this.slotCounts[0];
                        target = null;
                     }

                     while (route.cursor >= end) {
                        start = end;
                        end += this.slotCounts[++targetIndex];
                        target = null;
                     }

                     int slot = route.cursor - start;
                     LazyOptional<IItemHandler> cap = targets.get(targetIndex);
                     if (target == null) {
                        target = (IItemHandler)cap.orElse(null);
                     }

                     if (target != null && cap.isPresent() && slot < target.getSlots()) {
                        this.probesLeft--;
                        ItemStack existing = target.getStackInSlot(slot);
                        int accepted = 0;
                        boolean sameSlot = false;
                        if (existing.m_41613_() < maxStack && (existing.m_41619_() || ItemStack.m_150942_(existing, stack))) {
                           int space = Math.min(maxStack, target.getSlotLimit(slot)) - existing.m_41613_();
                           if (space > 0) {
                              int offer = Math.min(remaining, space);
                              this.insertsLeft--;
                              int left = target.insertItem(slot, stack.m_255036_(offer), false).m_41613_();
                              accepted = Math.max(0, offer - left);
                              remaining -= accepted;
                              sameSlot = accepted == offer && remaining == 0 && offer < space;
                           }
                        }

                        if (!sameSlot && ++route.cursor >= this.totalSlots) {
                           route.cursor = 0;
                        }

                        if (accepted > 0) {
                           route.failedProbes = 0;
                        } else {
                           route.failedProbes++;
                        }
                        continue;
                     }

                     this.invalidTarget = true;
                     break;
                  }

                  this.budgetHit = true;
                  break;
               }

               if (route.failedProbes >= this.totalSlots) {
                  route.failedProbes = 0;
                  route.retryAt = this.tick + 5L;
               }

               return remaining;
            }
         }
      } else {
         return remaining;
      }
   }

   private void readLayout(List<LazyOptional<IItemHandler>> targets) {
      int total = 0;
      boolean changed = false;

      for (int i = 0; i < targets.size(); i++) {
         LazyOptional<IItemHandler> cap = targets.get(i);
         IItemHandler target = (IItemHandler)cap.orElse(null);
         if (target == null) {
            this.invalidTarget = true;
         }

         int count = target == null ? 0 : target.getSlots();
         if (count != this.slotCounts[i] || this.capabilities[i] != cap) {
            changed = true;
         }

         this.capabilities[i] = cap;
         this.slotCounts[i] = count;
         total += count;
      }

      if (changed || total != this.totalSlots) {
         this.resetRoutes();
      }

      this.totalSlots = total;
      this.layoutDirty = false;

      for (int i = targets.size(); i < this.capabilities.length; i++) {
         this.capabilities[i] = null;
      }
   }

   private AssemblerOutputRouter.Route routeFor(ItemStack stack) {
      for (AssemblerOutputRouter.Route route : this.routes) {
         if (route != null && route.item == stack.m_41720_() && ItemStack.m_150942_(route.sample, stack)) {
            return route;
         }
      }

      AssemblerOutputRouter.Route routex = new AssemblerOutputRouter.Route(stack);
      this.routes[this.nextRoute] = routex;
      if (++this.nextRoute == this.routes.length) {
         this.nextRoute = 0;
      }

      return routex;
   }

   private static final class Route {
      final ItemStack sample;
      final Item item;
      int cursor;
      int failedProbes;
      long retryAt;

      Route(ItemStack stack) {
         this.item = stack.m_41720_();
         this.sample = stack.m_255036_(1);
      }
   }
}
