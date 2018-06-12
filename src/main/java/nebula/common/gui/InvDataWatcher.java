/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

import java.io.IOException;

import nebula.common.network.PacketBufferExt;
import net.minecraft.inventory.IInventory;

/**
 * @author ueyudiud
 */
public final class InvDataWatcher
{
	private final IInventory inventory;
	private final int[] array;
	
	public InvDataWatcher(IInventory inv)
	{
		this.inventory = inv;
		this.array = new int[inv.getFieldCount()];
	}
	
	public boolean serializeAll(PacketBufferExt buf)
	{
		buf.writeByte(1);
		for (int i = 0; i < this.array.length; ++i)
		{
			buf.writeInt(this.inventory.getField(i));
		}
		return true;
	}
	
	public boolean update(PacketBufferExt buf)
	{
		buf.writeByte(2);
		boolean flag = false;
		for (int i = 0; i < this.array.length; ++i)
		{
			int newValue = this.inventory.getField(i);
			if (newValue != this.array[i])
			{
				buf.writeByte(i);
				buf.writeInt(this.array[i] = newValue);
				flag = true;
			}
		}
		buf.writeByte(0xFF);
		return flag;
	}
	
	public void deserialize(PacketBufferExt buf) throws IOException
	{
		switch (buf.readByte())
		{
		case 0:
			break;
		case 1:
			for (int i = 0; i < this.inventory.getFieldCount(); ++i)
			{
				this.inventory.setField(i, this.array[i] = buf.readInt());
			}
			break;
		case 2:
			int id;
			while ((id = buf.readByte()) != -1)
			{
				this.inventory.setField(id, this.array[id] = buf.readInt());
			}
			break;
		default:
			throw new IOException();
		}
	}
}
