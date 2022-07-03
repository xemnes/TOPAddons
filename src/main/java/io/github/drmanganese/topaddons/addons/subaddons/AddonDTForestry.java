package io.github.drmanganese.topaddons.addons.subaddons;

import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.google.common.collect.ImmutableSet;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IHiveTile;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.ITree;
import forestry.api.core.IErrorLogicSource;
import forestry.api.core.IErrorState;
import forestry.api.farming.FarmDirection;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IEntityButterfly;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.apiculture.multiblock.TileAlvearySwarmer;
import forestry.apiculture.tiles.TileApiary;
import forestry.apiculture.tiles.TileBeeHousingBase;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.arboriculture.tiles.TileSapling;
import forestry.arboriculture.tiles.TileTreeContainer;
import forestry.core.ModuleCore;
import forestry.core.blocks.BlockBogEarth;
import forestry.core.errors.EnumErrorCode;
import forestry.core.fluids.Fluids;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.tiles.TileEngine;
import forestry.core.tiles.TileForestry;
import forestry.core.utils.GeneticsUtil;
import forestry.energy.tiles.TileEngineBiogas;
import forestry.factory.tiles.TileFermenter;
import forestry.factory.tiles.TileMoistener;
import forestry.factory.tiles.TileRaintank;
import forestry.factory.tiles.TileStill;
import forestry.farming.tiles.TileFarm;
import io.github.drmanganese.topaddons.TOPAddons;
import io.github.drmanganese.topaddons.addons.AddonBlank;
import io.github.drmanganese.topaddons.api.TOPAddon;
import io.github.drmanganese.topaddons.elements.forestry.ElementBeeHousingInventory;
import io.github.drmanganese.topaddons.elements.forestry.ElementForestryFarm;
import io.github.drmanganese.topaddons.reference.Colors;
import io.github.drmanganese.topaddons.reference.Names;
import io.github.drmanganese.topaddons.styles.ProgressStyleForestryMultiColored;
import maxhyper.dynamictreesforestry.blocks.BlockDynamicLeavesFruit;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.config.ConfigSetup;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import java.util.Arrays;
import java.util.List;

import static maxhyper.dynamictreesforestry.ModContent.*;

@TOPAddon(dependency = "dynamictreesforestry")
public class AddonDTForestry extends AddonBlank {



    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {

              /*
             Dynamic Trees - Forestry Leaves ripeness
              */
        if (blockState.getBlock() instanceof BlockDynamicLeavesFruit) {
            int fruitAge = blockState.getValue(BlockDynamicLeaves.TREE);
            if (fruitAge == 1 || fruitAge == 2) {
                if (blockState.getBlock() == appleLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Apple" + " - " + TextFormatting.RED + "{*topaddons.forestry:unripe*}");
                }
                if (blockState.getBlock() == walnutLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Walnut" + " - " + TextFormatting.RED + "{*topaddons.forestry:unripe*}");
                }
                if (blockState.getBlock() == chestnutLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Chestnut" + " - " + TextFormatting.RED + "{*topaddons.forestry:unripe*}");
                }
                if (blockState.getBlock() == cherryLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Cherry" + " - " + TextFormatting.RED + "{*topaddons.forestry:unripe*}");
                }
                if (blockState.getBlock() == lemonLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Lemon" + " - " + TextFormatting.RED + "{*topaddons.forestry:unripe*}");
                }
                if (blockState.getBlock() == plumLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Plum" + " - " + TextFormatting.RED + "{*topaddons.forestry:unripe*}");
                }
            }
            if (fruitAge == 3) {
                if (blockState.getBlock() == appleLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Apple" + " - " + TextStyleClass.OK + "{*topaddons.forestry:ripe*}");
                }
                if (blockState.getBlock() == walnutLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Walnut" + " - " + TextStyleClass.OK + "{*topaddons.forestry:ripe*}");
                }
                if (blockState.getBlock() == chestnutLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Chestnut" + " - " + TextStyleClass.OK + "{*topaddons.forestry:ripe*}");
                }
                if (blockState.getBlock() == cherryLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Cherry" + " - " + TextStyleClass.OK + "{*topaddons.forestry:ripe*}");
                }
                if (blockState.getBlock() == lemonLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Lemon" + " - " + TextStyleClass.OK + "{*topaddons.forestry:ripe*}");
                }
                if (blockState.getBlock() == plumLeaves) {
                    textPrefixed(probeInfo, "{*topaddons.forestry:fruit*}", "Plum" + " - " + TextStyleClass.OK + "{*topaddons.forestry:ripe*}");
                }
            }
        }

    }
}
