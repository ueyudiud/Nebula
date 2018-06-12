/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.stack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public class SubfluidStack
{
	private Subfluid fluid;
	public int amount;
	
	public SubfluidStack(Subfluid fluid)
	{
		this(fluid, 1);
	}
	public SubfluidStack(Subfluid fluid, int amount)
	{
		this.fluid = fluid;
		this.amount = amount;
	}
	
	public Fluid getFluid()
	{
		return this.fluid.getFluid();
	}
	
	public Subfluid getSubfluid()
	{
		return this.fluid;
	}
	
	public FluidStack toFluidStack()
	{
		return this.fluid.stack(this.amount);
	}
	
	/**
	 * Return a copy of Subfluid stack with specific size,
	 * <code>null</code> if amount is non-positive number.
	 * @param amount the stack amount.
	 * @return the new stack.
	 */
	@Nullable
	public SubfluidStack of(int amount)
	{
		return amount > 0 ? new SubfluidStack(this.fluid, amount) : null;
	}
}
