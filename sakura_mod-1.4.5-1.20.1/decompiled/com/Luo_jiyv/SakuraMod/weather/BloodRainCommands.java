package com.Luo_jiyv.SakuraMod.weather;

import com.Luo_jiyv.SakuraMod.Config;
import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class BloodRainCommands {
   @SubscribeEvent
   public static void onRegisterCommands(RegisterCommandsEvent event) {
      CommandNode<CommandSourceStack> weather = event.getDispatcher().getRoot().getChild("weather");
      if (weather == null) {
         SakuraMod.LOGGER.warn("Vanilla /weather command not found; bloodrain branch skipped");
      } else if (weather.getChild("bloodrain") == null) {
         LiteralCommandNode<CommandSourceStack> bloodRain = ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_(
                        "bloodrain"
                     )
                     .requires(source -> source.m_6761_(2)))
                  .executes(ctx -> start((CommandSourceStack)ctx.getSource(), 300)))
               .then(
                  Commands.m_82129_("seconds", IntegerArgumentType.integer(1, 50000))
                     .executes(ctx -> start((CommandSourceStack)ctx.getSource(), IntegerArgumentType.getInteger(ctx, "seconds")))
               ))
            .build();
         weather.addChild(bloodRain);
         SakuraMod.LOGGER.debug("Registered /weather bloodrain branch");
      }
   }

   private static int start(CommandSourceStack source, int seconds) {
      if (!Config.isBloodRainEnabled()) {
         source.m_81352_(Component.m_237113_("血怒之雨已在模组配置中关闭（异象 → 异象天气）"));
         return 0;
      } else {
         BloodRainWeather.start(source.m_81377_(), seconds * 20);
         return 1;
      }
   }

   private BloodRainCommands() {
   }
}
