package com.xeno.materia.common;

import com.xeno.materia.common.capabilities.MateriaCapability;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.DynamicFluidContainerModel;
import org.jline.utils.Colors;

import java.util.HashMap;
import java.util.Map;

public enum MateriaEnum implements Comparable<MateriaEnum>
{
    DENIED(-1, ItemStack.EMPTY),
    CRYO(0, new ItemStack(Blocks.BLUE_ICE)),
    PYRO(1, new ItemStack(Blocks.MAGMA_BLOCK)),
    MYCO(2, new ItemStack(Blocks.MYCELIUM)),
    GEMMA(3, new ItemStack(Blocks.EMERALD_BLOCK)),
    PHOTO(4, new ItemStack(Blocks.GLOWSTONE)),
    DYNA(5, new ItemStack(Blocks.TNT)),
    KREA(6, new ItemStack(Blocks.BONE_BLOCK)),
    GEO(7, new ItemStack(Blocks.DIRT)),
    ORICHO(8, new ItemStack(Blocks.IRON_BLOCK)),
    PROTO(9, new ItemStack(Blocks.ANCIENT_DEBRIS)),
    AERO(10, new ItemStack(Blocks.PURPUR_PILLAR)),
    HYDRO(11, new ItemStack(Blocks.PRISMARINE)),
    XYLO(12, new ItemStack(Blocks.ACACIA_LOG)),
    CHLORO(13, new ItemStack(Blocks.ACACIA_LEAVES)),
    ELECTRO(14, new ItemStack(Blocks.REDSTONE_BLOCK)),
    PHASMO(15, new ItemStack(Blocks.SOUL_SAND));

    private final int value;
    private final ItemStack representative;

    public int getValue()
    {
        return this.value;
    }

    public ItemStack getRepresentative()
    {
        return this.representative;
    }

    MateriaEnum(int value, ItemStack representative)
    {
        this.value = value;
        this.representative = representative;
    }

    public String getDisplay(long amount, long limit)
    {
        return String.format("%d / %d", amount, limit);
    }

    public String getPercent(long amount, long limit)
    {
        return String.format("%.1f %%", limit > 0 ? (amount * 100.0f / limit) : 0f);
    }

    public String getBasicPercent(long amount, long limit)
    {
        return String.format("%d %%", (long) (limit > 0 ? (amount * 100.0f / limit) : 0f));
    }

    public String getDisplay(MateriaCapability c)
    {
        return getDisplay(c.getMateria(this), c.getLimit(this));
    }

    public String getPercent(MateriaCapability c)
    {
        return getBasicPercent(c.getMateria(this), c.getLimit(this));
    }

    public String getToast(long amount, long limit)
    {
        return this.name() + " " + getDisplay(amount, limit);
    }
}
