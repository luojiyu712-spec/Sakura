package com.Luo_jiyv.SakuraMod.client.renderer;

import com.Luo_jiyv.SakuraMod.block.MolecularAssemblerBlock;
import com.Luo_jiyv.SakuraMod.blockentity.MolecularAssemblerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MolecularAssemblerRenderer implements BlockEntityRenderer<MolecularAssemblerBlockEntity> {
   private static final ResourceLocation LIGHTS = ResourceLocation.fromNamespaceAndPath("sakura_mod", "block/molecular_assembler_lights");
   private static final RenderType GLOW_TYPE = RenderType.m_234338_(TextureAtlas.f_118259_);
   private static final float IN0 = 0.125F;
   private static final float IN1 = 0.875F;
   private static final float UV0 = 0.125F;
   private static final float UV1 = 0.875F;
   private static final float PAD = 0.00125F;

   public MolecularAssemblerRenderer(Context context) {
   }

   public void render(MolecularAssemblerBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      BlockState state = be.m_58900_();
      if (state.m_61138_(MolecularAssemblerBlock.LIT)) {
         if ((Boolean)state.m_61143_(MolecularAssemblerBlock.LIT)) {
            TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.m_91087_().m_91258_(InventoryMenu.f_39692_).apply(LIGHTS);
            if (sprite != null) {
               VertexConsumer consumer = buffer.m_6299_(GLOW_TYPE);
               if (consumer != null) {
                  Vec3 camera = Minecraft.m_91087_().f_91063_.m_109153_().m_90583_();
                  this.cube(poseStack, consumer, sprite, 0.12375F, 0.12375F, 0.12375F, 0.87625F, 0.87625F, 0.87625F, be.m_58899_(), camera);
                  ItemStack display = be.getDisplayStack();
                  if (be.isClientRunning() && !display.m_41619_()) {
                     Level level = be.m_58904_();
                     float time = (level == null ? 0.0F : (float)level.m_46467_()) + partialTick;
                     poseStack.m_85836_();
                     poseStack.m_85837_(0.5, 0.4, 0.5);
                     poseStack.m_85841_(0.7F, 0.7F, 0.7F);
                     Minecraft.m_91087_()
                        .m_91291_()
                        .m_269128_(display, ItemDisplayContext.GROUND, 15728880, OverlayTexture.f_118083_, poseStack, buffer, level, 0);
                     poseStack.m_85849_();
                  }
               }
            }
         }
      }
   }

   private void cube(
      PoseStack poseStack,
      VertexConsumer consumer,
      TextureAtlasSprite sprite,
      float x0,
      float y0,
      float z0,
      float x1,
      float y1,
      float z1,
      BlockPos pos,
      Vec3 camera
   ) {
      Pose pose = poseStack.m_85850_();
      float du = sprite.m_118410_() - sprite.m_118409_();
      float dv = sprite.m_118412_() - sprite.m_118411_();
      float u0 = sprite.m_118409_() + du * 0.125F;
      float u1 = sprite.m_118409_() + du * 0.875F;
      float v0 = sprite.m_118411_() + dv * 0.125F;
      float v1 = sprite.m_118411_() + dv * 0.875F;
      if (faceVisible(pos, Direction.DOWN, camera)) {
         this.quad(consumer, pose, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, u0, u1, v0, v1, 0.0F, -1.0F, 0.0F);
      }

      if (faceVisible(pos, Direction.UP, camera)) {
         this.quad(consumer, pose, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, u0, u1, v0, v1, 0.0F, 1.0F, 0.0F);
      }

      if (faceVisible(pos, Direction.NORTH, camera)) {
         this.quad(consumer, pose, x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0, u0, u1, v0, v1, 0.0F, 0.0F, -1.0F);
      }

      if (faceVisible(pos, Direction.SOUTH, camera)) {
         this.quad(consumer, pose, x1, y0, z1, x0, y0, z1, x0, y1, z1, x1, y1, z1, u0, u1, v0, v1, 0.0F, 0.0F, 1.0F);
      }

      if (faceVisible(pos, Direction.WEST, camera)) {
         this.quad(consumer, pose, x0, y0, z1, x0, y0, z0, x0, y1, z0, x0, y1, z1, u0, u1, v0, v1, -1.0F, 0.0F, 0.0F);
      }

      if (faceVisible(pos, Direction.EAST, camera)) {
         this.quad(consumer, pose, x1, y0, z0, x1, y0, z1, x1, y1, z1, x1, y1, z0, u0, u1, v0, v1, 1.0F, 0.0F, 0.0F);
      }
   }

   private static boolean faceVisible(BlockPos pos, Direction side, Vec3 camera) {
      double dx = camera.f_82479_ - (pos.m_123341_() + 0.5);
      double dy = camera.f_82480_ - (pos.m_123342_() + 0.5);
      double dz = camera.f_82481_ - (pos.m_123343_() + 0.5);
      double dot = dx * side.m_122429_() + dy * side.m_122430_() + dz * side.m_122431_();
      return dot > 0.0;
   }

   private void quad(
      VertexConsumer consumer,
      Pose pose,
      float ax,
      float ay,
      float az,
      float bx,
      float by,
      float bz,
      float cx,
      float cy,
      float cz,
      float dx,
      float dy,
      float dz,
      float u0,
      float u1,
      float v0,
      float v1,
      float nx,
      float ny,
      float nz
   ) {
      consumer.m_252986_(pose.m_252922_(), ax, ay, az)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(u0, v1)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_252939_(pose.m_252943_(), nx, ny, nz)
         .m_5752_();
      consumer.m_252986_(pose.m_252922_(), bx, by, bz)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(u1, v1)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_252939_(pose.m_252943_(), nx, ny, nz)
         .m_5752_();
      consumer.m_252986_(pose.m_252922_(), cx, cy, cz)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(u1, v0)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_252939_(pose.m_252943_(), nx, ny, nz)
         .m_5752_();
      consumer.m_252986_(pose.m_252922_(), dx, dy, dz)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(u0, v0)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_252939_(pose.m_252943_(), nx, ny, nz)
         .m_5752_();
   }
}
