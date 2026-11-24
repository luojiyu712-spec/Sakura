package com.Luo_jiyv.SakuraMod.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class SimpleContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
   protected SimpleContainerScreen(T menu, Inventory inv, Component title) {
      super(menu, inv, title);
   }

   protected abstract ResourceLocation getTexture();

   protected void m_7286_(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
      RenderSystem.setShader(GameRenderer::m_172817_);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      gui.m_280218_(this.getTexture(), this.f_97735_, this.f_97736_, 0, 0, this.f_97726_, this.f_97727_);
   }

   public void m_88315_(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
      this.m_280273_(gui);
      super.m_88315_(gui, mouseX, mouseY, partialTick);
      this.m_280072_(gui, mouseX, mouseY);
   }
}
