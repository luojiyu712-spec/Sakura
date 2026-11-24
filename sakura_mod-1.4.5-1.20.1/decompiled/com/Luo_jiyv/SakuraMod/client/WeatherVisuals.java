package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.mojang.blaze3d.pipeline.RenderTarget;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.event.ViewportEvent.ComputeFogColor;
import net.minecraftforge.client.event.ViewportEvent.RenderFog;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.joml.Vector3f;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public final class WeatherVisuals {
   private static final ResourceLocation RAIN = ResourceLocation.withDefaultNamespace("textures/environment/rain.png");
   private static final ResourceLocation SNOW = ResourceLocation.withDefaultNamespace("textures/environment/snow.png");
   static final ResourceLocation BLOOD_RAIN = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/environment/blood_rain.png");
   static final ResourceLocation BLOOD_SNOW = ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/environment/blood_snow.png");
   private static final ParticleOptions BLOOD_SPLASH = new DustParticleOptions(new Vector3f(0.7F, 0.035F, 0.03F), 0.32F);
   private static final Vec3 MIST_COLOR = new Vec3(0.07, 0.07, 0.08);
   private static final Vec3 BLOOD_FOG_COLOR = new Vec3(0.42, 0.06, 0.05);
   private static final Vec3 BLOOD_SKY_COLOR = new Vec3(0.58, 0.07, 0.05);
   private static final Vec3 BLOOD_HAZE_COLOR = new Vec3(0.31, 0.018, 0.015);
   private static final Vec3 MIST_HAZE_COLOR = new Vec3(0.047, 0.05, 0.058);
   private static final float BLOOD_HAZE_DENSITY = 1.3F;
   private static SakuraClientConfig.WeatherRendering renderMode = SakuraClientConfig.WeatherRendering.OFF;
   private static float replacementRain;
   private static boolean rendererFailed;

   private WeatherVisuals() {
   }

   public static boolean inOverworld() {
      ClientLevel level = Minecraft.m_91087_().f_91073_;
      return level != null && Level.f_46428_.equals(level.m_46472_());
   }

   @SubscribeEvent
   public static void onRenderTick(RenderTickEvent event) {
      if (event.phase == Phase.START) {
         try {
            updateRenderMode();
         } catch (LinkageError | RuntimeException var2) {
            failWeatherRenderer(var2);
         }
      }
   }

   static boolean needsWeatherShader() {
      SakuraClientConfig.WeatherRendering selected = SakuraClientConfig.getWeatherRendering();
      return !rendererFailed
         && (
            selected == SakuraClientConfig.WeatherRendering.COMPATIBLE
               || selected == SakuraClientConfig.WeatherRendering.AUTO && WeatherVisuals.ShaderPackCheck.ACTIVE.getAsBoolean()
         );
   }

   private static void updateRenderMode() {
      if (!rendererFailed && renderMode == SakuraClientConfig.WeatherRendering.COMPATIBLE) {
         WeatherFogRenderer.beginFrame();
      }

      replacementRain = 0.0F;
      if (inOverworld() && (BloodRainClientState.hasBloodPrecipitation() || !(MistClientState.getIntensity() <= 0.0F))) {
         SakuraClientConfig.WeatherRendering selected = SakuraClientConfig.getWeatherRendering();
         if (selected == SakuraClientConfig.WeatherRendering.OFF) {
            renderMode = SakuraClientConfig.WeatherRendering.OFF;
         } else if (!rendererFailed && !GraphicsCompatibility.safeMode()) {
            renderMode = selected == SakuraClientConfig.WeatherRendering.AUTO && WeatherVisuals.ShaderPackCheck.ACTIVE.getAsBoolean()
               ? SakuraClientConfig.WeatherRendering.COMPATIBLE
               : selected;
            if (renderMode == SakuraClientConfig.WeatherRendering.COMPATIBLE && !WeatherFogRenderer.isAvailable()) {
               renderMode = SakuraClientConfig.WeatherRendering.AUTO;
            }
         } else {
            renderMode = SakuraClientConfig.WeatherRendering.AUTO;
         }
      } else {
         renderMode = SakuraClientConfig.WeatherRendering.OFF;
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onFogColor(ComputeFogColor event) {
      if (renderMode == SakuraClientConfig.WeatherRendering.AUTO && canTint(event.getCamera())) {
         float rain = bloodRainStrength((float)event.getPartialTick());
         float mist = MistClientState.getIntensity();
         Vec3 color = new Vec3(event.getRed(), event.getGreen(), event.getBlue()).m_165921_(MIST_COLOR, mist).m_165921_(BLOOD_FOG_COLOR, rain);
         event.setRed((float)color.f_82479_);
         event.setGreen((float)color.f_82480_);
         event.setBlue((float)color.f_82481_);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onRenderFog(RenderFog event) {
      float mist = MistClientState.getIntensity() * (1.0F - bloodRainStrength((float)event.getPartialTick()));
      if (renderMode == SakuraClientConfig.WeatherRendering.AUTO && !(mist <= 0.0F) && event.getMode() == FogMode.FOG_TERRAIN && canTint(event.getCamera())) {
         float far = Math.min(event.getFarPlaneDistance(), 64.0F);
         float near = Math.min(event.getNearPlaneDistance(), Math.min(4.0F, far * 0.5F));
         event.setNearPlaneDistance(Mth.m_14179_(mist, event.getNearPlaneDistance(), near));
         event.setFarPlaneDistance(Mth.m_14179_(mist, event.getFarPlaneDistance(), far));
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onRenderWeather(RenderLevelStageEvent event) {
      if (renderMode == SakuraClientConfig.WeatherRendering.COMPATIBLE) {
         try {
            renderWeather(event);
         } catch (LinkageError | RuntimeException var2) {
            failWeatherRenderer(var2);
         }
      }
   }

   private static void renderWeather(RenderLevelStageEvent event) {
      if (!WeatherFogRenderer.isAvailable()) {
         renderMode = SakuraClientConfig.WeatherRendering.AUTO;
         replacementRain = 0.0F;
      } else {
         Camera camera = event.getCamera();
         Stage stage = event.getStage();
         if (stage == Stage.AFTER_SKY) {
            WeatherFogRenderer.captureView(camera, event.getPoseStack());
         } else if (WeatherFogRenderer.hasView(camera)) {
            RenderTarget weatherTarget = null;
            if (stage != Stage.AFTER_LEVEL) {
               if (stage != Stage.AFTER_WEATHER || !Minecraft.m_91085_()) {
                  return;
               }

               weatherTarget = event.getLevelRenderer().m_109831_();
               if (weatherTarget == null) {
                  return;
               }
            }

            float rain = bloodRainStrength(event.getPartialTick());
            float bloodDensity = rain * 1.3F;
            float mistDensity = MistClientState.getIntensity() * (1.0F - rain);
            float density = canTint(camera) ? bloodDensity + mistDensity : 0.0F;

            try {
               if (density > 0.0F || replacementRain > 0.0F) {
                  Vec3 color = density > 0.0F ? MIST_HAZE_COLOR.m_165921_(BLOOD_HAZE_COLOR, bloodDensity / density) : MIST_HAZE_COLOR;
                  WeatherFogRenderer.render(event, density, color, replacementRain, weatherTarget);
               }
            } finally {
               replacementRain = 0.0F;
               WeatherFogRenderer.beginFrame();
            }
         }
      }
   }

   public static Vec3 tintSky(Vec3 color, float partialTick) {
      if (renderMode == SakuraClientConfig.WeatherRendering.AUTO && inOverworld()) {
         float rain = bloodRainStrength(partialTick);
         float mist = MistClientState.getIntensity();
         return color.m_165921_(MIST_COLOR, mist).m_165921_(BLOOD_SKY_COLOR, rain * 0.85F);
      } else {
         return color;
      }
   }

   public static float precipitationStrength(float original) {
      if (!(original <= 0.0F) && renderMode == SakuraClientConfig.WeatherRendering.COMPATIBLE && !rendererFailed) {
         try {
            if (WeatherFogRenderer.isAvailable() && renderBloodRain()) {
               Camera camera = Minecraft.m_91087_().f_91063_.m_109153_();
               if (WeatherFogRenderer.hasView(camera) && canTint(camera)) {
                  replacementRain = original;
                  return 0.0F;
               } else {
                  return original;
               }
            } else {
               return original;
            }
         } catch (LinkageError | RuntimeException var2) {
            failWeatherRenderer(var2);
            return original;
         }
      } else {
         return original;
      }
   }

   private static void failWeatherRenderer(Throwable cause) {
      renderMode = SakuraClientConfig.WeatherRendering.AUTO;
      replacementRain = 0.0F;
      if (!rendererFailed) {
         rendererFailed = true;

         try {
            WeatherFogRenderer.disableRendering(cause);
         } catch (LinkageError | RuntimeException var2) {
            cause.addSuppressed(var2);
            SakuraMod.LOGGER.warn("[sakura-render] weather renderer initialization/cleanup failed; using vanilla weather until restart", cause);
         }
      }
   }

   public static ResourceLocation precipitationTexture(ResourceLocation original) {
      if (renderBloodRain() && (RAIN.equals(original) || SNOW.equals(original))) {
         return RAIN.equals(original) ? BLOOD_RAIN : BLOOD_SNOW;
      } else {
         return original;
      }
   }

   public static ParticleOptions precipitationSplash(ParticleOptions original) {
      return original == ParticleTypes.f_123761_ && renderBloodRain() ? BLOOD_SPLASH : original;
   }

   private static boolean renderBloodRain() {
      return renderMode != SakuraClientConfig.WeatherRendering.OFF && BloodRainClientState.hasBloodPrecipitation() && inOverworld();
   }

   private static boolean canTint(Camera camera) {
      return inOverworld() && camera.m_167685_() == FogType.NONE
         ? !(camera.m_90592_() instanceof LivingEntity living && (living.m_21023_(MobEffects.f_19610_) || living.m_21023_(MobEffects.f_216964_)))
         : false;
   }

   private static float bloodRainStrength(float partialTick) {
      ClientLevel level = Minecraft.m_91087_().f_91073_;
      return level != null && BloodRainClientState.hasBloodPrecipitation() ? level.m_46722_(partialTick) : 0.0F;
   }

   private static final class ShaderPackCheck {
      private static final BooleanSupplier ACTIVE = createCheck();

      private static BooleanSupplier createCheck() {
         ModList mods = ModList.get();
         if (!mods.isLoaded("oculus") && !mods.isLoaded("iris")) {
            return optiFineCheck();
         } else {
            String[] var1 = new String[]{"net.irisshaders.iris.api.v0.IrisApi", "net.coderbot.iris.api.v0.IrisApi"};
            int var2 = var1.length;
            int var3 = 0;

            while (var3 < var2) {
               String name = var1[var3];

               try {
                  Class<?> apiType = Class.forName(name, false, WeatherVisuals.class.getClassLoader());
                  Method isInUse = apiType.getMethod("isShaderPackInUse");
                  Object api = apiType.getMethod("getInstance").invoke(null);
                  return () -> isPackActive(api, isInUse);
               } catch (ClassNotFoundException var8) {
                  var3++;
               } catch (LinkageError | ReflectiveOperationException var9) {
                  return () -> true;
               }
            }

            return () -> true;
         }
      }

      private static boolean isPackActive(Object api, Method isInUse) {
         try {
            return Boolean.TRUE.equals(isInUse.invoke(api));
         } catch (LinkageError | ReflectiveOperationException var3) {
            return true;
         }
      }

      private static BooleanSupplier optiFineCheck() {
         ClassLoader loader = WeatherVisuals.class.getClassLoader();

         try {
            Class.forName("net.optifine.shaders.Shaders", false, loader);
         } catch (ClassNotFoundException var4) {
            boolean installed = ModList.get().isLoaded("optifine");
            return () -> installed;
         } catch (LinkageError var5) {
            return () -> true;
         }

         try {
            Method isShaders = Class.forName("net.optifine.Config", false, loader).getMethod("isShaders");
            return () -> isPackActive(null, isShaders);
         } catch (LinkageError | ReflectiveOperationException var3) {
            return () -> true;
         }
      }
   }
}
