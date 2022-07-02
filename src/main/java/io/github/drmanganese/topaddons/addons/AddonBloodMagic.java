package io.github.drmanganese.topaddons.addons;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import io.github.drmanganese.topaddons.api.TOPAddon;
import io.github.drmanganese.topaddons.elements.ElementRenderHelper;
import io.github.drmanganese.topaddons.elements.bloodmagic.ElementAltarCrafting;
import io.github.drmanganese.topaddons.elements.bloodmagic.ElementNodeFilter;
import io.github.drmanganese.topaddons.reference.Names;

import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.config.ConfigSetup;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import WayofTime.bloodmagic.altar.BloodAltar;
import WayofTime.bloodmagic.altar.IBloodAltar;
import WayofTime.bloodmagic.api.impl.recipe.RecipeBloodAltar;
import WayofTime.bloodmagic.core.data.Binding;
import WayofTime.bloodmagic.core.data.SoulNetwork;
import WayofTime.bloodmagic.iface.IBindable;
import WayofTime.bloodmagic.item.ItemBloodOrb;
import WayofTime.bloodmagic.item.sigil.ItemSigilBase;
import WayofTime.bloodmagic.item.sigil.ItemSigilHolding;
import WayofTime.bloodmagic.orb.IBloodOrb;
import WayofTime.bloodmagic.routing.IMasterRoutingNode;
import WayofTime.bloodmagic.tile.TileAltar;
import WayofTime.bloodmagic.tile.TileIncenseAltar;
import WayofTime.bloodmagic.tile.TileMimic;
import WayofTime.bloodmagic.tile.routing.TileFilteredRoutingNode;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import WayofTime.bloodmagic.util.helper.NumeralHelper;
import com.google.common.collect.Lists;
import mcjty.theoneprobe.Tools;

import java.util.List;

import static mcjty.theoneprobe.api.TextStyleClass.MODNAME;

@TOPAddon(dependency = "bloodmagic")
public class AddonBloodMagic extends AddonBlank {

    @ObjectHolder("bloodmagic:mimic")
    public static Block MIMIC;
    @ObjectHolder("bloodmagic:sigil_holding")
    public static Item SIGIL_HOLDING;
    @ObjectHolder("bloodmagic:sigil_seer")
    public static Item SIGIL_SEER;
    @ObjectHolder("bloodmagic:sigil_divination")
    public static Item SIGIL_DIVINATION;
    @ObjectHolder("bloodmagic:blood_orb")
    public static Item BLOOD_ORB;
    private boolean requireSigil = true;
    private boolean seeMimickWithSigil = true;

    @Override
    public void updateConfigs(Configuration config) {
        requireSigil = config.get("bloodmagic", "requireSigil", true, "Is holding a divination sigil required to see certain information.").setLanguageKey("topaddons.config:bloodmagic_sigil").getBoolean();
        seeMimickWithSigil = config.get("bloodmagic", "seeMimickWithSigil", true, "Shows the player that they're looking at a mimick block when holding a seer sigil.").setLanguageKey("topaddons.config:bloodmagic_mimick_sigil").getBoolean();
    }

    @Override
    public void registerElements() {
        registerElement("filter_node", ElementNodeFilter::new);
        registerElement("altar_crafting", ElementAltarCrafting::new);
    }

    @Override
    public void addTankNames() {
        Names.tankNamesMap.put(TileAltar.class, new String[]{"Blood Altar"});
    }

    @Override
    public List<IBlockDisplayOverride> getBlockDisplayOverrides() {
        return Lists.newArrayList((IBlockDisplayOverride) (mode, probeInfo, player, world, blockState, data) -> {
            /*
             * Show the mimic block's "mimicked" block when it has an ItemBlock in its
             * internal inventory.
             */
            if (blockState.getBlock() == MIMIC) {
                ItemStack mimicStack = ((TileMimic) world.getTileEntity(data.getPos())).getStackInSlot(0);

                if (!mimicStack.isEmpty() && mimicStack.getItem() instanceof ItemBlock) {
                    if (Tools.show(mode, mcjty.theoneprobe.config.ConfigSetup.getRealConfig().getShowModName())) {
                        probeInfo.horizontal()
                                .item(mimicStack)
                                .vertical()
                                .itemLabel(mimicStack)
                                .text(MODNAME + Tools.getModName(((ItemBlock) mimicStack.getItem()).getBlock()));
                    } else {
                        probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                                .item(mimicStack)
                                .itemLabel(mimicStack);
                    }

                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        boolean holdingSeer = !requireSigil || holdingSigil(player, (ItemSigilBase) SIGIL_SEER);
        boolean holdingDivine = holdingSeer || !requireSigil || holdingSigil(player, (ItemSigilBase) SIGIL_DIVINATION);

        TileEntity tile = world.getTileEntity(data.getPos());

        if (tile instanceof IBloodAltar && holdingDivine) {
            IBloodAltar altar = (IBloodAltar) tile;
            textPrefixed(probeInfo, "{*topaddons.bloodmagic:tier*}", NumeralHelper.toRoman(altar.getTier().toInt()), TextFormatting.RED);

            if (altar instanceof TileAltar && holdingSeer) {
                ItemStack input = ((TileAltar) altar).getStackInSlot(0);
                if (input.isEmpty()) return;
                BloodAltar bloodAltar = ReflectionHelper.getPrivateValue(TileAltar.class, (TileAltar) altar, "bloodAltar");

                if (input.getItem() instanceof IBloodOrb) {
                    Binding binding = ((IBindable) input.getItem()).getBinding(input);
                    if (binding != null) {
                        SoulNetwork network = NetworkHelper.getSoulNetwork(binding);
                        addAltarCraftingElement(probeInfo, input, ItemStack.EMPTY, network.getCurrentEssence(), ((ItemBloodOrb) BLOOD_ORB).getOrb(input).getCapacity(), 0, player);
                    } else {
                        probeInfo.text(TextStyleClass.WARNING + "{*topaddons.bloodmagic:unbound_orb*}");
                    }
                } else if (altar.isActive()) {
                    ItemStack result = ((RecipeBloodAltar) ReflectionHelper.getPrivateValue(BloodAltar.class, bloodAltar, "recipe")).getOutput();
                    if (!result.isEmpty()) {
                        addAltarCraftingElement(probeInfo, input, result, bloodAltar.getProgress(), bloodAltar.getLiquidRequired(), bloodAltar.getConsumptionRate(), player);
                    }
                }
            }
            else if (tile instanceof IBloodAltar && !holdingSeer) {
                probeInfo.textSmall(TextFormatting.GRAY + "More info: Held Seer's Sigil required");
            }
        }
        else if (tile instanceof IBloodAltar && !holdingDivine) {
            probeInfo.textSmall(TextFormatting.GRAY + "More info: Held Sigil required");
        }

        if (tile instanceof TileFilteredRoutingNode && !(tile instanceof IMasterRoutingNode)) {
            TileFilteredRoutingNode node = (TileFilteredRoutingNode) tile;
            ItemStack filterStack = node.getFilterStack(data.getSideHit());
            if (!filterStack.isEmpty()) {
                BlockPos sidePos = data.getPos().offset(data.getSideHit());
                if (world.getTileEntity(sidePos) != null) {
                    IBlockState sideState = world.getBlockState(sidePos);
                    ItemStack inventoryOnSide = sideState.getBlock().getPickBlock(sideState, new RayTraceResult(data.getHitVec(), data.getSideHit().getOpposite(), sidePos), world, sidePos, player);
                    addFilterElement(probeInfo, data.getSideHit().getName(), inventoryOnSide, filterStack, player);
                }
            }
        }

        if (tile instanceof TileIncenseAltar && holdingDivine) {
            TileIncenseAltar altar = (TileIncenseAltar) tile;
            textPrefixed(probeInfo, "{*topaddons.bloodmagic:tranquility*}", Integer.toString((int) ((100D * (int) (100 * altar.tranquility)) / 100D)));
            textPrefixed(probeInfo, "{*topaddons.bloodmagic:bonus*}", (int) (altar.incenseAddition * 100) + "%");
        }
        else if (tile instanceof TileIncenseAltar && !holdingDivine) {
            probeInfo.textSmall(TextFormatting.GRAY + "More info: Held Sigil required");
        }


        if (tile instanceof TileMimic && seeMimickWithSigil && holdingSeer) {
            ItemStack mimicStack = ((TileMimic) world.getTileEntity(data.getPos())).getStackInSlot(0);
            if (!mimicStack.isEmpty()) {
                probeInfo.text(TextFormatting.GRAY + data.getPickBlock().getDisplayName());
            }
        }
        else if (tile instanceof TileMimic && !holdingSeer) {
            probeInfo.textSmall(TextFormatting.GRAY + "More info: Held Seer's Sigil required");
        }
    }

    private boolean holdingSigil(EntityPlayer player, ItemSigilBase sigil) {
        for (EnumHand hand : EnumHand.values()) {
            ItemStack heldStack = player.getHeldItem(hand);
            if (!heldStack.isEmpty()) {
                if (heldStack.getItem() == sigil) {
                    return true;
                } else if (heldStack.getItem() == SIGIL_HOLDING) {
                    ItemStack currentHoldingStack = ItemSigilHolding.getItemStackInSlot(heldStack, ItemSigilHolding.getCurrentItemOrdinal(heldStack));
                    return !currentHoldingStack.isEmpty() && currentHoldingStack.getItem() == sigil;
                }
            }
        }

        return false;
    }

    private void addFilterElement(IProbeInfo probeInfo, String side, ItemStack inventoryOnSide, ItemStack filterStack, EntityPlayer player) {
        probeInfo.element(new ElementNodeFilter(getElementId(player, "filter_node"), side, inventoryOnSide, filterStack));
    }

    private void addAltarCraftingElement(IProbeInfo probeInfo, ItemStack input, ItemStack result, int progress, int required, float consumption, EntityPlayer player) {
        probeInfo.element(new ElementAltarCrafting(getElementId(player, "altar_crafting"), input, result, progress, required * input.getCount(), consumption));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void getProbeConfig(IProbeConfig config, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        boolean holdingSeer = !requireSigil || holdingSigil(player, (ItemSigilBase) SIGIL_SEER);
        if (world.getTileEntity(data.getPos()) instanceof TileMimic) {
            if (holdingSeer) {
                config.showChestContents(IProbeConfig.ConfigMode.EXTENDED);
            } else {
                config.showChestContents(IProbeConfig.ConfigMode.NOT);
            }
        }
    }
}
