/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.model.flexible;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;

import nebula.NebulaLog;
import nebula.base.Ety;
import nebula.base.J;
import nebula.base.S;
import nebula.base.function.F;
import nebula.client.model.IStateMapperExt;
import nebula.client.model.ModelLocation;
import nebula.client.model.flexible.NebulaModelDeserializer.Transform;
import nebula.client.util.IIconCollection;
import nebula.common.item.ItemFluidDisplay;
import nebula.common.util.IO;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Nebula Model Loader (Before: Nebula Item Model Loader & Nebula Block
 * ModelLoader), provide more flexible model loader for creator, make more
 * convenient model file writing rules for resource pack maker.
 * <p>
 * 
 * @author ueyudiud
 * @version 0.13
 */
@SideOnly(Side.CLIENT)
public enum NebulaModelLoader implements ICustomModelLoader
{
	INSTANCE;
	
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	/**
	 * The default variant or key for model, use to load model with only single
	 * meta or model is missing a texture set.<br>
	 */
	public static final String				NORMAL					= "";
	/**
	 * The normal meta generator, it will return default variant.
	 */
	public static final Function<?, String>	NORMAL_METAGENERATOR	= F.anyf(NORMAL);
	/**
	 * The default color (RGBa) in model quad.<br>
	 */
	public static final int					NORMAL_COLOR			= 0xFFFFFFFF;
	/**
	 * The normal color get function used to get color multiple, it will return
	 * default color.
	 */
	public static final ToIntFunction<?>	NORMAL_MULTIPLIER		= any -> NORMAL_COLOR;
	/**
	 * The missing no icon, it always return when NebulaModelLoader can not find
	 * icon handler by resource location.
	 */
	public static final IIconCollection		ICON_HANDLER_MISSING	= new MissingnoIconHandler();
	
	private static final Map<ResourceLocation, Entry<ResourceLocation, JsonDeserializer<? extends IModel>>>	MODEL_PROVIDERS					= new HashMap<>();
	private static final Map<ResourceLocation, JsonDeserializer<? extends IModel>>							MODEL_DESERIALIZERS				= new HashMap<>();
	private static final Map<ResourceLocation, ToIntFunction<IBlockState>>									BUILTIN_BLOCK_COLORMULTIPLIER	= new HashMap<>();
	private static final Map<ResourceLocation, ToIntFunction<ItemStack>>									BUILTIN_ITEM_COLORMULTIPLIER	= new HashMap<>();
	private static final Map<ResourceLocation, Supplier<Map<String, ResourceLocation>>>						BUILTIN_TEXTURESET				= new HashMap<>();
	private static final Map<ResourceLocation, Item>														LOCATION_TO_ITEM_MAP			= new HashMap<>();
	
	static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(SubmetaLoader.BlockSubmetaGetter.class, SubmetaLoader.BLOCK_LOADER)
			.registerTypeAdapter(SubmetaLoader.ItemSubmetaGetter.class, SubmetaLoader.ITEM_LOADER)
			.registerTypeAdapter(INebulaModelPart.class, NebulaModelDeserializer.BLOCK_MODEL_PART_DESERIALIZERS).registerTypeAdapter(ModelPartCollection.class, ModelPartCollection.DESERIALIZER)
			.registerTypeAdapter(NebulaModelDeserializer.Transform.class, (JsonDeserializer<Transform>) (json, typeOfT, context) -> {
				Transform transform = new Transform();
				// TODO
				return transform;
			}).registerTypeAdapter(IModel.class, (JsonDeserializer<IModel>) (json, type, context) -> {
				String key = J.getOrDefault(json.getAsJsonObject(), "loader", null);
				JsonDeserializer<? extends IModel> deserializer = INSTANCE.defaultDeserializer;
				if (key != null)
				{
					deserializer = MODEL_DESERIALIZERS.get(new ResourceLocation(key));
				}
				if (deserializer == null)
				{
					if (INSTANCE.defaultDeserializer == null)
					{
						throw new JsonParseException("No deserializer found.");
					}
					else
					{
						deserializer = INSTANCE.defaultDeserializer;
						NebulaLog.debug("The deserializer \"{}\" not found, use default deserializer instead.", key);
					}
				}
				return deserializer.deserialize(json, type, context);
			}).create();
	
	/**
	 * Register model deserializer.
	 * 
	 * @param location
	 * @param deserializer
	 */
	public static void registerDeserializer(ResourceLocation location, JsonDeserializer<? extends IModel> deserializer)
	{
		MODEL_DESERIALIZERS.put(location, deserializer);
	}
	
	/**
	 * Mark this model should loaded by NebulaModelLoaer.<br>
	 * This method should called before <code>FMLInitializationEvent</code>
	 * 
	 * @param item The marked item.
	 * @param location The loading location.
	 */
	public static void registerModel(Item item, ResourceLocation location)
	{
		// The default model location of item.
		ModelResourceLocation location1 = new ModelLocation(item.getRegistryName(), "inventory");
		// For stack to location logic, it will only return single model
		// location, the variant will be match in model.
		ModelLoader.setCustomMeshDefinition(item, stack -> location1);
		// Register allowed build item variant.
		ModelLoader.registerItemVariants(item, location1);
		// The real location of model.
		location = new ResourceLocation(location.getResourceDomain(), "models/item/" + location.getResourcePath());
		
		LOCATION_TO_ITEM_MAP.put(location1, item);
		registerModel(location1, location, NebulaModelDeserializer.ITEM);
	}
	
	public static void registerModel(Block block, IStateMapperExt mapper, IBlockState state, ResourceLocation location)
	{
		if (block != null) LOCATION_TO_ITEM_MAP.put(location, Item.getItemFromBlock(block));
		registerModel(mapper.getLocationFromState(state), new ResourceLocation(location.getResourceDomain(), "models/block1/" + location.getResourcePath()), NebulaModelDeserializer.BLOCK);
	}
	
	public static void registerModel(ResourceLocation location, ResourceLocation location2)
	{
		registerModel(location, location2, null);
	}
	
	/**
	 * Mark this model should loaded by NebulaModelLoader.
	 * 
	 * @param location the model loader predicated
	 * @param location2 the real loading location.
	 * @param deserializer the used deserializer to load resource.
	 */
	public static void registerModel(ResourceLocation location, ResourceLocation location2, @Nullable JsonDeserializer<? extends IModel> deserializer)
	{
		MODEL_PROVIDERS.put(location, new Ety<>(location2, deserializer));
	}
	
	/**
	 * Register item meta generator.
	 * 
	 * @param location the registry name of generator.
	 * @param function the meta generator.
	 */
	public static void registerItemMetaGenerator(ResourceLocation location, Function<ItemStack, String> function)
	{
		SubmetaLoader.ITEM_META_GENERATOR.put(location, function);
	}
	
	/**
	 * Register block meta generator.
	 * 
	 * @param location the registry name of generator.
	 * @param function the meta generator.
	 */
	public static void registerBlockMetaGenerator(ResourceLocation location, Function<IBlockState, String> function)
	{
		SubmetaLoader.BLOCK_META_GENERATOR.put(location, function);
	}
	
	public static void registerBlockColorMultiplier(ResourceLocation location, ToIntFunction<IBlockState> colorMultiplier)
	{
		BUILTIN_BLOCK_COLORMULTIPLIER.put(location, colorMultiplier);
	}
	
	public static void registerItemColorMultiplier(ResourceLocation location, ToIntFunction<ItemStack> colorMultiplier)
	{
		BUILTIN_ITEM_COLORMULTIPLIER.put(location, colorMultiplier);
	}
	
	public static void registerTextureSet(ResourceLocation location, Supplier<Map<String, ResourceLocation>> map)
	{
		BUILTIN_TEXTURESET.put(location, map);
	}
	
	PrintStream stream;
	
	public IResourceManager	manager;
	public ResourceLocation	currentLocation;
	public Item				currentItem;
	
	private JsonDeserializer<? extends IModel> defaultDeserializer;
	
	private Map<ResourceLocation, Map<String, ResourceLocation>>	cacheLocations;
	private Map<ResourceLocation, IModel>							models;
	private Map<ResourceLocation, ModelPartCollection>				parts;
	
	@Override
	public void onResourceManagerReload(IResourceManager manager)
	{
		if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) return;
		
		initPrintStream();
		info("Nebula Model Loader start model loading.");
		this.manager = manager;
		info("Clean caches.");
		this.models = new HashMap<>();
		this.cacheLocations = new HashMap<>();
		this.parts = new HashMap<>();
		this.loadingTextureSets.clear();
		SubmetaLoader.onResourceReloadStart();
		
		Map<ResourceLocation, Entry<ResourceLocation, JsonDeserializer<? extends IModel>>> map = new HashMap<>(MODEL_PROVIDERS);
		this.stream.println("Load replace loading model locations from resource.");
		for (ModContainer container : Loader.instance().getModList())
		{
			try
			{
				byte[] codes = IO.copyResource(manager, new ResourceLocation(container.getModId(), "models/nebula_relocation.txt"));
				info("Loading from " + container.getModId() + " replacements.");
				BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(codes), "UTF-8"));
				String line;
				int l = 0;
				while ((line = reader.readLine()) != null)
				{
					if (line.length() != 0 && line.charAt(0) != '#')
					{
						String[] split = S.splitstoa(line, ',');
						if (split.length <= 1 || split.length >= 4) throw new RuntimeException();
						String source = split[0].trim();
						List<String> path = S.splitstol(split[1].trim(), ':', 1);
						
						map.put(split[0].indexOf('#') != -1 ? new ModelResourceLocation(source) : new ResourceLocation(source),
								new Ety<>(path.size() == 1 ? new ResourceLocation(container.getModId(), path.get(0)) : new ResourceLocation(path.get(0), path.get(1)),
										split.length == 2 ? NebulaModelDeserializer.GENERAL : NebulaModelDeserializer.valueOf(split[2].trim())));
						
						l ++;
					}
				}
				info("Loaded " + l + " replacements from " + container.getModId() + " resurces.");
			}
			catch (IOException exception)
			{
				continue;
			}
			catch (RuntimeException exception)
			{
				warn("Invalid file get. modid: " + container.getModId(), exception);
			}
		}
		info("Finished loaded replaced location.");
		
		ProgressBar bar = ProgressManager.push("Loading Nebula Model", map.size());
		BlockColors colors1 = Minecraft.getMinecraft().getBlockColors();
		ItemColors colors2 = Minecraft.getMinecraft().getItemColors();
		for (Entry<ResourceLocation, Entry<ResourceLocation, JsonDeserializer<? extends IModel>>> entry : map.entrySet())
		{
			bar.step(entry.getValue().getKey().toString());
			this.defaultDeserializer = entry.getValue().getValue();
			this.currentLocation = entry.getValue().getKey();
			this.currentItem = LOCATION_TO_ITEM_MAP.get(entry.getKey());
			IModel cache = null;
			ResourceLocation location = entry.getValue().getKey();
			try
			{
				location = new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".json");
				byte[] code = IO.copyResource(manager, location);
				cache = GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(code)), IModel.class);
			}
			catch (IOException exception)
			{
				warn("File not found: " + location);
			}
			catch (Exception exception)
			{
				warn("Fail to load model of name \"" + entry.getKey() + "\"=>\"" + entry.getValue().getKey() + "\"", exception);
			}
			if (cache != null)
			{
				this.models.put(entry.getKey(), cache);
				if (cache instanceof IRecolorableModel && colors1 != null && colors2 != null)
				{
					((IRecolorableModel) cache).registerColorMultiplier(colors1);
					((IRecolorableModel) cache).registerColorMultiplier(colors2);
				}
			}
			this.currentItem = null;
			this.currentLocation = null;
			this.defaultDeserializer = null;
		}
		ProgressManager.pop(bar);
		SubmetaLoader.onResourceReloadEnd();
		info("Nebula Model Loader finished model loading.");
	}
	
	private static final DateFormat FORMAT = new SimpleDateFormat("[HH:mm:ss]");
	
	void info(String msg)
	{
		this.stream.println(FORMAT.format(new Date()) + msg);
		NebulaLog.info(msg);
	}
	
	void warn(String msg)
	{
		this.stream.println(FORMAT.format(new Date()) + msg);
		NebulaLog.warn(msg);
	}
	
	void warn(String msg, Throwable t)
	{
		this.stream.println(FORMAT.format(new Date()) + msg);
		t.printStackTrace(this.stream);
		NebulaLog.warn(msg);
	}
	
	private void initPrintStream()
	{
		if (this.stream != null) return;
		try
		{
			File file = new File(Minecraft.getMinecraft().mcDataDir, "logs\\nebula_model.log");
			if (!file.createNewFile())
			{
				File file1 = new File(Minecraft.getMinecraft().mcDataDir, "logs\\nebula_model_last.log");
				file1.delete();
				file.renameTo(file1);
				file.delete();
			}
			this.stream = new PrintStream(new FileOutputStream(file));
		}
		catch (IOException e)
		{
			NebulaLog.warn("Fail to create custom print stream, use system version instead.", e);
			this.stream = System.out;
		}
	}
	
	@SubscribeEvent
	public void onModelBaked(ModelBakeEvent event)
	{
		if (!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) return;
		
		for (IModel model : this.models.values())
		{
			if (model instanceof FlexibleModel)
			{
				((FlexibleModel) model).loadResources();
			}
		}
		
		this.models = null;
		this.parts = null;
	}
	
	/**
	 * @param modelLocation the location of model.
	 * @return <code>true</code> if the model with modelLocation is exist in
	 *         NebulaModelLoader and <code>false</code> for otherwise.
	 */
	@Override
	public boolean accepts(ResourceLocation modelLocation)
	{
		return this.models.containsKey(modelLocation) ||
				(modelLocation instanceof ModelResourceLocation &&
						this.models.containsKey(new ResourceLocation(modelLocation.getResourceDomain(), modelLocation.getResourcePath())));
	}
	
	/**
	 * Get item meta applier.
	 * @param location the location of generator
	 * @return
	 */
	public static Function<ItemStack, String> loadItemMetaGenerator(String location)
	{
		return SubmetaLoader.loadItemMetaGenerator(location);
	}
	
	/**
	 * Get block meta applier.
	 * @param location the location of generator.
	 * @return
	 */
	public static Function<IBlockState, String> loadBlockMetaGenerator(String location)
	{
		return SubmetaLoader.loadBlockMetaGenerator(location);
	}
	
	/**
	 * Get icon/iconset by location.
	 * 
	 * @param location
	 * @return
	 */
	public static IIconCollection loadIconHandler(@Nonnull String location)
	{
		if (location.charAt(0) == '[')
		{
			return new TemplateIconHandler(getTextureSet(new ResourceLocation(location.substring(1))));
		}
		return new SimpleIconHandler(NORMAL, new ResourceLocation(location));// Default loader instead.
	}
	
	/**
	 * Get color multiplier use for item model.
	 * 
	 * @param location
	 * @return
	 */
	public static ToIntFunction<ItemStack> loadItemColorMultiplier(String location)
	{
		return BUILTIN_ITEM_COLORMULTIPLIER.getOrDefault(new ResourceLocation(location), (ToIntFunction<ItemStack>) NORMAL_MULTIPLIER);
	}
	
	/**
	 * Get texture set, which is used for {@link IIconCollection}.
	 * <p>
	 * The loader will find resources at
	 * {@code "[domain]:models/textureset/[path]"}. The file is format as
	 * {@code key=value}, and use {@code #} to mark a line of annotation.
	 * <p>
	 * The loader will find in inner resources supplier when not found any
	 * mapping in resources, and will throw an exception of file not found when
	 * no textures set found.
	 * 
	 * @param location
	 * @return
	 */
	public static Map<String, ResourceLocation> getTextureSet(ResourceLocation location)
	{
		try
		{
			return INSTANCE.cacheLocations.computeIfAbsent(location, INSTANCE::loadTextureSet$);
		}
		catch (Exception exception)
		{
			INSTANCE.warn("Failed to get texture set from " + location, exception);
			return ImmutableMap.of();
		}
	}
	
	private final LinkedList<ResourceLocation> loadingTextureSets = new LinkedList<>();
	
	private Map<String, ResourceLocation> loadTextureSet$(ResourceLocation location)
	{
		if (this.loadingTextureSets.contains(location))
			throw new InternalError();
		this.loadingTextureSets.addLast(location);
		Map<String, ResourceLocation> result;
		try
		{
			result = loadTextureSet(location);
		}
		finally
		{
			this.loadingTextureSets.removeLast();
		}
		return result;
	}
	
	private Map<String, ResourceLocation> loadTextureSet(ResourceLocation location)
	{
		ResourceLocation location2 = location;
		location = new ResourceLocation(location.getResourceDomain(), "models/textureset/" + location.getResourcePath() + ".txt");
		try (IResource resource = this.manager.getResource(location))
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
			String key;
			Map<String, ResourceLocation> builder = new HashMap<>();
			int line = 0;
			while ((key = reader.readLine()) != null)
			{
				++line;
				if (key.length() == 0 || key.charAt(0) == '#')// For annotate.
					continue;
				switch (key.charAt(0))
				{
				case '@':
				{
					String[] values = S.splitstoa(key, ' ');
					switch (values[0])
					{
					case "@include":
						if (values.length != 2)
							throw new IllegalArgumentException("Invalid @include uses. file: " + location + " line: " + line);
						try
						{
							builder.putAll(getTextureSet(new ResourceLocation(values[1])));
						}
						catch (InternalError error)
						{
							this.stream.println("Looped loading. file: " + location + " line: " + line + " target: " + values[1]);
						}
						catch (RuntimeException exception)
						{
							this.stream.println("Fail to load texture set. location: " + values[1]);
						}
						break;
					case "@remove":
						if (values.length != 2)
							throw new IllegalArgumentException("Invalid @remove uses. file: " + location + " line: " + line);
						builder.remove(values[1]);
						break;
					default:
						throw new IllegalArgumentException("Unknown operation, got: " + values[0] + ". file: " + location + " line: " + line);
					}
					break;
				}
				case '[':
				{
					List<String> list = new ArrayList<>();
					int i = 0, j = 0, k = 0;
					findBracket: while (k < key.length())
					{
						switch (key.charAt(k ++))
						{
						case ' ' :
							if (i == k)
								++ i;
							break;
						case ',' :
							list.add(key.substring(i, j));
							i = k;
							break;
						case ']' :
							list.add(key.substring(i, j));
							break findBracket;
						case '[' :
							throw new IllegalArgumentException("Illegal key character.");
						default:
							j = k - 1;
							break;
						}
					}
					if (key.charAt(k) != '=')
						throw new IllegalArgumentException("'=' is expected.");
					list.forEach(F.const2c(builder::put, new ResourceLocation(key.substring(k + 1).trim())));
					break;
				}
				default :
				{
					int idx;
					if ((idx = key.indexOf('=')) == -1)
						throw new RuntimeException("\"" + key + "\" is missing a '=' for key pair.");
					builder.put(key.substring(0, idx), new ResourceLocation(key.substring(idx + 1)));
					break;
				}
				}
			}
			return ImmutableMap.copyOf(builder);
		}
		catch (IOException exception)
		{
			Supplier<Map<String, ResourceLocation>> supplier = BUILTIN_TEXTURESET.get(location2);
			if (supplier != null)
			{
				return supplier.get();
			}
			exception.printStackTrace(this.stream);
			return ImmutableMap.of();
		}
		catch (RuntimeException exception)
		{
			exception.printStackTrace(this.stream);
			return ImmutableMap.of();
		}
	}
	
	/**
	 * Get model from model loader.
	 * 
	 * @param location the model location.
	 * @return the model.
	 */
	public static IModel getModel(ResourceLocation location)
	{
		IModel model = INSTANCE.models.get(location);
		return (model == null) ? ModelLoaderRegistry.getModelOrMissing(location) : model;
	}
	
	/**
	 * Load model part from resource location.
	 * 
	 * @param location
	 * @return
	 */
	public static INebulaModelPart getModelPart(String location)
	{
		ModelResourceLocation location2 = new ModelResourceLocation(location);
		return INSTANCE.parts.computeIfAbsent(new ResourceLocation(location2.getResourceDomain(), location2.getResourcePath()), INSTANCE::loadModelPart).getModelPart(location2.getVariant());
	}
	
	/**
	 * Load model part from location.
	 * <p>
	 * The actual location is <tt>[domain]/models/parts/[path]</tt>.
	 * 
	 * @param location the part location.
	 * @return the model part.
	 */
	public ModelPartCollection loadModelPart(ResourceLocation location)
	{
		location = new ResourceLocation(location.getResourceDomain(), "models/parts/" + location.getResourcePath() + ".json");
		try (IResource resource = this.manager.getResource(location))
		{
			return GSON.fromJson(new InputStreamReader(resource.getInputStream()), ModelPartCollection.class);
		}
		catch (FileNotFoundException exception)
		{
			info("Model part " + location + " not found.");
			return ModelPartCollection.EMPTY;
		}
		catch (IOException exception)
		{
			warn("Fail to load model part " + location, exception);
			return ModelPartCollection.EMPTY;
		}
	}
	
	/**
	 * Use {@link #getModel(net.minecraft.util.ResourceLocation)} instead
	 * if want to get loaded model.
	 * 
	 * @return The loaded model.
	 */
	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception
	{
		IModel model = this.models.get(modelLocation);
		if (model == null && modelLocation instanceof ModelResourceLocation)
		{
			model = this.models.get(new ResourceLocation(modelLocation.getResourceDomain(), modelLocation.getResourcePath()));
		}
		if (model == null)
		{
			// If model is null, it means the loader of location shoudn't be this loader.
			throw new RuntimeException(String.format("The model location \"%s\" is not belong to NebulaModelLoader. There must be some wrong of other model loader.", modelLocation));
		}
		return model;
	}
	
	static
	{
		registerItemMetaGenerator(new ResourceLocation("forge", "contain_fluid"), stack -> {
			FluidStack stack1 = FluidUtil.getFluidContained(stack);
			return stack1 == null ? "empty" : stack1.getFluid().getName();
		});
		registerItemMetaGenerator(new ResourceLocation("nebula", "display_fluid"), stack -> ItemFluidDisplay.getFluid(stack).getName());
		registerItemColorMultiplier(new ResourceLocation("nebula", "display_fluid/color"), stack -> ItemFluidDisplay.getFluid(stack).getColor());
		
		registerTextureSet(new ResourceLocation("forge", "fluid"), () ->
		Maps.transformValues(FluidRegistry.getRegisteredFluids(), Fluid::getStill));
	}
}
