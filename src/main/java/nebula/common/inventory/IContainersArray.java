/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

/**
 * @author ueyudiud
 */
public interface IContainersArray<T> extends IContainers<T>
{
	T[] toArray();
	
	void fromArray(T[] array);
	
	void clearRange(int from, int to);
}
