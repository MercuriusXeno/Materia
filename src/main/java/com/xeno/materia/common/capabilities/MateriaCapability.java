package com.xeno.materia.common.capabilities;

import com.xeno.materia.MateriaMod;
import com.xeno.materia.common.MateriaEnum;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.TreeMap;

@AutoRegisterCapability
public interface MateriaCapability extends INBTSerializable<CompoundTag>
{
	ResourceLocation ID = MateriaMod.location("cap_materia_carrier");
	TreeMap<MateriaEnum, Long> getLimits();
	TreeMap<MateriaEnum, Long> getStock();
	long getMateria(MateriaEnum type);
	long setMateria(MateriaEnum type, long i);
	long getLimit(MateriaEnum type);
	long setLimit(MateriaEnum type, long i);
	void doDeathPenalty();
	void absorbMateria(HashMap<ResourceLocation, Double> materiaValues);
}
