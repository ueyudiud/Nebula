/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
public interface INBTSelfWriter<N extends NBTBase>
{
	default NBTTagCompound writeTo(NBTTagCompound nbt, String key)
	{
		nbt.setTag(key, writeTo());
		return nbt;
	}
	
	/**
	 * Write target to NBT.
	 * @param nbt
	 */
	N writeTo();
}
