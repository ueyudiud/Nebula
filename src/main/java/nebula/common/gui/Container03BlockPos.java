/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

import nebula.common.world.ICoord;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author ueyudiud
 */
public class Container03BlockPos extends Container02Opener
{
	public final World		world;
	public final BlockPos	pos;
	private IBlockState		state;
	
	public Container03BlockPos(EntityPlayer player, ICoord coord)
	{
		this(player, coord.world(), coord.pos());
	}
	
	public Container03BlockPos(EntityPlayer player, World world, BlockPos pos)
	{
		super(player);
		this.world = world;
		this.pos = pos;
		// Get block state, to check if block changed to close GUI.
		this.state = world.getBlockState(pos);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return match(this.state, this.world.getBlockState(this.pos)) && playerIn.world == this.world && playerIn.getDistanceSq(this.pos.getX() + .5F, this.pos.getY() + .5F, this.pos.getZ() + .5F) <= 16;
	}
	
	protected boolean match(IBlockState oldState, IBlockState newstate)
	{
		return oldState == newstate;
	}
}
