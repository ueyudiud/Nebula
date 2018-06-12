/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.util;

import nebula.common.G;

/**
 * @author ueyudiud
 */
@Deprecated
public final class Sides
{
	private Sides()
	{
	}
	
	public static boolean isClient()
	{
		return G.isClient();
	}
	
	public static boolean isServer()
	{
		return G.isServer();
	}
	
	public static boolean isSimulating()
	{
		return G.isSimulating();
	}
}
