package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

final class WeatherFogRenderer {
   private static final int CELL_SIZE = 16;
   private static final int BUFFER_SIZE = 32768;
   private static final int MAX_SHADER_BUILDS = 8;
   private static final Matrix4f VIEW = new Matrix4f();
   private static final Matrix4f CLIP = new Matrix4f();
   private static final FrustumIntersection FRUSTUM = new FrustumIntersection();
   private static final Vec3 PRECIPITATION_COLOR = new Vec3(0.78, 0.92, 0.92);
   private static ShaderInstance shader;
   private static Camera capturedCamera;
   private static boolean renderDisabled;
   private static int shaderBuilds;
   private static boolean fogVerified;
   private static boolean rainVerified;
   private static boolean snowVerified;
   private static boolean firstDrawLogged;
   private static boolean drawResourcesStarted;

   private WeatherFogRenderer() {
   }

   static void registerShader(RegisterShadersEvent event) {
      GraphicsCompatibility.detectOnce();
      shader = null;
      beginFrame();

      boolean needed;
      try {
         needed = !renderDisabled && !GraphicsCompatibility.safeMode() && WeatherVisuals.needsWeatherShader();
      } catch (LinkageError | RuntimeException var8) {
         renderDisabled = true;
         SakuraMod.LOGGER.warn("[sakura-render] weather shader selection failed; using vanilla weather until restart", var8);
         return;
      }

      if (!needed) {
         SakuraMod.LOGGER
            .info(
               "[sakura-render] custom weather shader skipped: safe={} disabled={} mode={} backend={}",
               new Object[]{GraphicsCompatibility.safeMode(), renderDisabled, SakuraClientConfig.getWeatherRendering(), GraphicsCompatibility.backend()}
            );
      } else if (shaderBuilds >= 8) {
         renderDisabled = true;
         SakuraMod.LOGGER.warn("[sakura-render] weather shader build limit ({}) reached; using vanilla weather until restart", 8);
      } else {
         shaderBuilds++;
         SakuraMod.LOGGER.info("[sakura-render] building custom weather shader: attempt={} backend={}", shaderBuilds, GraphicsCompatibility.backend());
         ResourceLocation id = ResourceLocation.fromNamespaceAndPath("sakura_mod", "weather");
         ShaderInstance candidate = null;

         try {
            candidate = new ShaderInstance(event.getResourceProvider(), id, DefaultVertexFormat.f_85819_);
            if (GlStateManager.glGetProgrami(candidate.m_108943_(), 35714) == 0) {
               throw new IOException("Could not link Sakura weather shader: " + GlStateManager.glGetProgramInfoLog(candidate.m_108943_(), 32768));
            }

            event.registerShader(candidate, loaded -> {
               shader = loaded;
               fogVerified = false;
               rainVerified = false;
               snowVerified = false;
               firstDrawLogged = false;
               SakuraMod.LOGGER.info("[sakura-render] custom weather shader ready: backend={}", GraphicsCompatibility.backend());
            });
         } catch (RuntimeException | LinkageError | IOException var7) {
            if (candidate != null) {
               try {
                  candidate.close();
               } catch (LinkageError | RuntimeException var6) {
                  var7.addSuppressed(var6);
               }
            }

            renderDisabled = true;
            SakuraMod.LOGGER
               .warn(
                  "[sakura-render] weather shader failed; using vanilla fog and precipitation until the client restarts (resource reload will not retry).",
                  var7
               );
         }
      }
   }

   static boolean isAvailable() {
      return !GraphicsCompatibility.safeMode() && shader != null && !renderDisabled && (!Minecraft.m_91085_() || !GraphicsCompatibility.isTranslated());
   }

   static void disableRendering(Throwable cause) {
      if (!renderDisabled) {
         renderDisabled = true;
         beginFrame();
         if (drawResourcesStarted) {
            cleanup(() -> WeatherFogRenderer.DrawResources.FOG.m_110188_(), cause);
            cleanup(() -> WeatherFogRenderer.DrawResources.RAIN.m_110188_(), cause);
            cleanup(() -> WeatherFogRenderer.DrawResources.SNOW.m_110188_(), cause);
            cleanup(BufferUploader::m_166835_, cause);
         }

         SakuraMod.LOGGER.warn("[sakura-render] custom weather rendering failed; using vanilla fog and precipitation until the client restarts.", cause);
      }
   }

   private static void cleanup(Runnable action, Throwable cause) {
      try {
         action.run();
      } catch (LinkageError | RuntimeException var3) {
         if (var3 != cause) {
            cause.addSuppressed(var3);
         }
      }
   }

   static void beginFrame() {
      capturedCamera = null;
   }

   static void captureView(Camera camera, PoseStack worldPose) {
      VIEW.set(worldPose.m_85850_().m_252922_());
      capturedCamera = camera;
   }

   static boolean hasView(Camera camera) {
      return capturedCamera == camera;
   }

   static void render(RenderLevelStageEvent event, float density, Vec3 color, float rain, @Nullable RenderTarget weatherTarget) {
      if (isAvailable() && !GraphicsCompatibility.safeMode()) {
         if (weatherTarget == null || !GraphicsCompatibility.isTranslated()) {
            Minecraft minecraft = Minecraft.m_91087_();
            Camera camera = event.getCamera();
            double time = (double)event.getRenderTick() + event.getPartialTick();
            if (!firstDrawLogged) {
               firstDrawLogged = true;
               SakuraMod.LOGGER
                  .info(
                     "[sakura-render] first custom weather draw: backend={} fabulous={} targetSwitch={}",
                     new Object[]{GraphicsCompatibility.backend(), Minecraft.m_91085_(), weatherTarget != null}
                  );
            }

            try {
               if (weatherTarget != null) {
                  VIEW.identity();
                  minecraft.m_91385_().m_83947_(false);
               }

               RenderSystem.getProjectionMatrix().mul(RenderSystem.getModelViewMatrix(), CLIP).mul(VIEW);
               FRUSTUM.set(CLIP, true);
               if (density > 0.0F) {
                  float maxDistance = Minecraft.m_91405_() ? 72.0F : 48.0F;
                  float far = Math.min(maxDistance, minecraft.f_91066_.m_193772_() * 16.0F);
                  renderFog(density, color, camera, far, time);
               }

               if (rain > 0.0F) {
                  renderPrecipitation(minecraft.f_91073_, camera.m_90583_(), camera.m_252775_(), rain, time);
               }
            } finally {
               if (weatherTarget != null) {
                  weatherTarget.m_83947_(false);
               }
            }
         }
      }
   }

   private static void renderFog(float density, Vec3 color, Camera camera, float far, double time) {
      Vec3 eye = camera.m_90583_();
      Vector3f look = camera.m_253058_();
      Vector3f left = camera.m_252775_();
      Vector3f up = camera.m_253028_();
      float padding = far + 9.0F;
      double windX = Math.sin(time * 0.012) * 3.0;
      double windZ = Math.cos(time * 0.00936) * 2.5;
      boolean verify = !fogVerified;
      VertexConsumer vertices = null;
      double farSquared = far * far;
      int endX = cell(eye.f_82479_ + padding);
      int startY = cell(eye.f_82480_ - padding);
      int endY = cell(eye.f_82480_ + padding);
      int startZ = cell(eye.f_82481_ - padding);
      int endZ = cell(eye.f_82481_ + padding);

      for (int x = cell(eye.f_82479_ - padding); x <= endX; x++) {
         for (int y = startY; y <= endY; y++) {
            for (int z = startZ; z <= endZ; z++) {
               long seed = Mth.m_14130_(x, y, z);
               float dx = (float)((x + 0.5) * 16.0 + jitter(seed, 0) * 11.0 + windX - eye.f_82479_);
               float dy = (float)((y + 0.5) * 16.0 + jitter(seed, 11) * 9.0 - eye.f_82480_);
               float dz = (float)((z + 0.5) * 16.0 + jitter(seed, 22) * 11.0 + windZ - eye.f_82481_);
               float distanceSquared = dx * dx + dy * dy + dz * dz;
               if (!(distanceSquared >= farSquared) && !(dx * look.x() + dy * look.y() + dz * look.z() <= 1.0F)) {
                  float distance = Mth.m_14116_(distanceSquared);
                  double nearby = Mth.m_14197_(Mth.m_14036_((distance - 6.0F) / 16.0F, 0.0F, 1.0F));
                  double distant = Mth.m_14197_(Mth.m_14036_((far - distance) / (far * 0.34F), 0.0F, 1.0F));
                  float alpha = (float)(0.62F * density * nearby * distant);
                  if (!(alpha < 0.012F)) {
                     float halfWidth = 18.0F + jitter(seed, 33) * 7.0F;
                     float halfHeight = 13.0F + jitter(seed, 41) * 4.0F;
                     float radius = Mth.m_14116_(halfWidth * halfWidth + halfHeight * halfHeight);
                     if (FRUSTUM.testSphere(dx, dy, dz, radius)) {
                        if (vertices == null) {
                           if (verify) {
                              checkGlErrors("before-fog", false);
                           }

                           drawResourcesStarted = true;
                           vertices = WeatherFogRenderer.DrawResources.BUFFERS.m_6299_(WeatherFogRenderer.DrawResources.FOG);
                        }

                        drawCloud(vertices, color, alpha, dx, dy, dz, halfWidth, halfHeight, left, up);
                     }
                  }
               }
            }
         }
      }

      if (vertices != null) {
         WeatherFogRenderer.DrawResources.BUFFERS.m_109912_(WeatherFogRenderer.DrawResources.FOG);
      }

      if (verify && vertices != null) {
         checkGlErrors("after-fog", true);
         fogVerified = true;
      }
   }

   private static void renderPrecipitation(ClientLevel level, Vec3 eye, Vector3f left, float strength, double time) {
      boolean verify = false;
      boolean emittedRain = false;
      boolean emittedSnow = false;
      int centerX = Mth.m_14107_(eye.f_82479_);
      int centerY = Mth.m_14107_(eye.f_82480_);
      int centerZ = Mth.m_14107_(eye.f_82481_);
      int radius = Minecraft.m_91405_() ? 10 : 5;
      float reach = radius + 0.5F;
      float reachSquared = reach * reach;
      float top = (float)(centerY + radius - eye.f_82480_);
      MutableBlockPos pos = new MutableBlockPos();

      for (int x = centerX - radius; x <= centerX + radius; x++) {
         for (int z = centerZ - radius; z <= centerZ + radius; z++) {
            float dx = (float)(x + 0.5 - eye.f_82479_);
            float dz = (float)(z + 0.5 - eye.f_82481_);
            float distanceSquared = dx * dx + dz * dz;
            if (!(distanceSquared >= reachSquared) && level.m_46805_(pos.m_122178_(x, centerY, z))) {
               int ground = level.m_6924_(Types.MOTION_BLOCKING, x, z);
               int bottomY = Math.max(centerY - radius, ground);
               float bottom = (float)(bottomY - eye.f_82480_);
               if (!(bottom >= top)) {
                  float halfHeight = (top - bottom) * 0.5F;
                  if (FRUSTUM.testSphere(dx, bottom + halfHeight, dz, halfHeight + 0.5F)) {
                     Biome biome = (Biome)level.m_204166_(pos).m_203334_();
                     Precipitation precipitation = biome.m_264600_(pos.m_142448_(bottomY));
                     if (precipitation != Precipitation.NONE) {
                        boolean snow = precipitation == Precipitation.SNOW;
                        if (!verify && (snow ? !snowVerified : !rainVerified)) {
                           checkGlErrors("before-precipitation", false);
                           verify = true;
                        }

                        drawResourcesStarted = true;
                        VertexConsumer vertices = WeatherFogRenderer.DrawResources.BUFFERS
                           .m_6299_(snow ? WeatherFogRenderer.DrawResources.SNOW : WeatherFogRenderer.DrawResources.RAIN);
                        if (snow) {
                           emittedSnow = true;
                        } else {
                           emittedRain = true;
                        }

                        float distance = Mth.m_14116_(distanceSquared);
                        float alpha = strength * 0.76F * Mth.m_14036_((reach - distance) / 3.0F, 0.0F, 1.0F);
                        float hx = distance > 0.01F ? -0.5F * dz / distance : left.x() * 0.5F;
                        float hz = distance > 0.01F ? 0.5F * dx / distance : left.z() * 0.5F;
                        long seed = Mth.m_14130_(x, 0, z);
                        float speed = snow ? 0.018F : 0.096F + 0.04F * jitter(seed, 11);
                        float uvTop = (float)Mth.m_14185_(jitter(seed, 0) - time * speed - (centerY + radius) * 0.25);
                        float uvBottom = uvTop + (top - bottom) * 0.25F;
                        vertex(vertices, PRECIPITATION_COLOR, alpha, dx - hx, bottom, dz - hz, 0.0F, uvBottom);
                        vertex(vertices, PRECIPITATION_COLOR, alpha, dx - hx, top, dz - hz, 0.0F, uvTop);
                        vertex(vertices, PRECIPITATION_COLOR, alpha, dx + hx, top, dz + hz, 1.0F, uvTop);
                        vertex(vertices, PRECIPITATION_COLOR, alpha, dx + hx, bottom, dz + hz, 1.0F, uvBottom);
                     }
                  }
               }
            }
         }
      }

      if (emittedRain || emittedSnow) {
         WeatherFogRenderer.DrawResources.BUFFERS.m_109911_();
      }

      if (verify && (emittedRain || emittedSnow)) {
         checkGlErrors("after-precipitation", true);
         rainVerified |= emittedRain;
         snowVerified |= emittedSnow;
      }
   }

   private static void checkGlErrors(String phase, boolean fail) {
      int first = GlStateManager._getError();
      if (first != 0) {
         int count = 1;

         boolean contextLost;
         for (contextLost = first == 1287; count < 16; count++) {
            int next = GlStateManager._getError();
            if (next == 0) {
               break;
            }

            contextLost |= next == 1287;
         }

         String message = "[sakura-render] GL error phase=" + phase + " first=0x" + Integer.toHexString(first) + " count=" + count;
         if (!fail && !contextLost && count != 16) {
            SakuraMod.LOGGER.warn("{} (pre-existing GL errors)", message);
         } else {
            throw new IllegalStateException(message);
         }
      }
   }

   private static int cell(double position) {
      return Mth.m_14107_(position / 16.0);
   }

   private static float jitter(long seed, int shift) {
      return (float)(seed >>> shift & 255L) / 255.0F - 0.5F;
   }

   private static void drawCloud(
      VertexConsumer vertices, Vec3 color, float alpha, float x, float y, float z, float halfWidth, float halfHeight, Vector3f left, Vector3f up
   ) {
      float hx = left.x() * halfWidth;
      float hy = left.y() * halfWidth;
      float hz = left.z() * halfWidth;
      float vx = up.x() * halfHeight;
      float vy = up.y() * halfHeight;
      float vz = up.z() * halfHeight;
      vertex(vertices, color, alpha, x - hx - vx, y - hy - vy, z - hz - vz, 0.0F, 1.0F);
      vertex(vertices, color, alpha, x - hx + vx, y - hy + vy, z - hz + vz, 0.0F, 0.0F);
      vertex(vertices, color, alpha, x + hx + vx, y + hy + vy, z + hz + vz, 1.0F, 0.0F);
      vertex(vertices, color, alpha, x + hx - vx, y + hy - vy, z + hz - vz, 1.0F, 1.0F);
   }

   private static void vertex(VertexConsumer vertices, Vec3 color, float alpha, float x, float y, float z, float u, float v) {
      vertices.m_252986_(VIEW, x, y, z).m_7421_(u, v).m_85950_((float)color.f_82479_, (float)color.f_82480_, (float)color.f_82481_, alpha).m_5752_();
   }

   private static final class DrawResources {
      private static final RenderType FOG = WeatherFogRenderer.WeatherRenderType.create(
         "sakura_weather_fog", ResourceLocation.fromNamespaceAndPath("sakura_mod", "textures/environment/mist_cloud.png"), true, false
      );
      private static final RenderType RAIN = WeatherFogRenderer.WeatherRenderType.create("sakura_weather_rain", WeatherVisuals.BLOOD_RAIN, false, true);
      private static final RenderType SNOW = WeatherFogRenderer.WeatherRenderType.create("sakura_weather_snow", WeatherVisuals.BLOOD_SNOW, false, true);
      private static final BufferSource BUFFERS = MultiBufferSource.m_109900_(
         Map.of(RAIN, new BufferBuilder(32768), SNOW, new BufferBuilder(32768)), new BufferBuilder(32768)
      );
   }

   private static final class WeatherRenderType extends RenderStateShard {
      private static final ShaderStateShard SHADER = new ShaderStateShard(() -> WeatherFogRenderer.shader);

      private WeatherRenderType() {
         super("sakura_weather", () -> {}, () -> {});
      }

      static RenderType create(String name, ResourceLocation texture, boolean blur, boolean sort) {
         return RenderType.m_173215_(
            name,
            DefaultVertexFormat.f_85819_,
            Mode.QUADS,
            32768,
            false,
            sort,
            CompositeState.m_110628_()
               .m_173292_(SHADER)
               .m_173290_(new TextureStateShard(texture, blur, false))
               .m_110685_(f_110139_)
               .m_110661_(f_110110_)
               .m_110663_(f_110113_)
               .m_110687_(f_110115_)
               .m_110691_(false)
         );
      }
   }
}
