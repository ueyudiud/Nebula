/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import javax.annotation.ParametersAreNonnullByDefault;

import nebula.common.inventory.ContainersEmpty.FluidContainersEmpty;
import nebula.common.inventory.task.Task;
import nebula.common.nbt.INBTSelfCompoundReaderAndWriter;
import nebula.common.nbt.NBTs;
import nebula.common.stack.SubfluidStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public interface IFluidContainers extends IContainers<FluidStack>, INBTSelfCompoundReaderAndWriter
{
	IFluidContainers EMPTY = new FluidContainersEmpty();
	
	@Override
	IFluidContainer[] getContainers();
	
	@Override
	IFluidContainer getContainer(int index);
	
	@Override
	default void writeTo(NBTTagCompound nbt)
	{
		NBTs.set(nbt, "data", getContainers());
	}
	
	@Override
	default NBTTagCompound writeTo(NBTTagCompound nbt, String key)
	{
		NBTs.set(nbt, key, getContainers());
		return nbt;
	}
	
	@Override
	default void readFrom(NBTTagCompound nbt)
	{
		NBTs.get(nbt, "data", getContainers());
	}
	
	@Override
	default void readFrom(NBTTagCompound nbt, String key)
	{
		NBTs.get(nbt, key, getContainers());
	}
	
	Task.TaskBTB taskInsertAllShaped(SubfluidStack[] stacks, int modifier);
	
	Task.TaskBTB taskInsertAllShapeless(SubfluidStack[] stacks, int modifier);
	
	Task.TaskBTB taskExtractAllShaped(SubfluidStack[] stacks, int modifier);
	
	Task.TaskBTB taskExtractAllShapeless(SubfluidStack[] stacks, int modifier);
}
