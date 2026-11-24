package com.Luo_jiyv.SakuraMod.util;

import com.Luo_jiyv.SakuraMod.mixin.ChunkGeneratorStructureStateAccessor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureSet.StructureSelectionEntry;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.jetbrains.annotations.Nullable;

final class RandomTeleporterStructureSearch {
   static final int MAX_PROBES = 32;
   private static final int MAX_CANDIDATES = 2048;
   private static final int SPREAD_GRID_RADIUS = 4;
   private static final List<ChunkPos> SPREAD_OFFSETS = spreadOffsets();
   private final RandomTeleporterSearch.Area area;
   private final Set<ResourceLocation> targets;
   private final ChunkGeneratorStructureState state;
   private final Iterator<Holder<StructureSet>> sets;
   private final ArrayDeque<RandomTeleporterStructureSearch.PlacementCursor> placements = new ArrayDeque<>();
   private final Set<StructurePlacement> scheduled = new HashSet<>();
   private final Set<Structure> matchingStructures = new HashSet<>();
   private final Set<Long> seenDestinations = new HashSet<>();
   private final List<ChunkPos> centers;
   private int candidates;

   RandomTeleporterStructureSearch(ServerLevel level, RandomTeleporterSearch.Area area, Set<ResourceLocation> targets, double angle) {
      this.area = area;
      this.targets = targets;
      this.state = level.m_7726_().m_255415_();
      this.sets = this.state.m_255252_().iterator();
      this.centers = searchCenters(area, angle);
   }

   @Nullable
   RandomTeleporterStructureSearch.Candidate step() {
      if (this.sets.hasNext()) {
         StructureSet set = (StructureSet)this.sets.next().m_203334_();
         boolean matches = false;

         for (StructureSelectionEntry entry : set.f_210003_()) {
            if (entry.f_210026_().m_203425_(key -> this.targets.contains(key.m_135782_()))) {
               this.matchingStructures.add((Structure)entry.f_210026_().m_203334_());
               matches = true;
            }
         }

         if (matches && this.scheduled.add(set.f_210004_())) {
            this.placements.addLast(new RandomTeleporterStructureSearch.PlacementCursor(set.f_210004_()));
         }

         return null;
      } else {
         RandomTeleporterStructureSearch.PlacementCursor cursor = this.placements.pollFirst();
         if (cursor == null) {
            return null;
         } else {
            ChunkPos chunk = cursor.next();
            if (!cursor.finished) {
               this.placements.addLast(cursor);
            }

            if (chunk == null) {
               return null;
            } else {
               this.candidates++;
               BlockPos pos = cursor.placement.m_227039_(chunk);
               if (this.area.insideBorder(pos.m_123341_(), pos.m_123343_()) && this.area.inRing(pos)) {
                  return !this.seenDestinations.add(pos.m_121878_()) ? null : new RandomTeleporterStructureSearch.Candidate(chunk, pos);
               } else {
                  return null;
               }
            }
         }
      }
   }

   boolean isDone() {
      return this.candidates >= 2048 || !this.sets.hasNext() && this.placements.isEmpty();
   }

   boolean matches(ChunkAccess chunk) {
      for (Entry<Structure, StructureStart> entry : chunk.m_6633_().entrySet()) {
         if (this.matchingStructures.contains(entry.getKey()) && entry.getValue().m_73603_()) {
            return true;
         }
      }

      return false;
   }

   private static List<ChunkPos> searchCenters(RandomTeleporterSearch.Area area, double angle) {
      List<ChunkPos> centers = new ArrayList<>(3);

      for (int i = 0; i < 3; i++) {
         double radius = area.minRadius() + (area.maxRadius() - area.minRadius()) * (i / 2.0);
         double direction = angle + i * Math.PI * 2.0 * 0.6180339887498949;
         BlockPos pos = area.pointAt(radius, direction);
         if (pos != null) {
            centers.add(new ChunkPos(pos));
         }
      }

      if (centers.isEmpty()) {
         centers.add(new ChunkPos(area.origin()));
      }

      return centers;
   }

   private static List<ChunkPos> spreadOffsets() {
      List<ChunkPos> offsets = new ArrayList<>();

      for (int radius = 0; radius <= 4; radius++) {
         for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
               if (Math.max(Math.abs(x), Math.abs(z)) == radius) {
                  offsets.add(new ChunkPos(x, z));
               }
            }
         }
      }

      return offsets;
   }

   record Candidate(ChunkPos chunk, BlockPos destination) {
   }

   private final class PlacementCursor {
      private final StructurePlacement placement;
      private int index;
      private boolean finished;

      private PlacementCursor(StructurePlacement placement) {
         this.placement = placement;
      }

      @Nullable
      private ChunkPos next() {
         if (this.placement instanceof ConcentricRingsStructurePlacement ring) {
            CompletableFuture<List<ChunkPos>> future = ((ChunkGeneratorStructureStateAccessor)RandomTeleporterStructureSearch.this.state)
               .sakura$getRingPositions()
               .get(ring);
            if (future != null && !future.isDone()) {
               return null;
            } else {
               List<ChunkPos> positions = future == null ? List.of() : future.getNow(List.of());
               if (this.index == positions.size()) {
                  this.finished = true;
                  return null;
               } else {
                  return positions.get(this.index++);
               }
            }
         } else if (this.index == RandomTeleporterStructureSearch.SPREAD_OFFSETS.size() * RandomTeleporterStructureSearch.this.centers.size()) {
            this.finished = true;
            return null;
         } else {
            ChunkPos center = RandomTeleporterStructureSearch.this.centers.get(this.index % RandomTeleporterStructureSearch.this.centers.size());
            ChunkPos offset = RandomTeleporterStructureSearch.SPREAD_OFFSETS.get(this.index / RandomTeleporterStructureSearch.this.centers.size());
            this.index++;
            return this.placement instanceof RandomSpreadStructurePlacement spread
               ? spread.m_227008_(
                  RandomTeleporterStructureSearch.this.state.m_254887_(),
                  center.f_45578_ + offset.f_45578_ * spread.m_205003_(),
                  center.f_45579_ + offset.f_45579_ * spread.m_205003_()
               )
               : new ChunkPos(center.f_45578_ + offset.f_45578_, center.f_45579_ + offset.f_45579_);
         }
      }
   }
}
