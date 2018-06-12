/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
public interface INBTSelfCompoundWriter extends INBTSelfWriter<NBTTagCompound>
{
	default NBTTagCompound writeTo()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeTo(nbt);
		return nbt;
	}
	
	void writeTo(NBTTagCompound nbt);
}
