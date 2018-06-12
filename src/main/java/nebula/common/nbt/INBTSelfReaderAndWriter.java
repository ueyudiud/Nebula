/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author ueyudiud
 */
public interface INBTSelfReaderAndWriter<N extends NBTBase> extends INBTSelfReader<N>, INBTSelfWriter<N>, INBTSerializable<N>
{
	@Override
	default N serializeNBT()
	{
		return writeTo();
	}
	
	@Override
	default void deserializeNBT(N nbt)
	{
		readFrom(nbt);
	}
}
