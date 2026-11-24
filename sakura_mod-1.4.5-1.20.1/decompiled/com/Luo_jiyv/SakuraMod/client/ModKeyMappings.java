package com.Luo_jiyv.SakuraMod.client;

import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;

@OnlyIn(Dist.CLIENT)
public class ModKeyMappings {
   public static final String CATEGORY = "key.categories.sakura_mod";
   public static final KeyMapping GRAV_CORE_ACTIVATE = new KeyMapping(
      "key.sakura_mod.grav_core_activate", KeyConflictContext.IN_GAME, Type.KEYSYM, 82, "key.categories.sakura_mod"
   );
   public static final KeyMapping GRAV_CORE_MODE_SWITCH = new KeyMapping(
      "key.sakura_mod.grav_core_mode_switch", KeyConflictContext.IN_GAME, Type.KEYSYM, 71, "key.categories.sakura_mod"
   );
   public static final KeyMapping EDIT_SHIELD_HUD = new KeyMapping(
      "key.sakura_mod.edit_shield_hud", KeyConflictContext.IN_GAME, Type.KEYSYM, 72, "key.categories.sakura_mod"
   );
   public static final KeyMapping TOGGLE_ARMOR_REVIVE = new KeyMapping(
      "key.sakura_mod.toggle_armor_revive", KeyConflictContext.IN_GAME, Type.KEYSYM, -1, "key.categories.sakura_mod"
   );
   public static final KeyMapping TOGGLE_AUTO_REPAIR = new KeyMapping(
      "key.sakura_mod.toggle_auto_repair", KeyConflictContext.IN_GAME, Type.KEYSYM, -1, "key.categories.sakura_mod"
   );
}
