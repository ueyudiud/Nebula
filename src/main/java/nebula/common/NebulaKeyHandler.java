/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common;

import nebula.NebulaProxy;
import nebula.NebulaRegistry;
import nebula.common.annotations.ModSensitive;
import nebula.common.util.Game;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Deprecated
public class NebulaKeyHandler
{
	public static boolean get(EntityPlayer player, String key)
	{
		return NebulaProxy.isKeyDown(player, key);
	}
	
	@ModSensitive
	public static void register(String name, int keycode)
	{
		register(name, keycode, Game.getActiveModID());
	}
	
	public static void register(String name, int keycode, String modid)
	{
		NebulaRegistry.registerKey(modid, name, keycode);
	}
	
	@SideOnly(Side.CLIENT)
	public static KeyBinding getBinding(String name)
	{
		return NebulaProxy.clientProxy().getBinding(name);
	}
}
