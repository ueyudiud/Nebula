/*
 * copyright 2016-2018 ueyudiud
 */
package ed.chunk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import ed.ED;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/**
 * @author ueyudiud
 */
class StateDegegateNormal extends StateDelegate
{
	int cap;
	short set = 1;//The meta of 0 is no except be enabled.
	
	StateDegegateNormal(Block block)
	{
		super(block);
		initSet();
	}
	
	private void initSet()
	{
		Set<IBlockState> set = new HashSet<>(16, 1.0F);
		for (int i = 1; i < 16; ++i)
		{
			try //May not consider array out of bounds problem.
			{
				IBlockState state = this.block.getStateFromMeta(i);
				if (!set.contains(state))
				{
					set.add(state);
					this.set |= (1 << i);
					this.cap = i + 1;
				}
			}
			catch (Throwable exception)
			{
				;
			}
		}
	}
	
	@Override
	IBlockState get(int meta)
	{
		return (this.set & 1 << meta) != 0 ? this.block.getStateFromMeta(meta) : this.def;
	}
	
	@Override
	int getMeta(IBlockState state)
	{
		return this.block.getMetaFromState(state);
	}
	
	@Override
	int capacity()
	{
		return this.cap;
	}
	
	@Override
	void logInformation()
	{
		for (int i = 0; i < this.cap; ++i)
		{
			try
			{
				ED.trace("{}=>{}", this.id + i, this.block.getStateFromMeta(i));
			}
			catch (RuntimeException exception)
			{
			}
		}
		Multimap<Integer, IBlockState> map = HashMultimap.create();
		for (IBlockState state : this.block.getBlockState().getValidStates())
		{
			map.put(this.block.getMetaFromState(state), state);
		}
		for (Entry<Integer, IBlockState> entry : map.entries())
		{
			ED.trace("{}<={}", this.id + entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	Collection<IBlockState> storable()
	{
		ImmutableList.Builder<IBlockState> builder = ImmutableList.builder();
		for (int i = 0; i < this.cap; ++i)
		{
			if ((this.set & 1 << i) != 0)
			{
				builder.add(get(i));
			}
		}
		return builder.build();
	}
}