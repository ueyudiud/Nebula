/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import nebula.common.stack.FS;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
public class FluidContainerArraySimple extends FluidContainerSimple
{
	final FluidStack[] stacks;
	private final int id;
	
	public static FluidContainerArraySimple[] create(int size, int limit)
	{
		return create(new FluidStack[size], limit);
	}
	
	public static FluidContainerArraySimple[] create(FluidStack[] stacks, int limit)
	{
		FluidContainerArraySimple[] result = new FluidContainerArraySimple[stacks.length];
		for (int i = 0; i < stacks.length; ++i)
		{
			result[i] = new FluidContainerArraySimple(stacks, i, limit);
		}
		return result;
	}
	
	public FluidContainerArraySimple(FluidStack[] stacks, int id, int limit)
	{
		super(limit);
		this.stacks = stacks;
		this.id = id;
	}
	
	@Override
	protected FluidStack get()
	{
		if (this.stacks[this.id] != null && this.stacks[this.id].amount <= 0)
		{
			this.stacks[this.id] = null;
		}
		return this.stacks[this.id];
	}
	
	@Override
	protected void add(int amount)
	{
		this.stacks[this.id].amount += amount;
	}
	
	@Override
	protected void set(FluidStack stack)
	{
		this.stacks[this.id] = FS.copy(stack);
	}
}
