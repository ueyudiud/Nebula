/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import static nebula.common.inventory.IContainer.fully;
import static nebula.common.inventory.IContainer.process;
import static nebula.common.inventory.IContainer.skipAvailableCheck;

import nebula.common.inventory.task.Task;
import nebula.common.stack.Subfluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
public abstract class FluidContainerSimple extends ContainerSimple<FluidStack> implements IFluidContainerSingle
{
	protected FluidContainerSimple(int capacity)
	{
		super(IStackHandler.FLUIDSTACK_HANDLER, capacity);
	}
	
	protected abstract void add(int size);
	
	@Override
	public FluidContainerSimpleSimulated simulated()
	{
		return new FluidContainerSimpleSimulated(this);
	}
	
	@Override
	public boolean isAvailable(Subfluid fluid)
	{
		return true;
	}
	
	@Override
	public int incrStack(Subfluid fluid, int amount, int modifier)
	{
		if (amount <= 0 || (!skipAvailableCheck(modifier) && !isAvailable(fluid)))
		{
			return 0;
		}
		else if (get() == null)
		{
			if (amount > this.capacity)
			{
				if (fully(modifier))
				{
					return 0;
				}
				if (process(modifier))
				{
					set(fluid.stack(this.capacity));
					onContainerChanged(modifier);
				}
				return this.capacity;
			}
			else
			{
				if (process(modifier))
				{
					set(fluid.stack(amount));
					onContainerChanged(modifier);
				}
				return amount;
			}
		}
		else if (fluid.match(get()))
		{
			int rem = this.capacity - get().amount;
			if (amount > rem)
			{
				if (fully(modifier))
				{
					return 0;
				}
				if (process(modifier))
				{
					add(rem);
					onContainerChanged(modifier);
				}
				return rem;
			}
			else
			{
				if (process(modifier))
				{
					add(amount);
					onContainerChanged(modifier);
				}
				return amount;
			}
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public int decrStack(Subfluid fluid, int amount, int modifier)
	{
		if (amount <= 0 || get() == null)
		{
			return 0;
		}
		else if (fluid.match(get()))
		{
			if (get().amount < amount)
			{
				if (fully(modifier))
				{
					return 0;
				}
				int result = get().amount;
				if (process(modifier))
				{
					set(null);
					onContainerChanged(modifier);
				}
				return result;
			}
			else
			{
				if (process(modifier))
				{
					add(-amount);
					onContainerChanged(modifier);
				}
				return amount;
			}
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public Task.TaskBTB taskIncr(Subfluid fluid, int amount, int modifier)
	{
		return amount <= 0 ? Task.pass() : access -> {
			if (!skipAvailableCheck(modifier) && !isAvailable(fluid))
			{
				return false;
			}
			else if (get() == null)
			{
				if (amount > this.capacity)
				{
					return false;
				}
				else
				{
					if (process(modifier))
					{
						set(fluid.stack(amount));
						onContainerChanged(modifier);
					}
					return true;
				}
			}
			else if (fluid.match(get()))
			{
				int rem = this.capacity - get().amount;
				if (amount > rem)
				{
					return false;
				}
				else
				{
					if (process(modifier))
					{
						add(amount);
						onContainerChanged(modifier);
					}
					return true;
				}
			}
			else
			{
				return false;
			}
		};
	}
	
	@Override
	public Task.TaskBTB taskDecr(Subfluid fluid, int amount, int modifier)
	{
		return amount <= 0 ? Task.pass() : access -> {
			if (get() == null)
			{
				return false;
			}
			else if (fluid.match(get()))
			{
				if (get().amount < amount || !access.test())
				{
					return false;
				}
				if (process(modifier))
				{
					if (get().amount > amount)
					{
						add(-amount);
						onContainerChanged(modifier);
					}
					else
					{
						set(null);
					}
				}
				return true;
			}
			else
			{
				return false;
			}
		};
	}
}
