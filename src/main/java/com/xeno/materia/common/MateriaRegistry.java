package com.xeno.materia.common;

import com.ldtteam.aequivaleo.api.compound.type.ICompoundType;
import com.ldtteam.aequivaleo.api.compound.type.group.ICompoundTypeGroup;
import com.xeno.materia.MateriaMod;
import com.xeno.materia.aeq.MateriaCompoundType;
import com.xeno.materia.aeq.MateriaCompoundTypeGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

public class MateriaRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MateriaMod.ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MateriaMod.ID);
	private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MateriaMod.ID);
	private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MateriaMod.ID);

	private static final ResourceLocation AEQ_COMPOUND_TYPES_LOC = new ResourceLocation("aequivaleo", "compound_type");
	private static final ResourceLocation AEQ_COMPOUND_TYPE_GROUPS_LOC = new ResourceLocation("aequivaleo", "compound_type_group");
	public static final DeferredRegister<ICompoundType> TYPES = DeferredRegister.create(AEQ_COMPOUND_TYPES_LOC, MateriaMod.ID);
	public static final DeferredRegister<ICompoundTypeGroup> TYPE_GROUPS = DeferredRegister.create(AEQ_COMPOUND_TYPE_GROUPS_LOC, MateriaMod.ID);
	public static final RegistryObject<MateriaCompoundTypeGroup> MATERIA_TYPES = TYPE_GROUPS.register("materia_types", MateriaCompoundTypeGroup::new);

	private static RegistryObject<ICompoundType> registerMateria(String name, MateriaEnum materiaEnum, Item representativeItem)
	{
		RegistryObject<ICompoundType> result = TYPES
				.register(name, () -> new MateriaCompoundType(materiaEnum, MATERIA_TYPES, representativeItem));
		MATERIA.put(materiaEnum, result);
		MATERIA_NAMES.put(result.getId(), materiaEnum);
		return result;
	}

	public static final HashMap<MateriaEnum, RegistryObject<ICompoundType>> MATERIA = new HashMap<>();
	public static final HashMap<ResourceLocation, MateriaEnum> MATERIA_NAMES = new HashMap<>();
	public static final RegistryObject<ICompoundType> CRYO = registerMateria("cryo", MateriaEnum.CRYO, Items.SNOWBALL);
	public static final RegistryObject<ICompoundType> PYRO = registerMateria("pyro", MateriaEnum.PYRO, Items.BLAZE_POWDER);
	public static final RegistryObject<ICompoundType> MYCO = registerMateria("myco", MateriaEnum.MYCO, Items.BROWN_MUSHROOM);
	public static final RegistryObject<ICompoundType> GEMMA = registerMateria("gemma", MateriaEnum.GEMMA, Items.DIAMOND);
	public static final RegistryObject<ICompoundType> PHOTO = registerMateria("photo", MateriaEnum.PHOTO, Items.GLOW_INK_SAC);
	public static final RegistryObject<ICompoundType> DYNA = registerMateria("dyna", MateriaEnum.DYNA, Items.GUNPOWDER);
	public static final RegistryObject<ICompoundType> KREA = registerMateria("krea", MateriaEnum.KREA, Items.MUTTON);
	public static final RegistryObject<ICompoundType> GEO = registerMateria("geo", MateriaEnum.GEO, Items.CLAY_BALL);
	public static final RegistryObject<ICompoundType> ORICHO = registerMateria("oricho", MateriaEnum.ORICHO, Items.RAW_COPPER);
	public static final RegistryObject<ICompoundType> PROTO = registerMateria("proto", MateriaEnum.PROTO, Items.NETHERITE_SCRAP);
	public static final RegistryObject<ICompoundType> AERO = registerMateria("aero", MateriaEnum.AERO, Items.ENDER_PEARL);
	public static final RegistryObject<ICompoundType> HYDRO = registerMateria("hydro", MateriaEnum.HYDRO, Items.WATER_BUCKET);
	public static final RegistryObject<ICompoundType> XYLO = registerMateria("xylo", MateriaEnum.XYLO, Items.STRIPPED_OAK_LOG);
	public static final RegistryObject<ICompoundType> CHLORO = registerMateria("chloro", MateriaEnum.CHLORO, Items.FERN);
	public static final RegistryObject<ICompoundType> ELECTRO = registerMateria("electro", MateriaEnum.ELECTRO, Items.REDSTONE);
	public static final RegistryObject<ICompoundType> PHASMO = registerMateria("phasmo", MateriaEnum.PHASMO, Items.GHAST_TEAR);
	public static final RegistryObject<ICompoundType> DENIED = registerMateria("denied", MateriaEnum.DENIED, Items.AIR);

	public static void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		BLOCKS.register(bus);
		ITEMS.register(bus);
		TYPES.register(bus);
		TYPE_GROUPS.register(bus);
		ENTITIES.register(bus);
		EFFECTS.register(bus);
	}

//	// TEST ITEMS FOR CASTING A PROJECTILE FROM THE MATERIA
//	public static final RegistryObject<Item> GEO_ITEM = ITEMS.register("geo_item", () -> new DebugProjectile(MateriaEnum.GEO));
//
//	// GOO BLOB ENTITY FOR THE LOBBING
//	public static final RegistryObject<EntityType<ThrownGeo>> THROWN_GEO = ENTITIES.register("thrown_geo",
//			() -> EntityType.Builder.<ThrownGeo>of(ThrownGeo::new, MobCategory.MISC)
//					.sized(0.25F, 0.25F).clientTrackingRange(32).updateInterval(2).build("thrown_geo"));
//
//	// STATUS EFFECTS, AILMENTS, BUFFS ETC
//	public static final PetrificationEffect PETRIFICATION_EFFECT_SINGLETON = (PetrificationEffect)new PetrificationEffect()
//			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "530806B2-4908-433E-8728-46D920B2A796", (double)-0.01F, AttributeModifier.Operation.MULTIPLY_TOTAL)
//			.addAttributeModifier(Attributes.ATTACK_SPEED, "AD42B3CC-5EE3-41FC-A81C-3B544202CA6F", (double)-0.01F, AttributeModifier.Operation.MULTIPLY_TOTAL);
//	public static final RegistryObject<AbstractEffect> PETRIFICATION_EFFECT = EFFECTS.register("petrification", () -> PETRIFICATION_EFFECT_SINGLETON);

}