package com.xeno.materia.common;

import com.xeno.materia.MateriaMod;
import com.xeno.materia.aeq.MateriaEquivalency;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;

public class MateriaBlockHandler
{

    private static HashMap<ResourceLocation, Double> getMateriaFromDrops(List<ItemStack> drops)
    {
        // don't forget we're client side, this is a display thing. Equivalencies are fractional.
        var result = new HashMap<ResourceLocation, Double>();
        for (var drop : drops)
        {
            var materiaValue = MateriaEquivalency.getEquivalency(drop);
            if (materiaValue.isEmpty())
            {
                continue;
            }
            for (var materia : materiaValue)
            {
                var loc = materia.asRef().type();
                if (result.containsKey(loc))
                {
                    result.put(loc, result.get(loc) + materia.getAmount());
                } else
                {
                    result.put(loc, materia.getAmount());
                }
            }
        }

        return result;
    }

    /**
     * Absorbs a block in the world if there is something there, this wound up being deprecated, but I'm hanging onto it
     * in case I can think of a reason this is helpful.
     * @param level
     * @param pos
     * @param thing
     * @param player
     * @param materiaValues
     */
    public static void doBlockAbsorb(Level level, BlockPos pos, BlockState thing, Player player,
                                     HashMap<ResourceLocation, Double> materiaValues)
    {
        if (!level.isClientSide && thing.getBlock() != Blocks.WATER || thing.getFluidState().isSource())
        {
            // destroy the block if it isn't water
            level.destroyBlock(pos, false);

            MateriaMod.debug("destroyed block, materia: ");
            materiaValues.forEach((k, v) -> MateriaMod.debug(k.getPath() + " " + k.getNamespace() + " " + v));
        }
        materiaValues.forEach((k, v) -> MateriaRegistry.MATERIA_NAMES.get(k).changeMateria(player, v.longValue()));
    }

    /**
     * Builds the loot parameter of the player's main hand which is maybe not always right, but whatever. Fix it later.
     * @param pEntity The player in scope
     * @return The loot param builder of the player based on their main hand.
     */
    public static LootParams.Builder buildLootParams(ServerPlayer pEntity)
    {
        return new LootParams.Builder(pEntity.serverLevel())
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pEntity.blockPosition()))
                .withParameter(LootContextParams.TOOL, pEntity.getMainHandItem()) // if any tool, it counts, but it has to be in main-hand.
                .withOptionalParameter(LootContextParams.THIS_ENTITY, pEntity);
    }
}
