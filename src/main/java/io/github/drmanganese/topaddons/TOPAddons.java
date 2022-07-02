package io.github.drmanganese.topaddons;

import io.github.drmanganese.topaddons.config.ConfigClient;
import io.github.drmanganese.topaddons.config.capabilities.CapEvents;
import io.github.drmanganese.topaddons.config.capabilities.ClientOptsCapability;
import io.github.drmanganese.topaddons.config.capabilities.IClientOptsCapability;
import io.github.drmanganese.topaddons.network.PacketHandler;
import io.github.drmanganese.topaddons.proxy.IProxy;
import io.github.drmanganese.topaddons.reference.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        version = Reference.VERSION,
        guiFactory = Reference.GUI_FACTORY,
        acceptedMinecraftVersions = "[1.11,)",
        dependencies = "required-after:theoneprobe@[1.4.8,);" +
                "after:forestry@[1.12.2-5.7.0.204,);" +
                "after:tconstruct;" +
                "after:bloodmagic;" +
                "after:storagedrawers;" +
                "after:botania;" +
                "after:ic2;" +
                "after:neotech;" +
                "after:lycanytesmobs;" +
                "after:opencomputers;" +
                "after:stevescarts;" +
                "after:mysticalagriculture",
        updateJSON = "https://raw.githubusercontent.com/DrManganese/TOPAddons/1.12/FUC.json"
)
public class TOPAddons {

    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    @CapabilityInject(IClientOptsCapability.class)
    public static final Capability<IClientOptsCapability> OPTS_CAP = null;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static IProxy proxy;

    public static Configuration config;
    public static Configuration configClient = null;

    public static boolean ic2Loaded = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile(), "1");
        config.load();
        cleanConfig();

        if (event.getSide() == Side.CLIENT) {
            configClient = new Configuration(new File(event.getModConfigurationDirectory().getPath(), Reference.MOD_ID + "_client.cfg"), "1");
            //noinspection MethodCallSideOnly
            ConfigClient.init(configClient);
            MinecraftForge.EVENT_BUS.register(ConfigClient.class);
            MinecraftForge.EVENT_BUS.register(this);
        }

        CapabilityManager.INSTANCE.register(IClientOptsCapability.class, new ClientOptsCapability.Storage(), ClientOptsCapability.class);
        MinecraftForge.EVENT_BUS.register(new CapEvents());

        PacketHandler.init();

        AddonManager.preInit(event);
        if (AddonManager.ADDONS.size() > 0) {
            TOPRegistrar.register();
            AddonManager.ADDONS.forEach(a -> a.updateConfigs(config));
            if (config.hasChanged()) {
                config.save();
            }
        }

        ic2Loaded = Loader.isModLoaded("ic2");

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void onFMLServerStarting(FMLServerStartingEvent event) {
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MOD_ID)) {
            AddonManager.ADDONS.forEach(a -> a.updateConfigs(config));
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    private void cleanConfig() {
        ConfigCategory oldBm = config.getCategory("blood magic");
        if (oldBm != null) {
            config.removeCategory(oldBm);
        }

        ConfigCategory oldForge = config.getCategory("forge");
        if (oldForge != null) {
            config.removeCategory(oldForge);
        }

        ConfigCategory oldVanilla = config.getCategory("vanilla");
        if (oldVanilla != null) {
            config.removeCategory(oldVanilla);
        }
    }
}
