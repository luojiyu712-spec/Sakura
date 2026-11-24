package com.Luo_jiyv.SakuraMod.multiblock;

import com.Luo_jiyv.SakuraMod.block.ControllerBlock;
import com.Luo_jiyv.SakuraMod.block.ControllerState;
import com.Luo_jiyv.SakuraMod.block.ControllerType;
import com.Luo_jiyv.SakuraMod.blockentity.ControllerBlockEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.level.LevelEvent.Unload;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class ControllerStructure {
   public static final int MAX_SIZE = 7;
   private static final int TOPOLOGY_WORK_PER_TICK = 512;
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final Map<ServerLevel, ControllerStructure.WorldNetworks> WORLDS = new IdentityHashMap<>();

   private ControllerStructure() {
   }

   public static void register(ServerLevel level, ControllerBlockEntity member) {
      ControllerStructure.WorldNetworks world = WORLDS.computeIfAbsent(level, ControllerStructure.WorldNetworks::new);
      long pos = member.m_58899_().m_121878_();
      ControllerBlockEntity old = (ControllerBlockEntity)world.loaded.put(pos, member);
      if (old != member) {
         if (old != null) {
            world.invalidate(old.network());
         }

         world.changed(pos);
      }
   }

   public static void unregister(ServerLevel level, ControllerBlockEntity member) {
      ControllerStructure.WorldNetworks world = WORLDS.get(level);
      long pos = member.m_58899_().m_121878_();
      if (world != null && world.loaded.get(pos) == member) {
         world.loaded.remove(pos);
         world.invalidate(member.network());
         member.setNetwork(null);
         world.changed(pos);
      }
   }

   public static void reloadEnergy(ServerLevel level, ControllerBlockEntity member) {
      ControllerStructure.WorldNetworks world = WORLDS.get(level);
      long pos = member.m_58899_().m_121878_();
      if (world != null && world.loaded.get(pos) == member) {
         world.changed(pos);
      }
   }

   @SubscribeEvent
   public static void onLevelTick(LevelTickEvent event) {
      if (event.phase == Phase.END && event.level instanceof ServerLevel level) {
         ControllerStructure.WorldNetworks world = WORLDS.get(level);
         if (world != null) {
            world.tick();
         }
      }
   }

   @SubscribeEvent
   public static void onLevelUnload(Unload event) {
      if (event.getLevel() instanceof ServerLevel level) {
         ControllerStructure.WorldNetworks world = WORLDS.remove(level);
         if (world != null) {
            world.active = false;
         }
      }
   }

   private static int axes(int faces) {
      return faces & faces >>> 1 & 21;
   }

   private static ControllerType type(BlockPos pos, int faces) {
      int axes = axes(faces);
      if (Integer.bitCount(axes) > 1) {
         return (pos.m_123341_() + pos.m_123342_() + pos.m_123343_() & 1) == 0 ? ControllerType.INSIDE_A : ControllerType.INSIDE_B;
      } else {
         return switch (axes) {
            case 1 -> ControllerType.COLUMN_Y;
            case 4 -> ControllerType.COLUMN_Z;
            case 16 -> ControllerType.COLUMN_X;
            default -> ControllerType.BLOCK;
         };
      }
   }

   private static void setAppearance(ServerLevel level, ControllerBlockEntity member, ControllerType type, ControllerState status) {
      BlockPos pos = member.m_58899_();
      LevelChunk chunk = level.m_7726_().m_7131_(pos.m_123341_() >> 4, pos.m_123343_() >> 4);
      if (chunk != null) {
         BlockState before = chunk.m_8055_(pos);
         if (before.m_60734_() instanceof ControllerBlock) {
            BlockState after = (BlockState)((BlockState)before.m_61124_(ControllerBlock.TYPE, type)).m_61124_(ControllerBlock.STATE, status);
            if (after != before) {
               level.m_7731_(pos, after, 18);
            }
         }
      }
   }

   public static final class Network {
      private static final int CAPACITY = 18000000;
      private static final int PORTS_PER_PASS = 32;
      private final ControllerStructure.WorldNetworks world;
      private final boolean conflict;
      private final List<ControllerBlockEntity> members;
      private final List<ControllerStructure.Port> ports = new ArrayList<>();
      private final BitSet charged = new BitSet();
      private final BitSet hasSpace = new BitSet();
      private final BitSet receivers = new BitSet();
      private boolean ready;
      private boolean transferring;
      private long total;
      private int discoverCursor;
      private int outputCursor;
      private long nextOutput = Long.MAX_VALUE;
      private long discoverAt;

      Network(ControllerStructure.WorldNetworks world, boolean conflict, List<ControllerBlockEntity> members) {
         this.world = world;
         this.conflict = conflict;
         this.members = conflict ? List.of() : members;
      }

      void addEnergy(int index, int amount) {
         if (!this.conflict) {
            this.total += amount;
            this.updateSlot(index, amount);
         }
      }

      private void updateSlot(int index, int amount) {
         this.charged.set(index, amount > 0);
         this.hasSpace.set(index, amount < 18000000);
      }

      public boolean validated() {
         return this.ready && this.world.active;
      }

      public boolean available() {
         return this.validated() && !this.conflict && !this.transferring;
      }

      public long energy() {
         return this.total;
      }

      public long capacity() {
         return this.members.size() * 18000000L;
      }

      public int receive(int amount, boolean simulate) {
         if (this.available() && amount > 0) {
            int accepted = (int)Math.min((long)amount, this.capacity() - this.total);
            if (!simulate && accepted > 0) {
               this.move(accepted);
            }

            return accepted;
         } else {
            return 0;
         }
      }

      public int extract(int amount, boolean simulate) {
         if (this.available() && amount > 0) {
            int extracted = (int)Math.min((long)amount, this.total);
            if (!simulate && extracted > 0) {
               this.move(-extracted);
            }

            return extracted;
         } else {
            return 0;
         }
      }

      private void move(int delta) {
         boolean receiving = delta > 0;
         BitSet slots = receiving ? this.hasSpace : this.charged;
         int left = Math.abs(delta);

         for (int i = slots.nextSetBit(0); left > 0; i = slots.nextSetBit(i + 1)) {
            ControllerBlockEntity member = this.members.get(i);
            int old = member.localEnergy();
            int amount = Math.min(left, receiving ? 18000000 - old : old);
            int value = receiving ? old + amount : old - amount;
            member.setLocalEnergy(value);
            this.updateSlot(i, value);
            left -= amount;
         }

         long oldTotal = this.total;
         this.total += delta;
         if (this.ready && oldTotal == 0L != (this.total == 0L)) {
            this.world.visualChanges.add(this);
            if (this.total == 0L) {
               this.world.unschedule(this);
            } else {
               this.world.schedule(this, this.world.level.m_46467_() + 1L);
            }
         }
      }

      public boolean interact(IEnergyStorage item) {
         if (!this.available()) {
            return false;
         } else {
            this.transferring = true;

            try {
               int room = (int)Math.min(2147483647L, this.capacity() - this.total);
               if (room > 0 && item.canExtract()) {
                  int received = clamp(item.extractEnergy(room, false), room);
                  if (received > 0) {
                     this.move(received);
                     return true;
                  }
               }

               return this.ready && this.offer(item) > 0;
            } finally {
               this.transferring = false;
            }
         }
      }

      private static int clamp(int accepted, int offered) {
         return Math.max(0, Math.min(accepted, offered));
      }

      private int offer(IEnergyStorage receiver) {
         int offered = (int)Math.min(2147483647L, this.total);
         if (offered != 0 && receiver.canReceive() && this.ready) {
            int accepted = clamp(receiver.receiveEnergy(offered, false), offered);
            if (accepted > 0) {
               this.move(-accepted);
            }

            return accepted;
         } else {
            return 0;
         }
      }

      public void wakeOutput() {
         this.discoverAt = 0L;
         this.world.schedule(this, this.world.level.m_46467_() + 1L);
      }

      private void push(long now) {
         if (now >= this.discoverAt) {
            for (int stop = Math.min(this.ports.size(), this.discoverCursor + 32); this.ready && this.discoverCursor < stop; this.discoverCursor++) {
               this.receivers.set(this.discoverCursor, this.receiver(this.ports.get(this.discoverCursor)) != null);
            }

            if (this.discoverCursor == this.ports.size()) {
               this.discoverCursor = 0;
               this.discoverAt = now + (this.receivers.isEmpty() ? 20 : 80);
            }
         }

         boolean moved = false;
         int probes = 0;
         int start = this.outputCursor;

         for (int pass = 0; pass < 2; pass++) {
            int limit = pass == 0 ? this.ports.size() : start;

            for (int i = this.receivers.nextSetBit(pass == 0 ? start : 0);
               i >= 0 && i < limit && this.ready && probes < 32 && this.total > 0L;
               i = this.receivers.nextSetBit(i + 1)
            ) {
               ControllerStructure.Port port = this.ports.get(i);
               this.outputCursor = i + 1;
               probes++;
               if (this.world.level.m_7726_().m_143239_(ChunkPos.m_151388_(port.source))) {
                  IEnergyStorage receiver = this.receiver(port);
                  if (receiver == null) {
                     this.receivers.clear(i);
                  } else if (this.ready) {
                     this.transferring = true;

                     try {
                        moved |= this.offer(receiver) > 0;
                     } finally {
                        this.transferring = false;
                     }
                  }
               }
            }
         }

         if (this.ready && this.total != 0L) {
            long retry = this.receivers.isEmpty() ? this.discoverAt : now + (!moved && probes != 32 ? 8 : 1);
            this.world.schedule(this, Math.max(now + 1L, Math.min(retry, this.discoverAt)));
         }
      }

      @Nullable
      private IEnergyStorage receiver(ControllerStructure.Port port) {
         BlockPos pos = port.target;
         LevelChunk chunk = this.world.level.m_7726_().m_7131_(pos.m_123341_() >> 4, pos.m_123343_() >> 4);
         if (chunk == null) {
            return null;
         } else {
            BlockEntity target = chunk.m_7702_(pos);
            return target != null && !target.m_58901_() && !(target instanceof ControllerBlockEntity)
               ? (IEnergyStorage)target.getCapability(ForgeCapabilities.ENERGY, port.inputSide).resolve().orElse(null)
               : null;
         }
      }

      private void updateVisuals() {
         if (this.ready && !this.conflict) {
            ControllerState state = this.total > 0L ? ControllerState.ONLINE : ControllerState.OFFLINE;

            for (ControllerBlockEntity member : this.members) {
               if (!this.ready) {
                  break;
               }

               ControllerStructure.setAppearance(this.world.level, member, (ControllerType)member.m_58900_().m_61143_(ControllerBlock.TYPE), state);
            }
         }
      }
   }

   private record Port(BlockPos source, BlockPos target, Direction inputSide) {
   }

   private static final class Validation {
      final ControllerStructure.WorldNetworks world;
      final long origin;
      final LongOpenHashSet seen = new LongOpenHashSet();
      final List<ControllerBlockEntity> members = new ArrayList<>();
      int scanIndex;
      int applyIndex;
      int minX;
      int maxX;
      int minY;
      int maxY;
      int minZ;
      int maxZ;
      boolean conflict;
      boolean powered;
      @Nullable
      ControllerStructure.Network network;

      Validation(ControllerStructure.WorldNetworks world, ControllerBlockEntity first) {
         this.world = world;
         BlockPos pos = first.m_58899_();
         this.origin = pos.m_121878_();
         this.minX = this.maxX = pos.m_123341_();
         this.minY = this.maxY = pos.m_123342_();
         this.minZ = this.maxZ = pos.m_123343_();
         this.seen.add(this.origin);
         this.members.add(first);
      }

      boolean step() {
         if (this.network == null) {
            this.scan(this.members.get(this.scanIndex++));
            if (this.scanIndex == this.members.size()) {
               this.network = new ControllerStructure.Network(this.world, this.conflict, this.members);
            }

            return false;
         } else {
            ControllerBlockEntity member = this.members.get(this.applyIndex);
            BlockPos pos = member.m_58899_();
            int faces = 0;

            for (Direction dir : ControllerStructure.DIRECTIONS) {
               if (this.seen.contains(BlockPos.m_121915_(pos.m_121878_(), dir))) {
                  faces |= 1 << dir.ordinal();
               } else if (!this.conflict) {
                  this.network.ports.add(new ControllerStructure.Port(pos, pos.m_121945_(dir), dir.m_122424_()));
               }
            }

            member.setNetwork(this.network);
            this.network.addEnergy(this.applyIndex, member.localEnergy());
            this.applyIndex++;
            ControllerState status = this.conflict ? ControllerState.CONFLICTED : (this.powered ? ControllerState.ONLINE : ControllerState.OFFLINE);
            ControllerStructure.setAppearance(this.world.level, member, ControllerStructure.type(pos, faces), status);
            if (this.world.validation == this && this.applyIndex == this.members.size()) {
               this.network.ready = true;
               if (this.network.total > 0L) {
                  this.world.schedule(this.network, this.world.level.m_46467_() + 1L);
               }

               return true;
            } else {
               return false;
            }
         }
      }

      private void scan(ControllerBlockEntity member) {
         BlockPos pos = member.m_58899_();
         this.powered = this.powered | member.localEnergy() > 0;
         if (!this.conflict) {
            this.minX = Math.min(this.minX, pos.m_123341_());
            this.maxX = Math.max(this.maxX, pos.m_123341_());
            this.minY = Math.min(this.minY, pos.m_123342_());
            this.maxY = Math.max(this.maxY, pos.m_123342_());
            this.minZ = Math.min(this.minZ, pos.m_123343_());
            this.maxZ = Math.max(this.maxZ, pos.m_123343_());
         }

         int faces = 0;

         for (Direction dir : ControllerStructure.DIRECTIONS) {
            long otherPos = BlockPos.m_121915_(pos.m_121878_(), dir);
            ControllerBlockEntity other = (ControllerBlockEntity)this.world.loaded.get(otherPos);
            if (other != null) {
               faces |= 1 << dir.ordinal();
               if (this.seen.add(otherPos)) {
                  this.members.add(other);
               }
            }
         }

         this.conflict = this.conflict
            | (this.maxX - this.minX >= 7 || this.maxY - this.minY >= 7 || this.maxZ - this.minZ >= 7 || Integer.bitCount(ControllerStructure.axes(faces)) > 1);
      }
   }

   private static final class WorldNetworks {
      final ServerLevel level;
      final Long2ObjectOpenHashMap<ControllerBlockEntity> loaded = new Long2ObjectOpenHashMap();
      final LongLinkedOpenHashSet pending = new LongLinkedOpenHashSet();
      final Set<ControllerStructure.Network> visualChanges = new HashSet<>();
      final TreeMap<Long, Set<ControllerStructure.Network>> output = new TreeMap<>();
      boolean active = true;
      @Nullable
      ControllerStructure.Validation validation;

      WorldNetworks(ServerLevel level) {
         this.level = level;
      }

      void invalidate(@Nullable ControllerStructure.Network network) {
         if (network != null && network.ready) {
            network.ready = false;
            this.visualChanges.remove(network);
            this.unschedule(network);
         }
      }

      void changed(long pos) {
         boolean touchesValidation = this.enqueue(pos);

         for (Direction dir : ControllerStructure.DIRECTIONS) {
            touchesValidation |= this.enqueue(BlockPos.m_121915_(pos, dir));
         }

         if (touchesValidation && this.validation != null) {
            this.pending.add(this.validation.origin);
            this.validation = null;
         }
      }

      private boolean enqueue(long pos) {
         ControllerBlockEntity member = (ControllerBlockEntity)this.loaded.get(pos);
         if (member != null) {
            this.invalidate(member.network());
            this.pending.add(pos);
         }

         return this.validation != null && this.validation.seen.contains(pos);
      }

      void schedule(ControllerStructure.Network network, long tick) {
         if (network.ready && !network.conflict && network.total != 0L && network.nextOutput > tick) {
            this.unschedule(network);
            network.nextOutput = tick;
            this.output.computeIfAbsent(tick, k -> new HashSet<>()).add(network);
         }
      }

      void unschedule(ControllerStructure.Network network) {
         if (network.nextOutput != Long.MAX_VALUE) {
            Set<ControllerStructure.Network> due = this.output.get(network.nextOutput);
            if (due != null) {
               due.remove(network);
               if (due.isEmpty()) {
                  this.output.remove(network.nextOutput);
               }
            }

            network.nextOutput = Long.MAX_VALUE;
         }
      }

      void tick() {
         for (int budget = 512; budget > 0; budget--) {
            if (this.validation == null) {
               if (this.pending.isEmpty()) {
                  break;
               }

               long origin = this.pending.removeFirstLong();
               ControllerBlockEntity member = (ControllerBlockEntity)this.loaded.get(origin);
               if (member == null || member.hasValidatedNetwork()) {
                  continue;
               }

               this.validation = new ControllerStructure.Validation(this, member);
            }

            ControllerStructure.Validation work = this.validation;
            if (work.step() && this.validation == work) {
               this.validation = null;
            }
         }

         long now = this.level.m_46467_();

         while (!this.output.isEmpty() && this.output.firstKey() <= now) {
            Entry<Long, Set<ControllerStructure.Network>> due = this.output.pollFirstEntry();

            for (ControllerStructure.Network network : due.getValue()) {
               if (network.nextOutput == due.getKey()) {
                  network.nextOutput = Long.MAX_VALUE;
                  if (network.ready && network.total > 0L) {
                     network.push(now);
                  }
               }
            }
         }

         if (!this.visualChanges.isEmpty()) {
            ControllerStructure.Network[] dirty = this.visualChanges.toArray(ControllerStructure.Network[]::new);
            this.visualChanges.clear();

            for (ControllerStructure.Network networkx : dirty) {
               networkx.updateVisuals();
            }
         }
      }
   }
}
