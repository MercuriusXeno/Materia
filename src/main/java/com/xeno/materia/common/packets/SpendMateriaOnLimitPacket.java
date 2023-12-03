package com.xeno.materia.common.packets;

import com.xeno.materia.common.MateriaEnum;
import com.xeno.materia.common.capabilities.MateriaCapability;
import com.xeno.materia.common.capabilities.MateriaCapabilityImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SpendMateriaOnLimitPacket
{
    private final int slot;

    public SpendMateriaOnLimitPacket(int slotIndex)
    {
        this.slot = slotIndex;
    }

    public SpendMateriaOnLimitPacket(FriendlyByteBuf buf)
    {
        this.slot = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(this.slot);
    }

    public class Handler
    {
        public static boolean onMessage(SpendMateriaOnLimitPacket message, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> handle(message));

            ctx.get().setPacketHandled(true);
            return true;
        }

        private static void handle(SpendMateriaOnLimitPacket message)
        {
            // if you're getting this packet, your local client is the one who needs the update, simple as.
            var player = Minecraft.getInstance().player;
            if (player == null)
            {
                return;
            }

            player.getCapability(MateriaCapabilityImpl.MATERIA)
                    .ifPresent(c -> tryIncreasingMaximum(c, message));
        }

        private static void tryIncreasingMaximum(@NotNull MateriaCapability materiaCapability,
                                                 SpendMateriaOnLimitPacket message)
        {
            var type = MateriaEnum.valueMap().get(message.slot);
            if (materiaCapability.getRoomLeft(type) == 0) {
                materiaCapability.zeroMateria(type);
                materiaCapability.incrementLimit(type);
            }
        }
    }
}
