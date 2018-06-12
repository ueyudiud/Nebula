/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import nebula.common.stack.FS;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
public class FluidContainerSingle extends FluidContainerSimple implements IContainerSingle<FluidStack>
{
	private FluidStack stack;
	
	public FluidContainerSingle(int capacity)
	{
		super(capacity);
	}
	
	@Override
	protected void add(int size)
	{
		this.stack.amount += size;
	}
	
	@Override
	protected FluidStack get()
	{
		if (this.stack != null && this.stack.amount <= 0)
		{
			this.stack = null;
		}
		return this.stack;
	}
	
	@Override
	protected void set(FluidStack stack)
	{
		this.stack = FS.copy(stack);
	}
}
