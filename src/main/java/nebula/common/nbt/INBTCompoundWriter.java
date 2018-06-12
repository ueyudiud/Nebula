/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public interface INBTCompoundWriter<T> extends INBTWriter<T, NBTTagCompound>
{
	default void writeTo(NBTTagCompound nbt, String key, T[] ts)
	{
		NBTTagList list = new NBTTagList();
		writeTo(list, ts);
		nbt.setTag(key, list);
	}
	
	default void writeTo(NBTTagList list, T[] ts)
	{
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound compound = new NBTTagCompound();
			writeTo(ts[i], compound);
			compound.setByte("idx", (byte) i);
			list.appendTag(compound);
		}
	}
	
	/**
	 * Only call when this type is implements the writer.
	 * 
	 * @param nbt
	 * @return
	 */
	default NBTTagCompound writeTo(T t)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeTo(t, nbt);
		return nbt;
	}
	
	void writeTo(T t, NBTTagCompound nbt);
}
