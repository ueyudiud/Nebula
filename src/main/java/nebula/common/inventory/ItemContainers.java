/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import nebula.common.inventory.task.Task;
import nebula.common.stack.AbstractStack;
import nebula.common.stack.SubitemStack;
import net.minecraft.item.ItemStack;

/**
 * @author ueyudiud
 */
public class ItemContainers<C extends IItemContainer> extends Containers<C, ItemStack> implements IItemContainers
{
	public ItemContainers(C...containers)
	{
		super(IStackHandler.ITEMSTACK_HANDLER, containers);
	}
	
	@Override
	public Task.TaskBTB taskInsertAllShaped(AbstractStack[] stacks, int modifier)
	{
		return taskShaped(stacks, (c, s) -> c.taskIncr(s, modifier));
	}
	
	@Override
	public Task.TaskBTB taskInsertAllShaped(SubitemStack[] stacks, int modifier)
	{
		return taskShaped(stacks, (c, s) -> c.taskIncr(s.getSubitem(), s.size, modifier));
	}
	
	@Override
	public Task.TaskBTB taskInsertAllShapeless(AbstractStack[] stacks, int modifier)
	{
		return taskShapelessInsert(stacks, modifier, (c, m) -> c.insertStack(m, modifier) ? null : m);
	}
	
	@Override
	public Task.TaskBTB taskInsertAllShapeless(SubitemStack[] stacks, int modifier)
	{
		return taskShapelessInsert(stacks, modifier, (c, m) -> m.of(m.size - c.incrStack(m.getSubitem(), m.size, modifier)));
	}
	
	@Override
	public Task.TaskBTB taskExtractAllShaped(AbstractStack[] stacks, int modifier)
	{
		return taskShaped(stacks, (c, s) -> c.taskDecr(s, modifier));
	}
	
	@Override
	public Task.TaskBTB taskExtractAllShaped(SubitemStack[] stacks, int modifier)
	{
		return taskShaped(stacks, (c, s) -> c.taskDecr(s.getSubitem(), s.size, modifier));
	}
	
	@Override
	public Task.TaskBTB taskExtractAllShapeless(AbstractStack[] stacks, int modifier)
	{
		return taskShapeless(stacks, modifier, (c, m) -> c.extractStack(m, modifier) ? null : m);
	}
	
	@Override
	public Task.TaskBTB taskExtractAllShapeless(SubitemStack[] stacks, int modifier)
	{
		return taskShapeless(stacks, modifier, (c, m) -> m.of(m.size - c.decrStack(m.getSubitem(), m.size, modifier)));
	}
}
