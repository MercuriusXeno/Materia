package com.xeno.materia.client;

import com.ldtteam.aequivaleo.api.plugin.AequivaleoPlugin;
import com.xeno.materia.MateriaMod;
import com.xeno.materia.aeq.MateriaCompoundType;
import com.xeno.materia.common.MateriaEnum;
import com.xeno.materia.common.MateriaNetworking;
import com.xeno.materia.common.MateriaRegistry;
import com.xeno.materia.common.PacketDoMateriaThing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MateriaMod.ID)
public class KeyHandler {

	@SubscribeEvent
	public static void keyEvent(final InputEvent.Key eventKey) {
		// if Aequivaleo isn't loaded, do _nothing_.
		if (!MateriaMod.isAequivaleoLoaded())
		{
			return;
		}
		RadialMenuKeyHandler.handleRadialKeyEvent(eventKey);
		AbilityKeyHandler.handleAbilityKeyEvent(eventKey);
		GeophageKeyHandler.handleGeophageKeyEvent(eventKey);
	}
}