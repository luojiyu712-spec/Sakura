package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.damage.ModDamageTypes;
import com.Luo_jiyv.SakuraMod.effect.ModEffects;
import com.Luo_jiyv.SakuraMod.item.SakuraRepairKitItem;
import com.Luo_jiyv.SakuraMod.util.AdvancementUtils;
import com.Luo_jiyv.SakuraMod.util.PlayerEquipCache;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class SakuraArmorEvents {
   private static final int EFFECT_DURATION = 240;
   private static final int REFRESH_THRESHOLD = 220;
   private static final float DURABILITY_COST_RATIO = 0.3F;
   private static final float AUTO_REPAIR_THRESHOLD = 0.3F;
   private static final int HOTBAR_SIZE = 9;
   public static final String NBT_REVIVE_ENABLED = "SakuraArmorReviveEnabled";
   public static final String NBT_AUTO_REPAIR_ENABLED = "SakuraArmorAutoRepairEnabled";
   private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private static final Map<UUID, Long> REVIVE_HANDLED = new ConcurrentHashMap<>();

   public static boolean isReviveEnabled(Player player) {
      CompoundTag data = player.getPersistentData();
      return !data.m_128441_("SakuraArmorReviveEnabled") ? true : data.m_128471_("SakuraArmorReviveEnabled");
   }

   public static boolean isAutoRepairEnabled(Player player) {
      CompoundTag data = player.getPersistentData();
      return !data.m_128441_("SakuraArmorAutoRepairEnabled") ? true : data.m_128471_("SakuraArmorAutoRepairEnabled");
   }

   public static boolean isReviveHandled(Player player) {
      Long tick = REVIVE_HANDLED.get(player.m_20148_());
      return tick != null && tick == player.m_9236_().m_46467_();
   }

   public static void handleTogglePacket(ServerPlayer player, boolean toggleRevive, boolean toggleAutoRepair) {
      if (toggleRevive) {
         boolean now = !isReviveEnabled(player);
         player.getPersistentData().m_128379_("SakuraArmorReviveEnabled", now);
         player.m_5661_(
            Component.m_237115_("item.sakura_mod.sakura_armor.revive." + (now ? "on" : "off")).m_130940_(now ? ChatFormatting.GREEN : ChatFormatting.RED), true
         );
      }

      if (toggleAutoRepair) {
         boolean now = !isAutoRepairEnabled(player);
         player.getPersistentData().m_128379_("SakuraArmorAutoRepairEnabled", now);
         player.m_5661_(
            Component.m_237115_("item.sakura_mod.sakura_armor.auto_repair." + (now ? "on" : "off")).m_130940_(now ? ChatFormatting.GREEN : ChatFormatting.RED),
            true
         );
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END) {
         Player player = event.player;
         if (!player.m_9236_().m_5776_()) {
            if (player.f_19797_ % 10 == 0) {
               int armorCount = PlayerEquipCache.countSakuraArmor(player);
               if (armorCount < 2) {
                  tryRemoveBlessing(player);
               } else {
                  boolean hasTool = PlayerEquipCache.handHoldsSakuraTool(player);
                  int amplifier = armorCount >= 4 ? 1 : 0;
                  applyEffect(player, (MobEffect)ModEffects.SAKURA_BLESSING.get(), amplifier);
                  if (hasTool) {
                     applyEffect(player, MobEffects.f_19598_, amplifier);
                  }
               }
            }
         }
      }
   }

   private static void tryRemoveBlessing(Player player) {
      if (!PlayerEquipCache.hasSakuraAmulet(player)) {
         MobEffectInstance current = player.m_21124_((MobEffect)ModEffects.SAKURA_BLESSING.get());
         if (current != null) {
            if (current.m_19564_() <= 2) {
               player.m_21195_((MobEffect)ModEffects.SAKURA_BLESSING.get());
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void protectFromFollowUpSpecialDamage(LivingAttackEvent event) {
      if (event.getEntity() instanceof Player player && !player.m_9236_().m_5776_() && ModDamageTypes.isOurDamage(event.getSource()) && isReviveHandled(player)
         )
       {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onLivingDamage(LivingDamageEvent event) {
      if (event.getEntity() instanceof Player player) {
         if (!player.m_9236_().m_5776_()) {
            if (isAutoRepairEnabled(player)) {
               tryAutoRepair(player);
            }

            if (!isReviveHandled(player)) {
               if (isReviveEnabled(player)) {
                  if (!(event.getAmount() <= 0.0F)) {
                     if (player.m_21023_((MobEffect)ModEffects.SAKURA_BLESSING.get())) {
                        DamageSource source = event.getSource();
                        if (!source.m_276093_(DamageTypes.f_286979_)) {
                           if (!player.m_7500_() && !player.m_5833_()) {
                              float remainingHealth = player.m_21223_() + player.m_6103_() - event.getAmount();
                              if (remainingHealth <= 0.0F) {
                                 if (PlayerEquipCache.hasFullSakuraArmor(player)) {
                                    if (!player.m_21205_().m_150930_(Items.f_42747_) && !player.m_21206_().m_150930_(Items.f_42747_)) {
                                       ItemStack helmet = player.m_6844_(EquipmentSlot.HEAD);
                                       ItemStack chest = player.m_6844_(EquipmentSlot.CHEST);
                                       ItemStack legs = player.m_6844_(EquipmentSlot.LEGS);
                                       ItemStack boots = player.m_6844_(EquipmentSlot.FEET);
                                       if (canAffordCost(helmet) && canAffordCost(chest) && canAffordCost(legs) && canAffordCost(boots)) {
                                          REVIVE_HANDLED.put(player.m_20148_(), player.m_9236_().m_46467_());
                                          event.setAmount(0.0F);
                                          applyTotemEffect(player);
                                          consumeDurability(helmet);
                                          consumeDurability(chest);
                                          consumeDurability(legs);
                                          consumeDurability(boots);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
      if (event.getEntity() instanceof Player player) {
         if (!player.m_9236_().m_5776_()) {
            if (event.getSlot().m_254934_()) {
               if (isAutoRepairEnabled(player)) {
                  tryAutoRepair(player);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      if (!player.m_9236_().m_5776_()) {
         if (isAutoRepairEnabled(player)) {
            tryAutoRepair(player);
         }
      }
   }

   @SubscribeEvent
   public static void onClone(Clone event) {
      CompoundTag old = event.getOriginal().getPersistentData();
      CompoundTag data = event.getEntity().getPersistentData();
      if (old.m_128441_("SakuraArmorReviveEnabled")) {
         data.m_128379_("SakuraArmorReviveEnabled", old.m_128471_("SakuraArmorReviveEnabled"));
      }

      if (old.m_128441_("SakuraArmorAutoRepairEnabled")) {
         data.m_128379_("SakuraArmorAutoRepairEnabled", old.m_128471_("SakuraArmorAutoRepairEnabled"));
      }
   }

   @SubscribeEvent
   public static void onPlayerLogout(PlayerLoggedOutEvent event) {
      REVIVE_HANDLED.remove(event.getEntity().m_20148_());
   }

   private static void tryAutoRepair(Player player) {
      if (PlayerEquipCache.countSakuraArmor(player) != 0) {
         int available = countAvailableKits(player);
         if (available > 0) {
            for (EquipmentSlot slot : ARMOR_SLOTS) {
               ItemStack armor = player.m_6844_(slot);
               if (PlayerEquipCache.isSakuraArmor(armor) && needsRepair(armor)) {
                  int kitsNeeded = calculateKitsNeeded(armor);
                  if (kitsNeeded > 0 && available >= kitsNeeded && consumeKits(player, kitsNeeded)) {
                     available -= kitsNeeded;
                     armor.m_41721_(0);
                     player.m_9236_().m_5594_(null, player.m_20183_(), SoundEvents.f_12471_, SoundSource.PLAYERS, 0.5F, 1.2F);
                  }
               }
            }
         }
      }
   }

   private static int calculateKitsNeeded(ItemStack armor) {
      int maxDamage = armor.m_41776_();
      if (maxDamage <= 0) {
         return 0;
      } else if (armor.m_41773_() <= 0) {
         return 0;
      } else {
         float damageRatio = (float)armor.m_41773_() / maxDamage;
         float perKit = 0.2F;
         return perKit <= 0.0F ? 1 : Math.max(1, (int)Math.ceil(damageRatio / perKit));
      }
   }

   private static int countAvailableKits(Player player) {
      int count = 0;

      for (int i = 0; i < 9; i++) {
         ItemStack s = player.m_150109_().m_8020_(i);
         if (isRepairKit(s)) {
            count += s.m_41613_();
         }
      }

      ItemStack offhand = player.m_21206_();
      if (isRepairKit(offhand)) {
         count += offhand.m_41613_();
      }

      return count;
   }

   private static boolean consumeKits(Player player, int amount) {
      int remaining = amount;

      for (int i = 0; i < 9 && remaining > 0; i++) {
         ItemStack s = player.m_150109_().m_8020_(i);
         if (isRepairKit(s)) {
            int take = Math.min(remaining, s.m_41613_());
            s.m_41774_(take);
            remaining -= take;
         }
      }

      if (remaining > 0) {
         ItemStack offhand = player.m_21206_();
         if (isRepairKit(offhand)) {
            int take = Math.min(remaining, offhand.m_41613_());
            offhand.m_41774_(take);
            remaining -= take;
         }
      }

      return remaining == 0;
   }

   private static boolean needsRepair(ItemStack armor) {
      if (!armor.m_41763_()) {
         return false;
      } else {
         int maxDamage = armor.m_41776_();
         if (maxDamage <= 0) {
            return false;
         } else {
            float remainRatio = 1.0F - (float)armor.m_41773_() / maxDamage;
            return remainRatio < 0.3F;
         }
      }
   }

   private static boolean isRepairKit(ItemStack stack) {
      return !stack.m_41619_() && stack.m_41720_() instanceof SakuraRepairKitItem;
   }

   private static void applyTotemEffect(Player player) {
      player.m_21153_(Math.max(player.m_21223_(), 10.0F));
      player.m_21219_();
      player.m_7292_(new MobEffectInstance(MobEffects.f_19617_, 100, 1));
      player.m_7292_(new MobEffectInstance(MobEffects.f_19605_, 900, 1));
      if (player.m_9236_() instanceof ServerLevel serverLevel) {
         double x = player.m_20185_();
         double y = player.m_20186_() + 1.0;
         double z = player.m_20189_();
         serverLevel.m_8767_(ParticleTypes.f_123767_, x, y, z, 150, 1.5, 1.5, 1.5, 0.5);
         serverLevel.m_8767_(ParticleTypes.f_123809_, x, y, z, 100, 2.0, 2.0, 2.0, 1.0);
      }

      player.m_9236_().m_5594_(null, player.m_20183_(), SoundEvents.f_12513_, SoundSource.PLAYERS, 1.0F, 1.0F);
      if (player instanceof ServerPlayer sp) {
         AdvancementUtils.award(sp, "sakura_mod:functional/self_sacrifice", "trigger");
      }
   }

   private static boolean canAffordCost(ItemStack armor) {
      if (armor.m_41619_()) {
         return false;
      } else if (!armor.m_41763_()) {
         return true;
      } else {
         int maxDamage = armor.m_41776_();
         if (maxDamage <= 0) {
            return true;
         } else {
            int cost = (int)(maxDamage * 0.3F);
            return armor.m_41773_() + cost < maxDamage;
         }
      }
   }

   private static void consumeDurability(ItemStack armor) {
      if (!armor.m_41619_()) {
         if (armor.m_41763_()) {
            int maxDamage = armor.m_41776_();
            if (maxDamage > 0) {
               int cost = (int)(maxDamage * 0.3F);
               int newDamage = Math.min(armor.m_41773_() + cost, maxDamage - 1);
               armor.m_41721_(newDamage);
            }
         }
      }
   }

   private static void applyEffect(Player player, MobEffect effect, int amplifier) {
      MobEffectInstance current = player.m_21124_(effect);
      boolean needRefresh = current == null || current.m_19564_() < amplifier || current.m_19564_() == amplifier && current.m_19557_() <= 220;
      if (needRefresh) {
         player.m_7292_(new MobEffectInstance(effect, 240, amplifier, false, false, true));
      }
   }
}
