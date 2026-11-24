package com.Luo_jiyv.SakuraMod.client.music;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MusicDimensions {
   private static final List<ResourceLocation> VANILLA_FALLBACK = List.of(
      ResourceLocation.withDefaultNamespace("overworld"), ResourceLocation.withDefaultNamespace("the_nether"), ResourceLocation.withDefaultNamespace("the_end")
   );
   private static List<ResourceLocation> lastKnown = VANILLA_FALLBACK;

   public static List<ResourceLocation> all() {
      Minecraft mc = Minecraft.m_91087_();
      Set<ResourceLocation> ids = new TreeSet<>(Comparator.comparing(ResourceLocation::toString));
      ClientPacketListener connection = mc.m_91403_();
      if (connection != null) {
         Set<ResourceKey<Level>> levels = connection.m_105151_();
         if (levels != null) {
            for (ResourceKey<Level> key : levels) {
               ids.add(key.m_135782_());
            }
         }
      }

      if (mc.f_91073_ != null) {
         ids.add(mc.f_91073_.m_46472_().m_135782_());
         mc.f_91073_.m_9598_().m_6632_(Registries.f_256858_).ifPresent(registry -> ids.addAll(registry.m_6566_()));
      }

      if (ids.isEmpty()) {
         return lastKnown;
      } else {
         List<ResourceLocation> result = new ArrayList<>(ids);
         lastKnown = List.copyOf(result);
         return result;
      }
   }

   public static boolean isLive() {
      return Minecraft.m_91087_().f_91073_ != null;
   }

   public static boolean isCurrent(ResourceLocation dimension) {
      Minecraft mc = Minecraft.m_91087_();
      return mc.f_91073_ != null && mc.f_91073_.m_46472_().m_135782_().equals(dimension);
   }

   public static Component label(ResourceLocation dimension) {
      Component name = translatedName(dimension);
      MutableComponent label = name.m_6881_();
      if (!name.getString().equals(dimension.toString())) {
         label.m_7220_(Component.m_237113_(" (" + dimension + ")").m_130940_(ChatFormatting.DARK_GRAY));
      }

      if (isCurrent(dimension)) {
         label.m_7220_(Component.m_237113_("  ")).m_7220_(Component.m_237115_("config.sakura_mod.music.dimension.current").m_130940_(ChatFormatting.GREEN));
      }

      return label;
   }

   private static Component translatedName(ResourceLocation dimension) {
      if ("minecraft".equals(dimension.m_135827_())) {
         String key = dimension.m_135815_();
         switch (key) {
            case "overworld":
               return Component.m_237115_("config.sakura_mod.music.dimension.overworld");
            case "the_nether":
               return Component.m_237115_("config.sakura_mod.music.dimension.the_nether");
            case "the_end":
               return Component.m_237115_("config.sakura_mod.music.dimension.the_end");
         }
      }

      String key = "dimension." + dimension.m_135827_() + "." + dimension.m_135815_();
      return Language.m_128107_().m_6722_(key) ? Component.m_237115_(key) : Component.m_237113_(dimension.toString());
   }

   private MusicDimensions() {
   }
}
