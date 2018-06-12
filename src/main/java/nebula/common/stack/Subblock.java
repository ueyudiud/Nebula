/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.stack;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import nebula.V;
import nebula.base.collection.A;
import nebula.common.nbt.NBTFormat;
import nebula.common.util.ICapabilityMatcher;
import nebula.common.util.W;
import nebula.common.world.ICoord;
import nebula.common.world.IModifiableCoord;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public class Subblock
{
	private Block block;
	private Map<IProperty<?>, ?> properties;
	private NBTFormat format;
	private Collection<ICapabilityMatcher<?>>[] matchers;
	
	private IBlockState state;
	
	public Subblock(IBlockState state                  ) { this(state, NBTFormat.EMPTY); }
	public Subblock(IBlockState state, NBTFormat format) { this(state.getBlock(), state.getProperties(), format); }
	
	public Subblock(Block block, Map<IProperty<?>, ?> properties                                                                          ) { this(block, properties, NBTFormat.EMPTY); }
	public Subblock(Block block, Map<IProperty<?>, ?> properties, NBTFormat format                                                        ) { this(block, properties, format, null); }
	public Subblock(Block block, Map<IProperty<?>, ?> properties, NBTFormat format, @Nullable Collection<ICapabilityMatcher<?>>[] matchers)
	{
		this.block = block;
		this.properties = properties;
		this.format = format;
		this.matchers = matchers;
		assert check_();
	}
	
	private boolean check_()
	{
		for (Entry<IProperty<?>, ?> entry : (Iterable<Entry<IProperty<?>, ?>>) this.properties.values())
		{
			if (!entry.getKey().getAllowedValues().contains(entry.getValue()))
				return false;
		}
		return this.matchers == null || this.matchers.length == 7;
	}
	
	public Block getBlock()
	{
		return this.block;
	}
	
	public IBlockState getBlockState()
	{
		if (this.state == null)
		{
			this.state = A.collect2(
					this.properties.entrySet().stream(),
					this.block.getDefaultState(),
					(s, p, v) -> s.withProperty(p, V.cast(v)));
		}
		return this.state;
	}
	
	public void set(IModifiableCoord coord, int flag)
	{
		coord.setBlockState(getBlockState(), flag);
		if (this.format.hasRules() || this.matchers != null)
		{
			adjustTile(coord.getTE());
		}
	}
	
	public void set(World world, BlockPos pos, int flag)
	{
		world.setBlockState(pos, this.state, flag);
		if (this.format.hasRules() || this.matchers != null)
		{
			adjustTile(world.getTileEntity(pos));
		}
	}
	
	private void adjustTile(TileEntity te)
	{
		NBTTagCompound nbt = te.writeToNBT(new NBTTagCompound());
		nbt.merge(this.format.template());
		te.writeToNBT(nbt);
		if (this.matchers != null)
		{
			for (int i = 0; i < 6; ++i)
			{
				if (this.matchers[i] != null)
				{
					EnumFacing facing = EnumFacing.VALUES[i];
					for (ICapabilityMatcher<?> matcher : this.matchers[i])
					{
						adjustCapability(te, matcher, facing);
					}
				}
			}
			if (this.matchers[6] != null)
			{
				for (ICapabilityMatcher<?> matcher : this.matchers[6])
				{
					adjustCapability(te, matcher, null);
				}
			}
		}
	}
	
	private <T> void adjustCapability(TileEntity te, ICapabilityMatcher<T> matcher, EnumFacing facing)
	{
		try
		{
			T value = Objects.requireNonNull(te.getCapability(null, facing));
			matcher.adjust(facing, value);
		}
		catch (Exception exception)
		{
			throw new IllegalStateException("Illegal matcher: " + matcher);
		}
	}
	
	public boolean match(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != this.block)
			return false;
		for (Entry<IProperty<?>, ?> entry : (Iterable<Entry<IProperty<?>, ?>>) this.properties.values())
		{
			if (state.getValue(entry.getKey()) != entry.getValue())
			{
				return false;
			}
		}
		if (this.format.hasRules() || this.matchers != null)
		{
			TileEntity tile = W.getTileEntity(world, pos, false);
			if (tile == null)
				return false;
			if (!matchCapability(tile) || !this.format.apply(tile.writeToNBT(new NBTTagCompound())))
				return false;
		}
		return true;
	}
	
	public boolean match(ICoord coord)
	{
		IBlockState state = coord.getBlockState();
		if (state.getBlock() != this.block)
			return false;
		for (Entry<IProperty<?>, ?> entry : (Iterable<Entry<IProperty<?>, ?>>) this.properties.values())
		{
			if (state.getValue(entry.getKey()) != entry.getValue())
			{
				return false;
			}
		}
		if (this.format.hasRules() || this.matchers != null)
		{
			TileEntity tile = coord.getTE();
			if (tile == null)
				return false;
			if (!matchCapability(tile) || !this.format.apply(tile.writeToNBT(new NBTTagCompound())))
				return false;
		}
		return true;
	}
	
	private boolean matchCapability(@Nullable TileEntity tile)
	{
		if (this.matchers != null)
		{
			for (int i = 0; i < 6; ++i)
			{
				if (this.matchers[i] != null)
				{
					EnumFacing facing = EnumFacing.VALUES[i];
					for (ICapabilityMatcher<?> matcher : this.matchers[i])
					{
						Capability<?> capability = matcher.target();
						if (!tile.hasCapability(capability, facing) || !matcher.test(V.cast(tile.getCapability(capability, facing))))
							return false;
					}
				}
			}
			if (this.matchers[6] != null)
			{
				for (ICapabilityMatcher<?> matcher : this.matchers[6])
				{
					Capability<?> capability = matcher.target();
					if (!tile.hasCapability(capability, null) || !matcher.test(V.cast(tile.getCapability(capability, null))))
						return false;
				}
			}
			return true;
		}
		else
		{
			return true;
		}
	}
}
