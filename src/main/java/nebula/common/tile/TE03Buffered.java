/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.tile;

import java.util.Arrays;

import nebula.common.util.Direction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * The buffered TileEntity.
 * 
 * @author ueyudiud
 */
public class TE03Buffered extends TE02Updatable implements IUpdatableTile
{
	public long	timer		= Long.MIN_VALUE;
	public long	lastActived	= Long.MIN_VALUE;
	
	private TileEntity[] tileNearby = null;
	
	public TE03Buffered()
	{
		
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		regetTileNearby(true);
	}
	
	@Override
	public void update()
	{
		super.update();
	}
	
	@Override
	public void causeUpdate(BlockPos pos, IBlockState state, boolean tileUpdate)
	{
		regetTileNearby(true);
	}
	
	@Override
	public TileEntity getTE(Direction offset)
	{
		TileEntity[] tileEntities = regetTileNearby(false);
		return tileEntities == null ? null : tileEntities[offset.ordinal()];
	}
	
	private TileEntity[] regetTileNearby(boolean forceToUpdate)
	{
		if (this.world.isBlockLoaded(this.pos))
		{
			boolean flag = this.tileNearby == null;
			if (flag)
			{
				this.tileNearby = new TileEntity[6];
			}
			if (forceToUpdate || flag)
			{
				for (Direction direction : Direction.DIRECTIONS_3D)
				{
					this.tileNearby[direction.ordinal()] = super.getTE(direction);
				}
			}
			return this.tileNearby;
		}
		else
		{
			if (this.tileNearby == null) this.tileNearby = new TileEntity[6];
			Arrays.fill(this.tileNearby, null);
			return this.tileNearby;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.timer = nbt.getLong("timer");
		this.lastActived = nbt.getLong("lt");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setLong("timer", this.timer);
		nbt.setLong("lt", this.lastActived);
		return nbt;
	}
	
	@Override
	protected final void updateEntity1()
	{
		if (this.timer == Long.MIN_VALUE)
		{
			this.timer = this.world.getWorldTime();
		}
		updateEntity2();
		this.lastActived = this.world.getWorldTime();
	}
	
	protected void updateEntity2()
	{
		
	}
}
