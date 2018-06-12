/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.collection;

import nebula.base.IntEntry;

/**
 * @author ueyudiud
 */
class IntEntry_<E> implements IntEntry<E>
{
	E key;
	int value;
	
	IntEntry_(E key           ) { this(key, 1); }
	IntEntry_(E key, int value)
	{
		this.key = key;
		this.value = value;
	}
	
	@Override
	public E getKey()
	{
		return this.key;
	}
	
	@Override
	public int getValue()
	{
		return this.value;
	}
	
	@Override
	public int setValue(int i)
	{
		int old = this.value;
		this.value = i;
		return old;
	}
}
