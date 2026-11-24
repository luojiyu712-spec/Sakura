package com.Luo_jiyv.SakuraMod.command;

import com.Luo_jiyv.SakuraMod.event.GoldenCubeEvents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public class GoldenCubeCommand {
   private static final int DEFAULT_IMMUNITY_SECONDS = 43200;

   @SubscribeEvent
   public static void onRegisterCommands(RegisterCommandsEvent event) {
      CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_(
                                 "golden_cube"
                              )
                              .requires(source -> source.m_6761_(2)))
                           .then(
                              ((LiteralArgumentBuilder)Commands.m_82127_("clear")
                                    .executes(ctx -> clear(ctx, List.of(((CommandSourceStack)ctx.getSource()).m_81375_()))))
                                 .then(
                                    Commands.m_82129_("targets", EntityArgument.m_91470_())
                                       .executes(ctx -> clear(ctx, EntityArgument.m_91477_(ctx, "targets")))
                                 )
                           ))
                        .then(
                           Commands.m_82127_("severity")
                              .then(
                                 Commands.m_82129_("targets", EntityArgument.m_91470_())
                                    .then(Commands.m_82129_("value", FloatArgumentType.floatArg(0.0F, 2.0F)).executes(GoldenCubeCommand::setSeverity))
                              )
                        ))
                     .then(
                        Commands.m_82127_("immunity")
                           .then(
                              ((RequiredArgumentBuilder)Commands.m_82129_("targets", EntityArgument.m_91470_()).executes(ctx -> setImmunity(ctx, 43200)))
                                 .then(
                                    Commands.m_82129_("seconds", IntegerArgumentType.integer(0, 31536000))
                                       .executes(ctx -> setImmunity(ctx, IntegerArgumentType.getInteger(ctx, "seconds")))
                                 )
                           )
                     ))
                  .then(
                     Commands.m_82127_("playcount")
                        .then(
                           Commands.m_82129_("targets", EntityArgument.m_91470_())
                              .then(Commands.m_82129_("count", IntegerArgumentType.integer(0, 1000)).executes(GoldenCubeCommand::setPlayCount))
                        )
                  ))
               .then(
                  Commands.m_82127_("withdrawal")
                     .then(
                        Commands.m_82129_("targets", EntityArgument.m_91470_())
                           .then(Commands.m_82129_("level", IntegerArgumentType.integer(0, 5)).executes(GoldenCubeCommand::setWithdrawal))
                     )
               ))
            .then(Commands.m_82127_("reject").then(Commands.m_82129_("targets", EntityArgument.m_91470_()).executes(GoldenCubeCommand::reject)))
      );
   }

   private static int clear(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> targets) {
      for (ServerPlayer player : targets) {
         GoldenCubeEvents.clearAll(player);
      }

      int count = targets.size();
      ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("已清除 " + count + " 名玩家的金色魔方状态（含彻底拒绝标记）"), false);
      return count;
   }

   private static int setSeverity(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      Collection<ServerPlayer> targets = EntityArgument.m_91477_(ctx, "targets");
      float value = FloatArgumentType.getFloat(ctx, "value");

      for (ServerPlayer player : targets) {
         GoldenCubeEvents.debugSetSeverity(player, value);
      }

      int count = targets.size();
      ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("已将 " + count + " 名玩家的严重度设为 " + value), true);
      return count;
   }

   private static int setImmunity(CommandContext<CommandSourceStack> ctx, int seconds) throws CommandSyntaxException {
      Collection<ServerPlayer> targets = EntityArgument.m_91477_(ctx, "targets");

      for (ServerPlayer player : targets) {
         GoldenCubeEvents.debugSetImmunity(player, seconds);
      }

      int count = targets.size();
      ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("已赋予 " + count + " 名玩家 " + seconds + " 秒免疫期"), true);
      return count;
   }

   private static int setPlayCount(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      Collection<ServerPlayer> targets = EntityArgument.m_91477_(ctx, "targets");
      int value = IntegerArgumentType.getInteger(ctx, "count");

      for (ServerPlayer player : targets) {
         GoldenCubeEvents.debugSetPlayCount(player, value);
      }

      int count = targets.size();
      ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("已将 " + count + " 名玩家的把玩次数设为 " + value), true);
      return count;
   }

   private static int setWithdrawal(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      Collection<ServerPlayer> targets = EntityArgument.m_91477_(ctx, "targets");
      int level = IntegerArgumentType.getInteger(ctx, "level");

      for (ServerPlayer player : targets) {
         GoldenCubeEvents.debugSetWithdrawal(player, level);
      }

      int count = targets.size();
      ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("已将 " + count + " 名玩家的戒断等级设为 " + level), true);
      return count;
   }

   private static int reject(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      Collection<ServerPlayer> targets = EntityArgument.m_91477_(ctx, "targets");

      for (ServerPlayer player : targets) {
         GoldenCubeEvents.debugReject(player);
      }

      int count = targets.size();
      ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_("已让 " + count + " 名玩家彻底拒绝魔方"), true);
      return count;
   }
}
