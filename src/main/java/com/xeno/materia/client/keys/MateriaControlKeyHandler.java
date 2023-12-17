package com.xeno.materia.client.keys;

import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.mojang.blaze3d.platform.InputConstants;
import com.xeno.materia.MateriaMod;
import com.xeno.materia.aeq.MateriaEquivalency;
import com.xeno.materia.client.ClientUtils;
import com.xeno.materia.common.MateriaEnum;
import com.xeno.materia.common.MateriaRegistry;
import com.xeno.materia.common.capabilities.MateriaCapability;
import com.xeno.materia.common.capabilities.MateriaCapabilityImpl;
import com.xeno.materia.common.packets.ConsumeItemPacket;
import com.xeno.materia.common.packets.MateriaNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

public class MateriaControlKeyHandler
{
    private static boolean isCreateKey(int key)
    {
        return isKeyMatchFor(key, ModKeyBindings.CREATE_KEY);
    }

    /**
     * Handles the actions of a mouse in conjunction with
     *
     * @param eventMouseButton
     */
    public static void handleMouseEventGenerally(InputEvent.MouseButton eventMouseButton)
    {
        // NOOP, I was doing something here and now I'm not.
    }

    public static void handleMouseEventOnScreens(ScreenEvent.MouseButtonPressed eventMouseButton)
    {
        // NOOP, I was doing something here and now I'm not.
    }

    public static void handleKeyEventOnScreens(ScreenEvent.KeyPressed pressedKeyEvent)
    {
        if (Minecraft.getInstance().screen instanceof EffectRenderingInventoryScreen screen)
        {
            handleCursorKeystroke(pressedKeyEvent.getKeyCode(), screen);
        }
    }

    private static void handleCursorKeystroke(int keyCode, EffectRenderingInventoryScreen screen)
    {
        // if the consume key is held, your clicks do something different on the inventory screen :)
        if (isKeyDown(ModKeyBindings.CONSUME_KEY))
        {
            MateriaMod.debug("Consume key down during click");
            doConsumeItemEvent(screen);
        }
        // let's say the user is really dumb and is trying to hold both keys at once, make these mutually exclusive.
        else if (isKeyDown(ModKeyBindings.CREATE_KEY))
        {
            MateriaMod.debug("Create key down during click");
            doCreateItemEvent(screen);
        }
    }

    public static boolean isKeyDown(KeyMapping keybind)
    {
        if (keybind.isUnbound())
            return false;

        boolean isDown = switch (keybind.getKey().getType())
                {
                    case KEYSYM -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keybind.getKey().getValue());
                    case MOUSE -> GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), keybind.getKey().getValue()) == GLFW.GLFW_PRESS;
                    default -> false;
                };
        return isDown && keybind.isConflictContextAndModifierActive();
    }

    /**
     * Needed to perform interactions in the inventory either creating or absorbing items from the player's alchemy
     * reserves. Hopefully this just works? lol
     *
     * @param screen The screen the player is interacting with, I have no clue if this is going to work.     *
     */
    private static void doConsumeItemEvent(EffectRenderingInventoryScreen screen)
    {
        // we need a player
        var player = Minecraft.getInstance().player;
        if (player == null)
        {
            return;
        }
        // there must be an item in the cursor
        var itemHeld = screen.getMenu().getCarried();
        if (itemHeld.isEmpty())
        {
            return;
        }

        // it must have an equivalency
        var equivalency = MateriaEquivalency.getEquivalency(itemHeld);
        if (equivalency.isEmpty())
        {
            return;
        }

        // if they're in creative mode's inventory there's strange client dominance
        // resulting in held item not actually existing server side, which is neat, but
        // just give the player materia. They're in creative. Stop wasting time.
        if (screen instanceof CreativeModeInventoryScreen)
        {
            consumeItemInCreativeMode(player, equivalency);
        } else
        {
            // The server has to assess if the player can consume the item by actually checking what they're holding.
            // Capacity checks and stuff fire during the server side handling, a bunch of stuff that doesn't happen
            // while creative mode is on.
            MateriaNetworking.sendToServer(new ConsumeItemPacket(player.isShiftKeyDown()));
        }
    }

    private static void consumeItemInCreativeMode(LocalPlayer player, Set<CompoundInstance> equivalency)
    {
        player.getCapability(MateriaCapabilityImpl.MATERIA)
                .ifPresent(mc -> equivalency.forEach(eq -> givePlayerMateria(mc, eq)));
        player.inventoryMenu.setCarried(ItemStack.EMPTY); // nullify the item
    }

    private static void givePlayerMateria(MateriaCapability mc, CompoundInstance eq)
    {
        if (MateriaRegistry.typeOf(eq) != MateriaEnum.DENIED)
        {
            mc.changeMateria(MateriaRegistry.typeOf(eq), eq.getAmount().longValue());
        }
    }

    private static boolean isRightMouse(int button)
    {
        return button == InputConstants.MOUSE_BUTTON_RIGHT;
    }

    private static boolean isLeftOrRightMouse(int button)
    {
        return isLeftMouse(button) || isRightMouse(button);
    }

    private static boolean isLeftMouse(int button)
    {
        return button == InputConstants.MOUSE_BUTTON_LEFT;
    }

    private static void doCreateItemEvent(EffectRenderingInventoryScreen screen)
    {
    }

    private static boolean isConsumeKey(int key)
    {
        return isKeyMatchFor(key, ModKeyBindings.CONSUME_KEY);
    }

    private static boolean isKeyMatchFor(int key, KeyMapping whateverKey)
    {
        return isKeyMatchFor(key, whateverKey.getKey().getValue());
    }

    private static boolean isKeyMatchFor(int key, int whateverKeyValue)
    {
        return key == whateverKeyValue;
    }

    /**
     * Holdover from when this was Geophage and I was doing in-world absorbing abilities which are a little jank/op.
     * Also just really not what I'm going for. But I wanted to hold onto this code in case I need to steal it later.
     *
     * @param player The player doing the looking.
     * @return The BlockHitResult of the thing the player is looking at.
     */
    private static BlockHitResult doHitScanThing(Player player)
    {

        // the "none" here means we're pretending we have an empty bucket. The reason this matters is
        // we can't pick up fluids or use geophage on fluids unless we pretend our fake bucket is empty first.
        BlockHitResult hit = ClientUtils.getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.SOURCE_ONLY);
        ;
        var fluidMaybe = player.level().getBlockState(hit.getBlockPos());
        if (fluidMaybe.isAir())
        {
            hit = ClientUtils.getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.NONE);
        }
        return hit;
    }
}
