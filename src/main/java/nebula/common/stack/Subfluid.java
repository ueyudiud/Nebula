/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.stack;

import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import nebula.common.nbt.INBTSelfReaderAndWriter;
import nebula.common.nbt.NBTFormat;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public class Subfluid implements INBTSelfReaderAndWriter<NBTTagCompound>
{
	public static Subfluid loadFromNBT(NBTTagCompound nbt)
	{
		return new Subfluid(nbt);
	}
	
	private Fluid fluid;
	private NBTFormat format;
	
	public Subfluid(@Nullable Fluid fluid, NBTFormat format)
	{
		this.fluid = fluid;
		this.format = format;
	}
	Subfluid(NBTTagCompound nbt)
	{
		readFrom(nbt);
	}
	
	public Fluid getFluid()
	{
		return this.fluid;
	}
	
	public FluidStack stack(int amount)
	{
		return new FluidStack(this.fluid, amount, this.format.template());
	}
	
	public boolean match(FluidStack stack)
	{
		return stack == null ? this.fluid == null :
			(this.fluid == stack.getFluid() && this.format.test(stack.tag));
	}
	
	@Override
	public void readFrom(NBTTagCompound nbt)
	{
		if (nbt.hasKey("fluid"))
		{
			this.fluid = FluidRegistry.getFluid(nbt.getString("fluid"));
			if (nbt.hasKey("nbt"))
			{
				this.format = NBTFormat.deserialize(nbt.getByteArray("nbt"));
			}
		}
	}
	
	@Override
	public NBTTagCompound writeTo()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		if (this.fluid != null)
		{
			nbt.setString("fluid", this.fluid.getName());
			if (this.format.hasRules())
			{
				nbt.setByteArray("nbt", this.format.serialize());
			}
		}
		return nbt;
	}
	
	@Override
	public int hashCode()
	{
		return 31 * Objects.hashCode(this.fluid) + this.format.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		if (!(obj instanceof Subfluid)) return false;
		Subfluid subfluid = (Subfluid) obj;
		return this.fluid == subfluid.fluid &&
				Arrays.equals(this.format.serialize(), subfluid.format.serialize());
	}
}