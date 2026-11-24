package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.enchantment.ModEnchantments;
import com.Luo_jiyv.SakuraMod.util.AdvancementUtils;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class SoulRetrievalEvents {
   private static final int LEVEL_THRESHOLD = 30;
   private static final Set<UUID> FEEDBACK_THIS_TICK = new HashSet<>();
   private static final String NBT_SOUL_SPENT = "SakuraSoulRetrievalSpent";

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.START) {
         FEEDBACK_THIS_TICK.clear();
      }
   }

   public static boolean tryRepair(ServerPlayer player, ItemStack stack) {
      if (player != null && !player.m_150110_().f_35937_ && stack.m_41763_()) {
         Enchantment enchantment = (Enchantment)ModEnchantments.SOUL_RETRIEVAL.get();
         int enchantLevel = EnchantmentHelper.m_44843_(enchantment, stack);
         if (enchantLevel <= 0) {
            return false;
         } else {
            clearSpent(stack);
            if (player.f_36078_ >= 30) {
               chargeExperience(player);
            } else {
               Map<Enchantment, Integer> enchants = EnchantmentHelper.m_44831_(stack);
               if (enchantLevel == 1) {
                  enchants.remove(enchantment);
                  EnchantmentHelper.m_44865_(enchants, stack);
                  stack.m_41784_().m_128379_("SakuraSoulRetrievalSpent", true);
               } else {
                  enchants.put(enchantment, enchantLevel - 1);
                  EnchantmentHelper.m_44865_(enchants, stack);
               }
            }

            stack.m_41721_(stack.m_41776_() / 4);
            if (FEEDBACK_THIS_TICK.add(player.m_20148_())) {
               AdvancementUtils.award(player, "sakura_mod:functional/soul_retrieval", "trigger");
               playFeedback(player);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static void chargeExperience(ServerPlayer player) {
      int level = player.f_36078_;
      int nextLevelCost = player.m_36323_();
      int progress = Mth.m_14045_(Math.round(player.f_36080_ * nextLevelCost), 0, nextLevelCost - 1);
      double points = pointsAtLevel(level) + progress;
      double remaining = points - Math.floor(points * 0.75);
      int low = 0;
      int high = level;

      while (low < high) {
         int mid = low + (high - low) / 2 + 1;
         if (pointsAtLevel(mid) <= remaining) {
            low = mid;
         } else {
            high = mid - 1;
         }
      }

      player.m_9174_(low);
      player.m_8985_((int)(remaining - pointsAtLevel(low)));
      player.f_36079_ = (int)remaining;
   }

   private static double pointsAtLevel(int level) {
      double l = level;
      if (level <= 15) {
         return l * (l + 6.0);
      } else {
         return level <= 30 ? (l * (5.0 * l - 81.0) + 720.0) / 2.0 : (l * (9.0 * l - 325.0) + 4440.0) / 2.0;
      }
   }

   public static boolean isSoulSpent(ItemStack stack) {
      CompoundTag tag = stack.m_41783_();
      return tag != null && tag.m_128471_("SakuraSoulRetrievalSpent");
   }

   public static void clearSpent(ItemStack stack) {
      stack.m_41749_("SakuraSoulRetrievalSpent");
   }

   public static void clearSpentIfReenchanted(ItemStack stack) {
      if (isSoulSpent(stack)
         && ModEnchantments.SOUL_RETRIEVAL.isPresent()
         && EnchantmentHelper.getTagEnchantmentLevel((Enchantment)ModEnchantments.SOUL_RETRIEVAL.get(), stack) > 0) {
         clearSpent(stack);
      }
   }

   private static void playFeedback(ServerPlayer player) {
      ServerLevel level = player.m_284548_();
      level.m_5594_(null, player.m_20183_(), SoundEvents.f_11671_, SoundSource.PLAYERS, 1.0F, 1.5F);
      level.m_8767_(ParticleTypes.f_123809_, player.m_20185_(), player.m_20186_() + 1.0, player.m_20189_(), 30, 0.6, 0.8, 0.6, 0.5);
      level.m_8767_(ParticleTypes.f_123810_, player.m_20185_(), player.m_20186_() + 1.0, player.m_20189_(), 10, 0.5, 0.5, 0.5, 0.1);
   }
}
