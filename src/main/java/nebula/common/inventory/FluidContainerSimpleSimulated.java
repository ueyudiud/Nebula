/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import nebula.common.stack.Subfluid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
public class FluidContainerSimpleSimulated extends FluidContainerSimple
{
	protected FluidContainerSimple container;
	protected FluidStack stack;
	
	protected FluidContainerSimpleSimulated(FluidContainerSimple container)
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
	public boolean isAvailable(FluidStack stack)
	{
		return this.container.isAvailable(stack);
	}
	
	@Override
	public boolean isAvailable(Subfluid fluid)
	{
		return this.container.isAvailable(fluid);
	}
	
	@Override
	protected FluidStack get()
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
		get().amount += size;
	}
	
	@Override
	protected void set(FluidStack stack)
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
