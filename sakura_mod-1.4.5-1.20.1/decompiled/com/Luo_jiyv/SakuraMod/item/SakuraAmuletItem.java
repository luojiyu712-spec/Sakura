package com.Luo_jiyv.SakuraMod.item;

import com.Luo_jiyv.SakuraMod.attribute.ModAttributes;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SakuraAmuletItem extends Item implements ICurioItem {
   public SakuraAmuletItem(Properties properties) {
      super(properties);
   }

   public boolean canEquip(SlotContext slotContext, ItemStack stack) {
      return "charm".equals(slotContext.identifier());
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
      Multimap<Attribute, AttributeModifier> modifiers = ArrayListMultimap.create();
      if ("charm".equals(slotContext.identifier())) {
         modifiers.put((Attribute)ModAttributes.SHIELD_CHARGES.get(), new AttributeModifier(uuid, "sakura_amulet_shield_capacity", 40.0, Operation.ADDITION));
      }

      modifiers.put(Attributes.f_22286_, new AttributeModifier(uuid, "sakura_amulet_luck", 10.0, Operation.ADDITION));
      boolean shared = ModAttributes.hasSharedAmuletAttributes();
      Attribute critChance = (Attribute)(shared ? ModAttributes.SHARED_CRIT_CHANCE : ModAttributes.CRIT_CHANCE).get();
      Attribute critDamage = (Attribute)(shared ? ModAttributes.SHARED_CRIT_DAMAGE : ModAttributes.CRIT_DAMAGE).get();
      Attribute healing = (Attribute)(shared ? ModAttributes.SHARED_HEALING_RECEIVED : ModAttributes.REGEN_BONUS).get();
      Operation multiplier = shared ? Operation.MULTIPLY_TOTAL : Operation.ADDITION;
      modifiers.put(critChance, new AttributeModifier(uuid, "sakura_amulet_crit_chance", 0.3, Operation.ADDITION));
      modifiers.put(critDamage, new AttributeModifier(uuid, "sakura_amulet_crit_damage", 0.6, multiplier));
      modifiers.put(healing, new AttributeModifier(uuid, "sakura_amulet_regen", 0.4, multiplier));
      return modifiers;
   }
}
