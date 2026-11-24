package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.client.music.MusicTrack;
import com.Luo_jiyv.SakuraMod.client.music.MusicTracks;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.slf4j.LoggerFactory;

public class SakuraClientConfig {
   public static final ForgeConfigSpec SPEC;
   public static final IntValue SHIELD_HUD_OFFSET_X;
   public static final IntValue SHIELD_HUD_OFFSET_Y;
   public static final IntValue SHIELD_HUD_WIDTH;
   public static final IntValue SHIELD_HUD_HEIGHT;
   public static final BooleanValue RANDOM_TELEPORTER_ENABLE_BIOME_SEARCH;
   public static final BooleanValue RANDOM_TELEPORTER_ENABLE_STRUCTURE_SEARCH;
   public static final EnumValue<SakuraClientConfig.WeatherRendering> WEATHER_RENDERING;
   public static final EnumValue<SakuraClientConfig.GraphicsMode> GRAPHICS_COMPATIBILITY;
   public static final BooleanValue MUSIC_ENABLED;
   public static final BooleanValue MUSIC_OVERRIDE_MOD_BIOME;
   public static final BooleanValue MUSIC_DISABLE_ISS_CSW;
   public static final ConfigValue<List<? extends String>> MUSIC_DISABLED_DIMENSION_TRACKS;
   public static final ConfigValue<List<? extends String>> MUSIC_DISABLED_CUSTOM_TRACKS;
   public static final Map<String, BooleanValue> MUSIC_TRACK_ENABLED;

   public static boolean isBiomeSearchEnabled() {
      try {
         return (Boolean)RANDOM_TELEPORTER_ENABLE_BIOME_SEARCH.get();
      } catch (IllegalStateException var1) {
         return true;
      }
   }

   public static boolean isStructureSearchEnabled() {
      try {
         return (Boolean)RANDOM_TELEPORTER_ENABLE_STRUCTURE_SEARCH.get();
      } catch (IllegalStateException var1) {
         return true;
      }
   }

   public static SakuraClientConfig.WeatherRendering getWeatherRendering() {
      try {
         return (SakuraClientConfig.WeatherRendering)WEATHER_RENDERING.get();
      } catch (IllegalStateException var1) {
         return SakuraClientConfig.WeatherRendering.AUTO;
      }
   }

   public static SakuraClientConfig.GraphicsMode getGraphicsCompatibility() {
      try {
         return (SakuraClientConfig.GraphicsMode)GRAPHICS_COMPATIBILITY.get();
      } catch (IllegalStateException var1) {
         return SakuraClientConfig.GraphicsMode.AUTO;
      }
   }

   public static boolean isCustomMusicEnabled() {
      try {
         return (Boolean)MUSIC_ENABLED.get();
      } catch (RuntimeException var1) {
         return false;
      }
   }

   public static boolean shouldOverrideModBiomeMusic() {
      try {
         return (Boolean)MUSIC_OVERRIDE_MOD_BIOME.get();
      } catch (IllegalStateException var1) {
         return false;
      }
   }

   public static boolean isIssCswMusicDisabled() {
      try {
         return (Boolean)MUSIC_DISABLE_ISS_CSW.get();
      } catch (RuntimeException var1) {
         return false;
      }
   }

   public static boolean isMusicTrackEnabled(MusicTrack track) {
      BooleanValue value = MUSIC_TRACK_ENABLED.get(track.id());

      try {
         return value != null ? (Boolean)value.get() : !track.isCustom() || !((List)MUSIC_DISABLED_CUSTOM_TRACKS.get()).contains(track.id());
      } catch (RuntimeException var3) {
         return true;
      }
   }

   public static Set<String> getDisabledCustomTracks() {
      try {
         return new HashSet<>((Collection<? extends String>)MUSIC_DISABLED_CUSTOM_TRACKS.get());
      } catch (RuntimeException var1) {
         return new HashSet<>();
      }
   }

   public static void setDisabledCustomTracks(Collection<String> values) {
      MUSIC_DISABLED_CUSTOM_TRACKS.set(values.stream().sorted().toList());
   }

   public static String dimensionTrackKey(ResourceLocation dimension, MusicTrack track) {
      return dimension + "|" + track.id();
   }

   public static Set<String> getDisabledDimensionTracks() {
      Set<String> result = new HashSet<>();

      try {
         for (String raw : (List)MUSIC_DISABLED_DIMENSION_TRACKS.get()) {
            if (raw != null) {
               int separator = raw.indexOf(124);
               if (separator > 0) {
                  String dimension = raw.substring(0, separator).trim();
                  String track = raw.substring(separator + 1).trim();
                  if (!dimension.isEmpty() && !track.isEmpty()) {
                     result.add(dimension + "|" + track);
                  }
               }
            }
         }

         return result;
      } catch (RuntimeException var6) {
         return new HashSet<>();
      }
   }

   public static void setDisabledDimensionTracks(Collection<String> values) {
      try {
         MUSIC_DISABLED_DIMENSION_TRACKS.set(new ArrayList<>(values));
      } catch (RuntimeException var2) {
         LoggerFactory.getLogger("sakura_mod").warn("Failed to update disabledDimensionTracks: {}", var2.getMessage());
      }
   }

   public static void save() {
      try {
         SPEC.save();
      } catch (Exception var1) {
         LoggerFactory.getLogger("sakura_mod").warn("Failed to save client config sakura_mod-client.toml: {}", var1.getMessage());
      }
   }

   static {
      Builder builder = new Builder();
      builder.comment("Shield HUD Settings / 次数盾 HUD 设置").push("shield_hud");
      SHIELD_HUD_OFFSET_X = builder.comment(new String[]{"Horizontal offset from screen center, in GUI pixels. Positive = left.", "相对屏幕中央的水平偏移，正数向左。"})
         .defineInRange("offsetX", 0, -5000, 5000);
      SHIELD_HUD_OFFSET_Y = builder.comment(
            new String[]{
               "Vertical offset from screen bottom minus 74 GUI pixels; independent of health and mounts.",
               "Positive = downward, Negative = upward.",
               "默认距屏幕底部 74 像素，与生命值和坐骑无关。正数向下，负数向上。"
            }
         )
         .defineInRange("offsetY", 0, -5000, 5000);
      SHIELD_HUD_WIDTH = builder.comment("Energy bar width in GUI pixels; cooldown stroke stays 2 pixels.").defineInRange("barWidth", 110, 64, 360);
      SHIELD_HUD_HEIGHT = builder.comment("Energy bar height in GUI pixels.").defineInRange("barHeight", 12, 10, 64);
      builder.pop();
      builder.comment("Random Teleporter / 随机传送器").push("random_teleporter");
      RANDOM_TELEPORTER_ENABLE_BIOME_SEARCH = builder.comment(new String[]{"是否显示群系筛选按钮并启用群系搜索。", "关闭后按钮隐藏，已选群系会在关闭设置界面时清空。"}).define("enableBiomeSearch", true);
      RANDOM_TELEPORTER_ENABLE_STRUCTURE_SEARCH = builder.comment(new String[]{"是否显示结构筛选按钮并启用结构搜索。", "关闭后按钮隐藏，已选结构会在关闭设置界面时清空。"})
         .define("enableStructureSearch", true);
      builder.pop();
      builder.comment("Graphics compatibility / 图形兼容性").push("graphics");
      GRAPHICS_COMPATIBILITY = builder.comment(
            new String[]{
               "AUTO detects OpenGL-to-D3D12/Vulkan translation layers and disables custom shaders.",
               "SAFE always disables custom shaders. OFF forces full effects (may crash on translated drivers).",
               "AUTO：检测到 OpenGL→D3D12/Vulkan 转译层时关闭自定义天气着色器。",
               "SAFE：始终关闭自定义天气着色器。OFF：强制全效果（转译驱动上可能导致崩溃）。",
               "Changing this may require F3+T or a restart to rebuild shaders."
            }
         )
         .defineEnum("compatibilityMode", SakuraClientConfig.GraphicsMode.AUTO);
      builder.pop();
      builder.push("weather");
      WEATHER_RENDERING = builder.comment(
            new String[]{
               "AUTO uses world fog without shaders and depth-tested weather with shaders.",
               "COMPATIBLE always uses depth-tested weather. OFF disables custom weather visuals.",
               "AUTO without an active shader pack and OFF skip custom weather shader loading.",
               "Shader/draw failures disable custom weather until restart; resource reload does not retry.",
               "At most 8 weather shader builds per session; further reloads use vanilla weather until restart.",
               "After enabling COMPATIBLE or a shader pack, reload resources to enable depth-tested weather.",
               "Fast graphics reduces the range of the depth-tested fog."
            }
         )
         .defineEnum("rendering", SakuraClientConfig.WeatherRendering.AUTO);
      builder.pop();
      builder.comment("Custom Background Music / 自定义背景音乐").push("music");
      MUSIC_ENABLED = builder.comment(
            new String[]{"开启樱花音乐播放器（包括背景、进度和天气音乐）。", "Enable the Sakura music player, including background, advancement and weather music."}
         )
         .define("enabled", true);
      MUSIC_OVERRIDE_MOD_BIOME = builder.comment(
            new String[]{
               "开启后用樱花音乐替代群系注册的第三方背景音乐；不影响唱片和 Boss 音乐。", "Replace registered mod biome music with Sakura music. Records and boss music still take priority."
            }
         )
         .define("overrideModBiomeMusic", false);
      MUSIC_DISABLE_ISS_CSW = builder.comment(
            new String[]{
               "兼容性：屏蔽交错织法（iss_csw）的无名巫师 Boss 音乐。",
               "正常情况下樱花背景音乐会自动避让，只在兼容异常时使用。",
               "只有仍然出现冲突时才需要打开此项。",
               "Compatibility: disable iss_csw Nameless Wizard boss music.",
               "Sakura normally yields to this track automatically.",
               "Enable this only if problems remain."
            }
         )
         .define("disableIssCswMusic", false);
      MUSIC_DISABLED_DIMENSION_TRACKS = builder.comment(
            new String[]{
               "按维度关闭的曲目，格式：维度ID|曲目ID。未列出的组合默认开启。",
               "Tracks disabled per dimension, format: dimension_id|track_id. Pairs not listed are enabled.",
               "Example: [\"minecraft:the_nether|sunlight\"]"
            }
         )
         .defineListAllowEmpty("disabledDimensionTracks", List.of(), element -> element instanceof String value && value.indexOf(124) > 0);
      MUSIC_DISABLED_CUSTOM_TRACKS = builder.comment("Globally disabled tracks from the custom OGG folders.")
         .defineListAllowEmpty("disabledCustomTracks", List.of(), element -> element instanceof String id && id.startsWith("custom/"));
      builder.comment("Per-track switches / 单曲开关（取消勾选的曲目不会进入播放列表）").push("tracks");
      Map<String, BooleanValue> trackOptions = new LinkedHashMap<>();

      for (MusicTrack track : MusicTracks.ALL) {
         String[] comment = switch (track.tag()) {
            case NIGHT -> new String[]{track.displayName() + " - night only / 夜间专属", "只在夜晚（13000~23000）播放，入夜后自动插队到下一首。"};
            case COLD -> new String[]{track.displayName() + " - cold biomes only / 寒冷群系专属", "只在寒冷群系（可积雪的温度）播放，进入后自动插队到下一首。"};
            case BATTLE -> new String[]{track.displayName() + " - boss battle / Boss 战专属", "只在 Boss 血条显示、且没有外部 Boss 音乐时播放；Boss 消失后停止。"};
            case ADVANCEMENT -> new String[]{track.displayName() + " - advancement / 进度音乐"};
            case WEATHER -> new String[]{track.displayName() + " - weather / 天气音乐"};
            case NORMAL -> new String[]{track.displayName()};
         };
         trackOptions.put(track.id(), builder.comment(comment).define(track.id(), true));
      }

      MUSIC_TRACK_ENABLED = Collections.unmodifiableMap(trackOptions);
      builder.pop();
      builder.pop();
      SPEC = builder.build();
   }

   public static enum GraphicsMode {
      AUTO,
      SAFE,
      OFF;
   }

   public static enum WeatherRendering {
      AUTO,
      COMPATIBLE,
      OFF;
   }
}
