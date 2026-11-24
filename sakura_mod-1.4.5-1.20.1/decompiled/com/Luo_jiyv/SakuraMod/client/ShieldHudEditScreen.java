package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.event.ShieldHudOverlay;
import java.nio.ByteBuffer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

@OnlyIn(Dist.CLIENT)
public class ShieldHudEditScreen extends Screen {
   private static final int LEFT = 1;
   private static final int RIGHT = 2;
   private static final int TOP = 4;
   private static final int BOTTOM = 8;
   private static final int MOVE = 16;
   private static final int HANDLE_RADIUS = 3;
   private int barWidth = (Integer)SakuraClientConfig.SHIELD_HUD_WIDTH.get();
   private int barHeight = (Integer)SakuraClientConfig.SHIELD_HUD_HEIGHT.get();
   private int offsetX = (Integer)SakuraClientConfig.SHIELD_HUD_OFFSET_X.get();
   private int offsetY = (Integer)SakuraClientConfig.SHIELD_HUD_OFFSET_Y.get();
   private int gesture;
   private double startMouseX;
   private double startMouseY;
   private int startX;
   private int startY;
   private int startWidth;
   private int startHeight;
   private final long[] cursors = new long[4];
   private long currentCursor;

   public ShieldHudEditScreen() {
      super(Component.m_237115_("screen.sakura_mod.shield_hud_edit.title"));
   }

   private ShieldHudOverlay.Layout layout() {
      return new ShieldHudOverlay.Layout(this.barWidth, this.barHeight);
   }

   private int left() {
      return ShieldHudOverlay.left(this.f_96543_, this.offsetX, this.layout());
   }

   private int top() {
      return ShieldHudOverlay.top(this.f_96544_, this.offsetY, this.layout());
   }

   protected void m_7856_() {
      this.gesture = 0;
      this.fitSizeToScreen();
      this.place(this.left(), this.top());
      int rowX = this.f_96543_ / 2 - 83;
      this.m_142416_(
         Button.m_253074_(Component.m_237115_("screen.sakura_mod.shield_hud_edit.reset"), button -> this.reset()).m_252987_(rowX, 64, 80, 20).m_253136_()
      );
      this.m_142416_(Button.m_253074_(Component.m_237115_("gui.done"), button -> this.m_7379_()).m_252987_(rowX + 86, 64, 80, 20).m_253136_());
      if (this.cursors[0] == 0L) {
         this.cursors[0] = GLFW.glfwCreateStandardCursor(221189);
         this.cursors[1] = GLFW.glfwCreateStandardCursor(221190);
         this.cursors[2] = diagonalCursor(false);
         this.cursors[3] = diagonalCursor(true);
      }
   }

   private void reset() {
      this.barWidth = 110;
      this.barHeight = 12;
      this.offsetX = this.offsetY = 0;
      this.fitSizeToScreen();
      this.place(this.left(), this.top());
      this.save();
   }

   private void fitSizeToScreen() {
      this.barWidth = Mth.m_14045_(this.barWidth, 64, Math.max(64, Math.min(360, this.f_96543_)));
      this.barHeight = Mth.m_14045_(this.barHeight, 10, Math.max(10, Math.min(64, this.f_96544_ - 4)));
   }

   private void place(int x, int y) {
      x = Mth.m_14045_(x, 0, Math.max(0, this.f_96543_ - this.layout().width()));
      y = Mth.m_14045_(y, 0, Math.max(0, this.f_96544_ - this.layout().height()));
      this.offsetX = (this.f_96543_ - this.layout().width()) / 2 - x;
      this.offsetY = y - (this.f_96544_ - 74);
   }

   public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      graphics.m_280509_(0, 0, this.f_96543_, this.f_96544_, 1342177280);
      int x = this.left();
      int y = this.top();
      int w = this.layout().width();
      int h = this.layout().height();
      if (this.f_96541_.f_91074_ != null) {
         ShieldHudOverlay.renderAt(graphics, this.f_96547_, this.f_96541_.f_91074_, partialTick, x, y, this.layout());
      }

      graphics.m_280509_(x - 1, y - 1, x + w + 1, y, -1057957771);
      graphics.m_280509_(x - 1, y + h, x + w + 1, y + h + 1, -1057957771);
      graphics.m_280509_(x - 1, y, x, y + h, -1057957771);
      graphics.m_280509_(x + w, y, x + w + 1, y + h, -1057957771);
      int hovered = this.hitHandle(mouseX, mouseY);

      for (int handle : this.handles()) {
         int hx = (handle & 1) != 0 ? x : ((handle & 2) != 0 ? x + w : x + w / 2);
         int hy = (handle & 4) != 0 ? y : ((handle & 8) != 0 ? y + h : y + h / 2);
         graphics.m_280509_(hx - 3, hy - 3, hx + 3, hy + 3, -15392211);
         graphics.m_280509_(hx - 2, hy - 2, hx + 2, hy + 2, hovered == handle ? -1 : -8841);
      }

      graphics.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, 16, 16777215);
      graphics.m_280653_(this.f_96547_, Component.m_237115_("screen.sakura_mod.shield_hud_edit.hint"), this.f_96543_ / 2, 32, 12965343);
      graphics.m_280653_(this.f_96547_, Component.m_237110_("screen.sakura_mod.shield_hud_edit.size", new Object[]{w, h}), this.f_96543_ / 2, 48, 8450047);
      super.m_88315_(graphics, mouseX, mouseY, partialTick);
      boolean overButton = this.m_6702_().stream().anyMatch(child -> child instanceof Button && child.m_5953_(mouseX, mouseY));
      this.cursor(overButton && this.gesture == 0 ? 0 : (this.gesture == 16 ? 0 : (this.gesture != 0 ? this.gesture : hovered)));
   }

   private int[] handles() {
      return new int[]{5, 6, 9, 10, 1, 2, 4, 8};
   }

   private int hitHandle(double mx, double my) {
      int x = this.left();
      int y = this.top();
      int w = this.layout().width();
      int h = this.layout().height();

      for (int handle : this.handles()) {
         int hx = (handle & 1) != 0 ? x : ((handle & 2) != 0 ? x + w : x + w / 2);
         int hy = (handle & 4) != 0 ? y : ((handle & 8) != 0 ? y + h : y + h / 2);
         if (Math.abs(mx - hx) <= 4.0 && Math.abs(my - hy) <= 4.0) {
            return handle;
         }
      }

      if (mx >= x - 3 && mx <= x + w + 3 && my >= y - 3 && my <= y + h + 3) {
         if (Math.abs(mx - x) <= 3.0) {
            return 1;
         }

         if (Math.abs(mx - x - w) <= 3.0) {
            return 2;
         }

         if (Math.abs(my - y) <= 3.0) {
            return 4;
         }

         if (Math.abs(my - y - h) <= 3.0) {
            return 8;
         }
      }

      return 0;
   }

   public boolean m_6375_(double mx, double my, int button) {
      if (super.m_6375_(mx, my, button)) {
         return true;
      } else if (button != 0) {
         return false;
      } else {
         this.gesture = this.hitHandle(mx, my);
         if (this.gesture == 0
            && mx >= this.left()
            && mx <= this.left() + this.layout().width()
            && my >= this.top()
            && my <= this.top() + this.layout().height()) {
            this.gesture = 16;
         }

         if (this.gesture == 0) {
            return false;
         } else {
            this.startMouseX = mx;
            this.startMouseY = my;
            this.startX = this.left();
            this.startY = this.top();
            this.startWidth = this.layout().width();
            this.startHeight = this.layout().height();
            return true;
         }
      }
   }

   public boolean m_7979_(double mx, double my, int button, double dragX, double dragY) {
      if (button == 0 && this.gesture != 0) {
         int dx = (int)Math.round(mx - this.startMouseX);
         int dy = (int)Math.round(my - this.startMouseY);
         if (this.gesture == 16) {
            this.place(this.startX + dx, this.startY + dy);
         } else {
            int right = this.startX + this.startWidth;
            int bottom = this.startY + this.startHeight;
            int x = this.startX;
            int y = this.startY;
            if ((this.gesture & 1) != 0) {
               x = Mth.m_14045_(this.startX + dx, Math.max(0, right - 360), right - 64);
            }

            if ((this.gesture & 2) != 0) {
               right = Mth.m_14045_(right + dx, this.startX + 64, Math.min(this.f_96543_, this.startX + 360));
            }

            if ((this.gesture & 4) != 0) {
               y = Mth.m_14045_(this.startY + dy, Math.max(0, bottom - 68), bottom - 14);
            }

            if ((this.gesture & 8) != 0) {
               bottom = Mth.m_14045_(bottom + dy, this.startY + 14, Math.min(this.f_96544_, this.startY + 68));
            }

            this.barWidth = right - x;
            this.barHeight = bottom - y - 4;
            this.place(x, y);
         }

         return true;
      } else {
         return super.m_7979_(mx, my, button, dragX, dragY);
      }
   }

   public boolean m_6348_(double mx, double my, int button) {
      if (button == 0 && this.gesture != 0) {
         this.gesture = 0;
         this.save();
         return true;
      } else {
         return super.m_6348_(mx, my, button);
      }
   }

   private void save() {
      SakuraClientConfig.SHIELD_HUD_OFFSET_X.set(this.offsetX);
      SakuraClientConfig.SHIELD_HUD_OFFSET_Y.set(this.offsetY);
      SakuraClientConfig.SHIELD_HUD_WIDTH.set(this.barWidth);
      SakuraClientConfig.SHIELD_HUD_HEIGHT.set(this.barHeight);
      SakuraClientConfig.save();
   }

   public void m_7379_() {
      this.save();
      super.m_7379_();
   }

   public boolean m_7043_() {
      return false;
   }

   private void cursor(int handle) {
      long desired = 0L;
      if (handle == 1 || handle == 2) {
         desired = this.cursors[0];
      } else if (handle == 4 || handle == 8) {
         desired = this.cursors[1];
      } else if (handle != 0 && handle != 16) {
         desired = this.cursors[handle != 5 && handle != 10 ? 3 : 2];
      }

      if (desired != this.currentCursor) {
         GLFW.glfwSetCursor(this.f_96541_.m_91268_().m_85439_(), desired);
         this.currentCursor = desired;
      }
   }

   public void m_7861_() {
      GLFW.glfwSetCursor(this.f_96541_.m_91268_().m_85439_(), 0L);
      this.currentCursor = 0L;

      for (int i = 0; i < this.cursors.length; i++) {
         if (this.cursors[i] != 0L) {
            GLFW.glfwDestroyCursor(this.cursors[i]);
         }

         this.cursors[i] = 0L;
      }

      super.m_7861_();
   }

   private static long diagonalCursor(boolean mirrored) {
      boolean[][] shape = new boolean[16][16];

      for (int i = 3; i <= 12; i++) {
         shape[i][i] = true;
      }

      for (int i = 3; i <= 7; i++) {
         shape[3][i] = true;
         shape[i][3] = true;
      }

      for (int i = 8; i <= 12; i++) {
         shape[12][i] = true;
         shape[i][12] = true;
      }

      ByteBuffer pixels = BufferUtils.createByteBuffer(1024);

      for (int y = 0; y < 16; y++) {
         for (int x = 0; x < 16; x++) {
            int px = mirrored ? 15 - x : x;
            boolean edge = false;

            for (int dy = -1; dy <= 1; dy++) {
               for (int dx = -1; dx <= 1; dx++) {
                  int xx = px + dx;
                  int yy = y + dy;
                  if (xx >= 0 && xx < 16 && yy >= 0 && yy < 16 && shape[yy][xx]) {
                     edge = true;
                  }
               }
            }

            byte value = (byte)(shape[y][px] ? -1 : 0);
            pixels.put(value).put(value).put(value).put((byte)(edge ? -1 : 0));
         }
      }

      pixels.flip();
      GLFWImage image = GLFWImage.malloc();

      long var17;
      try {
         image.set(16, 16, pixels);
         var17 = GLFW.glfwCreateCursor(image, 8, 8);
      } catch (Throwable var12) {
         if (image != null) {
            try {
               image.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (image != null) {
         image.close();
      }

      return var17;
   }
}
