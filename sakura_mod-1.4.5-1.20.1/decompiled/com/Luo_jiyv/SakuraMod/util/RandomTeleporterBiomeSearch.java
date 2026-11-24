package com.Luo_jiyv.SakuraMod.util;

import java.util.Set;
import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate.Sampler;
import org.jetbrains.annotations.Nullable;

final class RandomTeleporterBiomeSearch {
   private static final int MAX_RING_INTERVALS = 64;
   private static final int MIN_RING_STEP = 16;
   private final RandomTeleporterSearch.Area area;
   private final BiomeSource source;
   private final Sampler sampler;
   private final Set<ResourceLocation> targets;
   private final int ringStep;
   private int radius;
   private int sample;
   private int samplesInRing;
   private boolean initialized;
   private boolean done;
   @Nullable
   private ResourceLocation excludedBiome;
   @Nullable
   private BlockPos result;

   RandomTeleporterBiomeSearch(RandomTeleporterSearch.Area area, BiomeSource source, Sampler sampler, Set<ResourceLocation> targets) {
      this.area = area;
      this.source = source;
      this.sampler = sampler;
      this.targets = targets;
      long span = (long)area.maxRadius() - area.minRadius();
      this.ringStep = (int)Math.max(16L, (span + 64L - 1L) / 64L);
      this.startRing(area.minRadius());
   }

   void step() {
      if (!this.done) {
         BlockPos origin = this.area.origin();
         if (!this.initialized) {
            this.initialized = true;
            if (this.targets.size() > 1) {
               this.excludedBiome = this.source
                  .m_203407_(origin.m_123341_() >> 2, origin.m_123342_() >> 2, origin.m_123343_() >> 2, this.sampler)
                  .m_203543_()
                  .map(key -> key.m_135782_())
                  .orElse(null);
               return;
            }
         }

         BlockPos candidate = this.area.pointAt(this.radius, (Math.PI * 2) * this.sample / this.samplesInRing);
         if (candidate != null
            && this.source
               .m_203407_(candidate.m_123341_() >> 2, origin.m_123342_() >> 2, candidate.m_123343_() >> 2, this.sampler)
               .m_203425_(key -> !key.m_135782_().equals(this.excludedBiome) && this.targets.contains(key.m_135782_()))) {
            this.result = candidate;
            this.done = true;
         } else {
            this.sample++;
            if (this.sample == this.samplesInRing) {
               if (this.radius == this.area.maxRadius()) {
                  this.done = true;
               } else {
                  this.startRing(this.radius + Math.min(this.ringStep, this.area.maxRadius() - this.radius));
               }
            }
         }
      }
   }

   private void startRing(int radius) {
      this.radius = radius;
      this.sample = 0;
      this.samplesInRing = radius == 0 ? 1 : Mth.m_14045_((int)((Math.PI * 2) * radius / 48.0), 32, 512);
   }

   boolean isDone() {
      return this.done;
   }

   @Nullable
   BlockPos result() {
      return this.result;
   }

   @Nullable
   BlockPos search(BooleanSupplier cancelled) {
      while (!this.done) {
         if (cancelled.getAsBoolean() || Thread.currentThread().isInterrupted()) {
            return null;
         }

         this.step();
      }

      return this.result;
   }
}
