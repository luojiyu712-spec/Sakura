package com.Luo_jiyv.SakuraMod.network;

import com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;

public class AssemblerSetRecipePacket {
   private final BlockPos pos;
   private final int menuId;
   private final ResourceLocation recipeId;

   public AssemblerSetRecipePacket(BlockPos pos, int menuId, ResourceLocation recipeId) {
      this.pos = pos;
      this.menuId = menuId;
      this.recipeId = recipeId;
   }

   public static void encode(AssemblerSetRecipePacket p, FriendlyByteBuf buf) {
      buf.m_130064_(p.pos);
      buf.m_130130_(p.menuId);
      buf.m_130085_(p.recipeId);
   }

   public static AssemblerSetRecipePacket decode(FriendlyByteBuf buf) {
      return new AssemblerSetRecipePacket(buf.m_130135_(), buf.m_130242_(), buf.m_130281_());
   }

   public static void handle(AssemblerSetRecipePacket p, Supplier<Context> ctxSup) {
      MolecularAssemblerBlockEntity machine = AssemblerPacketAccess.machine(ctxSup.get().getSender(), p.pos, p.menuId);
      if (machine != null) {
         machine.selectRecipe(p.recipeId);
      }
   }
}
