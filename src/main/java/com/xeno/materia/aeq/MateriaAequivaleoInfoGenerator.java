package com.xeno.materia.aeq;

import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.ldtteam.aequivaleo.api.compound.information.datagen.ForcedInformationProvider;
import com.xeno.materia.MateriaMod;
import com.xeno.materia.common.MateriaRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MateriaAequivaleoInfoGenerator extends ForcedInformationProvider
{
	// these are junky expressions for changing the values of lots of things at once by using common values.
	// but there is some variation where it feels warranted. Mostly these are just placeholder values as I've no clue what to anticipate balance-wise? So I'm just wingin' it.

	// It may bore you to know most things are just worth 4. So many things are just 4, it's not very thoughtful at first blush. This is just a way to get something in place.
	private final int usual = 4;
	// this is an int in case I need to rapidly scale things, trust me, it might seem dumb but bear with me. If I wanted to flip these all to 2 on a whim, I could.
	private final int unit = 1;
	// this is the perfunctory doubling required by blocks of things that have slabs, as seen in terrainGeo
	private final int slabMult = 2;
	// it's really common for there to just be recipes where 4x something = something else, so quad mults common.
	private final int quadMult = 4;

	// the justification for buckets being 3 hydro. it coulda been a lot of numbers, but 3 seems reasonable and sane.
	private final int cauldronDoses = 3;

	// the reason this is doubled is to make slabs be the baseline "4" and because 8 winds up being a bottom line for glass, weirdly. 9 is a bottom line for ingots.
	private final int terrainGeo = usual * slabMult;

	// these are just to make math with sticks, logs and planks basic.
	// the reason sticks have to be 12 is a little convoluted. If it's 3, something winds up with .75 on it, which doubling wouldn't help
	// so quadrupling is the next step.
	private final int stickXylo = 6;
	private final int plankSticks = 2;
	private final int plankXylo = stickXylo * plankSticks;
	private final int logPlanks = 4;
	private final int logXylo = plankXylo * logPlanks;

	private final int ingotNuggets = 9; // "metal" is broadly 9 because ingots decomposing to nuggets is more divisible at 9
	private final int vanillaStackLimit = 64; // this my way of saying throw a 64 in there because a number needs to be big for some reason. Usually multiplying it by something else.
	private final int diamondflation = vanillaStackLimit * terrainGeo; // somewhat arbitrary inflation here, both of these are test values.
	private final int netheriteflation = diamondflation * ingotNuggets; // "both" meaning this one too.

	public MateriaAequivaleoInfoGenerator(final DataGenerator dataGenerator)
	{
		super(MateriaMod.ID, dataGenerator);
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
		saveKreaFoods(krea(usual));
		saveMycoStuff(myco(usual));
		saveMycoLogs(myco(logXylo), xylo(logXylo)); // myco logs are sweet, they have both. This fixes an issue where myco logs compete.
		// quasi-food
		saveData(Items.MILK_BUCKET, krea(unit), hydro(unit));

		// ores, metal ingots, gems
		saveOresAndMinerals();

		// random terrestrial nonsense
		saveMiscTerrestrialStuff();

		// nether
		saveNetherStuff();

		// ender
		saveEnderStuff();

		// containers
		// basing this on cauldron doses felt pretty arbitrary but hydro is plentiful (aka infinite, you can just stock water forever)
		saveData(Items.WATER_BUCKET, hydro(usual * cauldronDoses));
		saveData(Items.LAVA_BUCKET, pyro(usual * cauldronDoses), geo(terrainGeo));

		// controversial
		saveData(Items.NETHER_STAR, photo(vanillaStackLimit), phasmo(vanillaStackLimit), proto(usual), denied(unit));
		// silly water. water is infinite, this is a joke.
		saveData(Items.HEART_OF_THE_SEA, hydro(vanillaStackLimit), proto(usual), denied(unit));
	}

	private void saveMycoLogs(CompoundInstance myco, CompoundInstance xylo)
	{
		saveData(Items.STRIPPED_CRIMSON_STEM, myco, xylo);
		saveData(Items.STRIPPED_WARPED_STEM, myco, xylo);
		saveData(Items.CRIMSON_STEM, myco, xylo);
		saveData(Items.WARPED_STEM, myco, xylo);
	}

	private void saveMiscTerrestrialStuff() {
		saveData(Items.SAND, gemma(terrainGeo));
		saveData(Items.GLASS, gemma(terrainGeo));
		saveData(Items.GLASS_BOTTLE, gemma(terrainGeo));
		saveData(Items.CHARCOAL, pyro(usual));
		saveData(Items.BRICK, geo(terrainGeo), pyro(unit));
		saveData(Items.POISONOUS_POTATO, chloro(usual), myco(usual), denied(unit));
		saveData(Items.CLAY_BALL, geo(usual), hydro(unit));
		saveData(Items.FEATHER, krea(usual));
		saveData(Items.BROWN_MUSHROOM_BLOCK, myco(usual));
		saveData(Items.RED_MUSHROOM_BLOCK, myco(usual));
		saveData(Items.COBWEB, krea(unit));
		saveData(Items.PUMPKIN, chloro(usual * 5));
		saveData(Items.PUMPKIN_SEEDS, chloro(usual));
		saveData(Items.CARVED_PUMPKIN, chloro(usual));
		saveData(Items.BELL, oricho(vanillaStackLimit));
		saveData(Items.GUNPOWDER, pyro(usual), dyna(usual));
		saveData(Items.HONEYCOMB, krea(usual), dyna(unit));
		saveData(Items.HONEY_BLOCK, krea(usual * quadMult), dyna(unit));
		saveData(Items.ICE, cryo(usual));
		saveData(Items.MAGMA_BLOCK, pyro(usual), geo(terrainGeo));
		saveData(Items.OBSIDIAN, geo(terrainGeo), gemma(terrainGeo));
		saveData(Items.PHANTOM_MEMBRANE, aero(vanillaStackLimit), phasmo(vanillaStackLimit));
		saveData(Items.RABBIT_FOOT, krea(usual), denied(unit));
		saveData(Items.ROTTEN_FLESH, proto(unit), krea(usual));
		saveData(Items.SLIME_BALL, dyna(usual));
		saveData(Items.SNOWBALL, cryo(unit));
		saveData(Items.SPIDER_EYE, krea(usual), dyna(unit));
	}

	private void saveEnderStuff() {
		saveData(Items.CHORUS_FLOWER, chloro(usual), aero(usual));
		saveData(Items.CHORUS_FRUIT, chloro(usual), aero(usual));
		saveData(Items.POPPED_CHORUS_FRUIT, chloro(usual), aero(usual), pyro(unit));
		saveData(Items.END_STONE, geo(terrainGeo), aero(unit * slabMult));
		saveData(Items.ENDER_PEARL, aero(usual));
		saveData(Items.SHULKER_SHELL, aero(vanillaStackLimit), phasmo(usual));
	}

	private void saveNetherStuff() {
		saveData(Items.NETHER_WART, myco(usual), phasmo(unit));
		saveData(Items.WARPED_WART_BLOCK, myco(usual), aero(unit));
		saveData(Items.NETHERRACK, geo(terrainGeo), pyro(unit));
		saveData(Items.CRIMSON_NYLIUM, myco(usual), phasmo(unit));
		saveData(Items.WARPED_NYLIUM, myco(usual), aero(unit));
		saveData(Items.CRYING_OBSIDIAN, geo(terrainGeo), gemma(terrainGeo), phasmo(terrainGeo));
		saveData(Items.GHAST_TEAR, phasmo(vanillaStackLimit), gemma(vanillaStackLimit));
		saveData(Items.GLOWSTONE_DUST, photo(unit));
		saveData(Items.SHROOMLIGHT, photo(vanillaStackLimit), myco(logXylo));
		saveData(Items.SOUL_SAND, gemma(terrainGeo), phasmo(usual));
		saveData(Items.SOUL_SOIL, geo(terrainGeo), phasmo(usual));
		saveData(Items.BASALT, geo(terrainGeo));
		saveData(Items.BLAZE_ROD, pyro(unit * 12));
	}

	private void saveOresAndMinerals() {
		// ore blocks
		saveData(Items.COAL_ORE, pyro(usual), geo(terrainGeo), denied(unit));
		saveData(Items.COAL, pyro(usual));
		saveData(Items.DIAMOND_ORE, gemma(vanillaStackLimit), proto(usual), geo(terrainGeo), denied(unit));
		saveData(Items.DIAMOND, gemma(vanillaStackLimit), proto(usual));
		saveData(Items.EMERALD_ORE, gemma(vanillaStackLimit * usual), geo(terrainGeo), denied(unit));
		saveData(Items.EMERALD, gemma(vanillaStackLimit * usual));
		saveData(Items.NETHER_GOLD_ORE, oricho(terrainGeo * ingotNuggets * 9), geo(terrainGeo), denied(unit));
		saveData(Items.GILDED_BLACKSTONE, oricho(terrainGeo * ingotNuggets * 9), geo(terrainGeo), denied(unit));
		saveData(Items.GOLD_ORE, oricho(terrainGeo * ingotNuggets * 6), geo(terrainGeo), denied(unit));
		saveData(Items.RAW_GOLD, oricho(terrainGeo * ingotNuggets * 6));
		saveData(Items.GOLD_INGOT, oricho(terrainGeo * ingotNuggets * 6), denied(unit));
		saveData(Items.IRON_ORE, oricho(terrainGeo * ingotNuggets * 2), geo(terrainGeo), denied(unit));
		saveData(Items.RAW_IRON, oricho(terrainGeo * ingotNuggets * 2));
		saveData(Items.IRON_INGOT, oricho(terrainGeo * ingotNuggets * 2), denied(unit));
		saveData(Items.COPPER_ORE, oricho(terrainGeo * ingotNuggets), electro(ingotNuggets), geo(terrainGeo), denied(unit));
		saveData(Items.RAW_COPPER, oricho(terrainGeo * ingotNuggets), electro(ingotNuggets));
		saveData(Items.COPPER_INGOT, oricho(terrainGeo * ingotNuggets), electro(ingotNuggets), denied(unit));
		saveData(Items.REDSTONE_ORE, electro(usual), geo(terrainGeo), denied(unit));
		saveData(Items.REDSTONE, electro(usual));
		saveData(Items.LAPIS_ORE, phasmo(usual), geo(terrainGeo), gemma(usual), denied(unit));
		saveData(Items.LAPIS_LAZULI, phasmo(usual), gemma(usual));
		saveData(Items.NETHER_QUARTZ_ORE, geo(terrainGeo), gemma(terrainGeo), denied(unit));
		saveData(Items.QUARTZ,gemma(terrainGeo));
		saveData(Items.ANCIENT_DEBRIS, oricho(diamondflation), proto(usual * 2), denied(unit));
		saveData(Items.NETHERITE_SCRAP, oricho(diamondflation), proto(usual * 2));
	}

	private void saveRocksAndDirt() {
		saveGeoBlocks(geo(terrainGeo));

		saveData(Items.MOSSY_COBBLESTONE, geo(terrainGeo), chloro(usual));
		saveData(Items.PODZOL, geo(terrainGeo), myco(usual));
		saveData(Items.GRASS_BLOCK, geo(terrainGeo), chloro(usual));
		saveData(Items.MYCELIUM, geo(terrainGeo), myco(usual));
	}

	private void saveAquaticStuffAndFish() {
		// coral, there's a lot of it
		saveCoral(chloro(unit), hydro(unit));
		saveCoralBlocks(chloro(unit * 4), hydro(unit * 4));

		// other aquatic stuff
		saveData(Items.PRISMARINE_CRYSTALS, hydro(unit), gemma(3), photo(6));
		saveData(Items.PRISMARINE_SHARD, hydro(unit), gemma(2), photo(4));
		saveData(Items.SCUTE, krea(usual), hydro(unit), proto(unit));
		saveData(Items.SEA_PICKLE, hydro(usual), chloro(usual), photo(usual));
		saveData(Items.SEAGRASS, chloro(usual), hydro(usual));
		saveData(Items.TURTLE_EGG, krea(usual), hydro(usual));
		saveData(Items.INK_SAC, krea(usual), hydro(usual));
		saveData(Items.GLOW_INK_SAC, krea(usual), photo(usual), hydro(usual));
		saveData(Items.WET_SPONGE, hydro(usual), aero(usual), krea(usual));
		saveData(Items.NAUTILUS_SHELL, hydro(usual), proto(3), krea(usual));
		saveMostFish(krea(usual), hydro(usual));
	}

	private void savePlantsAndStuff() {
		saveLeavesAndSmallPlants(chloro(usual));
		saveTallPlants(chloro(usual * 2));
		saveLogsAndSaplings(xylo(logXylo));
		saveFlowersAndDye(chloro(usual));
		saveDoubleFlowers(chloro(usual * 2));
		saveData(Items.BAMBOO, xylo(stickXylo / 2));
		saveData(Items.BONE, krea(usual * 3));
		saveData(Items.STRING, krea(unit));
	}

	private void saveMostFish(CompoundInstance krea, CompoundInstance hydro) {
		saveData(Items.SALMON, krea, hydro);
		saveData(Items.COD, krea, hydro);
		saveData(Items.PUFFERFISH, krea, hydro);
		saveData(Items.TROPICAL_FISH, krea, hydro);
	}

	private void saveKreaFoods(CompoundInstance krea) {
		saveData(Items.BEEF, krea);
		saveData(Items.EGG, krea);
		saveData(Items.CHICKEN, krea);
		saveData(Items.MUTTON, krea);
		saveData(Items.RABBIT, krea);
		saveData(Items.PORKCHOP, krea);
		saveData(Items.RABBIT_HIDE, krea);

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

	private void saveMycoStuff(CompoundInstance myco) {
		saveData(Items.CRIMSON_FUNGUS, myco);
		saveData(Items.WARPED_FUNGUS, myco);
		saveData(Items.RED_MUSHROOM, myco);
		saveData(Items.BROWN_MUSHROOM, myco);

		saveData(Items.TWISTING_VINES, myco);
		saveData(Items.WEEPING_VINES, myco);
		saveData(Items.MUSHROOM_STEM, myco);
	}

	private void saveChloroFoods(CompoundInstance chloro) {
		saveData(Items.APPLE, chloro);
		saveData(Items.BEETROOT_SEEDS, chloro);
		saveData(Items.CARROT, chloro);
		saveData(Items.MELON_SLICE, chloro);
		saveData(Items.SUGAR_CANE, chloro);
		saveData(Items.WHEAT, chloro);
		saveData(Items.WHEAT_SEEDS, chloro);
		saveData(Items.POTATO, chloro);
	}

	private void saveGeoBlocks(CompoundInstance geo) {
		saveData(Items.BLACKSTONE, geo);
		saveData(Items.COBBLESTONE, geo);
		saveData(Items.DIRT, geo);
		saveData(Items.FLINT, geo);
		saveData(Items.GRAVEL, geo);
		saveData(Items.RED_SAND, geo);
	}

	private void saveTallPlants(CompoundInstance chloro) {

		saveData(Items.LARGE_FERN, chloro);
		saveData(Items.TALL_GRASS, chloro);
	}

	private void saveCoral(CompoundInstance krea, CompoundInstance hydro) {
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

	private void saveCoralBlocks(CompoundInstance krea, CompoundInstance hydra) {
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

	private void saveDoubleFlowers(CompoundInstance chloro) {
		saveData(Items.LILAC, chloro);
		saveData(Items.PEONY, chloro);
		saveData(Items.ROSE_BUSH, chloro);
		saveData(Items.SUNFLOWER, chloro);
	}

	private void saveFlowersAndDye(CompoundInstance chloro) {

		saveData(Items.ALLIUM, chloro);
		saveData(Items.AZURE_BLUET, chloro);
		saveData(Items.BLUE_ORCHID, chloro);
		saveData(Items.COCOA_BEANS, chloro);
		saveData(Items.CORNFLOWER, chloro);
		saveData(Items.DANDELION, chloro);
		saveData(Items.LILY_OF_THE_VALLEY, chloro);
		saveData(Items.ORANGE_TULIP, chloro);
		saveData(Items.OXEYE_DAISY, chloro);
		saveData(Items.PINK_TULIP, chloro);
		saveData(Items.POPPY, chloro);
		saveData(Items.RED_TULIP, chloro);
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

		// weird ones, not flowers, but still dyes
		saveData(Items.BEETROOT, chloro);
		saveData(Items.CACTUS, chloro);

		saveData(Items.ACACIA_SAPLING, chloro);
		saveData(Items.BIRCH_SAPLING, chloro);
		saveData(Items.DARK_OAK_SAPLING, chloro);
		saveData(Items.JUNGLE_SAPLING, chloro);
		saveData(Items.OAK_SAPLING, chloro);
		saveData(Items.SPRUCE_SAPLING, chloro);
	}

	private void saveLogsAndSaplings(CompoundInstance xylo) {
		saveData(Items.ACACIA_LOG, xylo);
		saveData(Items.BIRCH_LOG, xylo);
		saveData(Items.DARK_OAK_LOG, xylo);
		saveData(Items.JUNGLE_LOG, xylo);
		saveData(Items.OAK_LOG, xylo);
		saveData(Items.SPRUCE_LOG, xylo);

		saveData(Items.STRIPPED_ACACIA_LOG, xylo);
		saveData(Items.STRIPPED_BIRCH_LOG, xylo);
		saveData(Items.STRIPPED_DARK_OAK_LOG, xylo);
		saveData(Items.STRIPPED_JUNGLE_LOG, xylo);
		saveData(Items.STRIPPED_OAK_LOG, xylo);
		saveData(Items.STRIPPED_SPRUCE_LOG, xylo);
	}

	private void saveLeavesAndSmallPlants(CompoundInstance chloro) {
		saveData(Items.ACACIA_LEAVES, chloro);
		saveData(Items.BIRCH_LEAVES, chloro);
		saveData(Items.DARK_OAK_LEAVES, chloro);
		saveData(Items.JUNGLE_LEAVES, chloro);
		saveData(Items.OAK_LEAVES, chloro);
		saveData(Items.SPRUCE_LEAVES, chloro);

		saveData(Items.DEAD_BUSH, chloro);
		saveData(Items.FERN, chloro);
		saveData(Items.GRASS, chloro);
		saveData(Items.CRIMSON_ROOTS, chloro);
		saveData(Items.NETHER_SPROUTS, chloro);
		saveData(Items.WARPED_ROOTS, chloro);

		saveData(Items.KELP, chloro);
		saveData(Items.LILY_PAD, chloro);

		saveData(Items.SWEET_BERRIES, chloro);
		saveData(Items.VINE, chloro);
	}

	private void saveData(Item item, CompoundInstance... instances) {
		saveData(newLinkedHashSet(item, new ItemStack(item)), instances);
	}

	private LinkedHashSet<Object> newLinkedHashSet(final Object... internal) {
		return new LinkedHashSet<>(Arrays.asList(internal));
	}

	private void saveData(LinkedHashSet<Object> items, CompoundInstance... instances) {
		save(specFor(items).withCompounds(instances));
	}

	private static CompoundInstance cryo(double d) { return new CompoundInstance(MateriaRegistry.CRYO.get(), d); }
	private static CompoundInstance pyro(double d) { return new CompoundInstance(MateriaRegistry.PYRO.get(), d); }
	private static CompoundInstance myco(double d) { return new CompoundInstance(MateriaRegistry.MYCO.get(), d); }
	private static CompoundInstance gemma(double d) { return new CompoundInstance(MateriaRegistry.GEMMA.get(), d); }
	private static CompoundInstance photo(double d) { return new CompoundInstance(MateriaRegistry.PHOTO.get(), d); }
	private static CompoundInstance dyna(double d) { return new CompoundInstance(MateriaRegistry.DYNA.get(), d); }
	private static CompoundInstance krea(double d) { return new CompoundInstance(MateriaRegistry.KREA.get(), d); }
	private static CompoundInstance geo(double d) { return new CompoundInstance(MateriaRegistry.GEO.get(), d); }
	private static CompoundInstance oricho(double d) { return new CompoundInstance(MateriaRegistry.ORICHO.get(), d); }
	private static CompoundInstance proto(double d) { return new CompoundInstance(MateriaRegistry.PROTO.get(), d); }
	private static CompoundInstance aero(double d) { return new CompoundInstance(MateriaRegistry.AERO.get(), d); }
	private static CompoundInstance hydro(double d) { return new CompoundInstance(MateriaRegistry.HYDRO.get(), d); }
	private static CompoundInstance xylo(double d) { return new CompoundInstance(MateriaRegistry.XYLO.get(), d); }
	private static CompoundInstance chloro(double d) { return new CompoundInstance(MateriaRegistry.CHLORO.get(), d); }
	private static CompoundInstance electro(double d) { return new CompoundInstance(MateriaRegistry.ELECTRO.get(), d); }
	private static CompoundInstance phasmo(double d) { return new CompoundInstance(MateriaRegistry.PHASMO.get(), d); }

	private static CompoundInstance denied(double d) { return new CompoundInstance(MateriaRegistry.DENIED.get(), d); }
}