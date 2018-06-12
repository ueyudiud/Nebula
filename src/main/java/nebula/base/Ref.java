/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base;

import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

/**
 * @author ueyudiud
 */
public class Ref<E>
{
	public E value;
	
	public Ref(       ) { }
	public Ref(E value) { this.value = value; }
	
	public Optional<E> optional()
	{
		return Optional.ofNullable(this.value);
	}
	
	public Ref<E> map(Function<? super E, ? extends E> function)
	{
		if (this.value != null)
		{
			this.value = function.apply(this.value);
		}
		return this;
	}
	
	public E computeIfAbsent(@Nullable E def)
	{
		if (this.value == null)
		{
			this.value = def;
		}
		return this.value;
	}
	
	public E get()
	{
		return this.value;
	}
	
	public E getOrDefault(@Nullable E def)
	{
		return this.value == null ? def : this.value;
	}
	
	@Override
	public String toString()
	{
		return "ref: {" + this.value + "}";
	}
}
