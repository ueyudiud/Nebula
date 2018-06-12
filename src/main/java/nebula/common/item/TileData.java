/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.item;

/**
 * @author ueyudiud
 */
public class TileData
{
	public ITileDataAccess<?> access;
	
	public TileData(ITileDataAccess<?> access)
	{
		this.access = access;
	}
}
