/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.function;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author ueyudiud
 */
public interface Judgable<T> extends Predicate<T>, com.google.common.base.Predicate<T>
{
	@Override default boolean apply(T input) { return test(input); }
	
	@Override default Judgable<T> negate() { return t -> !test(t); }
	
	@Override default Judgable<T> and(Predicate<? super T> other) { return new And(this, other); }
	
	@Override default Judgable<T> or(Predicate<? super T> other) { return new Or(this, other); }
	
	default <R> Judgable<R> from(Function<R, ? extends T> function)
	{
		return t -> test(function.apply(t));
	}
}
