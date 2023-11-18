package com.xeno.materia.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.DynamicFluidContainerModel;
import org.jline.utils.Colors;

import java.util.HashMap;
import java.util.Map;

public enum MateriaEnum {
	DENIED(-1, ItemStack.EMPTY),
	CRYO(0, new ItemStack(Items.SNOWBALL)),
	PYRO(1, new ItemStack(Items.BLAZE_POWDER)),
	MYCO(2, new ItemStack(Items.BROWN_MUSHROOM)),
	GEMMA(3, new ItemStack(Items.DIAMOND)),
	PHOTO(4, new ItemStack(Items.GLOW_INK_SAC)),
	DYNA(5, new ItemStack(Items.GUNPOWDER)),
	KREA(6, new ItemStack(Items.PORKCHOP)),
	GEO(7, new ItemStack(Items.CLAY_BALL)),
	ORICHO(8, new ItemStack(Items.RAW_COPPER)),
	PROTO(9, new ItemStack(Items.GHAST_TEAR)),
	AERO(10, new ItemStack(Items.FEATHER)),
	HYDRO(11, new ItemStack(Items.HEART_OF_THE_SEA)),
	XYLO(12, new ItemStack(Items.STICK)),
	CHLORO(13, new ItemStack(Items.FERN)),
	ELECTRO(14, new ItemStack(Items.REDSTONE)),
	PHASMO(15, new ItemStack(Items.LAPIS_LAZULI));

	private final int value;
	private final ItemStack representative;
	public int getValue() { return this.value; }
	public ItemStack getRepresentative() {return this.representative;}
	MateriaEnum(int value, ItemStack representative)
	{
		this.value = value;
		this.representative = representative;
	}

	private static final Map<Integer, MateriaEnum> map = new HashMap<Integer, MateriaEnum>();
	public static MateriaEnum valueOf(int value) {
		return map.get(value);
	}

}
