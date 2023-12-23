package com.xeno.materia.common;

import com.xeno.materia.client.radial.MateriaSlotData;
import com.xeno.materia.client.radial.RadialMenuSlot;
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
    DENIED(-1), CRYO(0), PYRO(1), MYCO(2), GEMMA(3), PHOTO(4), VIVO(5), KREA(6), GEO(7),
    ORICHO(8), PROTO(9), AERO(10), HYDRO(11), XYLO(12), CHLORO(13), ELECTRO(14), ANIMA(15);

    private final int value;
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

    MateriaEnum(int value)
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
        var stockNeeded = (long) Math.ceil(getLimit(player) * MateriaConfig.materiaLimitCost);
        if (getStock(player) >= stockNeeded)
        {
            changeMateria(player, -stockNeeded);
            changeLimit(player, getLimit(player) + MateriaConfig.materiaLimitStep);
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

    public RadialMenuSlot<MateriaSlotData> makeRadialSlot(Player p)
    {
        // Base slots are those that are just materia-defined.
        // They're not abilities, they're categories of abilities. You step into them.
        var slotData = new MateriaSlotData(this.name(), this.getValue(), MateriaRegistry.MATERIA_ICON_NAMES.get(this), this.getStock(p), this.getLimit(p));
        return new RadialMenuSlot<>(this.name(), this.getDisplay(p), this.getPercent(p), this.getPercentValue(p), this, slotData);
    }
}
