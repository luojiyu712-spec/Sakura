package com.Luo_jiyv.SakuraMod.item;

import com.Luo_jiyv.SakuraMod.damage.SakuraSpecialDamage;
import com.Luo_jiyv.SakuraMod.damage.SakuraWeaponAttack;
import com.Luo_jiyv.SakuraMod.particle.SakuraSlashOptions;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class SakuraSwordItem extends SwordItem {
   private static final double BASE_RADIUS = 3.0;
   private static final double SWEEP_HALF_THICKNESS = 0.5;
   private static final double SWEEP_PARTICLE_RADIUS = 1.25;
   private static final Map<Player, Integer> LAST_SWEEP_TICK = new WeakHashMap<>();
   private static final Map<Player, Boolean> LAST_SWEEP_COUNTERCLOCKWISE = new WeakHashMap<>();

   public SakuraSwordItem(Tier tier, int damage, float attackSpeed, Properties properties) {
      super(tier, damage, attackSpeed, properties);
   }

   public boolean canPerformAction(ItemStack stack, ToolAction action) {
      return action != ToolActions.SWORD_SWEEP && super.canPerformAction(stack, action);
   }

   public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
      sweep(player, entity instanceof PartEntity<?> part ? part.getParent() : entity);
      return false;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onLeftClickBlock(LeftClickBlock event) {
      if (event.getAction() == Action.START) {
         sweep(event.getEntity(), null);
      }
   }

   public static void sweep(Player player, @Nullable Entity primaryTarget) {
      ItemStack sword = player.m_21205_();
      if (player.m_9236_() instanceof ServerLevel level
         && player.m_6084_()
         && !player.m_5833_()
         && !player.m_5803_()
         && !player.m_6117_()
         && sword.m_41720_() instanceof SakuraSwordItem) {
         Integer lastTick = LAST_SWEEP_TICK.put(player, player.f_19797_);
         if (lastTick == null || lastTick != player.f_19797_) {
            double radius = 3.0 + EnchantmentHelper.m_44843_(Enchantments.f_44983_, sword);
            double radiusSquared = radius * radius;
            Vec3 horizontalForward = Vec3.m_82498_(0.0F, player.m_146908_());
            Vec3 forward = player.m_20154_();
            Vec3 right = new Vec3(-horizontalForward.f_82481_, 0.0, horizontalForward.f_82479_);
            Vec3 normal = right.m_82537_(forward).m_82541_();
            Vec3 center = player.m_20191_().m_82399_();
            double searchDiameter = (radius + 0.5) * 2.0;
            AABB searchBox = AABB.m_165882_(center, searchDiameter, searchDiameter, searchDiameter);
            SakuraWeaponAttack attack = SakuraWeaponAttack.capture(player);
            boolean slashActive = SakuraSpecialDamage.isSlashActive(player);
            DamageSource source = player.m_269291_().m_269075_(player);

            for (LivingEntity target : level.m_45976_(LivingEntity.class, searchBox)) {
               if (target != player
                  && target != primaryTarget
                  && target.m_6084_()
                  && !target.m_5833_()
                  && !(target instanceof ArmorStand)
                  && !player.m_7307_(target)
                  && !target.m_7307_(player)
                  && !(target instanceof Player other && !player.m_7099_(other))) {
                  AABB bounds = target.m_20191_();
                  Vec3 offset = bounds.m_82399_().m_82546_(center);
                  double normalDistance = offset.m_82526_(normal);
                  double halfDepth = 0.5
                     * (
                        Math.abs(normal.f_82479_) * bounds.m_82362_()
                           + Math.abs(normal.f_82480_) * bounds.m_82376_()
                           + Math.abs(normal.f_82481_) * bounds.m_82385_()
                     );
                  if (!(Math.abs(normalDistance) > 0.5 + halfDepth)
                     && !(offset.m_82556_() - normalDistance * normalDistance > radiusSquared)
                     && player.m_142582_(target)) {
                     Vec3 away = offset.m_165925_() < 1.0E-8 ? horizontalForward : offset;
                     attack.hit(player, target, sword, source, away, 0.4);
                  }
               }
            }

            if (primaryTarget == null) {
               player.m_36334_();
               ItemStack original = sword.m_41777_();
               sword.m_41622_(1, player, user -> {
                  user.m_21190_(InteractionHand.MAIN_HAND);
                  ForgeEventFactory.onPlayerDestroyItem(user, original, InteractionHand.MAIN_HAND);
               });
            }

            level.m_6263_(null, player.m_20185_(), player.m_20186_(), player.m_20189_(), SoundEvents.f_12317_, SoundSource.PLAYERS, 1.0F, 1.0F);
            boolean counterclockwise = false;
            if (slashActive) {
               counterclockwise = !LAST_SWEEP_COUNTERCLOCKWISE.getOrDefault(player, true);
               LAST_SWEEP_COUNTERCLOCKWISE.put(player, counterclockwise);
            }

            playSweepParticles(level, player.m_19879_(), center, forward, player.m_146909_(), player.m_146908_(), radius, slashActive, counterclockwise);
         }
      }
   }

   private static void playSweepParticles(
      ServerLevel level, int ownerId, Vec3 center, Vec3 forward, float pitch, float yaw, double radius, boolean slashActive, boolean counterclockwise
   ) {
      if (slashActive) {
         level.m_8767_(
            new SakuraSlashOptions(ownerId), center.f_82479_, center.f_82480_, center.f_82481_, 0, pitch, yaw, counterclockwise ? -radius : radius, 1.0
         );
      } else {
         Vec3 front = center.m_82549_(forward.m_82490_(1.25));
         level.m_8767_(ParticleTypes.f_123766_, front.f_82479_, front.f_82480_, front.f_82481_, 0, 1.0, 0.0, 0.0, 1.0);
      }
   }
}
