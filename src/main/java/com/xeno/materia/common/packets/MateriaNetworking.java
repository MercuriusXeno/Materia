package com.xeno.materia.common.packets;
import com.xeno.materia.MateriaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class MateriaNetworking
{

	public static SimpleChannel INSTANCE;

	private static int ID = 0;

	public static int nextID()
	{
		return ID++;
	}

	public static void registerMessages()
	{
		INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(MateriaMod.ID, "network"), () -> "1.0", s -> true, s -> true);

		INSTANCE.registerMessage(nextID(), ConsumeItemPacket.class, ConsumeItemPacket::encode, ConsumeItemPacket::new, ConsumeItemPacket.Handler::onMessage);
		INSTANCE.registerMessage(nextID(), MateriaUpdatePacket.class, MateriaUpdatePacket::encode, MateriaUpdatePacket::new, MateriaUpdatePacket.Handler::onMessage);
		INSTANCE.registerMessage(nextID(), MateriaSyncPacket.class, MateriaSyncPacket::encode, MateriaSyncPacket::new, MateriaSyncPacket.Handler::onMessage);
		INSTANCE.registerMessage(nextID(), MateriaLimitUpdatePacket.class, MateriaLimitUpdatePacket::encode, MateriaLimitUpdatePacket::new, MateriaLimitUpdatePacket.Handler::onMessage);
		INSTANCE.registerMessage(nextID(), SpendMateriaOnLimitPacket.class, SpendMateriaOnLimitPacket::encode, SpendMateriaOnLimitPacket::new, SpendMateriaOnLimitPacket.Handler::onMessage);
	}

	public static void sendToNearby(Level world, BlockPos pos, Object toSend)
	{
		if (world instanceof ServerLevel ws) {
			ws.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).stream()
					.filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64)
					.forEach(p -> INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), toSend));
		}
	}

	public static void sendToNearby(Level world, Entity e, Object toSend)
	{

		sendToNearby(world, e.blockPosition(), toSend);
	}

	public static void sendToPlayerClient(Object msg, ServerPlayer player)
	{
		if (player.connection == null) {
			return;
		}
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}

	public static void sendToServer(Object msg)
	{
		INSTANCE.sendToServer(msg);
	}
}