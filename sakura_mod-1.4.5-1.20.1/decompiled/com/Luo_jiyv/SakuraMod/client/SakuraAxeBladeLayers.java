package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.combat.SakuraAxeSlashShape;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

final class SakuraAxeBladeLayers {
   private static final int SEGMENTS = 96;

   private SakuraAxeBladeLayers() {
   }

   static void render(
      VertexConsumer vertices,
      Camera camera,
      TextureAtlasSprite body,
      TextureAtlasSprite edge,
      TextureAtlasSprite glow,
      Vec3 origin,
      Vec3 forward,
      Vec3 up,
      Vec3 side,
      double halfAngle,
      float opacity
   ) {
      Vec3 facing = origin.m_82526_(side) <= 0.0 ? side : side.m_82490_(-1.0);
      surface(vertices, glow, origin.m_82546_(facing.m_82490_(0.012)), forward, up, halfAngle, opacity * 0.27F, 1.0, true);
      body(vertices, body, origin.m_82549_(facing.m_82490_(0.006)), forward, up, halfAngle, opacity, 1.0);
      edge(vertices, camera, edge, origin, forward, up, halfAngle, opacity);
   }

   static void body(VertexConsumer vertices, TextureAtlasSprite texture, Vec3 origin, Vec3 forward, Vec3 up, double halfAngle, float opacity, double scale) {
      surface(vertices, texture, origin, forward, up, halfAngle, opacity, scale, false);
   }

   private static void surface(
      VertexConsumer vertices, TextureAtlasSprite texture, Vec3 origin, Vec3 forward, Vec3 up, double halfAngle, float opacity, double scale, boolean glow
   ) {
      Vec3 lastOuter = null;
      Vec3 lastInner = null;
      float lastU = 0.0F;

      for (int i = 0; i <= 96; i++) {
         float u = i / 96.0F;
         SakuraAxeSlashShape.Section section = SakuraAxeSlashShape.sectionAt(u, halfAngle);
         Vec3 outerOffset = section.outer().inPlane(forward, up).m_82490_(scale);
         Vec3 innerOffset = section.inner().inPlane(forward, up).m_82490_(scale);
         if (glow) {
            Vec3 crossSection = outerOffset.m_82546_(innerOffset);
            outerOffset = outerOffset.m_82549_(crossSection.m_82490_(0.24));
            innerOffset = innerOffset.m_82546_(crossSection.m_82490_(0.3));
         }

         Vec3 outer = origin.m_82549_(outerOffset);
         Vec3 inner = origin.m_82549_(innerOffset);
         if (lastOuter != null) {
            quad(vertices, texture, lastOuter, lastInner, inner, outer, lastU, u, glow ? 0.0F : 0.42F, 1.0F, opacity);
         }

         lastOuter = outer;
         lastInner = inner;
         lastU = u;
      }
   }

   private static void edge(
      VertexConsumer vertices, Camera camera, TextureAtlasSprite texture, Vec3 origin, Vec3 forward, Vec3 up, double halfAngle, float opacity
   ) {
      Vec3 lastLeft = null;
      Vec3 lastRight = null;
      float lastU = 0.0F;
      double facing = Math.abs(new Vec3(camera.m_253058_()).m_82526_(up.m_82537_(forward)));
      float grazing = (float)(1.0 - Mth.m_14197_(Mth.m_14008_((facing - 0.08) / 0.32, 0.0, 1.0)));
      float edgeV = 0.42F * grazing;

      for (int i = 0; i <= 96; i++) {
         float u = i / 96.0F;
         SakuraAxeSlashShape.Section section = SakuraAxeSlashShape.sectionAt(u, halfAngle);
         Vec3 point = origin.m_82549_(section.outer().inPlane(forward, up));
         point = point.m_82549_(point.m_82490_(-1.0).m_82541_().m_82490_(0.035));
         double grazingWidth = 0.08 * (1.0 + 0.8 * Math.min(1.0, point.m_82553_() / 12.0));
         double width = Mth.m_14139_(grazing, 0.15, grazingWidth) * Math.pow(Math.max(0.0, section.taper()), 0.65);
         Vec3 before = SakuraAxeSlashShape.sectionAt(Math.max(0.0, u - 0.010416666666666666), halfAngle).outer().inPlane(forward, up);
         Vec3 after = SakuraAxeSlashShape.sectionAt(Math.min(1.0, u + 0.010416666666666666), halfAngle).outer().inPlane(forward, up);
         Vec3 across = after.m_82546_(before).m_82537_(point.m_82490_(-1.0)).m_82541_();
         if (across.m_82556_() < 0.5) {
            across = new Vec3(camera.m_252775_());
         }

         Vec3 left = point.m_82546_(across.m_82490_(width));
         Vec3 right = point.m_82549_(across.m_82490_(width));
         if (lastLeft != null) {
            quad(vertices, texture, lastLeft, lastRight, right, left, lastU, u, edgeV, 1.0F - edgeV, opacity);
         }

         lastLeft = left;
         lastRight = right;
         lastU = u;
      }
   }

   private static void quad(
      VertexConsumer vertices, TextureAtlasSprite texture, Vec3 a, Vec3 b, Vec3 c, Vec3 d, float startU, float endU, float outerV, float innerV, float opacity
   ) {
      Vec3 normal = b.m_82546_(a).m_82537_(c.m_82546_(a)).m_82549_(c.m_82546_(a).m_82537_(d.m_82546_(a)));
      boolean front = normal.m_82526_(a) <= 0.0;
      vertex(vertices, texture, a, startU, outerV, opacity);
      vertex(vertices, texture, front ? b : d, front ? startU : endU, front ? innerV : outerV, opacity);
      vertex(vertices, texture, c, endU, innerV, opacity);
      vertex(vertices, texture, front ? d : b, front ? endU : startU, front ? outerV : innerV, opacity);
   }

   private static void vertex(VertexConsumer vertices, TextureAtlasSprite texture, Vec3 pos, float u, float v, float alpha) {
      vertices.m_5483_(pos.f_82479_, pos.f_82480_, pos.f_82481_)
         .m_7421_(Mth.m_14179_(u, texture.m_118409_(), texture.m_118410_()), Mth.m_14179_(v, texture.m_118411_(), texture.m_118412_()))
         .m_85950_(1.0F, 1.0F, 1.0F, alpha)
         .m_85969_(15728880)
         .m_5752_();
   }
}
