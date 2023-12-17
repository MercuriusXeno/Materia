package com.xeno.materia.client.keys;

import com.xeno.materia.MateriaMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MateriaMod.ID)
public class KeyHandler
{

    @SubscribeEvent
    public static void keyEvent(final InputEvent.Key eventKey)
    {
        // if Aequivaleo isn't loaded, do _nothing_.
        if (!MateriaMod.isAequivaleoLoaded())
        {
            return;
        }
        RadialMenuKeyHandler.handleRadialKeyEvent(eventKey);
        AbilityKeyHandler.handleAbilityKeyEvent(eventKey);
    }

    @SubscribeEvent
    public static void mouseEvent(final InputEvent.MouseButton.Post eventButton)
    {
        MateriaControlKeyHandler.handleMouseEventGenerally(eventButton);
    }

    @SubscribeEvent
    public static void screenMouseButtonPressed(final ScreenEvent.MouseButtonPressed.Post pressedMouseEvent)
    {
        MateriaControlKeyHandler.handleMouseEventOnScreens(pressedMouseEvent);
    }

    @SubscribeEvent
    public static void screenMouseButtonPressed(final ScreenEvent.KeyPressed.Post pressedKeyEvent)
    {
        MateriaControlKeyHandler.handleKeyEventOnScreens(pressedKeyEvent);
    }
}