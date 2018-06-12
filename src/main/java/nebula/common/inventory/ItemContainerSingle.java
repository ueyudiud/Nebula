/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import net.minecraft.item.ItemStack;

/**
 * @author ueyudiud
 */
public class ItemContainerSingle extends ItemContainerSimple
{
	private ItemStack stack;
	
	public ItemContainerSingle(int limit)
	{
		super(limit);
	}
	
	@Override
	protected ItemStack get()
	{
		if (this.stack != null && this.stack.stackSize <= 0)
		{
			this.stack = null;
		}
		return this.stack;
	}
	
	@Override
	protected void add(int size)
	{
		this.stack.stackSize += size;
	}
	
	@Override
	protected void set(ItemStack stack)
	{
		this.stack = ItemStack.copyItemStack(stack);
	}
	
	@Override
	public void clear()
	{
		this.stack = null;
	}
}
