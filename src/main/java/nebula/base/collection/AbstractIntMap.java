/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.collection;

import java.util.Iterator;

import nebula.base.IntEntry;

/**
 * @author ueyudiud
 */
public abstract class AbstractIntMap<E> implements IntMap<E>
{
	@Override
	public int hashCode()
	{
		int hashcode = 0;
		for (IntEntry<E> entry : this)
			hashcode += entry.hashCode();
		return hashcode;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		
		if (!(obj instanceof IntMap<?>)) return false;
		
		IntMap<?> map = (IntMap<?>) obj;
		
		if (map.size() != size()) return false;
		
		try
		{
			for (IntEntry<E> entry : this)
			{
				if (!map.contains(entry.getKey(), entry.getValue()))
					return false;
			}
		}
		catch (ClassCastException | NullPointerException e)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public String toString()
	{
		if (isEmpty()) return "{}";
		StringBuilder builder = new StringBuilder().append('{');
		Iterator<IntEntry<E>> iterator = iterator();
		builder.append(iterator.next());
		while (iterator.hasNext())
		{
			builder.append(',').append(iterator.next());
		}
		return builder.append('}').toString();
	}
}
