package com.Luo_jiyv.SakuraMod.command;

import com.Luo_jiyv.SakuraMod.event.ShieldMechanismEvents;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
public final class ShieldDebugCommand {
   private ShieldDebugCommand() {
   }

   @SubscribeEvent
   public static void register(RegisterCommandsEvent event) {
      int max = 100;
      event.getDispatcher()
         .register(
            (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_(
                                 "sakura_shield"
                              )
                              .requires(source -> source.m_6761_(2)))
                           .executes(ctx -> get((CommandSourceStack)ctx.getSource(), List.of(((CommandSourceStack)ctx.getSource()).m_81375_()))))
                        .then(
                           ((LiteralArgumentBuilder)Commands.m_82127_("get")
                                 .executes(ctx -> get((CommandSourceStack)ctx.getSource(), List.of(((CommandSourceStack)ctx.getSource()).m_81375_()))))
                              .then(
                                 Commands.m_82129_("targets", EntityArgument.m_91470_())
                                    .executes(ctx -> get((CommandSourceStack)ctx.getSource(), EntityArgument.m_91477_(ctx, "targets")))
                              )
                        ))
                     .then(
                        Commands.m_82127_("set")
                           .then(
                              Commands.m_82129_("targets", EntityArgument.m_91470_())
                                 .then(
                                    Commands.m_82129_("points", IntegerArgumentType.integer(0, max))
                                       .executes(
                                          ctx -> change(
                                             (CommandSourceStack)ctx.getSource(),
                                             EntityArgument.m_91477_(ctx, "targets"),
                                             IntegerArgumentType.getInteger(ctx, "points"),
                                             false
                                          )
                                       )
                                 )
                           )
                     ))
                  .then(
                     Commands.m_82127_("add")
                        .then(
                           Commands.m_82129_("targets", EntityArgument.m_91470_())
                              .then(
                                 Commands.m_82129_("points", IntegerArgumentType.integer(-max, max))
                                    .executes(
                                       ctx -> change(
                                          (CommandSourceStack)ctx.getSource(),
                                          EntityArgument.m_91477_(ctx, "targets"),
                                          IntegerArgumentType.getInteger(ctx, "points"),
                                          true
                                       )
                                    )
                              )
                        )
                  ))
               .then(
                  ((LiteralArgumentBuilder)Commands.m_82127_("reset")
                        .executes(ctx -> change((CommandSourceStack)ctx.getSource(), List.of(((CommandSourceStack)ctx.getSource()).m_81375_()), max, false)))
                     .then(
                        Commands.m_82129_("targets", EntityArgument.m_91470_())
                           .executes(ctx -> change((CommandSourceStack)ctx.getSource(), EntityArgument.m_91477_(ctx, "targets"), max, false))
                     )
               )
         );
   }

   private static int get(CommandSourceStack source, Collection<ServerPlayer> targets) {
      for (ServerPlayer player : targets) {
         feedback(source, player, false);
      }

      return targets.size() == 1 ? points(targets.iterator().next()) : targets.size();
   }

   private static int change(CommandSourceStack source, Collection<ServerPlayer> targets, int value, boolean relative) {
      for (ServerPlayer player : targets) {
         if (relative) {
            ShieldMechanismEvents.debugAddCharges(player, value);
         } else {
            ShieldMechanismEvents.debugSetCharges(player, value);
         }

         feedback(source, player, true);
      }

      return targets.size();
   }

   private static int points(ServerPlayer player) {
      return ShieldMechanismEvents.getEnergy(player);
   }

   private static void feedback(CommandSourceStack source, ServerPlayer player, boolean changed) {
      source.m_288197_(
         () -> Component.m_237110_(
            changed ? "command.sakura_mod.shield.set" : "command.sakura_mod.shield.get",
            new Object[]{player.m_5446_(), points(player), ShieldMechanismEvents.getCapacity(player)}
         ),
         changed
      );
   }
}
