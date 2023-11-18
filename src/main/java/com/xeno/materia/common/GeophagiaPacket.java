package com.xeno.materia.common;

import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.ldtteam.aequivaleo.api.compound.type.ICompoundType;
import com.xeno.materia.MateriaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class GeophagiaPacket
{
	private final BlockPos pos;

	public GeophagiaPacket(BlockPos pos)
	{
		this.pos = pos;
	}

	public GeophagiaPacket(FriendlyByteBuf buf)
	{
		this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.pos.getX());
		buf.writeInt(this.pos.getY());
		buf.writeInt(this.pos.getZ());
	}

	public class Handler
	{
		public static boolean onMessage(GeophagiaPacket message, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> handle(message, ctx));

			ctx.get().setPacketHandled(true);
			return true;
		}

		private static void handle(GeophagiaPacket message, Supplier<NetworkEvent.Context> ctx)
		{
			var player = ctx.get().getSender();
			if (player == null)
			{
				return;
			}

			var thing = player.level().getBlockState(message.pos);
			// absolutely not. I will not eat inventories, and I don't care if sometimes things are just weirdly inedible.
			// it's better to be safe here.
			if (thing.hasBlockEntity()) {
				return;
			}
			var lParams = buildLootParams(player);
			var drops = thing.getDrops(lParams);
			// don't forget we're client side, this is a display thing. Equivalencies are fractional.
			var materiaValues = new HashMap<ResourceLocation, Double>();
			for(var drop : drops) {
				var materiaValue = MateriaMod.getEquivalency(drop.getItem());
				if (materiaValue.isEmpty()) {
					continue;
				}
				for(var materia : materiaValue)
				{
					var loc = materia.getType().getRegistryName();
					if (materiaValues.containsKey(loc)) {
						materiaValues.put(loc, materiaValues.get(loc) + materia.getAmount());
					}
					else
					{
						materiaValues.put(loc, materia.getAmount());
					}
				}
			}


			var cap = player.getCapability(MateriaCapabilities.MATERIA);
			cap.ifPresent(c -> c.absorbMateria(materiaValues));
		}
	}
	private static LootParams.Builder buildLootParams(ServerPlayer pEntity)
	{
		return new LootParams.Builder(pEntity.serverLevel())
			.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pEntity.blockPosition()))
			.withParameter(LootContextParams.TOOL, pEntity.getMainHandItem()) // if any tool, it counts, but it has to be in main-hand.
			.withOptionalParameter(LootContextParams.THIS_ENTITY, pEntity);
	}
}
