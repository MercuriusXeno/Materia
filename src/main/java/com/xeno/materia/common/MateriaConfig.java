package com.xeno.materia.common;

import com.xeno.materia.MateriaMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
@EventBusSubscriber(modid = MateriaMod.ID, bus = Bus.MOD)
public class MateriaConfig
{
    private static final Builder BUILDER = new Builder();

    private static final LongValue DEFAULT_MATERIA_LIMIT = BUILDER
            .comment("How many units of each type of materia the player can hold before any investment.")
            .defineInRange("defaultMateriaLimit", 100, 0, Long.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

//
    public static long defaultMateriaMax;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        defaultMateriaMax = DEFAULT_MATERIA_LIMIT.get();
    }
}
