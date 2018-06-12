/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

/**
 * @author ueyudiud
 */
public interface ISlot<T>
{
	T getStack();
	
	void putStack(T stack);
}
