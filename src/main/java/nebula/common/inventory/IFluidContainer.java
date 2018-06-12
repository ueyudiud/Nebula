/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import nebula.common.inventory.task.Task;
import nebula.common.nbt.INBTSelfCompoundReaderAndWriter;
import nebula.common.stack.Subfluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public interface IFluidContainer extends INBTSelfCompoundReaderAndWriter, IContainer<FluidStack>
{
	/**
	 * Return <code>true</code> if fluid is available to insert into this container.
	 * @param fluid the checked sub fluid.
	 * @return
	 */
	boolean isAvailable(Subfluid fluid);
	
	/**
	 * Return the total capacity of this container.
	 * @return the capacity.
	 */
	int getCapacity();
	
	/**
	 * Return stack amount contained in this container.
	 * @return the stack amount.
	 */
	int getStackAmountInContainer();
	
	/**
	 * Return max stack amount sill can be insert to this container.
	 * @return the max amount.
	 */
	int getRemainCapacityInContainer();
	
	/**
	 * Create a simulated container, used for multiple insert check.
	 * @return the simulated container.
	 * @throws UnsupportedOperationException when simulated item container is not allowed.
	 */
	IFluidContainer simulated();
	
	/**
	 * Return all stacks in the container.<p>
	 * This collection is immutable that if you want take
	 * operation on any stack in the container, use method
	 * in this interface instead.
	 * @return the collection.
	 */
	@Nonnull Collection<FluidStack> stacks();
	
	/**
	 * Take <tt>click</tt> operation.<p>
	 * Called as player operation simulated.<p>
	 * The available modifiers are {@link #PROCESS}.
	 * @param stack the click item.
	 * @param modifier the operation request.
	 */
	/* Should I take this method in fluid slot? */
	default ActionResult<ItemStack> clickContainer(@Nullable ItemStack stack, int modifier) { return new ActionResult<>(EnumActionResult.PASS, stack); }
	
	/**
	 * Take <tt>increase</tt> operation.<p>
	 * Increase amount of FluidStack contained specific fluid from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #FULLY}, {@link #SKIP_AC}.
	 * @param fluid the specific fluid.
	 * @param amount the increase amount.
	 * @param modifier the operation request.
	 * @return the increased amount.
	 */
	int incrStack(Subfluid fluid, int amount, int modifier);
	
	/**
	 * Take <tt>decrease</tt> operation.<p>
	 * Decrease amount of ItemStack contained specific fluid of from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #FULLY}.
	 * @param fluid the specific fluid.
	 * @param size the decrease amount.
	 * @param modifier the operation request.
	 * @return the decreased amount.
	 */
	int decrStack(Subfluid fluid, int amount, int modifier);
	
	/**
	 * Create <tt>increase</tt> operation task.<p>
	 * Increase FluidStack {@link #FULLY} into container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #SKIP_REFRESH}.
	 * @param fluid the fluid to take increasing match.
	 * @param amount the amount to increase.
	 * @param modifier the operation request.
	 * @return the task.
	 * @see #incrStack(Subfluid, int, int)
	 */
	Task.TaskBTB taskIncr(Subfluid fluid, int amount, int modifier);
	
	/**
	 * Create <tt>decrease</tt> operation task.<p>
	 * Decrease FluidStack {@link #FULLY} from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #SKIP_REFRESH}.
	 * @param fluid the fluid to take decreasing match.
	 * @param amount the amount to decrease
	 * @param modifier the operation request.
	 * @return the task.
	 * @see #decrStack(Subfluid, int, int)
	 */
	Task.TaskBTB taskDecr(Subfluid fluid, int amount, int modifier);
}
