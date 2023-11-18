package com.xeno.materia.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.xeno.materia.common.GeophagiaPacket;
import com.xeno.materia.common.MateriaNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Column;
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
		var hit = getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.ANY); // unsure about fluid thing here.
		MateriaNetworking.sendToServer(new GeophagiaPacket(hit.getBlockPos()));
	}

	protected static BlockHitResult getPlayerPOVHitResult(Level pLevel, Player pPlayer, ClipContext.Fluid pFluidMode) {
		float f = pPlayer.getXRot();
		float f1 = pPlayer.getYRot();
		Vec3 vec3 = pPlayer.getEyePosition();
		float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
		float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
		float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = pPlayer.getBlockReach();
		Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
		return pLevel.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, pFluidMode, pPlayer));
	}

	private static boolean isGeophagiaKey(int key)
	{
		return key == ModKeyBindings.GEOPHAGIA.getKey().getValue();
	}
}
