package com.xeno.materia.common.packets;

import com.xeno.materia.MateriaMod;
import com.xeno.materia.common.capabilities.MateriaCapability;
import com.xeno.materia.common.capabilities.MateriaCapabilityImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MateriaSyncPacket
{

    private final CompoundTag nbt;
    private final String reason;

    public MateriaSyncPacket(MateriaCapability cap, String reason)
    {
        this.nbt = cap.serializeNBT();
        this.reason = reason;
    }

    public MateriaSyncPacket(FriendlyByteBuf buf)
    {
        this.nbt = buf.readNbt();
        this.reason = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeNbt(this.nbt);
        buf.writeUtf(this.reason);
    }

    public class Handler
    {
        public static boolean onMessage(MateriaSyncPacket message, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> handle(message));

            ctx.get().setPacketHandled(true);
            return true;
        }

        private static void handle(MateriaSyncPacket message)
        {
            // if you're getting this packet, your local client is the one who needs the update, simple as.
            var player = Minecraft.getInstance().player;
            if (player == null)
            {
                return;
            }

            player.getCapability(MateriaCapabilityImpl.MATERIA).ifPresent(c -> c.deserializeNBT(message.nbt));
            MateriaMod.debug("synced player client nbt (entire materia set?) reason: " + message.reason);
            player.getCapability(MateriaCapabilityImpl.MATERIA).ifPresent(c -> c.getStock().forEach((k, v) ->
                    MateriaMod.debug(k.name() + ": " + v)));
        }
    }
}
