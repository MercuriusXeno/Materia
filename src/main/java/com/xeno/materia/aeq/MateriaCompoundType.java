package com.xeno.materia.aeq;

import com.ldtteam.aequivaleo.api.compound.type.ICompoundType;
import com.ldtteam.aequivaleo.api.compound.type.group.ICompoundTypeGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xeno.materia.common.MateriaEnum;
import com.xeno.materia.MateriaMod;
import com.xeno.materia.common.MateriaRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.function.Supplier;

public class MateriaCompoundType implements ICompoundType {

	public static final Codec<MateriaCompoundType> CODEC = StringRepresentable.fromEnum(MateriaEnum::values).xmap(
			e -> new MateriaCompoundType(e, MateriaRegistry.MATERIA_TYPE),
			MateriaCompoundType::getMateria
	);

	public final MateriaEnum materia;
	public final Supplier<MateriaCompoundTypeGroup> groupSupplier;

	public MateriaCompoundType(MateriaEnum materia, Supplier<MateriaCompoundTypeGroup> groupSupplier) {
		this.materia = materia;
		this.groupSupplier = groupSupplier;
	}

	public MateriaEnum getMateria() { return materia; }

	@Override
	public ICompoundTypeGroup getGroup() {
		return groupSupplier.get();
	}
}