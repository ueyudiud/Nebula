/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory.task;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;

import nebula.common.inventory.task.Task.TaskBTB;
import nebula.common.inventory.task.TaskList.TaskListBool;
import nebula.common.inventory.task.TaskList.TaskListLong;
import nebula.common.inventory.task.TaskList.TaskListRef;

/**
 * @author ueyudiud
 */
public interface Task
{
	static TaskBTB fail()
	{
		return __ -> false;
	}
	
	static TaskBTB pass()
	{
		return __ -> true;
	}
	
	interface TaskBTB extends Task
	{
		boolean invoke(TaskListBool list) throws InvocationTargetException;
		
		default boolean invoke()
		{
			try
			{
				return invoke(TaskList.taskListFromTrue());
			}
			catch (InvocationTargetException exception)
			{
				throw (RuntimeException) exception.getTargetException();
			}
		}
	}
	
	interface TaskLTL extends Task
	{
		long invoke(TaskListLong list) throws InvocationTargetException;
	}
	
	interface TaskOTO<T1, T2> extends Task
	{
		T2 invoke(TaskListRef<T1> list) throws InvocationTargetException;
	}
	
	interface TaskBTO<T> extends Task
	{
		T invoke(TaskListBool list) throws InvocationTargetException;
	}
	
	interface TaskBTL extends Task
	{
		long invoke(TaskListBool list) throws InvocationTargetException;
	}
	
	interface TaskOTB<T> extends Task
	{
		boolean invoke(TaskListRef<T> list) throws InvocationTargetException;
	}
	
	interface TaskOTL<T> extends Task
	{
		long invoke(TaskListRef<T> list) throws InvocationTargetException;
	}
	
	interface TaskLTB extends Task
	{
		boolean invoke(TaskListLong list) throws InvocationTargetException;
	}
	
	interface TaskLTO<T> extends Task
	{
		T invoke(TaskListLong list) throws InvocationTargetException;
	}
}

class LinkedTaskBTB extends TaskListInstance implements TaskBTB
{
	private TaskListBool next;
	
	LinkedTaskBTB(MethodHandle[] tasks)
	{
		this.tasks = tasks;
	}
	
	@Override
	public boolean test() throws InvocationTargetException
	{
		return this.i == this.tasks.length ? this.next.test() : super.test();
	}
	
	@Override
	public void reset()
	{
		super.reset();
		this.next = null;
	}
	
	@Override
	public boolean invoke(TaskListBool list) throws InvocationTargetException
	{
		this.next = list;
		boolean result = run();
		reset();
		return result;
	}
}
