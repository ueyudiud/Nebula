/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

/**
 * @author ueyudiud
 */
public interface IContainerSingle<T> extends IContainer<T>
{
	/**
	 * Get the copied of only stack in container.<p>
	 * @return the stack in this container.
	 * @see #stack()
	 */
	@Override
	T getStackInContainer();
	
	/**
	 * Get the only stack in container.
	 * @return
	 */
	T stack();
}
