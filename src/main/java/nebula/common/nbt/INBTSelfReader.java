/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
public interface INBTSelfReader<N extends NBTBase>
{
	default void readFrom(NBTTagCompound nbt, String key)
	{
		readFrom((N) nbt.getTag(key));
	}
	
	void readFrom(N nbt);
}
