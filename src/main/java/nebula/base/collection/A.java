/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.collection;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ObjectArrays;

import nebula.V;
import nebula.base.Ref;
import nebula.base.function.F;
import nebula.base.function.TriFunction;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public final class A
{
	private A() { }
	
	public static <E> List<E> argument(@Nullable E e1, E...es) { return new ArgumentList<>(ObjectArrays.concat(e1, es)); }
	public static <E> List<E> argument(Object[] objects) { return new ArgumentList<>(objects); }
	
	public static <E> List<E> nonnull() { return conditional(F.P_ANY); }
	public static <E> List<E> conditional(Predicate<? super E> predicate) { return new ArrayListConditional<>(predicate); }
	
	/**
	 * Fill elements provide by function.
	 * @param array the array to fill.
	 * @param function the provider.
	 * @return the filled array.
	 */
	public static <E> E[] fill(E[] array, IntFunction<? extends E> function) { return fill(array, 0, array.length, function); }
	/**
	 * Fill elements provide by function from <tt>from</tt> index(inclusive) to <tt>to</tt> index(exclusive).
	 * @param array the array to fill.
	 * @param from the start fill index.
	 * @param to the end fill index.
	 * @param function the provider.
	 * @return the filled array.
	 */
	public static <E> E[] fill(E[] array, int from, int to, IntFunction<? extends E> function)
	{
		Objects.requireNonNull(function);
		for (int i = from; i < to; array[i] = function.apply(i), ++i);
		return array;
	}
	public static <E> E[] fill(E[] array, Supplier<? extends E> supplier)
	{
		for (int i = 0; i < array.length; array[i ++] = supplier.get());
		return array;
	}
	
	public static Object[] suboa(Object[] array, int from        ) { return suboa(array, from, array.length); }
	public static Object[] suboa(Object[] array, int from, int to)
	{
		if (from > to)
			throw new IndexOutOfBoundsException();
		else if (from == to)
			return V.OBJECTS_EMPTY;
		Object[] result = new Object[to - from];
		System.arraycopy(array, from, result, 0, result.length);
		return result;
	}
	
	/**
	 * Create a ArrayParser.
	 * @param objects the array.
	 * @return the parser.
	 * @see nebula.base.collection.ArrayParser
	 */
	public static ArrayParser parser(Object[] array) { return new ArrayParser(array); }
	
	public static <E> int sum(E[] es, ToIntFunction<E> function)
	{
		int sum = 0;
		for (E e : es) { sum += function.applyAsInt(e); }
		return sum;
	}
	
	public static <E> void consume(E[] es, Consumer<? super E> consumer)
	{
		Objects.requireNonNull(consumer);
		for (E e : es) { consumer.accept(e); }
	}
	
	public static <R, E> R collect(Stream<E> stream, @Nullable R first, BiFunction<? super R, ? super E, ? extends R> function)
	{
		Ref<R> ref = new Ref<>(first);
		stream.forEach(e -> ref.value = function.apply(ref.value, e));
		return ref.value;
	}
	
	public static <R, E1, E2> R collect2(Stream<? extends Entry<E1, E2>> stream, @Nullable R first, TriFunction<? super R, ? super E1, ? super E2, ? extends R> function)
	{
		Ref<R> ref = new Ref<>(first);
		stream.forEach(e -> ref.value = function.apply(ref.value, e.getKey(), e.getValue()));
		return ref.value;
	}
	
	public static <T> T[] requiredNonnull(T... args)
	{
		for (Object arg : args)
		{
			if (arg == null)
			{
				throw new NullPointerException();
			}
		}
		return args;
	}
	
	/**
	 * Compare two array with type by compactor.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Comparator
	 */
	public static <E> int compare(E[] array1, E[] array2, Comparator<? super E> comparator)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = comparator.compare(array1[i], array2[i])) != 0)
				return com;
		return array1.length > array2.length ? 1 : array1.length < array2.length ? -1 : 0;
	}
	/**
	 * Compare two byte array.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Byte#compare(byte, byte)
	 * @see Comparable
	 */
	public static int compare(byte[] array1, byte[] array2)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = Byte.compare(array1[i], array2[i])) != 0)
				return com;
		return array1.length > array2.length ? 1 : array1.length < array2.length ? -1 : 0;
	}
	/**
	 * Compare two short array.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Short#compare(short, short)
	 * @see Comparable
	 */
	public static int compare(short[] array1, short[] array2)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = Short.compare(array1[i], array2[i])) != 0)
				return com;
		return array1.length > array2.length ? 1 : array1.length < array2.length ? -1 : 0;
	}
	/**
	 * Compare two int array.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Integer#compare(int, int)
	 * @see Comparable
	 */
	public static int compare(int[] array1, int[] array2)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = Integer.compare(array1[i], array2[i])) != 0)
				return com;
		return array1.length > array2.length ? 1 : array1.length < array2.length ? -1 : 0;
	}
	/**
	 * Compare two long array.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Long#compare(long, long)
	 * @see Comparable
	 */
	public static int compare(long[] array1, long[] array2)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = Long.compare(array1[i], array2[i])) != 0)
				return com;
		return array1.length > array2.length ? 1 : array1.length < array2.length ? -1 : 0;
	}
	/**
	 * Compare two float array.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Float#compare(float, float)
	 * @see Comparable
	 */
	public static int compare(float[] array1, float[] array2)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = Float.compare(array1[i], array2[i])) != 0)
				return com;
		return array1.length > array2.length ? 1 : array1.length < array2.length ? -1 : 0;
	}
	/**
	 * Compare two double array.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Double#compare(double, double)
	 * @see Comparable
	 */
	public static int compare(double[] array1, double[] array2)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = Double.compare(array1[i], array2[i])) != 0)
				return com;
		return array1.length > array2.length ? 1 : array1.length < array2.length ? -1 : 0;
	}
	/**
	 * Compare two char array.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Character#compare(double, double)
	 * @see Comparable
	 */
	public static int compare(char[] array1, char[] array2)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = Character.compare(array1[i], array2[i])) != 0)
				return com;
		return array1.length > array2.length ? 1 : array1.length < array2.length ? -1 : 0;
	}
	/**
	 * Compare two boolean array.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Boolean#compare(double, double)
	 * @see Comparable
	 */
	public static int compare(boolean[] array1, boolean[] array2)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = Boolean.compare(array1[i], array2[i])) != 0)
				return com;
		return array1.length > array2.length ? 1 : array1.length < array2.length ? -1 : 0;
	}
	/**
	 * Compare two array with comparable type.
	 * 
	 * @param array1 the first array.
	 * @param array2 the last array.
	 * @return the order result.
	 * @see Comparable
	 */
	public static <L extends Comparable<? super R>, R> int compare(L[] array1, R[] array2)
	{
		int size = Math.min(array1.length, array2.length);
		int com;
		for (int i = 0; i < size; ++i)
			if ((com = array1[i].compareTo(array2[i])) != 0)
				return com;
		return array1.length > array2.length ? 1 : array1.length < array2.length ? -1 : 0;
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
			if (array1[i] instanceof Object[])
				if ((com = deepCompare((Object[]) array1[i], (Object[]) array2[i])) != 0)
					return com;
			if (array1[i] instanceof byte[])
				if ((com = compare((byte[]) array1[i], (byte[]) array2[i])) != 0)
					return com;
			if (array1[i] instanceof short[])
				if ((com = compare((short[]) array1[i], (short[]) array2[i])) != 0)
					return com;
			if (array1[i] instanceof int[])
				if ((com = compare((int[]) array1[i], (int[]) array2[i])) != 0)
					return com;
			if (array1[i] instanceof long[])
				if ((com = compare((long[]) array1[i], (long[]) array2[i])) != 0)
					return com;
			if (array1[i] instanceof float[])
				if ((com = compare((float[]) array1[i], (float[]) array2[i])) != 0)
					return com;
			if (array1[i] instanceof double[])
				if ((com = compare((double[]) array1[i], (double[]) array2[i])) != 0)
					return com;
			if (array1[i] instanceof char[])
				if ((com = compare((char[]) array1[i], (char[]) array2[i])) != 0)
					return com;
			if (array1[i] instanceof boolean[])
				if ((com = compare((boolean[]) array1[i], (boolean[]) array2[i])) != 0)
					return com;
			if ((com = ((Comparable) array1[i]).compareTo(array2[i])) != 0)
				return com;
		}
		return array1.length > array2.length ? 1 : array1.length < array2.length ? -1 : 0;
	}
	
	public static ObjArrayParseHelper create(@Nullable Object...objects) { return new ObjArrayParseHelper(objects); }
	
	/**
	 * Create a filtered Iterable store value by cache.
	 * @param iterable the iterator provider.
	 * @param predicate the filter.
	 * @return the filtered Iterable.
	 */
	public static <T> Iterable<T> filter(Collection<? extends T> iterable, Predicate<? super T> predicate) { return new CachedFiltedIterable<>(iterable.size(), iterable.iterator(), predicate); }
	/**
	 * Create a filtered Iterable store value by cache.
	 * @param predicate the filter.
	 * @param ts the target values.
	 * @return the filtered Iterable.
	 */
	public static <T> Iterable<T> filter(Predicate<? super T> predicate, T...ts) { return new CachedFiltedIterable<>(ts.length, (Iterator<T>) argument(ts).iterator(), predicate); }
	
	public static <T> Object[] transformu(T[] array, Function<? super T, ? extends T> function)
	{
		final int length = array.length;
		T[] result = ObjectArrays.newArray(array, array.length);
		for (int i = 0; i < length; result[i] = function.apply(array[i]), i ++);
		return result;
	}
	/**
	 * Transform array to mapped values.
	 * @param array the source array.
	 * @param clazz the result array type.
	 * @param function the mapping function.
	 * @return
	 */
	public static <T> Object[] transform(T[] array, Function<? super T, ?> function)
	{
		final int length = array.length;
		Object[] result = new Object[array.length];
		for (int i = 0; i < length; result[i] = function.apply(array[i]), i ++);
		return result;
	}
	/**
	 * Transform array to mapped values.
	 * @param array the source array.
	 * @param clazz the result array type.
	 * @param function the mapping function.
	 * @return
	 */
	public static <R, T> R[] transform(T[] array, Class<R> clazz, Function<? super T, ? extends R> function)
	{
		final int length = array.length;
		R[] result = ObjectArrays.newArray(clazz, length);
		for (int i = 0; i < length; result[i] = function.apply(array[i]), i ++);
		return result;
	}
	
	/**
	 * Clone every array in target array and deep clone on
	 * every element if it is an array.
	 * @param array the input array.
	 * @return the cloned array.
	 */
	public static <T> T[] deepClone(T[] array)
	{
		T[] result = array.clone();
		for (int i = 0; i < array.length; ++i)
		{
			if (result[i] instanceof byte[])
			{
				result[i] = (T) ((byte[]) result[i]).clone();
			}
			else if (result[i] instanceof short[])
			{
				result[i] = (T) ((short[]) result[i]).clone();
			}
			else if (result[i] instanceof int[])
			{
				result[i] = (T) ((int[]) result[i]).clone();
			}
			else if (result[i] instanceof long[])
			{
				result[i] = (T) ((long[]) result[i]).clone();
			}
			else if (result[i] instanceof float[])
			{
				result[i] = (T) ((float[]) result[i]).clone();
			}
			else if (result[i] instanceof double[])
			{
				result[i] = (T) ((double[]) result[i]).clone();
			}
			else if (result[i] instanceof boolean[])
			{
				result[i] = (T) ((boolean[]) result[i]).clone();
			}
			else if (result[i] instanceof char[])
			{
				result[i] = (T) ((char[]) result[i]).clone();
			}
			else if (result[i] instanceof Object[])
			{
				result[i] = (T) deepClone((Object[]) result[i]);
			}
		}
		return result;
	}
}
