package com.Luo_jiyv.SakuraMod.menu;

import com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntity;
import com.Luo_jiyv.SakuraMod.integration.BeyondIntegration;
import com.Luo_jiyv.SakuraMod.network.AssemblerSettingsPacket;
import com.Luo_jiyv.SakuraMod.network.AssemblerSideConfigPacket;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

public class MolecularAssemblerScreen extends AbstractContainerScreen<MolecularAssemblerMenu> {
   private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/molecular_assembler.png");
   private static final ResourceLocation TAB_LEFT = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/tab/left_tab.png");
   private static final ResourceLocation TAB_RIGHT = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/tab/right_tab.png");
   private static final ResourceLocation ICON_ON = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/icon/control_mode_ignore.png");
   private static final ResourceLocation ICON_STOPPED = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/icon/control_mode_not_working.png");
   private static final ResourceLocation ICON_POWERED = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/icon/control_mode_powered.png");
   private static final ResourceLocation ICON_UNPOWERED = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/icon/control_mode_unpowered.png");
   private static final ResourceLocation ICON_POP_UP = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/icon/popmode_up.png");
   private static final ResourceLocation ICON_POP_DOWN = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/icon/popmode_down.png");
   private static final int TAB_STEP = 30;
   private static final int TAB_TOP = 14;
   private static final int BAR_X = 148;
   private static final int BAR_Y = 44;
   private static final int BAR_W = 6;
   private static final int BAR_H = 26;
   private static final ResourceLocation TEX_SIDE_BG = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/side_config.png");
   private static final int SIDE_W = 68;
   private static final int SIDE_H = 82;
   private static final int[][] SIDE_CELLS = new int[][]{{25, 17}, {5, 37}, {25, 37}, {45, 37}, {5, 57}, {25, 57}};
   private static final Direction[] SIDE_DIRS = new Direction[]{Direction.UP, Direction.WEST, Direction.NORTH, Direction.EAST, Direction.DOWN, Direction.SOUTH};
   private static final String[] SIDE_NAMES = new String[]{"无", "输出", "输入", "输入 / 输出"};
   private static final ResourceLocation TEX_BAR = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/energy_bar.png");
   private static final ResourceLocation TEX_ENERGY_ICON = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/energy_icon.png");
   private static final ResourceLocation ICON_SIDE_CONFIG = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/gui/icon/side_config.png");
   private MolecularAssemblerScreen.TabWidget netIn;
   private MolecularAssemblerScreen.TabWidget netOut;
   private MolecularAssemblerScreen.TabWidget netFe;
   private MolecularAssemblerScreen.TabWidget bind;
   private MolecularAssemblerScreen.TabWidget eject;
   private MolecularAssemblerScreen.TabWidget mode;
   private MolecularAssemblerScreen.TabWidget amount;
   private MolecularAssemblerScreen.TabWidget recipe;
   private MolecularAssemblerScreen.TabWidget sideConfig;
   private boolean sideConfigOpen = false;
   private int lblNetIn = Integer.MIN_VALUE;
   private int lblNetOut = Integer.MIN_VALUE;
   private int lblNetFe = Integer.MIN_VALUE;
   private int lblNetId = Integer.MIN_VALUE;
   private int lblEject = Integer.MIN_VALUE;
   private int lblMode = Integer.MIN_VALUE;
   private int lblAmount = Integer.MIN_VALUE;
   private int lblCount = Integer.MIN_VALUE;
   private int lblSelected = Integer.MIN_VALUE;
   private final ItemStack[] sideIcons = new ItemStack[]{
      ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_
   };
   private final BlockState[] sideStates = new BlockState[6];
   private long sideCacheTime = -1L;
   private static final int DRAG_NONE = 0;
   private static final int DRAG_MAIN = 1;
   private static final int DRAG_SIDE = 2;
   private boolean dragging = false;
   private int dragTarget = 0;
   private double dragLastX;
   private double dragLastY;
   private int sideOffX;
   private int sideOffY;

   public MolecularAssemblerScreen(MolecularAssemblerMenu menu, Inventory inv, Component title) {
      super(menu, inv, title);
      this.f_97726_ = 176;
      this.f_97727_ = 199;
   }

   protected void m_7856_() {
      super.m_7856_();
      this.invalidateLabelCache();
      int panelLeft = this.f_97735_;
      int panelTop = this.f_97736_;
      this.netIn = this.addTab(panelLeft - 23 + 1, panelTop + 14 + 0, true, "gui.sakura_mod.assembler.net_extract", (byte)3);
      this.netOut = this.addTab(panelLeft - 23 + 1, panelTop + 14 + 30, true, "gui.sakura_mod.assembler.net_insert", (byte)4);
      this.netFe = this.addTab(panelLeft - 23 + 1, panelTop + 14 + 60, true, "gui.sakura_mod.assembler.net_energy", (byte)5);
      this.bind = this.addTab(panelLeft - 23 + 1, panelTop + 14 + 90, true, "gui.sakura_mod.assembler.bind_net", (byte)6);
      int rx = panelLeft + 176 - 1;
      this.eject = this.addTab(rx, panelTop + 14 + 0, false, "gui.sakura_mod.assembler.auto_eject", (byte)1);
      this.mode = this.addTab(rx, panelTop + 14 + 30, false, "gui.sakura_mod.assembler.work_mode", (byte)0);
      this.amount = this.addTab(rx, panelTop + 14 + 60, false, "gui.sakura_mod.assembler.amount", (byte)2);
      this.recipe = new MolecularAssemblerScreen.TabWidget(rx, panelTop + 14 + 90, false, b -> {
         MolecularAssemblerBlockEntity be = ((MolecularAssemblerMenu)this.f_97732_).getBlockEntity();
         int count = Math.max(1, be.getRecipeCount());
         this.send((byte)7, (be.getSelected() + 1) % count);
      }, Component.m_237119_());
      this.m_142416_(this.recipe);
      this.sideConfig = new MolecularAssemblerScreen.TabWidget(rx, panelTop + 14 + 120, false, b -> this.sideConfigOpen = true, Component.m_237119_());
      this.sideConfig.setIcon(ICON_SIDE_CONFIG);
      this.sideConfig.m_257544_(Tooltip.m_257550_(Component.m_237113_("侧面配置")));
      this.m_142416_(this.sideConfig);
      if (!BeyondIntegration.isLoaded()) {
         this.netIn.f_93624_ = false;
         this.netOut.f_93624_ = false;
         this.netFe.f_93624_ = false;
         this.bind.f_93624_ = false;
      }

      this.refreshLabels();
   }

   private MolecularAssemblerScreen.TabWidget addTab(int x, int y, boolean left, String tooltipKey, byte action) {
      MolecularAssemblerScreen.TabWidget widget = new MolecularAssemblerScreen.TabWidget(x, y, left, b -> this.send(action, 0), Component.m_237119_());
      widget.tooltipKey = tooltipKey;
      this.m_142416_(widget);
      return widget;
   }

   private void send(byte action, int value) {
      ModNetwork.CHANNEL
         .sendToServer(
            new AssemblerSettingsPacket(
               ((MolecularAssemblerMenu)this.f_97732_).getBlockEntity().m_58899_(), ((MolecularAssemblerMenu)this.f_97732_).f_38840_, action, value
            )
         );
   }

   protected void m_181908_() {
      super.m_181908_();
      this.refreshLabels();
   }

   private void refreshLabels() {
      MolecularAssemblerBlockEntity be = ((MolecularAssemblerMenu)this.f_97732_).getBlockEntity();
      if (be != null) {
         int vNetIn = be.isNetExtract() ? 1 : 0;
         int vNetOut = be.isNetInsert() ? 1 : 0;
         int vNetFe = be.isNetEnergy() ? 1 : 0;
         int vNetId = be.getNetId();
         int vEject = be.isAutoEject() ? 1 : 0;
         int vMode = be.getWorkMode();
         int vAmount = be.getCraftAmount();
         int vCount = be.getRecipeCount();
         int vSelected = be.getSelected();
         if (vNetIn != this.lblNetIn
            || vNetOut != this.lblNetOut
            || vNetFe != this.lblNetFe
            || vNetId != this.lblNetId
            || vEject != this.lblEject
            || vMode != this.lblMode
            || vAmount != this.lblAmount
            || vCount != this.lblCount
            || vSelected != this.lblSelected) {
            this.lblNetIn = vNetIn;
            this.lblNetOut = vNetOut;
            this.lblNetFe = vNetFe;
            this.lblNetId = vNetId;
            this.lblEject = vEject;
            this.lblMode = vMode;
            this.lblAmount = vAmount;
            this.lblCount = vCount;
            this.lblSelected = vSelected;
            this.netIn.m_93666_(Component.m_237113_(vNetIn != 0 ? "开" : "关"));
            this.netOut.m_93666_(Component.m_237113_(vNetOut != 0 ? "开" : "关"));
            this.netFe.m_93666_(Component.m_237113_(vNetFe != 0 ? "开" : "关"));
            this.bind.m_93666_(Component.m_237113_(vNetId >= 0 ? "#" + vNetId : "—"));
            this.eject.setIcon(vEject != 0 ? ICON_POP_UP : ICON_POP_DOWN);

            this.mode.setIcon(switch (vMode) {
               case 1 -> ICON_STOPPED;
               case 2 -> ICON_POWERED;
               case 3 -> ICON_UNPOWERED;
               default -> ICON_ON;
            });
            this.amount.m_93666_(Component.m_237113_(vAmount == 1 ? "组" : "个"));
            this.recipe.m_93666_(Component.m_237113_(vCount > 1 ? String.valueOf(vSelected + 1) : "—"));
            this.recipe.tooltipKey = "gui.sakura_mod.assembler.pattern";
            tip(this.netIn, stateText(vNetIn != 0));
            tip(this.netOut, stateText(vNetOut != 0));
            tip(this.netFe, stateText(vNetFe != 0));
            tip(this.bind, Component.m_237113_(vNetId >= 0 ? "#" + vNetId : "—"));
            tip(this.eject, stateText(vEject != 0));
            tip(this.mode, Component.m_237115_(modeKey(vMode)));
            tip(this.amount, Component.m_237115_(vAmount == 1 ? "gui.sakura_mod.assembler.amount.stack" : "gui.sakura_mod.assembler.amount.single"));
            tip(this.recipe, Component.m_237113_(vCount > 1 ? vSelected + 1 + "/" + vCount : "—"));
         }
      }
   }

   private void invalidateLabelCache() {
      this.lblNetIn = Integer.MIN_VALUE;
      this.lblNetOut = Integer.MIN_VALUE;
      this.lblNetFe = Integer.MIN_VALUE;
      this.lblNetId = Integer.MIN_VALUE;
      this.lblEject = Integer.MIN_VALUE;
      this.lblMode = Integer.MIN_VALUE;
      this.lblAmount = Integer.MIN_VALUE;
      this.lblCount = Integer.MIN_VALUE;
      this.lblSelected = Integer.MIN_VALUE;
   }

   private static Component stateText(boolean on) {
      return Component.m_237115_(on ? "gui.sakura_mod.assembler.on" : "gui.sakura_mod.assembler.off");
   }

   private static String modeKey(int mode) {
      return switch (mode) {
         case 1 -> "gui.sakura_mod.assembler.mode.stopped";
         case 2 -> "gui.sakura_mod.assembler.mode.redstone_on";
         case 3 -> "gui.sakura_mod.assembler.mode.redstone_off";
         default -> "gui.sakura_mod.assembler.mode.always";
      };
   }

   private static void tip(MolecularAssemblerScreen.TabWidget widget, Component state) {
      if (widget != null && widget.tooltipKey != null) {
         widget.m_257544_(Tooltip.m_257550_(Component.m_237115_(widget.tooltipKey).m_130946_(": ").m_7220_(state)));
      }
   }

   public void m_88315_(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
      this.handleDrag(mouseX, mouseY);
      this.m_280273_(gg);
      super.m_88315_(gg, mouseX, mouseY, partialTick);
      this.renderGhosts(gg);
      if (this.sideConfigOpen) {
         this.renderSideConfig(gg, mouseX, mouseY);
      } else {
         MolecularAssemblerBlockEntity be = ((MolecularAssemblerMenu)this.f_97732_).getBlockEntity();
         if (be != null) {
            int bx = this.f_97735_ + 148;
            int by = this.f_97736_ + 44;
            if (mouseX >= bx && mouseX < bx + 6 && mouseY >= by && mouseY < by + 26) {
               gg.m_280557_(this.f_96547_, Component.m_237113_(be.getSyncedEnergy() + " / 360000 FE"), mouseX, mouseY);
               return;
            }
         }

         int iconX = this.energyIconX();
         int iconY = this.energyIconY();
         if (mouseX >= iconX && mouseX < iconX + 23 && mouseY >= iconY && mouseY < iconY + 26) {
            gg.m_280557_(this.f_96547_, Component.m_237113_("消耗 " + (be != null && be.getCraftAmount() == 1 ? 6400 : 100) + " FE/t"), mouseX, mouseY);
         } else {
            this.m_280072_(gg, mouseX, mouseY);
         }
      }
   }

   private int energyIconX() {
      return this.f_97735_ - 23 + 1;
   }

   private int energyIconY() {
      return this.f_97736_ + 14 + 120;
   }

   public boolean m_6375_(double mouseX, double mouseY, int button) {
      if (this.sideConfigOpen) {
         int px = this.sideConfigX();
         int py = this.sideConfigY();
         if (mouseX >= px + 3 && mouseX < px + 13 && mouseY >= py + 3 && mouseY < py + 13) {
            this.sideConfigOpen = false;
            return true;
         } else {
            for (int i = 0; i < 6; i++) {
               int cx = px + SIDE_CELLS[i][0];
               int cy = py + SIDE_CELLS[i][1];
               if (mouseX >= cx && mouseX < cx + 18 && mouseY >= cy && mouseY < cy + 18) {
                  ModNetwork.CHANNEL
                     .sendToServer(
                        new AssemblerSideConfigPacket(
                           ((MolecularAssemblerMenu)this.f_97732_).getBlockEntity().m_58899_(),
                           ((MolecularAssemblerMenu)this.f_97732_).f_38840_,
                           SIDE_DIRS[i].ordinal()
                        )
                     );
                  return true;
               }
            }

            return true;
         }
      } else {
         boolean onSlot = this.getSlotUnderMouse() != null;
         boolean onWidget = false;

         for (GuiEventListener listener : this.m_6702_()) {
            if (listener instanceof AbstractWidget w
               && w.f_93624_
               && mouseX >= w.m_252754_()
               && mouseX < w.m_252754_() + w.m_5711_()
               && mouseY >= w.m_252907_()
               && mouseY < w.m_252907_() + w.m_93694_()) {
               onWidget = true;
               break;
            }
         }

         return super.m_6375_(mouseX, mouseY, button);
      }
   }

   private void handleDrag(int mouseX, int mouseY) {
      if (this.f_96541_ != null && this.f_96541_.m_91268_() != null) {
         boolean down = GLFW.glfwGetMouseButton(this.f_96541_.m_91268_().m_85439_(), 0) == 1;
         if (!down) {
            this.dragging = false;
            this.dragTarget = 0;
         } else if (!((MolecularAssemblerMenu)this.f_97732_).m_142621_().m_41619_()) {
            this.dragging = false;
            this.dragTarget = 0;
         } else {
            boolean onTitle = mouseY >= this.f_97736_ && mouseY < this.f_97736_ + 14 && mouseX >= this.f_97735_ && mouseX < this.f_97735_ + 176;
            boolean inSidePanel = this.sideConfigOpen
               && mouseX >= this.sideConfigX()
               && mouseX < this.sideConfigX() + 68
               && mouseY >= this.sideConfigY()
               && mouseY < this.sideConfigY() + 82;
            boolean onSideCell = false;
            if (inSidePanel) {
               if (mouseX >= this.sideConfigX() + 3 && mouseX < this.sideConfigX() + 13 && mouseY >= this.sideConfigY() + 3 && mouseY < this.sideConfigY() + 13
                  )
                {
                  onSideCell = true;
               }

               for (int i = 0; i < 6 && !onSideCell; i++) {
                  int cx = this.sideConfigX() + SIDE_CELLS[i][0];
                  int cy = this.sideConfigY() + SIDE_CELLS[i][1];
                  if (mouseX >= cx && mouseX < cx + 18 && mouseY >= cy && mouseY < cy + 18) {
                     onSideCell = true;
                  }
               }
            }

            if (!this.dragging) {
               if (onTitle && !this.sideConfigOpen) {
                  this.dragging = true;
                  this.dragTarget = 1;
                  this.dragLastX = mouseX;
                  this.dragLastY = mouseY;
               } else if (inSidePanel && !onSideCell) {
                  this.dragging = true;
                  this.dragTarget = 2;
                  this.dragLastX = mouseX;
                  this.dragLastY = mouseY;
               }
            } else {
               int dx = (int)Math.round(mouseX - this.dragLastX);
               int dy = (int)Math.round(mouseY - this.dragLastY);
               if (dx != 0 || dy != 0) {
                  this.dragLastX = mouseX;
                  this.dragLastY = mouseY;
                  if (this.dragTarget == 2) {
                     this.sideOffX += dx;
                     this.sideOffY += dy;
                  } else {
                     this.f_97735_ += dx;
                     this.f_97736_ += dy;
                     this.moveWidgets(dx, dy);
                  }
               }
            }
         }
      }
   }

   private void moveWidgets(int dx, int dy) {
      for (GuiEventListener listener : this.m_6702_()) {
         if (listener instanceof AbstractWidget w) {
            w.m_252865_(w.m_252754_() + dx);
            w.m_253211_(w.m_252907_() + dy);
         }
      }
   }

   protected void m_7286_(GuiGraphics gg, float partialTick, int mouseX, int mouseY) {
      gg.m_280163_(BG, this.f_97735_, this.f_97736_, 0.0F, 0.0F, 176, 199, 176, 199);
      int barX = this.f_97735_ + 148;
      int barY = this.f_97736_ + 44;
      MolecularAssemblerBlockEntity beNow = ((MolecularAssemblerMenu)this.f_97732_).getBlockEntity();
      int filled = beNow == null ? 26 : (int)(26L * beNow.getSyncedEnergy() / Math.max(1, 360000));
      if (filled > 0) {
         gg.m_280163_(TEX_BAR, barX, barY + (26 - filled), 0.0F, 26 - filled, 6, filled, 16, 26);
      }

      gg.m_280163_(TEX_ENERGY_ICON, this.energyIconX(), this.energyIconY(), 0.0F, 0.0F, 23, 26, 23, 26);
   }

   private int sideConfigX() {
      return this.f_97735_ + 54 + this.sideOffX;
   }

   private int sideConfigY() {
      return this.f_97736_ + 58 + this.sideOffY;
   }

   private static String faceName(Direction dir) {
      return switch (dir) {
         case UP -> "顶面";
         case DOWN -> "底面";
         case NORTH -> "北面";
         case SOUTH -> "南面";
         case WEST -> "西面";
         case EAST -> "东面";
         default -> throw new IncompatibleClassChangeError();
      };
   }

   private void renderSideCellIcon(GuiGraphics gg, ItemStack icon, BlockState state, int x, int y) {
      if (!icon.m_41619_()) {
         gg.m_280480_(icon, x, y);
      } else if (state != null && !state.m_60795_() && this.f_96541_ != null) {
         BlockState defaultState = state.m_60734_().m_49966_();
         gg.m_280168_().m_85836_();
         gg.m_280168_().m_252880_(x + 8.0F, y + 8.0F, 100.0F);
         gg.m_280168_().m_85841_(9.0F, -9.0F, 9.0F);
         gg.m_280168_().m_252880_(-0.5F, -0.5F, -0.5F);
         this.f_96541_.m_91289_().renderSingleBlock(defaultState, gg.m_280168_(), gg.m_280091_(), 15728880, OverlayTexture.f_118083_, ModelData.EMPTY, null);
         gg.m_280262_();
         gg.m_280168_().m_85849_();
      }
   }

   private void refreshSideCache() {
      if (this.f_96541_ != null && this.f_96541_.f_91073_ != null) {
         if (((MolecularAssemblerMenu)this.f_97732_).getBlockEntity() != null) {
            BlockPos pos = ((MolecularAssemblerMenu)this.f_97732_).getBlockEntity().m_58899_();

            for (int i = 0; i < 6; i++) {
               BlockState state = this.f_96541_.f_91073_.m_8055_(pos.m_121945_(SIDE_DIRS[i]));
               this.sideStates[i] = state;
               ItemStack icon = state.m_60734_().m_5456_().m_7968_();
               if (icon.m_41619_()) {
                  ResourceLocation key = ForgeRegistries.BLOCKS.getKey(state.m_60734_());
                  if (key != null) {
                     Item byName = (Item)ForgeRegistries.ITEMS.getValue(key);
                     if (byName != null && byName != Items.f_41852_) {
                        icon = new ItemStack(byName);
                     }
                  }
               }

               this.sideIcons[i] = icon;
            }
         }
      }
   }

   private void renderSideConfig(GuiGraphics gg, int mouseX, int mouseY) {
      gg.m_280168_().m_85836_();
      gg.m_280168_().m_252880_(0.0F, 0.0F, 500.0F);
      if (this.f_96541_ != null && this.f_96541_.f_91073_ != null) {
         long now = this.f_96541_.f_91073_.m_46467_();
         if (this.sideCacheTime < 0L || now - this.sideCacheTime >= 10L) {
            this.sideCacheTime = now;
            this.refreshSideCache();
         }
      }

      int px = this.sideConfigX();
      int py = this.sideConfigY();
      gg.m_280163_(TEX_SIDE_BG, px, py, 0.0F, 0.0F, 68, 82, 68, 82);
      MolecularAssemblerBlockEntity be = ((MolecularAssemblerMenu)this.f_97732_).getBlockEntity();

      for (int i = 0; i < 6; i++) {
         int cx = px + SIDE_CELLS[i][0];
         int cy = py + SIDE_CELLS[i][1];
         if (this.f_96541_ != null && this.f_96541_.f_91073_ != null) {
            this.renderSideCellIcon(gg, this.sideIcons[i], this.sideStates[i], cx + 1, cy + 1);
         }

         if (be != null) {
            String mark = switch (be.getSideConfig(SIDE_DIRS[i].ordinal())) {
               case 0 -> "×";
               case 1 -> "←";
               case 2 -> "→";
               default -> "⇄";
            };
            gg.m_280056_(this.f_96547_, mark, cx + 12, cy + 10, 16777215, false);
         }
      }

      if (be != null) {
         for (int i = 0; i < 6; i++) {
            int cxx = px + SIDE_CELLS[i][0];
            int cyx = py + SIDE_CELLS[i][1];
            if (mouseX >= cxx && mouseX < cxx + 18 && mouseY >= cyx && mouseY < cyx + 18) {
               gg.m_280557_(
                  this.f_96547_, Component.m_237113_(faceName(SIDE_DIRS[i]) + "：" + SIDE_NAMES[be.getSideConfig(SIDE_DIRS[i].ordinal())]), mouseX, mouseY
               );
               break;
            }
         }
      }

      gg.m_280168_().m_85849_();
      gg.m_280262_();
   }

   private void renderGhosts(GuiGraphics gg) {
      MolecularAssemblerBlockEntity be = ((MolecularAssemblerMenu)this.f_97732_).getBlockEntity();
      if (be != null) {
         for (int i = 0; i < 9; i++) {
            ItemStack ghost = be.getGhost(i);
            if (!ghost.m_41619_()) {
               int col = i % 3;
               int row = i / 3;
               int x = this.f_97735_ + 29 + col * 18;
               int y = this.f_97736_ + 31 + row * 18;
               gg.m_280480_(ghost, x, y);
               gg.m_280509_(x, y, x + 16, y + 16, -1712920858);
            }
         }
      }
   }

   protected void m_280003_(GuiGraphics gg, int mouseX, int mouseY) {
      gg.m_280614_(this.f_96547_, this.f_96539_, 8, 6, 4210752, false);
   }

   private class TabWidget extends Button {
      private final boolean leftSide;
      private ResourceLocation icon;
      String tooltipKey;

      TabWidget(int x, int y, boolean leftSide, OnPress onPress, Component message) {
         super(x, y, 23, 26, message, onPress, f_252438_);
         this.leftSide = leftSide;
      }

      void setIcon(ResourceLocation icon) {
         this.icon = icon;
      }

      protected void m_87963_(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
         gg.m_280163_(
            this.leftSide ? MolecularAssemblerScreen.TAB_LEFT : MolecularAssemblerScreen.TAB_RIGHT,
            this.m_252754_(),
            this.m_252907_(),
            0.0F,
            0.0F,
            23,
            26,
            23,
            26
         );
         if (this.icon != null) {
            gg.m_280163_(this.icon, this.m_252754_() + 3, this.m_252907_() + 5, 0.0F, 0.0F, 16, 16, 16, 16);
         } else {
            String text = this.m_6035_().getString();
            if (!text.isEmpty()) {
               int tx = this.m_252754_() + (23 - MolecularAssemblerScreen.this.f_96547_.m_92895_(text)) / 2;
               int ty = this.m_252907_() + 9;
               gg.m_280056_(MolecularAssemblerScreen.this.f_96547_, text, tx, ty, 3158064, false);
            }
         }

         if (this.m_198029_()) {
            gg.m_280509_(this.m_252754_() + 1, this.m_252907_() + 1, this.m_252754_() + 23 - 1, this.m_252907_() + 26 - 1, 822083583);
         }
      }
   }
}
