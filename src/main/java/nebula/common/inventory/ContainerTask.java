/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BooleanSupplier;

import nebula.base.function.F;
import nebula.common.inventory.task.Task;
import nebula.common.inventory.task.TaskBuilder;
import nebula.common.inventory.task.TaskBuilder.TaskBuilderBool;
import nebula.common.inventory.task.TaskList;

/**
 * @author ueyudiud
 */
@Deprecated
public interface ContainerTask
{
	boolean access(BooleanSupplier next);
	
	default boolean access()
	{
		return access(F.BS_T);
	}
	
	static ContainerTask pass()
	{
		return BooleanSupplier::getAsBoolean;
	}
	
	static ContainerTask failback()
	{
		return __ -> false;
	}
	
	static TasksBuilder builder()
	{
		return new TasksBuilder();
	}
	
	@Deprecated
	class TasksBuilder
	{
		private TaskBuilderBool builder = TaskBuilder.builder();
		
		public void add(ContainerTask task)
		{
			builder.add((Task.TaskBTB) n -> task.access(() -> {
				try
				{
					return n.test();
				}
				catch (InvocationTargetException e)
				{
					throw (RuntimeException) e.getTargetException();
				}
			}));
		}
		
		public void add(Task.TaskBTB task)
		{
			builder.add(task);
		}
		
		public ContainerTask link()
		{
			Task.TaskBTB task = builder.asTask();
			return n -> {
				try
				{
					return task.invoke(n::getAsBoolean);
				}
				catch (InvocationTargetException e)
				{
					throw (RuntimeException) e.getTargetException();
				}
			};
		}
		
		public BooleanSupplier build()
		{
			return build2()::run;
		}
		
		public TaskList.TaskListBool build2()
		{
			return builder.build();
		}
	}
}