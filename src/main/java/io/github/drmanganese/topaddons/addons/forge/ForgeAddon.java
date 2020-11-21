package io.github.drmanganese.topaddons.addons.forge;

import io.github.drmanganese.topaddons.addons.TopAddon;
import io.github.drmanganese.topaddons.addons.forge.tiles.FluidHandlerTileInfo;
import io.github.drmanganese.topaddons.api.*;
import io.github.drmanganese.topaddons.capabilities.ElementSync;
import io.github.drmanganese.topaddons.elements.forge.FluidGaugeElement;
import io.github.drmanganese.topaddons.config.ColorValue;
import io.github.drmanganese.topaddons.config.Config;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import mcjty.theoneprobe.api.ITheOneProbe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ForgeAddon extends TopAddon implements IAddonBlocks, IAddonElements, IAddonConfig, IAddonConfigProviders {

    public static final String GAUGE_ELEMENT_ID = "fluid_gauge";

    // Client
    public static ForgeConfigSpec.EnumValue<FluidColorAlgorithm> gaugeFluidColorAlgorithm;
    public static ForgeConfigSpec.IntValue gaugeFluidColorTransparency;
    public static ForgeConfigSpec.BooleanValue gaugeShowCapacity;
    public static ForgeConfigSpec.BooleanValue gaugeRounded;
    public static ColorValue gaugeBackgroundColor;
    public static ColorValue gaugeBorderColor;

    // Synced
    public static ForgeConfigSpec.EnumValue<FluidGaugeChoice> fluidGaugeChoice;
    public static ForgeConfigSpec.BooleanValue gaugeUseCustomTankNames;
    public static ColorValue machineProgressBackgroundColor;
    public static ColorValue machineProgressBorderColor;

    // Common
    public static ForgeConfigSpec.ConfigValue<List<String>> gaugeModBlacklist;

    public ForgeAddon() {
        super("forge");
    }

    @Override
    public void registerElements(ITheOneProbe probe) {
        ElementSync.registerElement(probe, GAUGE_ELEMENT_ID, FluidGaugeElement::new);
    }

    @Nonnull
    @Override
    public ImmutableMultimap<Class<? extends TileEntity>, ITileInfo> getTileInfos() {
        return ImmutableMultimap.of(TileEntity.class, FluidHandlerTileInfo.INSTANCE);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder, ModConfig.Type type) {
        builder.push(name);
        if (type == ModConfig.Type.CLIENT) {
            builder.push("fluidGauge");
            gaugeRounded = builder.comment("Show a rounded tank fluid gauge").define("gaugeRounded", true);
            gaugeBackgroundColor = new ColorValue(builder.comment("Fluid gauge background color (try #557F0000 for BC red)").define("gaugeBackgroundColor", "#55666666", ColorValue::test));
            gaugeBorderColor = new ColorValue(builder.comment("Fluid gauge border color (try #FF7F0000 for BC red)").define("gaugeBorderColor", "#ff666666", ColorValue::test));
            gaugeUseCustomTankNames = builder.comment("Allow certain tiles to show custom tank names").define("gaugeUseCustomTankNames", true);
            gaugeFluidColorAlgorithm = builder.comment("Which \"algorithm\" should be used to pick fluid colors (TOP_LEFT is sometimes lighter)").defineEnum("gaugeFluidColorAlgorithm", FluidColorAlgorithm.AVERAGE_COLOR);
            fluidGaugeChoice = builder.comment("Which fluid gauges to show, BOTH and THE_ONE_PROBE_ONLY options also depend on the The One Probe \"showTankSetting\" configuration").defineEnum("fluidGaugeChoice", FluidGaugeChoice.TOP_ADDONS_ONLY);
            gaugeShowCapacity = builder.comment("Show the tank's total capacity in the fluid gauge").define("gaugeShowCapacity", true);
            gaugeFluidColorTransparency = builder.comment("Fluid color transparency.").defineInRange("gaugeFluidColorTransparency", 255, 0, 255);
            builder.pop();
            machineProgressBackgroundColor = new ColorValue(builder.comment("Machine progress bar background color").define("machineProgressBackgroundColor", "#55363636", ColorValue::test));
            machineProgressBorderColor = new ColorValue(builder.comment("Machine progress bar border color").define("machineProgressBorderColor", "#ff969696", ColorValue::test));
        }

        if (type == ModConfig.Type.COMMON)
            gaugeModBlacklist = builder.comment("List of mod IDs for which no TOP Addons fluid gauge should be shown").define("gaugeModBlacklist", new ArrayList<>());
        builder.pop();
    }

    @Override
    public List<ForgeConfigSpec.ConfigValue<?>> getClientConfigValuesToSync() {
        return Lists.newArrayList(
            fluidGaugeChoice,
            machineProgressBackgroundColor.configValue,
            machineProgressBorderColor.configValue,
            gaugeUseCustomTankNames
        );
    }

    @Override
    @Nonnull
    public ImmutableMap<Object, ITileConfigProvider> getBlockConfigProviders() {
        return ImmutableMap.of(TileEntity.class, FluidHandlerTileInfo.INSTANCE);
    }

    public enum FluidGaugeChoice {
        BOTH(false, false),
        THE_ONE_PROBE_ONLY(false, true),
        TOP_ADDONS_ONLY(true, false);

        public final boolean hideOriginal;
        public final boolean hideTopAddonsGauge;

        FluidGaugeChoice(boolean hideOriginal, boolean hideTopAddonsGauge) {
            this.hideOriginal = hideOriginal;
            this.hideTopAddonsGauge = hideTopAddonsGauge;
        }

        public static FluidGaugeChoice getSyncedValueFor(PlayerEntity player) {
            return (FluidGaugeChoice) Config.getSyncedEnum(player, ForgeAddon.fluidGaugeChoice);
        }
    }
    
    public enum FluidColorAlgorithm {
        TOP_LEFT_COLOR,
        AVERAGE_COLOR;
    }
}
