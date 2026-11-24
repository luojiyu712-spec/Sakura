package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntity;
import com.Luo_jiyv.SakuraMod.menu.MolecularAssemblerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

final class AssemblerPacketAccess {
   private AssemblerPacketAccess() {
   }

   @Nullable
   static MolecularAssemblerBlockEntity machine(@Nullable ServerPlayer player, BlockPos pos, int menuId) {
      return player != null
            && player.f_36096_ instanceof MolecularAssemblerMenu menu
            && menu.f_38840_ == menuId
            && menu.getBlockEntity().m_58899_().equals(pos)
            && menu.m_6875_(player)
            && menu.allowConfiguration(player)
         ? menu.getBlockEntity()
         : null;
   }
}
