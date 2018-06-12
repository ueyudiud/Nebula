/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.stack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public class SubitemStack
{
	private Subitem item;
	public int size;
	
	public SubitemStack(Subitem item)
	{
		this(item, 1);
	}
	public SubitemStack(Subitem item, int size)
	{
		this.item = item;
		this.size = size;
	}
	
	public Item getItem()
	{
		return this.item.getItem();
	}
	
	public Subitem getSubitem()
	{
		return this.item;
	}
	
	public ItemStack toItemStack()
	{
		return this.item.stack(this.size);
	}
	
	/**
	 * Return a copy of Subitem stack with specific size,
	 * <code>null</code> if size is non-positive number.
	 * @param size the stack size.
	 * @return the new stack.
	 */
	@Nullable
	public SubitemStack of(int size)
	{
		return size > 0 ? new SubitemStack(this.item, size) : null;
	}
}
