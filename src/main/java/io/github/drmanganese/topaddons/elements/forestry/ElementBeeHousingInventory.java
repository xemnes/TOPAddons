package io.github.drmanganese.topaddons.elements.forestry;

import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import io.github.drmanganese.topaddons.elements.ElementRenderHelper;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.network.NetworkTools;
import net.minecraft.util.ResourceLocation;

import static mcjty.theoneprobe.rendering.RenderHelper.renderItemStack;

public class ElementBeeHousingInventory implements IElement {

    private int id;

    private final NonNullList<ItemStack> inventoryStacks;
    private boolean isApiary = false;

    public ElementBeeHousingInventory(int id, boolean isApiary, NonNullList<ItemStack> inventoryStacks) {
        this.id = id;
        this.inventoryStacks = inventoryStacks;
        this.isApiary = isApiary;
    }

    public ElementBeeHousingInventory(ByteBuf buf) {
        int slots = 9;
        if (buf.readBoolean()) {
            isApiary = true;
            slots = 12;
        }
        this.inventoryStacks = NonNullList.withSize(slots, ItemStack.EMPTY);
        for (int i = 0; i < slots; i++) {
            this.inventoryStacks.set(i, NetworkTools.readItemStack(buf));
        }
    }

    @Override
    public void render(int x, int y) {
        Minecraft minecraft = Minecraft.getMinecraft();
        ElementRenderHelper.drawBox(x + 9, y, x + 47, y + 20, 0xffd9b634, 0xff6d5b1a, 0x33d9b634);
        if (inventoryStacks.get(0).getItem() != Item.getItemFromBlock(Blocks.BARRIER))
            renderItemStack(minecraft, minecraft.getRenderItem(), inventoryStacks.get(0), x + 11, y + 2, "");
        if (inventoryStacks.get(1).getItem() != Item.getItemFromBlock(Blocks.BARRIER))
            renderItemStack(minecraft, minecraft.getRenderItem(), inventoryStacks.get(1), x + 29, y + 2, inventoryStacks.get(1).getCount() + "");

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        minecraft.getTextureManager().bindTexture(new ResourceLocation("theoneprobe", "textures/gui/sprites.png"));
        RenderHelper.drawTexturedModalRect(x + 1, y + 23, 203, 0, 53, 54);
        GlStateManager.disableBlend();

        for (int i = 2; i < 9; i++) {
            int xPos = x + 2;
            int yPos = y + 22;

            if (i < 4) {
                //X
                //X
                xPos += 2;
                yPos += 8 + 18 * (i - 1.8);
            } else if (i < 7) {
                // X
                // X
                // X
                xPos += 18;
                yPos += 18 * (i - 3.85);
            } else {
                //  X
                //  X
                xPos += 34;
                yPos += 8 + 18 * (i - 6.8);
            }


            if (inventoryStacks.get(i).getItem() != Item.getItemFromBlock(Blocks.BARRIER)) {
                renderItemStack(minecraft, minecraft.getRenderItem(), inventoryStacks.get(i), xPos, yPos, inventoryStacks.get(i).getCount() + "");
            }
        }

        if (isApiary) {
            ElementRenderHelper.drawBox(x + 58, y + 20, x + 78, y + 78, 0xffcf7551, 0xff683b28, 0x33cf7551);
            for (int i = 9; i < 12; i++) {
                if (inventoryStacks.get(i).getItem() != Item.getItemFromBlock(Blocks.BARRIER)) {
                    renderItemStack(minecraft, minecraft.getRenderItem(), inventoryStacks.get(i), x + 60, y + 22 + 19 * (i-9), "");
                }
            }
        }
    }

    @Override
    public int getWidth() {
        return isApiary ? 79 : 40;
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isApiary);
        for (ItemStack inventoryStack : inventoryStacks) {
            NetworkTools.writeItemStack(buf, inventoryStack);
        }
    }

    @Override
    public int getID() {
        return id;
    }
}
