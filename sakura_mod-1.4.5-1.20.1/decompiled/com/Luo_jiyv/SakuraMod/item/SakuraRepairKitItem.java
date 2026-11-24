package com.Luo_jiyv.SakuraMod.item;

import com.Luo_jiyv.SakuraMod.util.AdvancementUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class SakuraRepairKitItem extends Item {
   public static final float REPAIR_RATIO = 0.2F;

   public SakuraRepairKitItem(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (!level.m_5776_()) {
         boolean repaired = false;

         for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (repair(player.m_6844_(slot))) {
               repaired = true;
            }
         }

         if (hand == InteractionHand.MAIN_HAND) {
            if (repair(player.m_21206_())) {
               repaired = true;
            }
         } else if (repair(player.m_21205_())) {
            repaired = true;
         }

         if (repaired) {
            level.m_5594_(null, player.m_20183_(), SoundEvents.f_12471_, SoundSource.PLAYERS, 1.0F, 1.0F);
            stack.m_41774_(1);
            if (player instanceof ServerPlayer sp) {
               AdvancementUtils.award(sp, "sakura_mod:functional/ding_ding", "trigger");
            }

            return InteractionResultHolder.m_19090_(stack);
         }
      }

      return InteractionResultHolder.m_19098_(stack);
   }

   private static boolean repair(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else if (!stack.m_41763_()) {
         return false;
      } else if (stack.m_41773_() <= 0) {
         return false;
      } else {
         int repairAmount = (int)(stack.m_41776_() * 0.2F);
         if (repairAmount <= 0) {
            repairAmount = 1;
         }

         int newDamage = Math.max(0, stack.m_41773_() - repairAmount);
         stack.m_41721_(newDamage);
         return true;
      }
   }
}
