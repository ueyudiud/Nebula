/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author ueyudiud
 */
class ArrayListConditional<E> extends ArrayList<E>
{
	private static final long serialVersionUID = 8879262538215888947L;
	
	private transient Predicate<? super E> predicate;
	
	ArrayListConditional(Predicate<? super E> predicate)
	{
		this.predicate = predicate;
	}
	
	@Override
	public boolean add(E e)
	{
		return this.predicate.test(e) ? super.add(e) : false;
	}
	
	@Override
	public void add(int index, E element)
	{
		if (this.predicate.test(element))
		{
			add(index, element);
		}
		else throw new IllegalArgumentException();
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		return super.addAll(new ArgumentList<>(c.stream().filter(this.predicate).toArray()));
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c)
	{
		return super.addAll(index, new ArgumentList<>(c.stream().filter(this.predicate).toArray()));
	}
}
