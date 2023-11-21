package com.xeno.materia.client.keys;

import com.mojang.blaze3d.platform.InputConstants;
import com.xeno.materia.client.ClientUtils;
import com.xeno.materia.common.packets.GeophagiaPacket;
import com.xeno.materia.common.packets.MateriaNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;

public class GeophageKeyHandler
{
	public static void handleGeophageKeyEvent(InputEvent.Key eventKey) {
		if (!isGeophagiaKey(eventKey.getKey()) || Minecraft.getInstance().player == null ||
				eventKey.getAction() != InputConstants.PRESS || Minecraft.getInstance().screen != null)
		{
			return;
		}

		doGeophagia();
	}

	private static void doGeophagia()
	{
		var player = Minecraft.getInstance().player;
		var hit = ClientUtils.getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.ANY); // unsure about fluid thing here.
		MateriaNetworking.sendToServer(new GeophagiaPacket(hit.getBlockPos()));
	}

	private static boolean isGeophagiaKey(int key)
	{
		return key == ModKeyBindings.GEOPHAGIA.getKey().getValue();
	}
}
