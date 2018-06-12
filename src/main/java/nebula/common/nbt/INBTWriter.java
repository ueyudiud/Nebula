/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public interface INBTWriter<T, N extends NBTBase>
{
	default NBTTagCompound writeTo(NBTTagCompound nbt, String key, @Nullable T t)
	{
		nbt.setTag(key, writeTo(t));
		return nbt;
	}
	
	N writeTo(T t);
	
	Class<T> type();
}
