/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory.task;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;

import nebula.common.inventory.task.TaskList.TaskListBool;
import nebula.common.inventory.task.TaskList.TaskListLong;
import nebula.common.inventory.task.TaskList.TaskListRef;

/**
 * @author ueyudiud
 */
public interface TaskList
{
	static TaskListBool taskListFromTrue()
	{
		return () -> true;
	}
	
	default void reset()
	{
		
	}
	
	default boolean run()
	{
		return true;
	}
	
	interface TaskListRef<T> extends TaskList
	{
		T result() throws InvocationTargetException;
	}
	
	interface TaskListLong extends TaskList
	{
		long resultl() throws InvocationTargetException;
	}
	
	interface TaskListBool extends TaskList
	{
		boolean test() throws InvocationTargetException;
	}
}

class TaskListInstance implements TaskListRef, TaskListLong, TaskListBool
{
	MethodHandle[] tasks;
	int i;
	
	@Override
	public void reset()
	{
		this.i = 0;
	}
	
	@Override
	public boolean run()
	{
		assert this.i == 0;
		try
		{
			return test();
		}
		catch (InvocationTargetException exception)
		{
			throw (RuntimeException) exception.getTargetException();
		}
	}
	
	@Override
	public boolean test() throws InvocationTargetException
	{
		if (this.i == this.tasks.length)
		{
			this.i = this.tasks.length;
			return true;
		}
		try
		{
			return (boolean) this.tasks[this.i ++].invoke(this);
		}
		catch (Throwable t)
		{
			throw new InvocationTargetException(t);
		}
	}
	
	@Override
	public long resultl() throws InvocationTargetException
	{
		try
		{
			return this.i == this.tasks.length - 1 ?
					(long) this.tasks[this.i ++].invoke() :
						(long) this.tasks[this.i ++].invoke(this);
		}
		catch (Throwable t)
		{
			throw new InvocationTargetException(t);
		}
	}
	
	@Override
	public Object result() throws InvocationTargetException
	{
		try
		{
			return this.i == this.tasks.length - 1 ?
					this.tasks[this.i ++].invoke() :
						this.tasks[this.i ++].invoke(this);
		}
		catch (Throwable t)
		{
			throw new InvocationTargetException(t);
		}
	}
}
