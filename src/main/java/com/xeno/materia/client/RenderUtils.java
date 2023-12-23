package com.xeno.materia.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.xeno.materia.client.radial.MateriaSlotData;
import com.xeno.materia.client.radial.TintedVertexConsumer;
import com.xeno.materia.common.MateriaRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.awt.Color;

// stolen from Ars Nouveau/Bailey, who probably stole it from someone else
public class RenderUtils
{
    // private static final RenderType TRANSLUCENT = RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS);
    private static final int TEXTURE_HEIGHT = 64;
    private static final int TEXTURE_WIDTH = 64;

    public static void drawMateriaSlotIcon(MateriaSlotData slotData, GuiGraphics graphics, int pX, int pY, int size, boolean renderTransparent)
    {
        drawMateriaSlotIconWithTranslucency(slotData, graphics, pX, pY, size, renderTransparent);
    }

    public static void drawMateriaSlotIconWithTranslucency(MateriaSlotData slotData, GuiGraphics graphics, int pX, int pY, int size, boolean renderTransparent)
    {
        var iconScale = 0.4f;
        var iconOriginalSize = 64;
        var iconScaledSize = iconScale * iconOriginalSize;
        var halfSpriteHeightOrWidth = iconOriginalSize / 2f;

        graphics.pose().pushPose();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        graphics.pose().translate(pX, pY, 0f);
        graphics.pose().scale(iconScale, iconScale, 0f);
        graphics.pose().translate(-halfSpriteHeightOrWidth, -halfSpriteHeightOrWidth, 0.0d);



        graphics.blitSprite(slotData.spriteLocation(), 0, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        graphics.pose().popPose();
    }
}