package com.Luo_jiyv.SakuraMod.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.phys.Vec3;

final class RandomTeleporterMesh {
   private static final double TURN = Math.PI * 2;
   private static final int ARC_COUNT = 12;
   private static final double SECTOR = Math.PI / 6;
   private static final int DESCENT_TICKS = 9;
   private static final float ASCENT_TICKS = 4.5F;
   private static final double SKY_HEIGHT = 16.0;
   private static final float FLARE_HEIGHT = 6.0F;
   private static final double BEAM_RADIUS = 0.43;
   private static final int COLUMN_SIDES = 40;
   private static final double COLUMN_STEP = Math.PI / 20;
   private static final float[] COLUMN_BANDS = new float[]{0.0F, 0.025F, 0.065F, 0.12F, 0.21F, 0.37F, 0.6F, 0.83F, 1.0F};
   private static final double GOLDEN_ANGLE = 2.399963229728653;
   private static final int CYAN = 4708863;
   private static final int BLUE = 2198527;
   private static final int WHITE = 13433599;
   private static final int WHITE_CORE = 15924735;
   private static final int GOLD = 16770213;
   private static final int VIOLET = 11374847;
   private final VertexConsumer vertices;
   private final float u;
   private final float v;

   RandomTeleporterMesh(VertexConsumer vertices, TextureAtlasSprite sprite) {
      this.vertices = vertices;
      this.u = (sprite.m_118409_() + sprite.m_118410_()) * 0.5F;
      this.v = (sprite.m_118411_() + sprite.m_118412_()) * 0.5F;
   }

   void search(Vec3 origin, float time) {
      double radius = searchRadius(time);
      double spin = time * 0.045;
      float reveal = Mth.m_14036_((time + 1.0F) / 5.0F, 0.0F, 1.0F);
      float pulse = 0.8F + 0.2F * Mth.m_14031_(time * 0.23F);
      this.arc(origin, radius - 0.22, 0.0, Math.PI * 2, 0.04, 48, 4708863, reveal * pulse * 0.76F);

      for (int i = 0; i < 12; i++) {
         double angle = spin + i * (Math.PI / 6);
         this.arc(origin, radius, angle, angle + 0.3979350694547071, 0.075, 4, 2198527, reveal * 0.52F);
         this.arc(origin.m_82520_(0.0, 0.005, 0.0), radius, angle, angle + 0.3979350694547071, 0.025, 4, 13433599, reveal * pulse);
         if (i % 3 == 0) {
            Vec3 notch = polar(origin, radius + 0.13, angle, 0.0);
            this.diamond(notch, angle, 0.064, 0.029, 16770213, reveal * 0.86F);
         }
      }

      float wave = time % 26.0F / 26.0F;
      this.arc(origin, 0.45 + 0.87 * wave, 0.0, Math.PI * 2, 0.028, 48, 4708863, reveal * 0.62F * Mth.m_14031_(wave * (float) Math.PI));
      this.searchOrbits(origin, time, radius, reveal);
   }

   private void searchOrbits(Vec3 origin, float time, double radius, float light) {
      for (int i = 0; i < 6; i++) {
         double angle = i * (Math.PI * 2) / 6.0 - time * 0.055;
         double outer = radius + 0.4;
         int color = i % 2 == 0 ? 11374847 : 4708863;
         this.arc(origin, outer, angle, angle + 0.68, 0.045, 8, color, light * 0.82F);
         Vec3 rune = polar(origin, outer, angle, 0.06);
         this.diamond(rune, angle, 0.115, 0.055, 16770213, light);
         float drift = (time / 31.0F + i / 6.0F) % 1.0F;
         float glow = light * Mth.m_14031_(drift * (float) Math.PI);
         Vec3 star = polar(origin, outer + 0.34 - drift, angle + drift * 1.5, 0.1 + glow * 0.54);
         Vec3 tail = polar(origin, outer + 0.47 - drift, angle + drift * 1.5 - 0.16, 0.08 + glow * 0.48);
         this.beam(tail, star, 0.008, 0.042, color, 0.0F, glow);
         this.glint(star, 0.135, i % 2 == 0 ? 16770213 : 15924735, glow);
      }
   }

   private static double searchRadius(float time) {
      return 1.02 + 0.08 * Mth.m_14031_(time * 0.23F);
   }

   void shatter(Vec3 origin, float time, int searchAge, float progress) {
      double radius = searchRadius(searchAge);
      float fade = (1.0F - progress) * (1.0F - progress);
      if (time < 5.0F) {
         this.searchOrbits(origin, searchAge + time, radius + time * 0.045, 1.0F - time / 5.0F);
      }

      for (int i = 0; i < 12; i++) {
         double angle = searchAge * 0.045 + i * (Math.PI / 6) + 0.19896753472735354;
         double outward = time * (0.044 + i % 4 * 0.012);
         double rise = time * (0.075 + i % 3 * 0.015) - time * time * 0.0035;
         Vec3 center = polar(origin, radius + outward, angle, Math.max(0.0, rise));
         Vec3 tangent = new Vec3(-Math.sin(angle), 0.0, Math.cos(angle));
         Vec3 radial = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
         double roll = time * (i % 2 == 0 ? 0.19 : -0.16);
         Vec3 face = radial.m_82490_(Math.cos(roll)).m_82520_(0.0, Math.sin(roll), 0.0);
         Vec3 tip = center.m_82549_(tangent.m_82490_(0.19));
         Vec3 base = center.m_82546_(tangent.m_82490_(0.17));
         this.quad(
            tip,
            center.m_82546_(face.m_82490_(0.055)),
            base,
            center.m_82549_(face.m_82490_(0.062)),
            i % 3 == 0 ? 16770213 : 4708863,
            fade * 0.88F,
            fade * 0.2F,
            fade * 0.75F,
            fade * 0.26F
         );
         this.quad(
            tip,
            center.m_82549_(face.m_82490_(0.062)),
            base,
            center.m_82546_(face.m_82490_(0.055)),
            i % 3 == 0 ? 16770213 : 4708863,
            fade * 0.88F,
            fade * 0.26F,
            fade * 0.75F,
            fade * 0.2F
         );
         Vec3 spark = polar(origin, radius - 0.22 + outward * 0.59, angle + 0.15, rise * 0.65);
         this.beam(spark, spark.m_82549_(radial.m_82490_(0.14)).m_82520_(0.0, 0.12, 0.0), 0.017, 0.004, 13433599, fade * 0.8F, 0.0F);
      }

      if (time < 6.0F) {
         this.arc(origin, radius - 0.22 + time * 0.047, 0.0, Math.PI * 2, 0.05, 48, 4708863, 0.75F * (1.0F - time / 6.0F));
      }
   }

   void preview(Vec3 origin, float time) {
      this.landingBeam(origin, time);
   }

   void arrival(Vec3 origin, float time) {
      this.landingBeam(origin, time + 13.0F);
   }

   private void landingBeam(Vec3 origin, float time) {
      float remaining = fade(time, 9.0F);
      double bottom = 12.8 * remaining * remaining;
      float contact = time - 9.0F;
      float bloom = ease((contact + 1.5F) / 5.5F);
      float light = 1.0F - ease((time - 13.0F) / 6.0F);
      float spread = ease(1.0F - (float)bottom / 6.0F);
      this.groundLight(origin, 0.43 * (0.88 + 0.18 * spread), bloom * light);
      this.column(origin, time, new RandomTeleporterMesh.Column(bottom, 16.0, 0.43, 1.0F, light * (0.74F + 0.26F * bloom), false));
      this.burst(origin, contact, false);
   }

   void departure(Vec3 origin, float time) {
      float lift = 1.0F - fade(time - 1.6F, 6.0F);
      float ignition = 1.0F - fade(time + 0.5F, 4.5F);
      double bottom = 16.0 * lift;
      double top = Mth.m_14139_(ignition, 0.85, 16.0);
      double radius = 0.43 * (1.0 - 0.28 * lift);
      float light = fade(time - 4.5F, 5.0F);
      float flare = fade(time, 4.0F);
      this.groundLight(origin, radius * 1.06, flare * 0.85F);
      this.column(origin, time, new RandomTeleporterMesh.Column(bottom, top, radius, flare, light, true));
      this.burst(origin, time, true);
      float overhead = time - 4.5F + 0.5F;
      if (overhead >= 0.0F && overhead < 7.0F) {
         float flash = fade(overhead, 7.0F);
         Vec3 crest = origin.m_82520_(0.0, 15.65, 0.0);
         this.arc(crest, 0.35 + overhead * 0.27, 0.0, Math.PI * 2, 0.08, 40, 11374847, flash * 0.65F);
         this.glint(crest, 0.82 + overhead * 0.09, 15924735, flash * 0.95F);
      }
   }

   private void burst(Vec3 origin, float time, boolean rising) {
      if (!(time < 0.0F) && !(time >= 23.0F)) {
         float shock = fade(time, 14.0F);
         double wave = 0.72 + time * 0.27;
         this.arc(origin.m_82520_(0.0, 0.018, 0.0), wave, 0.0, Math.PI * 2, 0.24, 60, 2198527, shock * 0.76F);
         this.arc(origin.m_82520_(0.0, 0.026, 0.0), wave, 0.0, Math.PI * 2, 0.086, 60, 15924735, shock * 0.96F);
         this.arc(origin.m_82520_(0.0, 0.064, 0.0), 0.62 + time * 0.14, 0.0, Math.PI * 2, 0.13, 48, 4708863, shock * 0.65F);
         double crownRadius = 0.6 + time * 0.175;
         Vec3 crown = origin.m_82520_(0.0, 0.27 + time * (rising ? 0.1 : 0.045), 0.0);

         for (int i = 0; i < 12; i++) {
            double angle = i * (Math.PI / 6) + time * (rising ? 0.23 : -0.17);
            this.arc(crown, crownRadius, angle, angle + 0.36128315516282616, 0.13, 5, i % 4 == 0 ? 16770213 : 11374847, shock * 0.9F);
         }

         float ember = fade(time, 23.0F);

         for (int i = 0; i < 32; i++) {
            double angle = i * 2.399963229728653;
            double speed = 0.104 + i % 7 * 0.023;
            double spread = 0.54 + i % 4 * 0.13 + time * speed;
            double verticalSpeed = (rising ? 0.22 : 0.17) + i % 5 * 0.047;
            double y = Math.max(0.08, 0.1 + time * verticalSpeed - time * time * 0.012);
            Vec3 tip = polar(origin, spread, angle, y);
            Vec3 tail = polar(origin, spread - 0.28 - speed, angle, Math.max(0.035, y - 0.18));
            int color = i % 7 == 0 ? 16770213 : (i % 3 == 0 ? 11374847 : 4708863);
            float flicker = 0.78F + 0.22F * Mth.m_14031_(time * 0.88F + i);
            this.beam(tail, tip, 0.012, 0.078, color, 0.0F, ember * flicker * 0.92F);
            if (i % 8 == 0) {
               this.glint(tip, 0.27, color, ember * flicker * 0.93F);
            }
         }

         float arcLight = fade(time, 6.0F);
         if (!(arcLight < 0.12F)) {
            for (int ix = 0; ix < 9; ix++) {
               double angle = ix * 2.399963229728653;
               Vec3 start = polar(origin, 0.62, angle, 0.33);
               float flicker = Mth.m_14031_(time * 1.9F + ix * 2.1F);
               Vec3 bend = polar(origin, 0.92 + time * 0.13, angle + flicker * 0.19, 0.92 + flicker * 0.32);
               Vec3 tip = polar(origin, 1.5 + time * 0.16, angle, 0.5 + ix % 4 * 0.38);
               this.beam(start, bend, 0.063, 0.06, 4708863, arcLight * 0.38F, arcLight * 0.97F);
               this.beam(bend, tip, 0.06, 0.01, 15924735, arcLight * 0.97F, 0.0F);
            }
         }
      }
   }

   private void groundLight(Vec3 origin, double radius, float light) {
      this.glowDisk(origin, radius * 2.55, 4708863, light * 0.62F);
      this.glowDisk(origin.m_82520_(0.0, 0.006, 0.0), radius * 1.62, 15924735, light);
   }

   private void glowDisk(Vec3 center, double radius, int color, float light) {
      if (!(light < 0.1F)) {
         Vec3 inside = center.m_82520_(radius * 0.38, 0.0, 0.0);
         Vec3 edge = center.m_82520_(radius, 0.0, 0.0);

         for (int i = 1; i <= 40; i++) {
            double angle = i * (Math.PI / 20);
            Vec3 nextInside = polar(center, radius * 0.38, angle, 0.0);
            Vec3 nextEdge = polar(center, radius, angle, 0.0);
            this.quad(center, nextInside, inside, inside, color, light, light * 0.8F, light * 0.8F, light * 0.8F);
            this.quad(inside, nextInside, nextEdge, edge, color, light * 0.8F, light * 0.8F, 0.0F, 0.0F);
            inside = nextInside;
            edge = nextEdge;
         }
      }
   }

   private void column(Vec3 origin, float time, RandomTeleporterMesh.Column shape) {
      if (!(shape.top - shape.bottom < 0.08) && !(shape.light < 0.13F)) {
         RandomTeleporterMesh.BeamBand[] bands = new RandomTeleporterMesh.BeamBand[COLUMN_BANDS.length];

         for (int i = 0; i < bands.length; i++) {
            bands[i] = shape.band(COLUMN_BANDS[i]);
         }

         boolean cameraInside = -origin.f_82480_ > shape.bottom && -origin.f_82480_ < shape.top;
         float glow = cameraInside ? 1.0F - 0.48F * (1.0F - ease((float)(origin.m_165924_() / shape.radius))) : 1.0F;
         this.shell(origin, bands, 1.4, 2198527, glow * 0.2F, false);
         this.shell(origin, bands, 1.16, 2198527, glow * 0.33F, false);
         this.shell(origin, bands, 0.89, 4708863, glow * 0.49F, false);
         this.shell(origin, bands, 0.5, 15924735, glow * 0.8F, false);
         this.shell(origin, bands, 0.5, 15924735, glow * 0.8F, true);
         this.shell(origin, bands, 0.89, 4708863, glow * 0.49F, true);
         this.shell(origin, bands, 1.16, 2198527, glow * 0.33F, true);
         this.shell(origin, bands, 1.4, 2198527, glow * 0.2F, true);
         this.streaks(origin, time, shape, bands);
      }
   }

   private void shell(Vec3 origin, RandomTeleporterMesh.BeamBand[] bands, double scale, int color, float light, boolean front) {
      double angleCorrection = Math.cos(0.07853981633974483);

      for (int i = 0; i < 40; i++) {
         double from = i * (Math.PI / 20);
         double to = from + (Math.PI / 20);
         double center = from + 0.07853981633974483;
         double cameraDot = Math.cos(center) * origin.f_82479_ + Math.sin(center) * origin.f_82481_;
         double first = front ? from : to;
         double second = front ? to : from;

         for (int band = 1; band < bands.length; band++) {
            RandomTeleporterMesh.BeamBand lower = bands[band - 1];
            RandomTeleporterMesh.BeamBand upper = bands[band];
            float lowLight = lower.light * light;
            float highLight = upper.light * light;
            if (!(Math.max(lowLight, highLight) < 0.1F)) {
               double slope = (lower.radius - upper.radius) / (upper.y - lower.y);
               double plane = (lower.radius + slope * (origin.f_82480_ + lower.y)) * scale * angleCorrection;
               if (cameraDot + plane < 0.0 == front) {
                  int lowColor = ARGB32.m_269105_(lower.bloom, color, 15924735);
                  int highColor = ARGB32.m_269105_(upper.bloom, color, 15924735);
                  this.vertex(polar(origin, lower.radius * scale, first, lower.y), lowColor, lowLight);
                  this.vertex(polar(origin, upper.radius * scale, first, upper.y), highColor, highLight);
                  this.vertex(polar(origin, upper.radius * scale, second, upper.y), highColor, highLight);
                  this.vertex(polar(origin, lower.radius * scale, second, lower.y), lowColor, lowLight);
               }
            }
         }
      }
   }

   private void streaks(Vec3 origin, float time, RandomTeleporterMesh.Column shape, RandomTeleporterMesh.BeamBand[] bands) {
      for (int i = 0; i < 20; i++) {
         double angle = i * 2.399963229728653 + time * 0.051;
         double offset = 1.27 + i % 5 * 0.13;
         int color = i % 5 == 0 ? 16770213 : (i % 3 == 0 ? 11374847 : 4708863);
         float intensity = 0.56F + 0.3F * Mth.m_14031_(i * 1.9F + time * 1.18F);
         RandomTeleporterMesh.BeamBand lower = bands[0];

         for (int j = 1; j < bands.length; j++) {
            RandomTeleporterMesh.BeamBand upper = bands[j];
            this.beam(
               polar(origin, lower.radius * offset, angle, lower.y),
               polar(origin, upper.radius * offset, angle, upper.y),
               0.034,
               0.028,
               color,
               lower.light * intensity,
               upper.light * intensity
            );
            lower = upper;
         }

         float travel = (time * (0.13F + i % 4 * 0.026F) + i * 0.137F) % 1.0F;
         float headAt = shape.ascending ? travel : 1.0F - travel;
         RandomTeleporterMesh.BeamBand head = shape.band(headAt);
         RandomTeleporterMesh.BeamBand tail = shape.band(Mth.m_14036_(headAt + (shape.ascending ? -0.24F : 0.24F), 0.0F, 1.0F));
         this.beam(
            polar(origin, tail.radius * offset, angle, tail.y), polar(origin, head.radius * offset, angle, head.y), 0.008, 0.055, 15924735, 0.0F, head.light
         );
      }
   }

   private static float ease(float progress) {
      float fraction = Mth.m_14036_(progress, 0.0F, 1.0F);
      return fraction * fraction * (3.0F - 2.0F * fraction);
   }

   private static float fade(float time, float duration) {
      return 1.0F - Mth.m_14036_(time / duration, 0.0F, 1.0F);
   }

   private static Vec3 polar(Vec3 origin, double radius, double angle, double height) {
      return origin.m_82520_(Math.cos(angle) * radius, height, Math.sin(angle) * radius);
   }

   private void arc(Vec3 origin, double radius, double from, double to, double width, int segments, int color, float alpha) {
      if (!(alpha < 0.1F)) {
         Vec3 inner = polar(origin, radius - width, from, 0.0);
         Vec3 middle = polar(origin, radius, from, 0.0);
         Vec3 outer = polar(origin, radius + width, from, 0.0);

         for (int i = 1; i <= segments; i++) {
            double angle = Mth.m_14139_((double)i / segments, from, to);
            Vec3 nextInner = polar(origin, radius - width, angle, 0.0);
            Vec3 nextMiddle = polar(origin, radius, angle, 0.0);
            Vec3 nextOuter = polar(origin, radius + width, angle, 0.0);
            this.quad(inner, nextInner, nextMiddle, middle, color, 0.0F, 0.0F, alpha, alpha);
            this.quad(middle, nextMiddle, nextOuter, outer, color, alpha, alpha, 0.0F, 0.0F);
            inner = nextInner;
            middle = nextMiddle;
            outer = nextOuter;
         }
      }
   }

   private void diamond(Vec3 center, double angle, double length, double width, int color, float alpha) {
      this.quad(
         polar(center, length, angle, 0.0),
         polar(center, width, angle - (Math.PI / 2), 0.0),
         polar(center, length, angle + Math.PI, 0.0),
         polar(center, width, angle + (Math.PI / 2), 0.0),
         color,
         alpha,
         alpha,
         alpha,
         alpha
      );
   }

   private void glint(Vec3 center, double size, int color, float light) {
      if (!(light < 0.1F)) {
         Vec3 across = new Vec3(-center.f_82481_, 0.0, center.f_82479_).m_82541_().m_82490_(size);
         Vec3 up = new Vec3(0.0, size * 1.36, 0.0);
         this.beam(center.m_82546_(across), center, 0.0, size * 0.16, color, 0.0F, light);
         this.beam(center, center.m_82549_(across), size * 0.16, 0.0, color, light, 0.0F);
         this.beam(center.m_82546_(up), center, 0.0, size * 0.16, color, 0.0F, light);
         this.beam(center, center.m_82549_(up), size * 0.16, 0.0, color, light, 0.0F);
      }
   }

   private void beam(Vec3 a, Vec3 b, double widthA, double widthB, int color, float alphaA, float alphaB) {
      Vec3 across = b.m_82546_(a).m_82537_(a.m_82549_(b).m_82490_(-0.5)).m_82541_();
      if (!(across.m_82556_() < 0.5)) {
         Vec3 sideA = across.m_82490_(widthA);
         Vec3 sideB = across.m_82490_(widthB);
         this.quad(a.m_82546_(sideA), a, b, b.m_82546_(sideB), color, 0.0F, alphaA, alphaB, 0.0F);
         this.quad(a, a.m_82549_(sideA), b.m_82549_(sideB), b, color, alphaA, 0.0F, 0.0F, alphaB);
      }
   }

   private void quad(Vec3 a, Vec3 b, Vec3 c, Vec3 d, int color, float aa, float ab, float ac, float ad) {
      this.vertex(a, color, aa);
      this.vertex(b, color, ab);
      this.vertex(c, color, ac);
      this.vertex(d, color, ad);
   }

   private void vertex(Vec3 point, int color, float alpha) {
      this.vertices
         .m_5483_(point.f_82479_, point.f_82480_, point.f_82481_)
         .m_7421_(this.u, this.v)
         .m_6122_(color >> 16, color >> 8 & 0xFF, color & 0xFF, (int)(alpha * 255.0F))
         .m_85969_(15728880)
         .m_5752_();
   }

   private record BeamBand(double y, double radius, float light, float bloom) {
   }

   private record Column(double bottom, double top, double radius, float flare, float light, boolean ascending) {
      RandomTeleporterMesh.BeamBand band(float fraction) {
         float aboveBase = (float)((this.top - this.bottom) * fraction);
         float base = 1.0F - RandomTeleporterMesh.ease(aboveBase / 2.7F);
         float topFade = 1.0F - RandomTeleporterMesh.ease((fraction - 0.81F) / 0.19F);
         float detached = RandomTeleporterMesh.ease((float)this.bottom / 1.4F);
         float baseFade = 1.0F - detached * (1.0F - RandomTeleporterMesh.ease(aboveBase / (this.ascending ? 2.0F : 0.9F)));
         float front = this.ascending ? RandomTeleporterMesh.fade((float)(this.top - this.bottom) - aboveBase, 4.0F) : base;
         float profile = 0.42F + 0.58F * front;
         double y = this.bottom + aboveBase;
         float foot = this.ascending ? this.flare * base : RandomTeleporterMesh.ease(1.0F - (float)y / 6.0F);
         double width = this.radius * (0.82 - 0.2 * fraction + 0.26 * foot);
         return new RandomTeleporterMesh.BeamBand(y, width, this.light * profile * topFade * baseFade, foot);
      }
   }
}
