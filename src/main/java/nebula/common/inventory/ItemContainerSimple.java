/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import static nebula.common.inventory.IContainer.fully;
import static nebula.common.inventory.IContainer.process;
import static nebula.common.inventory.IContainer.skipAvailableCheck;

import nebula.common.inventory.task.Task;
import nebula.common.stack.AbstractStack;
import nebula.common.stack.BaseStack;
import nebula.common.stack.Subitem;
import nebula.common.util.L;
import net.minecraft.item.ItemStack;

/**
 * @author ueyudiud
 */
public abstract class ItemContainerSimple extends ContainerSimple<ItemStack> implements IItemContainerSingle
{
	public ItemContainerSimple(int limit)
	{
		super(IStackHandler.ITEMSTACK_HANDLER, limit);
	}
	
	@Override
	protected int getMaxCapacityFor(ItemStack stack)
	{
		return Math.min(this.capacity, stack.getMaxStackSize());
	}
	
	protected abstract void add(int size);
	
	@Override
	public boolean isAvailable(Subitem item)
	{
		return true;
	}
	
	@Override
	public ItemContainerSimpleSimulated simulated()
	{
		return new ItemContainerSimpleSimulated(this);
	}
	
	@Override
	public int incrStack(Subitem item, int size, int modifier)
	{
		if (size <= 0 || (!skipAvailableCheck(modifier) && !isAvailable(item)))
		{
			return 0;
		}
		else if (get() == null)
		{
			int size1 = L.min(size, this.capacity, item.getMaxStackSize());
			if (fully(modifier) && size != size1)
			{
				return 0;
			}
			if (process(modifier))
			{
				set(item.stack(size1));
				onContainerChanged(modifier);
			}
			return size1;
		}
		else if (item.match(get()))
		{
			int size1 = L.min(size + get().stackSize, this.capacity, item.getMaxStackSize()) - get().stackSize;
			if (fully(modifier) && size != size1)
			{
				return 0;
			}
			if (size1 > 0 && process(modifier))
			{
				add(size1);
				onContainerChanged(modifier);
			}
			return size1;
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public int decrStack(Subitem item, int size, int modifier)
	{
		if (get() == null || size <= 0)
		{
			return 0;
		}
		else if (item.match(get()))
		{
			if (fully(modifier) && get().stackSize < size)
			{
				return 0;
			}
			if (get().stackSize > size)
			{
				if (process(modifier))
				{
					add(-size);
					onContainerChanged(modifier);
				}
				return size;
			}
			else
			{
				size = get().stackSize;
				if (process(modifier))
				{
					set(null);
					onContainerChanged(modifier);
				}
				return size;
			}
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public boolean insertStack(AbstractStack stack, int modifier)
	{
		return taskIncr(stack, modifier).invoke();
	}
	
	@Override
	public boolean extractStack(AbstractStack stack, int modifier)
	{
		return taskDecr(stack, modifier).invoke();
	}
	
	@Override
	public Task.TaskBTB taskIncr(Subitem item, int size, int modifier)
	{
		return taskIncr(new BaseStack(item, size), modifier);
	}
	
	@Override
	public Task.TaskBTB taskIncr(AbstractStack stack, int modifier)
	{
		return stack == null || !stack.valid() ? Task.pass() : access -> {
			if (stack == null || !stack.valid())
			{
				return true;
			}
			else if (get() == null)
			{
				ItemStack s = stack.instance();
				if (skipAvailableCheck(modifier) || !isAvailable(s))
				{
					if (s.stackSize > this.capacity || s.stackSize > s.getMaxStackSize() || !access.test())
					{
						return false;
					}
					if ((modifier & PROCESS) != 0)
					{
						set(s);
						onContainerChanged(modifier);
					}
					return true;
				}
				return false;
			}
			else if (stack.similar(get()))
			{
				int i = stack.size(get());
				if (i > getRemainCapacityInContainer() || !access.test())
				{
					return false;
				}
				if (process(modifier))
				{
					add(i);
					onContainerChanged(modifier);
				}
				return true;
			}
			else
			{
				return false;
			}
		};
	}
	
	@Override
	public Task.TaskBTB taskDecr(Subitem item, int size, int modifier)
	{
		return taskDecr(new BaseStack(item, size), modifier);
	}
	
	@Override
	public Task.TaskBTB taskDecr(AbstractStack stack, int modifier)
	{
		return stack == null || !stack.valid() ? Task.pass() : access -> {
			if (get() == null)
			{
				return false;
			}
			else if (stack.contain(get()) && access.test())
			{
				if (process(modifier))
				{
					int i = stack.size(get());
					if (i < get().stackSize)
					{
						add(-i);
					}
					else
					{
						set(null);
					}
					onContainerChanged(modifier);
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
