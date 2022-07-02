package io.github.drmanganese.topaddons.addons.subaddons.binniesmods;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import io.github.drmanganese.topaddons.addons.AddonBlank;
import io.github.drmanganese.topaddons.styles.ProgressStyleExtraTreesMachine;

import java.util.Collections;
import java.util.List;

import binnie.core.machines.Machine;
import binnie.core.machines.TileEntityMachine;
import binnie.core.machines.power.ComponentProcess;
import binnie.extratrees.machines.lumbermill.LumbermillLogic;
import com.google.common.collect.Lists;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IBlockDisplayOverride;
import mcjty.theoneprobe.api.IProbeConfig;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.config.ConfigSetup;
import static mcjty.theoneprobe.api.TextStyleClass.*;

public class SubAddonTrees extends AddonBlank {

    @ObjectHolder("extratrees:hops")
    private static final BlockCrops HOPS = null;

    private static final List<Class<? extends ComponentProcess>> machineProcesses = Lists.newArrayList(
            LumbermillLogic.class
    );

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity tile = world.getTileEntity(data.getPos());

        if (tile instanceof TileEntityMachine) {
            Machine machine = ((TileEntityMachine) tile).getMachine();

            if (machine != null) {
                for (Class<? extends ComponentProcess> process : machineProcesses) {
                    if (machine.hasComponent(process)) {
                        ComponentProcess logic = machine.getComponent(process);
                        if (logic.isInProgress()) {
                            probeInfo.progress(Math.round(logic.getProgress()), 100, new ProgressStyleExtraTreesMachine(process));
                        }

                        break;
                    }
                }
            }
        }
    }

    @Override
    public List<IBlockDisplayOverride> getBlockDisplayOverrides() {
        return Collections.singletonList((mode, probeInfo, player, world, blockState, data) -> {
            //noinspection ConstantConditions

            return false;
        });
    }
}
