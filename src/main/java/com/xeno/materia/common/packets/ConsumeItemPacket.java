package com.xeno.materia.common.packets;

import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.xeno.materia.aeq.MateriaEquivalency;
import com.xeno.materia.common.MateriaRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ConsumeItemPacket
{
    private final boolean isShifted; // shift enables full stack manipulations of both consume and create

    public ConsumeItemPacket(FriendlyByteBuf buf)
    {
        this.isShifted = buf.readBoolean();
    }

    public ConsumeItemPacket(boolean isShiftKeyHeld)
    {
        this.isShifted = isShiftKeyHeld;
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeBoolean(isShifted);
    }

    public class Handler
    {
        public static boolean onMessage(ConsumeItemPacket message, NetworkEvent.Context ctx)
        {
            ctx.enqueueWork(() -> handle(message, ctx));

            ctx.setPacketHandled(true);
            return true;
        }

        private static void handle(ConsumeItemPacket message, NetworkEvent.Context ctx)
        {
            // try for player
            var player = ctx.getSender();
            if (player == null)
            {
                return;
            }

            // try for slot and item
            var itemStack = player.containerMenu.getCarried();
            if (itemStack.isEmpty())
            {
                return;
            }

            // try for materia values
            var materiaFromDrops = MateriaEquivalency.getEquivalency(itemStack);
            if (materiaFromDrops.isEmpty())
            {
                return;
            }

            // examine the player's limits and sizzle at them if they're full.
            tryConsume(player, message, materiaFromDrops, itemStack.getCount());
        }
    }

    private static void tryConsume(ServerPlayer player, ConsumeItemPacket message,
                                   Set<CompoundInstance> materiaFromDrops, int stackCount)
    {
        AtomicInteger consumeAttempts =
                new AtomicInteger(getConsumeAttemptsIntended(message, stackCount));
        materiaFromDrops.forEach(m ->
                consumeAttempts.set(getConsumeAttemptLimit(consumeAttempts.get(), m, player)));

        if (consumeAttempts.get() == 0) {
            return;
        }

        if (player.inventoryMenu.getCarried().isEmpty()) {
            return;
        }

        // figure out what to replace the carried stack with, either a lesser stack or air.
        var newStack = ItemStack.EMPTY;
        if (consumeAttempts.get() < player.inventoryMenu.getCarried().getCount()) {
            newStack = player.inventoryMenu.getCarried()
                    .copyWithCount(player.inventoryMenu.getCarried().getCount() - consumeAttempts.get());
        }

        // replace the stack while rewarding the player's materia
        player.inventoryMenu.setCarried(newStack);
        materiaFromDrops.forEach(m -> rewardPlayerMateria(player, m));
    }

    private static void rewardPlayerMateria(Player p, CompoundInstance m)
    {
        MateriaRegistry.typeOf(m).changeMateria(p, m.getAmount().longValue());
    }

    /**
     * Handles returning the maximum number of items the player should consume to completely fill their materia limit.
     * This can return 0 if the materia in scope is also 0 (to avoid dividing by zero) as well as if the player's
     * materia of a given type is full.
     *
     * @param maxNumberToAttemptToConsume The baseline number to attempt, based on what keys are held/mouse button used
     *                                    and the stack size in scope.
     * @param materia                     The compound instance which is a type of materia and an amount (double).
     * @param p                           The player to store materia.
     * @return The maximum number of attempts the player should make either to completely fill one type of materia
     *          before stopping. The mod won't allow you to burn items if you don't have the space to hold them in you.
     */
    private static int getConsumeAttemptLimit(int maxNumberToAttemptToConsume,
                                              CompoundInstance materia, Player p)
    {
        return materia.getAmount() == 0 ? 0 : Math.min(maxNumberToAttemptToConsume,
                getFullConsumeCountToFillMateriaType(materia, p));
    }

    private static int getFullConsumeCountToFillMateriaType(CompoundInstance materia, Player p)
    {
        return  (int)Math.ceil(MateriaRegistry.typeOf(materia).getSpace(p) / materia.getAmount());
    }

    /**
     * Handles figuring out how many items the player is trying to consume at a time, at most.
     *
     * @param message    The message which contains whether the player is holding shift or using right click.
     * @param stackCount The stack count of the stack they're hovering over which factors into half/full stack counts.
     * @return How many items should be consumed, at most, by the operation, not accounting for limits.
     */
    private static int getConsumeAttemptsIntended(ConsumeItemPacket message, int stackCount)
    {
        return !message.isShifted ? 1 : stackCount;
    }

}
