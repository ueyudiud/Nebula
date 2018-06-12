/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.tile;

import java.util.Arrays;

import nebula.V;
import nebula.base.collection.A;
import nebula.common.inventory.IContainer;
import nebula.common.inventory.IItemContainerSingle;
import nebula.common.inventory.ItemContainerArraySimple;
import nebula.common.nbt.NBTs;
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
import net.minecraftforge.common.util.Constants.NBT;

/**
 * @author ueyudiud
 */
public abstract class TE02StaticInventory extends TE01Static implements IInventory, ITB_BreakBlock
{
	protected final IItemContainerSingle[]	stacks;
	
	protected String customName;
	
	protected TE02StaticInventory(int size)
	{
		ItemStack[] stacks = new ItemStack[size];
		this.stacks = A.fill(new IItemContainerSingle[size], i -> new ItemContainerArraySimple(stacks, i, getInventoryStackLimit()));
	}
	
	protected void onInventoryChanged(int index)
	{
		markDirty();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		NBTs.set(compound, "items", this.stacks);
		if (this.customName != null)
		{
			compound.setString("customName", this.customName);
		}
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		NBTs.get(compound, "items", this.stacks);
		if (compound.hasKey("customName", NBT.TAG_STRING))
		{
			this.customName = compound.getString("customName");
		}
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return true;
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
		Arrays.fill(this.stacks, null);
		onInventoryChanged(-1);
	}
	
	@Override
	public int getSizeInventory()
	{
		return this.stacks.length;
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return this.stacks[index].getStackInContainer();
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		ItemStack stack = this.stacks[index].getStackInContainer();
		this.stacks[index].clear();
		onInventoryChanged(index);
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.stacks[index].setStackInContainer(stack);
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return this.stacks[index].extractStack(count, IContainer.PROCESS);
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return V.GENERAL_MAX_STACK_SIZE;
	}
	
	@Override
	public void onBlockBreak(IBlockState state)
	{
		super.onBlockBreak(state);
		TileEntities.dropItemStacks(this);
	}
}
