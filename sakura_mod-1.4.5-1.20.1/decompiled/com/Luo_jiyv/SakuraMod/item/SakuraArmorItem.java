package com.Luo_jiyv.SakuraMod.item;

import com.Luo_jiyv.SakuraMod.attribute.ModAttributes;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class SakuraArmorItem extends ArmorItem {
   private final Supplier<Multimap<Attribute, AttributeModifier>> wornAttributes;

   public SakuraArmorItem(ArmorMaterial material, Type type, Properties properties) {
      super(material, type, properties);
      EquipmentSlot wornSlot = this.m_40402_();
      UUID shieldId = UUID.nameUUIDFromBytes(("sakura_mod:armor_shield_" + wornSlot.m_20751_()).getBytes(StandardCharsets.UTF_8));
      this.wornAttributes = Suppliers.memoize(
         () -> ImmutableMultimap.builder()
            .putAll(super.m_7167_(wornSlot))
            .put((Attribute)ModAttributes.SHIELD_CHARGES.get(), new AttributeModifier(shieldId, "sakura_armor_shield_capacity", 10.0, Operation.ADDITION))
            .build()
      );
   }

   public Multimap<Attribute, AttributeModifier> m_7167_(EquipmentSlot slot) {
      return slot == this.m_40402_() ? this.wornAttributes.get() : super.m_7167_(slot);
   }
}
