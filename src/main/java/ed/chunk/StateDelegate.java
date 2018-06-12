/*
 * copyright 2016-2018 ueyudiud
 */
package ed.chunk;

import java.util.*;

import ed.EDConfig;
import nebula.common.block.IExtendedDataBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

/**
 * @author ueyudiud
 */
abstract class StateDelegate
{
	static final StateDelegate AIR = new StateDegegateSingle(Blocks.AIR);
	
	static StateDelegate create(Block block)
	{
		return block == Blocks.AIR ? AIR : block.getBlockState().getProperties().isEmpty() ? new StateDegegateSingle(block) : (EDConfig.buildStateIn || (block instanceof IExtendedDataBlock)) ? new StateDelegateExt(block) : new StateDegegateNormal(block);
	}
	
	final Block block;
	final IBlockState def;
	int id = -1;
	
	StateDelegate(Block block)
	{
		this.block = block;
		this.def = block.getDefaultState();
	}
	
	void setId(int id)
	{
		this.id = id;
	}
	
	int getId()
	{
		return this.id;
	}
	
	abstract IBlockState get(int meta);
	
	abstract int getMeta(IBlockState state);
	
	abstract int capacity();
	
	abstract void logInformation();
	
	abstract Collection<IBlockState> storable();
}
