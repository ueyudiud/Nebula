/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import nebula.common.stack.FS;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
public interface IFluidContainerSingle extends IFluidContainer, IContainerSingle<FluidStack>
{
	@Override
	default boolean hasStackInContainer() { return getStackInContainer() != null; }
	
	@Override
	default FluidStack getStackInContainer() { return FS.copy(getStackInContainer()); }
	
	@Override
	default int getStackAmountInContainer() { return FS.amount(getStackInContainer()); }
	
	@Override
	default Collection<FluidStack> stacks() { return getStackInContainer() == null ? ImmutableList.of() : ImmutableList.of(getStackInContainer()); }
}
