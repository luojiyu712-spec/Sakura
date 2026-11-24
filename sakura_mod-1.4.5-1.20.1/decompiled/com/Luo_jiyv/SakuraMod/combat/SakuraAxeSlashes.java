package com.Luo_jiyv.SakuraMod.combat;

import com.Luo_jiyv.SakuraMod.damage.SakuraSpecialDamage;
import com.Luo_jiyv.SakuraMod.damage.SakuraWeaponAttack;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.network.SakuraAxeSlashPacket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.level.LevelEvent.Unload;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class SakuraAxeSlashes {
   private static final Map<ServerLevel, List<SakuraAxeSlashes.Slash>> ACTIVE = new IdentityHashMap<>();

   private SakuraAxeSlashes() {
   }

   public static void launch(ServerLevel level, Player owner, ItemStack weapon, SakuraWeaponAttack attack, @Nullable Entity primaryTarget) {
      SakuraAxeSlashes.Slash slash = new SakuraAxeSlashes.Slash(level, owner, weapon, attack, primaryTarget);
      ACTIVE.computeIfAbsent(level, ignored -> new ArrayList<>()).add(slash);
      slash.sync(false);
   }

   public static int activeCount(Player owner) {
      List<SakuraAxeSlashes.Slash> slashes = ACTIVE.get(owner.m_9236_());
      return slashes == null ? 0 : (int)slashes.stream().filter(slash -> slash.owner == owner).count();
   }

   @SubscribeEvent
   public static void onLevelTick(LevelTickEvent event) {
      if (event.phase == Phase.END && event.level instanceof ServerLevel level) {
         List<SakuraAxeSlashes.Slash> slashes = ACTIVE.get(level);
         if (slashes != null) {
            for (SakuraAxeSlashes.Slash slash : List.copyOf(slashes)) {
               if (!slash.tick()) {
                  slash.sync(true);
                  slashes.remove(slash);
               }
            }

            if (slashes.isEmpty()) {
               ACTIVE.remove(level);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLevelUnload(Unload event) {
      if (event.getLevel() instanceof ServerLevel level) {
         ACTIVE.remove(level);
      }
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      ACTIVE.clear();
   }

   private static final class Slash {
      private static final double HALF_WIDTH = 0.42;
      private final UUID id = UUID.randomUUID();
      private final ServerLevel level;
      private final Player owner;
      private final ItemStack weapon;
      private final SakuraWeaponAttack attack;
      private final SakuraSpecialDamage.AttackBonuses slashBonuses;
      private final Set<UUID> hit = new HashSet<>();
      private final Vec3 forward;
      private final Vec3 side;
      private final Vec3 horizontal;
      private final float yaw;
      private final float pitch;
      private final List<ServerPlayer> viewers;
      private Vec3 position;
      private int age;

      private Slash(ServerLevel level, Player owner, ItemStack weapon, SakuraWeaponAttack attack, @Nullable Entity primaryTarget) {
         this.level = level;
         this.owner = owner;
         this.weapon = weapon;
         this.attack = attack;
         this.slashBonuses = SakuraSpecialDamage.AttackBonuses.capture(owner);
         if (primaryTarget != null) {
            this.hit.add(primaryTarget.m_20148_());
         }

         this.position = owner.m_20191_().m_82399_().m_82520_(0.0, 0.24, 0.0);
         this.yaw = owner.m_146908_();
         this.pitch = owner.m_146909_();
         this.forward = Vec3.m_82498_(this.pitch, this.yaw);
         this.horizontal = Vec3.m_82498_(0.0F, this.yaw);
         this.side = new Vec3(-this.horizontal.f_82481_, 0.0, this.horizontal.f_82479_);
         this.viewers = level.m_6907_().stream().filter(player -> player.m_20238_(this.position) <= 9216.0).toList();
      }

      private void sync(boolean stopped) {
         SakuraAxeSlashPacket packet = new SakuraAxeSlashPacket(this.level.m_46472_().m_135782_(), this.id, this.position, this.pitch, this.yaw, stopped);

         for (ServerPlayer viewer : this.viewers) {
            if (!viewer.m_9232_() && viewer.m_9236_() == this.level) {
               ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), packet);
            }
         }
      }

      private boolean tick() {
         Vec3 start = this.position;
         Vec3 end = start.m_82549_(this.forward.m_82490_(1.25));
         if (this.owner.m_6084_()
            && !this.owner.m_213877_()
            && !this.owner.m_5833_()
            && this.owner.m_9236_() == this.level
            && this.level.m_46805_(BlockPos.m_274446_(end))) {
            this.age++;
            BlockHitResult wall = this.clip(start, end);
            boolean stopped = wall.m_6662_() == Type.BLOCK;
            Vec3 destination = stopped ? wall.m_82450_() : end;
            this.hitTargets(start, destination, this.owner);
            this.position = destination;
            return !stopped && this.age < 10;
         } else {
            return false;
         }
      }

      private BlockHitResult clip(Vec3 start, Vec3 end) {
         return this.level.m_45547_(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, null));
      }

      private void hitTargets(Vec3 start, Vec3 end, Player owner) {
         Vec3 forward = this.forward;
         Vec3 side = this.side;
         Vec3 up = side.m_82537_(forward);
         double length = end.m_82554_(start);
         double duration = Math.min(1.0, length / 1.25);
         double startAge = Math.max(0, this.age - 1);
         double foldMargin = SakuraAxeSlashShape.foldMargin(duration);
         SakuraAxeSlashShape.Section[] firstHalf = SakuraAxeSlashShape.outline(startAge + duration * 0.25);
         SakuraAxeSlashShape.Section[] secondHalf = SakuraAxeSlashShape.outline(startAge + duration * 0.75);
         AABB search = new AABB(start, end).m_82400_(2.2);

         for (LivingEntity target : this.level.m_45976_(LivingEntity.class, search)) {
            if (target != owner
               && target.m_6084_()
               && !(target instanceof ArmorStand)
               && !this.hit.contains(target.m_20148_())
               && !owner.m_7307_(target)
               && !target.m_7307_(owner)
               && !SakuraSpecialDamage.isImmuneTarget(target)
               && !(target instanceof Player other && !owner.m_7099_(other))) {
               AABB bounds = target.m_20191_();
               Vec3 center = bounds.m_82399_();
               Vec3 offset = center.m_82546_(start);
               if (!(Math.abs(offset.m_82526_(side)) > 0.42 + halfExtent(bounds, side))) {
                  double depth = offset.m_82526_(forward);
                  double height = offset.m_82526_(up);
                  double sweptHalfDepth = halfExtent(bounds, forward) + length * 0.25 + foldMargin;
                  double halfHeight = halfExtent(bounds, up) + foldMargin;
                  boolean intersects = SakuraAxeSlashShape.intersects(firstHalf, depth - length * 0.25, height, sweptHalfDepth, halfHeight)
                     || SakuraAxeSlashShape.intersects(secondHalf, depth - length * 0.75, height, sweptHalfDepth, halfHeight);
                  if (intersects && this.clip(start, center).m_6662_() != Type.BLOCK) {
                     this.hit.add(target.m_20148_());
                     DamageSource source = SakuraSpecialDamage.axeSlashSource(target, owner, this.position, this.slashBonuses, this.id);
                     this.attack.hit(owner, target, this.weapon, source, this.horizontal, 0.0);
                  }
               }
            }
         }
      }

      private static double halfExtent(AABB box, Vec3 axis) {
         return 0.5 * (Math.abs(axis.f_82479_) * box.m_82362_() + Math.abs(axis.f_82480_) * box.m_82376_() + Math.abs(axis.f_82481_) * box.m_82385_());
      }
   }
}
