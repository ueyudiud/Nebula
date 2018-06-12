/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client;

import nebula.client.render.IIconLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Deprecated
@SideOnly(Side.CLIENT)
public class NebulaTextureHandler
{
	/**
	 * Register Icon Loader to list.
	 * @param loader
	 */
	@Deprecated
	public static void addIconLoader(IIconLoader loader)
	{
		NebulaClientHandler.addIconLoader(loader);
	}
}
