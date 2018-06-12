/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import nebula.common.inventory.task.Task;
import nebula.common.inventory.task.Task.TaskBTB;
import nebula.common.nbt.INBTSelfCompoundReaderAndWriter;
import nebula.common.stack.AbstractStack;
import nebula.common.stack.SubfluidStack;
import nebula.common.stack.SubitemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
public abstract class ContainersEmpty<T> implements IContainers<T>, INBTSelfCompoundReaderAndWriter
{
	protected ContainersEmpty() {}
	
	static final class ItemContainersEmpty extends ContainersEmpty<ItemStack> implements IItemContainers
	{
		private static final IItemContainer[] CONTAINERS = new IItemContainer[0];
		
		@Override
		public IItemContainer getContainer(int index)
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		
		@Override
		public IItemContainer[] getContainers()
		{
			return CONTAINERS;
		}
		
		public TaskBTB taskInsertAllShaped    (AbstractStack[] stacks, int modifier) { return task(stacks); }
		public TaskBTB taskInsertAllShaped    (SubitemStack[]  stacks, int modifier) { return task(stacks); }
		public TaskBTB taskInsertAllShapeless (AbstractStack[] stacks, int modifier) { return task(stacks); }
		public TaskBTB taskInsertAllShapeless (SubitemStack[]  stacks, int modifier) { return task(stacks); }
		public TaskBTB taskExtractAllShaped   (AbstractStack[] stacks, int modifier) { return task(stacks); }
		public TaskBTB taskExtractAllShaped   (SubitemStack[]  stacks, int modifier) { return task(stacks); }
		public TaskBTB taskExtractAllShapeless(AbstractStack[] stacks, int modifier) { return task(stacks); }
		public TaskBTB taskExtractAllShapeless(SubitemStack[]  stacks, int modifier) { return task(stacks); }
	}
	
	static final class FluidContainersEmpty extends ContainersEmpty<FluidStack> implements IFluidContainers
	{
		private static final IFluidContainer[] CONTAINERS = new IFluidContainer[0];
		
		@Override
		public IFluidContainer getContainer(int index)
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		
		@Override
		public IFluidContainer[] getContainers()
		{
			return CONTAINERS;
		}
		
		public TaskBTB taskInsertAllShaped    (SubfluidStack[]  stacks, int modifier) { return task(stacks); }
		public TaskBTB taskInsertAllShapeless (SubfluidStack[]  stacks, int modifier) { return task(stacks); }
		public TaskBTB taskExtractAllShaped   (SubfluidStack[]  stacks, int modifier) { return task(stacks); }
		public TaskBTB taskExtractAllShapeless(SubfluidStack[]  stacks, int modifier) { return task(stacks); }
	}
	
	@Override
	public void writeTo(NBTTagCompound nbt)
	{
		
	}
	
	@Override
	public NBTTagCompound writeTo(NBTTagCompound nbt, String key)
	{
		return nbt;
	}
	
	@Override
	public void readFrom(NBTTagCompound nbt, String key)
	{
		
	}
	
	@Override
	public void readFrom(NBTTagCompound nbt)
	{
		
	}
	
	@Override
	public void clear()
	{
		
	}
	
	boolean access(Object[]    values) { return values.length == 0; }
	boolean access(Iterable<?> values) { return !values.iterator().hasNext(); }
	
	Task.TaskBTB task(Object[]    values) { return access(values) ? Task.pass() : Task.fail(); }
	Task.TaskBTB task(Iterable<?> values) { return access(values) ? Task.pass() : Task.fail(); }
	
	public int getContainerSize() { return 0; }
	public IContainer<T> getContainer(int index) { throw new ArrayIndexOutOfBoundsException(); }
	
	public boolean insertAllStackShaped   (T[]                   stacks, int modifier) { return access(stacks); }
	public boolean insertAllStackShapeless(T[]                   stacks, int modifier) { return access(stacks); }
	public boolean insertAllStackShapeless(Iterable<? extends T> stacks, int modifier) { return access(stacks); }
	
	public boolean extractAllStackShaped   (T[]                   stacks, int modifier) { return access(stacks); }
	public boolean extractAllStackShapeless(T[]                   stacks, int modifier) { return access(stacks); }
	public boolean extractAllStackShapeless(Iterable<? extends T> stacks, int modifier) { return access(stacks); }
	
	public TaskBTB taskInsertAllShaped   (T[]                   stacks, int modifier) { return task(stacks); }
	public TaskBTB taskInsertAllShapeless(T[]                   stacks, int modifier) { return task(stacks); }
	public TaskBTB taskInsertAllShapeless(Iterable<? extends T> stacks, int modifier) { return task(stacks); }
	
	public TaskBTB taskExtractAllShaped   (T[]                   stacks, int modifier) { return task(stacks); }
	public TaskBTB taskExtractAllShapeless(T[]                   stacks, int modifier) { return task(stacks); }
	public TaskBTB taskExtractAllShapeless(Iterable<? extends T> stacks, int modifier) { return task(stacks); }
}
