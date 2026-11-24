package com.Luo_jiyv.SakuraMod.combat;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class SakuraAxeSlashShape {
   public static final int LIFETIME = 10;
   public static final double SPEED = 1.25;
   public static final int SEGMENTS = 36;
   public static final double MAX_EXTENT = 2.2;
   private static final double OPEN_ANGLE = 2.2;
   private static final double FLIGHT_ANGLE = 1.9;
   private static final double FOLD_TICKS = 4.0;
   private static final double DEPTH_RADIUS = 1.73;
   private static final double HEIGHT_RADIUS = 1.55;

   private SakuraAxeSlashShape() {
   }

   public static double halfAngle(double age) {
      return Mth.m_14139_(Mth.m_14008_(age / 4.0, 0.0, 1.0), 2.2, 1.9);
   }

   public static SakuraAxeSlashShape.Section section(int index, double halfAngle) {
      return sectionAt(index / 36.0, halfAngle);
   }

   public static SakuraAxeSlashShape.Section sectionAt(double fraction, double halfAngle) {
      double angle = (2.0 * fraction - 1.0) * halfAngle;
      double sin = Math.sin(angle);
      double cos = Math.cos(angle);
      double taper = Math.sin(Math.PI * fraction);
      SakuraAxeSlashShape.Point outer = new SakuraAxeSlashShape.Point(-0.26 + cos * 1.73, 0.2 + sin * 1.55);
      SakuraAxeSlashShape.Point inner = new SakuraAxeSlashShape.Point(-0.26 + cos * (1.73 - 0.77 * taper), 0.2 + sin * (1.55 - 0.28 * taper));
      return new SakuraAxeSlashShape.Section(outer, inner, taper);
   }

   static SakuraAxeSlashShape.Section[] outline(double age) {
      double halfAngle = halfAngle(age);
      SakuraAxeSlashShape.Section[] outline = new SakuraAxeSlashShape.Section[37];

      for (int i = 0; i < outline.length; i++) {
         outline[i] = section(i, halfAngle);
      }

      return outline;
   }

   static double foldMargin(double duration) {
      return 0.12975000000000012 * duration * 0.25;
   }

   static boolean intersects(SakuraAxeSlashShape.Section[] outline, double depth, double height, double halfDepth, double halfHeight) {
      for (int i = 1; i < outline.length; i++) {
         SakuraAxeSlashShape.Section start = outline[i - 1];
         SakuraAxeSlashShape.Section end = outline[i];
         SakuraAxeSlashShape.Point a = start.outer();
         SakuraAxeSlashShape.Point b = start.inner();
         SakuraAxeSlashShape.Point c = end.inner();
         SakuraAxeSlashShape.Point d = end.outer();
         if (!separated(1.0, 0.0, a, b, c, d, depth, height, halfDepth, halfHeight)
            && !separated(0.0, 1.0, a, b, c, d, depth, height, halfDepth, halfHeight)
            && !separated(b.height - a.height, a.depth - b.depth, a, b, c, d, depth, height, halfDepth, halfHeight)
            && !separated(c.height - b.height, b.depth - c.depth, a, b, c, d, depth, height, halfDepth, halfHeight)
            && !separated(d.height - c.height, c.depth - d.depth, a, b, c, d, depth, height, halfDepth, halfHeight)
            && !separated(a.height - d.height, d.depth - a.depth, a, b, c, d, depth, height, halfDepth, halfHeight)) {
            return true;
         }
      }

      return false;
   }

   private static boolean separated(
      double depthAxis,
      double heightAxis,
      SakuraAxeSlashShape.Point a,
      SakuraAxeSlashShape.Point b,
      SakuraAxeSlashShape.Point c,
      SakuraAxeSlashShape.Point d,
      double depth,
      double height,
      double halfDepth,
      double halfHeight
   ) {
      double pa = a.depth * depthAxis + a.height * heightAxis;
      double pb = b.depth * depthAxis + b.height * heightAxis;
      double pc = c.depth * depthAxis + c.height * heightAxis;
      double pd = d.depth * depthAxis + d.height * heightAxis;
      double center = depth * depthAxis + height * heightAxis;
      double radius = halfDepth * Math.abs(depthAxis) + halfHeight * Math.abs(heightAxis);
      return center + radius < Math.min(Math.min(pa, pb), Math.min(pc, pd)) || center - radius > Math.max(Math.max(pa, pb), Math.max(pc, pd));
   }

   public record Point(double depth, double height) {
      public Vec3 inPlane(Vec3 forward, Vec3 up) {
         return forward.m_82490_(this.depth).m_82549_(up.m_82490_(this.height));
      }
   }

   public record Section(SakuraAxeSlashShape.Point outer, SakuraAxeSlashShape.Point inner, double taper) {
   }
}
