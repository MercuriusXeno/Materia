package com.xeno.materia.client.keys;

import com.xeno.materia.MateriaMod;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(modid = MateriaMod.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {

	private static final String CATEGORY = "key.category.materia.general";
	public static final KeyMapping OPEN_RADIAL_HUD = new KeyMapping("key.materia.radial", GLFW.GLFW_KEY_F, CATEGORY);
	public static final KeyMapping GEOPHAGIA = new KeyMapping("key.materia.geophagia", GLFW.GLFW_KEY_G, CATEGORY);
	public static final KeyMapping ABILITY = new KeyMapping("key.materia.ability", GLFW.GLFW_KEY_V, CATEGORY);

	@SubscribeEvent
	public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
		event.register(OPEN_RADIAL_HUD);
		event.register(ABILITY);
		event.register(GEOPHAGIA);
	}
}