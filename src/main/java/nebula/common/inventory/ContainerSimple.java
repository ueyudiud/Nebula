/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import static nebula.common.inventory.IContainer.fully;
import static nebula.common.inventory.IContainer.process;
import static nebula.common.inventory.IContainer.skipAvailableCheck;
import static nebula.common.inventory.IContainer.skipRefresh;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import nebula.common.inventory.task.Task;
import nebula.common.nbt.INBTSelfCompoundReaderAndWriter;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
public abstract class ContainerSimple<T> implements IContainerSingle<T>, INBTSelfCompoundReaderAndWriter
{
	private final IStackHandler<T> handler;
	protected final int capacity;
	protected int modCount = 0;
	
	protected ContainerSimple(IStackHandler<T> handler, int capacity)
	{
		this.handler = handler;
		this.capacity = capacity;
	}
	
	protected abstract T get();
	
	protected abstract void set(T stack);
	
	@Override
	public void readFrom(NBTTagCompound nbt)
	{
		set(this.handler.readFrom(nbt));
	}
	
	@Override
	public void writeTo(NBTTagCompound nbt)
	{
		this.handler.writeTo(get(), nbt);
	}
	
	@Override
	public void readFrom(NBTTagCompound nbt, String key)
	{
		set(this.handler.readFrom(nbt, key));
	}
	
	@Override
	public NBTTagCompound writeTo(NBTTagCompound nbt, String key)
	{
		return this.handler.writeTo(nbt, key, get());
	}
	
	@Override
	public T getStackInContainer()
	{
		return this.handler.copy(get());
	}
	
	@Override
	public boolean hasStackInContainer()
	{
		return get() != null;
	}
	
	@Override
	public void clear()
	{
		set(null);
	}
	
	@Override
	public Collection<T> stacks()
	{
		return get() == null ? ImmutableList.of() : ImmutableList.of(get());
	}
	
	@Override
	public T stack()
	{
		return get();
	}
	
	@Override
	public void merge()
	{
		
	}
	
	@Override
	public void refresh()
	{
		onContainerChanged();
	}
	
	protected final void onContainerChanged(int modifier)
	{
		if (!skipRefresh(modifier))
		{
			onContainerChanged();
		}
	}
	
	protected void onContainerChanged()
	{
		this.modCount ++;
	}
	
	public abstract ContainerSimple<T> simulated();
	
	@Override
	public boolean isAvailable(T stack)
	{
		return true;
	}
	
	@Override
	public void setStackInContainer(T stack)
	{
		set(stack);
	}
	
	protected int getMaxCapacityFor(T stack)
	{
		return this.capacity;
	}
	
	public int getCapacity()
	{
		return this.capacity;
	}
	
	public int getStackSizeInContainer()
	{
		return this.handler.size(get());
	}
	
	public int getRemainCapacityInContainer()
	{
		return get() == null ? this.capacity : getMaxCapacityFor(get()) - this.handler.size(get());
	}
	
	public int incrStack(T stack, int modifier)
	{
		if ((stack = this.handler.validate(stack)) == null || (!skipAvailableCheck(modifier) && !isAvailable(stack)))
		{
			return 0;
		}
		else if (get() == null)
		{
			if (this.handler.size(stack) > this.capacity)
			{
				if (fully(modifier))
				{
					return 0;
				}
				if (process(modifier))
				{
					set(this.handler.copy(stack, this.capacity));
					onContainerChanged(modifier);
				}
				return this.capacity;
			}
			if (process(modifier))
			{
				set(this.handler.copy(stack));
				onContainerChanged(modifier);
			}
			return this.handler.size(stack);
		}
		else if (this.handler.isSimilar(get(), stack))
		{
			int rem = getRemainCapacityInContainer();
			if (this.handler.size(stack) > rem)
			{
				if (fully(modifier))
				{
					return 0;
				}
				if (process(modifier))
				{
					this.handler.add(get(), rem);
					onContainerChanged(modifier);
				}
				return rem;
			}
			else
			{
				if (process(modifier))
				{
					this.handler.add(get(), this.handler.size(stack));
					onContainerChanged(modifier);
				}
				return this.handler.size(stack);
			}
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public int decrStack(T stack, int modifier)
	{
		if ((stack = this.handler.validate(stack)) == null || get() == null)
		{
			return 0;
		}
		else if (this.handler.isSimilar(get(), stack))
		{
			int i = this.handler.size(get()) - this.handler.size(stack);
			if (i < 0)
			{
				if (fully(modifier))
				{
					return 0;
				}
				int result = this.handler.size(get());
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
					if (i == 0)
						set(null);
					else
						this.handler.size(get(), i);
					onContainerChanged(modifier);
				}
				return this.handler.size(stack);
			}
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public T decrStack(int size, int modifier)
	{
		if (size <= 0 || get() == null)
		{
			return null;
		}
		else if (get() != null)
		{
			int i = this.handler.size(get()) - size;
			if (i <= 0)
			{
				if (i < 0 && fully(modifier))
				{
					return null;
				}
				if (process(modifier))
				{
					T result = get();
					set(null);
					onContainerChanged(modifier);
					return result;
				}
				else
				{
					return this.handler.copy(get());
				}
			}
			else
			{
				if (process(modifier))
				{
					this.handler.size(get(), i);
					onContainerChanged(modifier);
				}
				return this.handler.copy(get(), size);
			}
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public Task.TaskBTB taskRefresh()
	{
		return access -> {
			if (access.test())
			{
				refresh();
				return true;
			}
			return false;
		};
	}
	
	@Override
	public T insertStack(T stack, int modifier)
	{
		int amt = incrStack(stack, modifier);
		return amt == 0 ? stack :
			this.handler.size(stack) == amt ? null :
				this.handler.copy(stack, this.handler.size(stack) - amt);
	}
	
	@Override
	public T extractStack(T stack, int modifier)
	{
		if (stack == null)//Extract any mode.
		{
			if (process(modifier))
			{
				set(null);
				onContainerChanged(modifier);
			}
			return null;
		}
		else//Extract specific mode.
		{
			int s = this.handler.size(stack);
			int r = decrStack(stack, modifier);
			return r == s ? null :
				r == 0 ? stack :
					this.handler.copy(stack, s - r);
		}
	}
	
	@Override
	public T extractStack(int size, int modifier)
	{
		if (get() == null)
		{
			return null;
		}
		if (size <= this.handler.size(get()))
		{
			T stack = this.handler.copy(get(), size);
			if (process(modifier))
			{
				if (size == this.handler.size(get()))
				{
					set(null);
				}
				else
				{
					this.handler.sub(get(), size);
				}
				onContainerChanged(modifier);
			}
			return stack;
		}
		else
		{
			if (fully(modifier))
			{
				return null;
			}
			T stack = get();
			if (process(modifier))
			{
				set(null);
				onContainerChanged(modifier);
			}
			else
			{
				stack = this.handler.copy(stack);
			}
			return stack;
		}
	}
	
	@Override
	public Task.TaskBTB taskIncr(T stackRaw, int modifier)
	{
		return stackRaw == null ? Task.pass() : access -> {
			T stack = stackRaw;
			if ((stack = this.handler.validate(stack)) == null || (!skipAvailableCheck(modifier) && !isAvailable(stack)))
			{
				return false;
			}
			else if (get() == null)
			{
				if (this.handler.size(stack) > this.capacity || !access.test())
				{
					return false;
				}
				if (process(modifier))
				{
					set(this.handler.copy(stack));
					onContainerChanged(modifier);
				}
				return true;
			}
			else if (this.handler.isSimilar(get(), stack))
			{
				int rem = getRemainCapacityInContainer();
				if (this.handler.size(stack) > rem || !access.test())
				{
					return false;
				}
				if (process(modifier))
				{
					this.handler.add(get(), this.handler.size(stack));
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
	public Task.TaskBTB taskDecr(T stack, int modifier)
	{
		return stack == null || this.handler.size(stack) <= 0 ? Task.pass() : access -> {
			if (this.handler.contains(stack, get()) && access.test())
			{
				if (process(modifier))
				{
					if (get() != null)
					{
						int size = this.handler.size(stack);
						if (size == this.handler.size(get()))
						{
							set(null);
						}
						else
						{
							this.handler.sub(get(), size);
						}
						onContainerChanged(modifier);
					}
				}
				return true;
			}
			return false;
		};
	}
	
	@Override
	public Task.TaskBTB taskDecr(int size, int modifier)
	{
		return size <= 0 ? Task.pass() : access -> {
			if (get() != null && size <= this.handler.size(get()))
			{
				if (!access.test())
				{
					return false;
				}
				if (process(modifier))
				{
					if (size == this.handler.size(get()))
					{
						set(null);
					}
					else
					{
						this.handler.sub(get(), size);
					}
					onContainerChanged(modifier);
				}
				return true;
			}
			return false;
		};
	}
}
