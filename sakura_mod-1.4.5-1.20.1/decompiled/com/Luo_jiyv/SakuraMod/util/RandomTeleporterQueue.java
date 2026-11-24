package com.Luo_jiyv.SakuraMod.util;

import com.Luo_jiyv.SakuraMod.mixin.ServerChunkCacheInvoker;
import com.mojang.datafixers.util.Either;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.ChunkHolder.ChunkLoadingFailure;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

final class RandomTeleporterQueue {
   private static final TicketType<ChunkPos> TELEPORTER_TICKET = TicketType.m_9462_("sakura_random_teleporter", Comparator.comparingLong(ChunkPos::m_45588_));
   private static final long CHUNK_TIMEOUT = TimeUnit.SECONDS.toNanos(20L);
   private static final int CHUNK_INTERVAL_TICKS = 4;
   private static final int TELEPORT_INTERVAL_TICKS = 20;
   @Nullable
   private static RandomTeleporterQueue.Request active;
   private static final ArrayDeque<UUID> WAITING = new ArrayDeque<>();
   private static int ticksUntilNext;

   private RandomTeleporterQueue() {
   }

   static void tick() {
      if (ticksUntilNext > 0) {
         ticksUntilNext--;
      }

      if (active != null && active.released && active.isDone()) {
         active = null;
      }
   }

   @Nullable
   static RandomTeleporterQueue.Request request(UUID player, ServerLevel level, ChunkPos pos, ChunkStatus status) {
      if (!WAITING.contains(player)) {
         WAITING.addLast(player);
      }

      if (active == null && ticksUntilNext <= 0 && player.equals(WAITING.peekFirst())) {
         active = new RandomTeleporterQueue.Request(level.m_7726_(), pos, status);
         WAITING.removeFirst();
         ticksUntilNext = 4;
         return active;
      } else {
         return null;
      }
   }

   static void withdraw(UUID player) {
      WAITING.remove(player);
   }

   static void staggerTeleports() {
      ticksUntilNext = 20;
   }

   static void clear() {
      if (active != null) {
         active.release();
      }

      active = null;
      WAITING.clear();
      ticksUntilNext = 0;
   }

   static final class Request {
      private final ServerChunkCache source;
      private final ChunkPos pos;
      private final ChunkStatus status;
      private final int ticketLevel;
      private final int ticketDistance;
      private final long deadline = System.nanoTime() + RandomTeleporterQueue.CHUNK_TIMEOUT;
      private CompletableFuture<? extends Either<? extends ChunkAccess, ChunkLoadingFailure>> future;
      private boolean released;

      private Request(ServerChunkCache source, ChunkPos pos, ChunkStatus status) {
         this.source = source;
         this.pos = pos;
         this.status = status;
         this.ticketLevel = ChunkLevel.m_287141_(status);
         this.ticketDistance = ChunkLevel.m_287141_(ChunkStatus.f_62326_) - this.ticketLevel;
         source.m_8387_(RandomTeleporterQueue.TELEPORTER_TICKET, pos, this.ticketDistance, pos);
      }

      @Nullable
      ChunkAccess poll() {
         if (this.future == null) {
            ChunkHolder holder = this.readyHolder();
            if (holder == null) {
               return null;
            }

            this.future = this.status == ChunkStatus.f_62326_ ? holder.m_140082_() : holder.m_140049_(this.status, this.source.f_8325_);
         }

         Either<? extends ChunkAccess, ChunkLoadingFailure> result = (Either<? extends ChunkAccess, ChunkLoadingFailure>)this.future.getNow(null);
         return result == null ? null : (ChunkAccess)result.left().orElse(null);
      }

      boolean isDone() {
         return this.future == null || this.future.isDone();
      }

      boolean failedOrTimedOut() {
         return this.future != null && this.future.isDone() || System.nanoTime() - this.deadline >= 0L;
      }

      void release() {
         if (!this.released) {
            this.released = true;
            if (this.status == ChunkStatus.f_62326_ && this.future == null) {
               ChunkHolder holder = this.readyHolder();
               if (holder != null) {
                  this.future = holder.m_140082_();
               }
            }

            this.source.m_8438_(RandomTeleporterQueue.TELEPORTER_TICKET, this.pos, this.ticketDistance, this.pos);
            if (RandomTeleporterQueue.active == this && this.isDone()) {
               RandomTeleporterQueue.active = null;
            }
         }
      }

      @Nullable
      private ChunkHolder readyHolder() {
         ChunkHolder holder = ((ServerChunkCacheInvoker)this.source).sakura$getVisibleChunkIfPresent(this.pos.m_45588_());
         return holder != null && holder.m_140093_() <= this.ticketLevel ? holder : null;
      }
   }
}
