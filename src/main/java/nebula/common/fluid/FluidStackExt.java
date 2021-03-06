/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.fluid;

import nebula.common.environment.IEnvironment;
import nebula.common.fluid.IFluidPropertiesAndBehaviours.IFP_Solutability;
import nebula.common.fluid.IFluidPropertiesAndBehaviours.IFP_Temperature;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackExt extends FluidStack
{
	public int temperature = getFluid().getTemperature(this);
	
	/**
	 * This provides a safe method for retrieving a FluidStack - if the Fluid is
	 * invalid, the stack will return as null.
	 */
	public static FluidStackExt loadFluidStackFromNBT(NBTTagCompound nbt)
	{
		if (nbt == null) return null;
		String fluidName = nbt.getString("FluidName");
		if (fluidName == null || FluidRegistry.getFluid(fluidName) == null) return null;
		FluidStackExt stack = new FluidStackExt(FluidRegistry.getFluid(fluidName));
		stack.readFromNBT(nbt);
		return stack;
	}
	
	public static boolean areFluidStackEqual(FluidStack stacka, FluidStack stackb)
	{
		return stacka == null || stackb == null ? stacka == stackb : stacka.isFluidEqual(stackb) && stacka.amount == stackb.amount;
	}
	
	public static FluidStackExt copyOf(FluidStack stack)
	{
		return stack == null ? null : new FluidStackExt(stack);
	}
	
	public FluidStackExt(Fluid fluid, int amount)
	{
		super(fluid, amount);
	}
	
	public FluidStackExt(Fluid fluid, int amount, NBTTagCompound nbt)
	{
		super(fluid, amount, nbt);
	}
	
	public FluidStackExt(Fluid fluid, int amount, int temperature, NBTTagCompound nbt)
	{
		super(fluid, amount, nbt);
		this.temperature = temperature;
	}
	
	public FluidStackExt(FluidStack stack, int amount)
	{
		super(stack, amount);
		if (stack instanceof FluidStackExt)
		{
			this.temperature = ((FluidStackExt) stack).temperature;
		}
	}
	
	public FluidStackExt(FluidStack stack, int amount, int temperature)
	{
		super(stack, amount);
		this.temperature = temperature;
	}
	
	FluidStackExt(FluidStack stack)
	{
		super(stack.getFluid(), stack.amount, stack.tag);
		if (stack instanceof FluidStackExt)
		{
			this.temperature = ((FluidStackExt) stack).temperature;
		}
	}
	
	FluidStackExt(Fluid fluid)
	{
		super(fluid, 1);
	}
	
	@Override
	public boolean isFluidEqual(FluidStack other)
	{
		if (!super.isFluidEqual(other)) return false;
		return !(other instanceof FluidStackExt) ? true : isPropertiesEqual((FluidStackExt) other);
	}
	
	public boolean isPropertiesEqual(FluidStackExt other)
	{
		return this.temperature == other.temperature;
	}
	
	public int getTemperature()
	{
		Fluid fluid = getFluid();
		if (fluid instanceof IFP_Temperature) return ((IFP_Temperature) fluid).regetTemperature(this, this.temperature);
		return this.temperature;
	}
	
	public boolean isSolutable(ItemStack stack)
	{
		Fluid fluid = getFluid();
		if (fluid instanceof IFP_Solutability) return ((IFP_Solutability) fluid).isItemSolutable(this, stack);
		return false;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt = super.writeToNBT(nbt);
		nbt.setInteger("Temperature", this.temperature);
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.amount = nbt.getInteger("Amount");
		if (nbt.hasKey("Tag"))
		{
			this.tag = nbt.getCompoundTag("Tag");
		}
		this.temperature = nbt.getInteger("Temperature");
	}
	
	public void onCreate(IEnvironment environment)
	{
		this.temperature = (int) environment.biomeTemperature();
	}
	
	@Override
	public FluidStack copy()
	{
		return new FluidStackExt(this);
	}
	
	public FluidStack toSimple()
	{
		return new FluidStack(getFluid(), this.amount, this.tag);
	}
}
