package com.Luo_jiyv.SakuraMod.mixin;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ServerChunkCache.class})
public interface ServerChunkCacheInvoker {
   @Invoker("getVisibleChunkIfPresent")
   @Nullable
   ChunkHolder sakura$getVisibleChunkIfPresent(long var1);
}
