package io.github.drmanganese.topaddons.elements.bloodmagic;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import io.github.drmanganese.topaddons.elements.ElementRenderHelper;

import WayofTime.bloodmagic.item.inventory.ItemInventory;
import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.network.NetworkTools;
import mcjty.theoneprobe.rendering.RenderHelper;

public class ElementNodeFilter implements IElement {

    private int id;

    private final String side;
    private final ItemStack inventoryOnSide;
    private final ItemStack filterStack;

    public ElementNodeFilter(int id, String side, ItemStack inventoryOnSide, ItemStack filterStack) {
        this.id = id;
        this.side = side;
        this.inventoryOnSide = inventoryOnSide;
        this.filterStack = filterStack;
    }

    public ElementNodeFilter(ByteBuf buf) {
        this.side = NetworkTools.readString(buf);
        this.inventoryOnSide = NetworkTools.readItemStack(buf);
        this.filterStack = NetworkTools.readItemStack(buf);
    }

    @Override
    public void render(int x, int y) {
        Minecraft mc = Minecraft.getMinecraft();
        ElementRenderHelper.drawBox(x, y, x + 20, y + 20, 0xffff3434, 0xffa21a1a, 0x33a21a1a);
        RenderHelper.renderItemStack(mc, mc.getRenderItem(), inventoryOnSide, x + 2, y + 2, "");
        RenderHelper.renderItemStack(mc, mc.getRenderItem(), filterStack, x + 19, y + 2, "");
        ElementRenderHelper.drawBox(x + 34, y, x + 182, y + 20, 0xffcf34ff, 0xff681aa2, 0x33681aa2);

        ItemInventory filterInv = new ItemInventory(filterStack, 9, "");
        int xOffset = 0;
        for (int i = 0; i < 9; i++) {
            if (filterInv.getStackInSlot(i) != null) {
                RenderHelper.renderItemStack(mc, mc.getRenderItem(), filterInv.getStackInSlot(i), x + 35 + xOffset + 1, y + 2, "");
                xOffset += 16;
            }
        }
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 21;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeString(buf, side);
        NetworkTools.writeItemStack(buf, inventoryOnSide);
        NetworkTools.writeItemStack(buf, filterStack);
    }

    @Override
    public int getID() {
        return id;
    }
}
