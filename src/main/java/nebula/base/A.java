/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Ints;

import nebula.common.util.L;

/**
 * Array helper methods.
 * 
 * @author ueyudiud
 */
@Deprecated
public final class A
{
	private A()
	{
	}
	
	/**
	 * Copy array elements to a new array with selected length.
	 * 
	 * @param array the source array.
	 * @param len the length of new array, use old array length if the select
	 *            length is smaller than old array length.
	 * @return the copied array.
	 */
	public static int[] copyToLength(int[] array, int len)
	{
		return Arrays.copyOf(array, len);
	}
	
	/**
	 * Copy array elements to a new array with selected length. The result array
	 * type is same to old array type.
	 * 
	 * @param array the old array, if it is <tt>null</tt> it will only use to
	 *            mark a type.
	 * @param len the new array length.
	 * @param <T> the type of array.
	 * @return if array is <tt>null</tt>, return a new array or return a copy
	 *         otherwise.
	 */
	public static <T> T[] copyToLength(@Nullable T[] array, int len)
	{
		if (array != null)
		{
			return Arrays.copyOf(array, len);
		}
		else
		{
			return (T[]) new Object[len];
		}
	}
	
	/**
	 * Given action to every element in array.
	 * 
	 * @see java.util.Collection#forEach(Consumer)
	 * @param iterable
	 * @param consumer
	 */
	public static <E> void executeAll(@Nonnull E[] iterable, @Nonnull Consumer<E> consumer)
	{
		Objects.requireNonNull(consumer);
		for (E element : iterable)
			consumer.accept(element);
	}
	
	/**
	 * Given action to every element in array.
	 * 
	 * @see java.util.Collection#forEach(Consumer)
	 * @param iterable the iterator provider.
	 * @param consumer the consumer.
	 */
	public static <E> void executeAll(@Nonnull E[] iterable, @Nonnull ObjIntConsumer<E> consumer)
	{
		Objects.requireNonNull(consumer);
		for (int i = 0; i < iterable.length; ++i)
			consumer.accept(iterable[i], i);
	}
	
	/**
	 * Match same element in array, use {@code L.equal(element, arg)} to match
	 * objects.
	 * 
	 * @param list The given array.
	 * @param arg The matched argument.
	 * @return
	 */
	public static <E> boolean contain(@Nonnull E[] list, @Nullable E arg)
	{
		for (E element : list)
			if (L.equals(element, arg))
				return true;
		return false;
	}
	
	public static boolean contain(int[] list, int arg)
	{
		return Ints.contains(list, arg);
	}
	
	public static boolean contain(char[] list, char arg)
	{
		return Chars.contains(list, arg);
	}
	
	/**
	 * Check all elements can be matched.
	 * 
	 * @param list
	 * @param checker
	 * @param <E> The type of element.
	 * @return
	 */
	public static <E> boolean and(E[] list, Predicate<? super E> checker)
	{
		for (E element : list)
			if (!checker.test(element)) return false;
		return true;
	}
	
	/**
	 * Check all elements can be matched.
	 * 
	 * @param list1 the first list.
	 * @param list2 the second list.
	 * @param checker
	 * @param <E1> the first list element type.
	 * @param <E2> the second list element type.
	 * @return
	 * @throws IllegalArgumentException if two list length are not same.
	 */
	public static <E1, E2> boolean and(E1[] list1, E2[] list2, BiPredicate<? super E1, ? super E2> checker)
	{
		if (list1.length != list2.length) throw new IllegalArgumentException("Array length not same.");
		final int length = list1.length;
		for (int i = 0; i < length; ++i)
			if (!checker.test(list1[i], list2[i]))
				return false;
		return true;
	}
	
	public static <E> boolean and(int[] list, IntPredicate predicate)
	{
		for (int element : list)
			if (!predicate.test(element))
				return false;
		return true;
	}
	
	public static <E> boolean and(long[] list, LongPredicate predicate)
	{
		for (long element : list)
			if (!predicate.test(element))
				return false;
		return true;
	}
	
	public static <E> boolean or(E[] list, Predicate<? super E> checker)
	{
		for (E element : list)
			if (checker.test(element))
				return true;
		return false;
	}
	
	public static <E> boolean or(int[] list, IntPredicate predicate)
	{
		for (int element : list)
			if (predicate.test(element))
				return true;
		return false;
	}
	
	public static <E> boolean or(long[] list, LongPredicate predicate)
	{
		for (long element : list)
			if (predicate.test(element))
				return true;
		return false;
	}
	
	/**
	 * Create new <code>int</code> array with same elements.
	 * 
	 * @param length the length of array.
	 * @param value the filled value.
	 * @return the array.
	 */
	public static int[] fillIntArray(int length, int value)
	{
		switch (length)
		{
		case 0 : return new int[0];
		case 1 : return new int[] { value };
		default:
			int[] ret = new int[length];
			for (int i = 0; i < length; ret[i++] = value);
			return ret;
		}
	}
	
	/**
	 * Create new <code>byte</code> array with same elements.
	 * 
	 * @param length the length of array.
	 * @param value the filled value.
	 * @return the array.
	 */
	public static byte[] fillByteArray(int length, byte value)
	{
		switch (length)
		{
		case 0 : return new byte[0];
		case 1 : return new byte[] { value };
		default:
			byte[] ret = new byte[length];
			for (int i = 0; i < length; ret[i++] = value);
			return ret;
		}
	}
	
	/**
	 * Create new <code>char</code> array with same elements.
	 * 
	 * @param length the length of array.
	 * @param value the filled value.
	 * @return the array.
	 */
	public static char[] fillCharArray(int length, char value)
	{
		switch (length)
		{
		case 0 : return new char[0];
		case 1 : return new char[] { value };
		default:
			char[] ret = new char[length];
			Arrays.fill(ret, value);
			return ret;
		}
	}
	
	public static <E> E[] fill(E[] array, IntFunction<E> function)
	{
		return fill(array, 0, array.length, function);
	}
	
	public static <E> E[] fill(E[] array, int from, int to, IntFunction<E> function)
	{
		for (int i = from; i < to; array[i] = function.apply(i), ++i);
		return array;
	}
	
	/**
	 * Get first equally <code>int</code> value position.
	 * 
	 * @param array the array.
	 * @param arg the element to match.
	 * @return the index, or <code>-1</code> if not matched.
	 */
	public static int indexOf(int[] array, int arg)
	{
		final int length = array.length;
		for (int i = 0; i < length; ++i)
			if (array[i] == arg)
				return i;
		return -1;
	}
	
	/**
	 * Get first equally <code>char</code> value position.
	 * 
	 * @param array the array.
	 * @param arg the element to match.
	 * @return the index, or <code>-1</code> if not matched.
	 */
	public static int indexOf(char[] array, char arg)
	{
		final int length = array.length;
		for (int i = 0; i < length; ++i)
			if (array[i] == arg)
				return i;
		return -1;
	}
	
	/**
	 * Get first matched element index in list.
	 * 
	 * @param array the array.
	 * @param arg the matching target.
	 * @return the index of element, <code>-1</code> means no element matched.
	 */
	public static int indexOfFirst(Object[] array, Object arg)
	{
		final int length = array.length;
		if (arg == null)
		{
			for (int i = 0; i < length; ++i)
			{
				if (array[i] == null)
				{
					return i;
				}
			}
		}
		else
		{
			for (int i = 0; i < length; ++i)
			{
				if (array[i] != null && arg.equals(array[i]))
				{
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Transform key array to target array.
	 * 
	 * @param array
	 * @param elementClass
	 * @param function Transform function.
	 * @return
	 */
	public static <K, T> T[] transform(K[] array, Class<T> elementClass, Function<? super K, ? extends T> function)
	{
		final int length = array.length;
		T[] result = ObjectArrays.newArray(elementClass, length);
		for (int i = 0; i < length; result[i] = function.apply(array[i]), ++i);
		return result;
	}
	
	/**
	 * Transform key array to object array.
	 * 
	 * @param array the source array.
	 * @param function transform function.
	 * @return the transformed array.
	 */
	public static <K> Object[] transform(K[] array, Function<? super K, ?> function)
	{
		return transform(array, 0, array.length, function);
	}
	
	/**
	 * Transform key array to object array.
	 * 
	 * @param array the source array.
	 * @param function transform function.
	 * @return the transformed array.
	 */
	public static <K> Object[] transform(K[] array, int from, int to, Function<? super K, ?> function)
	{
		Object[] result = new Object[to - from];
		for (int i = from; i < to; result[i - from] = function.apply(array[i]), ++i);
		return result;
	}
	
	/**
	 * Transform key array to target array.
	 * 
	 * @param array
	 * @param elementClass
	 * @param function Transform function.
	 * @return
	 */
	public static <T> T[] transform(int[] array, Class<T> elementClass, IntFunction<? extends T> function)
	{
		final int length = array.length;
		T[] result = ObjectArrays.newArray(elementClass, length);
		for (int i = 0; i < length; result[i] = function.apply(array[i]), ++i);
		return result;
	}
	
	public static int[] rangeIntArray(int to)
	{
		return rangeIntArray(0, to);
	}
	
	/**
	 * Create a array with ranged number.
	 * <p>
	 * For examples : the result of <tt>rangeIntArray(1, 3)</tt> is
	 * <tt>[1, 2]</tt>
	 * 
	 * @param from start value (include itself)
	 * @param to end value (exclude itself)
	 * @return the int array.
	 */
	public static int[] rangeIntArray(int from, int to)
	{
		int[] array = new int[to - from];
		final int length = array.length;
		for (int i = 0; i < length; array[i] = from + i, i++);
		return array;
	}
	
	/**
	 * Create a array with selected generator.
	 * <p>
	 * Examples : <code>createIntArray(3, i->i*i)</code> and the result is
	 * <tt>[0, 1, 4]</tt>
	 * 
	 * @param length the length of array.
	 * @param operator the function to provide <tt>int</tt> value.
	 * @return the array.
	 */
	public static int[] createIntArray(int length, IntUnaryOperator operator)
	{
		int[] result = new int[length];
		for (int i = 0; i < length; result[i] = operator.applyAsInt(i), ++i);
		return result;
	}
	
	/**
	 * Create a array with selected generator.
	 * 
	 * @param length the length of array.
	 * @param function the function to provider <tt>long</tt> value
	 * @return the array.
	 * @see #createIntArray(int, IntUnaryOperator)
	 */
	public static long[] createLongArray(int length, IntToLongFunction function)
	{
		long[] result = new long[length];
		for (int i = 0; i < length; result[i] = function.applyAsLong(i), ++i);
		return result;
	}
	
	/**
	 * Create a new array fill with single element.
	 * <p>
	 * The argument should not be null for this method use value to predicated
	 * type of array.
	 * 
	 * @param length the length of array.
	 * @param value the value filling the array.
	 * @return the filled array.
	 */
	public static <E> E[] createArray(int length, @Nonnull E value)
	{
		E[] array = (E[]) ObjectArrays.newArray(value.getClass(), length);
		Arrays.fill(array, value);
		return array;
	}
	
	public static char[] sublist(char[] array, int start, int end)
	{
		char[] a1 = new char[end - start];
		System.arraycopy(array, start, a1, 0, end - start);
		return a1;
	}
	
	/**
	 * Get sub list of array.
	 * 
	 * @param array the source array.
	 * @param off the sublist start position(include itself).
	 * @return the sub array with element start at <tt>off</tt> position in
	 *         source array and include all element after, if off position is
	 *         <code>0</code>, the method will return itself.
	 * @see #sublist(Object[], int, int)
	 */
	public static <E> E[] sublist(@Nonnull E[] array, int off)
	{
		return off == 0 ? array : sublist(array, off, array.length - off);
	}
	
	/**
	 * Create a sub list from argument list.
	 * 
	 * @param array the source of array.
	 * @param off the sublist start position(include itself).
	 * @param len the length of array.
	 * @return the sub array with element start at <tt>off</tt> position in
	 *         source array and end at <tt>off + len</tt> position in source
	 *         array, if off is <code>0</code> and len is
	 *         <code>array.length</code>, the method will return itself.
	 * @throws java.lang.IndexOutOfBoundsException when copy length is out of
	 *             array bound.
	 */
	public static <E> E[] sublist(@Nonnull E[] array, int off, int len)
	{
		if (off == 0 && len == array.length)
			return array;
		E[] a1 = ObjectArrays.newArray(array, len);
		System.arraycopy(array, off, a1, 0, len);
		return a1;
	}
	
	/**
	 * Create a sub list from argument int list.
	 * 
	 * @param array the source of array.
	 * @param off the sublist start position(include itself).
	 * @param len the length of array.
	 * @return the sub array with element start at <tt>off</tt> position in
	 *         source array and end at <tt>off + len</tt> position in source
	 *         array, if off is <code>0</code> and len is
	 *         <code>array.length</code>, the method will return itself.
	 * @throws java.lang.IndexOutOfBoundsException when copy length is out of
	 *             array bound.
	 */
	public static int[] sublist(@Nonnull int[] array, int off, int len)
	{
		if (off == 0 && len == array.length)
			return array;
		int[] a1 = new int[len];
		System.arraycopy(array, off, a1, 0, len);
		return a1;
	}
	
	/**
	 * Return if list and all elements are non-null, or throw an
	 * NullPointerException otherwise.
	 * 
	 * @param <T> type of elements.
	 * @param array the elements.
	 * @return the elements.
	 * @throws NullPointerException if any element in array is null.
	 */
	public static <T> T[] allNonNull(T[] array)
	{
		Objects.requireNonNull(array);
		for (Object arg : array)
			Objects.requireNonNull(arg);
		return array;
	}
	
	/**
	 * Compare two array with comparable type by natural order.
	 * <p>
	 * This method is not type safe.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Comparable
	 * @throws ClassCastException if elements can not cast to same comparable
	 *             type.
	 */
	public static int compare(Object[] array1, Object[] array2)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = ((Comparable) array1[i]).compareTo(array2[i])) != 0) return com;
		return Integer.compare(array1.length, array2.length);
	}
	
	/**
	 * Compare two array with comparable type by compactor.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Comparable
	 */
	public static <E1 extends E, E2 extends E, E> int compare(E1[] array1, E2[] array2, Comparator<E> comparator)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = comparator.compare(array1[i], array2[i])) != 0) return com;
		return Integer.compare(array1.length, array2.length);
	}
	
	/**
	 * Compare two array with comparable type by natural order, and the array
	 * will be regarded as a comparable type.
	 * <p>
	 * This method is not type safe.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Comparable
	 * @throws ClassCastException if elements can not cast to same comparable
	 *             type.
	 */
	public static int deepCompare(@Nonnull Object[] array1, @Nonnull Object[] array2)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
		{
			if (array1[i] instanceof Object[] && array2[i] instanceof Object[]) if ((com = deepCompare((Object[]) array1[i], (Object[]) array2[i])) != 0) return com;
			if ((com = ((Comparable) array1[i]).compareTo(array2[i])) != 0) return com;
		}
		return -Integer.compare(array1.length, array2.length);
	}
	
	@Nonnegative
	public static <E> int countIf(@Nonnull E[] array, @Nonnull Predicate<? super E> predicate)
	{
		int i = 0;
		for (E e : array)
			if (predicate.test(e))
				++ i;
		return i;
	}
	
	/**
	 * Get counted number that how many value that equals to value in array.
	 * 
	 * @param array the array.
	 * @param value the matching value.
	 * @return the counted number.
	 */
	@Nonnegative
	public static int count(@Nonnull Object[] array, @Nullable Object value)
	{
		int i = 0;
		if (value == null)
			for (Object object : array)
			{
				if (object == null)
					++ i;
			}
		else
			for (Object object : array)
				if (object != null && value.equals(object))
					++ i;
		return i;
	}
	
	public static <E> List<E> argument(Object[] array)
	{
		return nebula.base.collection.A.argument(array);
	}
}
