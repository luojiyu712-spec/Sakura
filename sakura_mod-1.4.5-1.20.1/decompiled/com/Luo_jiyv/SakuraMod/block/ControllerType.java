package com.Luo_jiyv.SakuraMod.block;

import net.minecraft.util.StringRepresentable;

public enum ControllerType implements StringRepresentable {
   BLOCK("block"),
   COLUMN_X("column_x"),
   COLUMN_Y("column_y"),
   COLUMN_Z("column_z"),
   INSIDE_A("inside_a"),
   INSIDE_B("inside_b");

   private final String name;

   private ControllerType(String name) {
      this.name = name;
   }

   public String m_7912_() {
      return this.name;
   }
}
