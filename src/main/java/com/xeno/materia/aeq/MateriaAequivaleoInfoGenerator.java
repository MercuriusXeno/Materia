package com.xeno.materia.aeq;

import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.ldtteam.aequivaleo.api.compound.information.datagen.ForcedInformationProvider;
import com.xeno.materia.MateriaMod;
import com.xeno.materia.common.MateriaRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class MateriaAequivaleoInfoGenerator extends ForcedInformationProvider
{
	// these are junky expressions for changing the values of lots of things at once by using common values.
	// but there is some variation where it feels warranted. Mostly these are just placeholder values as I've no clue what to anticipate balance-wise? So I'm just wingin' it.

	// this is ints for scaling things by "feel" is basically all this is
	private static final int doubled = 2;
	private static final int tripled = 3;
	private static final int quadrupled = 4;
	private static final int eighth = 15;
	private static final int quarter = eighth * doubled;
	private static final int third = 40;
	private static final int half = quarter * doubled;

	// It may bore you to know most things are just worth X. So many things are just X, it's not very thoughtful at first blush. This is just a way to get something in place.
	private static final int usual = half * doubled;

	// this is the perfunctory doubling required by blocks of things that have slabs, as seen in terrainGeo
	private static final int slabMult = doubled;
	// it's really common for there to just be recipes where 4x something = something else, so quad mults common.

	// the justification for buckets being 3 hydro. it coulda been a lot of numbers, but 3 seems reasonable and sane.
	// the caveat of this section is that lava, through its millibucket concept, controls the price of fire
	// which then extends to the various fire elemental items that are used in smelting, directly. Each one's
	// pyro score is based on how many items it smelts. Lava (1000 = 100 items) so Coal (80 = 8), Blaze (120 = 12)
	private static final int pyroFuelValue = 10;
	private static final int lavaSmelts = 100;
	private static final int coalSmelts = 8;
	private static final int blazeRodSmelts = 12;
	private static final int millibucketCount = lavaSmelts * pyroFuelValue; // which we then use on water too!
	private static final int coalValue = coalSmelts * pyroFuelValue;
	private static final int blazeRodValue = blazeRodSmelts * pyroFuelValue;

	// the reason this is doubled is to make slabs be the baseline "4" and because 8 winds up being a bottom line for glass, weirdly. 9 is a bottom line for ingots.
	private static final int terrainGeo = usual;
	private static final int glass = 8;

	// these are just to make math with sticks, logs and planks basic.
	// the reason sticks have to be 12 is a little convoluted. If it's 3, something winds up with .75 on it, which doubling wouldn't help
	// so quadrupling is the next step.
	private static final int bambooXylo = 24;
	private static final int stickBamboo = doubled;
	private static final int stickXylo = bambooXylo * stickBamboo;
	private static final int plankSticks = doubled;
	private static final int plankXylo = stickXylo * plankSticks;
	private static final int logPlanks = quadrupled;
	private static final int logXylo = plankXylo * logPlanks;

	private static final int copperNuggetValue = 8;
	private static final int ironNuggetValue = copperNuggetValue * tripled;
	private static final int goldNuggetValue = ironNuggetValue * tripled;
	private static final int nineCraftingSlots = 9; // "metal" is broadly 9 because ingots decomposing to nuggets is more divisible at 9
	private static final int ingotValue = copperNuggetValue * nineCraftingSlots;
	private static final int vanillaStackLimit = 64; // this my way of saying throw a 64 in there because a number needs to be big for some reason. Usually multiplying it by something else.
	private static final int diamondGemma = usual * nineCraftingSlots; // somewhat arbitrary inflation here, both of these are test values. I use this on netherite too.

	private static final int spawnEggProto = 1000;
	private static final int woolValue = 36;
	public MateriaAequivaleoInfoGenerator(final DataGenerator dataGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider)
	{
		super(MateriaMod.ID, dataGenerator, lookupProvider);
	}

	@Override
	public void calculateDataToSave()
	{
		// plants and stuff
		savePlantsAndStuff();

		// aquatic stuff and fish
		saveAquaticStuffAndFish();

		// rocks and dirt
		saveRocksAndDirt();

		// most normal foods
		saveChloroFoods(chloro(usual));
		saveKreaAnimalProducts(krea(usual));
		saveKreaWool(krea(woolValue));
		saveMycoStuff(myco(usual));
		saveMycoLogs(myco(logXylo), xylo(logXylo)); // myco logs are sweet, they have both. This fixes an issue where myco logs compete.
		// quasi-food
		saveData(Items.MILK_BUCKET, krea(half), hydro(half));

		// ores, metal ingots
		saveMetalsAndOres();

		// gems, in particular
		saveGemstones();

		// random terrestrial nonsense
		saveMiscTerrestrialStuff();

		// nether
		saveNetherStuff();

		// ender
		saveEnderStuff();

		// containers
		saveContainersOfStuff();

		// air, bedrock, that kinda stuff.
		saveAbjectDenials();

		// making it so spawn eggs have a proto value commensurate with the power I have planned, may change.
		saveSpawnEggs();

		saveMobHeads();

		saveArcheologyStuff();

		saveHorseArmor();
	}

	private void saveHorseArmor()
	{
		saveData(Items.IRON_HORSE_ARMOR, oricho(ingotValue * tripled * tripled));
		saveData(Items.GOLDEN_HORSE_ARMOR, oricho(ingotValue * tripled * tripled * tripled));
		saveData(Items.DIAMOND_HORSE_ARMOR, gemma(ingotValue * tripled * tripled * tripled));
	}

	private void saveMobHeads()
	{
		saveData(Items.CREEPER_HEAD, krea(usual), vivo(usual), anima(half));
		saveData(Items.DRAGON_HEAD, krea(usual), aero(usual), anima(half));
		saveData(Items.PIGLIN_HEAD, krea(usual), pyro(usual), anima(half));
		saveData(Items.PLAYER_HEAD, krea(usual));
		saveData(Items.ZOMBIE_HEAD, krea(usual), anima(usual));
		saveData(Items.SKELETON_SKULL, krea(usual), anima(usual));
		saveData(Items.WITHER_SKELETON_SKULL, krea(usual), anima(usual * quadrupled), pyro(usual));
	}

	private void saveContainersOfStuff()
	{
		saveData(Items.WATER_BUCKET, hydro(millibucketCount), oricho(ingotValue * tripled));
		saveData(Items.LAVA_BUCKET, pyro(millibucketCount), oricho(ingotValue * tripled));
		saveData(Items.AXOLOTL_BUCKET, krea(usual * quadrupled), vivo(usual), hydro(usual * quadrupled), oricho(ingotValue * tripled), denied(usual));
		saveData(Items.COD_BUCKET, krea(usual * quadrupled), vivo(usual), hydro(usual * quadrupled), oricho(ingotValue * tripled), denied(usual));
		saveData(Items.SALMON_BUCKET, krea(usual * quadrupled), vivo(usual), hydro(usual * quadrupled), oricho(ingotValue * tripled), denied(usual));
		saveData(Items.PUFFERFISH_BUCKET, krea(usual * quadrupled), vivo(usual), hydro(usual * quadrupled), oricho(ingotValue * tripled), denied(usual));
		saveData(Items.TADPOLE_BUCKET, krea(usual * quadrupled), vivo(usual), hydro(usual * quadrupled), oricho(ingotValue * tripled), denied(usual));
		saveData(Items.TROPICAL_FISH_BUCKET, krea(usual * quadrupled), vivo(usual), hydro(usual * quadrupled), oricho(ingotValue * tripled), denied(usual));
		saveData(Items.POWDER_SNOW_BUCKET, cryo(millibucketCount), oricho(ingotValue * tripled));
	}

	private void saveGemstones()
	{
		saveData(Items.DIAMOND, gemma(diamondGemma), proto(usual * quadrupled));
		saveData(Items.EMERALD, gemma(usual * nineCraftingSlots));
		saveData(Items.LAPIS_LAZULI, anima(usual), gemma(usual));
		saveData(Items.QUARTZ, gemma(terrainGeo));

		saveData(Items.AMETHYST_BLOCK, gemma(terrainGeo * quadrupled)); // I just think amethyst should be a good find
		saveData(Items.AMETHYST_SHARD, gemma(half));

		saveData(Items.CALCITE, gemma(half));
	}

	private void saveMetalsAndOres()
	{
		// ore blocks
		saveData(Items.COAL, pyro(coalValue));
		saveData(Items.RAW_GOLD, oricho(goldNuggetValue * nineCraftingSlots));
		saveData(Items.RAW_IRON, oricho(ironNuggetValue * nineCraftingSlots));
		saveData(Items.CHAIN, oricho(ironNuggetValue * nineCraftingSlots + ironNuggetValue * doubled));
		saveData(Items.RAW_COPPER, oricho(ingotValue), electro(ingotValue));

		// three copper variants beyond "block", should create the transmutation pathways for all the cut variants, slabs, etc.
		saveData(Items.EXPOSED_COPPER, oricho(ingotValue * nineCraftingSlots), electro(ingotValue * nineCraftingSlots));
		saveData(Items.WEATHERED_COPPER, oricho(ingotValue * nineCraftingSlots), electro(ingotValue * nineCraftingSlots));
		saveData(Items.OXIDIZED_COPPER, oricho(ingotValue * nineCraftingSlots), electro(ingotValue * nineCraftingSlots));

		saveData(Items.REDSTONE, electro(usual));
		saveData(Items.NETHERITE_SCRAP, oricho(ingotValue * vanillaStackLimit), proto(usual * nineCraftingSlots)); // like x7~ gold (64x vs 9x)
	}

	private void saveKreaWool(CompoundInstance krea)
	{
		// weirder still, making wool equal to dyes
		saveData(Items.WHITE_WOOL, krea);
		saveData(Items.RED_WOOL, krea);
		saveData(Items.ORANGE_WOOL, krea);
		saveData(Items.YELLOW_WOOL, krea);
		saveData(Items.GREEN_WOOL, krea);
		saveData(Items.BLUE_WOOL, krea);
		saveData(Items.PURPLE_WOOL, krea);
		saveData(Items.PINK_WOOL, krea);
		saveData(Items.LIME_WOOL, krea);
		saveData(Items.CYAN_WOOL, krea);
		saveData(Items.GRAY_WOOL, krea);
		saveData(Items.LIGHT_GRAY_WOOL, krea);
		saveData(Items.BLACK_WOOL, krea);
		saveData(Items.BROWN_WOOL, krea);
		saveData(Items.LIGHT_BLUE_WOOL, krea);
		saveData(Items.MAGENTA_WOOL, krea);
	}

	private void saveArcheologyStuff()
	{
		BuiltInRegistries.ITEM.forEach(this::protoPotteryLol);
	}
	private void protoPotteryLol(Item item)
	{
		// sadly there's no super type but at least every single one is named "pottery_sherd", so we use that.
		var key = BuiltInRegistries.ITEM.getKey(item);
		if (key != null && key.getPath().contains("pottery_sherd"))
		{
			saveData(item, proto(half), denied(half));
		}
	}

	private void saveAbjectDenials()
	{
		// controversial
		saveData(Items.NETHER_STAR, photo(vanillaStackLimit), anima(vanillaStackLimit), proto(usual), denied(half));
		// silly water. water is infinite, this is a joke.
		saveData(Items.HEART_OF_THE_SEA, hydro(vanillaStackLimit), proto(usual), denied(half));
	}

	private void saveSpawnEggs()
	{
		// it's SO annoying looping over spawn eggs, just use the registry!
		BuiltInRegistries.ITEM.forEach(this::denyEggItems);
	}

	private void denyEggItems(Item item)
	{
		if (item instanceof SpawnEggItem)
		{
			saveData(item, proto(spawnEggProto), denied(half));
		}
	}

	private void saveMycoLogs(CompoundInstance myco, CompoundInstance xylo)
	{
		saveData(Items.STRIPPED_CRIMSON_STEM, myco, xylo);
		saveData(Items.STRIPPED_WARPED_STEM, myco, xylo);
		saveData(Items.CRIMSON_STEM, myco, xylo);
		saveData(Items.WARPED_STEM, myco, xylo);
	}

	private void saveMiscTerrestrialStuff()
	{
		saveData(Items.SAND, gemma(glass));
		saveData(Items.GLASS, gemma(glass));
		saveData(Items.GLASS_BOTTLE, gemma(glass));
		saveData(Items.CHARCOAL, pyro(coalValue));
		saveData(Items.BRICK, geo(terrainGeo), denied(usual));
		saveData(Items.POISONOUS_POTATO, chloro(usual), myco(usual), denied(half));
		saveData(Items.CLAY_BALL, geo(usual), hydro(half));
		saveData(Items.FEATHER, krea(usual));
		saveData(Items.BROWN_MUSHROOM_BLOCK, myco(usual));
		saveData(Items.RED_MUSHROOM_BLOCK, myco(usual));
		saveData(Items.COBWEB, krea(half));
		saveData(Items.PUMPKIN, chloro(usual * 5));
		saveData(Items.PUMPKIN_SEEDS, chloro(usual));
		saveData(Items.SMALL_DRIPLEAF, chloro(usual), vivo(usual));
		saveData(Items.BIG_DRIPLEAF, chloro(usual), vivo(usual));
		saveData(Items.CARVED_PUMPKIN, chloro(usual));
		saveData(Items.BELL, oricho(vanillaStackLimit));
		saveData(Items.GUNPOWDER, pyro(third), vivo(usual));
		saveData(Items.HONEYCOMB, krea(usual), vivo(usual));
		saveData(Items.BEE_NEST, krea(usual * nineCraftingSlots), vivo(usual * nineCraftingSlots));
		saveData(Items.HONEY_BLOCK, krea(usual * quadrupled), vivo(half));
		saveData(Items.ICE, cryo(usual));
		saveData(Items.MAGMA_BLOCK, pyro(usual), geo(terrainGeo));
		saveData(Items.OBSIDIAN, geo(terrainGeo), gemma(terrainGeo));
		saveData(Items.PHANTOM_MEMBRANE, aero(vanillaStackLimit), anima(vanillaStackLimit));
		saveData(Items.RABBIT_FOOT, krea(usual));
		saveData(Items.ROTTEN_FLESH, proto(half), krea(usual));
		saveData(Items.SLIME_BALL, vivo(usual));
		saveData(Items.SNOWBALL, cryo(half));
		saveData(Items.SPIDER_EYE, krea(usual), vivo(half));
		saveData(Items.SCULK_VEIN, anima(half), myco(half));
		saveData(Items.SCULK, anima(usual), myco(usual));
		saveData(Items.SCULK_CATALYST, anima(usual), myco(usual), proto(usual * quadrupled));
		saveData(Items.SCULK_SHRIEKER, anima(usual), myco(usual), vivo(usual * quadrupled));
		saveData(Items.SCULK_SENSOR, anima(usual), myco(usual), electro(usual * quadrupled));
		saveData(Items.GLOW_BERRIES, photo(usual), chloro(usual));
		saveData(Items.GLOW_LICHEN, photo(half), chloro(half));
		saveData(Items.GOAT_HORN, krea(usual));
		saveData(Items.HANGING_ROOTS, xylo(half));
		saveData(Items.MANGROVE_ROOTS, xylo(half));
		saveData(Items.MOSS_BLOCK, xylo(half));
		saveData(Items.OCHRE_FROGLIGHT, vivo(usual), photo(usual * quadrupled));
		saveData(Items.PEARLESCENT_FROGLIGHT, vivo(usual), photo(usual * quadrupled));
		saveData(Items.VERDANT_FROGLIGHT, vivo(usual), photo(usual * quadrupled));
		saveData(Items.POINTED_DRIPSTONE, geo(usual), proto(quadrupled));
		saveData(Items.SNIFFER_EGG, proto(usual), vivo(usual), krea(usual * quadrupled));
		saveData(Items.TURTLE_EGG, proto(usual), vivo(usual), krea(usual * quadrupled));

	}

	private void saveEnderStuff()
	{
		saveData(Items.CHORUS_FLOWER, chloro(usual), aero(usual));
		saveData(Items.CHORUS_FRUIT, chloro(usual), aero(usual));
		saveData(Items.POPPED_CHORUS_FRUIT, chloro(usual), aero(usual), pyro(half));
		saveData(Items.END_STONE, geo(terrainGeo), aero(half * slabMult));
		saveData(Items.ENDER_PEARL, aero(usual));
		saveData(Items.SHULKER_SHELL, aero(vanillaStackLimit), anima(usual));
		saveData(Items.DRAGON_BREATH, proto(eighth), vivo(usual), aero(usual), gemma(terrainGeo));
		saveData(Items.DRAGON_EGG, proto(usual * quadrupled), aero(usual * quadrupled));
		saveData(Items.ECHO_SHARD, aero(usual), anima(usual));
		saveData(Items.ELYTRA, aero(usual * nineCraftingSlots), anima(usual), proto(usual), denied(usual));
	}

	private void saveNetherStuff()
	{
		saveData(Items.NETHER_WART, myco(usual), anima(half));
		saveData(Items.WARPED_WART_BLOCK, myco(usual), aero(half));
		saveData(Items.NETHERRACK, geo(terrainGeo), pyro(half));
		saveData(Items.CRIMSON_NYLIUM, myco(usual), anima(half));
		saveData(Items.WARPED_NYLIUM, myco(usual), aero(half));
		saveData(Items.CRYING_OBSIDIAN, geo(terrainGeo), gemma(terrainGeo), anima(terrainGeo));
		saveData(Items.GHAST_TEAR, anima(vanillaStackLimit), gemma(vanillaStackLimit));
		saveData(Items.GLOWSTONE_DUST, photo(half));
		saveData(Items.SHROOMLIGHT, photo(vanillaStackLimit), myco(logXylo));
		saveData(Items.SOUL_SAND, gemma(terrainGeo), anima(usual));
		saveData(Items.SOUL_SOIL, geo(terrainGeo), anima(usual));
		saveData(Items.BASALT, geo(terrainGeo));
		saveData(Items.BLAZE_ROD, pyro(blazeRodValue));
	}

	private void saveRocksAndDirt()
	{
		saveData(Items.BLACKSTONE, geo(terrainGeo));
		saveData(Items.COBBLESTONE, geo(terrainGeo));
		saveData(Items.COBBLED_DEEPSLATE, geo(terrainGeo));
		saveData(Items.DIRT, geo(terrainGeo));
		saveData(Items.MUD, geo(terrainGeo), hydro(usual));
		saveData(Items.FLINT, geo(terrainGeo));
		saveData(Items.GRAVEL, geo(terrainGeo));
		saveData(Items.RED_SAND, geo(terrainGeo));
		saveData(Items.ROOTED_DIRT, geo(terrainGeo), xylo(half));

		saveData(Items.MOSSY_COBBLESTONE, geo(terrainGeo), chloro(usual));
		saveData(Items.PODZOL, geo(terrainGeo), myco(usual));
		saveData(Items.GRASS_BLOCK, geo(terrainGeo), chloro(usual));
		saveData(Items.MYCELIUM, geo(terrainGeo), myco(usual));
	}

	private void saveAquaticStuffAndFish()
	{
		// coral, there's a lot of it
		saveCoral(chloro(half), hydro(half));
		saveCoralBlocks(chloro(half * 4), hydro(half * 4));

		// other aquatic stuff
		saveData(Items.PRISMARINE_CRYSTALS, hydro(half), gemma(quarter), photo(quarter));
		saveData(Items.PRISMARINE_SHARD, hydro(half), gemma(quarter), photo(quarter));
		saveData(Items.SCUTE, krea(usual), hydro(half), proto(half));
		saveData(Items.SEA_PICKLE, hydro(usual), chloro(usual), photo(usual));
		saveData(Items.SEAGRASS, chloro(usual), hydro(usual));
		saveData(Items.TURTLE_EGG, krea(usual), hydro(usual));
		saveData(Items.INK_SAC, krea(usual), hydro(usual));
		saveData(Items.GLOW_INK_SAC, krea(usual), photo(usual), hydro(usual));
		saveData(Items.WET_SPONGE, hydro(usual), aero(usual), krea(usual));
		saveData(Items.NAUTILUS_SHELL, hydro(usual), proto(usual), krea(usual));
		saveMostFish(krea(usual), hydro(usual));
	}

	private void saveMostFish(CompoundInstance krea, CompoundInstance hydro)
	{
		saveData(Items.SALMON, krea, hydro);
		saveData(Items.COD, krea, hydro);
		saveData(Items.PUFFERFISH, krea, hydro);
		saveData(Items.TROPICAL_FISH, krea, hydro);
	}

	private void saveKreaAnimalProducts(CompoundInstance krea)
	{
		saveData(Items.BEEF, krea);
		saveData(Items.EGG, krea);
		saveData(Items.CHICKEN, krea);
		saveData(Items.MUTTON, krea);
		saveData(Items.RABBIT, krea);
		saveData(Items.PORKCHOP, krea);
		saveData(Items.RABBIT_HIDE, krea);
		saveData(Items.BONE, krea);
	}

	private void saveMycoStuff(CompoundInstance myco)
	{
		saveData(Items.CRIMSON_FUNGUS, myco);
		saveData(Items.WARPED_FUNGUS, myco);
		saveData(Items.RED_MUSHROOM, myco);
		saveData(Items.BROWN_MUSHROOM, myco);

		saveData(Items.TWISTING_VINES, myco);
		saveData(Items.WEEPING_VINES, myco);
		saveData(Items.MUSHROOM_STEM, myco);
	}

	private void saveChloroFoods(CompoundInstance chloro)
	{
		saveData(Items.APPLE, chloro);
		saveData(Items.BEETROOT_SEEDS, chloro);
		saveData(Items.CARROT, chloro);
		saveData(Items.MELON_SLICE, chloro);
		saveData(Items.SUGAR_CANE, chloro);
		saveData(Items.WHEAT, chloro);
		saveData(Items.WHEAT_SEEDS, chloro);
		saveData(Items.POTATO, chloro);

	}

	private void saveTallPlants(CompoundInstance chloro)
	{

		saveData(Items.LARGE_FERN, chloro);
		saveData(Items.TALL_GRASS, chloro);
	}

	private void saveCoral(CompoundInstance krea, CompoundInstance hydro)
	{
		saveData(Items.BRAIN_CORAL, krea, hydro);
		saveData(Items.BUBBLE_CORAL, krea, hydro);
		saveData(Items.DEAD_BRAIN_CORAL, krea, hydro);
		saveData(Items.DEAD_BUBBLE_CORAL, krea, hydro);
		saveData(Items.DEAD_FIRE_CORAL, krea, hydro);
		saveData(Items.DEAD_HORN_CORAL, krea, hydro);
		saveData(Items.DEAD_TUBE_CORAL, krea, hydro);
		saveData(Items.FIRE_CORAL, krea, hydro);
		saveData(Items.HORN_CORAL, krea, hydro);
		saveData(Items.TUBE_CORAL, krea, hydro);
		saveData(Items.BRAIN_CORAL_FAN, krea, hydro);
		saveData(Items.BUBBLE_CORAL_FAN, krea, hydro);
		saveData(Items.DEAD_BRAIN_CORAL_FAN, krea, hydro);
		saveData(Items.DEAD_BUBBLE_CORAL_FAN, krea, hydro);
		saveData(Items.DEAD_FIRE_CORAL_FAN, krea, hydro);
		saveData(Items.DEAD_HORN_CORAL_FAN, krea, hydro);
		saveData(Items.DEAD_TUBE_CORAL_FAN, krea, hydro);
		saveData(Items.FIRE_CORAL_FAN, krea, hydro);
		saveData(Items.HORN_CORAL_FAN, krea, hydro);
		saveData(Items.TUBE_CORAL_FAN, krea, hydro);
	}

	private void saveCoralBlocks(CompoundInstance krea, CompoundInstance hydra)
	{
		saveData(Items.BRAIN_CORAL_BLOCK, krea, hydra);
		saveData(Items.BUBBLE_CORAL_BLOCK, krea, hydra);
		saveData(Items.DEAD_BRAIN_CORAL_BLOCK, krea, hydra);
		saveData(Items.DEAD_BUBBLE_CORAL_BLOCK, krea, hydra);
		saveData(Items.DEAD_FIRE_CORAL_BLOCK, krea, hydra);
		saveData(Items.DEAD_HORN_CORAL_BLOCK, krea, hydra);
		saveData(Items.DEAD_TUBE_CORAL_BLOCK, krea, hydra);
		saveData(Items.FIRE_CORAL_BLOCK, krea, hydra);
		saveData(Items.HORN_CORAL_BLOCK, krea, hydra);
		saveData(Items.TUBE_CORAL_BLOCK, krea, hydra);
	}

	private void saveDoubleFlowers(CompoundInstance chloro)
	{
		saveData(Items.LILAC, chloro);
		saveData(Items.PEONY, chloro);
		saveData(Items.ROSE_BUSH, chloro);
		saveData(Items.SUNFLOWER, chloro);
	}

	private void saveDyesAndFlowers(CompoundInstance chloro)
	{

		saveData(Items.ALLIUM, chloro);
		saveData(Items.AZALEA, chloro);
		saveData(Items.AZURE_BLUET, chloro);
		saveData(Items.BLUE_ORCHID, chloro);
		saveData(Items.COCOA_BEANS, chloro);
		saveData(Items.CORNFLOWER, chloro);
		saveData(Items.DANDELION, chloro);
		saveData(Items.FLOWERING_AZALEA, chloro);
		saveData(Items.LILY_OF_THE_VALLEY, chloro);
		saveData(Items.ORANGE_TULIP, chloro);
		saveData(Items.OXEYE_DAISY, chloro);
		saveData(Items.PINK_TULIP, chloro);
		saveData(Items.PINK_PETALS, chloro);
		saveData(Items.POPPY, chloro);
		saveData(Items.RED_TULIP, chloro);
		saveData(Items.TORCHFLOWER, chloro);
		saveData(Items.WHITE_TULIP, chloro);
		saveData(Items.BLACK_DYE, chloro);
		saveData(Items.BLUE_DYE, chloro);
		saveData(Items.BROWN_DYE, chloro);
		saveData(Items.CYAN_DYE, chloro);
		saveData(Items.GRAY_DYE, chloro);
		saveData(Items.GREEN_DYE, chloro);
		saveData(Items.LIGHT_BLUE_DYE, chloro);
		saveData(Items.LIGHT_GRAY_DYE, chloro);
		saveData(Items.LIME_DYE, chloro);
		saveData(Items.MAGENTA_DYE, chloro);
		saveData(Items.ORANGE_DYE, chloro);
		saveData(Items.PINK_DYE, chloro);
		saveData(Items.PURPLE_DYE, chloro);
		saveData(Items.RED_DYE, chloro);
		saveData(Items.WHITE_DYE, chloro);
		saveData(Items.YELLOW_DYE, chloro);

		// weird ones, not flowers, but still dyes in value.
		saveData(Items.BEETROOT, chloro);
		saveData(Items.CACTUS, chloro);
	}

	private void savePlantsAndStuff()
	{
		saveLeavesAndSmallPlants(chloro(usual));
		saveTallPlants(chloro(usual * 2));
		saveLogs(xylo(logXylo));
		saveSaplings(chloro(logXylo)); // saplings are rich chloro
		saveDyesAndFlowers(chloro(usual));
		saveDoubleFlowers(chloro(usual * 2));
		saveData(Items.BAMBOO, xylo(bambooXylo));
		saveData(Items.STRIPPED_BAMBOO_BLOCK, xylo(bambooXylo * nineCraftingSlots));
		saveData(Items.STRING, krea(half));
		saveData(Items.PITCHER_POD, chloro(usual), vivo(half));
		saveData(Items.PITCHER_PLANT, chloro(usual), vivo(half));
		saveData(Items.TORCHFLOWER_SEEDS, chloro(usual), pyro(half));
		saveData(Items.SPORE_BLOSSOM, chloro(usual), myco(usual));
		saveData(Items.WITHER_ROSE, chloro(usual), anima(usual), proto(quarter));
	}

	private void saveSaplings(CompoundInstance chloro)
	{
		saveData(Items.ACACIA_SAPLING, chloro);
		saveData(Items.BIRCH_SAPLING, chloro);
		saveData(Items.CHERRY_SAPLING, chloro);
		saveData(Items.DARK_OAK_SAPLING, chloro);
		saveData(Items.JUNGLE_SAPLING, chloro);
		saveData(Items.MANGROVE_PROPAGULE, chloro);
		saveData(Items.OAK_SAPLING, chloro);
		saveData(Items.SPRUCE_SAPLING, chloro);
	}

	private void saveLogs(CompoundInstance xylo)
	{
		saveData(Items.ACACIA_LOG, xylo);
		saveData(Items.BIRCH_LOG, xylo);
		saveData(Items.CHERRY_LOG, xylo);
		saveData(Items.DARK_OAK_LOG, xylo);
		saveData(Items.JUNGLE_LOG, xylo);
		saveData(Items.MANGROVE_LOG, xylo);
		saveData(Items.OAK_LOG, xylo);
		saveData(Items.SPRUCE_LOG, xylo);

		saveData(Items.STRIPPED_ACACIA_LOG, xylo);
		saveData(Items.STRIPPED_BIRCH_LOG, xylo);
		saveData(Items.STRIPPED_CHERRY_LOG, xylo);
		saveData(Items.STRIPPED_DARK_OAK_LOG, xylo);
		saveData(Items.STRIPPED_JUNGLE_LOG, xylo);
		saveData(Items.STRIPPED_MANGROVE_LOG, xylo);
		saveData(Items.STRIPPED_OAK_LOG, xylo);
		saveData(Items.STRIPPED_SPRUCE_LOG, xylo);
	}

	private void saveLeavesAndSmallPlants(CompoundInstance chloro)
	{
		saveData(Items.ACACIA_LEAVES, chloro);
		saveData(Items.AZALEA_LEAVES, chloro);
		saveData(Items.FLOWERING_AZALEA_LEAVES, chloro);
		saveData(Items.BIRCH_LEAVES, chloro);
		saveData(Items.CHERRY_LEAVES, chloro);
		saveData(Items.DARK_OAK_LEAVES, chloro);
		saveData(Items.JUNGLE_LEAVES, chloro);
		saveData(Items.MANGROVE_LEAVES, chloro);
		saveData(Items.OAK_LEAVES, chloro);
		saveData(Items.SPRUCE_LEAVES, chloro);

		saveData(Items.DEAD_BUSH, chloro);
		saveData(Items.FERN, chloro);
		saveData(Items.SHORT_GRASS, chloro);
		saveData(Items.CRIMSON_ROOTS, chloro);
		saveData(Items.NETHER_SPROUTS, chloro);
		saveData(Items.WARPED_ROOTS, chloro);

		saveData(Items.KELP, chloro);
		saveData(Items.LILY_PAD, chloro);

		saveData(Items.SWEET_BERRIES, chloro);
		saveData(Items.VINE, chloro);
	}

	private void saveData(Item item, CompoundInstance... instances)
	{
		saveData(newLinkedHashSet(item, new ItemStack(item)), instances);
	}

	private LinkedHashSet<Object> newLinkedHashSet(final Object... internal)
	{
		return new LinkedHashSet<>(Arrays.asList(internal));
	}

	private void saveData(LinkedHashSet<Object> items, CompoundInstance... instances)
	{
		save(specFor(items).withCompounds(instances));
	}

	private static CompoundInstance cryo(double d)
	{
		return new CompoundInstance(MateriaRegistry.CRYO.get(), d);
	}

	private static CompoundInstance pyro(double d)
	{
		return new CompoundInstance(MateriaRegistry.PYRO.get(), d);
	}

	private static CompoundInstance myco(double d)
	{
		return new CompoundInstance(MateriaRegistry.MYCO.get(), d);
	}

	private static CompoundInstance gemma(double d)
	{
		return new CompoundInstance(MateriaRegistry.GEMMA.get(), d);
	}

	private static CompoundInstance photo(double d)
	{
		return new CompoundInstance(MateriaRegistry.PHOTO.get(), d);
	}

	private static CompoundInstance vivo(double d)
	{
		return new CompoundInstance(MateriaRegistry.VIVO.get(), d);
	}

	private static CompoundInstance krea(double d)
	{
		return new CompoundInstance(MateriaRegistry.KREA.get(), d);
	}

	private static CompoundInstance geo(double d)
	{
		return new CompoundInstance(MateriaRegistry.GEO.get(), d);
	}

	private static CompoundInstance oricho(double d)
	{
		return new CompoundInstance(MateriaRegistry.ORICHO.get(), d);
	}

	private static CompoundInstance proto(double d)
	{
		return new CompoundInstance(MateriaRegistry.PROTO.get(), d);
	}

	private static CompoundInstance aero(double d)
	{
		return new CompoundInstance(MateriaRegistry.AERO.get(), d);
	}

	private static CompoundInstance hydro(double d)
	{
		return new CompoundInstance(MateriaRegistry.HYDRO.get(), d);
	}

	private static CompoundInstance xylo(double d)
	{
		return new CompoundInstance(MateriaRegistry.XYLO.get(), d);
	}

	private static CompoundInstance chloro(double d)
	{
		return new CompoundInstance(MateriaRegistry.CHLORO.get(), d);
	}

	private static CompoundInstance electro(double d)
	{
		return new CompoundInstance(MateriaRegistry.ELECTRO.get(), d);
	}

	private static CompoundInstance anima(double d)
	{
		return new CompoundInstance(MateriaRegistry.ANIMA.get(), d);
	}

	private static CompoundInstance denied(double d)
	{
		return new CompoundInstance(MateriaRegistry.DENIED.get(), d);
	}
}