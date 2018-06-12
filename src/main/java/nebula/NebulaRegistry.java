/*
 * copyright 2016-2018 ueyudiud
 */
package nebula;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang3.tuple.Pair;

import nebula.client.NebulaClientHandler;
import nebula.client.NebulaRenderHandler;
import nebula.client.render.IIconLoader;
import nebula.client.render.IItemCustomRender;
import nebula.common.G;
import nebula.common.annotations.ModSensitive;
import nebula.common.nbt.INBTSelfReaderAndWriter;
import nebula.common.network.IPacket;
import nebula.common.network.Network;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public final class NebulaRegistry
{
	private NebulaRegistry() {}
	
	public static Network registerNetwork(String name, Pair<Class<? extends IPacket>, Side>...packets)
	{
		Network network = Network.network(name);
		for (Pair<Class<? extends IPacket>, Side> pair : packets)
		{
			network.registerPacket(pair.getLeft(), pair.getRight());
		}
		return network;
	}
	
	@ModSensitive
	public static void registerKey(              String name, int keycode) { registerKey(G.activeModid(), name, keycode); }
	public static void registerKey(String modid, String name, int keycode)
	{
		NebulaProxy.proxy.registerKey(name, keycode, modid);
	}
	
	public static void addWorldDataProvider(String name, INBTSelfReaderAndWriter<?> provider)
	{
		Nebula.worldDataProviders.put(name, provider);
	}
	
	@ModSensitive
	public static void registerBlock(Block block, String name) { registerBlock(block, G.activeModid(), name); }
	@ModSensitive
	public static void registerBlock(Block block, String name, Item itemBlock) { registerBlock(block, G.activeModid(), name, itemBlock); }
	
	/**
	 * Register block with modid. (The Forge given big warning if the modid and
	 * active mod id can not matched, I don't think this warning should be
	 * given.)
	 * 
	 * @param block
	 * @param modid
	 * @param name
	 */
	@Deprecated
	public static void registerBlock(Block block, String modid, String name) { registerBlock(block, modid, name, new ItemBlock(block)); }
	
	/**
	 * Register block with modid and ItemBlock type.
	 * 
	 * @param block the block.
	 * @param modid the registry modid.
	 * @param name the registry name.
	 * @param itemBlock the item block, the item type of this block.
	 */
	@Deprecated
	public static void registerBlock(Block block, String modid, String name, Item itemBlock)
	{
		GameRegistry.register(block.setRegistryName(modid, name));
		GameRegistry.register(itemBlock.setRegistryName(modid, name));
		NebulaProxy.proxy.registerRender(block);
	}
	
	@ModSensitive
	public static void registerItem(Item item, String name) { registerItem(item, G.activeModid(), name); }
	
	@Deprecated
	public static void registerItem(Item item, String modid, String name)
	{
		GameRegistry.register(item.setRegistryName(modid, name));
		NebulaProxy.proxy.registerRender(item);
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerItemCustomRender(Item item, IItemCustomRender render)
	{
		NebulaRenderHandler.registerRender(item, render);
	}
	
	@SideOnly(Side.CLIENT)
	public static void addIconLoader(IIconLoader loader)
	{
		NebulaClientHandler.addIconLoader(loader);
	}
}
