package com.xeno.materia.client.keys;

import com.xeno.materia.MateriaMod;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(modid = MateriaMod.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {

	private static final String GENERAL_KEYS_CATEGORY = "key.category.materia.general";
	public static final KeyMapping OPEN_RADIAL_HUD_KEY = new KeyMapping("key.materia.radial", GLFW.GLFW_KEY_F, GENERAL_KEYS_CATEGORY);
	public static final KeyMapping CONSUME_KEY = new KeyMapping("key.materia.consume", GLFW.GLFW_KEY_X, GENERAL_KEYS_CATEGORY);
	public static final KeyMapping CREATE_KEY = new KeyMapping("key.materia.create", GLFW.GLFW_KEY_C, GENERAL_KEYS_CATEGORY);
	public static final KeyMapping ABILITY_KEY = new KeyMapping("key.materia.ability", GLFW.GLFW_KEY_G, GENERAL_KEYS_CATEGORY);

	@SubscribeEvent
	public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
		event.register(OPEN_RADIAL_HUD_KEY);
		event.register(ABILITY_KEY);
		event.register(CONSUME_KEY);
		event.register(CREATE_KEY);
	}
}