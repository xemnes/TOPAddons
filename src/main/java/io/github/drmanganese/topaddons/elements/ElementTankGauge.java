package io.github.drmanganese.topaddons.elements;

import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import io.github.drmanganese.topaddons.styles.ProgressStyleTank;

import java.awt.*;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.apiimpl.client.ElementProgressRender;
import mcjty.theoneprobe.apiimpl.client.ElementTextRender;
import mcjty.theoneprobe.network.NetworkTools;
import mcjty.theoneprobe.rendering.RenderHelper;

import static io.github.drmanganese.topaddons.elements.ElementRenderHelper.drawSmallText;

public class ElementTankGauge implements IElement {

    private int id;

    private final String tankName, fluidName, suffix;
    private final int amount, capacity, color1, color2;
    private final boolean sneaking;

    public ElementTankGauge(int id, String tankName, String fluidName, int amount, int capacity, String suffix, int color1, boolean sneaking) {
        this.id = id;
        this.tankName = tankName;
        this.fluidName = fluidName;
        this.amount = amount;
        this.capacity = capacity;
        this.suffix = suffix;
        this.color1 = color1;
        this.color2 = new Color(this.color1).darker().hashCode();
        this.sneaking = sneaking;
    }

    public ElementTankGauge(ByteBuf buf) {
        this.tankName = NetworkTools.readString(buf);
        this.fluidName = NetworkTools.readString(buf);
        this.amount = buf.readInt();
        this.capacity = buf.readInt();
        this.suffix = NetworkTools.readString(buf);
        this.color1 = buf.readInt();
        this.color2 = new Color(this.color1).darker().hashCode();
        this.sneaking = buf.readBoolean();
    }

    @Override
    public void render(int x, int y) {
        if (capacity > 0) {
            ElementProgressRender.render(new ProgressStyleTank().filledColor(color1).alternateFilledColor(color2), amount, capacity, x, y, 100, 14);
        } else {
            ElementProgressRender.render(new ProgressStyleTank(), amount, capacity, x, y, 100, 14);
        }


            RenderHelper.renderText(Minecraft.getMinecraft(), x + 3, y + 3, (capacity > 0) ? ElementProgress.format(amount, amount > 9999 ? NumberFormat.COMPACT : NumberFormat.FULL, "") + "/" + ElementProgress.format(capacity, capacity > 9999 ? NumberFormat.COMPACT : NumberFormat.FULL, "") + " " + suffix : I18n.format("topaddons:tank_empty"), RenderHelper.renderColorToHSB(color1, 0.3f, 1.0f));
            drawSmallText(x + 99 - Minecraft.getMinecraft().fontRenderer.getStringWidth(fluidName) / 2, y + 15, fluidName, RenderHelper.renderBarTextColor(color1));


        drawSmallText(x + 1, y + 15, tankName, 0xffffffff);
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeString(buf, this.tankName);
        NetworkTools.writeString(buf, this.fluidName);
        buf.writeInt(this.amount);
        buf.writeInt(this.capacity);
        NetworkTools.writeString(buf, this.suffix);
        buf.writeInt(this.color1);
        buf.writeBoolean(sneaking);
    }

    @Override
    public int getID() {
        return id;
    }
}
