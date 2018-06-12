/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory.task;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

import nebula.common.inventory.task.Task.*;
import nebula.common.inventory.task.TaskBuilder.TaskBuilderBool;
import nebula.common.inventory.task.TaskBuilder.TaskBuilderLong;
import nebula.common.inventory.task.TaskBuilder.TaskBuilderRef;
import nebula.common.inventory.task.TaskList.TaskListBool;
import nebula.common.inventory.task.TaskList.TaskListLong;
import nebula.common.inventory.task.TaskList.TaskListRef;

/**
 * @author ueyudiud
 */
public interface TaskBuilder
{
	static TaskBuilderBool builder()
	{
		return new Instance();
	}
	
	interface TaskBuilderBool extends TaskBuilder
	{
		TaskBuilderBool add(TaskBTB task);
		
		TaskBuilderLong add(TaskBTL task);
		
		<T> TaskBuilderRef<T> add(TaskBTO<T> task);
		
		TaskListBool build();
		
		TaskBTB asTask();
	}
	
	interface TaskBuilderLong extends TaskBuilder
	{
		TaskBuilderBool add(TaskLTB task);
		
		TaskBuilderLong add(TaskLTL task);
		
		<T> TaskBuilderRef<T> add(TaskLTO<T> task);
		
		TaskListBool build(long start);
	}
	
	interface TaskBuilderRef<T> extends TaskBuilder
	{
		TaskBuilderBool add(TaskOTB<? super T> task);
		
		TaskBuilderLong add(TaskOTL<? super T> task);
		
		<R> TaskBuilderRef<R> add(TaskOTO<? super T, R> task);
		
		TaskListBool build(T start);
	}
}

class Instance implements TaskBuilderLong, TaskBuilderBool, TaskBuilderRef
{
	private static final MethodHandles.Lookup LOOKUP = MethodHandles.publicLookup();
	
	private static final int INITALIZE_SIZE = 4;
	
	private static final MethodType
	OTB = MethodType.methodType(TaskListRef.class, boolean.class),
	OTO = MethodType.methodType(TaskListRef.class, Object.class),
	OTL = MethodType.methodType(TaskListRef.class, long.class),
	BTB = MethodType.methodType(TaskListBool.class, boolean.class),
	BTO = MethodType.methodType(TaskListBool.class, Object.class),
	BTL = MethodType.methodType(TaskListBool.class, long.class),
	LTB = MethodType.methodType(TaskListLong.class, boolean.class),
	LTO = MethodType.methodType(TaskListLong.class, Object.class),
	LTL = MethodType.methodType(TaskListLong.class, long.class);
	
	int i = 0;
	MethodHandle[] handles = new MethodHandle[INITALIZE_SIZE];
	
	private Instance add(Task task, MethodType type)
	{
		try
		{
			if (this.i == this.handles.length)
			{
				this.handles = Arrays.copyOf(this.handles, this.handles.length << 1);
			}
			this.handles[this.i ++] = LOOKUP.bind(task, "invoke", type);
			return this;
		}
		catch (NoSuchMethodException | IllegalAccessException exception)
		{
			throw new InternalError(exception);
		}
	}
	
	public TaskBuilderBool add(TaskOTB task) { return add(task, OTB); }
	public TaskBuilderLong add(TaskOTL task) { return add(task, OTL); }
	public TaskBuilderRef add(TaskOTO task) { return add(task, OTO); }
	public TaskBuilderBool add(TaskBTB task) { return add(task, BTB); }
	public TaskBuilderLong add(TaskBTL task) { return add(task, BTL); }
	public <T> TaskBuilderRef<T> add(TaskBTO<T> task) { return add(task, BTO); }
	public TaskBuilderBool add(TaskLTB task) { return add(task, LTB); }
	public TaskBuilderLong add(TaskLTL task) { return add(task, LTL); }
	public <T> TaskBuilderRef<T> add(TaskLTO<T> task) { return add(task, LTO); }
	
	private TaskListInstance build_()
	{
		TaskListInstance instance = new TaskListInstance();
		instance.tasks = Arrays.copyOf(this.handles, this.i);
		return instance;
	}
	
	private TaskListInstance build_(TaskList bind)
	{
		this.handles[this.i - 1] = this.handles[this.i - 1].bindTo(bind);
		TaskListInstance instance = new TaskListInstance();
		instance.tasks = Arrays.copyOf(this.handles, this.i);
		return instance;
	}
	
	public TaskListBool build(Object start) { return build_((TaskListRef      ) () -> start); }
	public TaskListBool build(            ) { return build_();                                }
	public TaskListBool build(long   start) { return build_((TaskListLong     ) () -> start); }
	
	public TaskBTB asTask() { return new LinkedTaskBTB(Arrays.copyOf(this.handles, this.i)); }
}