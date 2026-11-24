package com.Luo_jiyv.SakuraMod.mixin;

import com.Luo_jiyv.SakuraMod.command.DeleteCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PersistentEntitySectionManager.class})
public abstract class PersistentEntitySectionManagerMixin {
   @Inject(
      method = {"addEntityWithoutEvent(Lnet/minecraft/world/level/entity/EntityAccess;Z)Z"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void sakura$checkDeleteLocks(EntityAccess entry, boolean loadedFromDisk, CallbackInfoReturnable<Boolean> cir) {
      if (entry instanceof Entity entity && DeleteCommand.isAdmissionBlocked(entity, loadedFromDisk)) {
         cir.setReturnValue(false);
      }
   }
}
