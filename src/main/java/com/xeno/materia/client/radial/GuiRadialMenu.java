package com.xeno.materia.client.radial;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.xeno.materia.client.keys.RadialMenuKeyHandler;
import com.xeno.materia.common.MateriaEnum;
import com.xeno.materia.common.packets.MateriaNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
/**
 * Unabashedly stolen from Gigaherz and BaileyH (they are my fronds)
 */
public class GuiRadialMenu<T> extends Screen
{
    private static final float PRECISION = 5.0f;
    private static final int MAX_SLOTS = 20;
    private static final String INFUSION_INSTRUCTIONS_1 = "radial.materia.infusion_instructions_1";
    private static final String INFUSION_INSTRUCTIONS_2 = "radial.materia.infusion_instructions_2";

    private final boolean closing;
    private final RadialMenu<T> radialMenu;
    private final List<RadialMenuSlot<T>> radialMenuSlots;
    final float OPEN_ANIMATION_LENGTH = 0.40f;
    private float totalTime;
    private float prevTick;
    private float extraTick;
    /**
     * Zero-Based index
     */
    private int selectedItem;
    public ItemRenderer itemRenderer;

    public GuiRadialMenu(RadialMenu<T> radialMenu)
    {
        super(Component.literal(""));
        this.radialMenu = radialMenu;
        this.radialMenuSlots = this.radialMenu.getRadialMenuSlots();
        this.closing = false;
        this.minecraft = Minecraft.getInstance();
        this.selectedItem = -1;
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @SubscribeEvent
    public static void updateInputEvent(MovementInputUpdateEvent event)
    {
        if (Minecraft.getInstance().screen instanceof GuiRadialMenu)
        {

            Options settings = Minecraft.getInstance().options;
            Input eInput = event.getInput();
            long window = Minecraft.getInstance().getWindow().getWindow();
            eInput.up = InputConstants.isKeyDown(window, settings.keyUp.getKey().getValue());
            eInput.down = InputConstants.isKeyDown(window, settings.keyDown.getKey().getValue());
            eInput.left = InputConstants.isKeyDown(window, settings.keyLeft.getKey().getValue());
            eInput.right = InputConstants.isKeyDown(window, settings.keyRight.getKey().getValue());

            eInput.forwardImpulse = eInput.up == eInput.down ? 0.0F : (eInput.up ? 1.0F : -1.0F);
            eInput.leftImpulse = eInput.left == eInput.right ? 0.0F : (eInput.left ? 1.0F : -1.0F);
            eInput.jumping = InputConstants.isKeyDown(window, settings.keyJump.getKey().getValue());
            eInput.shiftKeyDown = InputConstants.isKeyDown(window, settings.keyShift.getKey().getValue());
            if (Minecraft.getInstance().player.isMovingSlowly())
            {
                eInput.leftImpulse = (float) ((double) eInput.leftImpulse * 0.3D);
                eInput.forwardImpulse = (float) ((double) eInput.forwardImpulse * 0.3D);
            }
        }
    }

    @Override
    public void tick()
    {
        if (totalTime != OPEN_ANIMATION_LENGTH)
        {
            extraTick++;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);
        PoseStack ms = graphics.pose();
        float openAnimation = closing ? 1.0f - totalTime / OPEN_ANIMATION_LENGTH : totalTime / OPEN_ANIMATION_LENGTH;
        float currTick = minecraft.getFrameTime();
        totalTime += (currTick + extraTick - prevTick) / 20f;
        extraTick = 0;
        prevTick = currTick;

        float animProgress = Mth.clamp(openAnimation, 0, 1);
        animProgress = (float) (1 - Math.pow(1 - animProgress, 3));
        float radiusIn = Math.max(0.1f, 50 * animProgress);
        float radiusOut = radiusIn * 2f;
        float iconCenterRadius = (radiusIn + radiusOut) * 0.5f;
        float percentFilledRadius = (radiusIn + radiusOut) * 0.75f;

        int centerOfScreenX = width / 2;
        int centerOfScreenY = height / 2;
        int numberOfSlices = Math.min(MAX_SLOTS, radialMenuSlots.size());

        double mousePositionInDegreesInRelationToCenterOfScreen = Math.toDegrees(Math.atan2(mouseY - centerOfScreenY, mouseX - centerOfScreenX));
        double mouseDistanceToCenterOfScreen = Math.sqrt(Math.pow(mouseX - centerOfScreenX, 2) + Math.pow(mouseY - centerOfScreenY, 2));
        float slot0 = (((-0.5f) / (float) numberOfSlices) + 0.25f) * 360;
        if (mousePositionInDegreesInRelationToCenterOfScreen < slot0)
        {
            mousePositionInDegreesInRelationToCenterOfScreen += 360;
        }

        ms.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);


        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        boolean hasMouseOver = false;
        int mousedOverSlot = -1;

        if (!closing)
        {
            selectedItem = -1;
            for (int i = 0; i < numberOfSlices; i++)
            {
                float sliceBorderLeft = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
                float sliceBorderRight = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
                if (mousePositionInDegreesInRelationToCenterOfScreen >= sliceBorderLeft && mousePositionInDegreesInRelationToCenterOfScreen < sliceBorderRight && mouseDistanceToCenterOfScreen >= radiusIn && mouseDistanceToCenterOfScreen < radiusOut)
                {
                    selectedItem = i;
                    break;
                }
            }
        }


        for (int i = 0; i < numberOfSlices; i++)
        {
            float sliceBorderLeft = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
            float sliceBorderRight = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
            if (selectedItem == i)
            {
                drawSlice(buffer, centerOfScreenX, centerOfScreenY, 10, radiusIn, radiusOut, sliceBorderLeft, sliceBorderRight, 63, 161, 191, 60);
                hasMouseOver = true;
                mousedOverSlot = selectedItem;
            } else
                drawSlice(buffer, centerOfScreenX, centerOfScreenY, 10, radiusIn, radiusOut, sliceBorderLeft, sliceBorderRight, 0, 0, 0, 64);
        }

        tessellator.end();
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        if (hasMouseOver && mousedOverSlot != -1)
        {
            int adjusted = ((mousedOverSlot + (numberOfSlices / 2 + 1)) % numberOfSlices) - 1;
            adjusted = adjusted == -1 ? numberOfSlices - 1 : adjusted;

            if (radialMenuSlots.get(adjusted).percentValue() >= 1.0d)
            {
                // make sure it's translatable
                var infusionInstructions1 = Component.translatable(INFUSION_INSTRUCTIONS_1);
                var infusionInstructions2 = Component.translatable(INFUSION_INSTRUCTIONS_2);
                graphics.drawCenteredString(font, infusionInstructions1,
                        width / 2, (height - font.lineHeight * 8) / 2, 16777215);
                graphics.drawCenteredString(font, infusionInstructions2,
                        width / 2, (height - font.lineHeight * 4) / 2, 16777215);
            }
            graphics.drawCenteredString(font, radialMenuSlots.get(adjusted)
                    .slotName(), width / 2, (height - font.lineHeight) / 2, 16777215);
            graphics.drawCenteredString(font, radialMenuSlots.get(adjusted)
                    .display(), width / 2, (height + font.lineHeight * 4) / 2, 16777215);
        }

        ms.popPose();
        for (int i = 0; i < numberOfSlices; i++)
        {
            float angle1 = ((i / (float) numberOfSlices) - 0.25f) * 2 * (float) Math.PI;
            if (numberOfSlices % 2 != 0)
            {
                angle1 += Math.PI / numberOfSlices;
            }
            float posX = centerOfScreenX + iconCenterRadius * (float) Math.cos(angle1);
            float posY = centerOfScreenY + iconCenterRadius * (float) Math.sin(angle1);
            float percentPosX = centerOfScreenX + percentFilledRadius * (float) Math.cos(angle1);
            float percentPosY = centerOfScreenY - (font.lineHeight / 2f) + percentFilledRadius * (float) Math.sin(angle1);
            // offset the percent coords because centered strings seem to align to the bottom rightmost pixel
            // of the string rather than, as the name implies, centering them? I don't really understand why
            // this is named centered. It's not even remotely centered on the coords provided, that's not what it does.
            // so the offset by half the font width again seems to fix this dumb bullshit, but I kind of hate it here.
            graphics.drawCenteredString(font, radialMenuSlots.get(i).percent(), (int) percentPosX, (int) percentPosY, 0xffffffff);
            T primarySlotIcon = radialMenuSlots.get(i).primarySlotIcon();
            List<T> secondarySlotIcons = radialMenuSlots.get(i).secondarySlotIcons();
            if (primarySlotIcon != null)
            {
                radialMenu.drawIcon(primarySlotIcon, graphics, (int) posX, (int) posY, 64);
                if (secondarySlotIcons != null && !secondarySlotIcons.isEmpty())
                {
                    drawSecondaryIcons(graphics, (int) posX, (int) posY, secondarySlotIcons);
                }
            }
            ms.pushPose();
            ms.translate(0, 0, 20);
            drawSliceName(graphics, String.valueOf(i + 1), (int) posX, (int) posY);
            ms.popPose();
        }

        if (mousedOverSlot != -1)
        {
            int adjusted = ((mousedOverSlot + (numberOfSlices / 2 + 1)) % numberOfSlices) - 1;
            adjusted = adjusted == -1 ? numberOfSlices - 1 : adjusted;
            selectedItem = adjusted;
        }

    }

    public void drawSecondaryIcons(GuiGraphics ms, int positionXOfPrimaryIcon, int positionYOfPrimaryIcon, List<T> secondarySlotIcons)
    {
        if (!radialMenu.isShowMoreSecondaryItems())
        {
            drawSecondaryIcon(ms, secondarySlotIcons.get(0), positionXOfPrimaryIcon, positionYOfPrimaryIcon, radialMenu.getSecondaryIconStartingPosition());
        } else
        {
            SecondaryIconPosition currentSecondaryIconPosition = radialMenu.getSecondaryIconStartingPosition();
            for (T secondarySlotIcon : secondarySlotIcons)
            {
                drawSecondaryIcon(ms, secondarySlotIcon, positionXOfPrimaryIcon, positionYOfPrimaryIcon, currentSecondaryIconPosition);
                currentSecondaryIconPosition = SecondaryIconPosition.getNextPositon(currentSecondaryIconPosition);
            }
        }
    }

    public void drawSecondaryIcon(GuiGraphics poseStack, T item, int positionXOfPrimaryIcon, int positionYOfPrimaryIcon, SecondaryIconPosition secondaryIconPosition)
    {
        int offset = radialMenu.getOffset();
        switch (secondaryIconPosition)
        {
            case NORTH -> radialMenu.drawIcon(item, poseStack, positionXOfPrimaryIcon + offset, positionYOfPrimaryIcon - 14 + offset, 10);
            case EAST -> radialMenu.drawIcon(item, poseStack, positionXOfPrimaryIcon + 14 + offset, positionYOfPrimaryIcon + offset, 10);
            case SOUTH -> radialMenu.drawIcon(item, poseStack, positionXOfPrimaryIcon + offset, positionYOfPrimaryIcon + 14 + offset, 10);
            case WEST -> radialMenu.drawIcon(item, poseStack, positionXOfPrimaryIcon - 14 + offset, positionYOfPrimaryIcon + offset, 10);
        }
    }

    public void drawSliceName(GuiGraphics graphics, String sliceName, int posX, int posY)
    {
        if (!radialMenu.isShowMoreSecondaryItems())
        {
            graphics.renderItemDecorations(font, ItemStack.EMPTY, posX + 5, posY, sliceName);
        } else
        {
            graphics.renderItemDecorations(font, ItemStack.EMPTY, posX + 5, posY + 5, sliceName);
        }
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers)
    {
        int adjustedKey = key - 48;
        if (adjustedKey >= 0 && adjustedKey < radialMenuSlots.size())
        {
            selectedItem = adjustedKey == 0 ? radialMenuSlots.size() : adjustedKey;
            selectedItem = selectedItem - 1; // Offset by 1 because 0 based indexing but users see 1 indexed
            mouseClicked(0, 0, 0);
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (this.selectedItem != -1)
        {
            // right click spends a full materia bar to increase your cap by 1.
            if (mouseButton == InputConstants.MOUSE_BUTTON_RIGHT) {
                radialMenu.setCurrentSlot(selectedItem);
                if (Minecraft.getInstance().player != null)
                {
                    var e = MateriaEnum.valueMap().get(selectedItem);
                    e.spendMateriaOnLimit(Minecraft.getInstance().player);
                    refreshSlots(Minecraft.getInstance().player, e);
                }
            }
        }
        return true;
    }

    public void drawSlice(
            BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a)
    {
        float angle = endAngle - startAngle;
        int sections = Math.max(1, Mth.ceil(angle / PRECISION));

        startAngle = (float) Math.toRadians(startAngle);
        endAngle = (float) Math.toRadians(endAngle);
        angle = endAngle - startAngle;

        for (int i = 0; i < sections; i++)
        {
            float angle1 = startAngle + (i / (float) sections) * angle;
            float angle2 = startAngle + ((i + 1) / (float) sections) * angle;

            float pos1InX = x + radiusIn * (float) Math.cos(angle1);
            float pos1InY = y + radiusIn * (float) Math.sin(angle1);
            float pos1OutX = x + radiusOut * (float) Math.cos(angle1);
            float pos1OutY = y + radiusOut * (float) Math.sin(angle1);
            float pos2OutX = x + radiusOut * (float) Math.cos(angle2);
            float pos2OutY = y + radiusOut * (float) Math.sin(angle2);
            float pos2InX = x + radiusIn * (float) Math.cos(angle2);
            float pos2InY = y + radiusIn * (float) Math.sin(angle2);

            buffer.vertex(pos1OutX, pos1OutY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos1InX, pos1InY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos2InX, pos2InY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos2OutX, pos2OutY, z).color(r, g, b, a).endVertex();
        }
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public void refreshSlots(Player pl, MateriaEnum e)
    {
        radialMenuSlots.remove(e.getValue());
        //noinspection unchecked
        radialMenuSlots.add(e.getValue(), (RadialMenuSlot<T>) e.makeRadialSlot(pl));
    }
}

/*
Note: This code has been modified from David Quintana's solution.
Below is the required copyright notice.
Copyright (c) 2015, David Quintana <gigaherz@gmail.com>
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the author nor the
      names of the contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/