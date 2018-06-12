/* 
 * copyright 2016-2018 ueyudiud
 */
package ed.chunk;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import ed.ED;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/**
 * @author ueyudiud
 */
class StateDegegateSingle extends StateDelegate
{
	StateDegegateSingle(Block block)
	{
		super(block);
	}
	
	@Override
	IBlockState get(int meta)
	{
		return this.def;
	}
	
	@Override
	int getMeta(IBlockState state)
	{
		return 0;
	}
	
	@Override
	int capacity()
	{
		return 1;
	}
	
	@Override
	void logInformation()
	{
		ED.trace("{}=>{}", this.id, this.def);
		ED.trace("{}<=[{}]", this.id, this.def);
	}
	
	@Override
	Collection<IBlockState> storable()
	{
		return ImmutableList.of(this.block.getDefaultState());
	}
}