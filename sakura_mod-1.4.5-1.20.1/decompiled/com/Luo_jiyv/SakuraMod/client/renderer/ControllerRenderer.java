package com.Luo_jiyv.SakuraMod.client.renderer;

import com.Luo_jiyv.SakuraMod.block.ControllerBlock;
import com.Luo_jiyv.SakuraMod.block.ControllerState;
import com.Luo_jiyv.SakuraMod.blockentity.ControllerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;

public class ControllerRenderer implements BlockEntityRenderer<ControllerBlockEntity> {
   private static final ResourceLocation RING_TEXTURE = ResourceLocation.parse("sakura_mod:textures/block/controller_inside_a.png");
   private static final RenderType RING_TYPE = RenderType.m_234338_(RING_TEXTURE);
   private static final RenderType GLOW_TYPE = RenderType.m_234338_(TextureAtlas.f_118259_);
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final RandomSource RANDOM = RandomSource.m_216327_();
   private static final Map<BlockState, ControllerRenderer.Glow> GLOW_QUADS = new HashMap<>();

   public ControllerRenderer(Context context) {
   }

   private static List<BakedQuad> glowQuads(BlockState state, BakedModel model) {
      ControllerRenderer.Glow cached = GLOW_QUADS.get(state);
      if (cached != null && cached.model() == model) {
         return cached.quads();
      } else {
         List<BakedQuad> out = new ArrayList<>();

         for (Direction side : DIRECTIONS) {
            collectGlowQuads(out, model.getQuads(state, side, RANDOM, ModelData.EMPTY, null));
         }

         collectGlowQuads(out, model.getQuads(state, null, RANDOM, ModelData.EMPTY, null));
         GLOW_QUADS.put(state, new ControllerRenderer.Glow(model, out));
         return out;
      }
   }

   private static void collectGlowQuads(List<BakedQuad> out, List<BakedQuad> quads) {
      for (BakedQuad quad : quads) {
         String path = quad.m_173410_().m_245424_().m_246162_().m_135815_();
         if (path.contains("lights") || path.contains("conflict")) {
            out.add(quad);
         }
      }
   }

   public boolean shouldRender(ControllerBlockEntity be, Vec3 camera) {
      return be.m_58904_() instanceof ClientLevel && be.m_58900_().m_61143_(ControllerBlock.STATE) != ControllerState.OFFLINE && super.m_142756_(be, camera);
   }

   public void render(ControllerBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      Level level = be.m_58904_();
      if (level != null) {
         boolean isRealDimension = level instanceof ClientLevel;
         BlockState state = be.m_58900_();
         ControllerState controllerState = (ControllerState)state.m_61143_(ControllerBlock.STATE);
         if (isRealDimension && controllerState == ControllerState.ONLINE) {
            poseStack.m_85836_();
            poseStack.m_85837_(0.5, 0.5, 0.5);
            float time = ((float)level.m_46467_() + partialTick) % 360.0F;
            poseStack.m_252781_(Axis.f_252436_.m_252977_(time));
            VertexConsumer consumer = buffer.m_6299_(RING_TYPE);
            if (consumer != null) {
               this.renderRingPlane(poseStack, consumer);
            }

            poseStack.m_85849_();
         }

         if (isRealDimension && (controllerState == ControllerState.ONLINE || controllerState == ControllerState.CONFLICTED)) {
            BakedModel model = Minecraft.m_91087_().m_91289_().m_110910_(state);
            VertexConsumer consumer = buffer.m_6299_(GLOW_TYPE);
            if (consumer == null) {
               return;
            }

            poseStack.m_85836_();
            poseStack.m_252880_(-0.001F, -0.001F, -0.001F);
            poseStack.m_85841_(1.002F, 1.002F, 1.002F);
            this.renderGlowQuads(poseStack, consumer, glowQuads(state, model));
            poseStack.m_85849_();
         }
      }
   }

   private void renderGlowQuads(PoseStack poseStack, VertexConsumer consumer, List<BakedQuad> quads) {
      Pose pose = poseStack.m_85850_();

      for (BakedQuad quad : quads) {
         int[] vertices = quad.m_111303_();

         for (int i = 0; i < 4; i++) {
            int offset = i * 8;
            float x = Float.intBitsToFloat(vertices[offset]);
            float y = Float.intBitsToFloat(vertices[offset + 1]);
            float z = Float.intBitsToFloat(vertices[offset + 2]);
            float u = Float.intBitsToFloat(vertices[offset + 4]);
            float v = Float.intBitsToFloat(vertices[offset + 5]);
            consumer.m_252986_(pose.m_252922_(), x, y, z)
               .m_6122_(255, 255, 255, 255)
               .m_7421_(u, v)
               .m_86008_(OverlayTexture.f_118083_)
               .m_85969_(15728880)
               .m_252939_(pose.m_252943_(), quad.m_111306_().m_122429_(), quad.m_111306_().m_122430_(), quad.m_111306_().m_122431_())
               .m_5752_();
         }
      }
   }

   private void renderRingPlane(PoseStack poseStack, VertexConsumer consumer) {
      Matrix4f matrix = poseStack.m_85850_().m_252922_();
      float size = 0.4F;
      float zOffset = 0.001F;
      consumer.m_252986_(matrix, -size, -size, zOffset)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(0.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 0.0F, 1.0F)
         .m_5752_();
      consumer.m_252986_(matrix, -size, size, zOffset)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(0.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 0.0F, 1.0F)
         .m_5752_();
      consumer.m_252986_(matrix, size, size, zOffset)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(1.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 0.0F, 1.0F)
         .m_5752_();
      consumer.m_252986_(matrix, size, -size, zOffset)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(1.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 0.0F, 1.0F)
         .m_5752_();
      consumer.m_252986_(matrix, size, -size, -zOffset)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(1.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 0.0F, -1.0F)
         .m_5752_();
      consumer.m_252986_(matrix, size, size, -zOffset)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(1.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 0.0F, -1.0F)
         .m_5752_();
      consumer.m_252986_(matrix, -size, size, -zOffset)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(0.0F, 1.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 0.0F, -1.0F)
         .m_5752_();
      consumer.m_252986_(matrix, -size, -size, -zOffset)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(0.0F, 0.0F)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(15728880)
         .m_5601_(0.0F, 0.0F, -1.0F)
         .m_5752_();
   }

   private record Glow(BakedModel model, List<BakedQuad> quads) {
   }
}
