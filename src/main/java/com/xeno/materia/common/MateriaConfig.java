package com.xeno.materia.common;

import com.xeno.materia.MateriaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@EventBusSubscriber(modid = MateriaMod.ID, bus = Bus.MOD)
public class MateriaConfig
{
    private static final Builder BUILDER = new Builder();

//    private static final BooleanValue LOG_DIRT_BLOCK = BUILDER
//            .comment("Whether to log the dirt block on common setup")
//            .define("logDirtBlock", true);

    private static final LongValue DEFAULT_MATERIA_LIMIT = BUILDER
            .comment("How many units of each type of materia the player can hold before any investment.")
            .defineInRange("defaultMateriaLimit", 64, 0, Long.MAX_VALUE);
    private static final DoubleValue LIMIT_LOST_ON_DEATH = BUILDER
            .comment("How much (%, 0.1 is 10%) limit of each type the player loses on death (cannot go below minimum).")
            .defineInRange("defaultLimitLostOnDeath", 0.5d, 0, Long.MAX_VALUE);
    private static final DoubleValue MATERIA_LOST_ON_DEATH = BUILDER
            .comment("How much (%, 0.1 is 10%) materia of each type the player loses on death.")
            .defineInRange("defaultMateriaLostOnDeath", 0.5d, 0, Long.MAX_VALUE);
    private static final DoubleValue OVERFLOW_GROWTH_COEFFICIENT = BUILDER
            .comment("What percentage of overflow is added directly to your materia limit when geophagia is used.")
            .defineInRange("overflowMateriaGrowth", 0.5d, 0, Long.MAX_VALUE);
//    public static final ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
//            .comment("What you want the introduction message to be for the magic number")
//            .define("magicNumberIntroduction", "The magic number is... ");

//    // a list of strings that are treated as resource locations for items
//    private static final ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
//            .comment("A list of items to log on common setup.")
//            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), MateriaConfig::validateItemName);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

//    public static boolean logDirtBlock;
//    public static int magicNumber;
//    public static String magicNumberIntroduction;
//    public static Set<Item> items;
    public static long defaultMateriaMax;
    public static double lossOfMateriaOnDeath;
    public static double lossOfLimitOnDeath;
    public static double overflowMateriaGrowth;

//    private static boolean validateItemName(final Object obj)
//    {
//        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
//    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        defaultMateriaMax = DEFAULT_MATERIA_LIMIT.get();
        lossOfMateriaOnDeath = DEFAULT_MATERIA_LIMIT.get();
        lossOfLimitOnDeath = DEFAULT_MATERIA_LIMIT.get();
        overflowMateriaGrowth = OVERFLOW_GROWTH_COEFFICIENT.get();
//        logDirtBlock = LOG_DIRT_BLOCK.get();
//        magicNumber = MAGIC_NUMBER.get();
//        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();


        // convert the list of strings into a set of items
//        items = ITEM_STRINGS.get().stream()
//                .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
//                .collect(Collectors.toSet());
    }
}
