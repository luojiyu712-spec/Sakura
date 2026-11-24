package com.Luo_jiyv.SakuraMod.item;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.client.ClientGravCoreModeCache;
import com.Luo_jiyv.SakuraMod.damage.ModDamageTypes;
import com.Luo_jiyv.SakuraMod.damage.SakuraSpecialDamage;
import com.Luo_jiyv.SakuraMod.effect.ModEffects;
import com.Luo_jiyv.SakuraMod.network.GravCoreSyncModePacket;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.util.PlayerEquipCache;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class GravCoreItem extends Item implements ICurioItem {
   public static final String NBT_MODE = "SakuraGravCoreMode";
   public static final int BURST_COOLDOWN = 80;
   public static final double BURST_RADIUS = 15.0;
   public static final float BURST_DAMAGE_RATIO = 0.5F;
   public static final float BURST_MIN_DAMAGE = 50.0F;
   public static final double BURST_KNOCKBACK = 2.5;
   public static final int BURST_CRUSH_AMPLIFIER = 4;
   public static final int BURST_CRUSH_DURATION = 80;
   public static final int CANNON_CHARGE_TICKS = 40;
   public static final double CANNON_RANGE = 320.0;
   public static final double CANNON_RADIUS = 2.5;
   public static final float CANNON_DAMAGE_RATIO = 2.5F;
   public static final float CANNON_MIN_DAMAGE = 250.0F;
   public static final int CANNON_CRUSH_AMPLIFIER = 1;
   public static final int CANNON_CRUSH_DURATION = 600;
   public static final float PATH_DROP_CHANCE = 0.5F;
   public static final int MELEE_CRUSH_AMPLIFIER = 0;
   public static final int MELEE_CRUSH_DURATION = 1200;
   private static final Set<String> PROTECTED_BLOCK_IDS = Set.of(
      "minecraft:bedrock",
      "minecraft:barrier",
      "minecraft:light",
      "minecraft:structure_void",
      "minecraft:command_block",
      "minecraft:chain_command_block",
      "minecraft:repeating_command_block",
      "minecraft:structure_block",
      "minecraft:jigsaw",
      "minecraft:end_portal",
      "minecraft:end_portal_frame",
      "minecraft:end_gateway",
      "minecraft:nether_portal",
      "minecraft:spawner",
      "minecraft:reinforced_deepslate",
      "minecraft:budding_amethyst",
      "minecraft:moving_piston"
   );
   private static volatile Set<Block> protectedBlocksCache;
   private static final int SCAN_CUBE_RADIUS = 4;
   private static final double SCAN_OFFSET_RADIUS = 4.0;
   private static final BlockPos[] SCAN_OFFSETS = buildScanOffsets();
   private static final double MAX_DIST_SQ = 10.889999999999999;
   private static final double SELF_SAFE_RADIUS_SQ = 2.25;
   private static final double AIM_LINE_RANGE = 48.0;
   private static final double BEAM_VISUAL_RANGE = 64.0;

   private static Set<Block> protectedBlocks() {
      Set<Block> cache = protectedBlocksCache;
      if (cache != null) {
         return cache;
      } else {
         Set<Block> set = new HashSet<>();

         for (String id : PROTECTED_BLOCK_IDS) {
            ResourceLocation rl = ResourceLocation.m_135820_(id);
            if (rl != null && BuiltInRegistries.f_256975_.m_7804_(rl)) {
               Block b = (Block)BuiltInRegistries.f_256975_.m_7745_(rl);
               if (b != null) {
                  set.add(b);
               }
            }
         }

         protectedBlocksCache = set;
         return set;
      }
   }

   private static boolean isProtected(BlockState state) {
      return protectedBlocks().contains(state.m_60734_());
   }

   private static BlockPos[] buildScanOffsets() {
      List<BlockPos> list = new ArrayList<>();
      double r2 = 16.0;

      for (int dx = -4; dx <= 4; dx++) {
         for (int dy = -4; dy <= 4; dy++) {
            for (int dz = -4; dz <= 4; dz++) {
               if (dx * dx + dy * dy + dz * dz <= r2) {
                  list.add(new BlockPos(dx, dy, dz));
               }
            }
         }
      }

      return list.toArray(new BlockPos[0]);
   }

   public GravCoreItem(Properties properties) {
      super(properties);
   }

   public static GravCoreItem.Mode getMode(Player player) {
      return player.m_9236_().m_5776_() ? getClientCachedMode() : GravCoreItem.Mode.fromString(player.getPersistentData().m_128461_("SakuraGravCoreMode"));
   }

   private static GravCoreItem.Mode getClientCachedMode() {
      GravCoreItem.Mode m = (GravCoreItem.Mode)DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ClientGravCoreModeCache::getMode);
      return m != null ? m : GravCoreItem.Mode.BURST;
   }

   public static void setMode(Player player, GravCoreItem.Mode mode) {
      player.getPersistentData().m_128359_("SakuraGravCoreMode", mode.name());
   }

   public static void toggleModeAndNotify(ServerPlayer player) {
      if (!hasCoreAccessible(player)) {
         syncModeToClient(player);
      } else {
         GravCoreItem.Mode next = getMode(player).next();
         setMode(player, next);
         player.m_5661_(
            Component.m_237110_("item.sakura_mod.gravcore.mode_changed", new Object[]{Component.m_237115_(next.translationKey())})
               .m_130940_(ChatFormatting.AQUA),
            true
         );
         syncModeToClient(player);
      }
   }

   public static void syncModeToClient(ServerPlayer player) {
      GravCoreItem.Mode mode = getMode(player);
      ModNetwork.CHANNEL.sendTo(new GravCoreSyncModePacket(mode.name()), player.f_8906_.f_9742_, NetworkDirection.PLAY_TO_CLIENT);
   }

   public static boolean hasCoreAccessible(Player player) {
      return PlayerEquipCache.hasCoreAccessible(player);
   }

   public boolean canEquip(SlotContext slotContext, ItemStack stack) {
      return "core".equals(slotContext.identifier());
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      GravCoreItem.Mode mode = getMode(player);
      if (!Config.isGravityAttackEnabled()) {
         if (!level.m_5776_()) {
            player.m_5661_(Component.m_237115_("item.sakura_mod.gravcore.attack_disabled").m_130940_(ChatFormatting.RED), true);
         }

         return InteractionResultHolder.m_19100_(stack);
      } else if (mode == GravCoreItem.Mode.CANNON) {
         if (player.m_36335_().m_41519_((Item)Moditems.GRAVCORE.get())) {
            return InteractionResultHolder.m_19100_(stack);
         } else {
            player.m_6672_(hand);
            return InteractionResultHolder.m_19096_(stack);
         }
      } else {
         Item key = (Item)Moditems.GRAVCORE.get();
         if (player.m_36335_().m_41519_(key)) {
            if (!level.m_5776_()) {
               showCooldownMessage(player);
            }

            return InteractionResultHolder.m_19100_(stack);
         } else {
            if (!level.m_5776_()) {
               triggerBlast(player);
               player.m_36335_().m_41524_(key, 80);
            }

            return InteractionResultHolder.m_19092_(stack, level.m_5776_());
         }
      }
   }

   public int m_8105_(ItemStack stack) {
      return 43;
   }

   public UseAnim m_6164_(ItemStack stack) {
      return UseAnim.BOW;
   }

   public void m_5929_(Level level, LivingEntity entity, ItemStack stack, int remainingTicks) {
      if (entity instanceof Player player) {
         if (getMode(player) == GravCoreItem.Mode.CANNON) {
            if (!Config.isGravityAttackEnabled()) {
               player.m_5810_();
            } else {
               int elapsed = this.m_8105_(stack) - remainingTicks;
               float progress = Math.min(1.0F, elapsed / 40.0F);
               if (level.m_5776_()) {
                  if (elapsed == 1) {
                     try {
                        Class<?> cls = Class.forName("com.Luo_jiyv.SakuraMod.client.GravCoreClient");
                        cls.getMethod("playChargeSound").invoke(null);
                     } catch (Throwable var12) {
                     }
                  }
               } else if (level instanceof ServerLevel serverLevel) {
                  int particleCount = 2 + (int)(progress * 8.0F);
                  double r = 2.0 * (1.0 - progress * 0.6);
                  serverLevel.m_8767_(ParticleTypes.f_123789_, player.m_20185_(), player.m_20186_() + 1.0, player.m_20189_(), particleCount, r, 0.4, r, 0.02);
                  if (elapsed % 3 == 0) {
                     broadcastAimLine(serverLevel, player, progress);
                  }

                  if (elapsed >= 40) {
                     fireCannon(player);
                     player.m_36335_().m_41524_((Item)Moditems.GRAVCORE.get(), 10);
                     player.m_5810_();
                  }
               }
            }
         }
      }
   }

   private static void broadcastAimLine(ServerLevel level, Player shooter, float progress) {
      Vec3 eye = shooter.m_146892_();
      Vec3 look = shooter.m_20154_();
      List<ServerPlayer> receivers = new ArrayList<>();

      for (ServerPlayer p : level.m_7654_().m_6846_().m_11314_()) {
         if (p != shooter && p.m_9236_() == level && !(p.m_20280_(shooter) > 9216.0)) {
            receivers.add(p);
         }
      }

      if (!receivers.isEmpty()) {
         double spacing = 5.0 - progress * 2.0;
         int steps = (int)(48.0 / spacing);

         for (int i = 1; i <= steps; i++) {
            Vec3 px = eye.m_82549_(look.m_82490_(i * spacing));

            for (ServerPlayer receiver : receivers) {
               level.m_8624_(receiver, ParticleTypes.f_123810_, false, px.f_82479_, px.f_82480_, px.f_82481_, 1, 0.05, 0.05, 0.05, 0.02);
            }
         }

         double purpleSpacing = Math.max(6.0, 10.0 - progress * 4.0);

         for (double d = purpleSpacing; d <= 48.0; d += purpleSpacing) {
            Vec3 px = eye.m_82549_(look.m_82490_(d));

            for (ServerPlayer receiver : receivers) {
               level.m_8624_(receiver, ParticleTypes.f_123789_, false, px.f_82479_, px.f_82480_, px.f_82481_, 1, 0.08, 0.08, 0.08, 0.03);
            }
         }
      }
   }

   public void m_5551_(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
      if (entity instanceof Player player) {
         if (getMode(player) == GravCoreItem.Mode.CANNON) {
            int elapsed = this.m_8105_(stack) - timeLeft;
            if (elapsed < 40 && !level.m_5776_()) {
               player.m_5661_(Component.m_237115_("item.sakura_mod.gravcore.charge_incomplete").m_130940_(ChatFormatting.GRAY), true);
            }
         }
      }
   }

   public static void tryActivateFromCurio(ServerPlayer player) {
      if (Config.isGravityAttackEnabled()) {
         if (getMode(player) != GravCoreItem.Mode.CANNON) {
            boolean hasCore = PlayerEquipCache.hasCoreAccessible(player);
            if (hasCore) {
               Item key = (Item)Moditems.GRAVCORE.get();
               if (player.m_36335_().m_41519_(key)) {
                  showCooldownMessage(player);
               } else {
                  triggerBlast(player);
                  player.m_36335_().m_41524_(key, 80);
               }
            }
         }
      }
   }

   private static void showCooldownMessage(Player player) {
      player.m_5661_(Component.m_237115_("item.sakura_mod.gravcore.cooldown").m_130940_(ChatFormatting.RED), true);
   }

   private static void triggerBlast(Player player) {
      Level level = player.m_9236_();
      Vec3 center = player.m_20182_();
      level.m_5594_(null, player.m_20183_(), SoundEvents.f_11913_, SoundSource.PLAYERS, 2.0F, 0.8F);
      if (level instanceof ServerLevel serverLevel) {
         RandomSource random = serverLevel.f_46441_;
         serverLevel.m_8767_(ParticleTypes.f_123812_, center.f_82479_, center.f_82480_ + 0.5, center.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);

         for (int i = 0; i < 40; i++) {
            double angle = random.m_188500_() * Math.PI * 2.0;
            double r = 2.0 + random.m_188500_() * 13.0;
            double x = center.f_82479_ + Math.cos(angle) * r;
            double z = center.f_82481_ + Math.sin(angle) * r;
            double y = center.f_82480_ + 0.3 + random.m_188500_() * 1.5;
            serverLevel.m_8767_(ParticleTypes.f_123813_, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
         }

         serverLevel.m_8767_(ParticleTypes.f_123789_, center.f_82479_, center.f_82480_ + 1.0, center.f_82481_, 60, 9.0, 2.5, 9.0, 1.5);
         serverLevel.m_8767_(ParticleTypes.f_123810_, center.f_82479_, center.f_82480_ + 1.0, center.f_82481_, 25, 10.5, 2.0, 10.5, 0.8);
      }

      AABB area = new AABB(
         center.f_82479_ - 15.0, center.f_82480_ - 15.0, center.f_82481_ - 15.0, center.f_82479_ + 15.0, center.f_82480_ + 15.0, center.f_82481_ + 15.0
      );
      Reference<DamageType> holder = level.m_9598_().m_175515_(Registries.f_268580_).m_246971_(ModDamageTypes.GRAVITY_CRUSH);
      DamageSource source = new DamageSource(holder, player, player);

      for (Entity entity : level.m_45933_(player, area)) {
         if (entity instanceof LivingEntity living
            && !(living instanceof ArmorStand)
            && living != player
            && !SakuraSpecialDamage.isImmuneTarget(living)
            && !(living.m_20270_(player) > 15.0)) {
            float damage = Math.max(50.0F, living.m_21233_() * 0.5F);
            if (hurtWithGravity(living, source, damage)) {
               Vec3 dir = living.m_20182_().m_82546_(center);
               if (dir.m_82556_() < 0.01) {
                  double angle = level.f_46441_.m_188500_() * Math.PI * 2.0;
                  dir = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
               } else {
                  dir = dir.m_82541_();
               }

               living.m_5997_(dir.f_82479_ * 2.5, 0.6, dir.f_82481_ * 2.5);
               living.m_7292_(new MobEffectInstance((MobEffect)ModEffects.GRAVITY_CRUSH.get(), 80, 4, false, false, true));
            }
         }
      }
   }

   private static void fireCannon(Player player) {
      if (Config.isGravityAttackEnabled()) {
         if (player.m_9236_() instanceof ServerLevel serverLevel) {
            Vec3 eye = player.m_146892_();
            Vec3 look = player.m_20154_().m_82541_();
            serverLevel.m_5594_(null, player.m_20183_(), SoundEvents.f_215771_, SoundSource.PLAYERS, 3.0F, 1.0F);
            if (Config.isGravityCannonBreakBlocks()) {
               destroyPathBlocks(serverLevel, player, eye, look);
            }

            Vec3 beamEnd = eye.m_82549_(look.m_82490_(320.0));
            AABB searchBox = new AABB(eye, beamEnd).m_82400_(10.5);
            Reference<DamageType> holder = serverLevel.m_9598_().m_175515_(Registries.f_268580_).m_246971_(ModDamageTypes.GRAVITY_CANNON);
            DamageSource source = new DamageSource(holder, player, player);

            for (Entity e : serverLevel.m_45933_(player, searchBox)) {
               if (e instanceof LivingEntity living && !(living instanceof ArmorStand) && living != player && !SakuraSpecialDamage.isImmuneTarget(living)) {
                  Vec3 center = living.m_20182_().m_82520_(0.0, living.m_20206_() * 0.5, 0.0);
                  double proj = center.m_82546_(eye).m_82526_(look);
                  if (!(proj < 0.0) && !(proj > 320.0)) {
                     Vec3 closest = eye.m_82549_(look.m_82490_(proj));
                     if (!(center.m_82554_(closest) > 2.5 + living.m_20205_() * 0.5)) {
                        float damage = Math.max(250.0F, living.m_21233_() * 2.5F);
                        if (hurtWithGravity(living, source, damage)) {
                           living.m_7292_(new MobEffectInstance((MobEffect)ModEffects.GRAVITY_CRUSH.get(), 600, 1, false, false, true));
                        }
                     }
                  }
               }
            }

            spawnCannonParticles(serverLevel, eye, look);
            spawnCannonBeam(serverLevel, eye, look);
         }
      }
   }

   private static boolean hurtWithGravity(LivingEntity target, DamageSource source, float amount) {
      if (target.m_6084_() && amount > 0.0F && Float.isFinite(amount)) {
         float health = target.m_21223_();
         float absorption = target.m_6103_();
         return target.m_6469_(source, amount) && target.m_6084_() && (target.m_21223_() < health || target.m_6103_() < absorption);
      } else {
         return false;
      }
   }

   private static void spawnCannonBeam(ServerLevel level, Vec3 eye, Vec3 look) {
      double range = Math.min(320.0, 64.0);
      double scale = 10.0;

      for (double d = 1.0; d <= range; d += 20.0) {
         Vec3 p = eye.m_82549_(look.m_82490_(d));
         level.m_8767_(ParticleTypes.f_123810_, p.f_82479_, p.f_82480_, p.f_82481_, 4, look.f_82479_ * 0.25, look.f_82480_ * 0.25, look.f_82481_ * 0.25, 0.0);
      }

      for (double d = 1.0; d <= range; d += 7.0) {
         Vec3 center = eye.m_82549_(look.m_82490_(d));
         double t = d / 320.0;
         double radius = 0.7 - t * 0.35;
         level.m_8767_(ParticleTypes.f_123745_, center.f_82479_, center.f_82480_, center.f_82481_, 6, radius, radius, radius, 0.01);
      }

      for (double d = 1.0; d <= range; d += 12.0) {
         Vec3 center = eye.m_82549_(look.m_82490_(d));
         double t = d / 320.0;
         double radius = 1.1 - t * 0.6;
         level.m_8767_(ParticleTypes.f_123789_, center.f_82479_, center.f_82480_, center.f_82481_, 6, radius, radius, radius, 0.02);
      }

      double sparkRange = Math.min(range, 32.0);

      for (double d = 1.0; d <= sparkRange; d++) {
         Vec3 center = eye.m_82549_(look.m_82490_(d));
         double t = d / sparkRange;
         double radius = 1.4 - t * 0.7;
         level.m_8767_(ParticleTypes.f_175830_, center.f_82479_, center.f_82480_, center.f_82481_, 8, radius, radius, radius, 0.15);
      }

      Vec3 muzzle = eye.m_82549_(look.m_82490_(1.0));
      level.m_8767_(ParticleTypes.f_123747_, muzzle.f_82479_, muzzle.f_82480_, muzzle.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
      Vec3 end = eye.m_82549_(look.m_82490_(range));
      level.m_8767_(ParticleTypes.f_175827_, end.f_82479_, end.f_82480_, end.f_82481_, 5, 0.3, 0.3, 0.3, 0.0);
   }

   private static void destroyPathBlocks(ServerLevel level, Player player, Vec3 eye, Vec3 look) {
      double step = 4.0;
      ItemStack tool = player.m_21205_();
      BlockPos[] offsets = SCAN_OFFSETS;
      MutableBlockPos cursor = new MutableBlockPos();

      for (double d = 0.0; d <= 320.0; d += 4.0) {
         double px = eye.f_82479_ + look.f_82479_ * d;
         double py = eye.f_82480_ + look.f_82480_ * d;
         double pz = eye.f_82481_ + look.f_82481_ * d;
         int cx = (int)Math.floor(px);
         int cy = (int)Math.floor(py);
         int cz = (int)Math.floor(pz);

         for (BlockPos offset : offsets) {
            cursor.m_122178_(cx + offset.m_123341_(), cy + offset.m_123342_(), cz + offset.m_123343_());
            if (level.m_46749_(cursor)) {
               BlockState state = level.m_8055_(cursor);
               if (!state.m_60795_()) {
                  double bx = cursor.m_123341_() + 0.5 - eye.f_82479_;
                  double by = cursor.m_123342_() + 0.5 - eye.f_82480_;
                  double bz = cursor.m_123343_() + 0.5 - eye.f_82481_;
                  if (!(bx * bx + by * by + bz * bz < 2.25)) {
                     double proj = bx * look.f_82479_ + by * look.f_82480_ + bz * look.f_82481_;
                     if (!(proj < 0.0) && !(proj > 320.0)) {
                        double offX = bx - look.f_82479_ * proj;
                        double offY = by - look.f_82480_ * proj;
                        double offZ = bz - look.f_82481_ * proj;
                        if (!(offX * offX + offY * offY + offZ * offZ > 10.889999999999999)) {
                           BlockPos bp = cursor.m_7949_();
                           if (!state.m_60819_().m_76178_()) {
                              level.m_7731_(bp, Blocks.f_50016_.m_49966_(), 2);
                           } else if (!isProtected(state) && level.m_7702_(bp) == null) {
                              if (level.f_46441_.m_188501_() < 0.5F) {
                                 Block.m_49881_(state, level, bp, null, player, tool);
                              }

                              level.m_7471_(bp, false);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void spawnCannonParticles(ServerLevel level, Vec3 eye, Vec3 look) {
      double scale = 10.0;
      double visualRange = Math.min(320.0, 64.0);

      for (double d = 1.0; d <= visualRange; d += 1.0 * scale) {
         Vec3 p = eye.m_82549_(look.m_82490_(d));
         level.m_8767_(ParticleTypes.f_235902_, p.f_82479_, p.f_82480_, p.f_82481_, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }

   public static enum Mode {
      BURST,
      CANNON;

      public static GravCoreItem.Mode fromString(String s) {
         return "CANNON".equals(s) ? CANNON : BURST;
      }

      public GravCoreItem.Mode next() {
         return this == BURST ? CANNON : BURST;
      }

      public String translationKey() {
         return "item.sakura_mod.gravcore.mode." + this.name().toLowerCase();
      }
   }
}
