/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.collection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * @author ueyudiud
 */
class CachedFiltedIterable<T> implements Iterable<T>
{
	private static final int MAX_INITALIZE_SIZE = 256;
	
	private final int size;
	private final Predicate<? super T> predicate;
	private final Iterator<? extends T> iterator;
	private int current;
	private Object[] cache;
	
	CachedFiltedIterable(int size, Iterator<? extends T> iterator, Predicate<? super T> predicate)
	{
		this.size = size;
		this.predicate = predicate;
		this.iterator = iterator;
		this.cache = new Object[initalizeSize(size)];
	}
	
	private static int initalizeSize(int size)
	{
		return size < MAX_INITALIZE_SIZE ? size : MAX_INITALIZE_SIZE;
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return new CachedFiltedIterator();
	}
	
	private T get(int i)
	{
		return (T) this.cache[i];
	}
	
	private synchronized void find()
	{
		if (this.current >= this.cache.length)
		{
			this.cache = Arrays.copyOf(this.cache, Math.min(this.size, this.current << 1));
		}
		while (this.iterator.hasNext())
		{
			T value = this.iterator.next();
			if (this.predicate.test(value))
			{
				this.cache[this.current ++] = value;
				return;
			}
		}
	}
	
	class CachedFiltedIterator implements Iterator<T>
	{
		int pos = 0;
		
		@Override
		public boolean hasNext()
		{
			if (this.pos < CachedFiltedIterable.this.current)
			{
				return true;
			}
			else
			{
				find();
				return this.pos < CachedFiltedIterable.this.current;
			}
		}
		
		@Override
		public T next()
		{
			if (this.pos < CachedFiltedIterable.this.current)
			{
				return get(this.pos ++);
			}
			else
			{
				find();
				if (this.pos < CachedFiltedIterable.this.current)
				{
					return get(this.pos ++);
				}
			}
			throw new IndexOutOfBoundsException();
		}
	}
}
