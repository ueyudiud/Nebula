/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.block;

import net.minecraft.block.Block;

/**
 * @author ueyudiud
 */
public interface IMetaExtHandler extends IMetaHandler
{
	void registerStateToRegister(Block block, IBlockStateRegister register);
}
