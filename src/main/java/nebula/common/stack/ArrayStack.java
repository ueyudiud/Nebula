/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.stack;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import nebula.base.function.F;
import nebula.common.util.L;
import net.minecraft.item.ItemStack;

public class ArrayStack implements AbstractStack
{
	public static ArrayStack sizeOf(ArrayStack stack, int size)
	{
		return new ArrayStack(size, stack.array);
	}
	
	public int					size;
	public final List<Subitem>	array;
	
	public ArrayStack(          Collection<Subitem> collection) { this(1, collection); }
	public ArrayStack(int size, Collection<Subitem> collection)
	{
		this.array = ImmutableList.copyOf(collection);
		this.size = size;
	}
	
	public ArrayStack(          Subitem...stacks) { this(1, stacks); }
	public ArrayStack(int size, Subitem...stacks)
	{
		this.array = ImmutableList.copyOf(stacks);
		this.size = size;
	}
	
	@Override
	public boolean similar(ItemStack stack)
	{
		return stack != null && L.contain(this.array, F.const2p(Subitem::match, stack));
	}
	
	@Override
	public boolean contain(ItemStack stack)
	{
		return similar(stack) && stack.stackSize >= this.size;
	}
	
	@Override
	public int size(ItemStack stack)
	{
		return this.size;
	}
	
	@Override
	public AbstractStack split(ItemStack stack)
	{
		return this.size >= stack.stackSize ? new ArrayStack(this.size - stack.stackSize, this.array) : null;
	}
	
	@Override
	public AbstractStack copyWithSize(int size)
	{
		return new ArrayStack(size, this.array);
	}
	
	@Override
	public ItemStack instance()
	{
		return this.array.get(0).stack(this.size);
	}
	
	@Override
	public List<ItemStack> display()
	{
		return IS.getStackOf(this.array, this.size);
	}
	
	@Override
	public boolean valid()
	{
		return !this.array.isEmpty();
	}
}
