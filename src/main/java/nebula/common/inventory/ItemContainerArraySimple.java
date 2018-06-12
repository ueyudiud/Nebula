/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import nebula.common.stack.IS;
import net.minecraft.item.ItemStack;

/**
 * @author ueyudiud
 */
public class ItemContainerArraySimple extends ItemContainerSimple
{
	final ItemStack[] stacks;
	private final int id;
	
	public static ItemContainerArraySimple[] create(int size, int limit)
	{
		return create(new ItemStack[size], limit);
	}
	
	public static ItemContainerArraySimple[] create(ItemStack[] stacks, int limit)
	{
		ItemContainerArraySimple[] result = new ItemContainerArraySimple[stacks.length];
		for (int i = 0; i < stacks.length; ++i)
		{
			result[i] = new ItemContainerArraySimple(stacks, i, limit);
		}
		return result;
	}
	
	public ItemContainerArraySimple(ItemStack[] stacks, int id, int limit)
	{
		super(limit);
		this.stacks = stacks;
		this.id = id;
	}
	
	@Override
	protected ItemStack get()
	{
		if (this.stacks[this.id] != null && this.stacks[this.id].stackSize <= 0)
		{
			this.stacks[this.id] = null;
		}
		return this.stacks[this.id];
	}
	
	@Override
	protected void add(int size)
	{
		this.stacks[this.id].stackSize += size;
	}
	
	@Override
	protected void set(ItemStack stack)
	{
		this.stacks[this.id] = IS.validate(stack);
	}
}
