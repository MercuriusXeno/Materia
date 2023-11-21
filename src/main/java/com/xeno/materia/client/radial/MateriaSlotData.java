package com.xeno.materia.client.radial;

import net.minecraft.world.item.ItemStack;

public record MateriaSlotData(String name, int materia, ItemStack representative, Long amount, Long limit) {
}
