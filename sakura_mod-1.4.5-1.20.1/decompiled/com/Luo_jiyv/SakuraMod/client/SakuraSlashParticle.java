package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.particle.ModParticles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

final class SakuraSlashParticle extends SakuraMeshParticle {
   private static final int BASE_SEGMENTS = 56;
   private static final float BLADE_TICKS = 12.0F;
   private static final double TURN = Math.PI * 2;
   private static final double START_ANGLE = -1.72;
   private static final double PLANE_TILT = 0.24;
   private static final SakuraSlashParticle.BladeLayer AURA = new SakuraSlashParticle.BladeLayer(1.38, 0.08, -0.16, 0.0, 1.0F, 0.36F, 0.67F, 0.24F, 0.0F, 1.0F);
   private static final SakuraSlashParticle.BladeLayer WAKE = new SakuraSlashParticle.BladeLayer(
      1.16, 0.025, -0.085, 0.12, 1.0F, 0.56F, 0.75F, 0.52F, 0.0F, 1.0F
   );
   private static final SakuraSlashParticle.BladeLayer BODY = new SakuraSlashParticle.BladeLayer(1.0, 0.0, 0.0, 0.2, 1.0F, 1.0F, 1.0F, 0.94F, 0.0F, 1.0F);
   private static final SakuraSlashParticle.BladeLayer EDGE = new SakuraSlashParticle.BladeLayer(
      0.22, 0.015, 0.015, 0.2, 1.0F, 0.96F, 0.99F, 0.94F, 0.0F, 0.56F
   );
   private static final SakuraSlashParticle.BladeLayer FILAMENT = new SakuraSlashParticle.BladeLayer(
      0.085, 0.27, 0.08, 0.0, 1.0F, 0.64F, 0.86F, 0.62F, 0.0F, 0.56F
   );
   private static final SakuraSlashParticle.BladeLayer OUTER_FILAMENT = new SakuraSlashParticle.BladeLayer(
      0.045, 0.46, 0.1, 0.0, 1.0F, 0.87F, 0.65F, 0.45F, 0.0F, 0.56F
   );
   private static final SakuraSlashParticle.BladeLayer[] INNER_FILAMENTS = new SakuraSlashParticle.BladeLayer[]{
      new SakuraSlashParticle.BladeLayer(0.1, -0.55, 0.23, 0.0, 1.0F, 0.82F, 0.96F, 0.6F, 0.0F, 0.56F),
      new SakuraSlashParticle.BladeLayer(0.075, -1.08, 0.25, 0.0, 1.0F, 0.91F, 0.73F, 0.55F, 0.0F, 0.56F),
      new SakuraSlashParticle.BladeLayer(0.055, -1.62, 0.27, 0.0, 1.0F, 0.69F, 0.89F, 0.5F, 0.0F, 0.56F)
   };
   private final Vec3 center;
   private final int ownerId;
   private final Vec3 forward;
   private final Vec3 right;
   private final Vec3 normal;
   private final double maxRadius;
   private final int detailLevel;
   private final int segments;
   private final boolean counterclockwise;
   private final SakuraSlashParticle.Petal[] petals;
   private final SakuraSlashParticle.Glint[] glints;
   private final SakuraSlashParticle.Wind[] winds;

   static void register(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet(
         (ParticleType)ModParticles.SAKURA_SLASH.get(),
         sprites -> (options, level, x, y, z, pitch, yaw, radius) -> new SakuraSlashParticle(
            level, options.ownerId(), x, y, z, (float)pitch, (float)yaw, radius, sprites
         )
      );
   }

   private SakuraSlashParticle(ClientLevel level, int ownerId, double x, double y, double z, float pitch, float yaw, double radius, SpriteSet sprites) {
      super(level, x, y, z);
      this.ownerId = ownerId;
      this.forward = Vec3.m_82498_(pitch, yaw);
      Vec3 horizontal = Vec3.m_82498_(0.0F, yaw);
      Vec3 side = new Vec3(-horizontal.f_82481_, 0.0, horizontal.f_82479_);
      this.center = new Vec3(x, y, z).m_82549_(this.forward.m_82490_(0.4)).m_82549_(side.m_82537_(this.forward).m_82490_(0.25));
      this.counterclockwise = radius < 0.0;
      this.right = side.m_82549_(side.m_82537_(this.forward).m_82490_(this.counterclockwise ? -0.24 : 0.24)).m_82541_();
      this.normal = this.right.m_82537_(this.forward);
      this.maxRadius = Mth.m_14008_(Math.abs(radius), 3.0, 6.0);
      this.detailLevel = Mth.m_14045_((int)Math.round(this.maxRadius - 3.0), 0, 3);
      this.segments = 56 + 12 * this.detailLevel;
      this.petals = new SakuraSlashParticle.Petal[26 + 12 * this.detailLevel];
      this.glints = new SakuraSlashParticle.Glint[14 + 8 * this.detailLevel];
      this.winds = new SakuraSlashParticle.Wind[3 + this.detailLevel];
      this.f_107225_ = 18;
      this.m_108337_(sprites.m_5819_(0, 1));

      for (int i = 0; i < this.petals.length; i++) {
         double angle = (Math.PI * 2) * (i + 0.1 + 0.8 * this.f_107223_.m_188500_()) / this.petals.length;
         this.petals[i] = new SakuraSlashParticle.Petal(
            angle,
            0.08 + 0.8 * this.f_107223_.m_188500_(),
            0.055 + 0.06 * this.f_107223_.m_188500_(),
            (Math.PI * 2) * this.f_107223_.m_188500_(),
            0.16 + 0.5 * this.f_107223_.m_188500_(),
            this.f_107223_.m_188499_() ? 1.0 : -1.0
         );
      }

      for (int i = 0; i < this.glints.length; i++) {
         this.glints[i] = new SakuraSlashParticle.Glint(
            (Math.PI * 2) * (i + this.f_107223_.m_188500_()) / this.glints.length,
            0.035 + 0.04 * this.f_107223_.m_188500_(),
            (Math.PI * 2) * this.f_107223_.m_188500_()
         );
      }

      for (int i = 0; i < this.winds.length; i++) {
         this.winds[i] = new SakuraSlashParticle.Wind(
            (Math.PI * 2) * (i + 0.18) / this.winds.length,
            1.1 + 0.15 * this.detailLevel + 0.4 * this.f_107223_.m_188500_(),
            (Math.PI * 2) * this.f_107223_.m_188500_(),
            (i % 2 == 0 ? 1.0 : -1.0) * (0.14 + 0.04 * this.detailLevel + 0.12 * this.f_107223_.m_188500_())
         );
      }

      this.m_107259_(new AABB(this.center, this.center).m_82400_(this.maxRadius + 2.2));
   }

   public void m_5989_() {
      if (++this.f_107224_ >= this.f_107225_) {
         this.m_107274_();
      }
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   private Vec3 renderOrigin(Camera camera) {
      return this.center.m_82546_(camera.m_90583_());
   }

   public void m_5744_(VertexConsumer vertices, Camera camera, float partialTick) {
      float progress = Mth.m_14036_((this.f_107224_ + partialTick) / 12.0F, 0.0F, 1.0F);
      float lifeProgress = Mth.m_14036_((this.f_107224_ + partialTick) / this.f_107225_, 0.0F, 1.0F);
      double spread = spread(progress);
      double outward = 0.78 * Mth.m_14197_(Mth.m_14008_((progress - 0.3) / 0.7, 0.0, 1.0));
      double sweep = Mth.m_14139_(Math.min(1.0, progress / 0.62), 0.92, Math.PI * 2);
      float opacity = (float)(1.0 - Mth.m_14197_(Mth.m_14008_((progress - 0.35) / 0.65, 0.0, 1.0)));
      Vec3 origin = this.renderOrigin(camera);
      boolean frontFace = origin.m_82526_(this.normal) <= 0.0;
      Vec3 towardCamera = frontFace ? this.normal : this.normal.m_82490_(-1.0);
      if (opacity > 0.0F) {
         this.renderArc(vertices, origin, towardCamera, frontFace, sweep, spread, outward, opacity, AURA);
         this.renderArc(vertices, origin, towardCamera, frontFace, sweep, spread, outward, opacity, WAKE);
         this.renderArc(vertices, origin, towardCamera, frontFace, sweep, spread, outward, opacity, BODY);
         this.renderArc(vertices, origin, towardCamera, frontFace, sweep, spread, outward, opacity, EDGE);
         this.renderArc(vertices, origin, towardCamera, frontFace, sweep, spread, outward, opacity, FILAMENT);
         this.renderArc(vertices, origin, towardCamera, frontFace, sweep, spread, outward, opacity, OUTER_FILAMENT);

         for (int band = 0; band < this.detailLevel; band++) {
            double delayedSweep = Math.max(0.2, sweep - 0.16 * (band + 1));
            this.renderArc(vertices, origin, towardCamera, frontFace, delayedSweep, spread, outward, opacity, INNER_FILAMENTS[band]);
         }

         this.renderGrazingEdge(vertices, camera, origin.m_82549_(towardCamera.m_82490_(0.02)), sweep, spread, outward, opacity);
         this.renderGlints(vertices, camera, origin.m_82549_(towardCamera.m_82490_(0.26)), sweep, spread, outward, progress, opacity);
      }

      this.renderAirflow(vertices, camera, origin, sweep, spread, outward, lifeProgress);
      this.renderPetals(vertices, camera, origin, sweep, spread, outward, lifeProgress);
   }

   private void renderGrazingEdge(VertexConsumer vertices, Camera camera, Vec3 origin, double sweep, double spread, double outward, float opacity) {
      double facing = Math.abs(new Vec3(camera.m_253058_()).m_82526_(this.normal));
      float visibility = (float)(1.0 - Mth.m_14197_(Mth.m_14008_((facing - 0.08) / 0.32, 0.0, 1.0)));
      if (!(visibility <= 0.0F)) {
         Vec3 previous = origin.m_82549_(this.radial(0.0).m_82490_(this.outerRadius(spread, outward, 0.0)));

         for (int i = 1; i <= this.segments; i++) {
            float fraction = (float)i / this.segments;
            float previousFraction = (float)(i - 1) / this.segments;
            Vec3 current = origin.m_82549_(this.radial(sweep * fraction).m_82490_(this.outerRadius(spread, outward, fraction)));
            Vec3 towardCamera = previous.m_82549_(current).m_82490_(-0.5);
            Vec3 side = current.m_82546_(previous).m_82537_(towardCamera).m_82541_();
            Vec3 lift = towardCamera.m_82541_().m_82490_(0.012);
            double previousTaper = Math.pow(Math.sin(Math.PI * previousFraction), 0.5);
            double taper = Math.pow(Math.sin(Math.PI * fraction), 0.5);
            float previousAlpha = opacity * visibility * trailOpacity(previousFraction);
            float alpha = opacity * visibility * trailOpacity(fraction);

            for (int layer = 0; layer < 2; layer++) {
               double width = layer == 0 ? 0.12 + 0.015 * this.maxRadius : 0.045;
               float brightness = layer == 0 ? 0.48F : 0.9F;
               Vec3 a = previous.m_82549_(lift).m_82546_(side.m_82490_(width * previousTaper));
               Vec3 b = previous.m_82549_(lift).m_82549_(side.m_82490_(width * previousTaper));
               Vec3 c = current.m_82549_(lift).m_82549_(side.m_82490_(width * taper));
               Vec3 d = current.m_82549_(lift).m_82546_(side.m_82490_(width * taper));
               float previousU = Mth.m_14179_(previousFraction, this.m_5970_(), this.m_5952_());
               float u = Mth.m_14179_(fraction, this.m_5970_(), this.m_5952_());
               float edgeV = Mth.m_14179_(0.56F, this.m_5951_(), this.m_5950_());
               vertex(vertices, a, previousU, this.m_5951_(), 1.0F, 0.9F, 0.97F, brightness * previousAlpha);
               vertex(vertices, b, previousU, edgeV, 1.0F, 0.9F, 0.97F, brightness * previousAlpha);
               vertex(vertices, c, u, edgeV, 1.0F, 0.9F, 0.97F, brightness * alpha);
               vertex(vertices, d, u, this.m_5951_(), 1.0F, 0.9F, 0.97F, brightness * alpha);
            }

            previous = current;
         }
      }
   }

   private void renderArc(
      VertexConsumer vertices,
      Vec3 origin,
      Vec3 towardCamera,
      boolean frontFace,
      double sweep,
      double spread,
      double outward,
      float opacity,
      SakuraSlashParticle.BladeLayer layer
   ) {
      Vec3 layerOrigin = origin.m_82549_(towardCamera.m_82490_(layer.height()));
      int strips = layer.arch() > 0.0 ? 4 : 1;
      Vec3[] previous = new Vec3[strips + 1];
      Vec3[] current = new Vec3[strips + 1];
      float lastU = 0.0F;
      float lastAlpha = 0.0F;

      for (int i = 0; i <= this.segments; i++) {
         float fraction = (float)(frontFace != this.counterclockwise ? i : this.segments - i) / this.segments;
         double outerRadius = this.outerRadius(spread, outward, fraction) + layer.rim() * Math.sin(Math.PI * fraction);
         double width = this.bladeWidth(fraction, spread) * layer.width() * (1.0 - 0.4 * outward / 0.78);
         Vec3 radial = this.radial(sweep * fraction);
         float u = Mth.m_14179_(fraction, this.m_5970_(), this.m_5952_());
         float alpha = layer.alpha() * opacity * trailOpacity(fraction);

         for (int strip = 0; strip <= strips; strip++) {
            double crossSection = (double)strip / strips;
            double arch = layer.arch() * Math.sin(Math.PI * Math.min(1.0, crossSection * layer.width())) * Math.sin(Math.PI * fraction);
            current[strip] = layerOrigin.m_82549_(radial.m_82490_(outerRadius - width * crossSection)).m_82549_(towardCamera.m_82490_(arch));
         }

         if (i > 0) {
            for (int strip = 0; strip < strips; strip++) {
               float outerV = Mth.m_14179_(Mth.m_14179_((float)strip / strips, layer.outerV(), layer.innerV()), this.m_5951_(), this.m_5950_());
               float innerV = Mth.m_14179_(Mth.m_14179_((float)(strip + 1) / strips, layer.outerV(), layer.innerV()), this.m_5951_(), this.m_5950_());
               vertex(vertices, previous[strip], lastU, outerV, layer.red(), layer.green(), layer.blue(), lastAlpha);
               vertex(vertices, previous[strip + 1], lastU, innerV, layer.red(), layer.green(), layer.blue(), lastAlpha);
               vertex(vertices, current[strip + 1], u, innerV, layer.red(), layer.green(), layer.blue(), alpha);
               vertex(vertices, current[strip], u, outerV, layer.red(), layer.green(), layer.blue(), alpha);
            }
         }

         Vec3[] swap = previous;
         previous = current;
         current = swap;
         lastU = u;
         lastAlpha = alpha;
      }
   }

   private void renderAirflow(VertexConsumer vertices, Camera camera, Vec3 origin, double sweep, double spread, double outward, float lifeProgress) {
      float fade = (float)(1.0 - Mth.m_14197_(Mth.m_14008_((lifeProgress - 0.35) / 0.65, 0.0, 1.0)));

      for (SakuraSlashParticle.Wind wind : this.winds) {
         if (!(wind.angle() >= sweep)) {
            double length = Math.min(wind.length(), sweep - wind.angle());
            double drift = lifeProgress * 0.48;
            Vec3 previous = null;
            float lastAlpha = 0.0F;
            double lastWidth = 0.0;

            for (int i = 0; i <= 20; i++) {
               double t = i / 20.0;
               double envelope = Math.sin(Math.PI * t);
               double phase = t * 8.0 + wind.phase() - lifeProgress * 9.0;
               double angle = wind.angle() + length * t + drift;
               double radius = this.maxRadius * Mth.m_14139_(Math.min(1.0, spread), 0.74, 0.98) + outward + drift * 0.35 + 0.11 * Math.sin(phase) * envelope;
               Vec3 pos = origin.m_82549_(this.radial(angle).m_82490_(radius))
                  .m_82549_(this.normal.m_82490_(wind.lift() * envelope + Math.cos(phase) * 0.16 * envelope));
               float alpha = fade * (float)envelope * 0.48F;
               double width = (0.035 + 0.025 * lifeProgress) * envelope;
               if (previous != null) {
                  SakuraParticleEffects.ribbon(vertices, camera, this.f_108321_, previous, pos, lastWidth, width, lastAlpha, alpha, 1.0F, 0.87F, 0.97F);
               }

               previous = pos;
               lastAlpha = alpha;
               lastWidth = width;
            }
         }
      }
   }

   private void renderPetals(VertexConsumer vertices, Camera camera, Vec3 origin, double sweep, double spread, double outward, float lifeProgress) {
      Vec3 screenSide = new Vec3(camera.m_252775_());
      Vec3 screenUp = new Vec3(camera.m_253028_());
      float opacity = (float)(1.0 - Mth.m_14197_(Mth.m_14008_((lifeProgress - 0.3) / 0.7, 0.0, 1.0)));

      for (SakuraSlashParticle.Petal petal : this.petals) {
         if (petal.angle() >= sweep) {
            break;
         }

         float fraction = (float)(petal.angle() / sweep);
         double birth = Mth.m_14008_((petal.angle() - 0.92) / 5.363185307179586, 0.0, 1.0) * 0.62 * 12.0 / this.f_107225_;
         double drift = Mth.m_14008_((lifeProgress - birth) / (1.0 - birth), 0.0, 1.0);
         float alpha = opacity * (0.55F + 0.45F * trailOpacity(fraction)) * (float)Mth.m_14008_((sweep - petal.angle()) / 0.28, 0.0, 1.0);
         double radius = this.outerRadius(spread, outward, fraction) - petal.inset() * this.bladeWidth(fraction, spread) + 0.48 * drift;
         double eddy = Math.sin(drift * 7.0 + petal.spin());
         Vec3 pos = origin.m_82549_(this.radial(petal.angle() + petal.direction() * drift * 0.28).m_82490_(radius + eddy * 0.1 * drift))
            .m_82549_(this.normal.m_82490_(petal.direction() * (0.12 + petal.lift() * drift) + eddy * 0.16 * drift));
         double spin = petal.spin() + petal.direction() * drift * 3.8;
         double cos = Math.cos(spin) * petal.size();
         double sin = Math.sin(spin) * petal.size();
         double flutter = 0.38 + 0.62 * Math.abs(Math.cos(spin * 1.7));
         Vec3 x = screenSide.m_82490_(cos).m_82549_(screenUp.m_82490_(sin)).m_82490_(0.48 * flutter);
         Vec3 y = screenUp.m_82490_(cos).m_82546_(screenSide.m_82490_(sin));
         this.renderPetal(vertices, pos, x, y, alpha);
      }
   }

   private void renderPetal(VertexConsumer vertices, Vec3 pos, Vec3 x, Vec3 y, float alpha) {
      Vec3[] outline = new Vec3[]{
         pos.m_82546_(y),
         pos.m_82546_(x.m_82490_(0.72)).m_82546_(y.m_82490_(0.38)),
         pos.m_82546_(x).m_82549_(y.m_82490_(0.4)),
         pos.m_82546_(x.m_82490_(0.42)).m_82549_(y),
         pos.m_82549_(y.m_82490_(0.7)),
         pos.m_82549_(x.m_82490_(0.42)).m_82549_(y),
         pos.m_82549_(x).m_82549_(y.m_82490_(0.4)),
         pos.m_82549_(x.m_82490_(0.72)).m_82546_(y.m_82490_(0.38))
      };
      float u = Mth.m_14179_(0.5F, this.m_5970_(), this.m_5952_());
      float v = Mth.m_14179_(0.17F, this.m_5951_(), this.m_5950_());

      for (int i = 0; i < outline.length; i++) {
         Vec3 a = outline[i];
         Vec3 b = outline[(i + 1) % outline.length];
         vertex(vertices, pos, u, v, 1.0F, 0.91F, 0.98F, alpha);
         vertex(vertices, a, u, v, 1.0F, 0.49F, 0.74F, alpha * 0.78F);
         vertex(vertices, b, u, v, 1.0F, 0.65F, 0.84F, alpha * 0.88F);
         vertex(vertices, b, u, v, 1.0F, 0.65F, 0.84F, alpha * 0.88F);
      }
   }

   private void renderGlints(VertexConsumer vertices, Camera camera, Vec3 origin, double sweep, double spread, double outward, float progress, float opacity) {
      Vec3 screenSide = new Vec3(camera.m_252775_());
      Vec3 screenUp = new Vec3(camera.m_253028_());

      for (SakuraSlashParticle.Glint glint : this.glints) {
         if (glint.angle() >= sweep) {
            break;
         }

         float fraction = (float)(glint.angle() / sweep);
         float reveal = (float)Mth.m_14008_((sweep - glint.angle()) / 0.2, 0.0, 1.0);
         float pulse = (float)Math.pow(0.5 + 0.5 * Math.sin(progress * 20.0 + glint.phase()), 2.0);
         float alpha = opacity * reveal * (0.25F + 0.75F * pulse) * trailOpacity(fraction);
         Vec3 pos = origin.m_82549_(this.radial(glint.angle()).m_82490_(this.outerRadius(spread, outward, fraction) + 0.06));
         this.renderStar(vertices, pos, screenSide.m_82490_(glint.size()), screenUp.m_82490_(glint.size() * 2.6), alpha);
      }

      float tipAlpha = opacity * (float)Math.sin(Math.PI * progress);
      Vec3 tip = origin.m_82549_(this.radial(sweep * 0.985).m_82490_(this.outerRadius(spread, outward, 0.985)));
      this.renderStar(vertices, tip, screenSide.m_82490_(0.12), screenUp.m_82490_(0.24), tipAlpha);
   }

   private void renderStar(VertexConsumer vertices, Vec3 pos, Vec3 x, Vec3 y, float alpha) {
      Vec3[] tips = new Vec3[]{pos.m_82546_(x), pos.m_82549_(y), pos.m_82549_(x), pos.m_82546_(y)};
      float u = Mth.m_14179_(0.5F, this.m_5970_(), this.m_5952_());
      float v = Mth.m_14179_(0.17F, this.m_5951_(), this.m_5950_());

      for (int i = 0; i < tips.length; i++) {
         Vec3 a = tips[i];
         Vec3 b = tips[(i + 1) % tips.length];
         vertex(vertices, pos, u, v, 1.0F, 0.98F, 0.88F, alpha);
         vertex(vertices, a, u, v, 1.0F, 0.72F, 0.77F, 0.0F);
         vertex(vertices, b, u, v, 1.0F, 0.72F, 0.77F, 0.0F);
         vertex(vertices, b, u, v, 1.0F, 0.72F, 0.77F, 0.0F);
      }
   }

   private static double spread(float progress) {
      return 1.0 - Math.pow(1.0 - progress, 3.0);
   }

   private Vec3 radial(double offset) {
      double angle = (this.counterclockwise ? -1.0 : 1.0) * (-1.72 + offset);
      return this.forward.m_82490_(Math.cos(angle)).m_82549_(this.right.m_82490_(Math.sin(angle)));
   }

   private static float trailOpacity(float fraction) {
      return 0.24F + 0.76F * fraction;
   }

   private double outerRadius(double spread, double outward, double fraction) {
      return this.maxRadius * Mth.m_14139_(spread, 0.66, 0.94) + outward + 0.18 * Math.sin(Math.PI * fraction);
   }

   private double bladeWidth(double fraction, double spread) {
      double taper = Math.pow(Math.sin(Math.PI * fraction), 0.62);
      double tailWidth = 1.0 + 0.36 * (1.0 - Mth.m_14197_(Math.min(1.0, fraction / 0.58)));
      double openingWidth = 1.0 + 0.16 * (1.0 - Mth.m_14197_(Math.min(1.0, spread / 0.78)));
      return (1.28 + 0.1 * this.maxRadius) * taper * (0.74 + 0.26 * fraction) * tailWidth * openingWidth;
   }

   private static void vertex(VertexConsumer vertices, Vec3 pos, float u, float v, float red, float green, float blue, float alpha) {
      vertices.m_5483_(pos.f_82479_, pos.f_82480_, pos.f_82481_).m_7421_(u, v).m_85950_(red, green, blue, alpha).m_85969_(15728880).m_5752_();
   }

   private record BladeLayer(double width, double rim, double height, double arch, float red, float green, float blue, float alpha, float outerV, float innerV) {
   }

   private record Glint(double angle, double size, double phase) {
   }

   private record Petal(double angle, double inset, double size, double spin, double lift, double direction) {
   }

   private record Wind(double angle, double length, double phase, double lift) {
   }
}
