package com.Luo_jiyv.SakuraMod.item;

import com.Luo_jiyv.SakuraMod.util.RandomTeleporterSearch;
import com.Luo_jiyv.SakuraMod.util.RandomTeleporterSettings;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RandomTeleporterItem extends Item {
   private static final int COOLDOWN_TICKS = 100;

   public RandomTeleporterItem(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (level.f_46443_) {
         return InteractionResultHolder.m_19090_(stack);
      } else if (!(level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer)) {
         return InteractionResultHolder.m_19098_(stack);
      } else if (player.m_36335_().m_41519_(this)) {
         return InteractionResultHolder.m_19100_(stack);
      } else {
         MinecraftServer server = serverLevel.m_7654_();
         int[] s = RandomTeleporterSettings.get(server, player.m_20148_());
         Set<ResourceLocation> biomes = RandomTeleporterSettings.getAllowedBiomes(server, player.m_20148_());
         Set<ResourceLocation> structures = RandomTeleporterSettings.getAllowedStructures(server, player.m_20148_());
         player.m_36335_().m_41524_(this, 100);
         return RandomTeleporterSearch.start(serverLevel, serverPlayer, biomes, structures, s[0], s[1])
            ? InteractionResultHolder.m_19090_(stack)
            : InteractionResultHolder.m_19100_(stack);
      }
   }

   public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(Component.m_237115_("item.sakura_mod.random_teleporter.tooltip").m_130940_(ChatFormatting.GRAY));
      tooltip.add(Component.m_237115_("item.sakura_mod.random_teleporter.tooltip2").m_130940_(ChatFormatting.GRAY));
      tooltip.add(Component.m_237115_("item.sakura_mod.random_teleporter.tooltip3").m_130940_(ChatFormatting.YELLOW));
   }
}
