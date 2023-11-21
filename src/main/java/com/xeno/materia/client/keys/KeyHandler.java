package com.xeno.materia.client.keys;

import com.xeno.materia.MateriaMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
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
		GeophageKeyHandler.handleGeophageKeyEvent(eventKey);
	}
}