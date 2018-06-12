/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.function;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

/**
 * @author ueyudiud
 */
@FunctionalInterface
public interface SequenceConsumer<T> extends ObjIntConsumer<T>
{
	default Consumer<T> toNormal()
	{
		return target -> accept(target, 0);
	}
	
	default <T1 extends T> void accept(T1[] list)
	{
		accept(list, 0, list.length);
	}
	
	default <T1 extends T> void accept(T1[] list, int start, int end)
	{
		for (int i = start; i < end; accept(list[i], i++));
	}
	
	default void accept(Iterable<? extends T> iterable)
	{
		if (iterable instanceof List)
			accept((List<? extends T>) iterable);
		else
			accept(iterable.iterator());
	}
	
	default void accept(Iterator<? extends T> iterator)
	{
		int i = 0;
		while (iterator.hasNext())
		{
			accept(iterator.next(), i++);
		}
	}
	
	default void accept(List<? extends T> list)
	{
		for (int i = 0; i < list.size(); accept(list.get(i), i++));
	}
}
