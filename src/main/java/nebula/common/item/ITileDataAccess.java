/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.item;

import nebula.common.nbt.INBTCompoundReaderAndWriter;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * @author ueyudiud
 */
public interface ITileDataAccess<D extends TileData> extends INBTCompoundReaderAndWriter<D>, Iterable<D>
{
	ItemStack toItemStack(D data);
	
	TileEntity toTileEntity(D data);
}
