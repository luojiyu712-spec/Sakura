package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.network.RandomTeleporterSettingsPacket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class StructureSelectScreen extends Screen {
   private final Screen parent;
   private final List<ResourceLocation> structures;
   private final Set<ResourceLocation> selected;
   private List<ResourceLocation> filtered;
   private static final int ROW_H = 22;
   private static final int TOP = 50;
   private static final int BOTTOM_MARGIN = 56;
   private static final int ROW_W = 300;
   private int scroll = 0;
   private int rowsPerScreen;
   private EditBox searchBox;
   private Button onlySelectedButton;
   private boolean onlySelected = false;
   private final List<StructureSelectScreen.Row> rows = new ArrayList<>();
   private Language lastLanguage;

   public StructureSelectScreen(Screen parent, List<ResourceLocation> structures, Set<ResourceLocation> selected) {
      super(Component.m_237115_("screen.sakura_mod.structure_select.title"));
      this.parent = parent;
      this.structures = structures;
      this.selected = new HashSet<>(selected);
      this.filtered = new ArrayList<>(structures);
   }

   protected void m_7856_() {
      this.rowsPerScreen = Math.max(1, (this.f_96544_ - 50 - 56) / 22);
      this.searchBox = new EditBox(this.f_96547_, this.f_96543_ / 2 - 150, 30, 200, 18, Component.m_237119_());
      this.searchBox.m_94199_(64);
      this.searchBox.m_257771_(Component.m_237113_("§7搜索结构名或 ID..."));
      this.searchBox.m_94151_(s -> this.applyFilter());
      this.m_142416_(this.searchBox);
      this.onlySelectedButton = Button.m_253074_(this.onlySelectedLabel(), b -> {
         this.onlySelected = !this.onlySelected;
         b.m_93666_(this.onlySelectedLabel());
         this.applyFilter();
      }).m_252987_(this.f_96543_ / 2 + 54, 30, 96, 18).m_253136_();
      this.m_142416_(this.onlySelectedButton);
      this.rows.clear();
      int rowX = this.f_96543_ / 2 - 150;

      for (int i = 0; i < this.rowsPerScreen; i++) {
         StructureSelectScreen.Row row = new StructureSelectScreen.Row();
         Button btn = Button.m_253074_(Component.m_237119_(), b -> this.onRowClicked(row)).m_252987_(rowX, 50 + i * 22, 300, 20).m_253136_();
         row.button = btn;
         this.rows.add(row);
         this.m_142416_(btn);
      }

      this.applyFilter();
      this.m_142416_(
         Button.m_253074_(Component.m_237115_("gui.done"), b -> this.save()).m_252987_(this.f_96543_ / 2 - 104, this.f_96544_ - 30, 100, 20).m_253136_()
      );
      this.m_142416_(
         Button.m_253074_(Component.m_237115_("gui.cancel"), b -> this.f_96541_.m_91152_(this.parent))
            .m_252987_(this.f_96543_ / 2 + 4, this.f_96544_ - 30, 100, 20)
            .m_253136_()
      );
   }

   private void onRowClicked(StructureSelectScreen.Row row) {
      ResourceLocation id = row.id;
      if (id != null) {
         if (this.selected.contains(id)) {
            this.selected.remove(id);
         } else {
            this.selected.add(id);
         }

         if (this.onlySelected && !this.selected.contains(id)) {
            this.applyFilter();
         } else {
            row.button.m_93666_(this.rowLabel(id));
         }
      }
   }

   private Component onlySelectedLabel() {
      return Component.m_237113_(this.onlySelected ? "§a[✔] 仅已选" : "§7[  ] 仅已选");
   }

   private void applyFilter() {
      String q = this.searchBox.m_94155_().trim().toLowerCase(Locale.ROOT);
      List<ResourceLocation> base;
      if (this.onlySelected) {
         base = new ArrayList<>();

         for (ResourceLocation id : this.structures) {
            if (this.selected.contains(id)) {
               base.add(id);
            }
         }
      } else {
         base = new ArrayList<>(this.structures);
      }

      if (q.isEmpty()) {
         this.filtered = base;
      } else {
         this.filtered = new ArrayList<>();

         for (ResourceLocation idx : base) {
            String path = idx.m_135815_().toLowerCase(Locale.ROOT);
            String ns = idx.m_135827_().toLowerCase(Locale.ROOT);
            String name = getStructureName(idx).getString().toLowerCase(Locale.ROOT);
            if (path.contains(q) || ns.contains(q) || name.contains(q)) {
               this.filtered.add(idx);
            }
         }
      }

      this.scroll = 0;
      this.refreshRows();
   }

   private void refreshRows() {
      int start = this.scroll;
      int end = Math.min(this.filtered.size(), start + this.rowsPerScreen);
      int x = this.f_96543_ / 2 - 150;

      for (int i = 0; i < this.rows.size(); i++) {
         StructureSelectScreen.Row row = this.rows.get(i);
         int idx = start + i;
         if (idx < end) {
            ResourceLocation id = this.filtered.get(idx);
            row.id = id;
            row.button.m_252865_(x);
            row.button.m_253211_(50 + i * 22);
            row.button.m_93666_(this.rowLabel(id));
            row.button.f_93624_ = true;
            row.button.f_93623_ = true;
         } else {
            row.id = null;
            row.button.f_93624_ = false;
         }
      }
   }

   public boolean m_6050_(double mx, double my, double delta) {
      int maxScroll = Math.max(0, this.filtered.size() - this.rowsPerScreen);
      int newScroll = (int)Math.max(0.0, Math.min((double)maxScroll, this.scroll - Math.signum(delta)));
      if (newScroll != this.scroll) {
         this.scroll = newScroll;
         this.refreshRows();
         return true;
      } else {
         return super.m_6050_(mx, my, delta);
      }
   }

   private void save() {
      ClientRandomTeleporterSettings.setStructures(this.selected);
      ModNetwork.CHANNEL
         .sendToServer(
            new RandomTeleporterSettingsPacket(
               ClientRandomTeleporterSettings.getMinRadius(),
               ClientRandomTeleporterSettings.getMaxRadius(),
               new ArrayList<>(ClientRandomTeleporterSettings.getAllowedBiomes()),
               ClientRandomTeleporterSettings.getMode(),
               new ArrayList<>(this.selected)
            )
         );
      this.f_96541_.m_91152_(this.parent);
   }

   public void m_88315_(GuiGraphics gg, int mx, int my, float pt) {
      Language lang = Language.m_128107_();
      if (lang != this.lastLanguage) {
         this.lastLanguage = lang;
         this.refreshRows();
      }

      this.m_280273_(gg);
      super.m_88315_(gg, mx, my, pt);
      gg.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, 12, 16777215);
      gg.m_280653_(this.f_96547_, Component.m_237113_("§7勾选后优先传送到选中的结构（共 " + this.selected.size() + " 个已选）"), this.f_96543_ / 2, this.f_96544_ - 46, 10526880);
   }

   public void m_7379_() {
      this.f_96541_.m_91152_(this.parent);
   }

   public boolean m_7043_() {
      return false;
   }

   private Component rowLabel(ResourceLocation id) {
      String prefix = this.selected.contains(id) ? "§a[✔] " : "§7[  ] ";
      return Component.m_237113_(prefix).m_7220_(getStructureName(id)).m_7220_(Component.m_237113_(" §8[" + prettyNamespace(id.m_135827_()) + "]"));
   }

   private static String prettyNamespace(String ns) {
      return "minecraft".equals(ns) ? "Minecraft" : ns;
   }

   private static Component getStructureName(ResourceLocation id) {
      String key = "structure." + id.m_135827_() + "." + id.m_135815_();
      String translated = I18n.m_118938_(key, new Object[0]);
      if (translated != null && !translated.equals(key)) {
         return Component.m_237113_(translated);
      } else {
         String path = id.m_135815_().replace('_', ' ');
         StringBuilder sb = new StringBuilder();

         for (String word : path.split(" ")) {
            if (!word.isEmpty()) {
               sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(' ');
            }
         }

         return Component.m_237113_(sb.toString().trim());
      }
   }

   private static final class Row {
      Button button;
      ResourceLocation id;
   }
}
