package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.util.PlayerEquipCache;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class FlightBraceletOverlay implements IGuiOverlay {
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/flight_bracelet/segment.png");
   private static final int SEGMENTS = 10;
   private static final int SEGMENT_SIZE = 9;
   private static final int TEXTURE_SIZE = 16;
   private static final int GAP = -1;
   private static final int RIGHT_HEIGHT_INCREMENT = 10;

   public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
      Minecraft mc = Minecraft.m_91087_();
      Player player = mc.f_91074_;
      if (player != null) {
         if (!mc.f_91066_.f_92062_) {
            if (!player.m_5833_()) {
               if (ClientFlightTimeCache.getTarget() > 0 || player.m_150110_().f_35935_) {
                  if (PlayerEquipCache.hasFlightBracelet(player)) {
                     ClientFlightTimeCache.tick();
                     int currentRightHeight = gui.rightHeight;
                     int totalWidth = 81;
                     int barRight = screenWidth / 2 + 91;
                     int baseY = screenHeight - currentRightHeight;
                     gui.rightHeight += 10;
                     float fTime = ClientFlightTimeCache.getFloat();
                     float fSegments = fTime / 3600.0F;
                     int fullSegments = (int)Math.floor(fSegments);
                     float partial = fSegments - fullSegments;
                     RenderSystem.enableBlend();
                     RenderSystem.defaultBlendFunc();

                     for (int i = 0; i < 10; i++) {
                        int x = barRight - (i + 1) * 9 - i * -1;
                        float alpha;
                        if (i < fullSegments) {
                           alpha = 1.0F;
                        } else {
                           if (i != fullSegments) {
                              continue;
                           }

                           alpha = partial;
                        }

                        if (!(alpha <= 0.01F)) {
                           RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                           graphics.m_280163_(TEX, x, baseY, 0.0F, 0.0F, 9, 9, 16, 16);
                        }
                     }

                     RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                     RenderSystem.disableBlend();
                  }
               }
            }
         }
      }
   }
}
