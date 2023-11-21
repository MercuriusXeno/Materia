package com.xeno.materia.client.toast;

import com.xeno.materia.common.MateriaEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.util.FormattedCharSequence;

public class MateriaPickupToast implements Toast
{
    private static final long TIME_VISIBLE = 500;
    private static final int TITLE_COLOR = 0xFF500050;
    private static final int TEXT_COLOR = 0xFF000000;
    // private final List<FormattedCharSequence> lines;
    private final int height;
    private final MateriaEnum type;
    private final String display;

    public MateriaPickupToast(MateriaEnum type, long amount, long limit) {
        this.type = type;

        var minecraft = Minecraft.getInstance();
        var font = minecraft.font;

        var formattedAmount = type.getToast(amount, limit);
        this.display = formattedAmount;
        height = Toast.super.height() + font.lineHeight;
    }

    @Override
    public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        var minecraft = Minecraft.getInstance();
        var font = minecraft.font;

        // stretch the middle
        guiGraphics.blit(TEXTURE, 0, 0, 0, 32, this.width(), 8);
        int middleHeight = height - 16;
        for (var middleY = 0; middleY < middleHeight; middleY += 16) {
            var tileHeight = Math.min(middleHeight - middleY, 16);
            guiGraphics.blit(TEXTURE, 0, 8 + middleY, 0, 32 + 8, this.width(), tileHeight);
        }
        guiGraphics.blit(TEXTURE, 0, height - 8, 0, 32 + 32 - 8, this.width(), 8);
        var lineY = 18;
        guiGraphics.drawString(toastComponent.getMinecraft().font, display, 30, lineY, TEXT_COLOR, false);

        return timeSinceLastVisible >= TIME_VISIBLE ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    @Override
    public int height() {
        return height;
    }
}
