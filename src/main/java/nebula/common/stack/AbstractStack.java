/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.stack;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

import com.google.common.collect.ImmutableList;

import nebula.base.function.Judgable;
import net.minecraft.item.ItemStack;

/**
 * Abstract stack type. Which is used in recipes, container checks, etc.
 * 
 * @author ueyudiud
 */
@ParametersAreNullableByDefault
public interface AbstractStack extends nebula.base.function.Judgable<ItemStack>
{
	/**
	 * Check is this stack similar to target stack. This method doesn't check
	 * size.
	 * 
	 * @param stack
	 * @return
	 */
	boolean similar(ItemStack stack);
	
	/**
	 * Check this stack is fully to input with target stack.
	 * 
	 * @param stack
	 * @return
	 */
	default boolean contain(ItemStack stack)
	{
		return similar(stack) && stack.stackSize >= size(stack);
	}
	
	int size(ItemStack stack);
	
	@Deprecated
	default AbstractStack split(@Nonnull ItemStack stack)
	{
		return copyWithSize(size(null) - stack.stackSize);
	}
	
	@Deprecated
	default AbstractStack copyWithSize(int size)
	{
		throw new UnsupportedOperationException();
	}
	
	// INFO : Please at least override instance or display one, or
	// the stack will be over flow!
	/**
	 * Create a instance stack, which should be a copy of source
	 * that any modification of instance will not affect this stack.
	 * 
	 * @return the instance {@link ItemStack} of this stack.
	 */
	default ItemStack instance()
	{
		return display().get(0);
	}
	
	/**
	 * Display most of stack matched, for most allowed matched in
	 * consideration should be contained, but it needn't contain
	 * all if matched stack are not enumable.<p>
	 * The result list should be unmodifiable.
	 * 
	 * @return the
	 */
	default List<ItemStack> display()
	{
		return ImmutableList.of(instance());
	}
	
	/**
	 * If this stack is valid. (Invalid stack will cause whole recipe is valid).
	 * Such like use empty ore dictionary list, no container item, etc.
	 * 
	 * @return Is this stack valid.
	 */
	default boolean valid()
	{
		return true;
	}
	
	@Override
	default boolean test(ItemStack stack)
	{
		return similar(stack);
	}
	
	default Judgable<ItemStack> containCheck()
	{
		return this::contain;
	}
	
	default boolean extract(@Nonnull ItemStack[] stacks, int index)
	{
		if (contain(stacks[index]))
		{
			extract_(stacks, index);
			return true;
		}
		return false;
	}
	
	default ItemStack extract(@Nonnull ItemStack stack)
	{
		int size = stack.stackSize - size(stack);
		return size != 0 ? IS.copy(stack, size) : null;
	}
	
	default void extract_(@Nonnull ItemStack[] stacks, int index)
	{
		int size = stacks[index].stackSize - size(stacks[index]);
		if (size == 0)
		{
			stacks[index] = null;
		}
		else
		{
			stacks[index].stackSize = size;
		}
	}
}
