package com.Luo_jiyv.SakuraMod.blockentity;

import com.Luo_jiyv.SakuraMod.block.MolecularAssemblerBlock;
import com.Luo_jiyv.SakuraMod.integration.BeyondIntegration;
import com.Luo_jiyv.SakuraMod.menu.MolecularAssemblerMenu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MolecularAssemblerBlockEntity extends BlockEntity implements MenuProvider, IDroppable {
   public static final int INPUT_START = 0;
   public static final int INPUT_SIZE = 27;
   public static final int OUTPUT_START = 27;
   public static final int OUTPUT_SIZE = 1;
   public static final int TOTAL_SLOTS = 28;
   public static final int CAPACITY = 360000;
   public static final int MAX_IO = 20000;
   public static final int ENERGY_PER_TICK_SINGLE = 100;
   public static final int ENERGY_PER_TICK_STACK = 6400;
   public static final int MODE_ALWAYS = 0;
   public static final int MODE_STOPPED = 1;
   public static final int MODE_REDSTONE_ON = 2;
   public static final int MODE_REDSTONE_OFF = 3;
   public static final int AMOUNT_SINGLE = 0;
   public static final int AMOUNT_STACK = 1;
   public static final int SIDE_NONE = 0;
   public static final int SIDE_OUTPUT = 1;
   public static final int SIDE_INPUT = 2;
   public static final int SIDE_BOTH = 3;
   private static final int STACK_BATCH = 192;
   private static final int SINGLE_BATCH = 3;
   private static final int CRAFT_INTERVAL = 2;
   private static final int SINGLE_NETWORK_BATCHES = 4;
   private static final int STACK_NETWORK_BATCHES = 2;
   private static final int TARGET_REFRESH = 40;
   private static final int NETWORK_RETRY = 10;
   public static final int DATA_ENERGY = 0;
   public static final int DATA_CAPACITY = 1;
   public static final int DATA_WORK_MODE = 2;
   public static final int DATA_AUTO_EJECT = 3;
   public static final int DATA_AMOUNT = 4;
   public static final int DATA_NET_EXTRACT = 5;
   public static final int DATA_NET_INSERT = 6;
   public static final int DATA_NET_ENERGY = 7;
   public static final int DATA_NET_ID = 8;
   public static final int DATA_RUNNING = 9;
   public static final int DATA_RECIPE_COUNT = 10;
   public static final int DATA_SELECTED = 11;
   private static final int DATA_ENERGY_HIGH = 12;
   private static final int DATA_NET_ID_HIGH = 13;
   public static final int DATA_COUNT = 14;
   private int workMode = 0;
   private boolean autoEject = false;
   private int craftAmount = 0;
   private boolean netExtract = false;
   private boolean netInsert = false;
   private boolean netEnergy = false;
   private int netId = -1;
   private final int[] sideConfig = new int[6];
   private int selected = 0;
   private boolean running = false;
   private boolean netPoweredCache = false;
   private int syncedEnergy = 0;
   private int syncedRecipeCount = 0;
   private int syncedSelected = 0;
   private AssemblerRecipes recipeIndex;
   private List<AssemblerRecipes.Entry> candidates = List.of();
   private AssemblerRecipes.Entry recipe;
   private ResourceLocation selectedId;
   private boolean recipeDirty = true;
   private boolean presentationDirty;
   private boolean missingLocal;
   private long networkRetryAt;
   private UUID netBinder;
   private MolecularAssemblerBlockEntity.NetworkPlan netPlan;
   private MolecularAssemblerBlockEntity.Workspace workspace;
   private final List<ItemStack> pending = new ArrayList<>();
   private static final int OUTPUT_RETRY = 5;
   private static final int DISPLAY_HOLD = 6;
   private static final Direction[] SIDES = Direction.values();
   private long outputRetryAt;
   private long networkOutputAt;
   private long targetRefreshAt;
   private long netPowerAt;
   private long craftAt;
   private long activeUntil;
   private boolean ticking;
   private boolean tickChanged;
   @Nullable
   private AssemblerOutputRouter outputRouter;
   private ItemStack ghostPattern = ItemStack.f_41583_;
   private final ItemStack[] ghosts = new ItemStack[9];
   private ItemStack displayStack = ItemStack.f_41583_;
   private final MolecularAssemblerBlockEntity.MachineItems items = new MolecularAssemblerBlockEntity.MachineItems(28);
   private final EnergyStorage energy = new EnergyStorage(360000, 20000, 20000, 0) {
      public int receiveEnergy(int maxReceive, boolean simulate) {
         if (maxReceive <= 0) {
            return 0;
         } else {
            int r = super.receiveEnergy(maxReceive, simulate);
            if (r > 0 && !simulate) {
               MolecularAssemblerBlockEntity.this.m_6596_();
            }

            return r;
         }
      }

      public int extractEnergy(int maxExtract, boolean simulate) {
         if (maxExtract <= 0) {
            return 0;
         } else {
            int r = super.extractEnergy(maxExtract, simulate);
            if (r > 0 && !simulate) {
               MolecularAssemblerBlockEntity.this.m_6596_();
            }

            return r;
         }
      }
   };
   private final IItemHandler externalItems = new IItemHandler() {
      public int getSlots() {
         return 28;
      }

      @NotNull
      public ItemStack getStackInSlot(int slot) {
         return MolecularAssemblerBlockEntity.this.items.getStackInSlot(slot < 27 ? 0 + slot : 27 + (slot - 27));
      }

      @NotNull
      public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
         return slot >= 0 && slot < 27 ? MolecularAssemblerBlockEntity.this.items.insertItem(0 + slot, stack, simulate) : stack;
      }

      @NotNull
      public ItemStack extractItem(int slot, int amount, boolean simulate) {
         return slot == 27 && amount > 0 ? MolecularAssemblerBlockEntity.this.items.extractItem(27 + (slot - 27), amount, simulate) : ItemStack.f_41583_;
      }

      public int getSlotLimit(int slot) {
         return 64;
      }

      public boolean isItemValid(int slot, @NotNull ItemStack stack) {
         return slot >= 0 && slot < 27;
      }
   };
   private LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> this.externalItems);
   private LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> this.energy);
   private final IItemHandler[] sideHandlers = new IItemHandler[6];
   private final LazyOptional<IItemHandler>[] sideCaps = new LazyOptional[6];
   private final ContainerData data = new ContainerData() {
      public int m_6413_(int index) {
         return MolecularAssemblerBlockEntity.this.getDataValue(index);
      }

      public void m_8050_(int index, int value) {
         MolecularAssemblerBlockEntity.this.setDataValue(index, value);
      }

      public int m_6499_() {
         return 14;
      }
   };
   private final List<LazyOptional<IItemHandler>> outputTargets = new ArrayList<>(6);

   public MolecularAssemblerBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.MOLECULAR_ASSEMBLER.get(), pos, state);

      for (int i = 0; i < this.ghosts.length; i++) {
         this.ghosts[i] = ItemStack.f_41583_;
      }

      for (int i = 0; i < this.sideConfig.length; i++) {
         this.sideConfig[i] = 3;
      }

      for (int i = 0; i < 6; i++) {
         int idx = i;
         this.sideHandlers[i] = new MolecularAssemblerBlockEntity.SideHandler(idx);
         this.sideCaps[i] = LazyOptional.of(() -> this.sideHandlers[idx]);
      }
   }

   public int getSideConfig(int side) {
      return side >= 0 && side < 6 ? Math.floorMod(this.sideConfig[side], 4) : 3;
   }

   public void cycleSideConfig(int side) {
      if (side >= 0 && side < 6) {
         this.sideConfig[side] = (this.sideConfig[side] + 1) % 4;
         this.m_6596_();
         this.neighboursChanged();
         this.presentationDirty = true;
      }
   }

   public static void tick(Level level, BlockPos pos, BlockState state, MolecularAssemblerBlockEntity be) {
      if (level instanceof ServerLevel server) {
         be.ticking = true;

         try {
            be.serverTick(server);
         } finally {
            be.ticking = false;
            if (be.tickChanged) {
               be.tickChanged = false;
               be.m_6596_();
            }
         }
      }
   }

   public void m_6596_() {
      if (this.ticking) {
         this.tickChanged = true;
      } else {
         super.m_6596_();
      }
   }

   private void serverTick(ServerLevel server) {
      this.refreshRecipes();
      long now = server.m_46467_();
      boolean localPower = this.energy.getEnergyStored() > 0;
      boolean checkPower = this.netEnergy && !localPower && now >= this.netPowerAt;
      boolean mayCraft = this.recipe != null && this.workEnabled();
      boolean canAttempt = mayCraft && now >= this.craftAt && now >= this.networkRetryAt && (!this.missingLocal || this.netExtract);
      boolean queuedNetworkOutput = this.networkOutputAt != 0L;
      boolean drain = queuedNetworkOutput
         ? now >= this.networkOutputAt || !mayCraft
         : now >= this.outputRetryAt && (!this.pending.isEmpty() || this.autoEject && !this.items.getStackInSlot(27).m_41619_());
      boolean bound = this.netId >= 0 && BeyondIntegration.isLoaded();
      boolean craftNeedsNetwork = canAttempt
         && (this.pending.isEmpty() || queuedNetworkOutput || drain)
         && (this.netInsert || this.netEnergy || this.netExtract);
      BeyondIntegration.Network net = !bound || !checkPower && (!drain || !this.netInsert) && !craftNeedsNetwork
         ? null
         : BeyondIntegration.open(this.netId, this.netBinder);
      if (checkPower) {
         this.netPoweredCache = net != null && net.energy() > 0L;
         this.netPowerAt = now + 20L;
      }

      BlockState state = this.m_58900_();
      boolean lit = localPower || this.netEnergy && this.netPoweredCache;
      if ((Boolean)state.m_61143_(MolecularAssemblerBlock.LIT) != lit) {
         server.m_7731_(this.f_58858_, (BlockState)state.m_61124_(MolecularAssemblerBlock.LIT, lit), 2);
      }

      BeyondIntegration.Network destination = this.netInsert ? net : null;
      boolean batchNetworkOutput = destination != null && this.recipe != null && this.recipe.batchable();
      boolean merge = queuedNetworkOutput && canAttempt && batchNetworkOutput;
      if (!merge && (drain || queuedNetworkOutput && canAttempt)) {
         this.flushOutputs(server, destination, now);
      }

      boolean crafted = canAttempt
         && (this.pending.isEmpty() || merge)
         && (!bound || net != null || !this.netExtract && !this.netEnergy)
         && this.craft(server, net, now);
      if (bound && craftNeedsNetwork && net == null) {
         this.networkRetryAt = now + 10L;
      }

      if (crafted) {
         this.craftAt = now + 2L;
         this.activeUntil = now + 6L;
         if (!merge && batchNetworkOutput) {
            int batches = this.craftAmount == 0 ? 4 : 2;
            this.networkOutputAt = now + (batches - 1) * 2L;
         } else if (!merge && now >= this.outputRetryAt) {
            this.flushOutputs(server, destination, now);
         }
      }

      if (merge && (drain || !crafted)) {
         this.flushOutputs(server, destination, now);
      }

      boolean active = mayCraft && now < this.activeUntil;
      if (this.running != active) {
         this.running = active;
         this.presentationDirty = true;
      }

      if (this.presentationDirty) {
         this.presentationDirty = false;
         BlockState current = this.m_58900_();
         server.m_7260_(this.f_58858_, current, current, 2);
      }
   }

   public int energyPerTick() {
      return this.craftAmount == 1 ? 6400 : 100;
   }

   private boolean workEnabled() {
      return switch (this.workMode) {
         case 1 -> false;
         case 2 -> this.f_58857_.m_276867_(this.f_58858_);
         case 3 -> !this.f_58857_.m_276867_(this.f_58858_);
         default -> true;
      };
   }

   private void refreshRecipes() {
      if (this.f_58857_ != null && !this.f_58857_.f_46443_ && (!this.ghostPattern.m_41619_() || this.recipeDirty)) {
         AssemblerRecipes index = this.ghostPattern.m_41619_() ? null : AssemblerRecipes.get(this.f_58857_);
         if (this.recipeDirty || index != this.recipeIndex) {
            this.recipeIndex = index;
            this.recipeDirty = false;
            this.candidates = index == null ? List.of() : index.candidates(this.ghostPattern);
            if (this.selectedId != null) {
               this.selected = 0;

               for (int i = 0; i < this.candidates.size(); i++) {
                  if (this.selectedId.equals(this.candidates.get(i).id())) {
                     this.selected = i;
                     break;
                  }
               }
            }

            if (this.selected < 0 || this.selected >= this.candidates.size()) {
               this.selected = 0;
            }

            this.useRecipe(this.candidates.isEmpty() ? null : this.candidates.get(this.selected));
         }
      }
   }

   private void useRecipe(@Nullable AssemblerRecipes.Entry entry) {
      if (this.networkOutputAt != 0L) {
         this.outputRetryAt = this.networkOutputAt = 0L;
      }

      this.recipe = entry;
      this.selectedId = entry == null ? null : entry.id();
      this.netPlan = null;
      this.missingLocal = false;
      this.networkRetryAt = 0L;
      this.displayStack = entry == null ? ItemStack.f_41583_ : entry.result().m_41777_();
      Arrays.fill(this.ghosts, ItemStack.f_41583_);
      if (entry != null) {
         for (int i = 0; i < entry.inputs().length; i++) {
            ItemStack[] samples = entry.inputs()[i].m_43908_();
            if (samples.length != 0) {
               int slot = entry.recipe().m_6671_() == RecipeType.f_44112_
                  ? 4
                  : (entry.recipe().m_6671_() == RecipeType.f_44113_ ? 3 + i : entry.positions()[i]);
               this.ghosts[slot] = samples[0].m_255036_(1);
            }
         }
      }

      this.presentationDirty = true;
   }

   public void setGhostPattern(ItemStack stack) {
      if (this.f_58857_ != null && !this.f_58857_.f_46443_) {
         ItemStack next = ItemStack.f_41583_;
         if (!stack.m_41619_()) {
            List<AssemblerRecipes.Entry> matches = AssemblerRecipes.get(this.f_58857_).candidates(stack);
            if (matches.isEmpty()) {
               return;
            }

            next = matches.get(0).result();
         }

         if (!ItemStack.m_150942_(next, this.ghostPattern)) {
            this.ghostPattern = next.m_255036_(1);
            this.selected = 0;
            this.selectedId = null;
            this.recipeDirty = true;
            this.m_6596_();
         }
      }
   }

   public void selectRecipe(ResourceLocation id) {
      if (this.f_58857_ != null && !this.f_58857_.f_46443_) {
         AssemblerRecipes.Entry entry = AssemblerRecipes.get(this.f_58857_).byId(id);
         if (entry != null && (!id.equals(this.selectedId) || !sameStack(this.ghostPattern, entry.result()))) {
            this.ghostPattern = entry.result().m_255036_(1);
            this.selectedId = id;
            this.recipeDirty = true;
            this.m_6596_();
         }
      }
   }

   public ItemStack getGhostPattern() {
      return this.ghostPattern;
   }

   public void selectRecipe(int index) {
      this.refreshRecipes();
      if (index != this.selected && index >= 0 && index < this.candidates.size()) {
         this.selected = index;
         this.useRecipe(this.candidates.get(index));
         this.m_6596_();
      }
   }

   private boolean craft(ServerLevel server, @Nullable BeyondIntegration.Network net, long now) {
      AssemblerRecipes.Entry entry = this.recipe;
      boolean fromNetwork = this.netExtract && net != null;
      if (!fromNetwork && this.missingLocal) {
         return false;
      } else {
         BeyondIntegration.Network destination = this.netInsert ? net : null;
         if (destination == null && this.autoEject) {
            this.refreshTargets(server, now);
         }

         boolean internalOnly = destination == null && this.outputTargets.isEmpty();
         if (internalOnly && entry.batchable() && entry.recipe().m_6671_() != RecipeType.f_44113_ && this.outputSpace(entry.result()) == 0) {
            return false;
         } else {
            BeyondIntegration.Network power = this.netEnergy ? net : null;
            int cost = this.energyPerTick();
            if ((power == null ? this.energy.getEnergyStored() : power.energy()) < cost) {
               if (power != null) {
                  this.networkRetryAt = now + 10L;
               }

               return false;
            } else {
               int limit = !entry.batchable() ? 1 : (this.craftAmount == 1 ? 192 : 3);
               if (this.workspace == null) {
                  this.workspace = new MolecularAssemblerBlockEntity.Workspace();
               }

               MolecularAssemblerBlockEntity.Workspace work = this.workspace;
               int times = fromNetwork ? this.planNetwork(entry, net, limit) : work.planLocal(entry, limit);
               if (times <= 0) {
                  if (fromNetwork) {
                     this.networkRetryAt = now + 10L;
                  } else {
                     this.missingLocal = true;
                  }

                  return false;
               } else {
                  boolean fixedResult = entry.hasFixedResult();
                  MolecularAssemblerBlockEntity.NetworkPlan plan = fromNetwork ? this.netPlan : null;
                  Container grid = fixedResult ? null : work.fillGrid(entry, plan, false);
                  ItemStack result = fixedResult ? entry.result() : AssemblerRecipes.assemble(entry, grid, server);
                  if (!validResult(entry, result)) {
                     if (fromNetwork) {
                        this.netPlan = null;
                        this.networkRetryAt = now + 10L;
                     } else {
                        this.craftAt = now + 5L;
                     }

                     return false;
                  } else {
                     if (internalOnly) {
                        int space = this.outputSpace(result);
                        if (space == 0) {
                           return false;
                        }

                        times = Math.min(times, (space + result.m_41613_() - 1) / result.m_41613_());
                     }

                     List<ItemStack> remainders = fixedResult ? work.fixedRemainders(entry, plan) : AssemblerRecipes.remainders(entry.recipe(), grid);
                     if (validRemainderCounts(remainders) && (fixedResult || remainders.size() == grid.m_6643_())) {
                        if (fromNetwork) {
                           times = this.withdrawNetwork(net, plan, work, times);
                           if (times > 0 && !entry.batchable()) {
                              work.fillGrid(entry, plan, true);
                              result = AssemblerRecipes.assemble(entry, grid, server);
                              remainders = AssemblerRecipes.remainders(entry.recipe(), grid);
                              if (!validResult(entry, result) || !validRemainders(remainders, grid)) {
                                 times = 0;
                              }
                           }
                        }

                        int paid = times == 0 ? 0 : (power == null ? this.energy.extractEnergy(cost, false) : power.extractEnergy(cost, false));
                        boolean commit = times > 0 && paid == cost;
                        if (!commit && paid > 0) {
                           if (power != null) {
                              power.refundEnergy(paid);
                           } else {
                              this.energy.receiveEnergy(paid, false);
                           }
                        }

                        if (fromNetwork) {
                           this.refundNetwork(net, plan, work, commit ? times : 0);
                           if (!commit) {
                              this.netPlan = null;
                              this.networkRetryAt = now + 10L;
                           }
                        } else if (commit) {
                           this.items.consume(work.used, times);
                        }

                        if (!commit) {
                           return false;
                        } else {
                           this.enqueue(result, result.m_41613_() * times);

                           for (int i = 0; i < remainders.size(); i++) {
                              ItemStack stack = remainders.get(i);
                              if (!stack.m_41619_()) {
                                 this.enqueue(stack, stack.m_41613_() * times);
                              }
                           }

                           this.m_6596_();
                           return true;
                        }
                     } else {
                        return false;
                     }
                  }
               }
            }
         }
      }
   }

   private int outputSpace(ItemStack stack) {
      int limit = Math.min(this.items.getSlotLimit(27), stack.m_41741_());
      ItemStack stored = this.items.getStackInSlot(27);
      if (stored.m_41619_()) {
         return limit;
      } else {
         return !ItemStack.m_150942_(stored, stack) ? 0 : Math.max(0, limit - stored.m_41613_());
      }
   }

   private static boolean validResult(AssemblerRecipes.Entry entry, ItemStack result) {
      return !result.m_41619_() && result.m_41720_() == entry.result().m_41720_() && result.m_41613_() <= 64;
   }

   private static boolean validRemainders(List<ItemStack> remainders, Container grid) {
      return remainders.size() == grid.m_6643_() && validRemainderCounts(remainders);
   }

   private static boolean validRemainderCounts(List<ItemStack> remainders) {
      for (int i = 0; i < remainders.size(); i++) {
         if (remainders.get(i).m_41613_() > 64) {
            return false;
         }
      }

      return true;
   }

   private int planNetwork(AssemblerRecipes.Entry entry, BeyondIntegration.Network net, int limit) {
      if (this.netPlan != null) {
         int possible = this.netPlan.available(net, limit);
         if (possible > 0) {
            return possible;
         }

         this.netPlan = null;
      }

      MolecularAssemblerBlockEntity.NetworkInputs inputs = new MolecularAssemblerBlockEntity.NetworkInputs(net, entry.inputs());

      for (Ingredient ingredient : entry.inputs()) {
         for (ItemStack sample : ingredient.m_43908_()) {
            if (!sample.m_41619_()) {
               inputs.add(net.itemKey(sample), sample);
            }
         }
      }

      if (!inputs.assignAll()) {
         net.forEachItem(inputs::add);
         if (!inputs.assignAll()) {
            return 0;
         }
      }

      this.netPlan = MolecularAssemblerBlockEntity.NetworkPlan.create(inputs.assigned);
      int possible = this.netPlan.available(net, limit);
      if (possible == 0) {
         this.netPlan = null;
      }

      return possible;
   }

   private static boolean sameStack(ItemStack first, ItemStack second) {
      return ItemStack.m_150942_(first, second);
   }

   private int withdrawNetwork(
      BeyondIntegration.Network net, MolecularAssemblerBlockEntity.NetworkPlan plan, MolecularAssemblerBlockEntity.Workspace work, int times
   ) {
      for (int i = 0; i < plan.keys().length && times > 0; i++) {
         int per = plan.perCraft()[i];
         if (per != 0) {
            BeyondIntegration.ItemKey requested = plan.keys()[i];
            BeyondIntegration.Withdrawal got = net.extract(requested, per * times);
            work.withdrawn[i] = got;
            if (got == null || !requested.equals(got.key())) {
               return 0;
            }

            times = Math.min(times, got.count() / per);
         }
      }

      return times;
   }

   private void refundNetwork(
      BeyondIntegration.Network net, MolecularAssemblerBlockEntity.NetworkPlan plan, MolecularAssemblerBlockEntity.Workspace work, int times
   ) {
      for (int i = 0; i < plan.keys().length; i++) {
         BeyondIntegration.Withdrawal got = work.withdrawn[i];
         if (got != null) {
            work.withdrawn[i] = null;
            int extra = got.count() - plan.perCraft()[i] * times;
            if (extra > 0) {
               ItemStack stack = got.copyStack(1);
               int left = extra - net.insert(stack, extra);
               if (left > 0) {
                  this.enqueue(stack, left);
                  this.m_6596_();
               }
            }
         }
      }
   }

   private void enqueue(ItemStack stack, int count) {
      if (count > 0) {
         for (ItemStack out : this.pending) {
            if (sameStack(out, stack)) {
               out.m_41769_(count);
               return;
            }
         }

         this.pending.add(stack.m_255036_(count));
      }
   }

   private void flushOutputs(ServerLevel server, @Nullable BeyondIntegration.Network net, long now) {
      this.networkOutputAt = 0L;
      if (this.autoEject) {
         this.refreshTargets(server, now);
         if (!this.outputTargets.isEmpty()) {
            if (this.outputRouter == null) {
               this.outputRouter = new AssemblerOutputRouter();
            }

            this.outputRouter.beginFlush(now);
         }

         ItemStack out = this.items.getStackInSlot(27);
         if (!out.m_41619_()) {
            int left = this.pushToTargets(out, out.m_41613_());
            if (left < out.m_41613_()) {
               this.items.setStackInSlot(27, out.m_255036_(left));
            }
         }
      }

      boolean changed = false;
      int i = 0;

      while (i < this.pending.size()) {
         ItemStack out = this.pending.get(i);
         int before = out.m_41613_();
         int left = net == null ? before : before - net.insert(out, before);
         if (left > 0 && this.autoEject) {
            left = this.pushToTargets(out, left);
         }

         if (left > 0 && this.outputSpace(out) > 0) {
            left = this.items.insertItem(27, out.m_255036_(left), false).m_41613_();
         }

         if (left != before) {
            changed = true;
         }

         if (left == 0) {
            this.pending.remove(i);
         } else {
            out.m_41764_(left);
            i++;
         }
      }

      if (changed) {
         this.m_6596_();
      }

      boolean hasNeighbour = this.autoEject && !this.outputTargets.isEmpty();
      boolean unfinished = !this.pending.isEmpty() || this.autoEject && !this.items.getStackInSlot(27).m_41619_();
      if (hasNeighbour && this.outputRouter.invalidTarget()) {
         this.targetRefreshAt = 0L;
      }

      this.outputRetryAt = unfinished ? now + (hasNeighbour && this.outputRouter.budgetHit() ? 1 : 5) : 0L;
   }

   public void neighboursChanged() {
      if (this.outputRouter != null) {
         this.outputRouter.changedTargets();
      }

      this.targetRefreshAt = 0L;
      this.outputRetryAt = 0L;
   }

   private void refreshTargets(ServerLevel server, long now) {
      if (now >= this.targetRefreshAt) {
         this.targetRefreshAt = now + 40L;
         int previous = this.outputTargets.size();
         int found = 0;
         boolean changed = false;

         for (Direction side : SIDES) {
            if ((this.sideConfig[side.ordinal()] & 1) != 0) {
               BlockPos pos = this.f_58858_.m_121945_(side);
               if (server.m_46805_(pos)) {
                  BlockEntity neighbour = server.m_7702_(pos);
                  if (neighbour != null) {
                     LazyOptional<IItemHandler> cap = neighbour.getCapability(ForgeCapabilities.ITEM_HANDLER, side.m_122424_());
                     if (cap.isPresent()) {
                        if (found >= previous) {
                           this.outputTargets.add(cap);
                           changed = true;
                        } else if (this.outputTargets.get(found) != cap) {
                           this.outputTargets.set(found, cap);
                           changed = true;
                        }

                        found++;
                     }
                  }
               }
            }
         }

         while (this.outputTargets.size() > found) {
            this.outputTargets.remove(this.outputTargets.size() - 1);
            changed = true;
         }

         if (changed && this.outputRouter != null) {
            this.outputRouter.changedTargets();
         }
      }
   }

   private int pushToTargets(ItemStack stack, int remaining) {
      return this.outputRouter == null ? remaining : this.outputRouter.push(this.outputTargets, stack, remaining);
   }

   private int getDataValue(int index) {
      return switch (index) {
         case 0 -> this.energy.getEnergyStored() & 65535;
         case 1 -> 360000;
         case 2 -> this.workMode;
         case 3 -> this.autoEject ? 1 : 0;
         case 4 -> this.craftAmount;
         case 5 -> this.netExtract ? 1 : 0;
         case 6 -> this.netInsert ? 1 : 0;
         case 7 -> this.netEnergy ? 1 : 0;
         case 8 -> this.netId & 65535;
         case 9 -> this.running ? 1 : 0;
         case 10 -> this.candidates.size();
         case 11 -> this.selected;
         case 12 -> this.energy.getEnergyStored() >>> 16;
         case 13 -> this.netId >>> 16;
         default -> 0;
      };
   }

   private void setDataValue(int index, int value) {
      switch (index) {
         case 0:
            this.syncedEnergy = this.syncedEnergy & -65536 | value & 65535;
         case 1:
         default:
            break;
         case 2:
            this.workMode = value;
            break;
         case 3:
            this.autoEject = value != 0;
            break;
         case 4:
            this.craftAmount = value;
            break;
         case 5:
            this.netExtract = value != 0;
            break;
         case 6:
            this.netInsert = value != 0;
            break;
         case 7:
            this.netEnergy = value != 0;
            break;
         case 8:
            this.netId = this.netId & -65536 | value & 65535;
            break;
         case 9:
            this.running = value != 0;
            break;
         case 10:
            this.syncedRecipeCount = value & 65535;
            break;
         case 11:
            this.syncedSelected = value & 65535;
            break;
         case 12:
            this.syncedEnergy = this.syncedEnergy & 65535 | value << 16;
            break;
         case 13:
            this.netId = this.netId & 65535 | value << 16;
      }
   }

   public void cycleWorkMode() {
      this.workMode = (this.workMode + 1) % 4;
      this.m_6596_();
   }

   public void toggleAutoEject() {
      this.autoEject = !this.autoEject;
      this.outputTargets.clear();
      this.neighboursChanged();
      this.m_6596_();
   }

   public void toggleCraftAmount() {
      this.craftAmount = this.craftAmount == 0 ? 1 : 0;
      if (this.networkOutputAt != 0L) {
         this.outputRetryAt = this.networkOutputAt = 0L;
      }

      this.m_6596_();
   }

   public void toggleNetExtract() {
      this.netExtract = !this.netExtract;
      this.networkChanged();
   }

   public void toggleNetInsert() {
      this.netInsert = !this.netInsert;
      this.networkChanged();
   }

   public void toggleNetEnergy() {
      this.netEnergy = !this.netEnergy;
      this.networkChanged();
   }

   public void bindPrimaryNet(Player player) {
      int id = BeyondIntegration.getPrimaryNetId(player);
      UUID binder = id < 0 ? null : player.m_20148_();
      if (id != this.netId || !Objects.equals(binder, this.netBinder)) {
         this.netId = id;
         this.netBinder = binder;
         this.networkChanged();
      }
   }

   public boolean canConfigure(Player player) {
      return this.netId < 0
         || this.netBinder == null
         || !BeyondIntegration.isLoaded()
         || this.netBinder.equals(player.m_20148_())
         || BeyondIntegration.open(this.netId, player.m_20148_()) != null;
   }

   private void networkChanged() {
      this.netPlan = null;
      this.networkRetryAt = 0L;
      this.netPowerAt = 0L;
      this.outputRetryAt = this.networkOutputAt = 0L;
      this.netPoweredCache = false;
      this.missingLocal = false;
      this.m_6596_();
   }

   public int getNetId() {
      return this.netId;
   }

   @NotNull
   public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
      if (cap == ForgeCapabilities.ITEM_HANDLER) {
         if (side == null) {
            return this.itemCap.cast();
         } else {
            return this.sideConfig[side.ordinal()] == 0 ? LazyOptional.empty() : this.sideCaps[side.ordinal()].cast();
         }
      } else {
         return cap == ForgeCapabilities.ENERGY ? this.energyCap.cast() : super.getCapability(cap, side);
      }
   }

   public void invalidateCaps() {
      super.invalidateCaps();
      this.itemCap.invalidate();
      this.energyCap.invalidate();

      for (LazyOptional<IItemHandler> cap : this.sideCaps) {
         if (cap != null) {
            cap.invalidate();
         }
      }
   }

   public void reviveCaps() {
      super.reviveCaps();
      this.itemCap = LazyOptional.of(() -> this.externalItems);
      this.energyCap = LazyOptional.of(() -> this.energy);

      for (int i = 0; i < this.sideCaps.length; i++) {
         int side = i;
         this.sideCaps[i] = LazyOptional.of(() -> this.sideHandlers[side]);
      }

      this.neighboursChanged();
   }

   public boolean m_6326_() {
      return true;
   }

   protected void m_183515_(CompoundTag tag) {
      super.m_183515_(tag);
      tag.m_128365_("Items", this.items.serializeNBT());
      tag.m_128365_("Energy", this.energy.serializeNBT());
      tag.m_128405_("WorkMode", this.workMode);
      tag.m_128379_("AutoEject", this.autoEject);
      tag.m_128405_("CraftAmount", this.craftAmount);
      tag.m_128379_("NetExtract", this.netExtract);
      tag.m_128379_("NetInsert", this.netInsert);
      tag.m_128379_("NetEnergy", this.netEnergy);
      tag.m_128405_("NetId", this.netId);
      if (this.netBinder != null) {
         tag.m_128362_("NetBinder", this.netBinder);
      }

      tag.m_128405_("Selected", this.selected);
      if (this.selectedId != null) {
         tag.m_128359_("SelectedRecipe", this.selectedId.toString());
      }

      tag.m_128385_("SideConfig", this.sideConfig);
      tag.m_128365_("GhostPattern", this.ghostPattern.m_41739_(new CompoundTag()));
      ListTag outputs = new ListTag();

      for (ItemStack stack : this.pending) {
         CompoundTag entry = stack.m_255036_(1).m_41739_(new CompoundTag());
         entry.m_128405_("Amount", stack.m_41613_());
         outputs.add(entry);
      }

      tag.m_128365_("Pending", outputs);
   }

   public void m_142466_(CompoundTag tag) {
      super.m_142466_(tag);
      this.items.deserializeNBT(tag.m_128469_("Items"));
      this.energy.deserializeNBT(IntTag.m_128679_(Math.max(0, Math.min(360000, tag.m_128451_("Energy")))));
      this.workMode = tag.m_128451_("WorkMode") & 3;
      this.autoEject = tag.m_128471_("AutoEject");
      this.craftAmount = tag.m_128451_("CraftAmount") == 1 ? 1 : 0;
      this.netExtract = tag.m_128471_("NetExtract");
      this.netInsert = tag.m_128471_("NetInsert");
      this.netEnergy = tag.m_128471_("NetEnergy");
      this.netId = tag.m_128425_("NetId", 3) ? tag.m_128451_("NetId") : -1;
      this.netBinder = tag.m_128403_("NetBinder") ? tag.m_128342_("NetBinder") : null;
      this.selected = Math.max(0, tag.m_128451_("Selected"));
      this.selectedId = tag.m_128425_("SelectedRecipe", 8) ? ResourceLocation.m_135820_(tag.m_128461_("SelectedRecipe")) : null;
      this.readSides(tag);
      this.ghostPattern = ItemStack.m_41712_(tag.m_128469_("GhostPattern")).m_255036_(1);
      this.pending.clear();
      this.networkOutputAt = 0L;
      if (tag.m_128425_("Pending", 9)) {
         ListTag list = tag.m_128437_("Pending", 10);

         for (int i = 0; i < list.size() && i < 29; i++) {
            CompoundTag entry = list.m_128728_(i);
            this.loadPending(ItemStack.m_41712_(entry), entry.m_128451_("Amount"));
         }
      } else {
         this.loadPending(ItemStack.m_41712_(tag.m_128469_("PendingOut")), tag.m_128451_("PendingOutCount"));
      }

      this.running = false;
      this.recipeDirty = true;
      this.netPoweredCache = false;
      this.netPowerAt = this.craftAt = this.networkRetryAt = this.activeUntil = 0L;
      this.neighboursChanged();
   }

   private void loadPending(ItemStack stack, int count) {
      if (!stack.m_41619_() && count > 0) {
         this.enqueue(stack, Math.min(count, 245760));
      }
   }

   private void readSides(CompoundTag tag) {
      int[] sides = tag.m_128465_("SideConfig");

      for (int i = 0; i < this.sideConfig.length; i++) {
         this.sideConfig[i] = i < sides.length ? sides[i] & 3 : 3;
      }
   }

   public CompoundTag m_5995_() {
      CompoundTag tag = new CompoundTag();
      tag.m_128385_("SideConfig", this.sideConfig);
      tag.m_128379_("Running", this.running);
      tag.m_128365_("Display", this.displayStack.m_41739_(new CompoundTag()));
      ListTag list = new ListTag();

      for (ItemStack ghost : this.ghosts) {
         list.add(ghost.m_41739_(new CompoundTag()));
      }

      tag.m_128365_("Ghosts", list);
      return tag;
   }

   public void handleUpdateTag(CompoundTag tag) {
      this.readSides(tag);
      this.running = tag.m_128471_("Running");
      this.displayStack = ItemStack.m_41712_(tag.m_128469_("Display"));
      ListTag list = tag.m_128437_("Ghosts", 10);

      for (int i = 0; i < this.ghosts.length; i++) {
         this.ghosts[i] = ItemStack.m_41712_(list.m_128728_(i));
      }
   }

   public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
      if (packet.m_131708_() != null) {
         this.handleUpdateTag(packet.m_131708_());
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.m_195640_(this);
   }

   public Component m_5446_() {
      return Component.m_237115_("container.sakura_mod.molecular_assembler");
   }

   @Nullable
   public AbstractContainerMenu m_7208_(int id, Inventory inventory, Player player) {
      this.refreshRecipes();
      return new MolecularAssemblerMenu(id, inventory, this);
   }

   public boolean dumpToPlayer(Player player) {
      if (!(this.f_58857_ instanceof ServerLevel)) {
         return false;
      } else {
         boolean any = !this.pending.isEmpty();

         for (int i = 0; i < 28; i++) {
            ItemStack stack = this.items.getStackInSlot(i);
            if (!stack.m_41619_()) {
               any = true;
               this.items.setStackInSlot(i, ItemStack.f_41583_);
               giveOrDrop(player, stack);
            }
         }

         for (ItemStack stack : this.pending) {
            int left = stack.m_41613_();

            while (left > 0) {
               int moved = Math.min(Math.max(1, stack.m_41741_()), left);
               giveOrDrop(player, stack.m_255036_(moved));
               left -= moved;
            }
         }

         this.pending.clear();
         this.networkOutputAt = 0L;
         if (any) {
            this.m_6596_();
         }

         return any;
      }
   }

   private static void giveOrDrop(Player player, ItemStack stack) {
      player.m_150109_().m_150079_(stack);
   }

   @Override
   public void drops() {
      if (this.f_58857_ instanceof ServerLevel server) {
         for (int var4 = 0; var4 < 28; var4++) {
            ItemStack stack = this.items.getStackInSlot(var4);
            if (!stack.m_41619_()) {
               this.items.setStackInSlot(var4, ItemStack.f_41583_);
               this.drop(server, stack);
            }
         }

         for (ItemStack stack : this.pending) {
            this.drop(server, stack);
         }

         this.pending.clear();
         this.networkOutputAt = 0L;
      }
   }

   private void drop(ServerLevel server, ItemStack stack) {
      int left = stack.m_41613_();

      while (left > 0) {
         int n = Math.min(left, Math.max(1, stack.m_41741_()));
         Containers.m_18992_(server, this.f_58858_.m_123341_(), this.f_58858_.m_123342_(), this.f_58858_.m_123343_(), stack.m_255036_(n));
         left -= n;
      }
   }

   public ItemStackHandler getItems() {
      return this.items;
   }

   public IItemHandler getExternalItems() {
      return this.externalItems;
   }

   public EnergyStorage getEnergy() {
      return this.energy;
   }

   public ContainerData getContainerData() {
      return this.data;
   }

   public int getWorkMode() {
      return this.workMode;
   }

   public int getCraftAmount() {
      return this.craftAmount;
   }

   public boolean isAutoEject() {
      return this.autoEject;
   }

   public boolean isNetExtract() {
      return this.netExtract;
   }

   public boolean isNetInsert() {
      return this.netInsert;
   }

   public boolean isNetEnergy() {
      return this.netEnergy;
   }

   public boolean isRunning() {
      return this.running;
   }

   public boolean isClientRunning() {
      return this.running;
   }

   public ItemStack getDisplayStack() {
      return this.displayStack;
   }

   public ItemStack getGhost(int index) {
      return index >= 0 && index < this.ghosts.length ? this.ghosts[index] : ItemStack.f_41583_;
   }

   public int getSyncedEnergy() {
      return Math.max(0, this.syncedEnergy);
   }

   public int getRecipeCount() {
      return this.syncedRecipeCount;
   }

   public int getSelected() {
      return this.syncedSelected;
   }

   private final class MachineItems extends ItemStackHandler {
      MachineItems(int size) {
         super(size);
      }

      protected void onContentsChanged(int slot) {
         if (slot < 27) {
            MolecularAssemblerBlockEntity.this.missingLocal = false;
         } else {
            MolecularAssemblerBlockEntity.this.outputRetryAt = 0L;
         }

         MolecularAssemblerBlockEntity.this.m_6596_();
      }

      public void deserializeNBT(CompoundTag tag) {
         this.setSize(28);
         ListTag list = tag.m_128437_("Items", 10);

         for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.m_128728_(i);
            int slot = entry.m_128451_("Slot");
            if (slot >= 0 && slot < 28) {
               this.stacks.set(slot, ItemStack.m_41712_(entry));
            }
         }
      }

      void consume(int[] used, int times) {
         for (int slot = 0; slot < 27; slot++) {
            if (used[slot] != 0) {
               ItemStack stack = (ItemStack)this.stacks.get(slot);
               int left = stack.m_41613_() - used[slot] * times;
               this.stacks.set(slot, left == 0 ? ItemStack.f_41583_ : stack.m_255036_(left));
            }
         }
      }
   }

   private static final class NetworkInput {
      final BeyondIntegration.ItemKey key;
      final ItemStack sample;
      final int matches;
      final long available;
      int used;
      int visited;

      NetworkInput(BeyondIntegration.ItemKey key, ItemStack sample, int matches, long available) {
         this.key = key;
         this.sample = sample;
         this.matches = matches;
         this.available = available;
      }
   }

   private static final class NetworkInputs {
      final BeyondIntegration.Network network;
      final Ingredient[] ingredients;
      final MolecularAssemblerBlockEntity.NetworkInput[] assigned;
      final Map<BeyondIntegration.ItemKey, MolecularAssemblerBlockEntity.NetworkInput> options = new LinkedHashMap<>();

      NetworkInputs(BeyondIntegration.Network network, Ingredient[] ingredients) {
         this.network = network;
         this.ingredients = ingredients;
         this.assigned = new MolecularAssemblerBlockEntity.NetworkInput[ingredients.length];
      }

      void add(BeyondIntegration.ItemKey key, ItemStack sample) {
         if (!this.options.containsKey(key)) {
            int matches = 0;

            for (int i = 0; i < this.ingredients.length; i++) {
               if (this.ingredients[i].test(sample)) {
                  matches |= 1 << i;
               }
            }

            if (matches != 0) {
               long available = this.network.count(key);
               if (available > 0L) {
                  this.options.put(key, new MolecularAssemblerBlockEntity.NetworkInput(key, key.copyStack(1), matches, available));
               }
            }
         }
      }

      boolean assignAll() {
         Arrays.fill(this.assigned, null);

         for (MolecularAssemblerBlockEntity.NetworkInput option : this.options.values()) {
            option.used = 0;
            option.visited = 0;
         }

         for (int i = 0; i < this.ingredients.length; i++) {
            if (!this.assign(i, i + 1)) {
               return false;
            }
         }

         return true;
      }

      private boolean assign(int input, int pass) {
         int mask = 1 << input;

         for (MolecularAssemblerBlockEntity.NetworkInput option : this.options.values()) {
            if ((option.matches & mask) != 0 && option.visited != pass && option.used < option.available) {
               option.used++;
               this.assigned[input] = option;
               return true;
            }
         }

         for (MolecularAssemblerBlockEntity.NetworkInput optionx : this.options.values()) {
            if ((optionx.matches & mask) != 0 && optionx.visited != pass) {
               optionx.visited = pass;

               for (int other = 0; other < this.assigned.length; other++) {
                  if (this.assigned[other] == optionx && this.assign(other, pass)) {
                     this.assigned[input] = optionx;
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private record NetworkPlan(ItemStack[] samples, int[] group, BeyondIntegration.ItemKey[] keys, int[] perCraft) {
      static MolecularAssemblerBlockEntity.NetworkPlan create(MolecularAssemblerBlockEntity.NetworkInput[] assigned) {
         ItemStack[] samples = new ItemStack[assigned.length];
         int[] group = new int[assigned.length];
         BeyondIntegration.ItemKey[] keys = new BeyondIntegration.ItemKey[assigned.length];
         int[] perCraft = new int[assigned.length];

         for (int i = 0; i < assigned.length; i++) {
            samples[i] = assigned[i].sample;
            int first = 0;

            while (first < i && assigned[first] != assigned[i]) {
               first++;
            }

            group[i] = first;
            if (perCraft[first]++ == 0) {
               keys[first] = assigned[i].key;
            }
         }

         return new MolecularAssemblerBlockEntity.NetworkPlan(samples, group, keys, perCraft);
      }

      int available(BeyondIntegration.Network net, int limit) {
         for (int i = 0; i < this.keys.length && limit > 0; i++) {
            if (this.keys[i] != null) {
               limit = (int)Math.min((long)limit, net.count(this.keys[i]) / this.perCraft[i]);
            }
         }

         return limit;
      }
   }

   private final class SideHandler implements IItemHandler {
      private final int facing;

      SideHandler(int facing) {
         this.facing = facing;
      }

      private int mode() {
         return MolecularAssemblerBlockEntity.this.sideConfig[this.facing];
      }

      public int getSlots() {
         return MolecularAssemblerBlockEntity.this.externalItems.getSlots();
      }

      @NotNull
      public ItemStack getStackInSlot(int slot) {
         return MolecularAssemblerBlockEntity.this.externalItems.getStackInSlot(slot);
      }

      @NotNull
      public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
         int m = this.mode();
         return m != 2 && m != 3 ? stack : MolecularAssemblerBlockEntity.this.externalItems.insertItem(slot, stack, simulate);
      }

      @NotNull
      public ItemStack extractItem(int slot, int amount, boolean simulate) {
         int m = this.mode();
         return m != 1 && m != 3 ? ItemStack.f_41583_ : MolecularAssemblerBlockEntity.this.externalItems.extractItem(slot, amount, simulate);
      }

      public int getSlotLimit(int slot) {
         return MolecularAssemblerBlockEntity.this.externalItems.getSlotLimit(slot);
      }

      public boolean isItemValid(int slot, @NotNull ItemStack stack) {
         return (this.mode() & 2) != 0 && MolecularAssemblerBlockEntity.this.externalItems.isItemValid(slot, stack);
      }
   }

   private final class Workspace {
      private Container grid;
      private RecipeType<?> gridType;
      private final List<ItemStack> remaining = new ArrayList<>(9);
      final int[] used = new int[27];
      final int[] available = new int[27];
      final int[] matches = new int[9];
      final Item[] matchedItems = new Item[27];
      final int[] matchedIngredients = new int[27];
      final int[] assigned = new int[9];
      final int[] order = new int[9];
      final BeyondIntegration.Withdrawal[] withdrawn = new BeyondIntegration.Withdrawal[9];
      AssemblerRecipes.Entry matchedRecipe;
      int visited;

      Container fillGrid(AssemblerRecipes.Entry entry, @Nullable MolecularAssemblerBlockEntity.NetworkPlan plan, boolean actual) {
         RecipeType<?> type = entry.recipe().m_6671_();
         if (this.gridType != type) {
            this.gridType = type;
            this.grid = (Container)(type == RecipeType.f_44113_
               ? new SimpleContainer(3)
               : (type == RecipeType.f_44112_ ? new SimpleContainer(1) : AssemblerRecipes.craftingGrid(AssemblerRecipes.emptyGrid())));
         }

         this.grid.m_6211_();

         for (int i = 0; i < entry.inputs().length; i++) {
            ItemStack unit = actual ? this.withdrawn[plan.group()[i]].copyStack(1) : this.sample(plan, i).m_255036_(1);
            this.grid.m_6836_(entry.positions()[i], unit);
         }

         return this.grid;
      }

      ItemStack sample(@Nullable MolecularAssemblerBlockEntity.NetworkPlan plan, int input) {
         return plan == null ? MolecularAssemblerBlockEntity.this.items.getStackInSlot(this.assigned[input]) : plan.samples()[input];
      }

      List<ItemStack> fixedRemainders(AssemblerRecipes.Entry entry, @Nullable MolecularAssemblerBlockEntity.NetworkPlan plan) {
         this.remaining.clear();

         for (int i = 0; i < entry.inputs().length; i++) {
            ItemStack sample = this.sample(plan, i);
            Item item = sample.m_41720_();
            if (item.getClass() != Item.class && item.getClass() != BlockItem.class || item.m_41470_()) {
               ItemStack unit = sample.m_255036_(1);
               if (unit.hasCraftingRemainingItem()) {
                  this.remaining.add(unit.getCraftingRemainingItem());
               }
            }
         }

         return this.remaining;
      }

      int planLocal(AssemblerRecipes.Entry entry, int limit) {
         if (this.matchedRecipe != entry) {
            this.matchedRecipe = entry;
            Arrays.fill(this.matchedItems, null);
         }

         int occupied = 0;

         for (int slot = 0; slot < 27; slot++) {
            int count = MolecularAssemblerBlockEntity.this.items.getStackInSlot(slot).m_41613_();
            this.available[slot] = count;
            if (count > 0) {
               occupied |= 1 << slot;
            }
         }

         if (occupied == 0) {
            return 0;
         } else {
            Arrays.fill(this.assigned, -1);
            Arrays.fill(this.used, 0);
            Arrays.fill(this.matches, 0);
            Ingredient[] inputs = entry.inputs();

            for (int slots = occupied; slots != 0; slots &= slots - 1) {
               int slotx = Integer.numberOfTrailingZeros(slots);
               ItemStack stack = MolecularAssemblerBlockEntity.this.items.getStackInSlot(slotx);
               int accepts;
               if (entry.itemOnlyIngredients() && this.matchedItems[slotx] == stack.m_41720_()) {
                  accepts = this.matchedIngredients[slotx];
               } else {
                  accepts = 0;

                  for (int i = 0; i < inputs.length; i++) {
                     if (inputs[i].test(stack)) {
                        accepts |= 1 << i;
                     }
                  }

                  if (entry.itemOnlyIngredients()) {
                     this.matchedItems[slotx] = stack.m_41720_();
                     this.matchedIngredients[slotx] = accepts;
                  }
               }

               while (accepts != 0) {
                  this.matches[Integer.numberOfTrailingZeros(accepts)] |= 1 << slotx;
                  accepts &= accepts - 1;
               }
            }

            int ix = 0;

            while (ix < inputs.length) {
               int mask = this.matches[ix];
               if (mask == 0) {
                  return 0;
               }

               int j;
               for (j = ix; j > 0 && Integer.bitCount(this.matches[this.order[j - 1]]) > Integer.bitCount(mask); j--) {
                  this.order[j] = this.order[j - 1];
               }

               this.order[j] = ix++;
            }

            for (int ixx = 0; ixx < inputs.length; ixx++) {
               this.visited = 0;
               if (!this.assign(this.order[ixx], inputs.length)) {
                  return 0;
               }
            }

            for (int slotx = 0; slotx < 27; slotx++) {
               if (this.used[slotx] != 0) {
                  limit = Math.min(limit, this.available[slotx] / this.used[slotx]);
               }
            }

            return limit;
         }
      }

      private boolean assign(int input, int size) {
         for (int slots = this.matches[input] & ~this.visited; slots != 0; slots &= slots - 1) {
            int slot = Integer.numberOfTrailingZeros(slots);
            if (this.used[slot] < this.available[slot]) {
               this.used[slot]++;
               this.assigned[input] = slot;
               return true;
            }
         }

         for (int slotsx = this.matches[input] & ~this.visited; slotsx != 0; slotsx = this.matches[input] & ~this.visited) {
            int slot = Integer.numberOfTrailingZeros(slotsx);
            this.visited |= 1 << slot;

            for (int other = 0; other < size; other++) {
               if (this.assigned[other] == slot && this.assign(other, size)) {
                  this.assigned[input] = slot;
                  return true;
               }
            }
         }

         return false;
      }
   }
}
