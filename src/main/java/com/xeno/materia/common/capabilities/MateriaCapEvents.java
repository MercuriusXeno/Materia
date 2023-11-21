package com.xeno.materia.common.capabilities;

import com.xeno.materia.MateriaMod;
import com.xeno.materia.common.packets.MateriaNetworking;
import com.xeno.materia.common.packets.MateriaSyncPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;


@Mod.EventBusSubscriber(modid = MateriaMod.ID)
public class MateriaCapEvents
{
    @SubscribeEvent
    public static void attachPlayerMateriaCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (!(event.getObject() instanceof Player))
        {
            return;
        }
        var cap = new MateriaCapabilityImpl((Player) event.getObject());
        var opt = LazyOptional.of(() -> cap);
        var provider = new ICapabilitySerializable<CompoundTag>()
        {
            @Override
            public CompoundTag serializeNBT()
            {
                return cap.serializeNBT();
            }

            @Override
            public void deserializeNBT(CompoundTag nbt)
            {
                cap.deserializeNBT(nbt);
            }

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction direction)
            {
                if (cap == MateriaCapabilityImpl.MATERIA)
                {
                    return opt.cast();
                }
                return LazyOptional.empty();
            }
        };

        event.addCapability(MateriaMod.location("materia"), provider);
    }

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent e)
    {
        syncPlayerEvent(e.getEntity(), "respawn?");
    }

    @SubscribeEvent
    public static void playerStartTracking(PlayerEvent.StartTracking e)
    {
        // syncPlayerEvent(e.getEntity(), "start tracking?"); // this one fires a LOT and we don't need it
    }

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        syncPlayerEvent(e.getEntity(), "log in?");
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent e)
    {
        syncPlayerEvent(e.getEntity(), "dim change?");
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        // we don't need to clone the cap unless the player died.
        if (!event.isWasDeath())
        {
            return;
        }
        // deserialize the old nbt into the new one, dust hands.
        event.getEntity().getCapability(MateriaCapabilityImpl.MATERIA).ifPresent(nc ->
                event.getOriginal().getCapability(MateriaCapabilityImpl.MATERIA).ifPresent(oc -> doDeathTransfer(event, oc, nc)));

    }

    private static void doDeathTransfer(PlayerEvent e, MateriaCapability oc, MateriaCapability nc)
    {
        nc.deserializeNBT(oc.serializeNBT());
        nc.doDeathPenalty();

        syncPlayerEvent(e.getEntity(), "death transfer?");
    }

    public static void syncPlayerEvent(Player playerEntity, String reason)
    {
        if (playerEntity instanceof ServerPlayer)
        {
            playerEntity.getCapability(MateriaCapabilityImpl.MATERIA)
                    .ifPresent(c -> MateriaNetworking.INSTANCE.send(PacketDistributor.PLAYER
                            .with(() -> (ServerPlayer) playerEntity), new MateriaSyncPacket(c, reason)));
        }
    }
}
