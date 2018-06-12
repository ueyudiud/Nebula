/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author ueyudiud
 */
public class InventorySimple implements IInventory
{
	private String inventoryTitle;
	private final int slotsCount;
	public final IItemContainers containers;
	protected final ItemStack[] stacks;
	private boolean hasCustomName;
	
	public InventorySimple(String title, boolean customName, int slotCount)
	{
		this.inventoryTitle = title;
		this.hasCustomName = customName;
		this.slotsCount = slotCount;
		this.stacks = new ItemStack[slotCount];
		this.containers = new ItemContainers<>(ItemContainerArraySimple.create(this.stacks, 64));
	}
	
	@Override
	public String getName()
	{
		return this.inventoryTitle;
	}
	
	@Override
	public boolean hasCustomName()
	{
		return this.hasCustomName;
	}
	
	@Override
	public ITextComponent getDisplayName()
	{
		return this.hasCustomName ? new TextComponentString(this.inventoryTitle) : new TextComponentTranslation(this.inventoryTitle);
	}
	
	@Override
	public int getSizeInventory()
	{
		return this.slotsCount;
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return this.stacks[index];
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return InventoryHelper.decrSlotSize(this.stacks, index, count, true);
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		ItemStack stack = this.stacks[index];
		this.stacks[index] = null;
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.stacks[index] = ItemStack.copyItemStack(stack);
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public void markDirty()
	{
		
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return true;
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
		return true;
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
		Arrays.fill(this.stacks, null);
	}
}
