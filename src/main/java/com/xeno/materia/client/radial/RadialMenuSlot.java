package com.xeno.materia.client.radial;

import java.util.ArrayList;
import java.util.List;

public record RadialMenuSlot<T>(String slotName, String display, String percent, T primarySlotIcon, List<T> secondarySlotIcons) {

	public RadialMenuSlot(String slotName, String display, String percent, T primarySlotIcon){
		this(slotName, display, percent, primarySlotIcon, new ArrayList<>());
	}
}