package com.xeno.materia.client.keys;

import com.mojang.blaze3d.platform.InputConstants;
import com.xeno.materia.client.RenderUtils;
import com.xeno.materia.client.radial.GuiRadialMenu;
import com.xeno.materia.client.radial.MateriaSlotData;
import com.xeno.materia.client.radial.RadialMenu;
import com.xeno.materia.client.radial.RadialMenuSlot;
import com.xeno.materia.common.MateriaEnum;
import com.xeno.materia.common.MateriaRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.InputEvent;

import java.util.ArrayList;
import java.util.List;

public class RadialMenuKeyHandler
{
    // client only thing, it's your last chosen option

    public static void handleRadialKeyEvent(InputEvent.Key eventKey)
    {
        if (isRadialKeyPressed(eventKey) && Minecraft.getInstance().player != null && isScreenValidForRadialToggle())
        {
            toggleRadial();
        }
    }

    public static void toggleRadial()
    {
        Player player = Minecraft.getInstance().player;
        if (Minecraft.getInstance().screen == null)
        {
            RadialMenuKeyHandler.openRadial(player);
        }
        else if (Minecraft.getInstance().screen instanceof GuiRadialMenu)
        {
            Minecraft.getInstance().player.closeContainer();
        }
    }

    private static boolean isScreenValidForRadialToggle()
    {
        return Minecraft.getInstance().screen == null || Minecraft.getInstance().screen instanceof GuiRadialMenu<?>;
    }

    public static boolean isRadialKeyPressed(InputEvent.Key eventKey)
    {
        return eventKey.getKey() == ModKeyBindings.OPEN_RADIAL_HUD_KEY.getKey().getValue() && eventKey.getAction() == InputConstants.PRESS;
    }

    public static void openRadial(Player player)
    {
        List<RadialMenuSlot<MateriaSlotData>> slots = getMateriaSlotDataFromPlayerCapability(player);
        // probably want to change the callback structure here as int is pretty weak.
        Minecraft.getInstance().setScreen(makeRadialForSlots(slots));
    }

    private static Screen makeRadialForSlots(List<RadialMenuSlot<MateriaSlotData>> slots)
    {
        return new GuiRadialMenu<>(new RadialMenu<>(AbilityKeyHandler::doSlotAssignment, slots, RenderUtils::drawMateriaSlotIcon, 3));
    }

    public static List<RadialMenuSlot<MateriaSlotData>> getMateriaSlotDataFromPlayerCapability(Player player)
    {
        var result = new ArrayList<RadialMenuSlot<MateriaSlotData>>();
        for (var e : MateriaEnum.values())
        {
            // don't draw a slot for denied materia, it isn't real. It can't hurt you.
            if (e == MateriaEnum.DENIED) {
                continue;
            }
            result.add(e.makeRadialSlot(player));
        }
        return result;
    }

    private static boolean isAttachmentMissing(MateriaEnum e)
    {
        return !MateriaRegistry.MATERIA_LIMIT_ATTACHMENTS.containsKey(e) || !MateriaRegistry.MATERIA_STOCK_ATTACHMENTS.containsKey(e);
    }

    private static boolean isPlayerMissingAttachment(Player player, MateriaEnum e)
    {
        return !player.hasData(MateriaRegistry.MATERIA_LIMIT_ATTACHMENTS.get(e)) || !player.hasData(MateriaRegistry.MATERIA_STOCK_ATTACHMENTS.get(e));
    }
}
