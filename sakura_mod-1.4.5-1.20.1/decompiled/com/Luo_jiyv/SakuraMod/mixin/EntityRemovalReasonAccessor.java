package com.Luo_jiyv.SakuraMod.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Entity.class})
public interface EntityRemovalReasonAccessor {
   @Accessor("removalReason")
   void sakura$setRemovalReason(RemovalReason var1);

   @Accessor("levelCallback")
   EntityInLevelCallback sakura$getLevelCallback();
}
