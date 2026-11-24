package com.Luo_jiyv.SakuraMod.event;

import com.Luo_jiyv.SakuraMod.effect.ModEffects;
import com.Luo_jiyv.SakuraMod.util.PlayerEquipCache;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class AbsoluteMaterialismEvents {
   private static final UUID MB_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567801");
   private static final UUID MT_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567802");
   private static final String MB_NAME = "sakura_materialism_suppress_base";
   private static final String MT_NAME = "sakura_materialism_suppress_total";
   private static final double ZERO_MULTIPLIER = -1.0;
   private static final int CHECK_INTERVAL = 10;
   private static final int EFFECT_DURATION = 200;
   private static final int EFFECT_REFRESH_THRESHOLD = 40;
   private static final String IRONS_SCROLL_CLASS = "io.redspace.ironsspellbooks.item.Scroll";
   private static volatile List<Attribute> cachedManaAttributes;
   private static volatile boolean ironSpellsInitialized = false;
   private static volatile boolean ironSpellsAvailable = false;
   private static Class<?> magicDataClass;
   private static Capability<?> magicDataCapability;
   private static Method setManaInt;
   private static Method setCurrentManaFloat;
   private static volatile boolean scrollClassInitialized = false;
   private static Class<?> scrollItemClass;

   private static List<Attribute> manaAttributes() {
      List<Attribute> cached = cachedManaAttributes;
      if (cached != null) {
         return cached;
      } else {
         List<Attribute> list = new ArrayList<>();

         for (Entry<ResourceKey<Attribute>, Attribute> entry : ForgeRegistries.ATTRIBUTES.getEntries()) {
            String name = entry.getKey().m_135782_().m_135815_().toLowerCase(Locale.ROOT);
            if (name.contains("mana")) {
               list.add(entry.getValue());
            }
         }

         cachedManaAttributes = list;
         return list;
      }
   }

   private static synchronized void initIronSpells() {
      if (!ironSpellsInitialized) {
         ironSpellsInitialized = true;

         try {
            magicDataClass = Class.forName("io.redspace.ironsspellbooks.api.magic.MagicData");
            Class<?> holderClass = Class.forName("io.redspace.ironsspellbooks.api.magic.MagicDataHolder");
            magicDataCapability = (Capability<?>)holderClass.getField("MAGIC_DATA").get(null);

            try {
               setManaInt = magicDataClass.getMethod("setMana", int.class);
            } catch (NoSuchMethodException var3) {
            }

            try {
               setCurrentManaFloat = magicDataClass.getMethod("setCurrentMana", float.class);
            } catch (NoSuchMethodException var2) {
            }

            ironSpellsAvailable = true;
         } catch (Throwable var4) {
            ironSpellsAvailable = false;
         }
      }
   }

   private static void initScrollClass() {
      if (!scrollClassInitialized) {
         scrollClassInitialized = true;

         try {
            scrollItemClass = Class.forName("io.redspace.ironsspellbooks.item.Scroll");
         } catch (Throwable var1) {
            scrollItemClass = null;
         }
      }
   }

   private static boolean isSpellScroll(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else {
         if (!scrollClassInitialized) {
            initScrollClass();
         }

         return scrollItemClass == null ? false : scrollItemClass.isInstance(stack.m_41720_());
      }
   }

   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      if (event.getSource().m_7639_() instanceof Player player) {
         if (player.m_21023_((MobEffect)ModEffects.ABSOLUTE_MATERIALISM.get())) {
            DamageSource source = event.getSource();
            if (!source.m_276093_(DamageTypes.f_268515_)
               && !source.m_276093_(DamageTypes.f_268530_)
               && !source.m_276093_(DamageTypes.f_268679_)
               && !source.m_276093_(DamageTypes.f_268482_)
               && !source.m_276093_(DamageTypes.f_268440_)) {
               event.setAmount(event.getAmount() * 1.02F);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onRightClickItem(RightClickItem event) {
      Player player = event.getEntity();
      if (player.m_21023_((MobEffect)ModEffects.ABSOLUTE_MATERIALISM.get())) {
         ItemStack stack = event.getItemStack();
         if (isSpellScroll(stack)) {
            if (!event.getLevel().m_5776_()) {
               player.m_5661_(Component.m_237115_("item.sakura_mod.absolute_materialism.scroll_blocked").m_130940_(ChatFormatting.RED), true);
            }

            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END) {
         Player player = event.player;
         if (!player.m_9236_().m_5776_()) {
            if (player.f_19797_ % 10 == 0) {
               boolean wearingSakuraArmor = hasSakuraArmor(player);
               if (wearingSakuraArmor) {
                  MobEffectInstance current = player.m_21124_((MobEffect)ModEffects.ABSOLUTE_MATERIALISM.get());
                  if (current == null || current.m_19557_() < 40) {
                     player.m_7292_(new MobEffectInstance((MobEffect)ModEffects.ABSOLUTE_MATERIALISM.get(), 200, 0, false, false, true));
                  }

                  suppressManaAttributes(player);
                  suppressIronSpellsMana(player);
               } else {
                  player.m_21195_((MobEffect)ModEffects.ABSOLUTE_MATERIALISM.get());
                  restoreManaAttributes(player);
               }
            }
         }
      }
   }

   private static void suppressManaAttributes(Player player) {
      for (Attribute attr : manaAttributes()) {
         AttributeInstance instance = player.m_21051_(attr);
         if (instance != null) {
            if (instance.m_22111_(MB_UUID) == null) {
               instance.m_22118_(new AttributeModifier(MB_UUID, "sakura_materialism_suppress_base", -1.0, Operation.MULTIPLY_BASE));
            }

            if (instance.m_22111_(MT_UUID) == null) {
               instance.m_22118_(new AttributeModifier(MT_UUID, "sakura_materialism_suppress_total", -1.0, Operation.MULTIPLY_TOTAL));
            }
         }
      }
   }

   private static void restoreManaAttributes(Player player) {
      for (Attribute attr : manaAttributes()) {
         AttributeInstance instance = player.m_21051_(attr);
         if (instance != null) {
            if (instance.m_22111_(MB_UUID) != null) {
               instance.m_22120_(MB_UUID);
            }

            if (instance.m_22111_(MT_UUID) != null) {
               instance.m_22120_(MT_UUID);
            }
         }
      }
   }

   private static void suppressIronSpellsMana(Player player) {
      if (!ironSpellsInitialized) {
         initIronSpells();
      }

      if (ironSpellsAvailable) {
         try {
            Optional<?> holder = player.getCapability(magicDataCapability).resolve();
            if (holder.isEmpty()) {
               return;
            }

            Object magicData = holder.get();
            if (setManaInt != null) {
               setManaInt.invoke(magicData, 0);
            } else if (setCurrentManaFloat != null) {
               setCurrentManaFloat.invoke(magicData, 0.0F);
            }
         } catch (Throwable var3) {
         }
      }
   }

   private static boolean hasSakuraArmor(Player player) {
      return PlayerEquipCache.countSakuraArmor(player) > 0;
   }
}
