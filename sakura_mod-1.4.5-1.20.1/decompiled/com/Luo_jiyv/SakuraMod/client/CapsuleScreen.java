package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleData;
import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleStat;
import com.Luo_jiyv.SakuraMod.network.CapsuleSettingsPacket;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CapsuleScreen extends Screen {
   private static final int VIEW_TOP = 44;
   private static final int ROW_HEIGHT = 38;
   private final InteractionHand hand;
   private final Map<CapsuleStat, CapsuleScreen.StatRow> rows = new EnumMap<>(CapsuleStat.class);
   private int viewBottom;
   private int scroll;
   private int maxScroll;
   private CapsuleScreen.ScrollBar scrollBar;

   public CapsuleScreen(InteractionHand hand) {
      super(Component.m_237115_("screen.sakura_mod.capsule.title"));
      this.hand = hand;
   }

   protected void m_7856_() {
      this.rows.clear();
      CapsuleStat[] stats = CapsuleStat.values();
      int panelWidth = Math.min(500, Math.max(260, this.f_96543_ - 100));
      int x = (this.f_96543_ - panelWidth) / 2;
      int startY = 46;
      int contentHeight = stats.length * 38;
      this.viewBottom = this.f_96544_ - 38;
      int viewportHeight = Math.max(40, this.viewBottom - 44);
      this.maxScroll = Math.max(0, startY + contentHeight - this.viewBottom);
      this.scroll = Math.max(0, Math.min(this.maxScroll, this.scroll));

      for (int i = 0; i < stats.length; i++) {
         this.rows.put(stats[i], new CapsuleScreen.StatRow(stats[i], x, startY + i * 38, panelWidth));
      }

      this.m_142416_(
         Button.m_253074_(Component.m_237115_("gui.done"), b -> this.m_7379_()).m_252987_(this.f_96543_ / 2 - 50, this.f_96544_ - 28, 100, 20).m_253136_()
      );
      this.scrollBar = new CapsuleScreen.ScrollBar(x + panelWidth + 8, 44, 8, viewportHeight);
      this.scrollBar.setMaxScroll(this.maxScroll);
      this.m_142416_(this.scrollBar);
      this.applyScroll();
   }

   private ItemStack currentStack() {
      Minecraft mc = Minecraft.m_91087_();
      return mc.f_91074_ == null ? ItemStack.f_41583_ : mc.f_91074_.m_21120_(this.hand);
   }

   private void setScroll(int newScroll) {
      this.scroll = Math.max(0, Math.min(this.maxScroll, newScroll));
      this.applyScroll();
      if (this.scrollBar != null) {
         this.scrollBar.setScroll(this.scroll);
      }
   }

   private void applyScroll() {
      for (CapsuleScreen.StatRow row : this.rows.values()) {
         row.applyScroll(this.scroll);
      }
   }

   public boolean m_6050_(double mouseX, double mouseY, double delta) {
      if (this.maxScroll > 0 && mouseY >= 44.0 && mouseY <= this.viewBottom) {
         this.setScroll(this.scroll - (int)Math.round(delta * 24.0));
         return true;
      } else {
         return super.m_6050_(mouseX, mouseY, delta);
      }
   }

   public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.m_280273_(graphics);
      graphics.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, 18, 16777215);
      graphics.m_280653_(this.f_96547_, Component.m_237115_("screen.sakura_mod.capsule.hint"), this.f_96543_ / 2, 30, 10526880);
      super.m_88315_(graphics, mouseX, mouseY, partialTick);
   }

   public void m_7379_() {
      if (!this.rows.isEmpty()) {
         Map<CapsuleStat, Double> values = new EnumMap<>(CapsuleStat.class);
         Map<CapsuleStat, Boolean> modes = new EnumMap<>(CapsuleStat.class);

         for (Entry<CapsuleStat, CapsuleScreen.StatRow> entry : this.rows.entrySet()) {
            values.put(entry.getKey(), entry.getValue().value);
            modes.put(entry.getKey(), entry.getValue().flat);
         }

         ModNetwork.CHANNEL.sendToServer(new CapsuleSettingsPacket(this.hand, values, modes));
      }

      super.m_7379_();
   }

   public boolean m_7043_() {
      return false;
   }

   private static Double parse(String text) {
      if (text != null && !text.isEmpty() && !"-".equals(text) && !".".equals(text) && !"-.".equals(text)) {
         try {
            double value = Double.parseDouble(text);
            value = Math.round(value * 100.0) / 100.0;
            return Math.max(-100.0, Math.min(100.0, value));
         } catch (NumberFormatException var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String format(double value) {
      return String.format(Locale.ROOT, "%.2f", value);
   }

   private final class ScrollBar extends AbstractWidget {
      private int maxScroll;
      private int scroll;
      private boolean dragging;

      private ScrollBar(int x, int y, int width, int height) {
         super(x, y, width, height, Component.m_237119_());
      }

      private void setMaxScroll(int maxScroll) {
         this.maxScroll = Math.max(0, maxScroll);
         this.f_93623_ = this.maxScroll > 0;
         this.f_93624_ = this.maxScroll > 0;
      }

      private void setScroll(int scroll) {
         this.scroll = Math.max(0, Math.min(this.maxScroll, scroll));
      }

      protected void m_87963_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
         int x = this.m_252754_();
         int y = this.m_252907_();
         int w = this.f_93618_;
         int h = this.f_93619_;
         graphics.m_280509_(x, y, x + w, y + h, 1426063360);
         int viewHeight = Math.max(1, CapsuleScreen.this.viewBottom - 44);
         int thumbHeight = Math.max(20, (int)((double)h * viewHeight / (viewHeight + this.maxScroll)));
         thumbHeight = Math.min(h, thumbHeight);
         double ratio = this.maxScroll <= 0 ? 0.0 : (double)this.scroll / this.maxScroll;
         int thumbY = y + (int)Math.round((h - thumbHeight) * ratio);
         graphics.m_280509_(x + 1, thumbY, x + w - 1, thumbY + thumbHeight, this.dragging ? -1 : -4671304);
      }

      public boolean m_6375_(double mouseX, double mouseY, int button) {
         if (this.f_93623_ && this.f_93624_ && button == 0 && this.m_5953_(mouseX, mouseY)) {
            this.dragging = true;
            this.updateFromMouse(mouseY);
            return true;
         } else {
            return false;
         }
      }

      public boolean m_7979_(double mouseX, double mouseY, int button, double dragX, double dragY) {
         if (this.dragging && button == 0) {
            this.updateFromMouse(mouseY);
            return true;
         } else {
            return false;
         }
      }

      public boolean m_6348_(double mouseX, double mouseY, int button) {
         if (this.dragging) {
            this.dragging = false;
            return true;
         } else {
            return false;
         }
      }

      private void updateFromMouse(double mouseY) {
         int viewHeight = Math.max(1, CapsuleScreen.this.viewBottom - 44);
         int thumbHeight = Math.max(20, (int)((double)this.f_93619_ * viewHeight / (viewHeight + this.maxScroll)));
         thumbHeight = Math.min(this.f_93619_, thumbHeight);
         double usable = Math.max(1, this.f_93619_ - thumbHeight);
         double ratio = (mouseY - this.m_252907_() - thumbHeight / 2.0) / usable;
         ratio = Math.max(0.0, Math.min(1.0, ratio));
         CapsuleScreen.this.setScroll((int)Math.round(ratio * this.maxScroll));
      }

      protected void m_168797_(NarrationElementOutput narrationElementOutput) {
      }
   }

   private final class StatRow {
      private final CapsuleStat stat;
      private final int x;
      private final int baseY;
      private final boolean enabled;
      private double value;
      private boolean flat;
      private CapsuleScreen.ValueSlider slider;
      private EditBox box;
      private Button reset;
      private Button modeButton;

      private StatRow(CapsuleStat stat, int x, int y, int panelWidth) {
         this.stat = stat;
         this.x = x;
         this.baseY = y;
         this.enabled = stat.isEnabled();
         ItemStack stack = CapsuleScreen.this.currentStack();
         this.value = Math.round(CapsuleData.capsuleValue(stack, stat) * 100.0) / 100.0;
         this.flat = CapsuleData.capsuleFlat(stack, stat);
         int sliderWidth = Math.max(80, panelWidth - 148);
         int boxX = x + sliderWidth + 4;
         int resetX = boxX + 60;
         int modeX = resetX + 38;
         this.box = new EditBox(CapsuleScreen.this.f_96547_, boxX, y + 1, 56, 18, Component.m_237113_("0.00"));
         this.box.m_94199_(8);
         this.box.m_94144_(CapsuleScreen.format(this.value));
         this.box.m_94186_(this.enabled);
         this.box.f_93623_ = this.enabled;
         this.box.m_94151_(text -> {
            Double v = CapsuleScreen.parse(text);
            if (v != null) {
               this.value = v;
               if (this.slider != null) {
                  this.slider.setValueQuietly(v);
               }
            }
         });
         CapsuleScreen.this.m_142416_(this.box);
         Component label = Component.m_237115_(stat.translationKey());
         if (!this.enabled) {
            label = label.m_6881_().m_7220_(Component.m_237115_("screen.sakura_mod.capsule.disabled"));
         }

         this.slider = new CapsuleScreen.ValueSlider(x, y, sliderWidth, 20, label, this.value, () -> this.flat, v -> {
            this.value = v;
            if (this.box != null) {
               this.box.m_94144_(CapsuleScreen.format(v));
            }
         });
         this.slider.f_93623_ = this.enabled;
         CapsuleScreen.this.m_142416_(this.slider);
         this.reset = Button.m_253074_(Component.m_237115_("screen.sakura_mod.capsule.reset"), b -> this.setValue(0.0))
            .m_252987_(resetX, y + 1, 34, 18)
            .m_253136_();
         this.reset.f_93623_ = this.enabled;
         CapsuleScreen.this.m_142416_(this.reset);
         this.modeButton = Button.m_253074_(this.modeLabel(), b -> this.toggleMode()).m_252987_(modeX, y + 1, 46, 18).m_253136_();
         this.modeButton.f_93623_ = this.enabled;
         CapsuleScreen.this.m_142416_(this.modeButton);
      }

      private Component modeLabel() {
         return Component.m_237115_(this.flat ? "screen.sakura_mod.capsule.mode.flat" : "screen.sakura_mod.capsule.mode.percent");
      }

      private void setValue(double newValue) {
         this.value = Math.max(-100.0, Math.min(100.0, newValue));
         if (this.slider != null) {
            this.slider.setValueQuietly(this.value);
         }

         if (this.box != null) {
            this.box.m_94144_(CapsuleScreen.format(this.value));
         }
      }

      private void toggleMode() {
         this.flat = !this.flat;
         if (this.modeButton != null) {
            this.modeButton.m_93666_(this.modeLabel());
         }

         if (this.slider != null) {
            this.slider.m_5695_();
         }
      }

      private void applyScroll(int offset) {
         int y = this.baseY - offset;
         boolean visible = y >= 44 && y + 38 <= CapsuleScreen.this.viewBottom;
         this.slider.m_253211_(y);
         this.slider.f_93624_ = visible;
         this.box.m_253211_(y + 1);
         this.box.f_93624_ = visible;
         this.box.f_93623_ = this.enabled && visible;
         if (!visible && this.box.m_93696_()) {
            this.box.m_93692_(false);
         }

         this.reset.m_253211_(y + 1);
         this.reset.f_93624_ = visible;
         this.modeButton.m_253211_(y + 1);
         this.modeButton.f_93624_ = visible;
      }
   }

   private static final class ValueSlider extends AbstractSliderButton {
      private final Component label;
      private final BooleanSupplier flatMode;
      private final Consumer<Double> onChange;

      private ValueSlider(int x, int y, int w, int h, Component label, double initial, BooleanSupplier flatMode, Consumer<Double> onChange) {
         super(x, y, w, h, Component.m_237119_(), (Math.max(-100.0, Math.min(100.0, initial)) + 100.0) / 200.0);
         this.label = label;
         this.flatMode = flatMode;
         this.onChange = onChange;
         this.m_5695_();
      }

      private double percent() {
         return Math.round((this.f_93577_ * 200.0 - 100.0) * 100.0) / 100.0;
      }

      private void setValueQuietly(double percent) {
         this.f_93577_ = Math.max(0.0, Math.min(1.0, (percent + 100.0) / 200.0));
         this.m_5695_();
      }

      protected void m_5695_() {
         String number = this.flatMode.getAsBoolean()
            ? String.format(Locale.ROOT, "%+.2f", this.percent())
            : String.format(Locale.ROOT, "%+.2f%%", this.percent());
         this.m_93666_(Component.m_237119_().m_7220_(this.label).m_7220_(Component.m_237113_(": " + number).m_130940_(ChatFormatting.GOLD)));
      }

      protected void m_5697_() {
         this.onChange.accept(this.percent());
      }
   }
}
