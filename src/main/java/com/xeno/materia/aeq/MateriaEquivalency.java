package com.xeno.materia.aeq;

import com.ldtteam.aequivaleo.api.IAequivaleoAPI;
import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.ldtteam.aequivaleo.api.compound.container.ICompoundContainer;
import com.ldtteam.aequivaleo.api.plugin.AequivaleoPlugin;
import com.ldtteam.aequivaleo.api.plugin.IAequivaleoPlugin;
import com.xeno.materia.MateriaMod;
import com.xeno.materia.common.MateriaRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@AequivaleoPlugin
public class MateriaEquivalency implements IAequivaleoPlugin
{
	public static final String ID = MateriaMod.location("materia_types").toString();
	private static final Set<CompoundInstance> EMPTY = new HashSet<>();
	private static double precisionCutoff = 1d / 128d;

	public static Set<CompoundInstance> getEquivalency(ItemStack stack)
	{
		return validEntryCache.getOrDefault(stack.getItem(), EMPTY);
	}

	// I'm doing this in registry init I think? so this is actually a throw lol
//	@Override
//	public void onConstruction()
//	{
//		ModList.get().getModContainerById(MateriaMod.ID).ifPresent(mod ->
//		{
//			if (mod.getEventBus() != null)
//			{
//				MateriaRegistry.TYPES.register(mod.getEventBus());
//				MateriaRegistry.TYPE_GROUPS.register(mod.getEventBus());
//			}
//		});
//	}

	@Override
	public String getId()
	{
		return ID;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onDataSynced(ResourceKey<Level> resourceKey)
	{
		// noop but probably should be doing something here to enable client side interactions
		// as well as some feedback that aeq is not loaded when the player tries doing something too early.
	}


	public static void doEquivalencyQualityCheck()
	{
		designateAllEquivalencyEntries();
		// log the results
		logEquivalencyIssues();

	}

	private static void logEquivalencyIssues()
	{
        BuiltInRegistries.ITEM.entrySet().forEach(MateriaEquivalency::checkEquivalencyValidity);
	}

	private static void checkEquivalencyValidity(Map.Entry<ResourceKey<Item>, Item> k)
	{
		if (invalidEntryCache.containsKey(k.getValue()))
		{
			MateriaMod.debug(String.format("Invalid entry found in %s", k.getValue().getName(k.getValue().getDefaultInstance())));
			var entry = invalidEntryCache.get(k.getValue());
			entry.forEach(ci -> MateriaMod.debug(String.format("Compound %s at %f - cutoff violated at %f", ci.asRef().type().toString(), ci.getAmount(),
					ci.getAmount() % 1d)));
		} else if (validEntryCache.containsKey(k.getValue()))
		{
			// noop, this is good, we want this
		} else
		{
			MateriaMod.debug(String.format("Compound entry not even attempted for %s, are we missing locked values?", k.getValue().getName(k.getValue().getDefaultInstance())));
		}
	}

	private static int entryCacheComparator(Item item, Item item1)
	{
		// I'm none too sure what description Id is. I just want the map sorted at rest for convenience/determinism
		return item.getDescriptionId().compareTo(item1.getDescriptionId());
	}

	static TreeMap<Item, Set<CompoundInstance>> validEntryCache =
			new TreeMap<>(MateriaEquivalency::entryCacheComparator);

	static TreeMap<Item, Set<CompoundInstance>> invalidEntryCache =
			new TreeMap<>(MateriaEquivalency::entryCacheComparator);


	private static void designateAllEquivalencyEntries()
	{
		// the reason I've hard coded this to the overworld resource key is that Materia doesn't have different values.
		// what I'm doing here is caching the all-entries the first time we hit this.
		getEquivalencyResultsFromApiInstance().forEach(MateriaEquivalency::placeEquivalency);

	}

	private static Map<ICompoundContainer<?>, Set<CompoundInstance>> getEquivalencyResultsFromApiInstance()
	{
		return IAequivaleoAPI.getInstance().getEquivalencyResults(Level.OVERWORLD)
				.getAllDataOf(MateriaRegistry.MATERIA_TYPE.get());
	}

	private static void placeEquivalency(ICompoundContainer<?> k, Set<CompoundInstance> v)
	{
		if (k.contents() instanceof Item i)
		{
			if (isValidEntry(k, v))
			{
				// determine if the set is valid.
				validEntryCache.put(i, v);
			} else
			{
				invalidEntryCache.put(i, v);
			}
		}
	}

	private static boolean isValidEntry(ICompoundContainer<?> k, Set<CompoundInstance> v)
	{
		// find "partial" values and deny them. This is an indication of things having too small values to justify
		// transmutation
		var isAnyValueMalformed = v.stream().anyMatch(c -> MateriaEquivalency.isMalformedEntry(k, c));
		return !isAnyValueMalformed;
	}

	private static boolean isMalformedEntry(ICompoundContainer<?> k, CompoundInstance compoundInstance)
	{
		// it's not uncommon for denied values to be malformed, we don't have to worry about those.
		// denied is more like an inheritable condition than an evaluation. Any amount of denied is enough to block.
		return compoundInstance.getType() != MateriaRegistry.DENIED.get() && Math.abs(compoundInstance.getAmount() % 1d) > precisionCutoff;
	}

	@Override
	public void onReloadFinishedFor(ServerLevel world)
	{
		if (world.dimension() == Level.OVERWORLD)
		{
			doEquivalencyQualityCheck();
		}
	}
}