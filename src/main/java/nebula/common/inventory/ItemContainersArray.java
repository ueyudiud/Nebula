/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.inventory;

import java.util.Arrays;

import nebula.base.collection.A;
import nebula.common.data.NBTLSs;
import nebula.common.nbt.NBTs;
import nebula.common.stack.IS;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
public class ItemContainersArray extends ItemContainers<ItemContainerArraySimple> implements IContainersArray<ItemStack>
{
	public final ItemStack[] stacks;
	
	public ItemContainersArray(int size, int limit)
	{
		super(ItemContainerArraySimple.create(size, limit));
		this.stacks = this.containers[0].stacks;
	}
	
	public ItemContainersArray(ItemStack[] stacks, int limit)
	{
		super(ItemContainerArraySimple.create(stacks, limit));
		this.stacks = stacks;
	}
	
	@Override
	public void fromArray(ItemStack[] array)
	{
		A.fill(this.stacks, 0, Math.min(this.stacks.length, array.length), i -> IS.copy(array[i]));
		if (this.stacks.length > array.length)
		{
			Arrays.fill(this.stacks, array.length, this.stacks.length, null);
		}
	}
	
	@Override
	public ItemStack[] toArray()
	{
		ItemStack[] result = new ItemStack[this.stacks.length];
		A.fill(result, i -> IS.copy(this.stacks[i]));
		return result;
	}
	
	@Override
	public void clear()
	{
		Arrays.fill(this.stacks, null);
	}
	
	@Override
	public void clearRange(int from, int to)
	{
		Arrays.fill(this.stacks, from, to, null);
	}
	
	@Override
	public NBTTagCompound writeTo(NBTTagCompound nbt, String key)
	{
		this.handler.writeTo(nbt, key, this.stacks);
		return nbt;
	}
	
	@Override
	public void writeTo(NBTTagCompound nbt)
	{
		NBTs.set(nbt, "stacks", this.stacks, NBTLSs.RW_ITEMSTACK);
	}
	
	@Override
	public void readFrom(NBTTagCompound nbt, String key)
	{
		this.handler.readFrom(nbt, key, this.stacks);
	}
	
	@Override
	public void readFrom(NBTTagCompound nbt)
	{
		NBTs.get(nbt, "stacks", this.stacks, NBTLSs.RW_ITEMSTACK);
	}
}
