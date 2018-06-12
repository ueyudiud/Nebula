/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import net.minecraft.nbt.NBTBase;

/**
 * @author ueyudiud
 */
public interface INBTReaderAndWriter<T, N extends NBTBase> extends INBTReader<T, N>, INBTWriter<T, N>
{
	
}
