package com.xeno.materia.common;

import com.xeno.materia.MateriaMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod.EventBusSubscriber(modid = MateriaMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MateriaConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.LongValue DEFAULT_MATERIA_LIMIT = BUILDER
            .comment("How many units of each type of materia the player can hold before any investment.")
            .defineInRange("defaultMateriaLimit", 100, 0, Long.MAX_VALUE);
    private static final ModConfigSpec.LongValue LIMIT_INCREASE = BUILDER
            .comment("How many units the limit increases by when you cultivate your materia.")
            .defineInRange("materiaLimitStep", 100, 0, Long.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue LIMIT_COST = BUILDER
            .comment("What percent of your limit you must invest to increase your limit.")
            .defineInRange("materiaLimitCost", 1d, 0d, Double.MAX_VALUE);

    public static final IConfigSpec SPEC = BUILDER.build();

//
    public static long defaultMateriaMax;
    public static long materiaLimitStep;
    public static double materiaLimitCost;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        defaultMateriaMax = DEFAULT_MATERIA_LIMIT.get();
        materiaLimitStep = LIMIT_INCREASE.get();
        materiaLimitCost = LIMIT_COST.get();
    }
}
