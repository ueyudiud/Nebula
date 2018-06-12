/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.tile;

import java.util.Random;

import nebula.Nebula;
import nebula.V;
import nebula.common.G;
import nebula.common.NebulaCommonHandler;
import nebula.common.annotations.TEAnnotations;
import nebula.common.block.BlockTE;
import nebula.common.network.IPacket;
import nebula.common.util.Direction;
import nebula.common.util.Players;
import nebula.common.util.W;
import nebula.common.world.IModifiableCoord;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Base TileEntity, add some helper method.
 * 
 * @author ueyudiud
 */
public class TE00Base extends TileEntity implements IModifiableCoord, TEAnnotations
{
	protected IBlockState	state;
	public Random			random	= new Random();
	public boolean			isUpdating;
	private int				lightLevel;
	
	public TE00Base()
	{
		
	}
	
	@Override
	public void onLoad()
	{
	}
	
	public boolean isInitialized()
	{
		return true;
	}
	
	public boolean isUpdating()
	{
		return this.isUpdating;
	}
	
	@Override
	public boolean isInvalid()
	{
		return super.isInvalid();
	}
	
	public boolean isClient()
	{
		return this.world == null ? !G.isSimulating() : this.world.isRemote;
	}
	
	public boolean isServer()
	{
		return this.world == null ? G.isSimulating() : !this.world.isRemote;
	}
	
	public boolean isDebugWorld()
	{
		return this.world.getWorldType() == WorldType.DEBUG_WORLD;
	}
	
	public void sendToAll(IPacket player)
	{
		if (this.world != null)
		{
			Nebula.network.sendToAll(player);
		}
	}
	
	public void sendToServer(IPacket packet)
	{
		if (this.world != null)
		{
			Nebula.network.sendToServer(packet);
		}
	}
	
	public void sendToPlayer(IPacket packet, EntityPlayer player)
	{
		if (this.world != null)
		{
			Nebula.network.sendToPlayer(packet, player);
		}
	}
	
	public void sendLargeToPlayer(IPacket packet, EntityPlayer player)
	{
		if (this.world != null)
		{
			Nebula.network.sendLargeToPlayer(packet, player);
		}
	}
	
	public void sendToNearby(IPacket packet, float range)
	{
		if (this.world != null)
		{
			Nebula.network.sendToNearBy(packet, this, range);
		}
	}
	
	public void sendToDim(IPacket packet)
	{
		if (this.world != null)
		{
			sendToDim(packet, this.world.provider.getDimension());
		}
	}
	
	public void sendToDim(IPacket packet, int dim)
	{
		if (this.world != null)
		{
			Nebula.network.sendToDim(packet, dim);
		}
	}
	
	public void syncToAll()
	{
		
	}
	
	public void syncToDim()
	{
		
	}
	
	public void syncToNearby()
	{
		
	}
	
	public void syncToPlayer(EntityPlayer player)
	{
		
	}
	
	public void markTileUpdate(int type)
	{
		NebulaCommonHandler.markTileEntityForUpdate(getDimension(), this.pos, type);
	}
	
	@Override
	public void markBlockUpdate()
	{
		this.world.notifyBlockOfStateChange(this.pos, getBlockType());
	}
	
	@Override
	public void markBlockRenderUpdate()
	{
		this.world.markBlockRangeForRenderUpdate(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1));
	}
	
	@Override
	public double getDistanceSq(BlockPos pos)
	{
		return this.pos.distanceSq(pos);
	}
	
	public double getDistanceFrom(BlockPos pos)
	{
		return Math.sqrt(getDistanceFrom(pos));
	}
	
	@Override
	public double getDistanceSq(double x, double y, double z)
	{
		return super.getDistanceSq(x, y, z);
	}
	
	public double getDistanceFrom(double x, double y, double z)
	{
		return Math.sqrt(getDistanceSq(x, y, z));
	}
	
	public double getDistanceSq(Entity entity)
	{
		return getDistanceSq(entity.posX, entity.posY, entity.posZ);
	}
	
	public double getDistanceFrom(Entity entity)
	{
		return getDistanceFrom(entity.posX, entity.posY, entity.posZ);
	}
	
	public Direction getRotation()
	{
		return Direction.Q;
	}
	
	public void onNeighbourBlockChange()
	{
		
	}
	
	/**
	 * The rotate for block check, INFO : The direction must be 2D rotation!
	 * 
	 * @param frontOffset
	 * @param lrOffset
	 * @param udOffset
	 * @param direction
	 * @param block
	 * @param meta
	 * @param ignoreUnloadChunk
	 * @return
	 */
	@Deprecated
	public boolean matchBlock(int frontOffset, int lrOffset, int udOffset, Direction direction, Block block, int meta, boolean ignoreUnloadChunk)
	{
		if (this.world == null) return false;
		int x = frontOffset * direction.x + lrOffset * direction.z;
		int y = udOffset;
		int z = frontOffset * direction.z + lrOffset * direction.x;
		return matchBlock(x, y, z, block, meta, ignoreUnloadChunk);
	}
	
	public boolean matchBlock(int offsetX, int offsetY, int offsetZ, Block block, int meta, boolean ignoreUnloadChunk)
	{
		return this.world == null ? false : W.isBlock(this.world, this.pos.add(offsetX, offsetY, offsetZ), block, meta, ignoreUnloadChunk);
	}
	
	public boolean matchBlockNearby(int offsetX, int offsetY, int offsetZ, Block block, int meta, boolean ignoreUnloadChunk)
	{
		return this.world == null ? false : W.isBlockNearby(this.world, this.pos.add(offsetX, offsetY, offsetZ), block, meta, ignoreUnloadChunk);
	}
	
	@Override
	public void explode(boolean removeTile, float strength, boolean isFlaming, boolean isSmoking)
	{
		IBlockState state = null;
		if (!removeTile)
		{
			state = this.world.getBlockState(this.pos);
		}
		this.world.setBlockState(this.pos, Blocks.AIR.getDefaultState(), removeTile ? 3 : 4);
		this.world.newExplosion(null, this.pos.getX() + .5, this.pos.getY() + .5, this.pos.getZ() + .5, strength, isFlaming, isSmoking);
		if (!removeTile)
		{
			this.world.setBlockState(this.pos, state, 4);
			this.world.setTileEntity(this.pos, this);
		}
	}
	
	@Override
	public boolean removeBlock()
	{
		return this.world.setBlockToAir(this.pos);
	}
	
	public boolean canHarvestBlock(EntityPlayer player)
	{
		return ForgeHooks.canHarvestBlock(getBlockType(), player, this.world, this.pos);
	}
	
	public float getBlockHardness(IBlockState state)
	{
		return 1.0F;
	}
	
	public float getExplosionResistance(Entity exploder, Explosion explosion)
	{
		return 1.0F;
	}
	
	public int getLightOpacity(IBlockState state)
	{
		return 255;
	}
	
	public int getLightValue(IBlockState state)
	{
		return 0;
	}
	
	public void onBlockAdded(IBlockState state)
	{
		
	}
	
	public void onBlockBreak(IBlockState state)
	{
		
	}
	
	public EnumActionResult onBlockActivated(EntityPlayer player, EnumHand hand, ItemStack stack, Direction side, float hitX, float hitY, float hitZ)
	{
		return EnumActionResult.PASS;
	}
	
	public boolean onBlockClicked(EntityPlayer player, Direction side, float hitX, float hitY, float hitZ)
	{
		return false;
	}
	
	@Override
	public void markLightForUpdate(EnumSkyBlock type)
	{
		int level = this.world.getLightFor(type, this.pos);
		if (this.lightLevel != level)
		{
			this.world.checkLight(this.pos);
			this.lightLevel = level;
		}
	}
	
	public boolean canBlockStay()
	{
		return this.world == null ? true : getBlockType().canPlaceBlockAt(this.world, this.pos);
	}
	
	public boolean tileGUICheck(EnumHand hand)
	{
		return isServer() && hand == EnumHand.MAIN_HAND;
	}
	
	public void openGUI(EntityPlayer player, int id)
	{
		openGUI(player, Nebula.MODID, id);
	}
	
	public void openGUI(EntityPlayer player, Object modid, int id)
	{
		//		player.openGui(modid, id, this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ());
		Players.openGui(player, modid, id, this.world, this.pos);
	}
	
	@Override
	public IBlockState getBlockState()
	{
		if (this.state == null)
		{
			regetBlockState();
		}
		return this.state;
	}
	
	@Override
	public final TileEntity getTE()
	{
		return this;
	}
	
	public int getLight(int xOffset, int yOffset, int zOffset, EnumSkyBlock type)
	{
		return this.world.getLightFor(type, this.pos.add(xOffset, yOffset, zOffset));
	}
	
	@Override
	public World world()
	{
		return this.world;
	}
	
	@Override
	public BlockPos pos()
	{
		return this.pos;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return oldState != newSate;
	}
	
	@Override
	public void updateContainingBlockInfo()
	{
		this.state = null;
		this.blockType = null;
	}
	
	protected void regetBlockState()
	{
		this.state = this.world.getBlockState(this.pos);
		this.blockType = this.state.getBlock();
		if (this.blockType instanceof BlockTE)
		{
			this.state = ((BlockTE) this.blockType).property_TE.withProperty(this.state, this);
			// Mark for real tile entity property.
		}
	}
	
	@Override
	public void markDirty()
	{
		if (this.world != null)
		{
			regetBlockState();
			
			this.world.markChunkDirty(this.pos, this);
			this.world.updateComparatorOutputLevel(this.pos, getBlockType());
		}
	}
	
	@Override
	public Block getBlockType()
	{
		if (this.state == null)
		{
			regetBlockState();
		}
		return this.state.getBlock();
	}
	
	@Override
	public int getBlockMetadata()
	{
		if (this.state == null)
		{
			regetBlockState();
		}
		return this.state.getBlock().getMetaFromState(this.state);
	}
	
	public int sendItemStackTo(ItemStack stack, Direction side, boolean fullStackTransfer, boolean dropToWorld, boolean process)
	{
		if (dropToWorld && W.isItemDropable(this.world, this.pos.offset(side.of()), side.opposite()))
		{
			if (process)
			{
				W.spawnDropInWorld(this.world, this.pos, side, stack);
			}
			return stack.stackSize;
		}
		TileEntity tile = getTE(side);
		if (tile == null)
		{
			return 0;
		}
		else if (tile.hasCapability(V.CAPABILITY_FLUID, side.opposite().of()))
		{
			IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.opposite().of());
			for (int i = 0; i < handler.getSlots(); ++i)
			{
				ItemStack remain = handler.insertItem(i, stack, true);
				if (fullStackTransfer ? remain == null : remain.stackSize < stack.stackSize)
				{
					if (process) handler.insertItem(i, stack, false);
					return stack.stackSize - remain.stackSize;
				}
			}
		}
		return 0;
	}
	
	public int sendFluidStackTo(FluidStack stack, Direction side, boolean process)
	{
		TileEntity tile = getTE(side);
		if (tile == null) return 0;
		if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.opposite().of()))
		{
			IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.opposite().of());
			return handler.fill(stack, process);
		}
		return 0;
	}
	
	public Direction getFacing()
	{
		return Direction.Q;
	}
}
