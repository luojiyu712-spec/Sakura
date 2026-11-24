package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class AssemblerSideConfigPacket {
   private final BlockPos pos;
   private final int menuId;
   private final int side;

   public AssemblerSideConfigPacket(BlockPos pos, int menuId, int side) {
      this.pos = pos;
      this.menuId = menuId;
      this.side = side;
   }

   public static void encode(AssemblerSideConfigPacket p, FriendlyByteBuf buf) {
      buf.m_130064_(p.pos);
      buf.m_130130_(p.menuId);
      buf.writeByte(p.side);
   }

   public static AssemblerSideConfigPacket decode(FriendlyByteBuf buf) {
      return new AssemblerSideConfigPacket(buf.m_130135_(), buf.m_130242_(), buf.readByte());
   }

   public static void handle(AssemblerSideConfigPacket p, Supplier<Context> ctxSup) {
      if (p.side >= 0 && p.side < 6) {
         MolecularAssemblerBlockEntity assembler = AssemblerPacketAccess.machine(ctxSup.get().getSender(), p.pos, p.menuId);
         if (assembler != null) {
            assembler.cycleSideConfig(p.side);
         }
      }
   }
}
