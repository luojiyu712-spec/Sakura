package com.Luo_jiyv.SakuraMod.menu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChickenCoopScreen extends SimpleContainerScreen<ChickenCoopMenu> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/chicken_coop_gui.png");
   private static final int EGG_LABEL_Y = 58;
   private static final int EGG_LABEL_COLOR = 4210752;

   public ChickenCoopScreen(ChickenCoopMenu menu, Inventory inv, Component title) {
      super(menu, inv, title);
      this.f_97726_ = 176;
      this.f_97727_ = 222;
      this.f_97731_ = this.f_97727_ - 94;
   }

   @Override
   protected ResourceLocation getTexture() {
      return TEXTURE;
   }

   protected void m_280003_(GuiGraphics gui, int mouseX, int mouseY) {
      super.m_280003_(gui, mouseX, mouseY);
      Component label = Component.m_237115_("gui.sakura_mod.chicken_coop.egg_area");
      int labelX = (this.f_97726_ - this.f_96547_.m_92852_(label)) / 2;
      gui.m_280614_(this.f_96547_, label, labelX, 58, 4210752, false);
   }
}
