/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common;

import nebula.common.tile.INetworkedSyncTile;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

/**
 * @author ueyudiud
 */
@Deprecated
public class NebulaSynchronizationHandler
{
	public static void markTileEntityForUpdate(INetworkedSyncTile tile, int type)
	{
		NebulaCommonHandler.markTileEntityForUpdate(tile.getDimension(), tile.pos(), type);
	}
	
	private static void markTileEntityForUpdate(int dim, ChunkPos pos, BlockPos pos1, int type)
	{
		NebulaCommonHandler.markTileEntityForUpdate(dim, pos1, type);
	}
}
