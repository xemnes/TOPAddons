package io.github.drmanganese.topaddons.elements.tconstruct;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import io.github.drmanganese.topaddons.elements.ElementRenderHelper;
import io.github.drmanganese.topaddons.reference.Colors;
import io.github.drmanganese.topaddons.styles.ProgressStyleSmelteryFluid;
import io.github.drmanganese.topaddons.styles.ProgressStyleTank;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.apiimpl.client.ElementProgressRender;
import mcjty.theoneprobe.network.NetworkTools;
import mcjty.theoneprobe.rendering.RenderHelper;

import static io.github.drmanganese.topaddons.elements.ElementRenderHelper.drawSmallText;

public class ElementSmelteryTank implements IElement {

    private int id;

    private final List<SmelteryFluid> fluids;
    private final int capacity;
    private final boolean inIngots;
    private final boolean sneaking;

    private static final ResourceLocation SPRITES = new ResourceLocation("theoneprobe", "textures/gui/sprites.png");

    private final int gaugeHeight;

    public ElementSmelteryTank(int id, List<FluidStack> fluids, int capacity, boolean inIngots, boolean sneaking) {
        this.id = id;
        this.fluids = new ArrayList<>();
        fluids.forEach(fluidStack -> this.fluids.add(new SmelteryFluid(fluidStack.getLocalizedName(), fluidStack.amount, Colors.getHashFromFluid(fluidStack), capacity)));
        this.capacity = capacity;
        this.inIngots = inIngots;
        this.sneaking = sneaking;

        gaugeHeight = calcGaugeHeight();
    }

    public ElementSmelteryTank(ByteBuf buf) {
        this.fluids = new ArrayList<>();
        int count = buf.readInt();
        this.capacity = buf.readInt();
        for (int i = 0; i < count; i++) {
            this.fluids.add(new SmelteryFluid(NetworkTools.readString(buf), buf.readInt(), buf.readInt(), this.capacity));
        }
        this.inIngots = buf.readBoolean();
        this.sneaking = buf.readBoolean();
        gaugeHeight = calcGaugeHeight();
    }

    private int calcGaugeHeight() {
        int sum = 0;
        for (SmelteryFluid fluid : this.fluids) {
            sum += fluid.height;
        }

        return Math.max(sum, 100);
    }

    @Override
    public void render(int x, int y) {
        if (!sneaking) {
            ElementProgressRender.render(new ProgressStyleTank(), 0, capacity, x, y, 100, 14);
            int xOffset = 0;
            for (int i = 0; i < fluids.size(); i++) {
                SmelteryFluid fluid = fluids.get(i);
                if (i > 0) {
                    xOffset += Math.ceil(100 * fluids.get(i - 1).amount / capacity);
                }
                ElementProgressRender.render(new ProgressStyleSmelteryFluid().backgroundColor(0x00ffffff).filledColor(fluid.color).alternateFilledColor(fluid.darker).showText(false).renderBG(false), fluid.amount, capacity, x + xOffset, y, 100, 14);
            }
//            for (int i = 1; i < 10; i++) {
////                RenderHelper.drawVerticalLine(x + i * 10, y + 1, y + (i == 5 ? 11 : 6), 0xff767676);
//            }
//            RenderHelper.drawVerticalLine(x + 99, y, y + 12, 0xff969696);
        } else {
//            ElementRenderHelper.drawGreyBox(x, y, x + 100, y + gaugeHeight + 2);
            RenderHelper.drawThickBeveledBoxGradient(x, y, x + 100, y + gaugeHeight + 2, 1, 0xff5000FF, 0xff28007F, 0xff000000);

            int yOffset = 0;

            for (int i = 0; i < fluids.size(); i++) {
                SmelteryFluid fluid = fluids.get(i);
                if (i > 0) {
                    yOffset += fluids.get(i - 1).height;
                }

//                Minecraft.getMinecraft().getTextureManager().bindTexture(SPRITES);
//                RenderHelper.renderColor(fluid.color);
                drawVerticalProgress(x, y - yOffset, fluid.amount, capacity, fluid.color, fluid.darker, fluid.height);
//                RenderHelper.drawTexturedModalRect(x + 1, y + 1, 4, 59, 98, fluid.height);

                String unit;
                int unitAmount = fluid.name.equals("Molten Emerald") ? 666 : 144;
                if (inIngots && fluid.amount >= unitAmount ) {
                    String type = fluid.name.equals("Molten Emerald") ? "gem" : "ingot";
                    final int r = fluid.amount % unitAmount;
                    final int amount = (fluid.amount - r) / unitAmount;
                    if (amount > 1) type += 's';
                    unit = amount + " " + type + " of " + fluid.name;
                    if (r > 0) {
                        unit = '>' + unit;
                    }
                } else {
                    unit = fluid.amount + "mB of " + fluid.name;
                }
                //noinspection MethodCallSideOnly
//                drawSmallText(x + 98 - Minecraft.getMinecraft().fontRenderer.getStringWidth(unit) / 2, y + gaugeHeight - 4 - yOffset, unit, fluid.brighter2);
                RenderHelper.renderSmallText(Minecraft.getMinecraft(), x + 98 - Minecraft.getMinecraft().fontRenderer.getStringWidth(unit) / 2, y + gaugeHeight - 4 - yOffset, unit, RenderHelper.renderColorToHSB(fluid.color, 0.3f, 1.0f));
            }

        }

        drawSmallText(x + 1, y + (sneaking ? gaugeHeight + 4 : 15), "" + "Smeltery", 0xffffffff);
        drawSmallText(x + 99 - Minecraft.getMinecraft().fontRenderer.getStringWidth("Sneak for Detailed View") / 2, y + 15, (sneaking ? "" : "Sneak for Detailed View"), 0xffffffff);
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return sneaking ? gaugeHeight + 8 : 20;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(fluids.size());
        buf.writeInt(this.capacity);
        fluids.forEach(fluid -> {
            NetworkTools.writeString(buf, fluid.name);
            buf.writeInt(fluid.amount);
            buf.writeInt(fluid.color);
        });
        buf.writeBoolean(this.inIngots);
        buf.writeBoolean(this.sneaking);
    }

    private void drawVerticalProgress(int x, int y, int amount, int capacity, int color, int dcolor, int height) {
        for (int i = 0; i < height; i++) {
            RenderHelper.drawHorizontalLine(x + 1, y + gaugeHeight - i, x + 99, (y + gaugeHeight - 2 - i) % 2 == 0 ? dcolor : color);
        }
    }

    @Override
    public int getID() {
        return id;
    }

    private final class SmelteryFluid {

        final String name;
        final int amount;
        final int color;
        final int darker;
        final int brighter2;

        int height;
        boolean wasTooShort = false;

        SmelteryFluid(String name, int amount, int color, int capacity) {
            this.name = name;
            this.amount = amount;
            this.color = color;
            this.darker = new Color(color, true).darker().hashCode();
            this.brighter2 = new Color(color, true).brighter().brighter().hashCode();

            height = (int) Math.floor(amount * 100 / capacity);
            if (height < 5) {
                height = 5;
                wasTooShort = true;
            }
        }
    }
}
