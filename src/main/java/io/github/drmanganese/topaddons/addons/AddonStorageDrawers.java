package io.github.drmanganese.topaddons.addons;

import io.github.drmanganese.topaddons.api.TOPAddon;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import mcjty.theoneprobe.config.ConfigSetup;

import java.util.Collections;
import java.util.List;

import static mcjty.theoneprobe.api.TextStyleClass.MODNAME;

@TOPAddon(dependency = "storagedrawers")
public class AddonStorageDrawers extends AddonBlank {

    @GameRegistry.ObjectHolder("theoneprobe:probe")
    private static final Item PROBE = null;

    private boolean replaceDrawers = true;

    @Override
    public void updateConfigs(Configuration config) {
        replaceDrawers = config.get("storagedrawers", "replaceDrawers", true, "Replace Storage Drawers default extended info.").setLanguageKey("topaddons.config:storagedrawers_extended").getBoolean();
    }

//    @Override
//    public List<IBlockDisplayOverride> getBlockDisplayOverrides() {
//        return Collections.singletonList(new IBlockDisplayOverride() {
//            @Override
//            public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
//                if (blockState.getBlock() == Blocks.END_PORTAL) {
//                    if (Tools.show(mode, ConfigSetup.getRealConfig().getShowModName())) {
//                        probeInfo.horizontal()
//                                .vertical()
//                                .text(Blocks.END_PORTAL_FRAME.getLocalizedName())
//                                .text(MODNAME + Tools.getModName(Blocks.END_PORTAL));
//                    } else {
//                        probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
//                                .text(Blocks.END_PORTAL_FRAME.getLocalizedName());
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
//    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (world.getTileEntity(data.getPos()) instanceof TileEntityDrawers) {
            TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(data.getPos());

            if (tile.getDrawerAttributes().isConcealed()) {
                probeInfo.text(TextStyleClass.LABEL + "{*topaddons.storage_drawers:shrouded*}");
                return;
            }


            if (mode == ProbeMode.EXTENDED && replaceDrawers) {
                NonNullList<ItemStack> stacks = NonNullList.create();
                for (int i = 0; i < tile.getGroup().getDrawerCount(); i++) {
                    ItemStack stack = tile.getGroup().getDrawer(i).getStoredItemPrototype().copy();
                    if (!stack.isEmpty()) {
                        stack.setCount(tile.getGroup().getDrawer(i).getStoredItemCount());
                        stacks.add(stack);
                    }
                }

                if (stacks.size() > 0) {
                    IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(ConfigSetup.chestContentsBorderColor).spacing(0));
                    for (ItemStack stack : stacks) {
                        if (tile.getDrawerAttributes().isUnlimitedVending()) {
                            ItemStack infiStack = stack.copy();
                            infiStack.setCount(1);
                            vertical.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                                    .item(infiStack)
                                    .vertical(probeInfo.defaultLayoutStyle().spacing(0))
                                    .itemLabel(stack)
                                    .text(TextStyleClass.INFOIMP + "[\u221e]");

                        } else if (!stack.isEmpty()) {
                            int mss = stack.getMaxStackSize();
                            int r = stack.getCount() % mss;
                            int q = (stack.getCount() - r) / mss;
                            vertical.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER).spacing(5))
                                    .item(stack)
                                    .vertical(probeInfo.defaultLayoutStyle().spacing(0))
                                    .text(stack.getDisplayName() + " ")
                                    .text(TextStyleClass.LABEL + "[" + (stack.getCount() >= mss ? q + "x" + mss + " + " : "") + r + "]" + " ");
                        }
                    }
                }

                textPrefixed(probeInfo, "{*storagedrawers.waila.config.displayStackLimit*}", tile.getDrawerAttributes().isUnlimitedStorage() ? "\u221e" : tile.getDrawerCapacity() * tile.upgrades().getStorageMultiplier() + " (x" + tile.upgrades().getStorageMultiplier() + ")");
                if (tile.getOwner() != null && tile.getOwner().compareTo(player.getUniqueID()) != 0) {
                    probeInfo.text(TextStyleClass.ERROR + "{*storagedrawers.waila.protected*}");
                }
            }
        }

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (world.getTileEntity(data.getPos()) instanceof TileEntityDrawers) {
            if (replaceDrawers && player.isSneaking() && !(ConfigSetup.needsProbe == ConfigSetup.PROBE_NEEDEDFOREXTENDED) || ConfigSetup.extendedInMain) {
                config.showChestContents(IProbeConfig.ConfigMode.NOT);
            } else {
                config.showChestContents(IProbeConfig.ConfigMode.EXTENDED);
            }
        }
    }
}
