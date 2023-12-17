package com.xeno.materia.client.radial;

import net.minecraft.resources.ResourceLocation;

public record MateriaSlotData(String name, int materia, ResourceLocation spriteLocation, Long amount, Long limit) {
}
