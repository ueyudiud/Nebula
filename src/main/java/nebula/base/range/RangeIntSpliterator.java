/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.range;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.IntConsumer;

/**
 * @author ueyudiud
 */
class RangeIntSpliterator implements Spliterator.OfInt
{
	int from;
	int to;
	
	RangeIntSpliterator(int from, int to)
	{
		this.from = from;
		this.to = to;
	}
	
	@Override
	public long estimateSize()
	{
		return this.to - this.from;
	}
	
	@Override
	public int characteristics()
	{
		return DISTINCT | IMMUTABLE | ORDERED | NONNULL;
	}
	
	@Override
	public OfInt trySplit()
	{
		if (this.to >= this.from + 2)
		{
			int mid = (this.from + this.to) >>> 1;
			RangeIntSpliterator spliterator = new RangeIntSpliterator(mid, this.to);
			this.to = mid;
			return spliterator;
		}
		return null;
	}
	
	@Override
	public boolean tryAdvance(IntConsumer action)
	{
		if (this.from < this.to)
		{
			action.accept(this.from ++);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public void forEachRemaining(IntConsumer action)
	{
		for (int i = this.from; i < this.to; action.accept(i ++));
		this.from = this.to;
	}
	
	@Override
	public Comparator<? super Integer> getComparator()
	{
		return Comparator.naturalOrder();
	}
}
