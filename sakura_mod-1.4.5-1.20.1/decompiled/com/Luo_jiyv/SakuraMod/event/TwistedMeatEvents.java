package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.effect.ModEffects;
import com.Luo_jiyv.SakuraMod.item.Moditems;
import com.Luo_jiyv.SakuraMod.util.AdvancementUtils;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class TwistedMeatEvents {
   private static final String WINDOW_START = "TwistedMeatWindowStart";
   private static final String WINDOW_COUNT = "TwistedMeatWindowCount";
   private static final String COOKED_COUNT = "TwistedMeatCookedCount";
   private static final int WINDOW_TICKS = 200;
   private static final int WINDOW_REQUIRED = 10;
   private static final int EFFECT_TICKS = 3600;
   private static final int EXTEND_TICKS = 1200;

   @SubscribeEvent
   public static void onFinishUse(Finish event) {
      if (event.getEntity() instanceof Player player) {
         if (!player.m_9236_().m_5776_()) {
            ItemStack stack = event.getItem();
            boolean raw = stack.m_150930_((Item)Moditems.TWISTED_MEAT_RAW.get());
            boolean cooked = stack.m_150930_((Item)Moditems.TWISTED_MEAT_COOKED.get());
            if (raw || cooked) {
               ThreadLocalRandom random = ThreadLocalRandom.current();
               if (raw) {
                  if (random.nextFloat() < 0.02F) {
                     player.m_7292_(new MobEffectInstance(MobEffects.f_19614_, 120, 0));
                  }

                  if (random.nextFloat() < 0.5F) {
                     player.m_7292_(new MobEffectInstance(MobEffects.f_19612_, 600, 0));
                  }
               } else if (random.nextFloat() < 0.02F) {
                  player.m_7292_(new MobEffectInstance(MobEffects.f_19612_, 600, 0));
               }

               if (player instanceof ServerPlayer serverPlayer) {
                  AdvancementUtils.award(serverPlayer, "sakura_mod:story/twisted_meat_gourmet", "trigger");
               }

               CompoundTag data = player.getPersistentData();
               if (cooked) {
                  int cookedCount = data.m_128451_("TwistedMeatCookedCount") + 1;
                  data.m_128405_("TwistedMeatCookedCount", cookedCount);
                  if (cookedCount >= 64 && player instanceof ServerPlayer serverPlayer) {
                     AdvancementUtils.award(serverPlayer, "sakura_mod:story/twisted_meat_64", "trigger");
                  }
               }

               MobEffectInstance growth = player.m_21124_((MobEffect)ModEffects.TWISTED_GROWTH.get());
               if (growth != null) {
                  int duration = growth.m_19557_() + 1200;
                  player.m_21195_((MobEffect)ModEffects.TWISTED_GROWTH.get());
                  player.m_7292_(new MobEffectInstance((MobEffect)ModEffects.TWISTED_GROWTH.get(), duration, 0, false, true, true));
               } else {
                  long now = player.m_9236_().m_46467_();
                  long start = data.m_128454_("TwistedMeatWindowStart");
                  int count = data.m_128451_("TwistedMeatWindowCount");
                  if (start == 0L || now - start > 200L) {
                     start = now;
                     count = 0;
                  }

                  if (++count >= 10) {
                     data.m_128356_("TwistedMeatWindowStart", 0L);
                     data.m_128405_("TwistedMeatWindowCount", 0);
                     player.m_7292_(new MobEffectInstance((MobEffect)ModEffects.TWISTED_GROWTH.get(), 3600, 0, false, true, true));
                  } else {
                     data.m_128356_("TwistedMeatWindowStart", start);
                     data.m_128405_("TwistedMeatWindowCount", count);
                  }
               }
            }
         }
      }
   }
}
