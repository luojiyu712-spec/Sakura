package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

public class AssemblerSettingsPacket {
   public static final byte ACT_CYCLE_MODE = 0;
   public static final byte ACT_TOGGLE_EJECT = 1;
   public static final byte ACT_TOGGLE_AMOUNT = 2;
   public static final byte ACT_TOGGLE_NET_EXTRACT = 3;
   public static final byte ACT_TOGGLE_NET_INSERT = 4;
   public static final byte ACT_TOGGLE_NET_ENERGY = 5;
   public static final byte ACT_BIND_PRIMARY = 6;
   public static final byte ACT_SELECT_RECIPE = 7;
   private final BlockPos pos;
   private final int menuId;
   private final byte action;
   private final int value;

   public AssemblerSettingsPacket(BlockPos pos, int menuId, byte action, int value) {
      this.pos = pos;
      this.menuId = menuId;
      this.action = action;
      this.value = value;
   }

   public static void encode(AssemblerSettingsPacket p, FriendlyByteBuf buf) {
      buf.m_130064_(p.pos);
      buf.m_130130_(p.menuId);
      buf.writeByte(p.action);
      buf.m_130130_(p.value);
   }

   public static AssemblerSettingsPacket decode(FriendlyByteBuf buf) {
      return new AssemblerSettingsPacket(buf.m_130135_(), buf.m_130242_(), buf.readByte(), buf.m_130242_());
   }

   public static void handle(AssemblerSettingsPacket p, Supplier<Context> ctxSup) {
      MolecularAssemblerBlockEntity assembler = AssemblerPacketAccess.machine(ctxSup.get().getSender(), p.pos, p.menuId);
      if (assembler != null) {
         switch (p.action) {
            case 0:
               assembler.cycleWorkMode();
               break;
            case 1:
               assembler.toggleAutoEject();
               break;
            case 2:
               assembler.toggleCraftAmount();
               break;
            case 3:
               assembler.toggleNetExtract();
               break;
            case 4:
               assembler.toggleNetInsert();
               break;
            case 5:
               assembler.toggleNetEnergy();
               break;
            case 6:
               assembler.bindPrimaryNet(ctxSup.get().getSender());
               break;
            case 7:
               assembler.selectRecipe(p.value);
         }
      }
   }
}
