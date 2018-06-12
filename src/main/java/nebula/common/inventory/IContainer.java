/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import java.util.Collection;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

import nebula.common.inventory.task.Task;

/**
 * @author ueyudiud
 */
@ParametersAreNullableByDefault
public interface IContainer<T>
{
	/**
	 * Clear all stack contained in this container.
	 */
	void clear();
	
	/**
	 * Return <code>true</code> if this container is simulated container.
	 * @return
	 */
	default boolean isSimulated() { return false; }
	
	/**
	 * Merge changes into real container.<p>
	 * This method only take effect when this container is simulated.
	 */
	default void merge() { }
	
	/**
	 * Return <code>true</code> if there is any stack in container.
	 * @return
	 */
	default boolean hasStackInContainer() { return getStackInContainer() != null; }
	
	/**
	 * Get stack in container.
	 * @return the stack in container.
	 */
	@Nullable T getStackInContainer();
	
	/**
	 * Return <code>true</code> if stack is available to insert into this container.
	 * @param stack the checked stack.
	 * @return
	 */
	boolean isAvailable(T stack);
	
	/**
	 * Set the container only contain the stack.
	 * @param stack the stack to set.
	 */
	void setStackInContainer(@Nullable T stack);
	
	/**
	 * Create a simulated container, used for multiple insert check.
	 * @return the simulated container.
	 * @throws UnsupportedOperationException when simulated item container is not allowed.
	 */
	IContainer<? extends T> simulated();
	
	/**
	 * Return all stacks in the container.<p>
	 * This collection is immutable that if you want take
	 * operation on any stack in the container, use method
	 * in this interface instead.
	 * @return the collection.
	 */
	@Nonnull Collection<? extends T> stacks();
	
	/** The <tt>modifier</tt> used when any operation is take effect on stack. */
	int PROCESS = 1;
	static boolean process(int modifier) { return (modifier & PROCESS) != 0; }
	/** The <tt>modifier</tt> used when operation only take when it can be fully reached. */
	int FULLY = 2;
	static boolean fully(int modifier) { return (modifier & FULLY) != 0; }
	/** The <tt>modifier</tt> used when need skip available checking when take <tt>insert</tt> or <tt>increase</tt> operation. */
	int SKIP_AC = 4;
	static boolean skipAvailableCheck(int modifier) { return (modifier & SKIP_AC) != 0; }
	/** The <tt>modifier</tt> used when need to skip slot <tt>refresh</tt> task. */
	int SKIP_REFRESH = 8;
	static boolean skipRefresh(int modifier) { return (modifier & SKIP_REFRESH) != 0; }
	
	/**
	 * Take <tt>refresh</tt> operation.
	 * Refresh the container, will mark changed and resort item.<p>
	 * You needn't called it after called any operation may modified container without
	 * {@link #SKIP_REFRESH} modifier used for they will be auto called.
	 */
	default void refresh() { taskRefresh().invoke(); }
	
	/**
	 * Take <tt>increase</tt> operation.<p>
	 * Increase size/amount of stack contained specific object from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #FULLY}, {@link #SKIP_AC}.
	 * @param stack the increase source stack.
	 * @param modifier the operation request.
	 * @return the increased size.
	 */
	int incrStack(T stack, int modifier);
	
	/**
	 * Take <tt>decrease</tt> operation.<p>
	 * Decrease size/amount of stack contained specific object of from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #FULLY}.
	 * @param stack the decrease stack source.
	 * @param modifier the operation request.
	 * @return the decreased stack.
	 */
	T decrStack(int size, int modifier);
	
	/**
	 * Take <tt>decrease</tt> operation.<p>
	 * Decrease size/amount of stack contained specific object of from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #FULLY}.
	 * @param stack the decrease stack source.
	 * @param modifier the operation request.
	 * @return the decreased size.
	 */
	int decrStack(T stack, int modifier);
	
	/**
	 * Create <tt>refresh</tt> operation task.<p>
	 * @return the task.
	 * @see #refresh()
	 */
	Task.TaskBTB taskRefresh();
	
	/**
	 * Take <tt>insert</tt> operation.<p>
	 * Insert stack to container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #FULLY}, {@link #SKIP_AC}.
	 * @param stack the stack to insert.
	 * @param modifier the operation request.
	 * @return the remained stack.
	 */
	@Nullable T insertStack(T stack, int modifier);
	
	/**
	 * Take <tt>extract</tt> operation.<p>
	 * Extract stack from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #FULLY}.
	 * @param stack the item stack to extract.
	 * @param modifier the operation request.
	 * @return the failed to extract stack.
	 */
	@Nullable T extractStack(T stack, int modifier);
	
	/**
	 * Take <tt>extract</tt> operation.<p>
	 * Extract stack from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #FULLY}.
	 * @param size the max size/amount can be extract.
	 * @param modifier the operation request.
	 * @return the extracted stack.
	 */
	@Nullable T extractStack(int size, int modifier);
	
	/**
	 * Create <tt>increase</tt> operation task.<p>
	 * Increase stack {@link #FULLY} into container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #SKIP_REFRESH}, {@link #SKIP_AC}.
	 * @param stack the item stack to increase.
	 * @param modifier the operation request.
	 * @return the task.
	 * @see #incrStack(T, int)
	 */
	Task.TaskBTB taskIncr(@Nullable T stack, int modifier);
	
	/**
	 * Create <tt>decrease</tt> operation task.<p>
	 * Decrease stack {@link #FULLY} from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #SKIP_REFRESH}.
	 * @param stack the item stack to decrease.
	 * @param modifier the operation request.
	 * @return the task.
	 * @see #decrStack(T, int)
	 */
	Task.TaskBTB taskDecr(@Nullable T stack, int modifier);
	
	/**
	 * Create <tt>decrease</tt> operation task.<p>
	 * Decrease stack {@link #FULLY} from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #SKIP_REFRESH}.
	 * @param size the size to decrease.
	 * @param modifier the operation request.
	 * @return the task.
	 * @see #decrStack(int, int)
	 * @see #extractStack(int, int)
	 */
	Task.TaskBTB taskDecr(@Nonnegative int size, int modifier);
}
