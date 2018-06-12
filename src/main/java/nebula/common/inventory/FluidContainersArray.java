/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import java.util.Arrays;

import nebula.base.collection.A;
import nebula.common.data.NBTLSs;
import nebula.common.nbt.NBTs;
import nebula.common.stack.FS;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
public class FluidContainersArray extends FluidContainers<FluidContainerArraySimple> implements IContainersArray<FluidStack>
{
	public final FluidStack[] stacks;
	
	public FluidContainersArray(int size, int limit)
	{
		super(FluidContainerArraySimple.create(size, limit));
		this.stacks = this.containers[0].stacks;
	}
	
	public FluidContainersArray(FluidStack[] stacks, int limit)
	{
		super(FluidContainerArraySimple.create(stacks, limit));
		this.stacks = stacks;
	}
	
	@Override
	public FluidStack[] toArray()
	{
		FluidStack[] result = new FluidStack[this.stacks.length];
		A.fill(result, i -> FS.copy(this.stacks[i]));
		return result;
	}
	
	@Override
	public void fromArray(FluidStack[] array)
	{
		A.fill(this.stacks, 0, Math.min(this.stacks.length, array.length), i -> FS.copy(array[i]));
		if (this.stacks.length > array.length)
		{
			Arrays.fill(this.stacks, array.length, this.stacks.length, null);
		}
	}
	
	@Override
	public void clear()
	{
		Arrays.fill(this.stacks, null);
	}
	
	@Override
	public void clearRange(int from, int to)
	{
		Arrays.fill(this.stacks, from, to, null);
	}
	
	@Override
	public NBTTagCompound writeTo(NBTTagCompound nbt, String key)
	{
		this.handler.writeTo(nbt, key, this.stacks);
		return nbt;
	}
	
	@Override
	public void writeTo(NBTTagCompound nbt)
	{
		NBTs.set(nbt, "stacks", this.stacks, NBTLSs.RW_FLUIDSTACK);
	}
	
	@Override
	public void readFrom(NBTTagCompound nbt, String key)
	{
		this.handler.readFrom(nbt, key, this.stacks);
	}
	
	@Override
	public void readFrom(NBTTagCompound nbt)
	{
		NBTs.get(nbt, "stacks", this.stacks, NBTLSs.RW_FLUIDSTACK);
	}
}
