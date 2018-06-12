/*
 * copyright 2016-2018 ueyudiud
 */
package nebula;

import static nebula.common.LanguageManager.registerLocal;
import static net.minecraftforge.fml.common.registry.EntityRegistry.registerModEntity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import nebula.client.CreativeTabBase;
import nebula.common.*;
import nebula.common.block.BlockBase;
import nebula.common.config.NebulaConfiguration;
import nebula.common.data.DataSerializers;
import nebula.common.entity.EntityFallingBlockExtended;
import nebula.common.entity.EntityProjectileItem;
import nebula.common.item.ItemBase;
import nebula.common.item.ItemFluidDisplay;
import nebula.common.nbt.INBTSelfReaderAndWriter;
import nebula.common.network.Network;
import nebula.common.network.packet.*;
import nebula.common.world.IBlockDataProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Nebula, is a Minecraft modification provide more useful library for other
 * modification, also changed some game rule for more compact to other mods.
 * 
 * @author ueyudiud
 */
@IFMLLoadingPlugin.MCVersion("1.10.2")
public class Nebula extends DummyModContainer implements WorldAccessContainer
{
	/** The minimum forge version far core required. */
	public static final int MIN_FORGE = 2272;
	
	public static final String	MODID	= "nebula";
	public static final String	NAME	= "Nebula";
	public static final String	VERSION	= "@VERSION@";
	
	/**
	 * The built-in render id, for prevent has location collide when naming
	 * resource path.
	 * <p>
	 * Most of Nebula built-in model use this location.
	 */
	@Deprecated
	public static final String INNER_RENDER = "nebula_inner";
	
	@Instance(MODID)
	public static Nebula instance;
	
	/**
	 * The debug mode flag, enable to switch to debug mode.
	 * <p>
	 * The debug mode will give more information of game, and exception will be
	 * always throw instead of log to file.
	 * <p>
	 */
	public static boolean debug = false;
	
	/**
	 * The network instance of nebula modification.
	 */
	public static Network network;
	
	/**
	 * The block state data provider. Use to get unique id of BlockState,
	 * generalized for 4096 id and 16 meta. Specially, if you added ED (Extra
	 * Data mod), the data provider will be Nebula full version one (This
	 * function was split from Nebula), which can store 20 bits meta.
	 */
	public static IBlockDataProvider blockDataProvider = new IBlockDataProvider.Template();
	
	/**
	 * The fluid item.
	 * @see ItemFluidDisplay
	 */
	public static ItemFluidDisplay fluid_displayment;
	
	/**
	 * The world data providers, use to load or save world data from file.
	 * <p>
	 * The provider SHOULD access <tt>null</tt> as input for method <tt>readFromNBT</tt>
	 * for if the data is missing.
	 */
	public static final Map<String, INBTSelfReaderAndWriter<?>> worldDataProviders = new HashMap<>(4);
	
	/** The language manager. */
	private LanguageManager	lang;
	
	/** The configuration of Nebula. */
	private Configuration configuration;
	
	public static CreativeTabs tabFluids;
	
	public Nebula()
	{
		super(new ModMetadata());
		instance = this;
		ModMetadata meta = getMetadata();
		meta.modId = MODID;
		meta.name = NAME;
		meta.version = VERSION;
		meta.credits = "ueyudiud";
		meta.authorList = ImmutableList.of("ueyudiud");
		meta.description = "Nebula core.";
		meta.logoFile = "/assets/nebula/textures/logo.png";
		V.log = NebulaLog.logger = LogManager.getLogger(Nebula.NAME);
	}
	
	@Override
	public Nebula getMod()
	{
		return this;
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		bus.register(this);
		return true;
	}
	
	@Override
	public File getSource()
	{
		return NebulaLoadingPlugin.source();
	}
	
	@Override
	public Class<?> getCustomResourcePackClass()
	{
		try
		{
			return Class.forName(NebulaLoadingPlugin.source().isDirectory() ?
					"net.minecraftforge.fml.client.FMLFolderResourcePack" : "net.minecraftforge.fml.client.FMLFileResourcePack");
		}
		catch (ClassNotFoundException exception)
		{
			return null;
		}
	}
	
	/**
	 * Get language manager instance.
	 * <p>
	 * Use to internationalize string, if you need't reload
	 * localization file, use state method instead.
	 * 
	 * @return the language manager.
	 * @see nebula.common.LanguageManager
	 */
	public LanguageManager getLanguageManager()
	{
		return this.lang;
	}
	
	@Subscribe
	public void check(FMLConstructionEvent event)
	{
		V.info("Injecting Nebula proxy...");// Forge does not let Dummy Mod Container auto inject proxy.
		try
		{
			Field field = NebulaProxy.class.getField("proxy");
			SidedProxy proxy = field.getAnnotation(SidedProxy.class);
			field.set(null, Class.forName(G.isClient() ? proxy.clientSide() : proxy.serverSide()).newInstance());
		}
		catch (Exception exception)
		{
			throw new RuntimeException("Fail to inject proxy!");
		}
		
		/**
		 * The Nebula and its child mod use Java8. There are method is added in
		 * Java8, so it is checked by a type exist since Java8. This checking
		 * will be removed when forge using Java8 for compile.
		 */
		V.info("Nebula start check java version...");
		try
		{
			((Function<?, ?>) arg -> null).apply(null);
		}
		catch (Exception exception)
		{
			throw new RuntimeException("Java version is out of date, please use java 8 to launch.", exception);
		}
		/**
		 * Checking forge version.
		 */
		V.info("Nebula checking forge version...");
		int forge = ForgeVersion.getBuildVersion();
		if ((forge > 0) && (forge < MIN_FORGE))
			throw new RuntimeException("The currently installed version of " + "Minecraft Forge (" + ForgeVersion.getMajorVersion() + "." + ForgeVersion.getMinorVersion() + "." + ForgeVersion.getRevisionVersion() + "." + forge + ") is out of data.\n"
					+ "Please update the Minecraft Forge.\n(Technical information: " + forge + " < " + MIN_FORGE + ")");
		V.info("Checking end.");
	}
	
	@Subscribe
	public void load(FMLPreInitializationEvent event)
	{
		this.lang = new LanguageManager(new File(G.mcFile(), "lang"));
		this.configuration = NebulaConfiguration.loadStaticConfig(NebulaConfig.class);
		
		MinecraftForge.EVENT_BUS.register(new NebulaPlayerHandler());
		MinecraftForge.EVENT_BUS.register(new NebulaWorldHandler());
		
		if (NebulaConfig.displayFluidInTab)
		{
			tabFluids = new CreativeTabBase("nebula.fluids", "Fluids[Nebula]", new ItemStack(Items.WATER_BUCKET));
		}
		fluid_displayment = (ItemFluidDisplay) new ItemFluidDisplay().setCreativeTab(tabFluids);
		int id = 0;
		registerModEntity(EntityFallingBlockExtended.class, "FallingBlockExtended", id++, "nebula", 32, 20, true);
		registerModEntity(EntityProjectileItem.class, "ProjectileItem", id++, "nebula", 32, 20, true);
		NebulaProxy.proxy.register();
	}
	
	@Subscribe
	public void load(FMLInitializationEvent event)
	{
		ItemBase.post();
		BlockBase.post();
		registerLocal("info.shift.click", V.CF.WHITE + "Press " + V.CF.ITALIC + "<%s>" + V.CF.RESET + " to get more information.");
		registerLocal("info.food.label", V.CF.RED + "Food Stat:");
		registerLocal("info.food.display", V.CF.RED + "F-%s S-%s W-%s");
		NetworkRegistry.INSTANCE.registerGuiHandler(MODID, NebulaProxy.proxy);
		network = Network.network(Nebula.MODID);
		network.registerPacket(PacketEntity.class, Side.CLIENT);
		network.registerPacket(PacketEntityAsk.class, Side.SERVER);
		network.registerPacket(PacketKey.class, Side.SERVER);
		network.registerPacket(PacketTESync.class, Side.CLIENT);
		network.registerPacket(PacketTESAsk.class, Side.CLIENT);
		network.registerPacket(PacketTEAsk.class, Side.SERVER);
		network.registerPacket(PacketBreakBlock.class, Side.CLIENT);
		network.registerPacket(PacketGuiTickUpdate.class, Side.SERVER);
		network.registerPacket(PacketChunkNetData.class, Side.CLIENT);
		network.registerPacket(PacketGuiAction.class, Side.SERVER);
		network.registerPacket(PacketContainerDataUpdate.class, Side.CLIENT);
		network.registerPacket(PacketGuiOpen.class, Side.CLIENT);
	}
	
	@Subscribe
	public void load(FMLLoadCompleteEvent event)
	{
		DataSerializers.init();
		this.lang.load(true);
		this.lang.save(true);
	}
	
	@Override
	public String getGuiClassName()
	{
		return "nebula.client.NebulaGuiFactory";
	}
	
	@Subscribe
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (event.getModID().equals(MODID))
		{
			NebulaConfiguration.loadStaticConfig(NebulaConfig.class, this.configuration);
		}
	}
	
	@Override
	public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		for (Entry<String, INBTSelfReaderAndWriter<?>> entry : worldDataProviders.entrySet())
		{
			entry.getValue().writeTo(nbt, entry.getKey());
		}
		return nbt;
	}
	
	@Override
	public void readData(SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap, NBTTagCompound tag)
	{
		for (Entry<String, INBTSelfReaderAndWriter<?>> entry : worldDataProviders.entrySet())
		{
			entry.getValue().readFrom(tag, entry.getKey());
		}
	}
}
