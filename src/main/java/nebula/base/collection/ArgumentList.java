/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.collection;

import java.util.AbstractList;

/**
 * @author ueyudiud
 */
class ArgumentList<E> extends AbstractList<E>
{
	private Object[] values;
	
	ArgumentList(Object[] values)
	{
		this.values = values;
	}
	
	@Override
	public E get(int index)
	{
		return (E) this.values[index];
	}
	
	@Override
	public int size()
	{
		return this.values.length;
	}
	
	@Override
	public Object[] toArray()
	{
		return this.values;
	}
}
