package com.xeno.materia.common;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public enum MateriaEnum implements Comparable<MateriaEnum>, StringRepresentable
{
    DENIED(-1, ItemStack.EMPTY), CRYO(0, new ItemStack(Blocks.BLUE_ICE)), PYRO(1, new ItemStack(Blocks.MAGMA_BLOCK)), MYCO(2, new ItemStack(Blocks.MYCELIUM)), GEMMA(3, new ItemStack(Blocks.EMERALD_BLOCK)), PHOTO(4, new ItemStack(Blocks.GLOWSTONE)), VIVO(5, new ItemStack(Blocks.TNT)), KREA(6, new ItemStack(Blocks.BONE_BLOCK)), GEO(7, new ItemStack(Blocks.DIRT)), ORICHO(8, new ItemStack(Blocks.IRON_BLOCK)), PROTO(9, new ItemStack(Blocks.ANCIENT_DEBRIS)), AERO(10, new ItemStack(Blocks.PURPUR_PILLAR)), HYDRO(11, new ItemStack(Blocks.PRISMARINE)), XYLO(12, new ItemStack(Blocks.ACACIA_LOG)), CHLORO(13, new ItemStack(Blocks.ACACIA_LEAVES)), ELECTRO(14, new ItemStack(Blocks.REDSTONE_BLOCK)), ANIMA(15, new ItemStack(Blocks.SOUL_SAND));

    private final int value;
    public static final MateriaEnum[] values = values();
    private static final Map<Integer, MateriaEnum> valueMap = new HashMap<>();

    public static Map<Integer, MateriaEnum> valueMap()
    {
        if (valueMap.isEmpty())
        {
            valueMap.putAll(initializeMappings());
        }
        return valueMap;
    }

    private static Map<Integer, MateriaEnum> initializeMappings()
    {
        var result = new HashMap<Integer, MateriaEnum>();
        for (MateriaEnum value : MateriaEnum.values())
        {
            valueMap.put(value.getValue(), value);
        }
        return result;
    }

    public int getValue()
    {
        return this.value;
    }

    public Long getLimit(Player player)
    {
        return player.getData(MateriaRegistry.MATERIA_LIMIT_ATTACHMENTS.get(this));
    }

    public Long getStock(Player player)
    {
        return player.getData(MateriaRegistry.MATERIA_STOCK_ATTACHMENTS.get(this));
    }

    MateriaEnum(int value, ItemStack representative)
    {
        this.value = value;
    }

    public String getDisplay(long amount, long limit)
    {
        return String.format("%s / %s", MateriaUtils.getAmountForDisplay(amount), MateriaUtils.getAmountForDisplay(limit));
    }

    public String getBasicPercent(long amount, long limit)
    {
        return String.format("%d %%", (long) (limit > 0 ? (amount * 100.0f / limit) : 0f));
    }

    public String getDisplay(Player p)
    {
        return getDisplay(getStock(p), getLimit(p));
    }

    public String getPercent(Player p)
    {
        return getBasicPercent(getStock(p), getLimit(p));
    }

    public String getToast(long amount, long limit)
    {
        return this.name() + " " + getDisplay(amount, limit);
    }

    public double getPercentValue(Player p)
    {
        return getLimit(p) == 0d ? 0d : (double) getStock(p) / (double) getLimit(p);
    }

    public void changeMateria(Player p, long longValue)
    {
        var throttled = Math.min(getLimit(p), getStock(p) + longValue);
        p.setData(MateriaRegistry.stockOf(this), throttled);
    }

    public void changeLimit(Player p, long longValue)
    {
        p.setData(MateriaRegistry.limitOf(this), longValue);
    }

    public long getSpace(Player p)
    {
        return getLimit(p) - getStock(p);
    }

    public void spendMateriaOnLimit(LocalPlayer player)
    {
        var stockNeeded = (long)Math.ceil(getLimit(player) * MateriaConfig.materiaLimitCost);
        if (getStock(player) >= stockNeeded) {
            changeMateria(player, -stockNeeded);
            changeLimit(player, MateriaConfig.materiaLimitStep);
        }
    }

    @Override
    public String getSerializedName()
    {
        return name();
    }

    public ResourceLocation texture()
    {
        return MateriaRegistry.MATERIA_ICON_NAMES.get(this);
    }
}
