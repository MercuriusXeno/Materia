package com.xeno.materia.data;

import com.xeno.materia.MateriaMod;
import com.xeno.materia.aeq.MateriaAequivaleoInfoGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = MateriaMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MateriaDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		// get the generator of the event and add a provider to handle the aequivaleo value generation; server-side only.
		event.getGenerator().addProvider(event.includeServer(),
				new MateriaAequivaleoInfoGenerator(event.getGenerator(), event.getLookupProvider()));

	}
}