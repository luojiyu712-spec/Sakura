package com.Luo_jiyv.SakuraMod.client.music;

import com.Luo_jiyv.SakuraMod.SakuraMod;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public final class ClientMusicReloadCommand {
   @SubscribeEvent
   public static void register(RegisterClientCommandsEvent event) {
      event.getDispatcher().register((LiteralArgumentBuilder)Commands.m_82127_("sakura_music").then(Commands.m_82127_("reload").executes(context -> {
         CommandSourceStack source = (CommandSourceStack)context.getSource();
         MusicAudioReload.reload().whenCompleteAsync((count, error) -> {
            if (error != null) {
               SakuraMod.LOGGER.warn("[Music] Could not reload Sakura audio", error);
               source.m_81352_(Component.m_237115_("commands.sakura_mod.music.client.failed"));
            } else if (count >= 0) {
               source.m_288197_(() -> Component.m_237110_("commands.sakura_mod.music.client.done", new Object[]{count}), false);
            }
         }, Minecraft.m_91087_());
         return 1;
      })));
   }

   private ClientMusicReloadCommand() {
   }
}
