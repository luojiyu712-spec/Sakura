package com.Luo_jiyv.SakuraMod.mixin;

import com.tacz.guns.api.modifier.ParameterizedCache;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import it.unimi.dsi.fastutil.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(
   value = {ParameterizedCachePair.class},
   remap = false
)
public interface TaczRecoilPairInvoker {
   @Invoker("<init>")
   static <L, R> ParameterizedCachePair<L, R> sakura$create(Pair<ParameterizedCache<L>, ParameterizedCache<R>> axes) {
      throw new AssertionError("Mixin invoker was not applied");
   }
}
