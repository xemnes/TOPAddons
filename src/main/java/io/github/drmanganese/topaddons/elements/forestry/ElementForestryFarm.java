package io.github.drmanganese.topaddons.elements.forestry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import io.github.drmanganese.topaddons.elements.ElementRenderHelper;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.network.NetworkTools;
import mcjty.theoneprobe.rendering.RenderHelper;

import static mcjty.theoneprobe.rendering.RenderHelper.renderItemStack;

public class ElementForestryFarm implements IElement {

    private int id;

    private final NonNullList<ItemStack> farmIcons;
    private String oneDirection;

    private NonNullList<ItemStack> inventoryStacks;

    public ElementForestryFarm(int id, NonNullList<ItemStack> farmIcons, String oneDirection, boolean renderInventory, NonNullList<ItemStack> inventoryStacks) {
        this.id = id;
        this.farmIcons = farmIcons;
        this.oneDirection = oneDirection;
        this.inventoryStacks = inventoryStacks;
    }

    public ElementForestryFarm(ByteBuf buf) {
        this.farmIcons = NonNullList.withSize(5, ItemStack.EMPTY);
        for (int i = 0; i < 5; i++) {
            this.farmIcons.set(i, NetworkTools.readItemStack(buf));
        }
        oneDirection = NetworkTools.readString(buf);
        if (buf.readBoolean()) {
            this.inventoryStacks = NonNullList.withSize(22, ItemStack.EMPTY);
            for (int i = 0; i < 22; i++) {
                this.inventoryStacks.set(i, NetworkTools.readItemStack(buf));
            }
        } else {
            this.inventoryStacks = NonNullList.create();
        }
    }

    @Override
    public void render(int x, int y) {
        Minecraft minecraft = Minecraft.getMinecraft();
        int centerX = x + 31;
        int centerY = y + 20;

        drawPlus(centerX - 22, centerY - 22, centerX + 38, centerY + 38);

        renderItemStack(minecraft, minecraft.getRenderItem(), farmIcons.get(4), centerX, centerY + 2, "");
        renderItemStack(minecraft, minecraft.getRenderItem(), farmIcons.get(0), centerX, centerY - 17, I18n.format("for.gui.solder." + oneDirection));
        renderItemStack(minecraft, minecraft.getRenderItem(), farmIcons.get(1), centerX + 19, centerY + 2, I18n.format("for.gui.solder." +nextDirection()));
        renderItemStack(minecraft, minecraft.getRenderItem(), farmIcons.get(2), centerX, centerY + 21, I18n.format("for.gui.solder." +nextDirection()));
        renderItemStack(minecraft, minecraft.getRenderItem(), farmIcons.get(3), centerX - 19, centerY + 2, I18n.format("for.gui.solder." +nextDirection()));
        nextDirection();

        if (this.inventoryStacks.size() > 0) {
            ElementRenderHelper.drawBox(x + 29, y + 70, x + 49, y + 90, 0xff349eff, 0xff1a4fa2, 0x33349eff);

            ElementRenderHelper.drawBox(x + 72, y + 1, x + 110, y + 57, 0xff349eff, 0xff1a4fa2, 0x33349eff);
            ElementRenderHelper.drawBox(x + 112, y + 1, x + 150, y + 57, 0xff349eff, 0xff1a4fa2, 0x33349eff);
            ElementRenderHelper.drawBox(x + 72, y + 61, x + 110, y + 99, 0xffffa834, 0xffa24715, 0x33ffa834);
            ElementRenderHelper.drawBox(x + 112, y + 61, x + 150, y + 99, 0xffffa834, 0xffa24715, 0x33ffa834);

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 2; k++) {
                        int xOffset = x + 2 + 4 * i + (2 * i + (k % 2)) * 18;
                        int yOffset = y + 62 + j * 18;
                        int slot = i * 6 + j * 2 + k;

                        if (!inventoryStacks.get(slot).isEmpty()) {
                            renderItemStack(minecraft, minecraft.getRenderItem(), inventoryStacks.get(slot), xOffset + 72, yOffset - 59, inventoryStacks.get(slot).getCount() + "");
                        }
                    }
                }
            }

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 2; k++) {
                        int xOffset = x + 2 + 4 * i + (2 * i + (k % 2)) * 18;
                        int yOffset = y + 122 + j * 18;
                        int slot = i * 4 + j * 2 + k + 12;

                        if (!inventoryStacks.get(slot).isEmpty()) {
                            renderItemStack(minecraft, minecraft.getRenderItem(), inventoryStacks.get(slot), xOffset + 72, yOffset - 59, inventoryStacks.get(slot).getCount() + "");
                        }
                    }
                }
            }

            if (!inventoryStacks.get(20).isEmpty()) {
                renderItemStack(minecraft, minecraft.getRenderItem(), inventoryStacks.get(20), x + 31, y + 72, inventoryStacks.get(20).getCount() + "");
            }
        }
    }

    @Override
    public int getWidth() {
        return (inventoryStacks.size() > 0) ? 155 : 60;
    }

    @Override
    public int getHeight() {
        return (inventoryStacks.size() > 0) ? 101 : 62;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        for (ItemStack farmIcon : farmIcons) {
            NetworkTools.writeItemStack(buf, farmIcon);
        }

        NetworkTools.writeString(buf, oneDirection);
        if (inventoryStacks.size() > 0) {
            buf.writeBoolean(true);
            for (ItemStack inventoryStack : inventoryStacks) {
                NetworkTools.writeItemStack(buf, inventoryStack);
            }
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public int getID() {
        return id;
    }

    private String nextDirection() {
        switch (this.oneDirection) {
            case "north":
                this.oneDirection = "east";
                return "east";
            case "east":
                this.oneDirection = "south";
                return "south";
            case "south":
                this.oneDirection = "west";
                return "west";
            case "west":
                this.oneDirection = "north";
                return "north";
            default:
                return this.oneDirection;
        }
    }

    private static void drawPlus(int x1, int y1, int x2, int y2) {
        Gui.drawRect(x1 + 21, y1 + 4, x2 - 21, y2 - 37, 0x3375d700);
        Gui.drawRect(x1 + 21, y1 + 41, x2 - 21, y2, 0x3375d700);
        Gui.drawRect(x1 + 2, y1 + 23, x2 - 2, y2 - 19, 0x3375d700);

        //TOP
        RenderHelper.drawHorizontalLine(x1 + 21, y1 + 3, x2 - 21, 0xff75d700);
        RenderHelper.drawVerticalGradientRect(x1 + 20, y1 + 3, x2 - 39, y2 - 37, 0xff75d700, 0xff63b400);
        RenderHelper.drawVerticalGradientRect(x2 - 21, y1 + 3, x2 - 20, y2 - 37, 0xff75d700, 0xff63b400);

        //RIGHT
        RenderHelper.drawHorizontalLine(x1 + 40, y1 + 22, x2 - 2, 0xff63b400);
        RenderHelper.drawVerticalGradientRect(x2 - 2, y1 + 22, x2 - 1, y2 - 18, 0xff63b400, 0xff4e8b00);
        RenderHelper.drawHorizontalLine(x1 + 40, y1 + 41, x2 - 2, 0xff4e8b00);

        //BOTTOM
        RenderHelper.drawVerticalGradientRect(x1 + 20, y1 + 41, x1 + 21, y1 + 61, 0xff4e8b00, 0xff3c6800);
        RenderHelper.drawVerticalGradientRect(x2 - 21, y1 + 41, x1 + 40, y1 + 61, 0xff4e8b00, 0xff3c6800);
        RenderHelper.drawHorizontalLine(x1 + 21, y1 + 60, x2 - 21, 0xff3c6800);

        //LEFT
        RenderHelper.drawHorizontalLine(x1 + 2, y1 + 22, x2 - 40, 0xff63b400);
        RenderHelper.drawVerticalGradientRect(x1 + 1, y1 + 22, x2 - 58, y2 - 18, 0xff63b400, 0xff4e8b00);
        RenderHelper.drawHorizontalLine(x1 + 2, y1 + 41, x2 - 40, 0xff4e8b00);
    }
}
