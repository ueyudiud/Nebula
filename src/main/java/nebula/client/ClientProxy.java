/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import nebula.Nebula;
import nebula.V;
import nebula.base.collection.A;
import nebula.client.model.ModelFluidBlock;
import nebula.client.model.OrderModelLoader;
import nebula.client.model.flexible.NebulaModelLoader;
import nebula.client.render.Colormap.ColormapFactory;
import nebula.client.render.RenderFallingBlockExt;
import nebula.client.render.RenderProjectileItem;
import nebula.client.util.Client;
import nebula.client.util.IRenderRegister;
import nebula.common.CommonProxy;
import nebula.common.G;
import nebula.common.entity.EntityFallingBlockExtended;
import nebula.common.entity.EntityProjectileItem;
import nebula.common.gui.Container00Base;
import nebula.common.item.IItemBehaviorsAndProperties;
import nebula.common.item.IItemBehaviorsAndProperties.IIP_Containerable;
import nebula.common.tile.IGuiTile;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener
{
	public static final IBlockColor	BIOME_COLOR			= (state, worldIn, pos, tintIndex) -> {
		boolean flag = worldIn == null || pos == null;
		// Biome biome =  flag ? null : worldIn.getBiome(pos);
		switch (tintIndex)
		{
		case 0:
			return flag ? ColorizerGrass.getGrassColor(0.7F, 0.7F) : BiomeColorHelper.getGrassColorAtPos(worldIn, pos);
		case 1:
			return flag ? ColorizerFoliage.getFoliageColorBasic() : BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
		case 2:
			return flag ? -1 : BiomeColorHelper.getWaterColorAtPos(worldIn, pos);
		default:
			return -1;
		}
	};
	public static final IItemColor	ITEM_BIOME_COLOR	= (stack, index) -> BIOME_COLOR.colorMultiplier(null, null, null, index);
	
	public static Multimap<IBlockColor, Block>	blockColorMap	= HashMultimap.create();
	public static Multimap<IItemColor, Item>	itemColorMap	= HashMultimap.create();
	public static List<Block>					buildInRender	= new ArrayList<>();
	List<KeyBinding> keybindings = new ArrayList<>(16);
	
	@Override
	public void register()
	{
		super.register();
		
		MinecraftForge.EVENT_BUS.register(NebulaClientHandler.class);
		
		// Take this proxy into resource manager reload listener.
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
		
		// Register color map loader.
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(ColormapFactory.INSTANCE);
		// The base model loader.
		ModelLoaderRegistry.registerLoader(NebulaModelLoader.INSTANCE);
		// The custom block model loaders.
		ModelLoaderRegistry.registerLoader(ModelFluidBlock.Loader.INSTANCE);
		ModelLoaderRegistry.registerLoader(OrderModelLoader.INSTANCE);
		// Register entity rendering handlers.
		RenderingRegistry.registerEntityRenderingHandler(EntityFallingBlockExtended.class, RenderFallingBlockExt.Factory.instance);
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileItem.class, RenderProjectileItem.Factory.instance);
	}
	
	@Override
	public World worldInstance(int id)
	{
		if (G.isSimulating())
			return super.worldInstance(id);
		else
		{
			World world = Minecraft.getMinecraft().world;
			return world == null ? null : world.provider.getDimension() != id ? null : world;
		}
	}
	
	@Override
	public EntityPlayer playerInstance()
	{
		return Minecraft.getMinecraft().player;
	}
	
	@Override
	public File fileDir()
	{
		return Minecraft.getMinecraft().mcDataDir;
	}
	
	public void addRenderRegisterListener(IRenderRegister register)
	{
		if (Loader.instance().hasReachedState(LoaderState.INITIALIZATION))
		{
			V.warn("Register too late, place register before initalization.");
		}
		else if (register != null)
		{
			NebulaClientHandler.renderRegisters.add(register);
		}
	}
	
	@Override
	public void registerRender(Object object)
	{
		if (object instanceof IRenderRegister)
		{
			addRenderRegisterListener((IRenderRegister) object);
		}
	}
	
	@Override
	public void setModelLocate(Item item, int meta, String modid, String name)
	{
		setModelLocate(item, meta, modid, name, null);
	}
	
	@Override
	public void setModelLocate(Item item, int meta, String modid, String name, String type)
	{
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(modid + ":" + name, type));
	}
	
	@Override
	public void registerBiomeColorMultiplier(Block...block)
	{
		registerColorMultiplier(BIOME_COLOR, block);
		registerColorMultiplier(ITEM_BIOME_COLOR, block);
	}
	
	public void registerColorMultiplier(IBlockColor color, Block[] block)
	{
		blockColorMap.putAll(color, A.argument(block));
	}
	
	private void registerColorMultiplier(IItemColor color, Block[] blocks)
	{
		itemColorMap.putAll(color, Lists.transform(Arrays.asList(blocks), Item::getItemFromBlock));
	}
	
	public void registerColorMultiplier(IItemColor color, Item[] item)
	{
		itemColorMap.putAll(color, A.argument(item));
	}
	
	@Override
	public String translateToLocalByI18n(String unlocal, Object...parameters)
	{
		return I18n.format(unlocal, parameters);
	}
	
	/**
	 * Internal, do not use. (Use ASM from forge)
	 */
	public static void onRegisterAllBlocks(BlockModelShapes shapes)
	{
		shapes.registerBuiltInBlocks(nebula.common.util.L.cast(buildInRender, Block.class));
		buildInRender = null;
	}
	
	public static void registerBuildInModel(Block block)
	{
		if (Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION))
		{
			V.warn("The block '" + block.getRegistryName() + "' register buildin model after initialzation, tried register it early.");
		}
		else
		{
			buildInRender.add(block);
		}
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager manager)
	{
		// Checking is reached in preinitalization state.
		if (Loader.instance().hasReachedState(LoaderState.PREINITIALIZATION))
		{
			BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
			ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
			for (Entry<IBlockColor, Block> entry : blockColorMap.entries())
			{
				blockColors.registerBlockColorHandler(entry.getKey(), entry.getValue());
			}
			for (Entry<IItemColor, Item> entry : itemColorMap.entries())
			{
				itemColors.registerItemColorHandler(entry.getKey(), entry.getValue());
			}
		}
		if (Loader.instance().hasReachedState(LoaderState.AVAILABLE))
		{
			Client.getFontRender().onResourceManagerReload(manager);
			Nebula.instance.getLanguageManager().load(false);
		}
	}
	
	/**
	 * Deprecated now, direct register by event.<p>
	 * <i>Let client proxy called this method <b>when FML pre-initialization</b>.</i>
	 */
	@Deprecated
	public static void registerRenderObject()
	{
		//		NebulaClientHandler.renderRegisters.forEach(IRenderRegister::registerRender);
		//		NebulaClientHandler.renderRegisters = null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID >= 0)
		{
			TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
			if (tile instanceof IGuiTile) return ((IGuiTile) tile).openGui(ID, player);
		}
		else
		{
			switch (ID)
			{
			case -1:
				if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof IItemBehaviorsAndProperties.IIP_Containerable)
				{
					return ((IIP_Containerable) player.getHeldItemMainhand().getItem()).openGui(world, new BlockPos(x, y, z), player, player.getHeldItemMainhand());
				}
			default:
				break;
			}
		}
		return null;
	}
	
	public KeyBinding getBinding(String key)
	{
		Byte id = this.keys.get(key);
		return id == null ? null : this.keybindings.get(id);
	}
	
	@Override
	public Runnable createOpenGuiTask(Object guiContainer, int windowId, byte[] initalizeData) throws IOException
	{
		Container00Base container = (Container00Base) ((GuiContainer) guiContainer).inventorySlots;
		container.windowId = windowId;
		container.updateChangesData(initalizeData);
		return () -> FMLCommonHandler.instance().showGuiScreen(guiContainer);
	}
}
