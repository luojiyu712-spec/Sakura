package com.Luo_jiyv.SakuraMod.mixin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ChunkGeneratorStructureState.class})
public interface ChunkGeneratorStructureStateAccessor {
   @Accessor("ringPositions")
   Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> sakura$getRingPositions();
}
