/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

import nebula.common.inventory.IItemContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @author ueyudiud
 */
public class ItemSlotOutput extends ItemSlot
{
	public ItemSlotOutput(IItemContainer container, IInventory inventory, int index, int xPosition, int yPosition)
	{
		super(container, inventory, index, xPosition, yPosition);
	}
	
	public ItemSlotOutput(IItemContainer container, int xPosition, int yPosition)
	{
		super(container, xPosition, yPosition);
	}
	
	@Override
	public boolean canPutStack(EntityPlayer player)
	{
		return player.capabilities.isCreativeMode;//Let player can edit GUI directly in creative mode, used for RPG map creating, etc.
	}
	
	@Override
	public boolean isItemExpected(ItemStack stack)
	{
		return false;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}
	
	@Override
	public void onSlotChanged()
	{
		super.onSlotChanged();
	}
}
