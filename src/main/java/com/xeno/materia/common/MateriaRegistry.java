package com.xeno.materia.common;

import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.ldtteam.aequivaleo.api.compound.type.ICompoundType;
import com.ldtteam.aequivaleo.api.compound.type.group.ICompoundTypeGroup;
import com.mojang.serialization.Codec;
import com.xeno.materia.MateriaMod;
import com.xeno.materia.aeq.MateriaCompoundType;
import com.xeno.materia.aeq.MateriaCompoundTypeGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.function.Supplier;

public class MateriaRegistry {

    // init method handles registering the various registry types we need
    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TYPES.register(bus);
        TYPE_GROUPS.register(bus);
        ENTITIES.register(bus);
        EFFECTS.register(bus);
        CREATIVE_MODE_TABS.register(bus);
        ATTACHMENT_TYPES.register(bus);
    }

    // neoforge registries, the builtins
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MateriaMod.ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MateriaMod.ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MateriaMod.ID);
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MateriaMod.ID);
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MateriaMod.ID);

    // aequivaleo registries and registrations, materia types
    public static final DeferredRegister<ICompoundType> TYPES = DeferredRegister.create(MateriaMod.aeqLoc(MateriaNames.COMPOUND_TYPE), MateriaMod.ID);
    public static final DeferredRegister<ICompoundTypeGroup> TYPE_GROUPS = DeferredRegister.create(MateriaMod.aeqLoc(MateriaNames.COMPOUND_TYPE_GROUP), MateriaMod.ID);
    public static final DeferredHolder<ICompoundTypeGroup, MateriaCompoundTypeGroup> MATERIA_TYPE = TYPE_GROUPS.register(MateriaNames.MATERIA_TYPES, MateriaCompoundTypeGroup::new);

    // data attachments, for the sake of outstanding flatness, each attachment is set up individually and separately.
    // LIMITS are separated from STOCKS, and each MateriaEnum (type) has its own attachment. That way
    public static final HashMap<MateriaEnum, Supplier<AttachmentType<Long>>> MATERIA_LIMIT_ATTACHMENTS = new HashMap<>();
    public static final HashMap<MateriaEnum, Supplier<AttachmentType<Long>>> MATERIA_STOCK_ATTACHMENTS = new HashMap<>();

    // the vanilla texture atlas for GUIs in case I need it, but I'm pretty sure I stopped needing it? I can't remember
    public static final ResourceLocation VANILLA_GUI_ATLAS = MateriaMod.mcLoc(MateriaNames.VANILLA_GUI_ATLAS);

    private static DeferredHolder<ICompoundType, MateriaCompoundType> registerMateria(String name, MateriaEnum materiaEnum, Item representativeItem) {
        DeferredHolder<ICompoundType, MateriaCompoundType> result = TYPES
                .register(name, () -> new MateriaCompoundType(materiaEnum, MATERIA_TYPE));
        MATERIA.put(materiaEnum, result);
        MATERIA_NAMES.put(result.getId(), materiaEnum);
        MATERIA_ICON_NAMES.put(materiaEnum, MateriaMod.location(String.format("textures/gui/%s.png", name)));
        MATERIA_LIMIT_ATTACHMENTS.put(materiaEnum, ATTACHMENT_TYPES.register(String.format("%s_limit", name),
                () -> AttachmentType.<Long>builder(() -> 0L).serialize(Codec.LONG).copyOnDeath().build()));
        MATERIA_STOCK_ATTACHMENTS.put(materiaEnum, ATTACHMENT_TYPES.register(String.format("%s_stock", name),
                () -> AttachmentType.<Long>builder(() -> 0L).serialize(Codec.LONG).copyOnDeath().build()));
        return result;
    }

    // hashmaps and holders for the materia types themselves, these are needed for different reasons/lookup.
    // there's cleaner ways to structure this, but I think they are few enough to tolerate this.
    public static final HashMap<MateriaEnum, DeferredHolder<ICompoundType, MateriaCompoundType>> MATERIA = new HashMap<>();
    public static final HashMap<ResourceLocation, MateriaEnum> MATERIA_NAMES = new HashMap<>();
    public static final HashMap<MateriaEnum, ResourceLocation> MATERIA_ICON_NAMES = new HashMap<>();
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> CRYO = registerMateria("cryo", MateriaEnum.CRYO, Items.SNOWBALL);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> PYRO = registerMateria("pyro", MateriaEnum.PYRO, Items.BLAZE_POWDER);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> MYCO = registerMateria("myco", MateriaEnum.MYCO, Items.BROWN_MUSHROOM);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> GEMMA = registerMateria("gemma", MateriaEnum.GEMMA, Items.DIAMOND);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> PHOTO = registerMateria("photo", MateriaEnum.PHOTO, Items.GLOW_INK_SAC);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> VIVO = registerMateria("vivo", MateriaEnum.VIVO, Items.GUNPOWDER);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> KREA = registerMateria("krea", MateriaEnum.KREA, Items.MUTTON);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> GEO = registerMateria("geo", MateriaEnum.GEO, Items.CLAY_BALL);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> ORICHO = registerMateria("oricho", MateriaEnum.ORICHO, Items.RAW_COPPER);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> PROTO = registerMateria("proto", MateriaEnum.PROTO, Items.NETHERITE_SCRAP);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> AERO = registerMateria("aero", MateriaEnum.AERO, Items.ENDER_PEARL);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> HYDRO = registerMateria("hydro", MateriaEnum.HYDRO, Items.WATER_BUCKET);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> XYLO = registerMateria("xylo", MateriaEnum.XYLO, Items.STRIPPED_OAK_LOG);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> CHLORO = registerMateria("chloro", MateriaEnum.CHLORO, Items.FERN);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> ELECTRO = registerMateria("electro", MateriaEnum.ELECTRO, Items.REDSTONE);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> ANIMA = registerMateria("anima", MateriaEnum.ANIMA, Items.GHAST_TEAR);
    public static final DeferredHolder<ICompoundType, MateriaCompoundType> DENIED = registerMateria("denied", MateriaEnum.DENIED, Items.AIR);

    // creative tab
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MateriaMod.ID);

    public static MateriaEnum getMateriaByCompoundType(CompoundInstance m) {
        return MATERIA_NAMES.get(m.asRef().type());
    }

    public static boolean isType(CompoundInstance compoundInstance, MateriaEnum type) {
        return MATERIA_NAMES.containsKey(compoundInstance.asRef().type()) &&
                MATERIA_NAMES.get(compoundInstance.asRef().type()) == type;
    }

    public static MateriaEnum typeOf(CompoundInstance eq) {
        return MATERIA_NAMES.get(eq.asRef().type());
    }

    public static Supplier<AttachmentType<Long>> stockOf(MateriaEnum materiaEnum)
    {
        return MATERIA_STOCK_ATTACHMENTS.get(materiaEnum);
    }

    public static Supplier<AttachmentType<Long>> limitOf(MateriaEnum materiaEnum)
    {
        return MATERIA_LIMIT_ATTACHMENTS.get(materiaEnum);
    }
}