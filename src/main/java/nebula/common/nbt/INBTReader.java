/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
public interface INBTReader<T, N extends NBTBase>
{
	default T readFrom(NBTTagCompound nbt, String key)
	{
		return readFrom((N) nbt.getTag(key));
	}
	
	/**
	 * Read value from NBT.
	 * @param nbt
	 * @return
	 */
	T readFrom(N nbt);
	
	Class<T> type();
}
