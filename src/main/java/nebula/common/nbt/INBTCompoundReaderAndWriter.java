/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import java.lang.reflect.ParameterizedType;

import com.google.common.reflect.TypeToken;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 * @param <T> The reading and writing target type.
 */
public interface INBTCompoundReaderAndWriter<T> extends INBTCompoundReader<T>, INBTCompoundWriter<T>, INBTReaderAndWriter<T, NBTTagCompound>
{
	@Override
	default Class<T> type()
	{
		return (Class<T>)
				((ParameterizedType) TypeToken.of(getClass()).getSupertype(INBTCompoundReaderAndWriter.class).getType()).getActualTypeArguments()[0];
	}
}
