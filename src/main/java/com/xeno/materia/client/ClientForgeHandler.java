package com.xeno.materia.client;

import com.xeno.materia.MateriaMod;
import com.xeno.materia.client.radial.GuiRadialMenu;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MateriaMod.ID)
public class ClientForgeHandler {

	@SubscribeEvent
	public static void overlayEvent(RenderGuiOverlayEvent.Pre event) {
		if (Minecraft.getInstance().screen instanceof GuiRadialMenu && event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
			event.setCanceled(true);
		}
	}
}
