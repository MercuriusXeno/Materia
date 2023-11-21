package com.xeno.materia.client;

import com.xeno.materia.MateriaMod;
import com.xeno.materia.client.radial.GuiRadialMenu;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MateriaMod.ID)
public class ClientForgeHandler {

	@SubscribeEvent
	public static void overlayEvent(RenderGuiOverlayEvent.Pre event) {
		if (Minecraft.getInstance().screen instanceof GuiRadialMenu && event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
			event.setCanceled(true);
		}
	}
}
