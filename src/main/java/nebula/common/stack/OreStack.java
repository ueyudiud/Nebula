/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.stack;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreStack implements AbstractStack
{
	public static AbstractStack sizeOf(OreStack stack, int size)
	{
		return size <= 0 ? null : new OreStack(stack.oreName, size);
	}
	
	public final String			oreName;
	public final int			size;
	private List<ItemStack>		ore;
	private List<ItemStack>		list;
	
	public OreStack(String ore)
	{
		this(ore, 1);
	}
	
	public OreStack(String ore, int size)
	{
		this.oreName = ore;
		this.ore = OreDictionary.getOres(ore);
		this.size = size;
	}
	
	@Override
	public boolean similar(ItemStack stack)
	{
		return stack != null && OreDictionary.containsMatch(false, this.ore, stack);
	}
	
	@Override
	public boolean contain(ItemStack stack)
	{
		return similar(stack) ? stack.stackSize >= this.size : false;
	}
	
	@Override
	public int size(ItemStack stack)
	{
		return this.size;
	}
	
	@Override
	public AbstractStack split(ItemStack stack)
	{
		return sizeOf(this, this.size - stack.stackSize);
	}
	
	@Override
	public AbstractStack copyWithSize(int size)
	{
		return sizeOf(this, size);
	}
	
	@Override
	public ItemStack instance()
	{
		if (!display().isEmpty())
		{
			return ItemStack.copyItemStack(this.list.get(0));
		}
		return null;
	}
	
	@Override
	public List<ItemStack> display()
	{
		if (this.list == null)
		{
			this.list = Lists.<ItemStack, ItemStack> transform(this.ore, s -> IS.copy(s, this.size));
		}
		return this.list;
	}
	
	@Override
	public boolean valid()
	{
		return !this.ore.isEmpty();
	}
	
	@Override
	public String toString()
	{
		return "[ore:" + this.oreName + "]x" + this.size;
	}
}
