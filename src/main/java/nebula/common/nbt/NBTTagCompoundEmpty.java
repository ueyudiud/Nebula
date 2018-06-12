/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableSet;

import nebula.V;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * The simulated empty NBT.<p>
 * This NBT has no tag exist and it which is immutable,
 * used to be as a result when getting a read-only NBT.
 * 
 * @author ueyudiud
 * @see net.minecraft.nbt.NBTTagCompound
 */
public class NBTTagCompoundEmpty extends NBTTagCompound
{
	/** The empty NBT tag. */
	public static final NBTTagCompoundEmpty INSTANCE = new NBTTagCompoundEmpty();
	
	private NBTTagCompoundEmpty()
	{
	}
	
	@Override
	public boolean getBoolean(String tag)
	{
		return false;
	}
	
	@Override
	public byte getByte(String tag)
	{
		return 0;
	}
	
	@Override
	public short getShort(String tag)
	{
		return 0;
	}
	
	@Override
	public int getInteger(String tag)
	{
		return 0;
	}
	
	@Override
	public long getLong(String tag)
	{
		return 0L;
	}
	
	@Override
	public float getFloat(String tag)
	{
		return 0F;
	}
	
	@Override
	public double getDouble(String tag)
	{
		return 0D;
	}
	
	@Override
	public byte[] getByteArray(String tag)
	{
		return V.BYTES_EMPTY;
	}
	
	@Override
	public int[] getIntArray(String tag)
	{
		return V.INTS_EMPTY;
	}
	
	@Override
	public String getString(String tag)
	{
		return "";
	}
	
	@Override
	public NBTTagCompound getCompoundTag(String tag)
	{
		return INSTANCE;
	}
	
	@Override
	public NBTTagList getTagList(String tag, int id)
	{
		return NBTTagListEmpty.INSTANCE;
	}
	
	@Override
	public NBTBase getTag(String tag)
	{
		return null;
	}
	
	@Override
	public void setTag(String tag, NBTBase nbt)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setBoolean(String tag, boolean value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setByte(String tag, byte value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setShort(String tag, short value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setInteger(String tag, int value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setLong(String tag, long value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setFloat(String tag, float value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setDouble(String tag, double value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setString(String tag, String value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setByteArray(String tag, byte[] value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setIntArray(String tag, int[] value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setUniqueId(String key, UUID value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean hasKey(String tag, int value)
	{
		return false;
	}
	
	@Override
	public boolean hasKey(String tag)
	{
		return false;
	}
	
	@Override
	public boolean hasNoTags()
	{
		return true;
	}
	
	@Override
	public Set<String> getKeySet()
	{
		return ImmutableSet.of();
	}
	
	@Override
	public void removeTag(String key)
	{
	}
	
	@Override
	public int hashCode()
	{
		return NBT.TAG_COMPOUND ^ 1;
	}
	
	@Override
	public void merge(NBTTagCompound other)
	{
		throw new UnsupportedOperationException("This is only a immutable NBTTagCompound, you can't take any operation on it!");
	}
	
	@Override
	public int getSize()
	{
		return 0;
	}
	
	@Override
	public boolean hasUniqueId(String key)
	{
		return false;
	}
	
	@Override
	public UUID getUniqueId(String key)
	{
		return new UUID(0L, 0L);
	}
	
	@Override
	public byte getTagId(String key)
	{
		return 0;
	}
	
	@Override
	public boolean equals(Object object)
	{
		return object instanceof NBTTagCompound && ((NBTTagCompound) object).hasNoTags();
	}
	
	/**
	 * Return a mutable NBT.
	 */
	@Override
	public NBTTagCompound copy()
	{
		return new NBTTagCompound();
	}
	
	@Override
	public String toString()
	{
		return "{}";
	}
}
