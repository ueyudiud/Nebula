/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import nebula.V;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * @author ueyudiud
 * @param <T> The reading target type.
 */
@ParametersAreNonnullByDefault
public interface INBTCompoundReader<T> extends INBTReader<T, NBTTagCompound>
{
	default void readFrom(NBTTagCompound nbt, String key, T[] ts)
	{
		readFrom(nbt.getTagList(key, NBT.TAG_COMPOUND), ts);
	}
	
	default void readFrom(NBTTagList list, T[] ts)
	{
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound compound = list.getCompoundTagAt(i);
			int idx = NBTs.getIntOrDefault(compound, "idx", -1);
			if (idx >= 0 && idx < ts.length)
			{
				ts[idx] = readFrom(compound);
			}
			else
			{
				V.warn("Fail to load data from NBT. array length: {}, index: {}", ts.length, idx);
			}
		}
	}
	
	/**
	 * Read target from nbt with sub tag.
	 * 
	 * @param nbt
	 * @param key
	 * @return
	 */
	@Override
	default @Nullable T readFrom(NBTTagCompound nbt, String key)
	{
		return nbt.hasKey(key, NBT.TAG_COMPOUND) ? readFrom(nbt.getCompoundTag(key)) : null;
	}
}
