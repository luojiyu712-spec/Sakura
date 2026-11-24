package com.Luo_jiyv.SakuraMod.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ServerLevel.class})
public interface ServerLevelEntityAccessor {
   @Accessor("entityManager")
   PersistentEntitySectionManager<Entity> sakura$getEntityManager();

   @Accessor("entityTickList")
   EntityTickList sakura$getEntityTickList();
}
