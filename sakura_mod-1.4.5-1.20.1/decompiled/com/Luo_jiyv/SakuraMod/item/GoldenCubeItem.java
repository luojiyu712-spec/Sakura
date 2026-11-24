package com.Luo_jiyv.SakuraMod.item;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.event.GoldenCubeEvents;
import com.Luo_jiyv.SakuraMod.util.GoldenCubeData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class GoldenCubeItem extends Item {
   public GoldenCubeItem(Properties properties) {
      super(properties);
   }

   public boolean m_5812_(ItemStack stack) {
      return true;
   }

   public boolean m_41475_() {
      return Config.isGoldenCubeEnabled() && Config.isGoldenCubeIndestructible();
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (!level.m_5776_()) {
         GoldenCubeEvents.playWithCube(player);
      }

      return InteractionResultHolder.m_19092_(stack, level.m_5776_());
   }

   public void m_7373_(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
      for (int i = 1; i <= 3; i++) {
         tooltip.add(Component.m_237115_("item.sakura_mod.golden_cube.tooltip." + i).m_130940_(ChatFormatting.GRAY));
      }
   }

   public boolean onDroppedByPlayer(ItemStack stack, Player player) {
      return !Config.isGoldenCubeEnabled() || !Config.isGoldenCubeUndroppable() || GoldenCubeData.isRejected(player);
   }

   public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
      Level level = entity.m_9236_();
      if (level.f_46443_) {
         return false;
      } else {
         CompoundTag data = entity.getPersistentData();
         if (Config.isGoldenCubeEnabled() && Config.isGoldenCubeIndestructible()) {
            entity.m_20331_(true);
            entity.m_146915_(true);
            if (!data.m_128471_("GoldenCubeFixed")) {
               data.m_128379_("GoldenCubeFixed", true);
               entity.m_149678_();
            }
         } else if (data.m_128471_("GoldenCubeFixed")) {
            CompoundTag saved = new CompoundTag();
            entity.m_7380_(saved);
            saved.m_128376_("Age", (short)0);
            entity.m_7378_(saved);
            data.m_128473_("GoldenCubeFixed");
            entity.m_20331_(false);
            entity.m_146915_(false);
         }

         if (Config.isGoldenCubeEnabled() && Config.isGoldenCubeVoidTeleportEnabled() && entity.m_20186_() < level.m_141937_() - 8) {
            ServerLevel serverLevel = (ServerLevel)level;
            BlockPos target = null;
            Player nearest = findTeleportTarget(serverLevel, entity);
            if (nearest != null) {
               target = nearest.m_20183_();
            } else {
               BlockPos spawn = serverLevel.m_220360_();
               if (spawn != null) {
                  target = spawn;
               }
            }

            if (target != null) {
               entity.m_7678_(target.m_123341_() + 0.5, target.m_123342_() + 1.0, target.m_123343_() + 0.5, 0.0F, 0.0F);
               entity.m_20334_(0.0, 0.0, 0.0);
               entity.m_32060_();
            }
         }

         return false;
      }
   }

   private static Player findTeleportTarget(ServerLevel level, ItemEntity entity) {
      if (entity.m_19749_() instanceof Player owner && canReceiveCube(owner)) {
         return owner;
      } else {
         Player nearest = null;
         double bestDistance = 262144.0;

         for (Player player : level.m_6907_()) {
            if (canReceiveCube(player) && GoldenCubeData.isActivated(player)) {
               double distance = player.m_20280_(entity);
               if (distance <= bestDistance) {
                  bestDistance = distance;
                  nearest = player;
               }
            }
         }

         return nearest;
      }
   }

   private static boolean canReceiveCube(Player player) {
      return player.m_6084_()
         && !player.m_7500_()
         && !player.m_5833_()
         && player.m_20186_() >= player.m_9236_().m_141937_()
         && !GoldenCubeData.isImmune(player)
         && !GoldenCubeData.isRejected(player);
   }
}
