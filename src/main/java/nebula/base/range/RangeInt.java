/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.range;

import java.io.Serializable;
import java.util.*;
import java.util.Spliterator.OfInt;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;

import nebula.base.A;
import net.minecraft.util.math.MathHelper;

/**
 * @author ueyudiud
 */
public class RangeInt extends AbstractList<Integer> implements Serializable, Set<Integer>
{
	private static final long serialVersionUID = 5277973178659886150L;
	
	/** The <code>int</code> range. */
	private final int min, max;
	
	/**
	 * Create a ranged collection.
	 * 
	 * @param min the minimum value in range (include itself).
	 * @param max the max value in range (exclude itself).
	 * @return the range.
	 */
	public static List<Integer> to(int min, int max) { return min >= max ? ImmutableList.of() : to_(min, max); }
	public static RangeInt to_(int min, int max) { return new RangeInt(min, max); }
	
	/**
	 * Create a ranged collection.
	 * 
	 * @param min the minimum value in range (include itself).
	 * @param max the max value in range (include itself).
	 * @return the range.
	 */
	public static List<Integer> range(int min, int max) { return min >= max ? ImmutableList.of() : range_(min, max); }
	public static RangeInt range_(int min, int max) { return new RangeInt(min, max + 1); }
	
	private RangeInt(int min, int max)
	{
		if (min >= max) throw new IllegalArgumentException("The min value must less than max value!");
		this.max = max;
		this.min = min;
	}
	
	@Override
	public Integer get(int index)
	{
		int i = index + this.min;
		if (i >= this.max || i < 0) throw new ArrayIndexOutOfBoundsException(index);
		return i;
	}
	
	@Override
	public int size()
	{
		return this.max - this.min;
	}
	
	@Override
	public boolean isEmpty()
	{
		return false;// The range will always not empty, with initializer checking.
	}
	
	@Override
	public boolean contains(Object o)
	{
		if (o instanceof Integer)
		{
			return (Integer) o >= this.min && (Integer) o < this.max;
		}
		return false;
	}
	
	@Override
	public Iterator<Integer> iterator()
	{
		return new Iterator<Integer>()
		{
			int current = RangeInt.this.min;
			
			@Override
			public boolean hasNext()
			{
				return this.current < RangeInt.this.max;
			}
			
			@Override
			public Integer next()
			{
				int i = this.current++;
				if (i >= RangeInt.this.max) throw new IndexOutOfBoundsException();
				return i;
			}
		};
	}
	
	private void rangeCheck(int index)
	{
		if (index < 0 || index >= size()) throw new IndexOutOfBoundsException("Index: " + index + " Range: [" + this.min + ", " + this.max + ")");
	}
	
	@Override
	public ListIterator<Integer> listIterator(int index)
	{
		rangeCheck(index);
		return new ListIterator<Integer>()
		{
			int current = RangeInt.this.min + index;
			
			@Override
			public boolean hasNext()
			{
				return this.current < RangeInt.this.max;
			}
			
			@Override
			public Integer next()
			{
				int i = this.current++;
				rangeCheck(i);
				return i;
			}
			
			@Override
			public boolean hasPrevious()
			{
				return this.current > RangeInt.this.min;
			}
			
			@Override
			public Integer previous()
			{
				int i = --this.current;
				rangeCheck(i);
				return i;
			}
			
			@Override
			public int nextIndex()
			{
				return this.current - RangeInt.this.min;
			}
			
			@Override
			public int previousIndex()
			{
				return this.current - RangeInt.this.min - 1;
			}
			
			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void set(Integer e)
			{
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void add(Integer e)
			{
				throw new UnsupportedOperationException();
			}
		};
	}
	
	@Override
	public Integer[] toArray()
	{
		return A.fill(new Integer[size()], i -> i + this.min);
	}
	
	@Override
	public <T> T[] toArray(T[] a)
	{
		int size;
		return A.<T> fill(a.length < (size = size()) ? ObjectArrays.newArray(a, size) : a, i -> (T) Integer.valueOf(i + this.min));
	}
	
	@Override
	public boolean add(Integer e)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean containsAll(Collection<?> collection)
	{
		if (collection.isEmpty()) return true;
		if (collection instanceof RangeInt)
		{
			return ((RangeInt) collection).min >= this.min && ((RangeInt) collection).max >= this.max;
		}
		else
		{
			return collection.stream().allMatch(this::contains);
		}
	}
	
	@Override
	public boolean addAll(Collection<? extends Integer> c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void forEach(Consumer<? super Integer> action)
	{
		for (int i = this.min; i < this.max; ++i)
		{
			action.accept(i);
		}
	}
	
	public void forEach1(IntConsumer action)
	{
		for (int i = this.min; i < this.max; ++i)
		{
			action.accept(i);
		}
	}
	
	@Override
	public int hashCode()
	{
		int hashCode = 1;
		for (int i = this.min; i < this.max; ++i)
		{
			hashCode = 31 * hashCode + Integer.hashCode(i);
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		if (o instanceof RangeInt) return ((RangeInt) o).min == this.min && ((RangeInt) o).max == this.max;
		if (!(o instanceof List) || size() != ((List<?>) o).size()) return false;
		
		Iterator<Integer> iterator1 = iterator();
		ListIterator<?> iterator2 = ((List<?>) o).listIterator();
		while (iterator1.hasNext() && iterator2.hasNext())
		{
			if (!Objects.equal(iterator1.next(), iterator2.next()))
				return false;
		}
		return !(iterator1.hasNext() || iterator2.hasNext());
	}
	
	@Override
	public String toString()
	{
		Iterator<Integer> itr = iterator();
		StringBuilder sb = new StringBuilder(size() * MathHelper.log2DeBruijn(this.max) >> 2).append('[');
		while (true)
		{
			sb.append(itr.next());
			if (!itr.hasNext()) return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}
	
	@Override
	public OfInt spliterator()
	{
		return new RangeIntSpliterator(this.min, this.max);
	}
	
	public IntStream intstream()
	{
		return StreamSupport.intStream(spliterator(), false);
	}
}
