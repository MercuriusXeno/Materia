package com.xeno.materia.client;

import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.xeno.materia.MateriaMod;
import com.xeno.materia.aeq.MateriaCompoundType;
import com.xeno.materia.aeq.MateriaEquivalency;
import com.xeno.materia.common.MateriaEnum;
import com.xeno.materia.common.MateriaUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;
import java.util.Set;

public class MateriaTooltipEventHandler {
    private static void addEquivalenciesToTooltip(List<Component> tooltip, ItemStack stack) {
        if (MateriaMod.getClientPlayerDimension() != null) {
            var itemResult = MateriaEquivalency.getEquivalency(stack);
            addEquivalenciesToTooltip(tooltip, itemResult);
        }
    }

    private static void addEquivalenciesToTooltip(List<Component> tooltip, Set<CompoundInstance> itemResult) {
        addTooltipPrefaceIfResultNotEmpty(itemResult.isEmpty());
        for (var i : itemResult) {
            addEquivalencyToTooltip(tooltip, i);
        }
    }

    private static void addEquivalencyToTooltip(List<Component> tooltip, CompoundInstance i) {
        if (i.getType() instanceof MateriaCompoundType materiaType && materiaType.materia != MateriaEnum.DENIED) {
            tooltip.add(Component.translatable("tooltip.materia.value", materiaType.materia,
                    MateriaUtils.getAmountForDisplay(i.getAmount().longValue())));
        }
    }

    private static void addTooltipPrefaceIfResultNotEmpty(boolean isEquivalencyEmpty) {
        if (!isEquivalencyEmpty) {
            Component.translatable("tooltip.materia.preface");
        }
    }

    public static void handle(ItemTooltipEvent event) {
        addEquivalenciesToTooltip(event.getToolTip(), event.getItemStack());
    }
}
