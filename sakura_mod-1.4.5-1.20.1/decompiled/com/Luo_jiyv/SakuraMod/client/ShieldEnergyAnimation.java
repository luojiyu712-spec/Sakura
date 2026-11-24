package com.Luo_jiyv.SakuraMod.client;

public final class ShieldEnergyAnimation {
   private final int duration;
   private int previousPoints = -1;
   private double from;
   private double target;
   private double startedAt;
   private double lastTime = Double.NEGATIVE_INFINITY;

   public ShieldEnergyAnimation(int duration) {
      this.duration = Math.max(1, duration);
   }

   public double sample(int confirmedPoints, double tickTime) {
      double now = Math.max(this.lastTime, tickTime);
      if (this.previousPoints < 0 || confirmedPoints < this.previousPoints) {
         this.from = this.target = confirmedPoints;
         this.startedAt = now;
      } else if (confirmedPoints > this.previousPoints) {
         this.from = this.valueAt(now);
         this.target = confirmedPoints;
         this.startedAt = now;
      }

      this.previousPoints = confirmedPoints;
      this.lastTime = now;
      return this.valueAt(now);
   }

   private double valueAt(double now) {
      double progress = Math.max(0.0, Math.min(1.0, (now - this.startedAt) / this.duration));
      return this.from + (this.target - this.from) * progress;
   }

   public void reset() {
      this.previousPoints = -1;
      this.lastTime = Double.NEGATIVE_INFINITY;
   }
}
