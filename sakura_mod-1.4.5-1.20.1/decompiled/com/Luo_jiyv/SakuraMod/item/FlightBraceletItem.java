package com.Luo_jiyv.SakuraMod.item;

import com.Luo_jiyv.SakuraMod.network.FlightTimeSyncPacket;
import com.Luo_jiyv.SakuraMod.network.ModNetwork;
import com.Luo_jiyv.SakuraMod.util.PlayerEquipCache;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class FlightBraceletItem extends Item implements ICurioItem {
   public static final String NBT_FLIGHT_TIME = "FlightTime";
   public static final int MAX_FLIGHT_TIME = 36000;
   public static final int TICKS_PER_SEGMENT = 3600;
   public static final int RECOVERY_TICKS = 600;
   public static final int RECOVERY_PER_TICK = 60;

   public FlightBraceletItem(Properties properties) {
      super(properties);
   }

   public boolean canEquip(SlotContext slotContext, ItemStack stack) {
      return "bracelet".equals(slotContext.identifier()) || "charm".equals(slotContext.identifier());
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      if (slotContext.entity() instanceof Player player) {
         if (!player.m_9236_().m_5776_()) {
            if (!player.m_7500_() && !player.m_5833_()) {
               int currentTime = getFlightTime(stack);
               boolean isFlying = player.m_150110_().f_35935_;
               if (isFlying && currentTime > 0) {
                  setFlightTime(stack, currentTime - 1);
               } else if (!isFlying && currentTime < 36000) {
                  boolean onGround = player.m_20096_();
                  boolean inFluid = player.m_20069_() || player.m_20077_();
                  if (onGround || inFluid) {
                     int next = Math.min(36000, currentTime + 60);
                     setFlightTime(stack, next);
                  }
               }

               if (player.f_19797_ % 10 == 0) {
                  sendSync(player, getFlightTime(stack));
               }
            }
         }
      }
   }

   private static void sendSync(Player player, int flightTime) {
      if (player instanceof ServerPlayer sp) {
         try {
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new FlightTimeSyncPacket(flightTime));
         } catch (Throwable var4) {
         }
      }
   }

   public static boolean canProvideFlight(Player player) {
      return PlayerEquipCache.hasChargedFlightBracelet(player);
   }

   public static int getFlightTime(ItemStack stack) {
      CompoundTag tag = stack.m_41783_();
      return tag != null && tag.m_128441_("FlightTime") ? Math.max(0, Math.min(36000, tag.m_128451_("FlightTime"))) : 36000;
   }

   public static void setFlightTime(ItemStack stack, int time) {
      stack.m_41784_().m_128405_("FlightTime", Math.max(0, Math.min(36000, time)));
   }

   public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
      int seconds = getFlightTime(stack) / 20;
      tooltip.add(
         Component.m_237110_("item.sakura_mod.flight_bracelet.tooltip.time", new Object[]{seconds})
            .m_130940_(seconds > 0 ? ChatFormatting.AQUA : ChatFormatting.RED)
      );
      tooltip.add(Component.m_237115_("item.sakura_mod.flight_bracelet.tooltip.desc").m_130940_(ChatFormatting.GRAY));
      super.m_7373_(stack, level, tooltip, flag);
   }
}
