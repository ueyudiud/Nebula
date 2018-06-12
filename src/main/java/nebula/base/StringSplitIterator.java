/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base;

import java.util.Iterator;

/**
 * @author ueyudiud
 */
class StringSplitIterator implements Iterator<String>
{
	private final String value;
	private final char splitor;
	private final int limit;
	private int pos = 0;
	private int count = 0;
	private boolean consumed = false;
	
	StringSplitIterator(String value, char splitor, int limit)
	{
		this.value = value;
		this.splitor = splitor;
		this.limit = limit;
	}
	
	@Override
	public boolean hasNext()
	{
		return !this.consumed;
	}
	
	@Override
	public String next()
	{
		if (this.consumed)
		{
			throw new IllegalStateException("The string value has already consumed.");
		}
		int id = this.value.indexOf(this.splitor, this.pos);
		if (id == -1)
		{
			this.consumed = true;
			return this.value.substring(this.pos);
		}
		else
		{
			String result = this.value.substring(this.pos, id);
			this.pos = id + 1;
			if (++ this.count >= this.limit)
			{
				this.consumed = true;
			}
			return result;
		}
	}
	
}
