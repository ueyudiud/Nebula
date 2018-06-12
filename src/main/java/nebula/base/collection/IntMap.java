/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.collection;

import java.util.OptionalInt;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nebula.base.IntEntry;

/**
 * @author ueyudiud
 */
public interface IntMap<E> extends Iterable<IntEntry<E>>
{
	/**
	 * Return a immutable empty IntegerMap.
	 * 
	 * @return the IntegerMap.
	 */
	static <E> IntMap<E> empty()
	{
		return (IntMap<E>) ImmutableEmptyIntMap.INSTANCE;
	}
	
	static <E> IntMap<E> of(E key, int value)
	{
		return new ImmutableSingleIntMap<>(key, value);
	}
	
	static <E> IntMap<E> copyOf(@Nonnull IntMap<? extends E> map)
	{
		switch (map.size())
		{
		case 0 :
			return empty();
		case 1 :
			IntEntry<? extends E> entry = map.iterator().next();
			return of(entry.getKey(), entry.getValue());
		default:
			return new HashIntMap<>(map);
		}
	}
	
	int size();
	
	default boolean isEmpty()
	{
		return size() == 0;
	}
	
	boolean containsKey(@Nullable Object key);
	
	boolean contains(@Nullable Object key, int value);
	
	/**
	 * Get value which specified key is mapped.
	 * <p>
	 * 
	 * @param key
	 * @return the value of key mapped.
	 * @see java.util.Map#get(Object)
	 */
	default int get(@Nullable Object key)
	{
		return getOrDefault(key, 0);
	}
	
	default int getOrDefault(@Nullable Object key, int value)
	{
		return getIf(key).orElse(value);
	}
	
	@Nonnull OptionalInt getIf(@Nullable Object key);
	
	default int put(@Nonnull IntEntry<E> entry)
	{
		return put(entry.getKey(), entry.getValue());
	}
	
	int put(@Nullable E key, int value);
	
	void putOrAdd(@Nullable E key, int amount);
	
	default void putAll(@Nonnull IntMap<? extends E> map)
	{
		for (IntEntry<? extends E> entry : map)
		{
			put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Called this method when regard this collection value as probability.
	 * <p>
	 * For use, divided each value with their GCD.
	 * 
	 * @throws IllegalStateException when some value is non-positive.
	 */
	void rescale() throws IllegalStateException;
	
	default int remove(@Nullable Object key, int def)
	{
		return removeIf(key).orElse(def);
	}
	
	@Nonnull OptionalInt removeIf(@Nullable Object key);
	
	void clear();
	
	/**
	 * Return the sum of all values.
	 * 
	 * @return
	 */
	int sum();
	
	Set<E> keySet();
	
	default void forEach(@Nonnull ObjIntConsumer<? super E> consumer)
	{
		for (IntEntry<? extends E> entry : this)
			consumer.accept(entry.getKey(), entry.getValue());
	}
	
	/**
	 * Return <code>true</code> if two map are equal.
	 * 
	 * @param obj
	 * @return
	 */
	boolean equals(Object obj);
	
	/**
	 * Return the hashcode of map.
	 * 
	 * @return the hashcode.
	 */
	int hashCode();
	
	/**
	 * 
	 * @return
	 */
	String toString();
	
	/**
	 * If the key is not already mapped to a value, computes it by function.
	 * @param key
	 * @param function
	 * @return
	 */
	default int computeIfAbsent(E key, @Nonnull ToIntFunction<? super E> function)
	{
		if (!containsKey(key))
		{
			int result = function.applyAsInt(key);
			put(key, result);
			return result;
		}
		else
		{
			return get(key);
		}
	}
	
	/**
	 * Transform all value.
	 * 
	 * @param operator the operator to transform value.
	 */
	default void transformAll(@Nonnull IntUnaryOperator operator)
	{
		for (IntEntry<E> entry : this)
		{
			entry.setValue(operator.applyAsInt(entry.getValue()));
		}
	}
	
	default OptionalInt find(@Nonnull Predicate<E> predicate)
	{
		for (IntEntry<E> entry : this)
		{
			if (predicate.test(entry.getKey()))
				return OptionalInt.of(entry.getValue());
		}
		return OptionalInt.empty();
	}
	
	@Override
	default Spliterator<IntEntry<E>> spliterator()
	{
		return Spliterators.spliterator(iterator(), size(), Spliterator.NONNULL | Spliterator.DISTINCT);
	}
}
