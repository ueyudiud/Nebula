/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import nebula.common.stack.FS;
import nebula.common.stack.IS;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
class ItemStackHandler implements IStackHandler<ItemStack>
{
	@Override
	public ItemStack readFrom(NBTTagCompound nbt)
	{
		return ItemStack.loadItemStackFromNBT(nbt);
	}
	
	@Override
	public ItemStack readFrom(NBTTagCompound nbt, String key)
	{
		return nbt.hasKey(key, NBT.TAG_COMPOUND) ? readFrom(nbt.getCompoundTag(key)) : null;
	}
	
	@Override
	public void writeTo(ItemStack stack, NBTTagCompound nbt)
	{
		if (stack != null)
		{
			stack.writeToNBT(nbt);
		}
	}
	
	@Override
	public NBTTagCompound writeTo(NBTTagCompound nbt, String key, ItemStack stack)
	{
		if (stack != null)
		{
			nbt.setTag(key, writeTo(stack));
		}
		return nbt;
	}
	
	@Override
	public ItemStack copy(ItemStack stack)
	{
		return ItemStack.copyItemStack(stack);
	}
	
	@Override
	public ItemStack copy(ItemStack stack, int newSize)
	{
		return IS.copy(stack, newSize);
	}
	
	@Override
	public int size(ItemStack stack)
	{
		return stack.stackSize;
	}
	
	@Override
	public int size(ItemStack stack, int newSize)
	{
		return stack.stackSize = newSize;
	}
	
	@Override
	public int add(ItemStack stack, int incrSize)
	{
		return stack.stackSize += incrSize;
	}
	
	@Override
	public int sub(ItemStack stack, int decrSize)
	{
		return stack.stackSize -= decrSize;
	}
	
	@Override
	public boolean isSimilar(ItemStack s1, ItemStack s2)
	{
		return IS.similar(s1, s2);
	}
	
	@Override
	public boolean isEqual(ItemStack s1, ItemStack s2)
	{
		return IS.equal(s1, s2);
	}
	
	@Override
	public boolean contains(ItemStack source, ItemStack target)
	{
		return source == null || source.stackSize <= 0 || (
				target != null &&
				IS.similar_(source, target) &&
				target.stackSize >= source.stackSize);
	}
	
	@Override
	public ItemStack validate(ItemStack stack)
	{
		return IS.validate(stack);
	}
}

class FluidStackHandler implements IStackHandler<FluidStack>
{
	@Override
	public FluidStack readFrom(NBTTagCompound nbt)
	{
		return FluidStack.loadFluidStackFromNBT(nbt);
	}
	
	@Override
	public FluidStack readFrom(NBTTagCompound nbt, String key)
	{
		return nbt.hasKey(key, NBT.TAG_COMPOUND) ? readFrom(nbt.getCompoundTag(key)) : null;
	}
	
	@Override
	public void writeTo(FluidStack stack, NBTTagCompound nbt)
	{
		if (stack != null)
		{
			stack.writeToNBT(nbt);
		}
	}
	
	@Override
	public NBTTagCompound writeTo(NBTTagCompound nbt, String key, FluidStack stack)
	{
		if (stack != null)
		{
			nbt.setTag(key, writeTo(stack));
		}
		return nbt;
	}
	
	@Override
	public FluidStack copy(FluidStack stack)
	{
		return FS.copy(stack);
	}
	
	@Override
	public FluidStack copy(FluidStack stack, int newSize)
	{
		return FS.copy(stack, newSize);
	}
	
	@Override
	public int size(FluidStack stack)
	{
		return stack.amount;
	}
	
	@Override
	public int size(FluidStack stack, int newSize)
	{
		return stack.amount = newSize;
	}
	
	@Override
	public int add(FluidStack stack, int incrSize)
	{
		return stack.amount += incrSize;
	}
	
	@Override
	public int sub(FluidStack stack, int decrSize)
	{
		return stack.amount -= decrSize;
	}
	
	@Override
	public boolean isSimilar(FluidStack s1, FluidStack s2)
	{
		return FS.similar(s1, s2);
	}
	
	@Override
	public boolean isEqual(FluidStack s1, FluidStack s2)
	{
		return FS.equal(s1, s2);
	}
	
	@Override
	public boolean contains(FluidStack source, FluidStack target)
	{
		return target.containsFluid(source);
	}
	
	@Override
	public FluidStack validate(FluidStack stack)
	{
		return stack.amount <= 0 ? null : stack;
	}
}
