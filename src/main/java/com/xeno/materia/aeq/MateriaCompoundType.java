package com.xeno.materia.aeq;

import com.ldtteam.aequivaleo.api.compound.type.ICompoundType;
import com.ldtteam.aequivaleo.api.compound.type.group.ICompoundTypeGroup;
import com.xeno.materia.common.MateriaEnum;
import com.xeno.materia.MateriaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.function.Supplier;

public class MateriaCompoundType implements ICompoundType {
	public final ResourceLocation registryName;
	public final MateriaEnum materia;
	public final Supplier<MateriaCompoundTypeGroup> groupSupplier;
	public final ItemStack representative;

	public MateriaCompoundType(MateriaEnum materia, Supplier<MateriaCompoundTypeGroup> groupSupplier, Item itemRepresentative) {
		this.materia = materia;
		this.groupSupplier = groupSupplier;
		this.registryName = MateriaMod.location(materia.name().toLowerCase(Locale.ENGLISH));
		this.representative = new ItemStack(itemRepresentative, 1);
	}

	public MateriaEnum getMateria() { return materia; }

	@Override
	public ICompoundTypeGroup getGroup() {
		return groupSupplier.get();
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}
}