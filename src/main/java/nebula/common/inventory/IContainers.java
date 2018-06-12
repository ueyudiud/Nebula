/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import java.util.Collection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

import nebula.common.inventory.task.Task;

/**
 * The basic containers type.
 * @author ueyudiud
 */
@ParametersAreNullableByDefault
public interface IContainers<T>
{
	/**
	 * Clear all stack in containers.
	 */
	void clear();
	
	/**
	 * Get container size of this containers.
	 * @return the size.
	 */
	int getContainerSize();
	
	/**
	 * Get all containers as an array.
	 * @return
	 */
	IContainer<T>[] getContainers();
	
	/**
	 * Get container with specific id.
	 * @param index the index of container.
	 * @return the container.
	 */
	IContainer<T> getContainer(int index);
	
	/**
	 * Get a stack contained in the container.
	 * @param index the container index.
	 * @return the contained stack, which is the one of contains, <code>null</code> if container is empty.
	 */
	@Nullable
	default T getStackInContainer(int index)
	{
		return getContainer(index).getStackInContainer();
	}
	
	/**
	 * Get stacks contained in the container.
	 * @param index the container index.
	 * @return the contained stacks.
	 */
	default Collection<? extends T> getStacksInContainer(int index)
	{
		return getContainer(index).stacks();
	}
	
	/**
	 * Insert all stack to its container with same index of stacks.<p>
	 * The stacks array length is predicated that
	 * <code>stacks.length &gt= getContainerSize()</code><p>
	 * The available modifiers are {@link IContainer#PROCESS}, {@link IContainer#SKIP_AC}.
	 * @param stacks the stacks to insert.
	 * @return return <code>true</code> if insert is succeed.
	 */
	boolean insertAllStackShaped(T[] stacks, int modifier);
	
	/**
	 * Insert all stack into containers.<p>
	 * The available modifiers are {@link IContainer#PROCESS}, {@link IContainer#SKIP_AC}.
	 * @param stacks the stacks to insert.
	 * @return return <code>true</code> if insert is succeed.
	 */
	boolean insertAllStackShapeless(T[] stacks, int modifier);
	
	/**
	 * Insert all stack into containers.<p>
	 * The available modifiers are {@link IContainer#PROCESS}, {@link IContainer#SKIP_AC}.
	 * @param stacks the stacks to insert.
	 * @return return <code>true</code> if insert is succeed.
	 */
	boolean insertAllStackShapeless(Iterable<? extends T> stacks, int modifier);
	
	/**
	 * Extract all stack from containers indexed.<p>
	 * The stacks array length is predicated that
	 * <code>stacks.length &gt= getContainerSize()</code><p>
	 * The available modifiers are {@link IContainer#PROCESS}.
	 * @param stacks the stacks to extract.
	 * @return return <code>true</code> if extract is succeed.
	 */
	boolean extractAllStackShaped(T[] stacks, int modifier);
	
	/**
	 * Extract all stack from containers.<p>
	 * The available modifiers are {@link IContainer#PROCESS}.
	 * @param stacks the stacks to extract.
	 * @return return <code>true</code> if extract is succeed.
	 */
	boolean extractAllStackShapeless(T[] stacks, int modifier);
	
	/**
	 * Extract all stack from containers.<p>
	 * The available modifiers are {@link IContainer#PROCESS}.
	 * @param stacks the stacks to extract.
	 * @return return <code>true</code> if extract is succeed.
	 */
	boolean extractAllStackShapeless(Iterable<? extends T> stacks, int modifier);
	
	/**
	 * Create <tt>insertAll</tt> operation task.<p>
	 * The available modifiers are {@link IContainer#PROCESS}, {@link IContainer#SKIP_AC}.
	 * @see #insertAllStackShaped(T[], int)
	 * @param stacks the stacks to insert.
	 * @param modifier the operation request.
	 * @return the task.
	 */
	Task.TaskBTB taskInsertAllShaped(T[] stacks, int modifier);
	
	/**
	 * Create <tt>insertAll</tt> operation task.<p>
	 * The available modifiers are {@link IContainer#PROCESS}, {@link IContainer#SKIP_AC}.
	 * @see #insertAllStackShapeless(T[], int)
	 * @param stacks the stacks to insert.
	 * @param modifier the operation request.
	 * @return the task.
	 */
	Task.TaskBTB taskInsertAllShapeless(T[] stacks, int modifier);
	
	/**
	 * Create <tt>insertAll</tt> operation task.<p>
	 * The available modifiers are {@link IContainer#PROCESS}, {@link IContainer#SKIP_AC}.
	 * @see #insertAllStackShapeless(Iterable, int)
	 * @param stacks the stacks to insert.
	 * @param modifier the operation request.
	 * @return the task.
	 */
	Task.TaskBTB taskInsertAllShapeless(Iterable<? extends T> stacks, int modifier);
	
	/**
	 * Create <tt>extractAll</tt> operation task.<p>
	 * The available modifiers are {@link IContainer#PROCESS}.
	 * @see #extractAllStackShaped(T[], int)
	 * @param stacks the stacks to extract.
	 * @param modifier the operation request.
	 * @return the task.
	 */
	Task.TaskBTB taskExtractAllShaped(T[] stacks, int modifier);
	
	/**
	 * Create <tt>extractAll</tt> operation task.<p>
	 * The available modifiers are {@link IContainer#PROCESS}.
	 * @see #extractAllStackShapeless(T[], int)
	 * @param stacks the stacks to extract.
	 * @param modifier the operation request.
	 * @return the task.
	 */
	Task.TaskBTB taskExtractAllShapeless(T[] stacks, int modifier);
	
	/**
	 * Create <tt>extractAll</tt> operation task.<p>
	 * The available modifiers are {@link IContainer#PROCESS}.
	 * @see #extractAllStackShapeless(Iterable, int)
	 * @param stacks the stacks to extract.
	 * @param modifier the operation request.
	 * @return the task.
	 */
	Task.TaskBTB taskExtractAllShapeless(Iterable<? extends T> stacks, int modifier);
}
