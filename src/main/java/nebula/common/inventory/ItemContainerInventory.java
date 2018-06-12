/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import javax.annotation.Nonnull;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @author ueyudiud
 */
public class ItemContainerInventory extends ItemContainerSimple
{
	protected final IInventory inventory;
	protected final int index;
	
	public ItemContainerInventory(@Nonnull IInventory inventory, int index)
	{
		super(inventory.getInventoryStackLimit());
		this.inventory = inventory;
		this.index = index;
	}
	
	@Override
	protected ItemStack get()
	{
		return this.inventory.getStackInSlot(this.index);
	}
	
	@Override
	protected void add(int size)
	{
		this.inventory.getStackInSlot(this.index).stackSize += size;
	}
	
	@Override
	protected void set(ItemStack stack)
	{
		this.inventory.setInventorySlotContents(this.index, stack);
	}
	
	@Override
	protected void onContainerChanged()
	{
		super.onContainerChanged();
		this.inventory.markDirty();
	}
	
	@Override
	public ItemStack extractStack(int size, int modifier)
	{
		if (modifier == PROCESS)
		{
			return this.inventory.decrStackSize(this.index, size);
		}
		return super.extractStack(size, modifier);
	}
}
