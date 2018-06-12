/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.tile;

import static nebula.common.inventory.IContainer.FULLY;
import static nebula.common.inventory.IContainer.PROCESS;

import java.util.Map.Entry;

import nebula.V;
import nebula.base.MutableIntEntry;
import nebula.common.fluid.container.FluidContainerHandler;
import nebula.common.inventory.IFluidContainer;
import nebula.common.inventory.IFluidContainers;
import nebula.common.inventory.IItemContainer;
import nebula.common.inventory.IItemContainers;
import nebula.common.inventory.task.Task;
import nebula.common.inventory.task.Task.TaskBTB;
import nebula.common.inventory.task.TaskBuilder;
import nebula.common.tile.ITilePropertiesAndBehavior.ITB_BreakBlock;
import nebula.common.util.W;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
public class TE05InventorySimple extends TE04Synchronization implements IInventory, ITB_BreakBlock
{
	protected IItemContainers items = IItemContainers.EMPTY;
	protected IFluidContainers fluids = IFluidContainers.EMPTY;
	
	protected TE05InventorySimple()
	{
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		this.items.writeTo(nbt, "items");
		this.fluids.writeTo(nbt, "fluids");
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.items.readFrom(nbt, "items");
		this.fluids.readFrom(nbt, "fluids");
	}
	
	@Override
	public void onBlockBreak(IBlockState state)
	{
		super.onBlockBreak(state);
		for (IItemContainer container : this.items.getContainers())
		{
			W.spawnDropsInWorld(this, container.stacks());
			container.clear();
		}
		//How about fluid?
	}
	
	protected Task.TaskBTB provideFillOrDrainTask(int itemIn, int itemOut, int fluidTarget, int maxIO, boolean doFill, boolean doDrain, boolean onlyFully)
	{
		assert itemIn != itemOut;
		IItemContainer in = this.items.getContainer(itemIn);
		IItemContainer out = this.items.getContainer(itemOut);
		IFluidContainer tar = this.fluids.getContainer(fluidTarget);
		return n -> {
			if (in.hasStackInContainer())
			{
				ItemStack stack = in.getStackInContainer();
				if (doDrain)
				{
					Entry<ItemStack, FluidStack> entry = FluidContainerHandler.drainContainer(stack, maxIO);
					if (entry != null)
					{
						TaskBTB task = TaskBuilder.builder()
								.add(in.taskDecr(stack, PROCESS))
								.add(tar.taskDecr(entry.getValue(), onlyFully ? PROCESS | FULLY : PROCESS))
								.add(out.taskIncr(entry.getKey(), PROCESS))
								.asTask();
						if (task.invoke(n))
						{
							return true;
						}
					}
				}
				if (doFill)
				{
					FluidStack stack1 = tar.extractStack(maxIO, 0);
					if (stack1 != null)
					{
						MutableIntEntry<ItemStack> entry = FluidContainerHandler.fillContainer(stack, stack1);
						if (entry != null)
						{
							Task.TaskBTB task = TaskBuilder.builder()
									.add(in.taskDecr(stack, PROCESS))
									.add(tar.taskDecr(entry.getValue(), onlyFully ? PROCESS : PROCESS))
									.add(out.taskIncr(entry.getKey(), PROCESS))
									.asTask();
							if (task.invoke(n))
							{
								return true;
							}
						}
					}
				}
			}
			return false;
		};
	}
	
	public final IItemContainer getItemContainer(int index)
	{
		return this.items.getContainer(index);
	}
	
	public final IFluidContainer getFluidContainer(int index)
	{
		return this.fluids.getContainer(index);
	}
	
	@Override
	public int getSizeInventory()
	{
		return this.items.getContainerSize();
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return this.items.getStackInContainer(index);
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return this.items.extractStack(index, count);
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return this.items.extractStack(index, null);
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.items.setStackInContainer(index, stack);
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return V.GENERAL_MAX_STACK_SIZE;
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return !isInvalid() && getDistanceFrom(player) <= 64;
	}
	
	@Override
	public void openInventory(EntityPlayer player)
	{
		
	}
	
	@Override
	public void closeInventory(EntityPlayer player)
	{
		
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return this.items.getContainer(index).isAvailable(stack);
	}
	
	@Override
	public int getField(int id)
	{
		return 0;
	}
	
	@Override
	public void setField(int id, int value)
	{
		
	}
	
	@Override
	public int getFieldCount()
	{
		return 0;
	}
	
	@Override
	public void clear()
	{
		this.items.clear();
	}
}
