package com.xeno.materia.client.keys;

import com.xeno.materia.MateriaMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MateriaMod.ID)
public class KeyHandler {

	@SubscribeEvent
	public static void keyEvent(final InputEvent.Key eventKey) {
		// if Aequivaleo isn't loaded, do _nothing_.
		if (!MateriaMod.isAequivaleoLoaded())
		{
			return;
		}
		RadialMenuKeyHandler.handleRadialKeyEvent(eventKey);
		AbilityKeyHandler.handleAbilityKeyEvent(eventKey);
	}

	@SubscribeEvent
	public static void mouseEvent(final InputEvent.MouseButton eventButton) {
		MateriaControlKeyHandler.handleMouseEventGenerally(eventButton);
	}

	@SubscribeEvent
	public static void screenMouseButtonPressed(final ScreenEvent.MouseButtonPressed pressedMouseEvent) {
		MateriaControlKeyHandler.handleMouseEventOnScreens(pressedMouseEvent);
	}

	@SubscribeEvent
	public static void screenMouseButtonPressed(final ScreenEvent.KeyPressed pressedKeyEvent) {
		MateriaControlKeyHandler.handleKeyEventOnScreens(pressedKeyEvent);
	}
}