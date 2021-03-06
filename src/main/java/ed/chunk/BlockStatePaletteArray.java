/*
 * copyright 2016-2018 ueyudiud
 */
package ed.chunk;

import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.IBlockStatePalette;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
public class BlockStatePaletteArray implements IBlockStatePalette
{
	private int								capacity	= 0;
	private final int						bitCount;
	private final IBlockState[]				states;
	private final BlockStateContainerExt	resizer;
	
	public BlockStatePaletteArray(int bitCount, BlockStateContainerExt resizer)
	{
		this.bitCount = bitCount;
		this.states = new IBlockState[1 << bitCount];
		this.resizer = resizer;
	}
	
	@Override
	public int idFor(IBlockState state)
	{
		for (int i = 0; i < this.capacity; ++i)
		{
			if (this.states[i] == state)
			{
				return i;
			}
		}
		
		int j = this.capacity;
		
		if (j < this.states.length)
		{
			this.states[j] = state;
			++ this.capacity;
			return j;
		}
		else
		{
			return this.resizer.onResize(this.bitCount + 1, state);
		}
	}
	
	@Override
	public IBlockState getBlockState(int indexKey)
	{
		return indexKey >= 0 && indexKey < this.capacity ? this.states[indexKey] : null;
	}
	
	@SideOnly(Side.CLIENT)
	public void read(PacketBuffer buf)
	{
		this.capacity = buf.readVarInt();
		
		for (int i = 0; i < this.capacity; ++i)
		{
			this.states[i] = ExtendedBlockStateRegister.INSTANCE.getStateFromNetworkID(buf.readVarInt());
		}
	}
	
	public void write(PacketBuffer buf)
	{
		buf.writeVarInt(this.capacity);
		
		for (int i = 0; i < this.capacity; ++i)
		{
			buf.writeVarInt(ExtendedBlockStateRegister.INSTANCE.getNetworkID(this.states[i]));
		}
	}
	
	public int getSerializedState()
	{
		int size = PacketBuffer.getVarIntSize(this.capacity);
		
		for (int i = 0; i < this.capacity; ++i)
		{
			size += PacketBuffer.getVarIntSize(ExtendedBlockStateRegister.INSTANCE.getNetworkID(this.states[i]));
		}
		return size;
	}
}
