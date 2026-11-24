package com.Luo_jiyv.SakuraMod.mixin;

import java.util.Map;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
   targets = {"net.minecraft.client.sounds.SoundManager$Preparations"}
)
public interface SoundPreparationsAccessor {
   @Accessor("registry")
   Map<ResourceLocation, WeighedSoundEvents> sakura$getRegistry();

   @Accessor("soundCache")
   Map<ResourceLocation, Resource> sakura$getFiles();
}
