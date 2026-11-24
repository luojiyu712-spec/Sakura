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

final class ShieldEnergyParticle extends SakuraMeshParticle {
   private static final double TURN = Math.PI * 2;
   private static final int SEGMENTS = 128;
   private final boolean shockwave;
   private final ShieldEnergyParticle.Fragment[] fragments;

   static void register(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet(
         (ParticleType)ModParticles.SHIELD_SHATTER.get(),
         sprites -> (options, level, x, y, z, dx, dy, dz) -> new ShieldEnergyParticle(level, x, y, z, false, sprites)
      );
      event.registerSpriteSet(
         (ParticleType)ModParticles.SHIELD_SHOCKWAVE.get(),
         sprites -> (options, level, x, y, z, dx, dy, dz) -> new ShieldEnergyParticle(level, x, y, z, true, sprites)
      );
   }

   private ShieldEnergyParticle(ClientLevel level, double x, double y, double z, boolean shockwave, SpriteSet sprites) {
      super(level, x, y, z);
      this.shockwave = shockwave;
      this.f_107225_ = shockwave ? 16 : 28;
      this.f_107219_ = false;
      this.m_108337_(sprites.m_5819_(0, 1));
      double extent = shockwave ? 17.0 : 9.0;
      this.m_107259_(new AABB(x, y, z, x, y, z).m_82400_(extent));
      this.fragments = new ShieldEnergyParticle.Fragment[shockwave ? 0 : 52];

      for (int i = 0; i < this.fragments.length; i++) {
         double vertical = 1.0 - 2.0 * (i + 0.5) / this.fragments.length;
         double angle = i * 2.399963229728653;
         double radial = Math.sqrt(1.0 - vertical * vertical);
         Vec3 direction = new Vec3(Math.cos(angle) * radial, vertical, Math.sin(angle) * radial);
         Vec3 side = direction.m_82537_(new Vec3(0.0, 1.0, 0.0)).m_82541_();
         Vec3 up = direction.m_82537_(side).m_82541_();
         this.fragments[i] = new ShieldEnergyParticle.Fragment(
            direction,
            side,
            up,
            0.13 + this.f_107223_.m_188500_() * 0.14,
            0.1 + this.f_107223_.m_188500_() * 0.12,
            (Math.PI * 2) * this.f_107223_.m_188500_(),
            (this.f_107223_.m_188499_() ? 1.0 : -1.0) * (0.06 + this.f_107223_.m_188500_() * 0.12)
         );
      }
   }

   public void m_5989_() {
      if (++this.f_107224_ >= this.f_107225_) {
         this.m_107274_();
      }
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   public void m_5744_(VertexConsumer vertices, Camera camera, float partialTick) {
      Vec3 origin = new Vec3(this.f_107212_, this.f_107213_, this.f_107214_).m_82546_(camera.m_90583_());
      float time = this.f_107224_ + partialTick;
      if (this.shockwave) {
         this.renderWave(vertices, camera, origin, time);
      } else {
         this.renderShards(vertices, camera, origin, time);
      }
   }

   private void renderWave(VertexConsumer vertices, Camera camera, Vec3 origin, float time) {
      double radius = 16.0 * Math.min(1.0, (time + 1.0) / 10.0);
      float fade = Mth.m_14036_((this.f_107225_ - time) / 6.0F, 0.0F, 1.0F);
      Vec3 right = new Vec3(1.0, 0.0, 0.0);
      Vec3 forward = new Vec3(0.0, 0.0, 1.0);
      Vec3 up = new Vec3(0.0, 1.0, 0.0);
      this.ring(vertices, camera, origin, right, forward, radius, 0.32, fade * 0.16F, false);
      this.ring(vertices, camera, origin, right, forward, radius, 0.055, fade * 0.95F, true);
      this.ring(vertices, camera, origin, right, forward, Math.max(0.05, radius - 0.65), 0.09, fade * 0.38F, false);
      this.ring(vertices, camera, origin, right, up, radius, 0.035, fade * 0.32F, false);
      this.ring(vertices, camera, origin, forward, up, radius, 0.035, fade * 0.32F, false);

      for (int i = 0; i < 128; i++) {
         double a = (Math.PI * 2) * i / 128.0;
         double b = (Math.PI * 2) * (i + 1) / 128.0;
         Vec3 p = origin.m_82520_(Math.cos(a) * radius, -0.55, Math.sin(a) * radius);
         Vec3 q = origin.m_82520_(Math.cos(b) * radius, -0.55, Math.sin(b) * radius);
         this.quad(vertices, p, q, q.m_82520_(0.0, 1.1, 0.0), p.m_82520_(0.0, 1.1, 0.0), fade * 0.025F, fade * 0.14F, false);
      }
   }

   private void renderShards(VertexConsumer vertices, Camera camera, Vec3 origin, float time) {
      float fade = Mth.m_14036_((this.f_107225_ - time) / 16.0F, 0.0F, 1.0F);

      for (int i = 0; i < this.fragments.length; i++) {
         ShieldEnergyParticle.Fragment f = this.fragments[i];
         Vec3 center = origin.m_82549_(f.direction().m_82490_(1.05 + f.speed() * time)).m_82520_(0.0, -0.0025 * time * time, 0.0);
         double spin = f.phase() + f.spin() * time;
         Vec3 side = f.side().m_82490_(Math.cos(spin)).m_82549_(f.up().m_82490_(Math.sin(spin)));
         Vec3 up = f.up()
            .m_82490_(Math.cos(spin))
            .m_82546_(f.side().m_82490_(Math.sin(spin)))
            .m_82490_(Math.cos(time * f.spin() * 0.7))
            .m_82549_(f.direction().m_82490_(Math.sin(time * f.spin() * 0.7)));
         Vec3[] outline = new Vec3[5];

         for (int point = 0; point < outline.length; point++) {
            double angle = (Math.PI * 2) * point / outline.length;
            double size = f.size() * (point % 2 == 0 ? 1.0 : 0.68);
            outline[point] = center.m_82549_(side.m_82490_(Math.cos(angle) * size)).m_82549_(up.m_82490_(Math.sin(angle) * size * 1.6));
         }

         for (int point = 0; point < outline.length; point++) {
            Vec3 a = outline[point];
            Vec3 b = outline[(point + 1) % outline.length];
            this.quad(vertices, center, a, b, b, fade * 0.48F, fade * 0.22F, false);
            this.ribbon(vertices, camera, a, b, 0.008, fade * 0.9F, true);
         }

         if (time < 16.0F && i % 2 == 0) {
            Vec3 tail = center.m_82546_(f.direction().m_82490_(0.2 + time * 0.015));
            this.ribbon(vertices, camera, tail, center, 0.013, fade * 0.55F, false);
         }
      }
   }

   private void ring(VertexConsumer vertices, Camera camera, Vec3 origin, Vec3 side, Vec3 up, double radius, double width, float alpha, boolean bright) {
      Vec3 previous = origin.m_82549_(side.m_82490_(radius));

      for (int i = 1; i <= 128; i++) {
         double angle = (Math.PI * 2) * i / 128.0;
         Vec3 current = origin.m_82549_(side.m_82490_(Math.cos(angle) * radius)).m_82549_(up.m_82490_(Math.sin(angle) * radius));
         this.ribbon(vertices, camera, previous, current, width, alpha, bright);
         previous = current;
      }
   }

   private void ribbon(VertexConsumer vertices, Camera camera, Vec3 a, Vec3 b, double width, float alpha, boolean bright) {
      Vec3 side = b.m_82546_(a).m_82537_(a.m_82549_(b).m_82490_(-0.5)).m_82541_();
      if (side.m_82556_() < 1.0E-8) {
         side = new Vec3(camera.m_253028_());
      }

      side = side.m_82490_(width);
      this.quad(vertices, a.m_82546_(side), a.m_82549_(side), b.m_82549_(side), b.m_82546_(side), alpha, alpha, bright);
   }

   private void quad(VertexConsumer vertices, Vec3 a, Vec3 b, Vec3 c, Vec3 d, float lowerAlpha, float upperAlpha, boolean bright) {
      this.vertex(vertices, a, lowerAlpha, bright);
      this.vertex(vertices, b, lowerAlpha, bright);
      this.vertex(vertices, c, upperAlpha, bright);
      this.vertex(vertices, d, upperAlpha, bright);
      this.vertex(vertices, d, upperAlpha, bright);
      this.vertex(vertices, c, upperAlpha, bright);
      this.vertex(vertices, b, lowerAlpha, bright);
      this.vertex(vertices, a, lowerAlpha, bright);
   }

   private void vertex(VertexConsumer vertices, Vec3 point, float alpha, boolean bright) {
      float u = (this.m_5970_() + this.m_5952_()) * 0.5F;
      float v = (this.m_5951_() + this.m_5950_()) * 0.5F;
      vertices.m_5483_(point.f_82479_, point.f_82480_, point.f_82481_)
         .m_7421_(u, v)
         .m_85950_(bright ? 0.78F : 0.12F, bright ? 1.0F : 0.72F, 1.0F, alpha)
         .m_85969_(15728880)
         .m_5752_();
   }

   private record Fragment(Vec3 direction, Vec3 side, Vec3 up, double size, double speed, double phase, double spin) {
   }
}
