package com.xeno.materia.client.keys;

import com.mojang.blaze3d.platform.InputConstants;
import com.xeno.materia.client.*;
import com.xeno.materia.client.radial.GuiRadialMenu;
import com.xeno.materia.client.radial.MateriaSlotData;
import com.xeno.materia.client.radial.RadialMenu;
import com.xeno.materia.client.radial.RadialMenuSlot;
import com.xeno.materia.common.capabilities.MateriaCapability;
import com.xeno.materia.common.MateriaEnum;
import com.xeno.materia.common.capabilities.MateriaCapabilityImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent.Key;

import java.util.ArrayList;
import java.util.List;

public class RadialMenuKeyHandler
{
	// client only thing, it's your last chosen option

	public static void handleRadialKeyEvent(Key eventKey)
	{
		if (isRadialKeyPressed(eventKey) && Minecraft.getInstance().player != null && isScreenValidForRadialToggle())
		{
			toggleRadial();
		}
	}

	public static void toggleRadial(){
		Player player = Minecraft.getInstance().player;
		if (Minecraft.getInstance().screen == null)
		{
			RadialMenuKeyHandler.openRadial(player);
		}
		else if(Minecraft.getInstance().screen instanceof GuiRadialMenu)
		{
			Minecraft.getInstance().player.closeContainer();
		}
	}

	private static boolean isScreenValidForRadialToggle()
	{
		return Minecraft.getInstance().screen == null || Minecraft.getInstance().screen instanceof GuiRadialMenu<?>;
	}

	public static boolean isRadialKeyPressed(Key eventKey)
	{
		return eventKey.getKey() == ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue() &&
			eventKey.getAction() == InputConstants.PRESS;
	}

	public static void openRadial(Player player)
	{
		List<RadialMenuSlot<MateriaSlotData>> slots = getMateriaSlotDataFromPlayerCapability(player);
		// probably want to change the callback structure here as int is pretty weak.
		Minecraft.getInstance().setScreen(makeRadialForSlots(slots));
	}

	private static Screen makeRadialForSlots(List<RadialMenuSlot<MateriaSlotData>> slots)
	{
		return new GuiRadialMenu<>(new RadialMenu<>(AbilityKeyHandler::doSlotAssignment, slots, RenderUtils::drawItemAsIcon, 3));
	}

	public static List<RadialMenuSlot<MateriaSlotData>> getMateriaSlotDataFromPlayerCapability(Player player)
	{
		var result = new ArrayList<RadialMenuSlot<MateriaSlotData>>();
		player.getCapability(MateriaCapabilityImpl.MATERIA).ifPresent(c -> deserializePlayerCapabilityToMateriaData(c, result));
		return result;
	}

	private static void deserializePlayerCapabilityToMateriaData(MateriaCapability c, List<RadialMenuSlot<MateriaSlotData>> result)
	{
		if (!c.getStock().isEmpty())
		{
			c.getStock().forEach((e, v) -> result.add(convertStockToSlot(c, e)));
		}
	}

	private static RadialMenuSlot<MateriaSlotData> convertStockToSlot(MateriaCapability c, MateriaEnum e)
	{
		// Base slots are those that are just materia-defined.
		// They're not abilities, they're categories of abilities. You step into them.
		var slotData = new MateriaSlotData(e.name(), e.getValue(), e.getRepresentative(), c.getMateria(e), c.getLimit(e));
		return new RadialMenuSlot<>(e.name(), e.getDisplay(c), e.getPercent(c), slotData);
	}
}
