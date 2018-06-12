/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import java.lang.reflect.Array;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import nebula.common.fluid.container.IItemFluidContainer;
import nebula.common.fluid.container.IItemFluidContainerV1;
import nebula.common.fluid.container.IItemFluidContainerV2;
import nebula.common.inventory.task.Task;
import nebula.common.inventory.task.TaskList;
import nebula.common.stack.AbstractStack;
import nebula.common.stack.FS;
import nebula.common.stack.IS;
import nebula.common.tool.EnumToolType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * The inventory helper method.
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public final class InventoryHelper
{
	private InventoryHelper() { }
	
	public static Task.TaskBTB taskMultiInsertShapeless(ItemStack[] stacks, int from, int to, ItemStack[] insert, boolean process)
	{
		return t -> {
			ItemStack[] simulates = copyOfRange(IStackHandler.ITEMSTACK_HANDLER, stacks, from, to);
			int free = 0;
			access: for (ItemStack s : insert)
			{
				if (s == null) continue;
				int size = s.stackSize;
				for (ItemStack simulate : simulates)
				{
					if (simulate != null && IS.similar_(simulate, s))
					{
						int i = simulate.getMaxStackSize() - simulate.stackSize;
						if (i > 0)
						{
							if (size > i)
							{
								size -= i;
								simulate.stackSize += i;
							}
							else
							{
								continue access;
							}
						}
					}
				}
				for (; free < simulates.length; ++free)
				{
					if (simulates[free] == null)
					{
						simulates[free] = s.copy();
						continue access;
					}
				}
				return false;
			}
			if (process)
			{
				System.arraycopy(insert, 0, stacks, from, simulates.length);
			}
			return true;
		};
	}
	
	public static <T> Task.TaskBTB taskMultiInsertShapeless(ItemStack[] stacks, int from, int to, Iterable<? extends ItemStack> insert, boolean process)
	{
		return t -> {
			ItemStack[] simulates = copyOfRange(IStackHandler.ITEMSTACK_HANDLER, stacks, from, to);
			int free = 0;
			access: for (ItemStack s : insert)
			{
				if (s == null) continue;
				int size = s.stackSize;
				for (ItemStack simulate : simulates)
				{
					if (simulate != null && IS.similar_(simulate, s))
					{
						int i = simulate.getMaxStackSize() - simulate.stackSize;
						if (i > 0)
						{
							if (size > i)
							{
								size -= i;
								simulate.stackSize += i;
							}
							else
							{
								continue access;
							}
						}
					}
				}
				for (; free < simulates.length; ++free)
				{
					if (simulates[free] == null)
					{
						simulates[free] = s.copy();
						continue access;
					}
				}
				return false;
			}
			if (process)
			{
				System.arraycopy(insert, 0, stacks, from, simulates.length);
			}
			return true;
		};
	}
	
	/**
	 * Create a <tt>insert</tt> task.
	 * @param handler the stack handler.
	 * @param stacks the slots.
	 * @param index the inserted slot index.
	 * @param stack the insert stack.
	 * @param process take the insert operation.
	 * @return the task.
	 */
	public static <T> Task.TaskBTB taskInsertAll(final IStackHandler<T> handler, T[] stacks, int index, int maxSize, @Nullable T stack, boolean process)
	{
		return handler.validate(stack) == null ? Task.pass() : t -> {
			if (stacks[index] == null)
			{
				if (handler.size(stack) <= maxSize && t.test())
				{
					if (process)
					{
						stacks[index] = handler.copy(stack);
					}
					return true;
				}
				return false;
			}
			else if (handler.isSimilar(stacks[index], stack))
			{
				int size = handler.size(stacks[index]) + handler.size(stack);
				if (size <= maxSize && t.test())
				{
					if (process)
					{
						handler.size(stacks[index], size);
					}
					return true;
				}
				return false;
			}
			else
			{
				return false;
			}
		};
	}
	
	/**
	 * Create a <tt>extract</tt> task.
	 * @param handler the stack handler.
	 * @param stacks the slots.
	 * @param index the extracted slot index.
	 * @param stack the extract stack.
	 * @param process take the extract operation.
	 * @return the task.
	 */
	public static <T> Task.TaskBTB taskExtractAll(final IStackHandler<T> handler, T[] stacks, int index, @Nullable T stack, boolean process)
	{
		return handler.validate(stack) == null ? Task.pass() : t -> {
			if (stacks[index] == null)
			{
				return false;
			}
			else if (handler.isSimilar(stacks[index], stack))
			{
				int size = handler.size(stacks[index]) - handler.size(stack);
				if (size > 0 && t.test())
				{
					if (process)
					{
						if (size == 0)
						{
							stacks[index] = null;
						}
						else
						{
							handler.size(stacks[index], size);
						}
					}
					return true;
				}
				return false;
			}
			else
			{
				return false;
			}
		};
	}
	
	/**
	 * Create a <tt>insert</tt> task.
	 * @param handler the stack handler.
	 * @param stacks the slots.
	 * @param index the inserted slot index.
	 * @param process take the insert operation.
	 * @return the task, the first argument is insert stack..
	 */
	public static <T> Task.TaskOTB<T> taskInsertFrom(final IStackHandler<T> handler, T[] stacks, int index, int maxSize, boolean process)
	{
		return s -> {
			T stack = s.result();
			if (stack == null)
			{
				return true;
			}
			if (stacks[index] == null)
			{
				if (handler.size(stack) <= maxSize)
				{
					if (process)
					{
						stacks[index] = handler.copy(stack);
					}
					return true;
				}
				return false;
			}
			else if (handler.isSimilar(stacks[index], stack))
			{
				int size = handler.size(stacks[index]) + handler.size(stack);
				if (size <= maxSize)
				{
					if (process)
					{
						handler.size(stacks[index], size);
					}
					return true;
				}
				return false;
			}
			else
			{
				return false;
			}
		};
	}
	
	/**
	 * Create a <tt>insert</tt> task.
	 * @param stacks the slots.
	 * @param index the inserted slot index.
	 * @param stack the insert stack.
	 * @param process take the insert operation.
	 * @return the task.
	 */
	public static Task.TaskBTB taskInsertAll(ItemStack[] stacks, int index, @Nullable ItemStack stack, boolean process)
	{
		return IS.validate(stack) == null ? Task.pass() : t -> {
			if (stacks[index] == null)
			{
				if (t.test())
				{
					if (process)
					{
						stacks[index] = ItemStack.copyItemStack(stack);
					}
					return true;
				}
				return false;
			}
			else if (IS.similar_(stacks[index], stack))
			{
				int size = stacks[index].stackSize + stack.stackSize;
				if (size <= stack.getMaxStackSize() && t.test())
				{
					if (process)
					{
						stacks[index].stackSize = size;
					}
					return true;
				}
				return false;
			}
			else
			{
				return false;
			}
		};
	}
	
	/**
	 * Create a <tt>extract</tt> task.
	 * @param stacks the slots.
	 * @param index the extracted slot index.
	 * @param stack the extract stack.
	 * @param process take the extract operation.
	 * @return the task.
	 */
	public static Task.TaskBTB taskExtractAll(ItemStack[] stacks, int index, @Nullable ItemStack stack, boolean process)
	{
		return IS.validate(stack) == null ? Task.pass() : t -> {
			if (stacks[index] == null)
			{
				return false;
			}
			else if (IS.similar_(stacks[index], stack))
			{
				int size = stacks[index].stackSize - stack.stackSize;
				if (size > 0 && t.test())
				{
					if (process)
					{
						if (size == 0)
						{
							stacks[index] = null;
						}
						else
						{
							stacks[index].stackSize = size;
						}
					}
					return true;
				}
				return false;
			}
			else
			{
				return false;
			}
		};
	}
	
	/**
	 * Create a <tt>insert</tt> task.
	 * @param stacks the slots.
	 * @param index the inserted slot index.
	 * @param stack the insert stack.
	 * @param process take the insert operation.
	 * @return the task.
	 */
	public static Task.TaskBTB taskInsertAll(ItemStack[] stacks, int index, @Nullable AbstractStack stack, boolean process)
	{
		return stack == null || !stack.valid() ? Task.pass() : t -> {
			if (stacks[index] == null)
			{
				if (t.test())
				{
					if (process)
					{
						stacks[index] = stack.instance();
					}
					return true;
				}
				return false;
			}
			else if (stack.similar(stacks[index]))
			{
				int size = stacks[index].stackSize + stack.size(stacks[index]);
				if (size < stacks[index].getMaxStackSize() && t.test())
				{
					if (process)
					{
						stacks[index].stackSize = size;
					}
					return true;
				}
				return false;
			}
			else
			{
				return false;
			}
		};
	}
	
	/**
	 * Create a <tt>extract</tt> task.
	 * @param stacks the slots.
	 * @param index the extracted slot index.
	 * @param stack the extract stack.
	 * @param process take the extract operation.
	 * @return the task.
	 */
	public static Task.TaskBTB taskExtractAll(ItemStack[] stacks, int index, @Nullable AbstractStack stack, boolean process)
	{
		return stack == null || !stack.valid() ? Task.pass() : t -> {
			if (stacks[index] == null)
			{
				return false;
			}
			else if (stack.contain(stacks[index]))
			{
				if (t.test())
				{
					if (process)
					{
						stack.extract(stacks, index);
					}
					return true;
				}
				return false;
			}
			else
			{
				return false;
			}
		};
	}
	
	/**
	 * Create a <tt>insert</tt> task.
	 * @param stacks the slots.
	 * @param index the inserted slot index.
	 * @param stack the insert stack.
	 * @param process take the insert operation.
	 * @return the task.
	 */
	public static Task.TaskBTB taskInsertAll(FluidStack[] stacks, int index, int maxAmount, @Nullable FluidStack stack, boolean process)
	{
		return FS.validate(stack) == null ? Task.pass() : t -> {
			if (stacks[index] == null)
			{
				if (t.test())
				{
					if (process)
					{
						stacks[index] = FS.copy(stack);
					}
					return true;
				}
				return false;
			}
			else if (FS.similar_(stacks[index], stack))
			{
				int size = stacks[index].amount + stack.amount;
				if (size <= maxAmount && t.test())
				{
					if (process)
					{
						stacks[index].amount = size;
					}
					return true;
				}
				return false;
			}
			else
			{
				return false;
			}
		};
	}
	
	/**
	 * Create a <tt>extract</tt> task.
	 * @param stacks the slots.
	 * @param index the extracted slot index.
	 * @param stack the extract stack.
	 * @param process take the extract operation.
	 * @return the task.
	 */
	public static Task.TaskBTB taskExtractAll(FluidStack[] stacks, int index, @Nullable FluidStack stack, boolean process)
	{
		return FS.validate(stack) == null ? Task.pass() : t -> {
			if (stacks[index] == null)
			{
				return false;
			}
			else if (FS.similar_(stacks[index], stack))
			{
				int size = stacks[index].amount - stack.amount;
				if (size > 0 && t.test())
				{
					if (process)
					{
						if (size == 0)
						{
							stacks[index] = null;
						}
						else
						{
							stacks[index].amount = size;
						}
					}
					return true;
				}
				return false;
			}
			else
			{
				return false;
			}
		};
	}
	
	/**
	 * Create a <tt>fill</tt> or <tt>drain</tt> task.
	 * @param stacks1 the stacks.
	 * @param index1 the input slot index.
	 * @param index2 the output slot index.
	 * @param stacks2 the stacks.
	 * @param index3 the filled/drained slot index.
	 * @param maxAmount the fluid capacity.
	 * @param doFill do <tt>fill</tt> task.
	 * @param doDrain do <tt>drain</tt> task.
	 * @return the task, return <code>true</code> if stacks are changed.
	 */
	public static Task.TaskBTB taskFillOrDrain(ItemStack[] stacks1, int index1, int index2, FluidStack[] stacks2, int index3, int maxAmount, boolean doFill, boolean doDrain, boolean fully)
	{
		assert index1 != index2;
		return n -> {
			ItemStack stack = stacks1[index1];
			if (stack == null)
			{
				return false;
			}
			else
			{
				stack = IS.copy(stack, 1);
				if (stack.getItem() instanceof IItemFluidContainer) //The fluid capability can not predicate var amount fluid container and fixed amount fluid container.
				{
					IItemFluidContainer container = (IItemFluidContainer) stack.getItem();
					if (container.isV1())
					{
						IItemFluidContainerV1 containerV1 = container.castV1();
						if (doFill && containerV1.canDrain(stack))
						{
							if (stacks2[index3] == null)
							{
								FluidStack s1 = containerV1.drain(stack, maxAmount, true);
								if (s1 != null && (!fully || !containerV1.hasFluid(stack)))
								{
									if (taskInsertAll(stacks1, index2, stack, true).invoke(TaskList.taskListFromTrue()))
									{
										stacks2[index3] = s1;
										decrSlotSizeExact(stacks1, index1, 1);
										return true;
									}
								}
							}
							else if (maxAmount > stacks2[index3].amount)
							{
								FluidStack s1 = containerV1.drain(stack, FS.copy(stacks2[index3], maxAmount - stacks2[index3].amount), true);
								if (s1 != null && (!fully || !containerV1.hasFluid(stack)))
								{
									if (taskInsertAll(stacks1, index2, stack, true).invoke(TaskList.taskListFromTrue()))
									{
										stacks2[index3].amount += s1.amount;
										decrSlotSizeExact(stacks1, index1, 1);
										return true;
									}
								}
							}
						}
						if (doDrain && stacks2[index3] != null && container.canFill(stack, stacks2[index3]))
						{
							int amount = containerV1.fill(stack, stacks2[index3], true);
							if (amount > 0 && (!fully || !containerV1.isFull(stack)))
							{
								if (taskInsertAll(stacks1, index2, stack, true).invoke(TaskList.taskListFromTrue()))
								{
									decrSlotSizeExact(stacks2, index3, amount);
									decrSlotSizeExact(stacks1, index1, 1);
									return true;
								}
							}
						}
					}
					else//if (container.isV2())
					{
						IItemFluidContainerV2 containerV2 = container.castV2();
						if (doFill && containerV2.hasFluid(stack))
						{
							FluidStack s1 = containerV2.getContain(stack);
							if (stacks2[index3] == null)
							{
								if (!fully || s1.amount <= maxAmount)
								{
									stack = containerV2.drain(stack, true);
									if (taskInsertAll(stacks1, index2, stack, true).invoke(TaskList.taskListFromTrue()))
									{
										decrSlotSizeExact(stacks1, index1, 1);
										stacks2[index3] = s1.copy();
										return true;
									}
								}
							}
							else if (maxAmount > stacks2[index3].amount)
							{
								if (!fully || s1.amount <= maxAmount - stacks2[index3].amount)
								{
									stack = containerV2.drain(stack, true);
									if (taskInsertAll(stacks1, index2, stack, true).invoke(TaskList.taskListFromTrue()))
									{
										decrSlotSizeExact(stacks1, index1, 1);
										stacks2[index3].amount += s1.amount;
										return true;
									}
								}
							}
						}
						int capacity;
						if (doDrain && stacks2[index3] != null && containerV2.canFill(stack, stacks2[index3])
								&& (capacity = containerV2.capacity(stack)) <= stacks2[index3].amount)
						{
							stack = containerV2.fill(stack, stacks2[index3], true);
							if (taskInsertAll(stacks1, index2, stack, true).invoke(TaskList.taskListFromTrue()))
							{
								decrSlotSizeExact(stacks1, index1, 1);
								decrSlotSizeExact(stacks2, index3, capacity);
								return true;
							}
						}
					}
					return false;
				}
				if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
				{
					IFluidHandler handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
					if (doFill)
					{
						if (stacks2[index3] == null)
						{
							FluidStack s1 = handler.drain(maxAmount, true);
							if (s1 != null && (!fully || !hasFluidContain(handler)))
							{
								if (taskInsertAll(stacks1, index2, stack, true).invoke(TaskList.taskListFromTrue()))
								{
									stacks2[index3] = s1;
									decrSlotSizeExact(stacks1, index1, 1);
									return true;
								}
							}
						}
						else if (maxAmount > stacks2[index3].amount)
						{
							FluidStack s1 = handler.drain(FS.copy(stacks2[index3], maxAmount - stacks2[index3].amount), true);
							if (s1 != null && (!fully || !hasFluidContain(handler)))
							{
								if (taskInsertAll(stacks1, index2, stack, true).invoke(TaskList.taskListFromTrue()))
								{
									stacks2[index3].amount += s1.amount;
									decrSlotSizeExact(stacks1, index1, 1);
									return true;
								}
							}
						}
					}
					if (doDrain && stacks2[index3] != null)
					{
						int amount = handler.fill(stacks2[index3], true);
						if (amount > 0 && (!fully || !hasFullFilled(handler)))
						{
							if (taskInsertAll(stacks1, index2, stack, true).invoke(TaskList.taskListFromTrue()))
							{
								decrSlotSizeExact(stacks2, index3, amount);
								decrSlotSizeExact(stacks1, index1, 1);
								return true;
							}
						}
					}
					return false;
				}
				return false;
			}
		};
	}
	
	public static void damageTool(ItemStack[] stacks, int index, float amount, @Nullable EntityPlayer player, EnumToolType toolType)
	{
		IS.damageTool(stacks[index], amount, player, toolType);
		stacks[index] = IS.validate(stacks[index]);
	}
	
	public static boolean insertSlotShapelessStacks(ItemStack[] stacks, int from, int to, ItemStack[] inserts, boolean process)
	{
		return taskMultiInsertShapeless(stacks, from, to, inserts, process).invoke();
	}
	
	public static boolean decrSlotStack(ItemStack[] stacks, int index, int size, boolean process)
	{
		if (stacks[index] != null && stacks[index].stackSize >= size)
		{
			if (process)
			{
				decrSlotSizeExact(stacks, index, size);
			}
			return true;
		}
		return false;
	}
	
	public static ItemStack decrSlotSize(ItemStack[] stacks, int index, int size, boolean process)
	{
		if (stacks[index] != null)
		{
			ItemStack stack = IS.copy(stacks[index], Math.min(size, stacks[index].stackSize));
			if (process)
			{
				decrSlotSizeExact(stacks, index, stack.stackSize);
			}
			return stack;
		}
		return null;
	}
	
	public static void decrSlotStack_(ItemStack[] stacks, int index, int size)
	{
		decrSlotSizeExact(stacks, index, size);
	}
	
	public static boolean decrSlotStack(ItemStack[] stacks, int index, ItemStack stack, boolean process)
	{
		if (stack == null || stack.stackSize <= 0)
		{
			return true;
		}
		if (stacks[index] != null && stacks[index].stackSize >= stack.stackSize)
		{
			if (process)
			{
				decrSlotSizeExact(stacks, index, stack.stackSize);
			}
			return true;
		}
		return false;
	}
	
	public static void decrSlotStack_(ItemStack[] stacks, int index, ItemStack stack)
	{
		if (stack != null && stack.stackSize >= 0)
		{
			decrSlotSizeExact(stacks, index, stack.stackSize);
		}
	}
	
	public static boolean decrSlotStack(FluidStack[] stacks, int index, int size, boolean process)
	{
		if (stacks[index] != null && stacks[index].amount >= size)
		{
			if (process)
			{
				decrSlotSizeExact(stacks, index, size);
			}
			return true;
		}
		return false;
	}
	
	public static void decrSlotStack_(FluidStack[] stacks, int index, int size)
	{
		decrSlotSizeExact(stacks, index, size);
	}
	
	public static boolean decrSlotStack(FluidStack[] stacks, int index, FluidStack stack, boolean process)
	{
		if (stack == null || stack.amount <= 0)
		{
			return true;
		}
		if (stacks[index] != null && stacks[index].amount >= stack.amount)
		{
			if (process)
			{
				decrSlotSizeExact(stacks, index, stack.amount);
			}
			return true;
		}
		return false;
	}
	
	public static void decrSlotStack_(FluidStack[] stacks, int index, FluidStack stack)
	{
		if (stack != null && stack.amount >= 0)
		{
			decrSlotSizeExact(stacks, index, stack.amount);
		}
	}
	
	private static void decrSlotSizeExact(ItemStack[] stacks, int index, int size)
	{
		if (stacks[index].stackSize > size)
		{
			stacks[index].stackSize -= size;
		}
		else
		{
			stacks[index] = null;
		}
	}
	
	private static void decrSlotSizeExact(FluidStack[] stacks, int index, int size)
	{
		if (stacks[index].amount > size)
		{
			stacks[index].amount -= size;
		}
		else
		{
			stacks[index] = null;
		}
	}
	
	private static boolean hasFluidContain(IFluidHandler handler)
	{
		for (IFluidTankProperties property : handler.getTankProperties())
		{
			if (property.canDrain() && property.getContents() != null)
			{
				return true;
			}
		}
		return false;
	}
	
	private static boolean hasFullFilled(IFluidHandler handler)
	{
		for (IFluidTankProperties property : handler.getTankProperties())
		{
			if (property.canFill() && property.getCapacity() > FS.amount(property.getContents()))
			{
				return true;
			}
		}
		return false;
	}
	
	private static <T> T[] copyOfRange(final IStackHandler<T> handler, T[] stacks, int from, int to)
	{
		T[] newArray = (T[]) Array.newInstance(stacks.getClass().getComponentType(), to - from);
		for (int i = 0; i < newArray.length; newArray[i] = handler.copy(stacks[from + i]), ++i);
		return newArray;
	}
}
