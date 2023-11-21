package com.xeno.materia.common.capabilities;

import com.ldtteam.aequivaleo.api.compound.type.ICompoundType;
import com.xeno.materia.common.MateriaConfig;
import com.xeno.materia.common.MateriaEnum;
import com.xeno.materia.common.MateriaRegistry;
import com.xeno.materia.common.packets.MateriaLimitUpdatePacket;
import com.xeno.materia.common.packets.MateriaNetworking;
import com.xeno.materia.common.packets.MateriaUpdatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

public class MateriaCapabilityImpl implements MateriaCapability
{
    public static Capability<MateriaCapability> MATERIA = CapabilityManager.get(new CapabilityToken<>()
    {
    });
    private final TreeMap<MateriaEnum, Long> limits = new TreeMap<MateriaEnum, Long>(this::materiaSorter);
    private final TreeMap<MateriaEnum, Long> stock = new TreeMap<MateriaEnum, Long>(this::materiaSorter);

    private int materiaSorter(MateriaEnum e, MateriaEnum o)
    {
        return e.getValue() - o.getValue();
    }

    private final Player player;

    public MateriaCapabilityImpl(Player player)
    {
        this.player = player;
        setDefaults();
    }

    private void loadFromCap(@NotNull MateriaCapability materiaCapability)
    {
        materiaCapability.getStock().forEach(this::setMateria);
        materiaCapability.getLimits().forEach(this::setLimit);
    }

    private void setDefaults()
    {
        // prime defaults
        MateriaRegistry.MATERIA.forEach(this::primeMateriaDefault);
    }

    private void primeMateriaDefault(MateriaEnum k, RegistryObject<ICompoundType> v)
    {
        if (k == MateriaEnum.DENIED)
        {
            return;
        }

        if (!limits.containsKey(k))
        {
            limits.put(k, MateriaConfig.defaultMateriaMax);
        }

        if (!stock.containsKey(k))
        {
            stock.put(k, 0L);
        }
    }

    @Override
    public TreeMap<MateriaEnum, Long> getLimits()
    {
        return limits;
    }

    @Override
    public TreeMap<MateriaEnum, Long> getStock()
    {
        return stock;
    }

    @Override
    public long getMateria(MateriaEnum type)
    {
        return stock.getOrDefault(type, 0L);
    }

    @Override
    public long setMateria(MateriaEnum type, long i)
    {
        var was = getMateria(type);
        if (was == i)
        {
            return i;
        }
        var delta = i - was;
        doMateriaGrowth(type, delta);
//        else if (player instanceof LocalPlayer)
//        {
//            // Minecraft.getInstance().getToasts().addToast(new MateriaPickupToast(type, delta, getLimit(type)));
//        }

        // fire an update event server side that syncs packets back to client about how much materia they now have.
        updateMateriaAmount(type, i);

        var overflow = i - getLimit(type);
        if (overflow > 0)
        {
            doOverflow(type, overflow);
        }

        stock.put(type, Math.min(getLimit(type), i));

        return stock.get(type);
    }

    private void doMateriaGrowth(MateriaEnum type, long delta)
    {
        addLimit(type, getMateriaGrowthFromDelta(delta));
    }

    private Long getMateriaGrowthFromDelta(long delta)
    {
        return (long) Math.floor(Math.sqrt(delta)); // wip winging it, not a real thing, probably gonna make this config
    }

    @Override
    public long getLimit(MateriaEnum type)
    {
        return limits.getOrDefault(type, 0L);
    }

    @Override
    public long setLimit(MateriaEnum type, long i)
    {
        // limit not changing, leave things be and don't send packets.
        if (getLimit(type) == i)
        {
            return i;
        }

        // fire an update event server side that syncs packets back to client about how much materia they can now have.
        updateLimitAmount(type, i);

        limits.put(type, i);
        return limits.get(type);
    }

    private long addLimit(MateriaEnum type, long i)
    {
        return setLimit(type, getLimit(type) + i);
    }

    public long addMateria(MateriaEnum type, long i)
    {
        return setMateria(type, getMateria(type) + i);
    }

    @Override
    public void doOverflow(MateriaEnum type, long overflow)
    {
        // formulas here need adjustment and the negative effects are still todo same as the baseline materia growth
        addLimit(type, getMateriaGrowthFromDelta((long)Math.floor(overflow * MateriaConfig.overflowMateriaGrowth)));
    }

    @Override
    public void absorbMateria(HashMap<ResourceLocation, Double> materiaValues)
    {
        materiaValues.forEach(this::absorbMateria);
    }

    public void updateMateriaAmount(MateriaEnum type, long i)
    {
        if (player instanceof ServerPlayer sp)
        {
            MateriaNetworking.sendToPlayerClient(new MateriaUpdatePacket(type, i), sp);
        }
    }

    public void updateLimitAmount(MateriaEnum type, long i)
    {
        if (player instanceof ServerPlayer sp)
        {
            MateriaNetworking.sendToPlayerClient(new MateriaLimitUpdatePacket(type, i), sp);
        }
    }

    private void absorbMateria(ResourceLocation resourceLocation, Double aDouble)
    {
        if (!MateriaRegistry.MATERIA_NAMES.containsKey(resourceLocation))
        {
            return;
        }
        var materiaEnum = MateriaRegistry.MATERIA_NAMES.get(resourceLocation);
        addMateria(materiaEnum, (long) Math.floor(aDouble));
    }

    @Override
    public boolean hasMateria(MateriaEnum type, long i)
    {
        return stock.containsKey(type);
    }

    @Override
    public void doDeathPenalty()
    {
        limits.forEach((k, v) -> limits.put(k, (long) Math.floor(v * MateriaConfig.lossOfLimitOnDeath)));
        stock.forEach((k, v) -> stock.put(k, (long) Math.floor(v * MateriaConfig.lossOfMateriaOnDeath)));
    }

    @Override
    public CompoundTag serializeNBT()
    {
        var tag = new CompoundTag();
        getLimits().forEach((k, v) -> tag.putLong( k.name() + "_limit", v));
        getStock().forEach((k, v) -> tag.putLong(k.name(), v));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        Arrays.stream(MateriaEnum.values()).forEach(e -> loadLimitFromNbt(nbt, e));
        Arrays.stream(MateriaEnum.values()).forEach(e -> loadStockFromNbt(nbt, e));
    }

    private void loadLimitFromNbt(CompoundTag nbt, MateriaEnum e)
    {
        setLimit(e, getLimitFromNbt(nbt, e.name()));
    }

    private long getLimitFromNbt(CompoundTag nbt, String name)
    {
        return getLongFromNbt(nbt, name + "_limit");
    }

    private void loadStockFromNbt(CompoundTag nbt, MateriaEnum e)
    {
        setMateria(e, getLongFromNbt(nbt, e.name()));
    }

    private long getLongFromNbt(CompoundTag nbt, String name)
    {
        var result = 0L;
        if (nbt.contains(name))
        {
            result = nbt.getLong(name);
        }
        return result;
    }
}
