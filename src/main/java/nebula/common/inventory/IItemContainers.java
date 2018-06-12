/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import javax.annotation.ParametersAreNonnullByDefault;

import nebula.common.inventory.ContainersEmpty.ItemContainersEmpty;
import nebula.common.inventory.task.Task;
import nebula.common.nbt.INBTSelfCompoundReaderAndWriter;
import nebula.common.nbt.NBTs;
import nebula.common.stack.AbstractStack;
import nebula.common.stack.SubitemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public interface IItemContainers extends IContainers<ItemStack>, INBTSelfCompoundReaderAndWriter
{
	IItemContainers EMPTY = new ItemContainersEmpty();
	
	@Override
	IItemContainer[] getContainers();
	
	@Override
	IItemContainer getContainer(int index);
	
	//Used for inventory.
	default ItemStack getStackInContainer(int index)
	{
		return getContainer(index).getStackInContainer();
	}
	
	default ItemStack extractStack(int index, int count)
	{
		return getContainer(index).extractStack(count, IItemContainer.PROCESS);
	}
	
	default ItemStack extractStack(int index, ItemStack stack)
	{
		return getContainer(index).extractStack(stack, IItemContainer.PROCESS);
	}
	
	default void setStackInContainer(int index, ItemStack stack)
	{
		getContainer(index).setStackInContainer(stack);
	}
	//Inventory helper method end.
	
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
	
	Task.TaskBTB taskInsertAllShaped(AbstractStack[] stacks, int modifier);
	
	Task.TaskBTB taskInsertAllShaped(SubitemStack[] stacks, int modifier);
	
	Task.TaskBTB taskInsertAllShapeless(AbstractStack[] stacks, int modifier);
	
	Task.TaskBTB taskInsertAllShapeless(SubitemStack[] stacks, int modifier);
	
	Task.TaskBTB taskExtractAllShaped(AbstractStack[] stacks, int modifier);
	
	Task.TaskBTB taskExtractAllShaped(SubitemStack[] stacks, int modifier);
	
	Task.TaskBTB taskExtractAllShapeless(AbstractStack[] stacks, int modifier);
	
	Task.TaskBTB taskExtractAllShapeless(SubitemStack[] stacks, int modifier);
}
