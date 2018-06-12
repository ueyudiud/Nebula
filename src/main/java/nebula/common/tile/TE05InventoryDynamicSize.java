/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.tile;

import static nebula.common.inventory.IContainer.PROCESS;

import java.util.Arrays;

import nebula.V;
import nebula.common.inventory.IItemContainer;
import nebula.common.tile.ITilePropertiesAndBehavior.ITB_BreakBlock;
import nebula.common.util.TileEntities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author ueyudiud
 */
public abstract class TE05InventoryDynamicSize extends TE04Synchronization implements IInventory, ITB_BreakBlock
{
	protected IItemContainer[] stacks;
	
	protected abstract IItemContainer[] stacks();
	
	protected void onInventoryChanged(int index)
	{
		markDirty();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		nebula.common.nbt.NBTs.set(compound, "items", stacks());
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		nebula.common.nbt.NBTs.get(compound, "items", stacks());
	}
	
	@Override
	public boolean hasCustomName()
	{
		return this.customName != null;
	}
	
	@Override
	public ITextComponent getDisplayName()
	{
		return hasCustomName() ? new TextComponentString(this.customName) : new TextComponentTranslation(getName());
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return player.getDistanceSq(this.pos) < 64.0;
	}
	
	@Override
	public void openInventory(EntityPlayer player)
	{
		
	}
	
	@Override
	public void closeInventory(EntityPlayer player)
	{
		
	}
	
	@Override
	public int getField(int id)
	{
		return 0;
	}
	
	@Override
	public void setField(int id, int value)
	{
		
	}
	
	@Override
	public int getFieldCount()
	{
		return 0;
	}
	
	@Override
	public void clear()
	{
		Arrays.fill(stacks(), null);
		onInventoryChanged(-1);
	}
	
	@Override
	public int getSizeInventory()
	{
		return stacks().length;
	}
	
	@Override
	public void onBlockBreak(IBlockState state)
	{
		super.onBlockBreak(state);
		TileEntities.dropItemStacks(this);
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return stacks()[index].getStackInContainer();
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return stacks()[index].extractStack(count, PROCESS);
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		IItemContainer container = stacks()[index];
		ItemStack stack = container.extractStack(Integer.MAX_VALUE, PROCESS);
		container.clear();//Should take clear operation?
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		stacks()[index].setStackInContainer(stack);
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return V.GENERAL_MAX_STACK_SIZE;
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return stacks()[index].isAvailable(stack);
	}
}
