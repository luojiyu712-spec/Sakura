package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.client.music.MusicDimensions;
import com.Luo_jiyv.SakuraMod.client.music.MusicTrack;
import com.Luo_jiyv.SakuraMod.client.music.MusicTracks;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SakuraConfigScreen {
   public static Screen create(Screen parent) {
      ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.m_237115_("config.sakura_mod.title"));
      ConfigEntryBuilder entryBuilder = builder.entryBuilder();
      ConfigCategory gravity = builder.getOrCreateCategory(Component.m_237115_("config.sakura_mod.category.gravity"));
      gravity.addEntry(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.gravity_cannon_break_blocks"), Config.isGravityCannonBreakBlocks())
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.gravity_cannon_break_blocks.tooltip")})
            .setSaveConsumer(Config.GRAVITY_CANNON_BREAK_BLOCKS::set)
            .build()
      );
      gravity.addEntry(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.gravity_attack_enabled"), Config.isGravityAttackEnabled())
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.gravity_attack_enabled.tooltip")})
            .setSaveConsumer(Config.GRAVITY_ATTACK_ENABLED::set)
            .build()
      );
      ConfigCategory loot = builder.getOrCreateCategory(Component.m_237115_("config.sakura_mod.category.loot"));
      loot.addEntry(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.sakura_loot_chest_only_vanilla"), Config.isSakuraLootChestOnlyVanilla())
            .setDefaultValue(false)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.sakura_loot_chest_only_vanilla.tooltip")})
            .setSaveConsumer(Config.SAKURA_LOOT_CHEST_ONLY_VANILLA::set)
            .build()
      );
      loot.addEntry(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.sakura_steel_fortune_bonus"), Config.isSakuraSteelFortuneBonusEnabled())
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.sakura_steel_fortune_bonus.tooltip")})
            .setSaveConsumer(Config.SAKURA_STEEL_FORTUNE_BONUS::set)
            .build()
      );
      ConfigCategory shield = builder.getOrCreateCategory(Component.m_237115_("config.sakura_mod.category.shield_hud"));
      shield.addEntry(
         entryBuilder.startIntField(Component.m_237115_("config.sakura_mod.shield_hud.offset_x"), (Integer)SakuraClientConfig.SHIELD_HUD_OFFSET_X.get())
            .setDefaultValue(0)
            .setMin(-3000)
            .setMax(3000)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.shield_hud.offset_x.tooltip")})
            .setSaveConsumer(SakuraClientConfig.SHIELD_HUD_OFFSET_X::set)
            .build()
      );
      shield.addEntry(
         entryBuilder.startIntField(Component.m_237115_("config.sakura_mod.shield_hud.offset_y"), (Integer)SakuraClientConfig.SHIELD_HUD_OFFSET_Y.get())
            .setDefaultValue(0)
            .setMin(-3000)
            .setMax(3000)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.shield_hud.offset_y.tooltip")})
            .setSaveConsumer(SakuraClientConfig.SHIELD_HUD_OFFSET_Y::set)
            .build()
      );
      ConfigCategory teleporter = builder.getOrCreateCategory(Component.m_237115_("config.sakura_mod.category.random_teleporter"));
      teleporter.addEntry(
         entryBuilder.startIntField(Component.m_237115_("config.sakura_mod.random_teleporter.max_radius_limit"), Config.getRandomTeleporterMaxRadiusLimit())
            .setDefaultValue(10000)
            .setMin(1000)
            .setMax(30000000)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.random_teleporter.max_radius_limit.tooltip")})
            .setSaveConsumer(Config.RANDOM_TELEPORTER_MAX_RADIUS_LIMIT::set)
            .build()
      );
      teleporter.addEntry(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.random_teleporter.async_search"), Config.isRandomTeleporterAsyncSearch())
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.random_teleporter.async_search.tooltip")})
            .setSaveConsumer(Config.RANDOM_TELEPORTER_ASYNC_SEARCH::set)
            .build()
      );
      teleporter.addEntry(
         entryBuilder.startBooleanToggle(
               Component.m_237115_("config.sakura_mod.random_teleporter.enable_biome_search"),
               (Boolean)SakuraClientConfig.RANDOM_TELEPORTER_ENABLE_BIOME_SEARCH.get()
            )
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.random_teleporter.enable_biome_search.tooltip")})
            .setSaveConsumer(SakuraClientConfig.RANDOM_TELEPORTER_ENABLE_BIOME_SEARCH::set)
            .build()
      );
      teleporter.addEntry(
         entryBuilder.startBooleanToggle(
               Component.m_237115_("config.sakura_mod.random_teleporter.enable_structure_search"),
               (Boolean)SakuraClientConfig.RANDOM_TELEPORTER_ENABLE_STRUCTURE_SEARCH.get()
            )
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.random_teleporter.enable_structure_search.tooltip")})
            .setSaveConsumer(SakuraClientConfig.RANDOM_TELEPORTER_ENABLE_STRUCTURE_SEARCH::set)
            .build()
      );
      ConfigCategory anomaly = builder.getOrCreateCategory(Component.m_237115_("config.sakura_mod.category.anomaly"));
      anomaly.addEntry(entryBuilder.startTextDescription(Component.m_237115_("config.sakura_mod.anomaly.intro")).build());
      SubCategoryBuilder anomalyWeather = entryBuilder.startSubCategory(Component.m_237115_("config.sakura_mod.anomaly.weather"));
      anomalyWeather.setExpanded(false);
      anomalyWeather.setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.weather.tooltip")});
      anomalyWeather.add(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.anomaly.weather.mist"), Config.isMistWeatherEnabled())
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.weather.mist.tooltip")})
            .setSaveConsumer(Config.MIST_WEATHER_ENABLED::set)
            .build()
      );
      anomalyWeather.add(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.anomaly.weather.blood_rain"), Config.isBloodRainEnabled())
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.weather.blood_rain.tooltip")})
            .setSaveConsumer(Config.BLOOD_RAIN_ENABLED::set)
            .build()
      );
      anomalyWeather.add(
         entryBuilder.startEnumSelector(
               Component.m_237115_("config.sakura_mod.anomaly.weather.rendering"),
               SakuraClientConfig.WeatherRendering.class,
               SakuraClientConfig.getWeatherRendering()
            )
            .setDefaultValue(SakuraClientConfig.WeatherRendering.AUTO)
            .setEnumNameProvider(mode -> Component.m_237115_("config.sakura_mod.anomaly.weather.rendering." + mode.name().toLowerCase(Locale.ROOT)))
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.weather.rendering.tooltip")})
            .setSaveConsumer(SakuraClientConfig.WEATHER_RENDERING::set)
            .build()
      );
      anomalyWeather.add(
         entryBuilder.startEnumSelector(
               Component.m_237115_("config.sakura_mod.graphics.mode"), SakuraClientConfig.GraphicsMode.class, SakuraClientConfig.getGraphicsCompatibility()
            )
            .setDefaultValue(SakuraClientConfig.GraphicsMode.AUTO)
            .setEnumNameProvider(mode -> Component.m_237115_("config.sakura_mod.graphics.mode." + mode.name().toLowerCase(Locale.ROOT)))
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.graphics.mode.tooltip")})
            .setSaveConsumer(SakuraClientConfig.GRAPHICS_COMPATIBILITY::set)
            .build()
      );
      anomaly.addEntry(anomalyWeather.build());
      SubCategoryBuilder anomalyItems = entryBuilder.startSubCategory(Component.m_237115_("config.sakura_mod.anomaly.items"));
      anomalyItems.setExpanded(false);
      anomalyItems.setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.items.tooltip")});
      anomalyItems.add(entryBuilder.startTextDescription(Component.m_237115_("config.sakura_mod.anomaly.items.golden_cube")).build());
      anomalyItems.add(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.anomaly.items.golden_cube.enabled"), Config.isGoldenCubeEnabled())
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.items.golden_cube.enabled.tooltip")})
            .setSaveConsumer(Config.GOLDEN_CUBE_ENABLED::set)
            .build()
      );
      anomalyItems.add(
         entryBuilder.startBooleanToggle(
               Component.m_237115_("config.sakura_mod.anomaly.items.golden_cube.void_teleport"), Config.isGoldenCubeVoidTeleportEnabled()
            )
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.items.golden_cube.void_teleport.tooltip")})
            .setSaveConsumer(Config.GOLDEN_CUBE_VOID_TELEPORT::set)
            .build()
      );
      anomalyItems.add(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.anomaly.items.golden_cube.indestructible"), Config.isGoldenCubeIndestructible())
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.items.golden_cube.indestructible.tooltip")})
            .setSaveConsumer(Config.GOLDEN_CUBE_INDESTRUCTIBLE::set)
            .build()
      );
      anomalyItems.add(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.anomaly.items.golden_cube.undroppable"), Config.isGoldenCubeUndroppable())
            .setDefaultValue(false)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.items.golden_cube.undroppable.tooltip")})
            .setSaveConsumer(Config.GOLDEN_CUBE_UNDROPPABLE::set)
            .build()
      );
      anomalyItems.add(
         entryBuilder.startDoubleField(Component.m_237115_("config.sakura_mod.anomaly.items.golden_cube.chest_chance"), Config.getGoldenCubeChestChance())
            .setDefaultValue(0.01)
            .setMin(0.0)
            .setMax(1.0)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.items.golden_cube.chest_chance.tooltip")})
            .setSaveConsumer(Config.GOLDEN_CUBE_CHEST_CHANCE::set)
            .build()
      );
      anomalyItems.add(entryBuilder.startTextDescription(Component.m_237115_("config.sakura_mod.anomaly.items.capsule")).build());
      anomalyItems.add(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.anomaly.items.capsule.fire_rate"), Config.isArchiteCapsuleFireRateEnabled())
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.items.capsule.fire_rate.tooltip")})
            .setSaveConsumer(Config.ARCHITE_CAPSULE_FIRE_RATE_ENABLED::set)
            .build()
      );
      anomalyItems.add(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.anomaly.items.capsule.damage"), Config.isArchiteCapsuleDamageEnabled())
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.anomaly.items.capsule.damage.tooltip")})
            .setSaveConsumer(Config.ARCHITE_CAPSULE_DAMAGE_ENABLED::set)
            .build()
      );
      anomalyItems.add(capsuleToggle(entryBuilder, "ads_time", Config.isArchiteCapsuleAdsEnabled(), Config.ARCHITE_CAPSULE_ADS_ENABLED::set));
      anomalyItems.add(capsuleToggle(entryBuilder, "ammo_speed", Config.isArchiteCapsuleAmmoSpeedEnabled(), Config.ARCHITE_CAPSULE_AMMO_SPEED_ENABLED::set));
      anomalyItems.add(
         capsuleToggle(entryBuilder, "armor_ignore", Config.isArchiteCapsuleArmorIgnoreEnabled(), Config.ARCHITE_CAPSULE_ARMOR_IGNORE_ENABLED::set)
      );
      anomalyItems.add(capsuleToggle(entryBuilder, "head_shot", Config.isArchiteCapsuleHeadShotEnabled(), Config.ARCHITE_CAPSULE_HEAD_SHOT_ENABLED::set));
      anomalyItems.add(
         capsuleToggle(entryBuilder, "hipfire_spread", Config.isArchiteCapsuleHipfireSpreadEnabled(), Config.ARCHITE_CAPSULE_HIPFIRE_SPREAD_ENABLED::set)
      );
      anomalyItems.add(capsuleToggle(entryBuilder, "move_spread", Config.isArchiteCapsuleMoveSpreadEnabled(), Config.ARCHITE_CAPSULE_MOVE_SPREAD_ENABLED::set));
      anomalyItems.add(
         capsuleToggle(entryBuilder, "sneak_spread", Config.isArchiteCapsuleSneakSpreadEnabled(), Config.ARCHITE_CAPSULE_SNEAK_SPREAD_ENABLED::set)
      );
      anomalyItems.add(
         capsuleToggle(entryBuilder, "prone_spread", Config.isArchiteCapsuleProneSpreadEnabled(), Config.ARCHITE_CAPSULE_PRONE_SPREAD_ENABLED::set)
      );
      anomalyItems.add(
         capsuleToggle(entryBuilder, "aim_accuracy", Config.isArchiteCapsuleAimAccuracyEnabled(), Config.ARCHITE_CAPSULE_AIM_ACCURACY_ENABLED::set)
      );
      anomalyItems.add(capsuleToggle(entryBuilder, "penetration", Config.isArchiteCapsulePenetrationEnabled(), Config.ARCHITE_CAPSULE_PENETRATION_ENABLED::set));
      anomalyItems.add(
         capsuleToggle(entryBuilder, "recoil_vertical", Config.isArchiteCapsuleRecoilVerticalEnabled(), Config.ARCHITE_CAPSULE_RECOIL_VERTICAL_ENABLED::set)
      );
      anomalyItems.add(
         capsuleToggle(
            entryBuilder, "recoil_horizontal", Config.isArchiteCapsuleRecoilHorizontalEnabled(), Config.ARCHITE_CAPSULE_RECOIL_HORIZONTAL_ENABLED::set
         )
      );
      anomalyItems.add(capsuleToggle(entryBuilder, "weight", Config.isArchiteCapsuleWeightEnabled(), Config.ARCHITE_CAPSULE_WEIGHT_ENABLED::set));
      anomalyItems.add(capsuleToggle(entryBuilder, "knockback", Config.isArchiteCapsuleKnockbackEnabled(), Config.ARCHITE_CAPSULE_KNOCKBACK_ENABLED::set));
      anomalyItems.add(
         capsuleToggle(entryBuilder, "effective_range", Config.isArchiteCapsuleEffectiveRangeEnabled(), Config.ARCHITE_CAPSULE_EFFECTIVE_RANGE_ENABLED::set)
      );
      anomaly.addEntry(anomalyItems.build());
      ConfigCategory music = builder.getOrCreateCategory(Component.m_237115_("config.sakura_mod.category.music"));
      music.addEntry(entryBuilder.startTextDescription(Component.m_237115_("config.sakura_mod.music.intro")).build());
      music.addEntry(entryBuilder.startTextDescription(Component.m_237115_("config.sakura_mod.music.custom.intro")).build());
      music.addEntry(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.music.enabled"), SakuraClientConfig.isCustomMusicEnabled())
            .setDefaultValue(true)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.music.enabled.tooltip")})
            .setSaveConsumer(SakuraClientConfig.MUSIC_ENABLED::set)
            .build()
      );
      music.addEntry(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.music.override_mod_biome"), SakuraClientConfig.shouldOverrideModBiomeMusic())
            .setDefaultValue(false)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.music.override_mod_biome.tooltip")})
            .setSaveConsumer(SakuraClientConfig.MUSIC_OVERRIDE_MOD_BIOME::set)
            .build()
      );
      music.addEntry(
         entryBuilder.startBooleanToggle(Component.m_237115_("config.sakura_mod.music.disable_iss_csw"), SakuraClientConfig.isIssCswMusicDisabled())
            .setDefaultValue(false)
            .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.music.disable_iss_csw.tooltip")})
            .setSaveConsumer(SakuraClientConfig.MUSIC_DISABLE_ISS_CSW::set)
            .build()
      );
      SubCategoryBuilder globalGroup = entryBuilder.startSubCategory(Component.m_237115_("config.sakura_mod.music.section.global"));
      globalGroup.setExpanded(false);
      globalGroup.setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.music.section.global.tooltip")});
      globalGroup.add(entryBuilder.startTextDescription(Component.m_237115_("config.sakura_mod.music.global.intro")).build());
      List<MusicTrack> tracks = MusicTracks.all();
      Set<String> disabledCustomTracks = SakuraClientConfig.getDisabledCustomTracks();

      for (MusicTrack track : tracks) {
         Consumer<Boolean> saveValue;
         if (track.isCustom()) {
            saveValue = enabled -> {
               if (enabled) {
                  disabledCustomTracks.remove(track.id());
               } else {
                  disabledCustomTracks.add(track.id());
               }
            };
         } else {
            saveValue = SakuraClientConfig.MUSIC_TRACK_ENABLED.get(track.id())::set;
         }

         globalGroup.add(
            new MusicPreviewEntry(
               track,
               saveValue,
               SakuraClientConfig.isMusicTrackEnabled(track),
               trackLabel(track),
               () -> Optional.of(new Component[]{trackTooltip(track), Component.m_237115_("config.sakura_mod.music.track.control.tooltip")})
            )
         );
      }

      music.addEntry(globalGroup.build());
      music.addEntry(entryBuilder.startTextDescription(Component.m_237115_("config.sakura_mod.music.dimensions.intro")).build());
      if (!MusicDimensions.isLive()) {
         music.addEntry(entryBuilder.startTextDescription(Component.m_237115_("config.sakura_mod.music.dimensions.fallback")).build());
      }

      Set<String> disabledDimensionTracks = SakuraClientConfig.getDisabledDimensionTracks();

      for (ResourceLocation dimension : MusicDimensions.all()) {
         SubCategoryBuilder group = entryBuilder.startSubCategory(MusicDimensions.label(dimension));
         group.setExpanded(false);
         group.setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.music.dimension.group.tooltip"), Component.m_237113_(dimension.toString())});

         for (MusicTrack track : tracks) {
            String key = SakuraClientConfig.dimensionTrackKey(dimension, track);
            group.add(
               entryBuilder.startBooleanToggle(trackLabel(track), !disabledDimensionTracks.contains(key))
                  .setDefaultValue(true)
                  .setTooltip(new Component[]{Component.m_237115_("config.sakura_mod.music.dimension.track.tooltip")})
                  .setSaveConsumer(value -> {
                     if (value) {
                        disabledDimensionTracks.remove(key);
                     } else {
                        disabledDimensionTracks.add(key);
                     }
                  })
                  .build()
            );
         }

         music.addEntry(group.build());
      }

      builder.setSavingRunnable(() -> {
         SakuraClientConfig.setDisabledCustomTracks(disabledCustomTracks);
         SakuraClientConfig.setDisabledDimensionTracks(disabledDimensionTracks);
         SakuraClientConfig.save();
         Config.save();
      });
      return builder.build();
   }

   private static AbstractConfigListEntry<?> capsuleToggle(ConfigEntryBuilder entryBuilder, String key, boolean value, Consumer<Boolean> save) {
      String base = "config.sakura_mod.anomaly.items.capsule." + key;
      return entryBuilder.startBooleanToggle(Component.m_237115_(base), value)
         .setDefaultValue(true)
         .setTooltip(new Component[]{Component.m_237115_(base + ".tooltip")})
         .setSaveConsumer(save)
         .build();
   }

   private static Component trackLabel(MusicTrack track) {
      MutableComponent label = Component.m_237113_(track.displayName());
      if (track.isCustom()) {
         label.m_130946_("  ").m_7220_(Component.m_237115_("config.sakura_mod.music.custom.label").m_130940_(ChatFormatting.GRAY));
      }

      if (track.tag() != MusicTrack.Tag.NORMAL) {
         label.m_130946_("  ").m_7220_(Component.m_237115_(trackTagKey(track)).m_130940_(ChatFormatting.GRAY));
      }

      return label;
   }

   private static String trackTagKey(MusicTrack track) {
      return "config.sakura_mod.music.tag." + track.tag().name().toLowerCase(Locale.ROOT);
   }

   private static Component trackTooltip(MusicTrack track) {
      return Component.m_237115_(track.tag() == MusicTrack.Tag.NORMAL ? "config.sakura_mod.music.track.tooltip" : trackTagKey(track) + ".tooltip");
   }
}
