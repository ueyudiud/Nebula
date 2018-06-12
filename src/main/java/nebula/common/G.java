/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common;

import java.io.File;

import javax.annotation.Nonnull;

import nebula.NebulaLoadingPlugin;
import nebula.NebulaProxy;
import nebula.common.annotations.ModSensitive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

/**
 * @author ueyudiud
 */
public final class G
{
	private G() { }
	
	/**
	 * Get {@link net.minecraftforge.fml.common.Loader#activeModContainer() active
	 * mod container} id.
	 * @return the mod id, or return <code>"minecraft"</code> when no mod is actived.
	 */
	@Nonnull
	@ModSensitive
	public static String activeModid()
	{
		try
		{
			if (Loader.instance().activeModContainer() == null) return "minecraft";
			return Loader.instance().activeModContainer().getModId();
		}
		catch (Exception exception)
		{
			return "unknown";
		}
	}
	
	public static File mcFile()
	{
		return NebulaLoadingPlugin.location;
	}
	
	public static File logFile()
	{
		File file = new File(mcFile(), "logs");
		if (!file.exists())
		{
			file.mkdir();
		}
		return file;
	}
	
	public static boolean isSimulating()
	{
		return FMLCommonHandler.instance().getEffectiveSide().isServer();
	}
	
	public static boolean isClient()
	{
		return FMLCommonHandler.instance().getSide().isClient();
	}
	
	public static boolean isServer()
	{
		return FMLCommonHandler.instance().getSide().isServer();
	}
	
	public static EntityPlayer player()
	{
		return NebulaProxy.playerInstance();
	}
	
	/**
	 * Query if we know of a mod named modname.
	 * 
	 * @param name the modid.
	 * @return return <code>true</code> if mod are loaded.
	 */
	public static boolean isModLoaded(String name)
	{
		return Loader.isModLoaded(name);
	}
	
	/**
	 * Register an element might implement the {@link nebula.client.util.IRenderRegister}.
	 * @param object
	 */
	public static void registerRenderObject(Object object)
	{
		NebulaProxy.commonProxy().registerRender(object);
	}
}
