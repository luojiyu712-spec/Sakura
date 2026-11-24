package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.client.SakuraMeshParticle;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
   targets = {"team.teampotato.ruok.util.particle.ParticleRender"},
   remap = false
)
public abstract class RuOKParticleCullingMixin {
   @Inject(
      method = {"camCull"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private static void sakura$keepMeshInView(
      Vec3 cameraPosition, Vec3 cameraLook, @Coerce Object particle, double fov, CallbackInfoReturnable<Boolean> callback
   ) {
      if (particle instanceof SakuraMeshParticle) {
         callback.setReturnValue(false);
      }
   }
}
