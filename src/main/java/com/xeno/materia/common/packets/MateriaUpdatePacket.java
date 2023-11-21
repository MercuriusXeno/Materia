package com.xeno.materia.common.packets;

import com.xeno.materia.MateriaMod;
import com.xeno.materia.common.MateriaEnum;
import com.xeno.materia.common.MateriaRegistry;
import com.xeno.materia.common.capabilities.MateriaCapabilityImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class MateriaUpdatePacket
{

    private final int materiaEnumValue; // most compressed type it can be is probably a short or a byte, considering it
    private final long amount; // caps on materia are unfathomably large for the sake of fun (tm)

    public MateriaUpdatePacket(MateriaEnum materiaType, long amount)
    {
        this.materiaEnumValue = materiaType.getValue();
        this.amount = amount;
    }

    public MateriaUpdatePacket(FriendlyByteBuf buf)
    {
        this.materiaEnumValue = buf.readInt();
        this.amount = buf.readLong();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(this.materiaEnumValue);
        buf.writeLong(this.amount);
    }

    public class Handler
    {
        public static boolean onMessage(MateriaUpdatePacket message, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() ->handle(message));
            ctx.get().setPacketHandled(true);
            return true;
        }

        private static void handle(MateriaUpdatePacket message)
        {
            Optional.ofNullable(Minecraft.getInstance().player).ifPresent(player ->
                player.getCapability(MateriaCapabilityImpl.MATERIA)
                    .ifPresent(c -> c.setMateria(MateriaRegistry.MATERIA_VALUES.get(message.materiaEnumValue), message.amount)));
            MateriaMod.debug("updated " + MateriaRegistry.MATERIA_VALUES.get(message.materiaEnumValue).name() + " " + message.amount);
        }
    }
}
