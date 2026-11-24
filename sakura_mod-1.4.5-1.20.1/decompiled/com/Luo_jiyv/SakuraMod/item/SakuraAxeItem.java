package com.Luo_jiyv.SakuraMod.item;

import com.Luo_jiyv.SakuraMod.combat.SakuraAxeSlashes;
import com.Luo_jiyv.SakuraMod.damage.SakuraWeaponAttack;
import com.Luo_jiyv.SakuraMod.util.PlayerEquipCache;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
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
public final class SakuraAxeItem extends AxeItem {
   private static final String SLASH_TAG = "SakuraAxeSlash";
   private static final Map<Player, Integer> LAST_SLASH_TICK = new WeakHashMap<>();

   public SakuraAxeItem(Tier tier, float damage, float attackSpeed, Properties properties) {
      super(tier, damage, attackSpeed, properties);
   }

   public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.m_21120_(hand);
      if (player.m_6144_() && !player.m_5833_()) {
         if (!level.f_46443_) {
            toggleSlash(player, stack);
         }

         return InteractionResultHolder.m_19092_(stack, level.f_46443_);
      } else {
         return super.m_7203_(level, player, hand);
      }
   }

   public InteractionResult m_6225_(UseOnContext context) {
      Level level = context.m_43725_();
      BlockPos pos = context.m_8083_();
      Player player = context.m_43723_();
      if (player != null && player.m_6144_() && !player.m_5833_()) {
         if (!level.f_46443_) {
            toggleSlash(player, context.m_43722_());
         }

         return InteractionResult.m_19078_(level.f_46443_);
      } else {
         BlockState state = level.m_8055_(pos);
         Block block = state.m_60734_();
         if (player != null
            && !player.m_5833_()
            && (state.m_204336_(BlockTags.f_13104_) || block instanceof SaplingBlock || block instanceof AzaleaBlock || block instanceof FungusBlock)
            && level.m_7966_(player, pos)
            && player.m_36204_(pos, context.m_43719_(), context.m_43722_())
            && BoneMealItem.applyBonemeal(new ItemStack(Items.f_42499_), level, pos, player)) {
            if (!level.f_46443_) {
               level.m_46796_(1505, pos, 0);
               context.m_43722_().m_41622_(1, player, user -> user.m_21190_(context.m_43724_()));
            }

            return InteractionResult.m_19078_(level.f_46443_);
         } else {
            return super.m_6225_(context);
         }
      }
   }

   private static void toggleSlash(Player player, ItemStack axe) {
      boolean enabled = !isSlashEnabled(axe);
      if (enabled) {
         axe.m_41784_().m_128379_("SakuraAxeSlash", true);
      } else {
         axe.m_41749_("SakuraAxeSlash");
      }

      player.m_5661_(Component.m_237115_(enabled ? "item.sakura_mod.sakura_axe.slash.on" : "item.sakura_mod.sakura_axe.slash.off"), true);
   }

   public static boolean isSlashEnabled(ItemStack axe) {
      CompoundTag tag = axe.m_41783_();
      return tag != null && tag.m_128471_("SakuraAxeSlash");
   }

   public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
      launchSlash(player, entity instanceof PartEntity<?> part ? part.getParent() : entity);
      return false;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onLeftClickBlock(LeftClickBlock event) {
      if (event.getAction() == Action.START) {
         launchSlash(event.getEntity(), null);
      }
   }

   public static void launchSlash(Player player, @Nullable Entity primaryTarget) {
      ItemStack axe = player.m_21205_();
      if (player.m_9236_() instanceof ServerLevel level
         && player.m_6084_()
         && !player.m_5833_()
         && !player.m_5803_()
         && !player.m_6117_()
         && axe.m_41720_() instanceof SakuraAxeItem
         && isSlashEnabled(axe)
         && PlayerEquipCache.hasFullSakuraArmor(player)) {
         Integer lastTick = LAST_SLASH_TICK.put(player, player.f_19797_);
         if (lastTick == null || lastTick != player.f_19797_) {
            ItemStack weapon = axe.m_41777_();
            SakuraAxeSlashes.launch(level, player, weapon, SakuraWeaponAttack.capture(player), primaryTarget);
            level.m_6263_(null, player.m_20185_(), player.m_20186_(), player.m_20189_(), SoundEvents.f_12317_, SoundSource.PLAYERS, 1.0F, 0.82F);
            if (primaryTarget == null) {
               player.m_36334_();
               axe.m_41622_(1, player, user -> {
                  user.m_21190_(InteractionHand.MAIN_HAND);
                  ForgeEventFactory.onPlayerDestroyItem(user, weapon.m_41777_(), InteractionHand.MAIN_HAND);
               });
            }
         }
      }
   }
}
