package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.client.music.MusicPlayerClient;
import com.Luo_jiyv.SakuraMod.client.music.MusicTrack;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MusicPreviewEntry extends TooltipListEntry<Boolean> {
   private static final int BUTTON_WIDTH = 58;
   private static final int BUTTON_HEIGHT = 20;
   private static final int TOGGLE_SIZE = 18;
   private static final int ROW_HEIGHT = 24;
   private final MusicTrack track;
   private final Consumer<Boolean> saveValue;
   private final Component label;
   private boolean enabled;
   private int lastX;
   private int lastY;
   private int lastWidth;
   private int lastHeight;

   public MusicPreviewEntry(MusicTrack track, Consumer<Boolean> saveValue, boolean enabled, Component label, Supplier<Optional<Component[]>> tooltipSupplier) {
      super(label, tooltipSupplier);
      this.track = track;
      this.saveValue = saveValue;
      this.enabled = enabled;
      this.label = label;
   }

   public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
      super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
      this.lastX = x;
      this.lastY = y;
      this.lastWidth = entryWidth;
      this.lastHeight = entryHeight;
      Minecraft mc = Minecraft.m_91087_();
      Font font = mc.f_91062_;
      int color = this.isEnabled() ? -1 : -6250336;
      int toggleX = x + entryWidth - 58 - 18 - 10;
      int toggleY = y + (entryHeight - 18) / 2;
      int buttonX = x + entryWidth - 58 - 2;
      int buttonY = y + (entryHeight - 20) / 2;
      int textY = y + (entryHeight - 8) / 2;
      int maxLabelWidth = Math.max(20, toggleX - x - 8);
      List<FormattedCharSequence> lines = font.m_92923_(this.label, maxLabelWidth);
      FormattedCharSequence line = lines.isEmpty() ? this.label.m_7532_() : lines.get(0);
      graphics.m_280649_(font, line, x, textY, color, false);
      boolean toggleHovered = mouseX >= toggleX && mouseX < toggleX + 18 && mouseY >= toggleY && mouseY < toggleY + 18;
      int toggleBorder = toggleHovered ? -1 : -9539986;
      graphics.m_280509_(toggleX, toggleY, toggleX + 18, toggleY + 18, toggleBorder);
      graphics.m_280509_(toggleX + 1, toggleY + 1, toggleX + 18 - 1, toggleY + 18 - 1, -14803426);
      if (this.enabled) {
         graphics.m_280509_(toggleX + 4, toggleY + 4, toggleX + 18 - 4, toggleY + 18 - 4, -11026086);
      }

      boolean buttonHovered = mouseX >= buttonX && mouseX < buttonX + 58 && mouseY >= buttonY && mouseY < buttonY + 20;
      boolean playing = MusicPlayerClient.isPreviewing(this.track);
      int borderColor = buttonHovered ? -1 : -9539986;
      int fillColor = playing ? -7459796 : (buttonHovered ? -12620120 : -14013910);
      graphics.m_280509_(buttonX, buttonY, buttonX + 58, buttonY + 20, borderColor);
      graphics.m_280509_(buttonX + 1, buttonY + 1, buttonX + 58 - 1, buttonY + 20 - 1, fillColor);
      String buttonText = Component.m_237115_(playing ? "config.sakura_mod.music.preview.stop" : "config.sakura_mod.music.preview.play").getString();
      graphics.m_280056_(font, buttonText, buttonX + (58 - font.m_92895_(buttonText)) / 2, buttonY + 6, -1, false);
   }

   public boolean m_6375_(double mouseX, double mouseY, int button) {
      int toggleX = this.lastX + this.lastWidth - 58 - 18 - 10;
      int toggleY = this.lastY + (this.lastHeight - 18) / 2;
      int buttonX = this.lastX + this.lastWidth - 58 - 2;
      int buttonY = this.lastY + (this.lastHeight - 20) / 2;
      if (button == 0 && mouseX >= toggleX && mouseX < toggleX + 18 && mouseY >= toggleY && mouseY < toggleY + 18) {
         this.enabled = !this.enabled;
         return true;
      } else if (button == 0 && mouseX >= buttonX && mouseX < buttonX + 58 && mouseY >= buttonY && mouseY < buttonY + 20) {
         MusicPlayerClient.togglePreview(this.track);
         return true;
      } else {
         return super.m_6375_(mouseX, mouseY, button);
      }
   }

   public void save() {
      super.save();
      this.saveValue.accept(this.enabled);
   }

   public int getItemHeight() {
      return 24;
   }

   public Boolean getValue() {
      return this.enabled;
   }

   public Optional<Boolean> getDefaultValue() {
      return Optional.of(true);
   }

   public List<? extends GuiEventListener> m_6702_() {
      return Collections.emptyList();
   }

   public List<? extends NarratableEntry> narratables() {
      return Collections.emptyList();
   }
}
