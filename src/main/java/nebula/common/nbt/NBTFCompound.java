/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import java.nio.ByteBuffer;

import nebula.V;
import nebula.base.collection.A;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
class NBTFCompound implements INBTFormatPart<NBTTagCompound>
{
	Info[] infos;
	
	class Info { byte t; String k; INBTFormatPart p; int length() { return 1 + this.k.length() + this.p.length(); } }
	
	@Override
	public boolean match(NBTTagCompound nbt)
	{
		if (nbt.getSize() < this.infos.length)
			return false;
		for (Info info : this.infos)
		{
			if (!nbt.hasKey(info.k))
			{
				return false;
			}
			try
			{
				if (!info.p.match(nbt.getTag(info.k)))
				{
					return false;
				}
			}
			catch (ClassCastException exception)
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void serialize(ByteBuffer buffer)
	{
		buffer.put((byte) this.infos.length);
		for (Info info : this.infos)
		{
			buffer.put(info.t);
			buffer.put((byte) info.k.length());
			buffer.put(info.k.getBytes(V.ISO_8859_1));
			info.p.serialize(buffer);
		}
	}
	
	@Override
	public void deserialize(ByteBuffer buffer)
	{
		this.infos = new Info[Byte.toUnsignedInt(buffer.get())];
		for (int i = 0; i < this.infos.length; ++i)
		{
			Info info = new Info();
			info.t = buffer.get();
			byte[] a = new byte[Byte.toUnsignedInt(buffer.get())];
			buffer.get(a);
			info.k = new String(a, V.ISO_8859_1);
			info.p = NBTFormat.deserialize1(info.t, buffer);
			this.infos[i] = info;
		}
	}
	
	@Override
	public int length()
	{
		return 1 + this.infos.length + A.sum(this.infos, Info::length);
	}
	
	@Override
	public NBTTagCompound template()
	{
		NBTTagCompound compound = new NBTTagCompound();
		for (Info info : this.infos)
		{
			compound.setTag(info.k, info.p.template());
		}
		return compound;
	}
}