package com.Luo_jiyv.SakuraMod.client;

import com.Luo_jiyv.SakuraMod.item.Moditems;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "sakura_mod",
   value = {Dist.CLIENT}
)
public class TooltipEvents {
   private static Map<Item, TooltipEvents.Tip> TIPS;

   private static Map<Item, TooltipEvents.Tip> tips() {
      if (TIPS == null) {
         Map<Item, TooltipEvents.Tip> map = new HashMap<>();
         TooltipEvents.Line piercing = new TooltipEvents.Line("tooltip.sakura_mod.special_damage.piercing", ChatFormatting.GRAY);
         TooltipEvents.Line protection = new TooltipEvents.Line("tooltip.sakura_mod.special_damage.protection", ChatFormatting.GRAY);
         map.put(
            (Item)Moditems.GRAVCORE.get(),
            TooltipEvents.Tip.details(
               List.of(
                  new TooltipEvents.Line("item.sakura_mod.gravcore.tooltip.1", ChatFormatting.GOLD),
                  new TooltipEvents.Line("item.sakura_mod.gravcore.tooltip.2", ChatFormatting.YELLOW),
                  new TooltipEvents.Line("item.sakura_mod.gravcore.tooltip.3", ChatFormatting.AQUA),
                  new TooltipEvents.Line("item.sakura_mod.gravcore.tooltip.4", ChatFormatting.LIGHT_PURPLE, 15, 4),
                  new TooltipEvents.Line("item.sakura_mod.gravcore.tooltip.damage", ChatFormatting.LIGHT_PURPLE, Math.round(50.0F), 50),
                  new TooltipEvents.Line("item.sakura_mod.gravcore.tooltip.5", ChatFormatting.LIGHT_PURPLE, 2, 320, 5),
                  new TooltipEvents.Line("item.sakura_mod.gravcore.tooltip.damage", ChatFormatting.LIGHT_PURPLE, Math.round(250.0F), 250),
                  piercing,
                  protection,
                  new TooltipEvents.Line("item.sakura_mod.gravcore.tooltip.7", ChatFormatting.GRAY),
                  new TooltipEvents.Line("item.sakura_mod.gravcore.tooltip.8", ChatFormatting.DARK_GRAY),
                  new TooltipEvents.Line("item.sakura_mod.gravcore.tooltip.break", ChatFormatting.YELLOW),
                  new TooltipEvents.Line("item.sakura_mod.gravcore.tooltip.protected", ChatFormatting.DARK_GRAY)
               )
            )
         );
         List<TooltipEvents.Line> armorKeys = List.of(
            new TooltipEvents.Line("item.sakura_mod.sakura_armor.tooltip.set_2", ChatFormatting.GREEN),
            new TooltipEvents.Line("item.sakura_mod.sakura_armor.tooltip.set_4", ChatFormatting.GREEN),
            new TooltipEvents.Line("item.sakura_mod.sakura_armor.tooltip.effect", ChatFormatting.LIGHT_PURPLE)
         );
         map.put((Item)Moditems.SAKURA_HELMET.get(), TooltipEvents.Tip.details(armorKeys));
         map.put((Item)Moditems.SAKURA_CHESTPLATE.get(), TooltipEvents.Tip.details(armorKeys));
         map.put((Item)Moditems.SAKURA_LEGGINGS.get(), TooltipEvents.Tip.details(armorKeys));
         map.put((Item)Moditems.SAKURA_BOOTS.get(), TooltipEvents.Tip.details(armorKeys));
         map.put(
            (Item)Moditems.SAKURA_AMULET.get(),
            TooltipEvents.Tip.details(
               List.of(
                  new TooltipEvents.Line("item.sakura_mod.sakura_amulet.tooltip.blessing", ChatFormatting.LIGHT_PURPLE),
                  new TooltipEvents.Line("item.sakura_mod.sakura_amulet.tooltip.full_set", ChatFormatting.LIGHT_PURPLE),
                  new TooltipEvents.Line("item.sakura_mod.sakura_amulet.tooltip.regen_note", ChatFormatting.GRAY)
               )
            )
         );
         map.put(
            (Item)Moditems.SAKURA_REPAIR_KIT.get(),
            TooltipEvents.Tip.details(
               List.of(
                  new TooltipEvents.Line("item.sakura_mod.sakura_repair_kit.tooltip.1", ChatFormatting.AQUA),
                  new TooltipEvents.Line("item.sakura_mod.sakura_repair_kit.tooltip.2", ChatFormatting.GRAY)
               )
            )
         );
         map.put(
            (Item)Moditems.SAKURA_BLOCK_ITEM.get(),
            TooltipEvents.Tip.details(List.of(new TooltipEvents.Line("block.sakura_mod.sakura_block.tooltip.detail", ChatFormatting.GOLD)))
         );
         map.put(
            (Item)Moditems.BLAZING_ROCK_ITEM.get(),
            TooltipEvents.Tip.details(List.of(new TooltipEvents.Line("block.sakura_mod.blazing_rock.tooltip", ChatFormatting.RED)))
         );
         map.put(
            (Item)Moditems.COBBLE_GENERATOR_ITEM.get(),
            TooltipEvents.Tip.details(
               List.of(
                  new TooltipEvents.Line("block.sakura_mod.cobble_generator.tooltip.1", ChatFormatting.AQUA),
                  new TooltipEvents.Line("block.sakura_mod.cobble_generator.tooltip.2", ChatFormatting.GRAY),
                  new TooltipEvents.Line("block.sakura_mod.cobble_generator.tooltip.3", ChatFormatting.RED)
               )
            )
         );
         map.put(
            (Item)Moditems.BEE_HONEY_BLOCK_ITEM.get(),
            TooltipEvents.Tip.details(
               List.of(
                  new TooltipEvents.Line("block.sakura_mod.bee_honey_block.tooltip.1", ChatFormatting.AQUA),
                  new TooltipEvents.Line("block.sakura_mod.bee_honey_block.tooltip.2", ChatFormatting.GRAY)
               )
            )
         );
         map.put(
            (Item)Moditems.CHICKEN_COOP_ITEM.get(),
            TooltipEvents.Tip.details(
               List.of(
                  new TooltipEvents.Line("block.sakura_mod.chicken_coop.tooltip.1", ChatFormatting.AQUA),
                  new TooltipEvents.Line("block.sakura_mod.chicken_coop.tooltip.2", ChatFormatting.GRAY)
               )
            )
         );
         map.put(
            (Item)Moditems.STRING_FARM_ITEM.get(),
            TooltipEvents.Tip.details(
               List.of(
                  new TooltipEvents.Line("block.sakura_mod.string_farm.tooltip.1", ChatFormatting.AQUA),
                  new TooltipEvents.Line("block.sakura_mod.string_farm.tooltip.2", ChatFormatting.GRAY),
                  new TooltipEvents.Line("block.sakura_mod.string_farm.tooltip.3", ChatFormatting.GOLD)
               )
            )
         );
         map.put(
            (Item)Moditems.BARRIER_STONE_ITEM.get(),
            TooltipEvents.Tip.details(
               List.of(
                  new TooltipEvents.Line("block.sakura_mod.barrier_stone.tooltip.1", ChatFormatting.AQUA),
                  new TooltipEvents.Line("block.sakura_mod.barrier_stone.tooltip.2", ChatFormatting.GRAY),
                  new TooltipEvents.Line("block.sakura_mod.barrier_stone.tooltip.3", ChatFormatting.GOLD)
               )
            )
         );
         map.put(
            (Item)Moditems.SAKURA_SWORD.get(),
            TooltipEvents.Tip.of(
               List.of(
                  new TooltipEvents.Line("item.sakura_mod.sakura_sword.tooltip.sweep", ChatFormatting.GOLD),
                  new TooltipEvents.Line("item.sakura_mod.sakura_weapon.tooltip.looting", ChatFormatting.AQUA)
               ),
               List.of(new TooltipEvents.Line("item.sakura_mod.sakura_sword.tooltip.slash_armor", ChatFormatting.RED))
            )
         );
         map.put(
            (Item)Moditems.SAKURA_AXE.get(),
            TooltipEvents.Tip.of(
               List.of(
                  new TooltipEvents.Line("item.sakura_mod.sakura_axe.tooltip.growth", ChatFormatting.GOLD),
                  new TooltipEvents.Line("item.sakura_mod.sakura_axe.tooltip.bonus", ChatFormatting.AQUA)
               ),
               List.of(
                  new TooltipEvents.Line("item.sakura_mod.sakura_axe.tooltip.slash", ChatFormatting.GOLD),
                  new TooltipEvents.Line("item.sakura_mod.sakura_axe.tooltip.slash_armor", ChatFormatting.RED)
               )
            )
         );
         map.put(
            (Item)Moditems.SAKURA_PICKAXE.get(),
            TooltipEvents.Tip.of(
               List.of(new TooltipEvents.Line("item.sakura_mod.sakura_tool.tooltip.fortune", ChatFormatting.AQUA)),
               List.of(
                  new TooltipEvents.Line("item.sakura_mod.sakura_pickaxe.tooltip.vein", ChatFormatting.GOLD),
                  new TooltipEvents.Line("item.sakura_mod.sakura_pickaxe.tooltip.double_drop", ChatFormatting.AQUA)
               )
            )
         );
         map.put(
            (Item)Moditems.SAKURA_SHOVEL.get(),
            TooltipEvents.Tip.of(
               List.of(new TooltipEvents.Line("item.sakura_mod.sakura_tool.tooltip.fortune", ChatFormatting.AQUA)),
               List.of(new TooltipEvents.Line("item.sakura_mod.sakura_shovel.tooltip.grass_toggle", ChatFormatting.GOLD))
            )
         );
         map.put(
            (Item)Moditems.SAKURA_HOE.get(),
            TooltipEvents.Tip.of(
               List.of(
                  new TooltipEvents.Line("item.sakura_mod.sakura_hoe.tooltip.growth", ChatFormatting.GOLD),
                  new TooltipEvents.Line("item.sakura_mod.sakura_tool.tooltip.fortune", ChatFormatting.AQUA)
               ),
               List.of()
            )
         );
         TIPS = map;
      }

      return TIPS;
   }

   private static boolean isShiftDown() {
      if (Screen.m_96638_()) {
         return true;
      } else {
         try {
            Minecraft mc = Minecraft.m_91087_();
            if (mc != null && mc.m_91268_() != null) {
               long window = mc.m_91268_().m_85439_();
               return InputConstants.m_84830_(window, 340) || InputConstants.m_84830_(window, 344);
            } else {
               return false;
            }
         } catch (Throwable var3) {
            return false;
         }
      }
   }

   @SubscribeEvent
   public static void onItemTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      TooltipEvents.Tip tip = tips().get(stack.m_41720_());
      if (tip != null) {
         List<Component> description = new ArrayList<>();

         for (TooltipEvents.Line line : tip.always()) {
            description.add(line.toComponent());
         }

         if (!tip.shift().isEmpty()) {
            if (isShiftDown()) {
               for (TooltipEvents.Line line : tip.shift()) {
                  description.add(line.toComponent());
               }
            } else {
               description.add(
                  Component.m_237110_("item.sakura_mod.tooltip.hold_shift", new Object[]{Component.m_237113_("Shift").m_130940_(ChatFormatting.GRAY)})
                     .m_130940_(ChatFormatting.DARK_GRAY)
               );
            }
         }

         List<Component> tooltip = event.getToolTip();
         tooltip.addAll(Math.min(1, tooltip.size()), description);
      }
   }

   public record Line(String key, ChatFormatting color, Object... args) {
      public Component toComponent() {
         return Component.m_237110_(this.key, this.args).m_130940_(this.color);
      }
   }

   public record Tip(List<TooltipEvents.Line> always, List<TooltipEvents.Line> shift) {
      public static TooltipEvents.Tip of(List<TooltipEvents.Line> always, List<TooltipEvents.Line> shift) {
         return new TooltipEvents.Tip(always, shift);
      }

      public static TooltipEvents.Tip details(List<TooltipEvents.Line> shift) {
         return new TooltipEvents.Tip(List.of(), shift);
      }
   }
}
