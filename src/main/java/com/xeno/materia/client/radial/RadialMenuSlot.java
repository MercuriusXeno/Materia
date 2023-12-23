package com.xeno.materia.client.radial;

import com.xeno.materia.common.MateriaEnum;

import java.util.ArrayList;
import java.util.List;

public record RadialMenuSlot<MateriaSlotData>(String slotName, String display, String percent, double percentValue,
								MateriaEnum enumValue, MateriaSlotData primarySlotIcon, List<MateriaSlotData> secondarySlotIcons) {

	public RadialMenuSlot(String slotName, String display, String percent, double percentValue,
						  MateriaEnum enumValue, MateriaSlotData primarySlotIcon){
		this(slotName, display, percent, percentValue, enumValue, primarySlotIcon, new ArrayList<>());
	}
}