package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public final class GraphicsCompatibility {
   private static final AtomicBoolean DETECTION_SCHEDULED = new AtomicBoolean();
   private static final AtomicBoolean NOTIFIED = new AtomicBoolean();
   private static volatile boolean detected;
   private static volatile GraphicsCompatibility.Backend backend = GraphicsCompatibility.Backend.UNKNOWN_TRANSLATED;
   private static volatile String vendor = "";
   private static volatile String renderer = "";
   private static volatile String version = "";
   private static volatile boolean forcedForLog;

   private GraphicsCompatibility() {
   }

   public static void detectOnClientTick() {
      if (!detected) {
         if (RenderSystem.isOnRenderThread()) {
            detectOnce();
         } else if (DETECTION_SCHEDULED.compareAndSet(false, true)) {
            Minecraft.m_91087_().execute(() -> {
               try {
                  detectOnce();
               } finally {
                  DETECTION_SCHEDULED.set(false);
               }
            });
         }
      }
   }

   public static void detectOnce() {
      if (!detected) {
         if (!RenderSystem.isOnRenderThread()) {
            detectOnClientTick();
         } else {
            synchronized (GraphicsCompatibility.class) {
               if (!detected) {
                  try {
                     vendor = nullToEmpty(GlStateManager._getString(7936));
                     renderer = nullToEmpty(GlStateManager._getString(7937));
                     version = nullToEmpty(GlStateManager._getString(7938));
                     GraphicsCompatibility.Backend forced = forcedBackend();
                     backend = forced != null ? forced : classify(vendor, renderer, version);
                     forcedForLog = forced != null;
                  } catch (LinkageError | RuntimeException var7) {
                     backend = GraphicsCompatibility.Backend.UNKNOWN_TRANSLATED;
                     SakuraMod.LOGGER.warn("[sakura-render] GL info unavailable; using safe mode", var7);
                  } finally {
                     detected = true;
                  }

                  SakuraMod.LOGGER
                     .info(
                        "[sakura-render] backend={} vendor='{}' renderer='{}' version='{}' safe={} forced={}",
                        new Object[]{backend, vendor, renderer, version, safeMode(), forcedForLog}
                     );
               }
            }
         }
      }
   }

   public static boolean isDetected() {
      return detected;
   }

   public static GraphicsCompatibility.Backend backend() {
      detectOnce();
      return backend;
   }

   public static boolean isTranslated() {
      return backend() != GraphicsCompatibility.Backend.NATIVE;
   }

   public static boolean safeMode() {
      SakuraClientConfig.GraphicsMode mode = SakuraClientConfig.getGraphicsCompatibility();
      if (mode == SakuraClientConfig.GraphicsMode.SAFE) {
         return true;
      } else {
         return mode == SakuraClientConfig.GraphicsMode.OFF ? false : isTranslated();
      }
   }

   public static void notifyOnce(LocalPlayer player) {
      if (detected && player != null && isTranslated() && safeMode()) {
         if (NOTIFIED.compareAndSet(false, true)) {
            player.m_5661_(Component.m_237110_("sakura_mod.graphics.safe_mode", new Object[]{backend.name()}).m_130940_(ChatFormatting.YELLOW), false);
         }
      }
   }

   private static GraphicsCompatibility.Backend classify(String vendor, String renderer, String version) {
      if (!renderer.isBlank() && !version.isBlank()) {
         String s = (vendor + " " + renderer + " " + version).toLowerCase(Locale.ROOT);
         if (s.contains("llvmpipe")
            || s.contains("softpipe")
            || s.contains("swiftshader")
            || s.contains("microsoft basic render")
            || s.contains("software rasterizer")) {
            return GraphicsCompatibility.Backend.SOFTWARE;
         } else if (s.contains("zink") || s.contains("dozen")) {
            return GraphicsCompatibility.Backend.ZINK;
         } else if (s.contains("d3d12") || s.contains("direct3d12") || s.contains("glon12")) {
            return GraphicsCompatibility.Backend.MESA_D3D12;
         } else if (s.contains("angle") || s.contains("direct3d11")) {
            return GraphicsCompatibility.Backend.ANGLE;
         } else if (s.contains("microsoft")) {
            return GraphicsCompatibility.Backend.UNKNOWN_TRANSLATED;
         } else {
            return isWindows() && s.contains("mesa") ? GraphicsCompatibility.Backend.UNKNOWN_TRANSLATED : GraphicsCompatibility.Backend.NATIVE;
         }
      } else {
         return GraphicsCompatibility.Backend.UNKNOWN_TRANSLATED;
      }
   }

   private static boolean isWindows() {
      return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
   }

   private static GraphicsCompatibility.Backend forcedBackend() {
      String value = System.getProperty("sakura.render.forceBackend", "").trim();
      if (value.isEmpty()) {
         return null;
      } else {
         try {
            return GraphicsCompatibility.Backend.valueOf(value.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var2) {
            SakuraMod.LOGGER.warn("[sakura-render] ignoring invalid sakura.render.forceBackend='{}'", value);
            return null;
         }
      }
   }

   private static String nullToEmpty(String value) {
      return value == null ? "" : value;
   }

   public static enum Backend {
      NATIVE,
      MESA_D3D12,
      ZINK,
      ANGLE,
      SOFTWARE,
      UNKNOWN_TRANSLATED;
   }
}
