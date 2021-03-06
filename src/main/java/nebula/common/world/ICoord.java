/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.world;

import java.util.List;

import nebula.common.util.Direction;
import nebula.common.util.W;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * A object contain a coordinate in world.<br>
 * Some useful method is given by.
 * 
 * @author ueyudiud
 */
public interface ICoord
{
	static IModifiableCoord create(World world, BlockPos pos)
	{
		TileEntity tile = W.getTileEntity(world, pos, false);
		if (tile instanceof IModifiableCoord)
		{
			return (IModifiableCoord) tile;
		}
		return new WorldCoord0(world, pos, tile);
	}
	
	/**
	 * The world belong.
	 * 
	 * @return
	 */
	World world();
	
	/**
	 * The position at.
	 * 
	 * @return
	 */
	BlockPos pos();
	
	default ICoord offset(Direction offset)
	{
		return offset(offset.x, offset.y, offset.z);
	}
	
	default ICoord offset(int x, int y, int z)
	{
		return new WorldCoord2(this, pos().add(x, y, z));
	}
	
	default IBlockState getBlockState()
	{
		return world().getBlockState(pos());
	}
	
	default IBlockState getBlockState(Direction offset)
	{
		return world().getBlockState(offset.offset(pos()));
	}
	
	default IBlockState getBlockState(int x, int y, int z)
	{
		return world().getBlockState(pos().add(x, y, z));
	}
	
	default int getWeakPower(EnumFacing side)
	{
		return world().getBlockState(pos()).getWeakPower(world(), pos(), side);
	}
	
	default int getRedstonePower(EnumFacing side, Direction offset)
	{
		return world().getRedstonePower(offset.offset(pos()), side);
	}
	
	default int getRedstonePower(EnumFacing side, int x, int y, int z)
	{
		return world().getRedstonePower(pos().add(x, y, z), side);
	}
	
	default int getWeakPower(EnumFacing side, Direction offset)
	{
		BlockPos pos = offset.offset(pos());
		return world().getBlockState(pos).getWeakPower(world(), pos, side);
	}
	
	default int getWeakPower(EnumFacing side, int x, int y, int z)
	{
		BlockPos pos = pos().add(x, y, z);
		return world().getBlockState(pos).getWeakPower(world(), pos, side);
	}
	
	default int getStrongPower(EnumFacing side)
	{
		return world().getBlockState(pos()).getStrongPower(world(), pos(), side);
	}
	
	default int getStrongPower(EnumFacing side, Direction offset)
	{
		BlockPos pos = pos().offset(offset.of());
		return world().getBlockState(pos).getStrongPower(world(), pos, side);
	}
	
	default int getStrongPower(EnumFacing side, int x, int y, int z)
	{
		BlockPos pos = pos().add(x, y, z);
		return world().getBlockState(pos).getStrongPower(world(), pos, side);
	}
	
	default int getLight(boolean checkNeighbors)
	{
		return world().getLight(pos(), checkNeighbors);
	}
	
	default int getLight(Direction offset, boolean checkNeighbors)
	{
		return world().getLight(offset.offset(pos()), checkNeighbors);
	}
	
	default int getLight(int x, int y, int z, boolean checkNeighbors)
	{
		return world().getLight(pos().add(x, y, z), checkNeighbors);
	}
	
	default Chunk chunk()
	{
		return world().getChunkFromBlockCoords(pos());
	}
	
	default boolean isAirNearby(boolean ignoreUnloadChunk)
	{
		return W.isAirNearby(world(), pos(), ignoreUnloadChunk);
	}
	
	default TileEntity getTE()
	{
		return world().getTileEntity(pos());
	}
	
	default TileEntity getTE(Direction offset)
	{
		return world().getTileEntity(offset.offset(pos()));
	}
	
	default TileEntity getTE(int x, int y, int z)
	{
		return world().getTileEntity(pos().add(x, y, z));
	}
	
	default boolean isBlockNearby(Block block)
	{
		return isBlockNearby(block, false);
	}
	
	default boolean isBlockNearby(Block block, boolean ignoreUnloadChunk)
	{
		return W.isBlockNearby(world(), pos(), block, ignoreUnloadChunk);
	}
	
	default boolean canSeeSky()
	{
		return world().canSeeSky(pos());
	}
	
	default boolean canSkyLightFullyOn()
	{
		return world().canBlockSeeSky(pos());
	}
	
	default <T extends Entity> List<T> getEntitiesWithinAABB(Class<T> entityClass)
	{
		return world().getEntitiesWithinAABB(entityClass, new AxisAlignedBB(pos()));
	}
	
	default <T extends Entity> List<T> getEntitiesWithinAABB(Class<T> entityClass, float x1, float y1, float z1, float x2, float y2, float z2)
	{
		BlockPos pos = pos();
		return world().getEntitiesWithinAABB(entityClass, new AxisAlignedBB(pos.getX() + x1, pos.getY() + y1, pos.getZ() + z1, pos.getX() + x2, pos.getY() + y2, pos.getZ() + z2));
	}
	
	default boolean isPlantable(IPlantable plantable, Direction offset)
	{
		IBlockState state = world().getBlockState(offset.offset(pos()));
		return state.getBlock().canSustainPlant(state, world(), pos(), offset.opposite().of(), plantable);
	}
	
	default boolean isPlantable(IPlantable plantable, int x, int y, int z, Direction offset)
	{
		BlockPos pos = pos().add(x, y, z);
		IBlockState state = world().getBlockState(offset.offset(pos));
		return state.getBlock().canSustainPlant(state, world(), pos, offset.opposite().of(), plantable);
	}
	
	default int getBlockLightOpacity()
	{
		return world().getBlockLightOpacity(pos());
	}
	
	default int getBlockLightOpacity(Direction offset)
	{
		return world().getBlockLightOpacity(offset.offset(pos()));
	}
	
	default int getBlockLightOpacity(int x, int y, int z)
	{
		return world().getBlockLightOpacity(pos().add(x, y, z));
	}
	
	default int getDimension()
	{
		return world().provider.getDimension();
	}
	
	default long getWorldTime()
	{
		return world().getWorldTime();
	}
	
	default Biome getBiome()
	{
		return world().getBiome(pos());
	}
	
	default double getDistanceSq(BlockPos pos)
	{
		return pos().distanceSq(pos);
	}
	
	default boolean isAirBlock()
	{
		return world().isAirBlock(pos());
	}
	
	default boolean isAirBlock(Direction offset)
	{
		return world().isAirBlock(offset.offset(pos()));
	}
	
	default boolean isAirBlock(int x, int y, int z)
	{
		return world().isAirBlock(pos().add(x, y, z));
	}
	
	default boolean isRaining()
	{
		return world().isRaining();
	}
	
	default boolean isCatchingRain(boolean checkNeayby)
	{
		return W.isCatchingRain(world(), pos(), checkNeayby);
	}
	
	default boolean isSideSolid(Direction side)
	{
		return world().isSideSolid(pos(), side.of());
	}
	
	default boolean isSideSolid(Direction offset, Direction side)
	{
		return world().isSideSolid(offset.offset(pos()), side.of());
	}
	
	default boolean isNearbySolid(Direction offset)
	{
		return world().isSideSolid(offset.offset(pos()), offset.opposite().of());
	}
	
	default IFluidHandler getFluidHandler(Direction offset)
	{
		World world = world();
		BlockPos pos = offset.offset(pos());
		TileEntity tile = world.getTileEntity(pos);
		return tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, offset.opposite().of()) ? tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, offset.opposite().of()) : null;
	}
	
	default IItemHandler getItemHandler(Direction offset)
	{
		World world = world();
		BlockPos pos = offset.offset(pos());
		TileEntity tile = world.getTileEntity(pos);
		return tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, offset.opposite().of()) ? tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, offset.opposite().of()) : null;
	}
	
	// default IItemHandlerIO getItemIOHandler(Direction offset)
	// {
	// World world = world();
	// BlockPos pos = offset.offset(pos());
	// TileEntity tile = world.getTileEntity(pos);
	// return tile.hasCapability(Capabilities.ITEM_HANDLER_IO,
	// offset.getOpposite().of()) ?
	// tile.getCapability(Capabilities.ITEM_HANDLER_IO,
	// offset.getOpposite().of()) : null;
	// }
}
