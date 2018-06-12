/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.util;

import javax.annotation.Nullable;

import nebula.base.function.Judgable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author ueyudiud
 */
public interface ICapabilityMatcher<T> extends Judgable<T>, INBTSerializable<NBTTagCompound>
{
	default T instance() { return instance(null); }
	
	default T instance(@Nullable EnumFacing side)
	{
		T value = target().getDefaultInstance();
		adjust(side, value);
		return value;
	}
	
	default void adjust(T t) { adjust(null, t); }
	
	void adjust(@Nullable EnumFacing side, T t);
	
	Capability<T> target();
}
