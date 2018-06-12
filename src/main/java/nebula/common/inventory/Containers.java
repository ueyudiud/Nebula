/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import static nebula.common.inventory.IContainer.PROCESS;
import static nebula.common.inventory.IContainer.process;

import java.util.function.BiFunction;

import nebula.common.inventory.task.Task;
import nebula.common.inventory.task.TaskBuilder;

/**
 * @author ueyudiud
 */
public class Containers<C extends IContainer<T>, T> implements IContainers<T>
{
	@SuppressWarnings("unused")
	protected final IStackHandler<T> handler;
	protected final C[] containers;
	protected C[] simulated;
	
	public Containers(IStackHandler<T> handler, C[] containers)
	{
		this.handler = handler;
		this.containers = containers;
	}
	
	protected void prepareSimulated()
	{
		if (this.simulated == null)
		{
			this.simulated = this.containers.clone();
			for (int i = 0; i < this.containers.length; ++i)
			{
				this.simulated[i] = (C) this.containers[i].simulated();
			}
		}
		else
		{
			for (IContainer<? extends T> container : this.simulated)
			{
				container.refresh();
			}
		}
	}
	
	@Override
	public void clear()
	{
		for (int i = 0; i < this.containers.length; this.containers[i ++].clear());
	}
	
	@Override
	public int getContainerSize()
	{
		return this.containers.length;
	}
	
	@Override
	public C[] getContainers()
	{
		return this.containers;
	}
	
	@Override
	public C getContainer(int index)
	{
		return this.containers[index];
	}
	
	@Override
	public boolean insertAllStackShaped(T[] stacks, int modifier)
	{
		return taskInsertAllShaped(stacks, modifier).invoke();
	}
	
	@Override
	public boolean insertAllStackShapeless(T[] stacks, int modifier)
	{
		return taskInsertAllShapeless(stacks, modifier).invoke();
	}
	
	@Override
	public boolean insertAllStackShapeless(Iterable<? extends T> stacks, int modifier)
	{
		return taskInsertAllShapeless(stacks, modifier).invoke();
	}
	
	@Override
	public boolean extractAllStackShaped(T[] stacks, int modifier)
	{
		return taskExtractAllShaped(stacks, modifier).invoke();
	}
	
	@Override
	public boolean extractAllStackShapeless(T[] stacks, int modifier)
	{
		return taskExtractAllShapeless(stacks, modifier).invoke();
	}
	
	@Override
	public boolean extractAllStackShapeless(Iterable<? extends T> stacks, int modifier)
	{
		return taskExtractAllShapeless(stacks, modifier).invoke();
	}
	
	protected <M> Task.TaskBTB taskShaped(M[] array, BiFunction<? super C, ? super M, ? extends Task.TaskBTB> function)
	{
		if (array.length > this.containers.length)
		{
			return Task.fail();
		}
		TaskBuilder.TaskBuilderBool builder = TaskBuilder.builder();
		for (int i = 0; i < this.containers.length; ++i)
		{
			builder.add(function.apply(this.containers[i], array[i]));
		}
		return builder.asTask();
	}
	
	@Override
	public Task.TaskBTB taskInsertAllShaped(T[] stacks, int modifier)
	{
		final int m1 = modifier | PROCESS;
		return taskShaped(stacks, (c, s) -> c.taskIncr(s, m1));
	}
	
	protected <M> Task.TaskBTB taskShapelessInsert(M[] stacks, int modifier, BiFunction<? super C, M, M> function)
	{
		boolean process = process(modifier);
		return n -> {
			prepareSimulated();
			label: for (M stack : stacks)
			{
				for (C container : this.simulated)
				{
					if (container.hasStackInContainer())
					{
						if ((stack = function.apply(container, stack)) == null)
							continue label;
					}
				}
				for (C container : this.simulated)
				{
					if (!container.hasStackInContainer())
					{
						if ((stack = function.apply(container, stack)) == null)
							continue label;
					}
				}
				return false;
			}
			if (n.test())
			{
				if (process)
				{
					for (IContainer<T> container : this.simulated)
					{
						container.merge();
					}
				}
				return true;
			}
			return false;
		};
	}
	
	protected <M> Task.TaskBTB taskShapelessInsert(Iterable<? extends M> stacks, int modifier, BiFunction<? super C, ? super M, M> function)
	{
		boolean process = process(modifier);
		return n -> {
			prepareSimulated();
			label: for (M stack : stacks)
			{
				for (C container : this.simulated)
				{
					if (container.hasStackInContainer())
					{
						if ((stack = function.apply(container, stack)) == null)
							continue label;
					}
				}
				for (C container : this.simulated)
				{
					if (!container.hasStackInContainer())
					{
						if ((stack = function.apply(container, stack)) == null)
							continue label;
					}
				}
				return false;
			}
			if (n.test())
			{
				if (process)
				{
					for (IContainer<T> container : this.simulated)
					{
						container.merge();
					}
				}
				return true;
			}
			return false;
		};
	}
	
	@Override
	public Task.TaskBTB taskInsertAllShapeless(T[] stacks, int modifier)
	{
		final int m1 = modifier | PROCESS;
		return taskShapelessInsert(stacks, modifier, (c, m) -> c.insertStack(m, m1));
	}
	
	@Override
	public Task.TaskBTB taskInsertAllShapeless(Iterable<? extends T> stacks, int modifier)
	{
		final int m1 = modifier | PROCESS;
		return this.<T>taskShapelessInsert(stacks, modifier, (c, m) -> c.insertStack(m, m1));
	}
	
	@Override
	public Task.TaskBTB taskExtractAllShaped(T[] stacks, int modifier)
	{
		final int m1 = modifier | PROCESS;
		return taskShaped(stacks, (c, m) -> c.taskDecr(m, m1));
	}
	
	protected <M> Task.TaskBTB taskShapeless(M[] stacks, int modifier, BiFunction<? super C, M, M> function)
	{
		boolean process = process(modifier);
		return n -> {
			prepareSimulated();
			label: for (M stack : stacks)
			{
				for (C container : this.simulated)
				{
					if ((stack = function.apply(container, stack)) == null)
						continue label;
				}
				return false;
			}
			if (n.test())
			{
				if (process)
				{
					for (IContainer<T> container : this.simulated)
					{
						container.merge();
					}
				}
				return true;
			}
			return false;
		};
	}
	
	protected <M> Task.TaskBTB taskShapeless(Iterable<? extends M> stacks, int modifier, BiFunction<? super C, ? super M, M> function)
	{
		boolean process = process(modifier);
		return n -> {
			prepareSimulated();
			label: for (M stack : stacks)
			{
				for (C container : this.simulated)
				{
					if ((stack = function.apply(container, stack)) == null)
						continue label;
				}
				return false;
			}
			if (n.test())
			{
				if (process)
				{
					for (IContainer<T> container : this.simulated)
					{
						container.merge();
					}
				}
				return true;
			}
			return false;
		};
	}
	
	@Override
	public Task.TaskBTB taskExtractAllShapeless(T[] stacks, int modifier)
	{
		final int m1 = modifier | PROCESS;
		return taskShapeless(stacks, modifier, (c, m) -> c.extractStack(m, m1));
	}
	
	@Override
	public Task.TaskBTB taskExtractAllShapeless(Iterable<? extends T> stacks, int modifier)
	{
		final int m1 = modifier | PROCESS;
		return this.<T>taskShapeless(stacks, modifier, (c, m) -> c.extractStack(m, m1));
	}
}
