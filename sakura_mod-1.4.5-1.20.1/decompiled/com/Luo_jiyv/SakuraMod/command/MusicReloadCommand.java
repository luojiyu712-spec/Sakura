package com.Luo_jiyv.SakuraMod.command;

import com.Luo_jiyv.SakuraMod.music.ServerDefaultMusic;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod"
)
public final class MusicReloadCommand {
   @SubscribeEvent
   public static void register(RegisterCommandsEvent event) {
      event.getDispatcher()
         .register(
            (LiteralArgumentBuilder)Commands.m_82127_("sakura_music")
               .then(((LiteralArgumentBuilder)Commands.m_82127_("reload").then(server("server"))).then(server("sever")))
         );
   }

   private static LiteralArgumentBuilder<CommandSourceStack> server(String name) {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)Commands.m_82127_(name).requires(source -> source.m_6761_(2)))
         .executes(context -> {
            CommandSourceStack source = (CommandSourceStack)context.getSource();
            source.m_288197_(() -> Component.m_237115_("commands.sakura_mod.music.server.scanning"), false);
            ServerDefaultMusic.reload(source.m_81377_()).whenComplete((count, error) -> {
               if (error != null) {
                  source.m_81352_(Component.m_237115_("commands.sakura_mod.music.server.failed"));
               } else {
                  source.m_288197_(() -> Component.m_237110_("commands.sakura_mod.music.server.done", new Object[]{count}), false);
               }
            });
            return 1;
         });
   }

   private MusicReloadCommand() {
   }
}
