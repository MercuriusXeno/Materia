package com.xeno.materia.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.HashMap;

public class MateriaCarrierCapability implements IMateriaCarrierCapability
{
    HashMap<MateriaEnum, Long> limit = new HashMap<>();
    HashMap<MateriaEnum, Long> stock = new HashMap<>();
    private Player player;

    @Override
    public HashMap<MateriaEnum, Long> getStock()
    {
        return stock;
    }

    @Override
    public long getMateria(MateriaEnum type)
    {
        return hasMateria(type, 0L) ? stock.get(type) : setMateria(type, 0L);
    }

    @Override
    public long setMateria(MateriaEnum type, long i)
    {
        var overflow = i - getLimit(type);
        if (overflow > 0)
        {
            doOverflow(overflow);
        }

        stock.put(type, Math.min(getLimit(type), i));
        return stock.get(type);
    }

    public long addMateria(MateriaEnum type, long i) {
        return setMateria(type, getMateria(type) + i);
    }

    @Override
    public void doOverflow(long overflow)
    {
        // noop, do something with overflow materia, probably increase limit tbh.
    }

    @Override
    public void absorbMateria(HashMap<ResourceLocation, Double> materiaValues)
    {
        materiaValues.forEach(this::absorbMateria);
    }

    private void absorbMateria(ResourceLocation resourceLocation, Double aDouble)
    {
        if (!MateriaRegistry.MATERIA_NAMES.containsKey(resourceLocation))
        {
            return;
        }
        var materiaEnum = MateriaRegistry.MATERIA_NAMES.get(resourceLocation);
        addMateria(materiaEnum, (long)Math.floor(aDouble));
    }

    @Override
    public boolean hasMateria(MateriaEnum type, long i)
    {
        return stock.containsKey(type);
    }

    @Override
    public long getLimit(MateriaEnum type)
    {
        return limit.getOrDefault(type, 0L);
    }

    @Override
    public long setLimit(MateriaEnum type, long i)
    {
        limit.put(type, i);
        return limit.get(type);
    }

    @Override
    public void setPlayer(Player object)
    {
        this.player = object;
    }

    @Override
    public Player getPlayer()
    {
        return player;
    }

    @Override
    public void doDeathPenalty()
    {
        // noop
    }

    @Override
    public CompoundTag serializeNBT()
    {
        var tag = new CompoundTag();
        getStock().forEach((k, v) -> tag.putLong(k.name(), v));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        Arrays.stream(MateriaEnum.values()).forEach(e -> loadMateriaFromNbt(nbt, e));
    }

    private void loadMateriaFromNbt(CompoundTag nbt, MateriaEnum e)
    {
        setMateria(e, getMateriaValueFromNbt(nbt, e.name()));
    }

    private long getMateriaValueFromNbt(CompoundTag nbt, String name)
    {
        var result = 0L;
        if (nbt.contains(name))
        {
            result = nbt.getLong(name);
        }
        return result;
    }
}
