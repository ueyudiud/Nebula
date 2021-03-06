/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nebula.V;
import net.minecraft.nbt.*;

public class NBTSynclizedCompound extends NBTTagCompound
{
	private Map<String, NBTBase>	tagMap	= new HashMap<>();
	private boolean					update;
	private NBTTagCompound			nbt;
	
	public NBTSynclizedCompound()
	{
		reset();
	}
	
	@Override
	public NBTSynclizedCompound copy()
	{
		NBTSynclizedCompound compound = new NBTSynclizedCompound();
		compound.tagMap.putAll(this.tagMap);
		return compound;
	}
	
	private void mark()
	{
		this.update = true;
	}
	
	public void reset()
	{
		this.update = false;
		this.nbt = new NBTTagCompound();
	}
	
	@Override
	public void removeTag(String name)
	{
		if (hasKey(name))
		{
			this.tagMap.remove(name);
		}
	}
	
	@Override
	public boolean hasNoTags()
	{
		return this.tagMap.isEmpty();
	}
	
	@Override
	public boolean hasKey(String name)
	{
		return this.tagMap.get(name) != null;
	}
	
	@Override
	public boolean hasKey(String name, int value)
	{
		try
		{
			NBTBase nbt;
			return (nbt = this.tagMap.get(name)) != null ? value == 99 || nbt.getId() == value : false;
		}
		catch (Exception exception)
		{
			V.warn("Fail to check key " + name + ".", exception);
			return false;
		}
	}
	
	@Override
	public NBTBase getTag(String name)
	{
		return this.tagMap.get(name);
	}
	
	@Override
	public NBTTagCompound getCompoundTag(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? NBTTagCompoundEmpty.INSTANCE : (NBTTagCompound) this.tagMap.get(name);
		}
		catch (Exception exception)
		{
			return NBTTagCompoundEmpty.INSTANCE;
		}
	}
	
	@Override
	public NBTTagList getTagList(String name, int id)
	{
		try
		{
			NBTTagList list = (NBTTagList) getTag(name);
			return id == 99 || list.getId() == id ? list : new NBTTagList();
		}
		catch (Exception exception)
		{
			return NBTTagListEmpty.INSTANCE;
		}
	}
	
	@Override
	public boolean getBoolean(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? false : ((NBTPrimitive) this.tagMap.get(name)).getByte() != 0;
		}
		catch (Exception exception)
		{
			return false;
		}
	}
	
	@Override
	public byte getByte(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0 : ((NBTPrimitive) this.tagMap.get(name)).getByte();
		}
		catch (Exception exception)
		{
			return 0;
		}
	}
	
	@Override
	public short getShort(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0 : ((NBTPrimitive) this.tagMap.get(name)).getShort();
		}
		catch (Exception exception)
		{
			return 0;
		}
	}
	
	@Override
	public int getInteger(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0 : ((NBTPrimitive) this.tagMap.get(name)).getInt();
		}
		catch (Exception exception)
		{
			return 0;
		}
	}
	
	@Override
	public long getLong(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0 : ((NBTPrimitive) this.tagMap.get(name)).getLong();
		}
		catch (Exception exception)
		{
			return 0;
		}
	}
	
	@Override
	public float getFloat(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0 : ((NBTPrimitive) this.tagMap.get(name)).getFloat();
		}
		catch (Exception exception)
		{
			return 0;
		}
	}
	
	@Override
	public double getDouble(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0 : ((NBTPrimitive) this.tagMap.get(name)).getDouble();
		}
		catch (Exception exception)
		{
			return 0;
		}
	}
	
	@Override
	public String getString(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? "" : ((NBTTagString) this.tagMap.get(name)).getString();
		}
		catch (Exception exception)
		{
			return "";
		}
	}
	
	@Override
	public byte[] getByteArray(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? new byte[0] : ((NBTTagByteArray) this.tagMap.get(name)).getByteArray();
		}
		catch (Exception exception)
		{
			return new byte[0];
		}
	}
	
	@Override
	public int[] getIntArray(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? new int[0] : ((NBTTagIntArray) this.tagMap.get(name)).getIntArray();
		}
		catch (Exception exception)
		{
			return new int[0];
		}
	}
	
	@Override
	public void setTag(String name, NBTBase tag)
	{
		try
		{
			if (hasKey(name))
			{
				if (!tag.equals(this.tagMap.get(name)))
				{
					this.tagMap.put(name, tag);
					this.nbt.setTag(name, tag);
					mark();
				}
			}
			else
			{
				this.tagMap.put(name, tag);
				this.nbt.setTag(name, tag);
				mark();
			}
		}
		catch (Exception exception)
		{
			V.error("Fail to set tag.", exception);
		}
	}
	
	@Override
	public void setBoolean(String name, boolean value)
	{
		setTag(name, new NBTTagByte((byte) (value ? 1 : 0)));
	}
	
	@Override
	public void setByte(String name, byte value)
	{
		setTag(name, new NBTTagByte(value));
	}
	
	public void setByte(String name, int value)
	{
		setTag(name, new NBTTagByte((byte) value));
	}
	
	@Override
	public void setShort(String name, short value)
	{
		setTag(name, new NBTTagShort(value));
	}
	
	public void setShort(String name, int value)
	{
		setTag(name, new NBTTagShort((short) value));
	}
	
	@Override
	public void setInteger(String name, int value)
	{
		setTag(name, new NBTTagInt(value));
	}
	
	@Override
	public void setLong(String name, long value)
	{
		setTag(name, new NBTTagLong(value));
	}
	
	@Override
	public void setFloat(String name, float value)
	{
		setTag(name, new NBTTagFloat(value));
	}
	
	@Override
	public void setDouble(String name, double value)
	{
		setTag(name, new NBTTagDouble(value));
	}
	
	@Override
	public void setString(String name, String value)
	{
		setTag(name, new NBTTagString(value));
	}
	
	@Override
	public void setByteArray(String name, byte[] value)
	{
		setTag(name, new NBTTagByteArray(value));
	}
	
	@Override
	public void setIntArray(String name, int[] value)
	{
		setTag(name, new NBTTagIntArray(value));
	}
	
	public NBTTagCompound asCompound()
	{
		NBTTagCompound ret = new NBTTagCompound();
		for (Entry<String, NBTBase> entry : this.tagMap.entrySet())
		{
			ret.setTag(entry.getKey(), entry.getValue().copy());
		}
		return ret;
	}
	
	public boolean isChanged()
	{
		return this.update;
	}
	
	public NBTTagCompound getChanged(boolean reset)
	{
		NBTTagCompound ret = this.nbt;
		if (reset)
		{
			reset();
		}
		return ret;
	}
	
	public void clear()
	{
		this.tagMap.clear();
		reset();
	}
}
