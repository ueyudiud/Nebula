/* 
 * copyright 2016-2018 ueyudiud
 */
package ed.chunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import ed.ED;
import nebula.base.IntEntry;
import nebula.base.collection.HashIntMap;
import nebula.common.block.IExtendedDataBlock;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

/**
 * @author ueyudiud
 */
class StateDelegateExt extends StateDelegate
{
	private static void build(final StateDelegateExt delegate, final Block block)
	{
		delegate.id_to_state = new ArrayList<>();
		delegate.state_to_id = new HashIntMap<>();
		BlockStateRegister.REGISTER.delegate = delegate;
		BlockStateRegister.REGISTER.block = block;
		if (block instanceof IExtendedDataBlock)
		{
			((IExtendedDataBlock) block).registerStateToRegister(BlockStateRegister.REGISTER);
		}
		else
		{
			BlockStateRegister.REGISTER.registerStates(block.getBlockState().getProperties());
		}
		delegate.id_to_state = ImmutableList.copyOf(delegate.id_to_state);
	}
	
	List<IBlockState>		id_to_state;
	HashIntMap<IBlockState>	state_to_id;
	Collection<IProperty<?>>sorts;
	
	StateDelegateExt(Block block)
	{
		super(block);
		build(this, block);
	}
	
	@Override
	IBlockState get(int meta)
	{
		return this.id_to_state.get(meta);
	}
	
	@Override
	int getMeta(IBlockState state)
	{
		return this.state_to_id.getOrDefault(state, 0);
	}
	
	@Override
	int capacity()
	{
		return this.id_to_state.size();
	}
	
	@Override
	void logInformation()
	{
		for (int i = 0; i < this.id_to_state.size(); ++i)
		{
			ED.trace("{}=>{}", this.id + i, this.id_to_state.get(i));
		}
		Multimap<Integer, IBlockState> map = HashMultimap.create();
		for (IntEntry<IBlockState> entry : this.state_to_id)
		{
			map.put(entry.getValue(), entry.getKey());
		}
		for (Entry<Integer, IBlockState> entry : map.entries())
		{
			ED.trace("{}<={}", this.id + entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	Collection<IBlockState> storable()
	{
		return Collections.unmodifiableCollection(this.id_to_state);
	}
}