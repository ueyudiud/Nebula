/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common;

import java.io.File;
import java.io.IOException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import nebula.common.item.IItemBehaviorsAndProperties;
import nebula.common.item.IItemBehaviorsAndProperties.IIP_Containerable;
import nebula.common.tile.IGuiTile;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * @author ueyudiud
 */
public class CommonProxy implements IGuiHandler
{
	protected BiMap<String, Byte> keys = HashBiMap.create();
	
	public boolean isKeydown(EntityPlayer player, String key)
	{
		if ("sneaking".equals(key))
			return player.isSneaking();
		Byte id = this.keys.get(key);
		return id == null ? false : (NebulaCommonHandler.PLAYER_KEY_MAP.getOrDefault(player, 0L) & 1 << id) != 0;
	}
	
	public void registerKey(String name, int keycode, String modid)
	{
		if (this.keys.size() >= 64)
			throw new IllegalStateException("Too many keys registered!");
		this.keys.put(name, (byte) this.keys.size());
	}
	
	public World worldInstance(int id)
	{
		return DimensionManager.getWorld(id);
	}
	
	public File fileDir()
	{
		return new File(".");
	}
	
	public EntityPlayer playerInstance()
	{
		return null;
	}
	
	public void setModelLocate(Item item, int meta, String modid, String name, String type)
	{
		
	}
	
	public void setModelLocate(Item item, int meta, String modid, String name)
	{
		
	}
	
	public void registerBiomeColorMultiplier(Block...block)
	{
		
	}
	
	public String translateToLocalByI18n(String unlocal, Object...parameters)
	{
		return null;
	}
	
	public void register()
	{
		MinecraftForge.EVENT_BUS.register(NebulaCommonHandler.class);
	}
	
	@Override
	public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID >= 0)
		{
			TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
			if (tile instanceof IGuiTile)
			{
				return ((IGuiTile) tile).openContainer(ID, player);
			}
		}
		else if (ID < 0)
		{
			switch (ID)
			{
			case -1:
				if (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof IItemBehaviorsAndProperties.IIP_Containerable)
				{
					return ((IIP_Containerable) player.getHeldItemMainhand().getItem()).openContainer(world, new BlockPos(x, y, z), player, player.getHeldItemMainhand());
				}
				break;
			default:
				break;
			}
		}
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
	
	public void registerRender(Object arg)
	{
		
	}
	
	/**
	 * Internal method.<p>
	 * Used to create a GUI changes task.
	 * @param guiContainer the GuiContainer type, the container of it is predicated for Container00Base.
	 * @param windowId the window id.
	 * @param initalizeData the initialize data.
	 * @return the open GUI task.
	 * @throws IOException
	 */
	public Runnable createOpenGuiTask(Object guiContainer, int windowId, byte[] initializeData) throws IOException
	{
		return () -> {};//Nothing to do in server.
	}
}
