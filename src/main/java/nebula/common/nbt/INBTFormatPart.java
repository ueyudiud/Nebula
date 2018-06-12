/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import java.nio.ByteBuffer;

import net.minecraft.nbt.NBTBase;

/**
 * @author ueyudiud
 */
interface INBTFormatPart<N extends NBTBase>
{
	abstract boolean match(N nbt);
	
	abstract void serialize(ByteBuffer buffer);
	
	abstract void deserialize(ByteBuffer buffer);
	
	abstract int length();
	
	abstract N template();
}
