package com.Luo_jiyv.SakuraMod.menu;

import com.Luo_jiyv.SakuraMod.block.FoxAnvilBlock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class FoxAnvilMenu extends AnvilMenu {
   private final ContainerLevelAccess access;

   public FoxAnvilMenu(int id, Inventory inventory, ContainerLevelAccess access) {
      super(id, inventory, access);
      this.access = access;
   }

   protected boolean m_8039_(BlockState state) {
      return state.m_60734_() instanceof FoxAnvilBlock;
   }

   protected void m_142365_(Player player, ItemStack stack) {
      super.m_142365_(player, stack);
      if (!player.m_9236_().m_5776_()) {
         if (!player.m_150110_().f_35937_) {
            if (!stack.m_41619_()) {
               this.access.m_39292_((level, pos) -> FoxAnvilBlock.consumeUse(level, pos));
            }
         }
      }
   }
}
