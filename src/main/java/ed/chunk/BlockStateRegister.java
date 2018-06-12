/*
 * copyright 2016-2018 ueyudiud
 */
package ed.chunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import nebula.base.collection.A;
import nebula.common.block.IBlockStateRegister;
import nebula.common.util.L;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

/**
 * @author ueyudiud
 */
class BlockStateRegister implements IBlockStateRegister
{
	static final BlockStateRegister REGISTER = new BlockStateRegister();
	
	Block block;
	StateDelegateExt delegate;
	
	@Override
	public void registerStates(Block block, IProperty<?>...properties)
	{
		assert this.delegate.block == block;
		registerStates(properties);
	}
	
	void registerStates(Collection<IProperty<?>> properties)
	{
		IBlockState state;
		Multimap<IBlockState, IBlockState> map;
		
		List<IProperty<?>> list = new ArrayList<>(this.delegate.block.getBlockState().getProperties());
		list.removeAll(properties);
		
		IProperty<?>[] ps = properties.toArray(new IProperty<?>[properties.size()]);
		forEach1(0, ps, L.cast(list, IProperty.class), this.delegate.block.getDefaultState(), map = HashMultimap.create());
		
		List<IBlockState> states = new ArrayList<>(map.keySet());
		states.sort((s1, s2) -> compare(ps, s1, s2));
		
		for (int i = 0; i < states.size(); registerStateMap(state = states.get(i++), map.get(state)));
		this.delegate.sorts = properties;
	}
	
	@Override
	public void registerStates(IProperty<?>...properties)
	{
		if (properties.length == 0)
		{
			registerStateMap(this.delegate.block.getDefaultState(), this.delegate.block.getBlockState().getValidStates());
			this.delegate.sorts = ImmutableList.of();
		}
		else
		{
			IBlockState state;
			Multimap<IBlockState, IBlockState> map;
			
			List<IProperty> list = new ArrayList<>(this.delegate.block.getBlockState().getProperties());
			list.removeAll(A.argument(properties));
			
			forEach1(0, properties, L.cast(list, IProperty.class), this.delegate.block.getDefaultState(), map = HashMultimap.create());
			
			List<IBlockState> states = new ArrayList<>(map.keySet());
			states.sort((s1, s2) -> compare(properties, s1, s2));
			
			for (int i = 0; i < states.size(); registerStateMap(state = states.get(i++), map.get(state)));
			this.delegate.sorts = ImmutableList.copyOf(properties);
		}
	}
	
	private int compare(IProperty[] properties, IBlockState state1, IBlockState state2)
	{
		int i;
		for (IProperty<?> property : properties)
		{
			if ((i = state1.getValue(property).compareTo(L.castAny(state2.getValue(property)))) != 0)
			{
				return i;
			}
		}
		return 0;
	}
	
	void forEach1(int id, IProperty[] properties1, IProperty<?>[] properties2, final IBlockState state, Multimap<IBlockState, IBlockState> map)
	{
		if (id == properties1.length)
		{
			if (properties2.length == 0)
			{
				List<IBlockState> list = new ArrayList<>();
				forEach2(0, properties2, state, list);
				map.putAll(state, list);
			}
			else
			{
				map.put(state, state);
			}
		}
		else
		{
			IProperty property = properties1[id ++];
			IBlockState state2 = state;
			do forEach1(id, properties1, properties2, state2, map);
			while ((state2 = state2.cycleProperty(property)) != state);
		}
	}
	
	void forEach2(int id, IProperty<?>[] properties, final IBlockState state, List<IBlockState> list)
	{
		if (id == properties.length)
		{
			list.add(state);
		}
		else
		{
			IProperty property = properties[id++];
			IBlockState state2 = state;
			do forEach2(id, properties, state2, list);
			while ((state2 = state2.cycleProperty(property)) != state);
		}
	}
	
	public void registerState(IBlockState state)
	{
		registerStateMap(state, ImmutableList.of(state));
	}
	
	public void registerStateMap(IBlockState source, IBlockState...castable)
	{
		registerStateMap(source, A.argument(castable));
	}
	
	public void registerStateMap(IBlockState source, Collection<IBlockState> castable)
	{
		int id = this.delegate.id_to_state.size();
		this.delegate.id_to_state.add(id, source);
		for (IBlockState state : castable)
		{
			this.delegate.state_to_id.put(state, id);
		}
	}
}
