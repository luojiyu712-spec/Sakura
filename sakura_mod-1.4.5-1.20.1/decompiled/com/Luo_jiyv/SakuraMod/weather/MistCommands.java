package com.Luo_jiyv.SakuraMod.weather;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class MistCommands {
   @SubscribeEvent
   public static void onRegisterCommands(RegisterCommandsEvent event) {
      CommandNode<CommandSourceStack> weather = event.getDispatcher().getRoot().getChild("weather");
      if (weather == null) {
         SakuraMod.LOGGER.warn("Vanilla /weather command not found; mist branch skipped");
      } else if (weather.getChild("mist") == null) {
         LiteralCommandNode<CommandSourceStack> mist = ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_(
                           "mist"
                        )
                        .requires(source -> source.m_6761_(2)))
                     .executes(ctx -> start((CommandSourceStack)ctx.getSource(), 1200)))
                  .then(
                     Commands.m_82129_("seconds", IntegerArgumentType.integer(1, 50000))
                        .executes(ctx -> start((CommandSourceStack)ctx.getSource(), IntegerArgumentType.getInteger(ctx, "seconds")))
                  ))
               .then(Commands.m_82127_("status").executes(ctx -> status((CommandSourceStack)ctx.getSource()))))
            .build();
         weather.addChild(mist);
         SakuraMod.LOGGER.debug("Registered /weather mist branch");
      }
   }

   private static int start(CommandSourceStack source, int seconds) {
      if (!Config.isMistWeatherEnabled()) {
         source.m_81352_(Component.m_237113_("灰棘迷雾已在模组配置中关闭（异象 → 异象天气）"));
         return 0;
      } else {
         MistWeather.start(source.m_81377_(), seconds * 20);
         return 1;
      }
   }

   private static int status(CommandSourceStack source) {
      ServerLevel level = source.m_81372_();
      ServerPlayer player = source.m_230896_();
      BlockPos base = player != null ? player.m_20183_() : BlockPos.m_274446_(source.m_81371_());
      int surfaceY = level.m_6924_(Types.MOTION_BLOCKING_NO_LEAVES, base.m_123341_(), base.m_123343_());
      BlockPos surface = new BlockPos(base.m_123341_(), surfaceY, base.m_123343_());
      int monsters = player == null
         ? -1
         : level.m_6443_(Monster.class, player.m_20191_().m_82400_(128.0), m -> m.m_6095_().m_20674_() == MobCategory.MONSTER).size();
      boolean placementOk = NaturalSpawner.m_47051_(SpawnPlacements.m_21752_(EntityType.f_20501_), level, surface, EntityType.f_20501_);
      boolean spawnRulesOk = SpawnPlacements.m_217074_(EntityType.f_20501_, level, MobSpawnType.NATURAL, surface, level.m_213780_());
      double spawnDist = Math.sqrt(base.m_123331_(level.m_220360_()));
      String text = String.join(
         "\n",
         "§b[迷雾自检]",
         " §7迷雾进行中：§f" + MistWeather.isActive() + "§7  剩余：§f" + MistWeather.remainingTicks(level.m_46467_()) + " ticks",
         " §7难度：§f"
            + level.m_46791_()
            + "§7  doMobSpawning：§f"
            + level.m_46469_().m_46207_(GameRules.f_46134_)
            + "§7  离出生点：§f"
            + Math.round(spawnDist)
            + " 格"
            + (spawnDist < 24.0 ? " §c(<24 会抑制刷怪)" : ""),
         " §7地表点：§f"
            + surface.m_123341_()
            + ","
            + surfaceY
            + ","
            + surface.m_123343_()
            + "§7  天空光：§f"
            + level.m_45517_(LightLayer.SKY, surface)
            + "§7  方块光：§f"
            + level.m_45517_(LightLayer.BLOCK, surface),
         " §7刷怪用有效光照：§f" + level.m_46803_(surface) + "§7（≤7 才可能刷怪）",
         " §7周围 128 格怪物：§f" + (monsters < 0 ? "无玩家" : monsters) + "§7 / 上限 §f" + MobCategory.MONSTER.m_21608_(),
         " §7落脚点合法(ON_GROUND)：§f" + placementOk,
         " §7刷怪规则放行(ZOMBIE)：§f" + spawnRulesOk + (spawnRulesOk ? " §a← 光照放行已生效" : " §c← 这里为 false 就是没生效")
      );
      source.m_288197_(() -> Component.m_237113_(text).m_130940_(ChatFormatting.GRAY), false);
      return 1;
   }

   private MistCommands() {
   }
}
