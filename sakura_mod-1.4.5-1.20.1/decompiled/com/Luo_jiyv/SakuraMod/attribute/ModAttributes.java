package com.Luo_jiyv.SakuraMod.attribute;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(
   modid = "sakura_mod",
   bus = Bus.MOD
)
public class ModAttributes {
   public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, "sakura_mod");
   public static final RegistryObject<Attribute> CRIT_CHANCE = ATTRIBUTES.register(
      "crit_chance", () -> new RangedAttribute("attribute.sakura_mod.crit_chance", 0.0, 0.0, 1.0).m_22084_(true)
   );
   public static final RegistryObject<Attribute> CRIT_DAMAGE = ATTRIBUTES.register(
      "crit_damage", () -> new RangedAttribute("attribute.sakura_mod.crit_damage", 0.0, 0.0, 10.0).m_22084_(true)
   );
   public static final RegistryObject<Attribute> REGEN_BONUS = ATTRIBUTES.register(
      "regen_bonus", () -> new RangedAttribute("attribute.sakura_mod.regen_bonus", 0.0, 0.0, 1000.0).m_22084_(true)
   );
   public static final RegistryObject<Attribute> SHARED_CRIT_CHANCE = attributesLibAttribute("crit_chance");
   public static final RegistryObject<Attribute> SHARED_CRIT_DAMAGE = attributesLibAttribute("crit_damage");
   public static final RegistryObject<Attribute> SHARED_HEALING_RECEIVED = attributesLibAttribute("healing_received");
   public static final int MAX_SHIELD_CAPACITY = 100;
   public static final RegistryObject<Attribute> SHIELD_CHARGES = ATTRIBUTES.register(
      "shield_charges", () -> new ModAttributes.ShieldCapacityAttribute().m_22084_(true)
   );

   private static RegistryObject<Attribute> attributesLibAttribute(String path) {
      return RegistryObject.create(ResourceLocation.fromNamespaceAndPath("attributeslib", path), ForgeRegistries.ATTRIBUTES);
   }

   public static boolean hasSharedAmuletAttributes() {
      return SHARED_CRIT_CHANCE.isPresent() && SHARED_CRIT_DAMAGE.isPresent() && SHARED_HEALING_RECEIVED.isPresent();
   }

   public static void register(IEventBus bus) {
      ATTRIBUTES.register(bus);
   }

   @SubscribeEvent
   public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
      EntityType<? extends LivingEntity> player = EntityType.f_20532_;
      if (!event.has(player, (Attribute)CRIT_CHANCE.get())) {
         event.add(player, (Attribute)CRIT_CHANCE.get(), 0.0);
      }

      if (!event.has(player, (Attribute)CRIT_DAMAGE.get())) {
         event.add(player, (Attribute)CRIT_DAMAGE.get(), 0.0);
      }

      if (!event.has(player, (Attribute)REGEN_BONUS.get())) {
         event.add(player, (Attribute)REGEN_BONUS.get(), 0.0);
      }

      if (!event.has(player, (Attribute)SHIELD_CHARGES.get())) {
         event.add(player, (Attribute)SHIELD_CHARGES.get());
      }
   }

   private static final class ShieldCapacityAttribute extends RangedAttribute {
      private ShieldCapacityAttribute() {
         super("attribute.sakura_mod.shield_charges", 0.0, 0.0, 100.0);
      }

      public double m_147361_() {
         return 0.0;
      }

      public double m_147362_() {
         return 100.0;
      }

      public double m_6740_(double value) {
         return Double.isNaN(value) ? this.m_147361_() : Mth.m_14008_(value, this.m_147361_(), this.m_147362_());
      }
   }
}
