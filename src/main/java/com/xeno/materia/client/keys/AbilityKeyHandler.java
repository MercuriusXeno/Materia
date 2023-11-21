package com.xeno.materia.client.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;

public class AbilityKeyHandler {
	public static void handleAbilityKeyEvent(InputEvent.Key eventKey) {
		if (!isAbilityKey(eventKey.getKey()) || Minecraft.getInstance().player == null ||
				eventKey.getAction() != InputConstants.PRESS || Minecraft.getInstance().screen != null) {
			return;
		}
		doAbility();
	}
	private static void doAbility() {
		// noop
	}
	private static boolean isAbilityKey(int key)
	{
		return key == ModKeyBindings.ABILITY.getKey().getValue();
	}
	public static void doSlotAssignment(int i) {
		// noop, do something here where we remember that the player wanted to do X
	}
}
