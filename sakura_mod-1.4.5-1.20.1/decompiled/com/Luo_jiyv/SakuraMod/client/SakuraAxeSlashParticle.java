package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.combat.SakuraAxeSlashShape;
import com.Luo_jiyv.SakuraMod.network.SakuraAxeSlashPacket;
import com.Luo_jiyv.SakuraMod.particle.ModParticles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public final class SakuraAxeSlashParticle extends SakuraMeshParticle {
   private static final Map<UUID, SakuraAxeSlashParticle> ACTIVE = new HashMap<>();
   private final Vec3 forward;
   private final Vec3 side;
   private final Vec3 up;
   private final long createdAt;
   private final SakuraAxeTrailParticle trail;
   private final TextureAtlasSprite edgeSprite;
   private final TextureAtlasSprite glowSprite;

   static void register(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet(
         (ParticleType)ModParticles.SAKURA_AXE_SLASH.get(),
         sprites -> (options, level, x, y, z, pitch, yaw, unused) -> new SakuraAxeSlashParticle(level, x, y, z, (float)pitch, (float)yaw, sprites)
      );
   }

   public static void handle(SakuraAxeSlashPacket packet) {
      Minecraft minecraft = Minecraft.m_91087_();
      ClientLevel level = minecraft.f_91073_;
      if (level != null && level.m_46472_().m_135782_().equals(packet.dimension())) {
         SakuraAxeSlashParticle previous = ACTIVE.remove(packet.id());
         if (previous != null) {
            previous.m_107274_();
         }

         if (!packet.stopped()) {
            Vec3 pos = packet.position();
            if (minecraft.f_91061_
               .m_107370_((ParticleOptions)ModParticles.SAKURA_AXE_SLASH.get(), pos.f_82479_, pos.f_82480_, pos.f_82481_, packet.pitch(), packet.yaw(), 0.0) instanceof SakuraAxeSlashParticle slash
               )
             {
               ACTIVE.put(packet.id(), slash);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         ClientLevel current = Minecraft.m_91087_().f_91073_;
         ACTIVE.values().removeIf(particle -> {
            if (particle.f_107208_ == current && particle.m_107276_() && current.m_46467_() - particle.createdAt <= 12L) {
               return false;
            } else {
               particle.m_107274_();
               return true;
            }
         });
      }
   }

   private SakuraAxeSlashParticle(ClientLevel level, double x, double y, double z, float pitch, float yaw, SpriteSet sprites) {
      super(level, x, y, z);
      this.forward = Vec3.m_82498_(pitch, yaw);
      Vec3 horizontal = Vec3.m_82498_(0.0F, yaw);
      this.side = new Vec3(-horizontal.f_82481_, 0.0, horizontal.f_82479_);
      this.up = this.side.m_82537_(this.forward);
      this.createdAt = level.m_46467_();
      this.f_107225_ = 10;
      this.f_107219_ = false;
      this.m_108337_(sprites.m_5819_(1, 3));
      this.edgeSprite = sprites.m_5819_(2, 3);
      this.glowSprite = sprites.m_5819_(3, 3);
      this.trail = new SakuraAxeTrailParticle(level, new Vec3(x, y, z), this.forward, this.up, sprites);
      Minecraft.m_91087_().f_91061_.m_107344_(this.trail);
      this.updateBounds();
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (++this.f_107224_ >= this.f_107225_) {
         this.m_107274_();
      } else {
         Vec3 start = new Vec3(this.f_107212_, this.f_107213_, this.f_107214_);
         Vec3 end = start.m_82549_(this.forward.m_82490_(1.25));
         if (!this.f_107208_.m_46805_(BlockPos.m_274446_(end))) {
            this.m_107274_();
         } else {
            BlockHitResult wall = this.f_107208_.m_45547_(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, null));
            if (wall.m_6662_() == Type.BLOCK) {
               this.m_107274_();
            } else {
               this.m_107264_(end.f_82479_, end.f_82480_, end.f_82481_);
               this.trail.append(end, this.f_107224_);
               this.updateBounds();
            }
         }
      }
   }

   public void m_107274_() {
      this.trail.finish();
      super.m_107274_();
   }

   private void updateBounds() {
      this.m_107259_(new AABB(this.f_107209_, this.f_107210_, this.f_107211_, this.f_107212_, this.f_107213_, this.f_107214_).m_82400_(2.5500000000000003));
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   public void m_5744_(VertexConsumer vertices, Camera camera, float partialTick) {
      float time = this.f_107224_ + partialTick;
      float opacity = Mth.m_14036_((time + 0.3F) / 0.9F, 0.0F, 1.0F) * Mth.m_14036_((this.f_107225_ - time) / 3.0F, 0.0F, 1.0F);
      Vec3 origin = new Vec3(
            Mth.m_14139_(partialTick, this.f_107209_, this.f_107212_),
            Mth.m_14139_(partialTick, this.f_107210_, this.f_107213_),
            Mth.m_14139_(partialTick, this.f_107211_, this.f_107214_)
         )
         .m_82546_(camera.m_90583_());
      SakuraAxeBladeLayers.render(
         vertices,
         camera,
         this.f_108321_,
         this.edgeSprite,
         this.glowSprite,
         origin,
         this.forward,
         this.up,
         this.side,
         SakuraAxeSlashShape.halfAngle(time),
         opacity
      );
   }
}
