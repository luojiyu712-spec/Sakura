package com.Luo_jiyv.SakuraMod.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CobbleGeneratorScreen extends SimpleContainerScreen<CobbleGeneratorMenu> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/cobble_generator_gui.png");

   public CobbleGeneratorScreen(CobbleGeneratorMenu menu, Inventory inv, Component title) {
      super(menu, inv, title);
      this.f_97726_ = 176;
      this.f_97727_ = 222;
      this.f_97731_ = this.f_97727_ - 94;
   }

   @Override
   protected ResourceLocation getTexture() {
      return TEXTURE;
   }
}
