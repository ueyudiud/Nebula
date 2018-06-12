/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
public class TileDataAccesses
{
	private static final Map<String, ITileDataAccess<?>> ACCESSES = new HashMap<>();
	
	public static void registerAccess(String key, ITileDataAccess<?> access)
	{
		if (ACCESSES.containsKey(key))
			throw new IllegalArgumentException(key + " has already registered!");
		ACCESSES.put(key, access);
	}
	
	public static <D extends TileData> D loadData(NBTTagCompound tag)
	{
		ITileDataAccess<D> access = (ITileDataAccess<D>) ACCESSES.get(tag.getString("type"));
		D data = access.readFrom(tag);
		data.access = access;
		return data;
	}
	
	public static <D extends TileData> D loadData(NBTTagCompound tag, String key)
	{
		return loadData(tag.getCompoundTag(key));
	}
}
