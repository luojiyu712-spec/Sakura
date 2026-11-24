package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.combat.SakuraAxeSlashShape;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

final class SakuraAxeTrailParticle extends SakuraMeshParticle {
   private static final int SAMPLES = 5;
   private static final int FADE_TICKS = 7;
   private final Vec3[] positions = new Vec3[5];
   private final int[] flightAges = new int[5];
   private final Vec3 forward;
   private final Vec3 up;
   private int count;
   private int idleTicks;
   private int fadeAge;
   private boolean finished;

   SakuraAxeTrailParticle(ClientLevel level, Vec3 origin, Vec3 forward, Vec3 up, SpriteSet sprites) {
      super(level, origin.f_82479_, origin.f_82480_, origin.f_82481_);
      this.forward = forward;
      this.up = up;
      this.f_107219_ = false;
      this.f_107225_ = 21;
      this.m_108337_(sprites.m_5819_(1, 3));
      this.append(origin, 0);
   }

   void append(Vec3 position, int flightAge) {
      if (!this.finished && this.m_107276_()) {
         if (this.count == 5) {
            System.arraycopy(this.positions, 1, this.positions, 0, 4);
            System.arraycopy(this.flightAges, 1, this.flightAges, 0, 4);
            this.count--;
         }

         this.positions[this.count] = position;
         this.flightAges[this.count++] = flightAge;
         this.idleTicks = 0;
         this.m_107264_(position.f_82479_, position.f_82480_, position.f_82481_);
         AABB bounds = new AABB(this.positions[0], position);

         for (int i = 1; i < this.count; i++) {
            bounds = bounds.m_82367_(new AABB(this.positions[i], this.positions[i]));
         }

         this.m_107259_(bounds.m_82400_(3.0));
      }
   }

   void finish() {
      this.finished = true;
   }

   public void m_5989_() {
      if (++this.idleTicks > 2) {
         this.finish();
      }

      if (++this.f_107224_ >= this.f_107225_ || this.finished && ++this.fadeAge >= 7) {
         this.m_107274_();
      }
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   private Vec3 sample(int index, float partialTick) {
      return !this.finished && this.count > 1 && index == this.count - 1
         ? this.positions[index - 1].m_165921_(this.positions[index], partialTick)
         : this.positions[index];
   }

   public void m_5744_(VertexConsumer vertices, Camera camera, float partialTick) {
      if (this.count >= 2) {
         double fade = this.finished ? Mth.m_14008_((this.fadeAge + partialTick) / 7.0F, 0.0, 1.0) : 0.0;
         float opacity = (float)(1.0 - Mth.m_14197_(fade));
         Vec3 cameraPos = camera.m_90583_();
         opacity *= (float)(1.0 - 0.4 * Math.abs(new Vec3(camera.m_253058_()).m_82526_(this.forward)));

         for (int index = Math.max(0, this.count - 3); index < this.count - 1; index++) {
            double freshness = (double)(index + 1) / this.count;
            float alpha = opacity * (float)(0.1 + 0.23 * freshness);
            Vec3 origin = this.sample(index, partialTick).m_82546_(cameraPos);
            double halfAngle = SakuraAxeSlashShape.halfAngle(this.flightAges[index]);
            SakuraAxeBladeLayers.body(vertices, this.f_108321_, origin, this.forward, this.up, halfAngle, alpha, 1.0 + 0.035 * fade);
         }
      }
   }
}
