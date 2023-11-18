package com.xeno.materia.common;

import com.xeno.materia.MateriaMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;

public interface IMateriaCarrierCapability extends INBTSerializable<CompoundTag>
{
	ResourceLocation ID = MateriaMod.location("cap_materia_carrier");
	HashMap<MateriaEnum, Long> getStock();
	long getMateria(MateriaEnum type);
	long setMateria(MateriaEnum type, long i);
	boolean hasMateria(MateriaEnum type, long i);
	long getLimit(MateriaEnum type);
	long setLimit(MateriaEnum type, long i);
	void setPlayer(Player object);
	Player getPlayer();
	void doDeathPenalty();
	void doOverflow(long overflow);
	void absorbMateria(HashMap<ResourceLocation, Double> materiaValues);
}
