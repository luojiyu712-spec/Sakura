package com.Luo_jiyv.SakuraMod.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ModToolTiers {
   public static final Tier SAKURA_STEEL = new Tier() {
      public int m_6609_() {
         return 10155;
      }

      public float m_6624_() {
         return 15.0F;
      }

      public float m_6631_() {
         return 6.0F;
      }

      public int m_6604_() {
         return 10;
      }

      public int m_6601_() {
         return 15;
      }

      public Ingredient m_6282_() {
         return Ingredient.m_43929_(new ItemLike[]{(ItemLike)Moditems.SAKURA_BLOCK_ITEM.get()});
      }
   };
}
