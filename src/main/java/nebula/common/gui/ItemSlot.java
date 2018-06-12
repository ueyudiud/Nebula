/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nebula.base.function.F;
import nebula.common.inventory.IItemContainer;
import nebula.common.inventory.ItemContainerInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author ueyudiud
 */
public class ItemSlot extends Slot implements ISlot<ItemStack>
{
	static final IInventory INVENTORY = new InventoryBasic("", false, 0);
	
	protected IItemContainer container;
	@Nonnull
	protected Predicate<ItemStack> predicate = F.P_T;
	
	public ItemSlot(IItemContainer container,                                  int xPosition, int yPosition) { this(container, INVENTORY, 0, xPosition, yPosition); }
	public ItemSlot(                          IInventory inventory, int index, int xPosition, int yPosition) { this(new ItemContainerInventory(inventory, index), inventory, index, xPosition, yPosition); }
	public ItemSlot(IItemContainer container, IInventory inventory, int index, int xPosition, int yPosition)
	{
		super(inventory, index, xPosition, yPosition);
		this.container = container;
	}
	
	public ItemSlot setPredicate(@Nonnull Predicate<ItemStack> predicate)
	{
		this.predicate = Objects.requireNonNull(predicate);
		return this;
	}
	
	public boolean canOperateStack(EntityPlayer player)
	{
		return true;
	}
	
	public boolean canPutStack(EntityPlayer player)
	{
		return canOperateStack(player);
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return canOperateStack(player);
	}
	
	@Override
	public ItemStack decrStackSize(int amount)
	{
		return this.container.extractStack(amount, IItemContainer.PROCESS);
	}
	
	@Override
	@Deprecated
	public boolean getHasStack()
	{
		return hasStack();
	}
	
	@Override
	public ItemStack getStack()
	{
		return this.container.getStackInContainer();
	}
	
	public boolean hasStack()
	{
		return this.container.hasStackInContainer();
	}
	
	public void setStack(ItemStack stack)
	{
		this.container.setStackInContainer(stack);
	}
	
	@Override
	public int getItemStackLimit(@Nullable ItemStack stack)
	{
		return stack == null || isItemValid(stack) ? getSlotStackLimit() : 0;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return this.container.isAvailable(stack);
	}
	
	/**
	 * Used for quick transfer.
	 * @param stack the stack.
	 * @return <code>true</code> if stack is expected for this slot.
	 */
	public boolean isItemExpected(ItemStack stack)
	{
		return this.predicate.test(stack);
	}
	
	@Override
	public boolean isSameInventory(Slot other)
	{
		return other instanceof ItemSlot && this.container == ((ItemSlot) other).container;
	}
	
	@Override
	public void onSlotChanged()
	{
		
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
		this.container.setStackInContainer(stack);
	}
}
