/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import static nebula.common.inventory.IContainer.PROCESS;

import nebula.common.inventory.task.Task;
import nebula.common.stack.SubfluidStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
public class FluidContainers<C extends IFluidContainer> extends Containers<C, FluidStack> implements IFluidContainers
{
	public FluidContainers(C...containers)
	{
		super(IStackHandler.FLUIDSTACK_HANDLER, containers);
	}
	
	@Override
	public Task.TaskBTB taskInsertAllShaped(SubfluidStack[] stacks, int modifier)
	{
		final int m1 = modifier | PROCESS;
		return taskShaped(stacks, (c, m) -> c.taskIncr(m.getSubfluid(), m.amount, m1));
	}
	
	@Override
	public Task.TaskBTB taskInsertAllShapeless(SubfluidStack[] stacks, int modifier)
	{
		final int m1 = modifier | PROCESS;
		return taskShapelessInsert(stacks, modifier, (c, m) -> m.of(m.amount - c.incrStack(m.getSubfluid(), m.amount, m1)));
	}
	
	@Override
	public Task.TaskBTB taskExtractAllShaped(SubfluidStack[] stacks, int modifier)
	{
		final int m1 = modifier | PROCESS;
		return taskShaped(stacks, (c, m) -> c.taskDecr(m.getSubfluid(), m.amount, m1));
	}
	
	@Override
	public Task.TaskBTB taskExtractAllShapeless(SubfluidStack[] stacks, int modifier)
	{
		final int m1 = modifier | PROCESS;
		return taskShapeless(stacks, modifier, (c, m) -> m.of(m.amount - c.decrStack(m.getSubfluid(), m.amount, m1)));
	}
}
