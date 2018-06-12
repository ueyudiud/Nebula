/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import nebula.common.stack.IS;
import net.minecraft.item.ItemStack;

/**
 * @author ueyudiud
 */
public interface IItemContainerSingle extends IItemContainer, IContainerSingle<ItemStack>
{
	@Override
	ItemStack getStackInContainer();
	
	@Override
	default int getStackSizeInContainer() { return IS.size(getStackInContainer()); }
	
	@Override
	default Collection<ItemStack> stacks() { return getStackInContainer() == null ? ImmutableList.of() : ImmutableList.of(getStackInContainer()); }
}
