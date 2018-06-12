/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import nebula.common.nbt.INBTCompoundReaderAndWriter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public interface IStackHandler<S> extends INBTCompoundReaderAndWriter<S>
{
	IStackHandler<ItemStack> ITEMSTACK_HANDLER = new ItemStackHandler();
	IStackHandler<FluidStack> FLUIDSTACK_HANDLER = new FluidStackHandler();
	
	@Nullable S copy(@Nullable S stack);
	
	S copy(S stack, int newSize);
	
	int size(S stack);
	
	int size(S stack, int newSize);
	
	int add(S stack, int incrSize);
	
	int sub(S stack, int decrSize);
	
	boolean isSimilar(S s1, S s2);
	
	boolean isEqual(S s1, S s2);
	
	boolean contains(@Nullable S source, @Nullable S target);
	
	@Nullable S validate(@Nullable S stack);
}
