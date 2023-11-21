package com.xeno.materia.client.radial;

import net.minecraft.client.gui.GuiGraphics;

public interface IDrawCallback<T> {
	void accept(T objectToBeDrawn, GuiGraphics poseStack, int positionX, int positionY, int size, boolean renderTransparent);
}