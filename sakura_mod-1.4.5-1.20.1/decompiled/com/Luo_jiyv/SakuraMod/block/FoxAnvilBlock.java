package com.Luo_jiyv.SakuraMod.block;

import com.Luo_jiyv.SakuraMod.blockentity.FoxAnvilBlockEntity;
import com.Luo_jiyv.SakuraMod.menu.FoxAnvilMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Fox.Type;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class FoxAnvilBlock extends AnvilBlock implements EntityBlock {
   public static final int MAX_USES = 25;
   private static final SoundEvent[] FOX_SOUNDS = new SoundEvent[]{
      SoundEvents.f_11944_, SoundEvents.f_11951_, SoundEvents.f_11949_, SoundEvents.f_11943_, SoundEvents.f_11945_
   };
   private static final SoundType SOUNDS = new SoundType(
      1.0F, 1.0F, SoundEvents.f_11946_, SoundEvents.f_11670_, SoundEvents.f_11944_, SoundEvents.f_11667_, SoundEvents.f_11948_
   );

   public static SoundEvent randomFoxSound(RandomSource random) {
      return FOX_SOUNDS[random.m_188503_(FOX_SOUNDS.length)];
   }

   public FoxAnvilBlock() {
      super(Properties.m_284310_().m_284180_(MapColor.f_283906_).m_60999_().m_60913_(5.0F, 1200.0F).m_60918_(SOUNDS));
   }

   @Nullable
   public BlockEntity m_142194_(BlockPos pos, BlockState state) {
      return new FoxAnvilBlockEntity(pos, state);
   }

   public void m_213897_(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (pos.m_123342_() >= level.m_141937_() && m_53241_(level.m_8055_(pos.m_7495_()))) {
         CompoundTag data = level.m_7702_(pos) instanceof FoxAnvilBlockEntity anvil ? anvil.m_187482_() : null;
         FallingBlockEntity entity = FallingBlockEntity.m_201971_(level, pos, state);
         entity.f_31944_ = data;
         this.m_6788_(entity);
      }
   }

   public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (!level.f_46443_) {
         playFoxSound(level, pos, level.m_213780_());
      }

      return super.m_6227_(state, level, pos, player, hand, hit);
   }

   public void m_48792_(Level level, BlockPos pos, BlockState state, BlockState replaced, FallingBlockEntity falling) {
      if (!level.f_46443_) {
         level.m_5594_(null, pos, SoundEvents.f_11948_, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   public void m_142525_(Level level, BlockPos pos, FallingBlockEntity falling) {
      if (!level.f_46443_) {
         level.m_5594_(null, pos, SoundEvents.f_11946_, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   public MenuProvider m_7246_(BlockState state, Level level, BlockPos pos) {
      return new SimpleMenuProvider(
         (id, inventory, player) -> new FoxAnvilMenu(id, inventory, ContainerLevelAccess.m_39289_(level, pos)), Component.m_237115_("container.repair")
      );
   }

   public static void consumeUse(Level level, BlockPos pos) {
      if (!level.f_46443_) {
         if (level.m_7702_(pos) instanceof FoxAnvilBlockEntity be) {
            int left = be.getUses() - 1;
            if (left > 0) {
               be.setUses(left);
            } else {
               level.m_5594_(null, pos, SoundEvents.f_11946_, SoundSource.BLOCKS, 1.0F, 1.0F);
               level.m_7471_(pos, false);
               if (level instanceof ServerLevel serverLevel) {
                  spawnFox(serverLevel, pos);
               }
            }
         }
      }
   }

   private static void spawnFox(ServerLevel level, BlockPos pos) {
      Fox fox = (Fox)EntityType.f_20452_.m_20615_(level);
      if (fox != null) {
         RandomSource random = level.m_213780_();
         fox.m_7678_(pos.m_123341_() + 0.5, pos.m_123342_(), pos.m_123343_() + 0.5, random.m_188501_() * 360.0F, 0.0F);
         fox.m_28464_(random.m_188499_() ? Type.SNOW : Type.RED);
         level.m_7967_(fox);
      }
   }

   public static void playFoxSound(Level level, BlockPos pos, RandomSource random) {
      SoundEvent sound = randomFoxSound(random);
      level.m_5594_(null, pos, sound, SoundSource.BLOCKS, 0.6F + random.m_188501_() * 0.4F, 0.85F + random.m_188501_() * 0.35F);
   }
}
