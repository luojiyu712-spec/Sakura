package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.network.RandomTeleporterSettingsPacket;
import com.Luo_jiyv.SakuraMod.network.RequestBiomeListPacket;
import com.Luo_jiyv.SakuraMod.network.RequestStructureListPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.IntConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RandomTeleporterScreen extends Screen {
   private final int absoluteMax;
   private int clientMin;
   private int clientMax;
   private RandomTeleporterScreen.RadiusSlider minSlider;
   private RandomTeleporterScreen.RadiusSlider maxSlider;
   private EditBox minBox;
   private EditBox maxBox;
   private int clientMode = 0;
   private final List<Button> modeButtons = new ArrayList<>();
   private boolean syncing = false;

   public RandomTeleporterScreen() {
      super(Component.m_237115_("screen.sakura_mod.random_teleporter.title"));
      this.absoluteMax = Config.getRandomTeleporterMaxRadiusLimit();
      this.clientMin = ClientRandomTeleporterSettings.getMinRadius();
      this.clientMax = ClientRandomTeleporterSettings.getMaxRadius();
      if (this.clientMin < 0) {
         this.clientMin = 0;
      }

      if (this.clientMin > this.absoluteMax) {
         this.clientMin = this.absoluteMax;
      }

      if (this.clientMax > this.absoluteMax) {
         this.clientMax = this.absoluteMax;
      }

      if (this.clientMax < this.clientMin) {
         this.clientMax = this.clientMin;
      }
   }

   protected void m_7856_() {
      if (!SakuraClientConfig.isBiomeSearchEnabled()) {
         ClientRandomTeleporterSettings.setBiomes(Set.of());
      }

      if (!SakuraClientConfig.isStructureSearchEnabled()) {
         ClientRandomTeleporterSettings.setStructures(Set.of());
      }

      boolean biomeEnabled = SakuraClientConfig.isBiomeSearchEnabled();
      boolean structureEnabled = SakuraClientConfig.isStructureSearchEnabled();
      int filterY = 20;
      int filterW = 96;
      int filterGap = 8;
      int visibleCount = (biomeEnabled ? 1 : 0) + (structureEnabled ? 1 : 0);
      int filterTotalW = filterW * visibleCount + filterGap * Math.max(0, visibleCount - 1);
      int filterX = this.f_96543_ / 2 - filterTotalW / 2;
      int cursor = filterX;
      if (biomeEnabled) {
         this.m_142416_(
            Button.m_253074_(
                  Component.m_237115_("screen.sakura_mod.random_teleporter.biome_filter"), b -> ModNetwork.CHANNEL.sendToServer(new RequestBiomeListPacket())
               )
               .m_252987_(filterX, filterY, filterW, 20)
               .m_253136_()
         );
         cursor = filterX + filterW + filterGap;
      }

      if (structureEnabled) {
         this.m_142416_(
            Button.m_253074_(
                  Component.m_237115_("screen.sakura_mod.random_teleporter.structure_filter"),
                  b -> ModNetwork.CHANNEL.sendToServer(new RequestStructureListPacket())
               )
               .m_252987_(cursor, filterY, filterW, 20)
               .m_253136_()
         );
      }

      this.clientMode = ClientRandomTeleporterSettings.getMode();
      this.modeButtons.clear();
      int btnW = 100;
      int btnH = 20;
      int btnGap = 4;
      int modeRowW = btnW * 3 + btnGap * 2;
      int modeStartX = this.f_96543_ / 2 - modeRowW / 2;
      int modeY = 46;

      for (int i = 0; i < 3; i++) {
         int m = i;
         Button btn = Button.m_253074_(this.modeLabel(m), b -> this.onModeClicked(m))
            .m_252987_(modeStartX + i * (btnW + btnGap), modeY, btnW, btnH)
            .m_253136_();
         this.modeButtons.add(btn);
         this.m_142416_(btn);
      }

      int sliderW = 200;
      int boxW = 80;
      int gap = 8;
      int slidersTotalW = sliderW + gap + boxW;
      int x = (this.f_96543_ - slidersTotalW) / 2;
      int yMin = this.f_96544_ / 2 - 30;
      int yMax = yMin + 28;
      this.minSlider = new RandomTeleporterScreen.RadiusSlider(
         x,
         yMin,
         sliderW,
         20,
         Component.m_237115_("screen.sakura_mod.random_teleporter.min"),
         this.clientMin,
         0,
         this.absoluteMax,
         true,
         this::onMinSliderChanged
      );
      this.m_142416_(this.minSlider);
      this.minBox = this.createNumberBox(x + sliderW + gap, yMin, boxW, this.clientMin, this::onMinBoxChanged);
      this.m_142416_(this.minBox);
      this.maxSlider = new RandomTeleporterScreen.RadiusSlider(
         x,
         yMax,
         sliderW,
         20,
         Component.m_237115_("screen.sakura_mod.random_teleporter.max"),
         this.clientMax,
         0,
         this.absoluteMax,
         false,
         this::onMaxSliderChanged
      );
      this.m_142416_(this.maxSlider);
      this.maxBox = this.createNumberBox(x + sliderW + gap, yMax, boxW, this.clientMax, this::onMaxBoxChanged);
      this.m_142416_(this.maxBox);
   }

   private Component modeLabel(int mode) {
      String key = switch (mode) {
         case 1 -> "screen.sakura_mod.random_teleporter.mode.sky";
         case 2 -> "screen.sakura_mod.random_teleporter.mode.cave";
         default -> "screen.sakura_mod.random_teleporter.mode.surface";
      };
      boolean selected = this.clientMode == mode;
      String prefix = selected ? "§a§l[✔] " : "§7[  ] ";
      Component name = Component.m_237115_(key);
      if (selected) {
         name = name.m_6881_().m_130944_(new ChatFormatting[]{ChatFormatting.GREEN, ChatFormatting.BOLD});
      } else {
         name = name.m_6881_().m_130940_(ChatFormatting.GRAY);
      }

      return Component.m_237113_(prefix).m_7220_(name);
   }

   private void onModeClicked(int mode) {
      this.clientMode = mode;
      ClientRandomTeleporterSettings.setMode(mode);

      for (int i = 0; i < this.modeButtons.size(); i++) {
         this.modeButtons.get(i).m_93666_(this.modeLabel(i));
      }
   }

   private EditBox createNumberBox(int x, int y, int w, int initial, IntConsumer onChanged) {
      EditBox box = new EditBox(this.f_96547_, x, y, w, 20, Component.m_237119_());
      box.m_94144_(String.valueOf(initial));
      box.m_94153_(s -> s.isEmpty() || s.matches("\\d{1,9}"));
      box.m_94151_(s -> {
         if (!this.syncing) {
            Integer v = parseInt(s);
            if (v != null) {
               onChanged.accept(v);
            }
         }
      });
      return box;
   }

   private void onMinSliderChanged(int v) {
      this.clientMin = v;
      if (this.clientMax < v) {
         this.clientMax = v;
         this.syncMaxSlider();
         this.syncMaxBox();
      }

      this.syncMinBox();
   }

   private void onMaxSliderChanged(int v) {
      this.clientMax = v;
      if (this.clientMin > v) {
         this.clientMin = v;
         this.syncMinSlider();
         this.syncMinBox();
      }

      this.syncMaxBox();
   }

   private void onMinBoxChanged(int v) {
      v = clamp(v, 0, this.absoluteMax);
      this.clientMin = v;
      if (this.clientMax < v) {
         this.clientMax = v;
         this.syncMaxSlider();
         this.syncMaxBox();
      }

      this.syncMinSlider();
   }

   private void onMaxBoxChanged(int v) {
      v = clamp(v, 0, this.absoluteMax);
      this.clientMax = v;
      if (this.clientMin > v) {
         this.clientMin = v;
         this.syncMinSlider();
         this.syncMinBox();
      }

      this.syncMaxSlider();
   }

   private void syncMinSlider() {
      this.minSlider.setValueFromInt(this.clientMin);
   }

   private void syncMaxSlider() {
      this.maxSlider.setValueFromInt(this.clientMax);
   }

   private void syncMinBox() {
      this.syncing = true;
      this.minBox.m_94144_(String.valueOf(this.clientMin));
      this.syncing = false;
   }

   private void syncMaxBox() {
      this.syncing = true;
      this.maxBox.m_94144_(String.valueOf(this.clientMax));
      this.syncing = false;
   }

   private static Integer parseInt(String s) {
      if (s != null && !s.isEmpty()) {
         try {
            return Integer.parseInt(s);
         } catch (NumberFormatException var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static int clamp(int v, int lo, int hi) {
      return Math.max(lo, Math.min(hi, v));
   }

   public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.m_88315_(graphics, mouseX, mouseY, partialTick);
      graphics.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, this.f_96544_ / 2 - 52, 16777215);
      graphics.m_280653_(this.f_96547_, Component.m_237115_("screen.sakura_mod.random_teleporter.hint"), this.f_96543_ / 2, this.f_96544_ / 2 + 60, 10526880);
   }

   public void m_7379_() {
      if (this.clientMax < this.clientMin) {
         int t = this.clientMin;
         this.clientMin = this.clientMax;
         this.clientMax = t;
      }

      ClientRandomTeleporterSettings.set(this.clientMin, this.clientMax);
      ModNetwork.CHANNEL
         .sendToServer(
            new RandomTeleporterSettingsPacket(
               this.clientMin,
               this.clientMax,
               new ArrayList<>(ClientRandomTeleporterSettings.getAllowedBiomes()),
               this.clientMode,
               new ArrayList<>(ClientRandomTeleporterSettings.getAllowedStructures())
            )
         );
      super.m_7379_();
   }

   public boolean m_7043_() {
      return false;
   }

   private class RadiusSlider extends AbstractSliderButton {
      private final Component label;
      private final int sliderMin;
      private final int sliderMax;
      private final IntConsumer onChanged;

      RadiusSlider(int x, int y, int w, int h, Component label, int initial, int sliderMin, int sliderMax, boolean isMin, IntConsumer onChanged) {
         super(x, y, w, h, Component.m_237119_(), ratio(initial, sliderMin, sliderMax));
         this.label = label;
         this.sliderMin = sliderMin;
         this.sliderMax = sliderMax;
         this.onChanged = onChanged;
         this.m_5695_();
      }

      private static double ratio(int value, int min, int max) {
         if (max <= min) {
            return 0.0;
         } else {
            double r = (double)(value - min) / (max - min);
            return Math.max(0.0, Math.min(1.0, r));
         }
      }

      int intValue() {
         return (int)Math.round(this.sliderMin + this.f_93577_ * (this.sliderMax - this.sliderMin));
      }

      void setValueFromInt(int v) {
         this.f_93577_ = ratio(v, this.sliderMin, this.sliderMax);
         this.m_5695_();
      }

      protected void m_5695_() {
         this.m_93666_(Component.m_237113_(this.label.getString() + ": " + this.intValue()));
      }

      protected void m_5697_() {
         this.onChanged.accept(this.intValue());
      }
   }
}
