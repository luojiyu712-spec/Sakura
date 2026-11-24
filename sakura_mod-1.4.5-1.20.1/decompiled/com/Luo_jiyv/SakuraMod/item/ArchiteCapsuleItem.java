package com.Luo_jiyv.SakuraMod.item;

import com.Luo_jiyv.SakuraMod.client.CapsuleClient;
import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleData;
import com.Luo_jiyv.SakuraMod.integration.tacz.CapsuleStat;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class ArchiteCapsuleItem extends Item {
   public ArchiteCapsuleItem(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (level.m_5776_()) {
         DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CapsuleClient.openScreen(hand));
      }

      return InteractionResultHolder.m_19092_(stack, level.m_5776_());
   }

   public void m_7373_(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
      super.m_7373_(stack, level, tooltip, flag);
      tooltip.add(Component.m_237115_("item.sakura_mod.archite_capsule.desc.1").m_130940_(ChatFormatting.DARK_PURPLE));
      tooltip.add(Component.m_237115_("item.sakura_mod.archite_capsule.desc.2").m_130940_(ChatFormatting.DARK_PURPLE));
      tooltip.add(Component.m_237115_("item.sakura_mod.archite_capsule.desc.3").m_130940_(ChatFormatting.DARK_PURPLE));
      tooltip.add(
         Component.m_237115_("item.sakura_mod.archite_capsule.desc.4").m_130944_(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
      );
      tooltip.add(Component.m_237119_());
      boolean any = false;

      for (CapsuleStat stat : CapsuleStat.values()) {
         if (CapsuleData.capsuleValue(stack, stat) != 0.0) {
            any = true;
            tooltip.add(
               Component.m_237110_(
                     "item.sakura_mod.archite_capsule.entry", new Object[]{Component.m_237115_(stat.translationKey()), CapsuleData.formatValue(stack, stat)}
                  )
                  .m_130940_(ChatFormatting.GOLD)
            );
         }
      }

      if (!any) {
         tooltip.add(Component.m_237115_("item.sakura_mod.archite_capsule.none").m_130940_(ChatFormatting.DARK_GRAY));
      }

      tooltip.add(Component.m_237115_("item.sakura_mod.archite_capsule.hint").m_130940_(ChatFormatting.DARK_GRAY));
   }
}
