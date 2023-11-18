package com.xeno.materia.client;

import java.util.ArrayList;
import java.util.List;

public record RadialMenuSlot<T>(String slotName, T primarySlotIcon, List<T> secondarySlotIcons) {

	public RadialMenuSlot(String slotName, T primarySlotIcon){
		this(slotName, primarySlotIcon,new ArrayList<>());
	}
}