package com.Luo_jiyv.SakuraMod.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

final class SakuraParticleEffects {
   private SakuraParticleEffects() {
   }

   static void ribbon(
      VertexConsumer vertices,
      Camera camera,
      TextureAtlasSprite sprite,
      Vec3 a,
      Vec3 b,
      double widthA,
      double widthB,
      float alphaA,
      float alphaB,
      float red,
      float green,
      float blue
   ) {
      Vec3 tangent = b.m_82546_(a);
      if (!(tangent.m_82556_() < 1.0E-10)) {
         Vec3 across = tangent.m_82537_(a.m_82549_(b).m_82490_(-0.5)).m_82541_();
         if (across.m_82556_() < 0.5) {
            across = new Vec3(camera.m_252775_());
         }

         Vec3 leftA = a.m_82546_(across.m_82490_(widthA));
         Vec3 rightA = a.m_82549_(across.m_82490_(widthA));
         Vec3 leftB = b.m_82546_(across.m_82490_(widthB));
         Vec3 rightB = b.m_82549_(across.m_82490_(widthB));
         point(vertices, sprite, leftA, red, green, blue, 0.0F);
         point(vertices, sprite, a, red, green, blue, alphaA);
         point(vertices, sprite, b, red, green, blue, alphaB);
         point(vertices, sprite, leftB, red, green, blue, 0.0F);
         point(vertices, sprite, a, red, green, blue, alphaA);
         point(vertices, sprite, rightA, red, green, blue, 0.0F);
         point(vertices, sprite, rightB, red, green, blue, 0.0F);
         point(vertices, sprite, b, red, green, blue, alphaB);
      }
   }

   static void star(VertexConsumer vertices, Camera camera, TextureAtlasSprite sprite, Vec3 pos, double size, float alpha) {
      Vec3 x = new Vec3(camera.m_252775_()).m_82490_(size);
      Vec3 y = new Vec3(camera.m_253028_()).m_82490_(size * 2.2);
      Vec3[] tips = new Vec3[]{pos.m_82546_(x), pos.m_82549_(y), pos.m_82549_(x), pos.m_82546_(y)};

      for (int i = 0; i < tips.length; i++) {
         point(vertices, sprite, pos, 1.0F, 0.98F, 0.88F, alpha);
         point(vertices, sprite, tips[i], 1.0F, 0.67F, 0.83F, 0.0F);
         point(vertices, sprite, tips[(i + 1) % tips.length], 1.0F, 0.67F, 0.83F, 0.0F);
         point(vertices, sprite, tips[(i + 1) % tips.length], 1.0F, 0.67F, 0.83F, 0.0F);
      }
   }

   private static void point(VertexConsumer vertices, TextureAtlasSprite sprite, Vec3 pos, float red, float green, float blue, float alpha) {
      vertices.m_5483_(pos.f_82479_, pos.f_82480_, pos.f_82481_)
         .m_7421_(Mth.m_14179_(0.5F, sprite.m_118409_(), sprite.m_118410_()), Mth.m_14179_(0.17F, sprite.m_118411_(), sprite.m_118412_()))
         .m_85950_(red, green, blue, alpha)
         .m_85969_(15728880)
         .m_5752_();
   }
}
