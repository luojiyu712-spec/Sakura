package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.client.SakuraClientConfig;
import com.Luo_jiyv.SakuraMod.client.ShieldClientTracker;
import com.Luo_jiyv.SakuraMod.client.ShieldHudEditScreen;
import com.Luo_jiyv.SakuraMod.network.ShieldStateSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class ShieldHudOverlay implements IGuiOverlay {
   public static final int BOTTOM_OFFSET = 74;
   public static final int COOLDOWN_STROKE = 2;

   public static ShieldHudOverlay.Layout configuredLayout() {
      return new ShieldHudOverlay.Layout((Integer)SakuraClientConfig.SHIELD_HUD_WIDTH.get(), (Integer)SakuraClientConfig.SHIELD_HUD_HEIGHT.get());
   }

   public static int left(int screenWidth, int offsetX, ShieldHudOverlay.Layout layout) {
      return Mth.m_14045_((screenWidth - layout.width()) / 2 - offsetX, 0, Math.max(0, screenWidth - layout.width()));
   }

   public static int top(int screenHeight, int offsetY, ShieldHudOverlay.Layout layout) {
      return Mth.m_14045_(screenHeight - 74 + offsetY, 0, Math.max(0, screenHeight - layout.height()));
   }

   public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
      Minecraft mc = Minecraft.m_91087_();
      Player player = mc.f_91074_;
      if (player != null
         && !mc.f_91066_.f_92062_
         && !(mc.f_91080_ instanceof ShieldHudEditScreen)
         && ShieldMechanismEvents.canUseShield(player)
         && ShieldClientTracker.getShield().capacity() != 0) {
         ShieldHudOverlay.Layout layout = configuredLayout();
         renderAt(
            graphics,
            mc.f_91062_,
            player,
            partialTick,
            left(screenWidth, (Integer)SakuraClientConfig.SHIELD_HUD_OFFSET_X.get(), layout),
            top(screenHeight, (Integer)SakuraClientConfig.SHIELD_HUD_OFFSET_Y.get(), layout),
            layout
         );
      }
   }

   public static void renderAt(GuiGraphics graphics, Font font, Player player, float partialTick, int x, int y, ShieldHudOverlay.Layout layout) {
      ShieldStateSyncPacket shield = ShieldClientTracker.getShield();
      int capacity = shield.capacity();
      if (capacity == 0) {
         int previewCapacity = 100;
         renderBar(graphics, font, x, y, layout, previewCapacity, previewCapacity, 1.0F, 1.0F, -8787214);
      } else {
         float remaining = ShieldClientTracker.getClientRemainingCooldown(player, partialTick);
         float cooldown = Mth.m_14036_(1.0F - remaining / 300.0F, 0.0F, 1.0F);
         float displayedCharges = ShieldClientTracker.getClientDisplayedCharges(player, partialTick);
         float energy = Mth.m_14036_(displayedCharges / capacity, 0.0F, 1.0F);
         int cooldownColor = remaining > 0.0F ? -1656469 : -8787214;
         renderBar(graphics, font, x, y, layout, shield.energy(), capacity, energy, cooldown, cooldownColor);
      }
   }

   private static void renderBar(
      GuiGraphics g, Font font, int x, int y, ShieldHudOverlay.Layout layout, int charges, int capacity, float energy, float cooldown, int cooldownColor
   ) {
      int w = layout.width();
      int h = layout.barHeight();
      g.m_280509_(x, y, x + w, y + h, charges == 0 ? -6794901 : -13075048);
      g.m_280509_(x + 1, y + 1, x + w - 1, y + h - 1, -267971810);
      int filled = Math.round((w - 4) * energy);
      if (filled > 0) {
         g.m_280024_(x + 2, y + 2, x + 2 + filled, y + h - 2, -12001807, -15440732);
         g.m_280509_(x + 2, y + 2, x + 2 + filled, y + 3, -5047553);
      }

      label(g, font, charges + " / " + capacity, x + w / 2.0F, y + h / 2.0F, w - 8, h - 3, charges);
      int cy = y + h + 2;
      g.m_280509_(x + 1, cy, x + w - 1, cy + 2, -653914085);
      g.m_280509_(x + 1, cy, x + 1 + Math.round((w - 2) * cooldown), cy + 2, cooldownColor);
   }

   private static void label(GuiGraphics g, Font font, String text, float cx, float cy, float availableWidth, float availableHeight, int charges) {
      float scale = Math.min(1.5F, Math.min(availableWidth / font.m_92895_(text), availableHeight / 9.0F));
      g.m_280168_().m_85836_();
      g.m_280168_().m_252880_(cx, cy, 0.0F);
      g.m_280168_().m_85841_(scale, scale, 1.0F);
      g.m_280056_(font, text, -font.m_92895_(text) / 2, -9 / 2, charges == 0 ? -21064 : -983809, true);
      g.m_280168_().m_85849_();
   }

   public record Layout(int barWidth, int barHeight) {
      public int width() {
         return this.barWidth;
      }

      public int height() {
         return this.barHeight + 4;
      }
   }
}
