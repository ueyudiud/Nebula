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
import nebula.common.stack.AbstractStack;
import nebula.common.stack.Subitem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public interface IItemContainer extends INBTSelfCompoundReaderAndWriter, IContainer<ItemStack>
{
	/**
	 * Get stack in container.
	 * @return the stack in container.
	 */
	@Nullable
	default ItemStack getStackInContainer() { return extractStack((ItemStack) null, 0); }
	
	/**
	 * Return <code>true</code> if item is available to insert into this container.
	 * @param item the checked sub item.
	 * @return
	 */
	boolean isAvailable(Subitem item);
	
	/**
	 * Return stack size contained in this container.
	 * @return the stack size.
	 */
	int getStackSizeInContainer();
	
	/**
	 * Return max stack size sill can be insert to this container.
	 * @return the max size.
	 */
	int getRemainCapacityInContainer();
	
	/**
	 * Create a simulated container, used for multiple insert check.
	 * @return the simulated container.
	 * @throws UnsupportedOperationException when simulated item container is not allowed.
	 */
	IItemContainer simulated();
	
	/**
	 * Return all stacks in the container.<p>
	 * This collection is immutable that if you want take
	 * operation on any stack in the container, use method
	 * in this interface instead.
	 * @return the collection.
	 */
	@Nonnull Collection<ItemStack> stacks();
	
	/**
	 * Take <tt>click</tt> operation.<p>
	 * Called as player operation simulated.<p>
	 * The available modifiers are {@link #PROCESS}.
	 * @param stack the click item.
	 * @param modifier the operation request.
	 */
	default ActionResult<ItemStack> clickContainer(@Nullable ItemStack stack, int modifier) { return new ActionResult<>(EnumActionResult.PASS, stack); }
	
	/**
	 * Take <tt>increase</tt> operation.<p>
	 * Increase size of ItemStack contained specific item from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #FULLY}, {@link #SKIP_AC}.
	 * @param item the specific item.
	 * @param size the increase size.
	 * @param modifier the operation request.
	 * @return the increased size.
	 */
	int incrStack(Subitem item, int size, int modifier);
	
	/**
	 * Take <tt>decrease</tt> operation.<p>
	 * Decrease size of ItemStack contained specific item of from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #FULLY}.
	 * @param item the specific item.
	 * @param size the decrease size.
	 * @param modifier the operation request.
	 * @return the decreased size.
	 */
	int decrStack(Subitem item, int size, int modifier);
	
	/**
	 * Take <tt>insert</tt> operation.<p>
	 * Extract ItemStack from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #SKIP_AC}.
	 * @param stack the item stack to insert.
	 * @param modifier the operation request.
	 * @return the remained stack.
	 */
	@Nullable
	boolean insertStack(AbstractStack stack, int modifier);
	
	/**
	 * Take <tt>extract</tt> operation.<p>
	 * Extract ItemStack from container.<p>
	 * The available modifiers are {@link #PROCESS}.
	 * @param stack the item stack to extract.
	 * @param modifier the operation request.
	 * @return the extracted stack.
	 */
	@Nullable
	boolean extractStack(AbstractStack stack, int modifier);
	
	/**
	 * Create <tt>increase</tt> operation task.<p>
	 * Increase ItemStack {@link #FULLY} into container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #SKIP_REFRESH}.
	 * @param item the item to take increasing match.
	 * @param size the size to increase.
	 * @param modifier the operation request.
	 * @return the task.
	 * @see #incrStack(Subitem, int, int)
	 */
	Task.TaskBTB taskIncr(Subitem item, int size, int modifier);
	
	/**
	 * Create <tt>increase</tt> operation task.<p>
	 * Increase ItemStack {@link #FULLY} into container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #SKIP_REFRESH}.
	 * @param stack the item stack to increase.
	 * @param modifier the operation request.
	 * @return the task.
	 * @see #insertStack(AbstractStack, int)
	 */
	Task.TaskBTB taskIncr(AbstractStack stack, int modifier);
	
	/**
	 * Create <tt>decrease</tt> operation task.<p>
	 * Decrease ItemStack {@link #FULLY} from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #SKIP_REFRESH}.
	 * @param item the item to take decreasing match.
	 * @param size the size to decrease
	 * @param modifier the operation request.
	 * @return the task.
	 * @see #decrStack(Subitem, int, int)
	 */
	Task.TaskBTB taskDecr(Subitem item, int size, int modifier);
	
	/**
	 * Create <tt>decrease</tt> operation task.<p>
	 * Decrease ItemStack {@link #FULLY} from container.<p>
	 * The available modifiers are {@link #PROCESS}, {@link #SKIP_REFRESH}.
	 * @param stack the item stack to decrease.
	 * @param modifier the operation request.
	 * @return the task.
	 * @see #extractStack(AbstractStack, int)
	 */
	Task.TaskBTB taskDecr(AbstractStack stack, int modifier);
}
