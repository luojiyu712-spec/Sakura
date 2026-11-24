package com.Luo_jiyv.SakuraMod.block;

import net.minecraft.util.StringRepresentable;

public enum ControllerState implements StringRepresentable {
   OFFLINE("offline"),
   ONLINE("online"),
   CONFLICTED("conflicted");

   private final String name;

   private ControllerState(String name) {
      this.name = name;
   }

   public String m_7912_() {
      return this.name;
   }
}
