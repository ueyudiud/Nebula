/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.stack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public final class FS
{
	private FS() { }
	
	/**
	 * Copy a new stack, return <code>null</code> if stack is <code>null</code>.
	 * @param stack the fluid stack.
	 * @return the copied fluid stack.
	 */
	public static FluidStack copy(@Nullable FluidStack stack)
	{
		return stack != null ? stack.copy() : null;
	}
	
	/**
	 * Copy a new stack with same data of old stack but different amount.
	 * @param stack the old stack.
	 * @param amount the new stack amount.
	 * @return the new stack.
	 */
	public static FluidStack copy(FluidStack stack, int amount)
	{
		FluidStack ret;
		(ret = stack.copy()).amount = amount;
		return ret;
	}
	
	public static int amount(@Nullable FluidStack stack)
	{
		return stack != null ? stack.amount : 0;
	}
	
	public static boolean similar(@Nullable FluidStack s1, @Nullable FluidStack s2)
	{
		return s1 == s2 || (s1 != null && similar_(s1, s2));
	}
	
	public static boolean similar_(FluidStack s1, FluidStack s2)
	{
		return s1.isFluidEqual(s2);
	}
	
	public static boolean equal(@Nullable FluidStack s1, @Nullable FluidStack s2)
	{
		return s1 == s2 || (s1 != null && equal_(s1, s2));
	}
	
	public static boolean equal_(FluidStack s1, FluidStack s2)
	{
		return s1.isFluidStackIdentical(s2);
	}
	
	/**
	 * Return itself if and only if stack is non-null and has positive amount.
	 * @param stack the fluid stack.
	 * @return the validated stack.
	 */
	@Nullable
	public static FluidStack validate(@Nullable FluidStack stack)
	{
		return stack != null && stack.amount > 0 ? stack : null;
	}
}
