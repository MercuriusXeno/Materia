package com.xeno.materia;

import com.ldtteam.aequivaleo.api.IAequivaleoAPI;
import com.ldtteam.aequivaleo.api.analysis.AnalysisState;
import com.mojang.logging.LogUtils;
import com.xeno.materia.client.MateriaTooltipEventHandler;
import com.xeno.materia.common.MateriaConfig;
import com.xeno.materia.common.MateriaNames;
import com.xeno.materia.common.MateriaRegistry;
import com.xeno.materia.common.packets.MateriaNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MateriaMod.ID)
public class MateriaMod {
    // Define mod id in a common place for everything to reference
    public static final String ID = MateriaNames.MATERIA;
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // I commented these out because I don't have items or blocks (yet? at all? idk)
    // so I may need this later, but idk.

    // Creates a new Block with the id "materia:example_block", combining the namespace and path
    // public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // Creates a new BlockItem with the id "materia:example_block", combining the namespace and path
    // public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Creates a new food item with the id "materia:example_id", nutrition 1 and saturation 2
    //public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
    //        .alwaysEat().nutrition(1).saturationMod(2f).build())));

//    // Creates a creative tab with the id "materia:example_tab" for the example item, that is placed after the combat tab
//    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
//            .withTabsBefore(CreativeModeTabs.COMBAT)
//            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
//            .displayItems((parameters, output) -> {
//                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
//            }).build());

    public MateriaMod(IEventBus modEventBus) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        MateriaRegistry.init(modEventBus);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MateriaConfig.SPEC);

        MateriaNetworking.registerMessages();
    }

    public static boolean isAequivaleoLoaded() {
        return Minecraft.getInstance().level != null &&
                IAequivaleoAPI.getInstance().getState(Minecraft.getInstance().level.dimension()) == AnalysisState.COMPLETED;
    }

    public static void debug(String... sArgs) {
        // noop
        var joined = String.join(", ", sArgs);
        LOGGER.debug(joined);
    }

    public static ResourceLocation mcLoc(String path) {
        return new ResourceLocation(path);
    }
    public static ResourceLocation aeqLoc(String s) { return new ResourceLocation(MateriaNames.AEQUIVALEO, s); }

    public static ResourceLocation location(String s) {
        return new ResourceLocation(ID, s);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        // LOGGER.info("HELLO FROM COMMON SETUP");

        // if (Config.logDirtBlock)
        //     LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        // LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        // Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
        // done through annotations
        // MinecraftForge.EVENT_BUS.addGenericListener(Player.class, MateriaCapabilities::attachPlayerMateriaCapability);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
        //     event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        // LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            // LOGGER.info("HELLO FROM CLIENT SETUP");
            // LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SubscribeEvent
        public static void onTextureStitch(TextureAtlasStitchedEvent event) {
            // wound up not needing this as far as I can tell but we'll see
//            MateriaMod.debug(String.format("Confessing atlases for info. If someone left this in, shame them. %s",
//                    event.getAtlas()));
//            if (event.getAtlas().location().equals(MateriaRegistry.VANILLA_GUI_ATLAS)) {
//
//            }

        }
    }

    @Mod.EventBusSubscriber(modid = ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {

            MateriaTooltipEventHandler.handle(event);
        }
    }

    public static ResourceKey<Level> getClientPlayerDimension() {
        ResourceKey<Level> result = null;
        if (Minecraft.getInstance().player != null) {
            result = Minecraft.getInstance().player.level().dimension();
        }
        return result;
    }
}
