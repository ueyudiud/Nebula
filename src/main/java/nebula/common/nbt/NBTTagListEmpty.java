/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import nebula.V;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * @author ueyudiud
 */
public class NBTTagListEmpty extends NBTTagList
{
	/** The empty NBT tag list. */
	public static final NBTTagList INSTANCE = new NBTTagListEmpty();
	
	private static final NBTTagEnd END = new NBTTagEnd();
	
	private NBTTagListEmpty()
	{
	}
	
	@Override
	public int getTagType()
	{
		return NBT.TAG_END;
	}
	
	@Override
	public int tagCount()
	{
		return 0;
	}
	
	@Override
	public NBTTagCompound getCompoundTagAt(int i)
	{
		throw new IndexOutOfBoundsException();
	}
	
	@Override
	public NBTBase get(int idx)
	{
		return END;
	}
	
	@Override
	public int[] getIntArrayAt(int i)
	{
		return V.INTS_EMPTY;
	}
	
	@Override
	public int getIntAt(int i)
	{
		return 0;
	}
	
	@Override
	public String getStringTagAt(int i)
	{
		return "";
	}
	
	@Override
	public float getFloatAt(int i)
	{
		return 0;
	}
	
	@Override
	public double getDoubleAt(int i)
	{
		return 0;
	}
	
	@Override
	public void appendTag(NBTBase nbt)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Return a mutable NBT.
	 */
	@Override
	public NBTTagList copy()
	{
		return new NBTTagList();
	}
	
	@Override
	public NBTBase removeTag(int i)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void set(int idx, NBTBase nbt)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean hasNoTags()
	{
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return NBT.TAG_LIST ^ 1;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof NBTBase && ((NBTBase) obj).getId() == NBT.TAG_LIST && ((NBTTagList) obj).hasNoTags();
	}
}
