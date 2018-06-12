/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import nebula.common.stack.Subitem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
public class ItemContainerSimpleSimulated extends ItemContainerSimple
{
	protected ItemContainerSimple container;
	protected ItemStack stack;
	
	protected ItemContainerSimpleSimulated(ItemContainerSimple container)
	{
		super(container.capacity);
		this.container = container;
		this.stack = container.get();
		this.modCount = container.modCount;
	}
	
	@Override
	public NBTTagCompound writeTo(NBTTagCompound nbt, String name)
	{
		return nbt;
	}
	
	@Override
	public void writeTo(NBTTagCompound nbt)
	{
		
	}
	
	@Override
	public void readFrom(NBTTagCompound nbt, String key)
	{
		
	}
	
	@Override
	public void readFrom(NBTTagCompound nbt)
	{
		
	}
	
	@Override
	public boolean isSimulated()
	{
		return true;
	}
	
	@Override
	public boolean isAvailable(ItemStack stack)
	{
		return this.container.isAvailable(stack);
	}
	
	@Override
	public boolean isAvailable(Subitem item)
	{
		return this.container.isAvailable(item);
	}
	
	@Override
	protected ItemStack get()
	{
		if (this.modCount < this.container.modCount)
		{
			this.stack = this.container.get();
			this.modCount = this.container.modCount;
		}
		return this.stack;
	}
	
	@Override
	protected void add(int size)
	{
		get().stackSize += size;
	}
	
	@Override
	protected void set(ItemStack stack)
	{
		this.stack = stack;
	}
	
	@Override
	public void merge()
	{
		if (this.modCount > this.container.modCount)
		{
			this.container.set(this.stack);
			this.container.refresh();
			this.modCount = this.container.modCount;
		}
		else
		{
			this.stack = this.container.get();
			this.modCount = this.container.modCount;
		}
	}
	
	@Override
	public void refresh()
	{
		this.stack = this.container.get();
	}
}
