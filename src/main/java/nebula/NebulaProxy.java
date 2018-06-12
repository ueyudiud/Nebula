/*
 * copyright 2016-2018 ueyudiud
 */
package nebula;

import nebula.client.ClientProxy;
import nebula.common.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
public class NebulaProxy
{
	/**
	 * The mod sided proxy.<p>
	 * DO NOT CALL THIS DIRECTLY ANY MORE, USE {@link #commonProxy()} instead.
	 */
	@Deprecated
	@SidedProxy(modId = Nebula.MODID, serverSide = "nebula.common.CommonProxy", clientSide = "nebula.client.ClientProxy")
	public static CommonProxy proxy;
	
	/** Get client proxy. */
	@SideOnly(Side.CLIENT)
	public static ClientProxy clientProxy() { return (ClientProxy) proxy; }
	/** Get common proxy. */
	public static CommonProxy commonProxy() { return proxy; }
	
	public static EntityPlayer playerInstance()
	{
		return proxy.playerInstance();
	}
	
	public static boolean isKeyDown(EntityPlayer player, String key)
	{
		return proxy.isKeydown(player, key);
	}
	
}
