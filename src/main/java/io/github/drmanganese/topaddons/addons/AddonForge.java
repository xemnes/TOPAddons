package io.github.drmanganese.topaddons.addons;

import io.github.drmanganese.topaddons.TOPAddons;
import io.github.drmanganese.topaddons.api.TOPAddon;
import io.github.drmanganese.topaddons.elements.ElementTankGauge;
import io.github.drmanganese.topaddons.reference.Colors;
import io.github.drmanganese.topaddons.reference.Names;

import mcjty.theoneprobe.config.ConfigSetup;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeConfig.ConfigMode;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;

import java.awt.Color;

@TOPAddon(dependency = "forge", fancyName = "Base", order = 1)
public class AddonForge extends AddonBlank {

    public static IProbeInfo addTankElement(IProbeInfo probeInfo, String name, String fluidName, int amount, int capacity, String suffix, int color, ProbeMode mode, EntityPlayer player) {
        return probeInfo.element(new ElementTankGauge(getElementId(player, "tank_gauge"), name, fluidName, amount, capacity, suffix, color, mode == ProbeMode.EXTENDED));
    }


    @SuppressWarnings("UnusedReturnValue")
    public static IProbeInfo addTankElement(IProbeInfo probeInfo, Class<? extends TileEntity> clazz, FluidTank fluidTank, int i, ProbeMode mode, EntityPlayer player) {
        String tankName = "Tank";
        if (Names.tankNamesMap.containsKey(clazz)) {
            tankName = Names.tankNamesMap.get(clazz)[i];
        }

        if (fluidTank.getFluid() != null) {
            return addTankElement(probeInfo, tankName, fluidTank.getFluid().getLocalizedName(), fluidTank.getFluidAmount(), fluidTank.getCapacity(), "mB", Colors.getHashFromFluid(fluidTank.getFluid()), mode, player);
        } else {
            return addTankElement(probeInfo, tankName, "", 0, 0, "", 0xff777777, mode, player);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static IProbeInfo addTankElement(IProbeInfo probeInfo, String name, FluidTankInfo tankInfo, ProbeMode mode, EntityPlayer player) {
        String suffix = "mB";
        if (name.equals("Blood Altar")) suffix = "LP";

        if (tankInfo.fluid != null) {
            return addTankElement(probeInfo, name, tankInfo.fluid.getLocalizedName(), tankInfo.fluid.amount, tankInfo.capacity, suffix, Colors.getHashFromFluid(tankInfo.fluid), mode, player);
        } else {
            return addTankElement(probeInfo, name, "", 0, 0, "", 0xff777777, mode, player);
        }
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (!showTOPAddonsTank(player))
            return;

        /* Disable for enderio, endertanks */
        String modid = ForgeRegistries.BLOCKS.getKey(blockState.getBlock()).getNamespace();
        if (modid.equals("enderio") || modid.equals("endertanks"))
            return;

        TileEntity tile = world.getTileEntity(data.getPos());

        if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            IFluidHandler capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            IFluidTankProperties[] tanks;
            try {
                tanks = capability.getTankProperties();
            } catch (NullPointerException ignored) {
                return;
            }

            for (int i = 0; i < tanks.length; i++) {
                IFluidTankProperties tank = tanks[i];
                String tankName = "Tank";
                if (Names.tankNamesMap.containsKey(tile.getClass())) {
                    tankName = Names.tankNamesMap.get(tile.getClass())[i];
                }

                if (tank.getContents() != null) {
                    addTankElement(probeInfo, tankName, tank.getContents().getFluid().getLocalizedName(tank.getContents()), tank.getContents().amount, tank.getCapacity(), "mB", Colors.getHashFromFluid(tank), mode, player);
                } else {
                    addTankElement(probeInfo, tankName, "", 0, 0, "", 0xff777777, mode, player);
                }
            }

        }
    }

    @Override
    public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (!showTOPTank(config, player)) {
            config.showTankSetting(ConfigMode.NOT);
        }
    }

    @Override
    public void registerElements() {
        registerElement("tank_gauge", ElementTankGauge::new);
    }

    @Override
    public void addFluidColors() {
        Colors.FLUID_NAME_COLOR_MAP.put(FluidRegistry.WATER.getName(), new Color(52, 95, 218).hashCode());
        Colors.FLUID_NAME_COLOR_MAP.put(FluidRegistry.LAVA.getName(), new Color(230, 145, 60).hashCode());

        //enderio
        Colors.FLUID_NAME_COLOR_MAP.put("xpjuice", 0xffb4ff00);
        Colors.FLUID_NAME_COLOR_MAP.put("nutrient_distillation", 0xff75821c);
        Colors.FLUID_NAME_COLOR_MAP.put("vapor_of_levity", 0xff4d857f);
        Colors.FLUID_NAME_COLOR_MAP.put("liquid_sunshine", 0xffd6c31e);
        Colors.FLUID_NAME_COLOR_MAP.put("fire_water", 0xff9e622b);
        Colors.FLUID_NAME_COLOR_MAP.put("hootch", 0xffd6d6d6);
        Colors.FLUID_NAME_COLOR_MAP.put("ender_distillation", 0xff23a34a);
        Colors.FLUID_NAME_COLOR_MAP.put("rocket_fuel", 0xff929270);
        Colors.FLUID_NAME_COLOR_MAP.put("cloud_seed", 0xff33898d);
        Colors.FLUID_NAME_COLOR_MAP.put("cloud_seed_concentrated", 0xff566c67);

        //integrateddynamics
        Colors.FLUID_NAME_COLOR_MAP.put("menrilresin", 0xff7bdbf6);
    }

    private boolean showTOPTank(IProbeConfig config, EntityPlayer player) {
        return player.getCapability(TOPAddons.OPTS_CAP, null).getInt("fluidGaugeDisplay") != 1
                && config.getShowTankSetting() != ConfigMode.NOT;
    }

    private boolean showTOPAddonsTank(EntityPlayer player) {
        return player.getCapability(TOPAddons.OPTS_CAP, null).getInt("fluidGaugeDisplay") != 0;
    }
}
