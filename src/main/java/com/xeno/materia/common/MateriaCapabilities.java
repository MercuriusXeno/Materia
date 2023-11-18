package com.xeno.materia.common;

import com.xeno.materia.MateriaMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = MateriaMod.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MateriaCapabilities
{
	public static final Capability<IMateriaCarrierCapability> MATERIA = CapabilityManager.get(new CapabilityToken<>() {});

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(IMateriaCarrierCapability.class);
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event)
	{
		// we don't need to clone the cap unless the player died.
		if (!event.isWasDeath()) {
			return;
		}
		// deserialize the old nbt into the new one, dust hands.
		var oldCap = event.getOriginal().getCapability(MATERIA);
		var newCap = event.getEntity().getCapability(MATERIA);
		newCap.ifPresent(nc -> {
			oldCap.ifPresent(oc -> nc.deserializeNBT(oc.serializeNBT()));
			nc.doDeathPenalty();
		});

	}

	@SubscribeEvent
	public static void attachPlayerMateriaCapability(AttachCapabilitiesEvent<Player> event)
	{
		// if (event.getObject() instanceof Player player) {
			event.addCapability(IMateriaCarrierCapability.ID, new ICapabilitySerializable<CompoundTag>() {
				@Override
				public CompoundTag serializeNBT() {
					return inst.orElseThrow(NullPointerException::new).serializeNBT();
				}
				@Override
				public void deserializeNBT(CompoundTag nbt) {
					inst.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
				}
				@Override
				public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
																  @Nullable Direction side) {
					return MATERIA.orEmpty(cap, inst.cast());
				}
				final LazyOptional<IMateriaCarrierCapability> inst = LazyOptional.of(() -> {
					MateriaCarrierCapability i = new MateriaCarrierCapability();
					i.setPlayer(event.getObject());
					// i.setPlayer(player);
					return i;
				});
			});

		// }
	}
}
