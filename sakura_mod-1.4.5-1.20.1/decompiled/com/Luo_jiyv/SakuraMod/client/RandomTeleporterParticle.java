package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.network.RandomTeleporterEffectPacket;
import com.Luo_jiyv.SakuraMod.particle.ModParticles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public final class RandomTeleporterParticle extends SakuraMeshParticle {
   private static final int SEARCH_SYNC_TIMEOUT = 60;
   private static final Map<Integer, RandomTeleporterParticle> SEARCHING = new HashMap<>();
   private static final Map<Integer, RandomTeleporterParticle> PREVIEWS = new HashMap<>();
   private RandomTeleporterEffectPacket.Phase phase = RandomTeleporterEffectPacket.Phase.DEPARTURE;
   private Vec3 anchor;
   private int playerId;
   private int searchAge;
   private long lastSync;
   private long lastTick;
   @Nullable
   private Player target;

   static void register(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet(
         (ParticleType)ModParticles.RANDOM_TELEPORTER.get(),
         sprites -> (options, level, x, y, z, dx, dy, dz) -> new RandomTeleporterParticle(level, x, y, z, sprites.m_5819_(0, 1))
      );
   }

   public static void handle(RandomTeleporterEffectPacket packet) {
      Minecraft minecraft = Minecraft.m_91087_();
      ClientLevel level = minecraft.f_91073_;
      if (level != null && level.m_46472_().m_135782_().equals(packet.dimension())) {
         int id = packet.playerId();
         if (packet.phase() == RandomTeleporterEffectPacket.Phase.PREVIEW || packet.phase() == RandomTeleporterEffectPacket.Phase.ARRIVAL) {
            RandomTeleporterParticle preview = PREVIEWS.remove(id);
            if (preview != null) {
               preview.m_107274_();
            }
         }

         RandomTeleporterParticle previous = packet.phase() == RandomTeleporterEffectPacket.Phase.PREVIEW ? null : SEARCHING.get(id);
         if (previous != null
            && packet.phase() == RandomTeleporterEffectPacket.Phase.SEARCH
            && previous.f_107208_ == level
            && previous.m_107276_()
            && level.m_46467_() - previous.lastTick <= 3L) {
            previous.lastSync = level.m_46467_();
         } else {
            if (previous != null) {
               SEARCHING.remove(id);
               previous.m_107274_();
            }

            Vec3 position = packet.position();
            if (minecraft.f_91061_
               .m_107370_((ParticleOptions)ModParticles.RANDOM_TELEPORTER.get(), position.f_82479_, position.f_82480_, position.f_82481_, 0.0, 0.0, 0.0) instanceof RandomTeleporterParticle effect
               )
             {
               effect.phase = packet.phase();
               effect.playerId = id;

               effect.f_107225_ = switch (effect.phase) {
                  case SEARCH -> Integer.MAX_VALUE;
                  case SHATTER -> 18;
                  case DEPARTURE -> 28;
                  case ARRIVAL -> 30;
                  case PREVIEW -> 23;
               };
               if (effect.phase == RandomTeleporterEffectPacket.Phase.SEARCH) {
                  SEARCHING.put(id, effect);
               } else if (effect.phase == RandomTeleporterEffectPacket.Phase.PREVIEW) {
                  PREVIEWS.put(id, effect);
               } else if (effect.phase == RandomTeleporterEffectPacket.Phase.SHATTER && previous != null && previous.f_107208_ == level) {
                  effect.searchAge = previous.f_107224_;
                  Vec3 ringPosition = previous.followPosition(minecraft.m_91296_());
                  if (ringPosition.m_82557_(position) <= 4.0) {
                     effect.anchor = ringPosition;
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         ClientLevel current = Minecraft.m_91087_().f_91073_;
         PREVIEWS.values().removeIf(effect -> {
            if (effect.f_107208_ == current && effect.m_107276_() && current.m_46467_() - effect.lastTick <= 3L) {
               return false;
            } else {
               effect.m_107274_();
               return true;
            }
         });
         SEARCHING.values().removeIf(effect -> {
            if (effect.f_107208_ == current && effect.m_107276_() && current.m_46467_() - effect.lastSync < 60L) {
               return false;
            } else {
               effect.m_107274_();
               return true;
            }
         });
      }
   }

   private RandomTeleporterParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
      super(level, x, y, z);
      this.anchor = new Vec3(x, y, z);
      this.lastTick = this.lastSync = level.m_46467_();
      this.f_107225_ = 28;
      this.f_107219_ = false;
      this.m_108337_(sprite);
   }

   public void m_5989_() {
      this.lastTick = this.f_107208_.m_46467_();
      if (++this.f_107224_ >= this.f_107225_
         || this.target != null && !this.target.m_6084_()
         || this.phase == RandomTeleporterEffectPacket.Phase.SEARCH && this.lastTick - this.lastSync >= 60L) {
         this.m_107274_();
      }
   }

   private Vec3 followPosition(float partialTick) {
      if (this.target == null
         && this.f_107208_.m_6815_(this.playerId) instanceof Player player
         && player.m_6084_()
         && (this.phase != RandomTeleporterEffectPacket.Phase.ARRIVAL || player.m_20182_().m_82557_(this.anchor) <= 16.0)) {
         this.target = player;
      }

      if (this.target == null) {
         return this.anchor;
      } else {
         Vec3 current = this.target.m_20182_();
         Vec3 interpolated = this.target.m_20318_(partialTick);
         return interpolated.m_82557_(current) > 4.0 ? current : interpolated;
      }
   }

   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107431_;
   }

   public void m_5744_(VertexConsumer vertices, Camera camera, float partialTick) {
      Vec3 position = this.phase != RandomTeleporterEffectPacket.Phase.SEARCH && this.phase != RandomTeleporterEffectPacket.Phase.ARRIVAL
         ? this.anchor
         : this.followPosition(partialTick);
      if (this.phase != RandomTeleporterEffectPacket.Phase.SEARCH || this.target != null) {
         Vec3 origin = position.m_82546_(camera.m_90583_()).m_82520_(0.0, 0.035, 0.0);
         if (!(origin.m_82556_() > 4096.0)) {
            RandomTeleporterMesh mesh = new RandomTeleporterMesh(vertices, this.f_108321_);
            float time = this.f_107224_ + partialTick;
            switch (this.phase) {
               case SEARCH:
                  mesh.search(origin, time);
                  break;
               case SHATTER:
                  mesh.shatter(origin, time, this.searchAge, time / this.f_107225_);
                  break;
               case DEPARTURE:
                  mesh.departure(origin, time);
                  break;
               case ARRIVAL:
                  mesh.arrival(origin, time);
                  break;
               case PREVIEW:
                  mesh.preview(origin, time);
            }
         }
      }
   }
}
