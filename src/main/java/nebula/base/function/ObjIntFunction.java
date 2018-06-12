/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.function;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ueyudiud
 */
public interface ObjIntFunction<T, R>
{
	R apply(T t, int i);
	
	default SequenceConsumer<T> andThen(Consumer<? super R> f)
	{
		return (t, i) -> f.accept(apply(t, i));
	}
	
	default <R1> ObjIntFunction<T, R1> andThen(Function<? super R, R1> f)
	{
		return (t, i) -> f.apply(apply(t, i));
	}
}
