package com.Luo_jiyv.SakuraMod.menu;

import com.Luo_jiyv.SakuraMod.blockentity.BeeHoneyBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

@OnlyIn(Dist.CLIENT)
public class BeeHoneyScreen extends AbstractContainerScreen<BeeHoneyMenu> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/bee_honey_gui.png");
   private static final int FLUID_X = 26;
   private static final int FLUID_Y = 17;
   private static final int FLUID_W = 16;
   private static final int FLUID_H = 52;
   private FluidStack fluidScratch = new FluidStack(Fluids.f_76191_, 0);

   public BeeHoneyScreen(BeeHoneyMenu menu, Inventory inv, Component title) {
      super(menu, inv, title);
      this.f_97726_ = 176;
      this.f_97727_ = 166;
      this.f_97731_ = this.f_97727_ - 94;
   }

   protected void m_7286_(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
      RenderSystem.setShader(GameRenderer::m_172817_);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int x = (this.f_96543_ - this.f_97726_) / 2;
      int y = (this.f_96544_ - this.f_97727_) / 2;
      gui.m_280218_(TEXTURE, x, y, 0, 0, this.f_97726_, this.f_97727_);
      this.renderFluid(gui, x, y);
   }

   private void renderFluid(GuiGraphics gui, int x, int y) {
      int amount = ((BeeHoneyMenu)this.f_97732_).getFluidData().m_6413_(0);
      int capacity = ((BeeHoneyMenu)this.f_97732_).getFluidData().m_6413_(1);
      if (amount > 0 && capacity > 0) {
         Fluid honey = BeeHoneyBlockEntity.getHoneyFluid();
         if (honey != null && honey != Fluids.f_76191_) {
            FluidStack fluid = this.fluidScratch;
            if (fluid.getFluid() != honey) {
               fluid = new FluidStack(honey, amount);
               this.fluidScratch = fluid;
            } else {
               fluid.setAmount(amount);
            }

            int fluidHeight = (int)((float)amount / capacity * 52.0F);
            if (fluidHeight > 0) {
               IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(honey);
               ResourceLocation still = ext.getStillTexture(fluid);
               if (still != null) {
                  TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.m_91087_().m_91258_(InventoryMenu.f_39692_).apply(still);
                  int color = ext.getTintColor(fluid);
                  float r = (color >> 16 & 0xFF) / 255.0F;
                  float g = (color >> 8 & 0xFF) / 255.0F;
                  float b = (color & 0xFF) / 255.0F;
                  float a = (color >> 24 & 0xFF) / 255.0F;
                  if (a == 0.0F) {
                     a = 1.0F;
                  }

                  RenderSystem.enableBlend();
                  RenderSystem.setShaderColor(r, g, b, a);
                  int drawX = x + 26;
                  int drawY = y + 17 + (52 - fluidHeight);
                  int drawn = 0;

                  while (drawn < fluidHeight) {
                     int seg = Math.min(16, fluidHeight - drawn);
                     gui.m_280159_(drawX, drawY + fluidHeight - drawn - seg, 0, 16, seg, sprite);
                     drawn += seg;
                  }

                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  RenderSystem.disableBlend();
               }
            }
         }
      }
   }

   public void m_88315_(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
      this.m_280273_(gui);
      super.m_88315_(gui, mouseX, mouseY, partialTick);
      this.m_280072_(gui, mouseX, mouseY);
      int x = (this.f_96543_ - this.f_97726_) / 2 + 26;
      int y = (this.f_96544_ - this.f_97727_) / 2 + 17;
      if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 52) {
         int amount = ((BeeHoneyMenu)this.f_97732_).getFluidData().m_6413_(0);
         int cap = ((BeeHoneyMenu)this.f_97732_).getFluidData().m_6413_(1);
         gui.m_280557_(this.f_96547_, Component.m_237113_(amount + " / " + cap + " mB"), mouseX, mouseY);
      }
   }
}
