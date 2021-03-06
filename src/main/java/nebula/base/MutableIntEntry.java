/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * A map entry (With value type is <tt>int</tt>).
 * 
 * @author ueyudiud
 * @param <K> the type of key.
 * @see java.util.Map.Entry
 */
public class MutableIntEntry<K> implements IntEntry<K>
{
	K	key;
	int	value;
	
	public MutableIntEntry(K key, int value)
	{
		this.key = key;
		this.value = value;
	}
	
	public K getKey()
	{
		return this.key;
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	public int setValue(int value)
	{
		int old = this.value;
		this.value = value;
		return old;
	}
	
	public void transformKey(UnaryOperator<K> operator)
	{
		this.key = operator.apply(this.key);
	}
	
	/**
	 * Returns the hash code value for this map entry.
	 * 
	 * @return the hash code value for this map entry
	 * @see #equals(Object)
	 * @see java.util.Map.Entry#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.key) ^ this.value;
	}
	
	/**
	 * Compares the specified object with this entry for equality.
	 * 
	 * @param obj object to be compared for equality with this map entry
	 * @return <tt>true</tt> if the specified object is equal to this map entry
	 * @see #hashCode()
	 * @see java.util.Map.Entry#equals(Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return obj == this || ((obj instanceof MutableIntEntry<?>) && ((MutableIntEntry<?>) obj).key.equals(this.key) && ((MutableIntEntry<?>) obj).value == this.value);
	}
	
	@Override
	public String toString()
	{
		return new StringBuilder().append(this.key).append('=').append(this.value).toString();
	}
}
