package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.util.GoldenCubeData;
import com.Luo_jiyv.SakuraMod.util.GoldenCubeWhispers;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public final class GoldenCubeClientEffects {
   private static final int MAX_TEXTS = 48;
   private static final RandomSource RANDOM = RandomSource.m_216327_();
   private static final GoldenCubeWhispers WHISPERS = new GoldenCubeWhispers(RANDOM);
   private static final List<GoldenCubeClientEffects.Whisper> TEXTS = new ArrayList<>();
   private static final BufferSource TEXT_BUFFERS = MultiBufferSource.m_109898_(new BufferBuilder(4096));
   private static int mode;
   private static int nextBatch;
   private static ClientLevel currentLevel;
   private static LocalPlayer currentPlayer;

   private GoldenCubeClientEffects() {
   }

   public static void setMode(int newMode) {
      int clamped = Mth.m_14045_(newMode, 0, 7);
      Minecraft minecraft = Minecraft.m_91087_();
      if (mode != clamped || currentLevel != minecraft.f_91073_ || currentPlayer != minecraft.f_91074_) {
         mode = clamped;
         TEXTS.clear();
         WHISPERS.reset();
         nextBatch = 0;
         currentLevel = minecraft.f_91073_;
         currentPlayer = minecraft.f_91074_;
      }
   }

   public static int getMode() {
      return mode;
   }

   public static void reset() {
      mode = 0;
      nextBatch = 0;
      TEXTS.clear();
      WHISPERS.reset();
      currentLevel = null;
      currentPlayer = null;
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         Minecraft minecraft = Minecraft.m_91087_();
         if (minecraft.f_91073_ != currentLevel || minecraft.f_91074_ != currentPlayer) {
            reset();
            currentLevel = minecraft.f_91073_;
            currentPlayer = minecraft.f_91074_;
         }

         if (currentPlayer == null || !currentPlayer.m_6084_() || currentPlayer.m_7500_() || currentPlayer.m_5833_()) {
            reset();
         } else if (mode != 0 && !minecraft.m_91104_()) {
            TEXTS.removeIf(text -> ++text.age >= text.lifetime || text.position.m_82557_(currentPlayer.m_146892_()) > 1024.0);
            if (--nextBatch <= 0) {
               spawnBatch(minecraft);
               nextBatch = mode == 1 ? 80 : 70 - mode * 7;
            }
         }
      }
   }

   private static void spawnBatch(Minecraft minecraft) {
      int count = mode == 1 ? 6 : 5 + mode * 2;
      int capacity = Math.min(48, GoldenCubeWhispers.poolSize(mode) - 9);
      Set<String> visible = new HashSet<>();

      for (GoldenCubeClientEffects.Whisper text : TEXTS) {
         visible.add(text.key);
      }

      Vec3 eye = currentPlayer.m_146892_();
      double facing = Math.toRadians(currentPlayer.m_146908_());
      int frontCount = count / 2;

      for (int i = 0; i < count; i++) {
         double angle = i < frontCount ? facing + ((i + RANDOM.m_188500_()) / frontCount - 0.5) * 1.9 : RANDOM.m_188500_() * Math.PI * 2.0;
         double radius = 3.0 + RANDOM.m_188500_() * 3.0;
         Vec3 position = eye.m_82520_(-Math.sin(angle) * radius, -0.8 + RANDOM.m_188500_() * 2.1, Math.cos(angle) * radius);
         if (TEXTS.size() >= capacity) {
            visible.remove(TEXTS.remove(0).key);
         }

         String key = WHISPERS.nextKey(mode, visible);
         visible.add(key);
         MutableComponent content = Component.m_237115_(key);
         if (mode >= 4 && RANDOM.m_188501_() < (mode - 3) * 0.12F) {
            content.m_7220_(Component.m_237113_(" [" + "RETURN".repeat(mode - 3) + "]").m_130940_(ChatFormatting.OBFUSCATED));
         }

         int color = mode == 7 ? 16717848 : (mode == 1 ? 16765802 : (mode >= 5 ? 15901619 : 14399476));
         TEXTS.add(
            new GoldenCubeClientEffects.Whisper(
               minecraft.f_91062_, key, content, position, 80 + RANDOM.m_188503_(41), 0.011F + RANDOM.m_188501_() * 0.004F, color
            )
         );
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onComputeFov(ComputeFovModifierEvent event) {
      Minecraft minecraft = Minecraft.m_91087_();
      if (event.getPlayer() == minecraft.f_91074_) {
         AttributeInstance speed = event.getPlayer().m_21051_(Attributes.f_22279_);
         if (speed != null) {
            AttributeModifier slow = speed.m_22111_(GoldenCubeData.WITHDRAWAL_SLOW_UUID);
            if (slow != null && slow.m_22217_() == Operation.MULTIPLY_TOTAL) {
               double multiplier = 1.0 + slow.m_22218_();
               double walkingSpeed = event.getPlayer().m_150110_().m_35947_();
               if (!(multiplier <= 0.0) && !(multiplier >= 1.0) && !(walkingSpeed <= 0.0)) {
                  double currentFactor = (speed.m_22135_() / walkingSpeed + 1.0) / 2.0;
                  double restoredFactor = (speed.m_22135_() / multiplier / walkingSpeed + 1.0) / 2.0;
                  double correction = event.getFovModifier() * (restoredFactor / currentFactor - 1.0) * (Double)minecraft.f_91066_.m_231925_().m_231551_();
                  if (Double.isFinite(correction)) {
                     event.setNewFovModifier(event.getNewFovModifier() + (float)correction);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onRenderLevel(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_PARTICLES && mode != 0 && !TEXTS.isEmpty()) {
         Minecraft minecraft = Minecraft.m_91087_();
         if (minecraft.f_91073_ == currentLevel
            && minecraft.f_91074_ == currentPlayer
            && currentPlayer != null
            && currentPlayer.m_6084_()
            && !currentPlayer.m_7500_()
            && !currentPlayer.m_5833_()) {
            PoseStack poseStack = event.getPoseStack();
            Camera camera = event.getCamera();
            Font font = minecraft.f_91062_;

            for (GoldenCubeClientEffects.Whisper text : TEXTS) {
               float age = text.age + event.getPartialTick();
               float opacity = Mth.m_14036_(age / 10.0F, 0.0F, 1.0F) * Mth.m_14036_((text.lifetime - age) / 20.0F, 0.0F, 1.0F);
               int alpha = (int)(opacity * 230.0F);
               if (alpha >= 8) {
                  Vec3 position = text.position.m_82520_(0.0, age * 0.003, 0.0).m_82546_(camera.m_90583_());
                  poseStack.m_85836_();
                  poseStack.m_85837_(position.f_82479_, position.f_82480_, position.f_82481_);
                  poseStack.m_252781_(camera.m_253121_());
                  poseStack.m_85841_(-text.scale, -text.scale, text.scale);

                  for (int i = 0; i < text.lines.size(); i++) {
                     font.m_272191_(
                        text.lines.get(i),
                        -text.widths[i] / 2.0F,
                        (i - text.lines.size() / 2.0F) * 9.0F,
                        alpha << 24 | text.color,
                        true,
                        poseStack.m_85850_().m_252922_(),
                        TEXT_BUFFERS,
                        DisplayMode.SEE_THROUGH,
                        0,
                        15728880
                     );
                  }

                  poseStack.m_85849_();
               }
            }

            TEXT_BUFFERS.m_109911_();
         }
      }
   }

   @SubscribeEvent
   public static void onLoggingOut(LoggingOut event) {
      reset();
   }

   private static final class Whisper {
      private final String key;
      private final List<FormattedCharSequence> lines;
      private final int[] widths;
      private final Vec3 position;
      private final int lifetime;
      private final float scale;
      private final int color;
      private int age;

      private Whisper(Font font, String key, Component text, Vec3 position, int lifetime, float scale, int color) {
         this.key = key;
         this.lines = font.m_92923_(text, 160);
         this.widths = this.lines.stream().mapToInt(font::m_92724_).toArray();
         this.position = position;
         this.lifetime = lifetime;
         this.scale = scale;
         this.color = color;
      }
   }
}
