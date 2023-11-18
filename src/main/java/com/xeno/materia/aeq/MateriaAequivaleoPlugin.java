package com.xeno.materia.aeq;

import com.ldtteam.aequivaleo.api.plugin.AequivaleoPlugin;
import com.ldtteam.aequivaleo.api.plugin.IAequivaleoPlugin;
import com.xeno.materia.MateriaMod;
import com.xeno.materia.common.MateriaRegistry;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

@AequivaleoPlugin
public class MateriaAequivaleoPlugin implements IAequivaleoPlugin {
	public static final String ID = MateriaMod.location("materia_types").toString();

	@Override
	public void onConstruction()
	{
		ModList.get().getModContainerById(MateriaMod.ID).ifPresent(mod -> {
			MateriaRegistry.TYPES.register(((FMLModContainer) mod).getEventBus());
			MateriaRegistry.TYPE_GROUPS.register(((FMLModContainer) mod).getEventBus());
		});
	}

	@Override
	public String getId() {
		return ID;
	}
}