/*
 * copyright 2016-2018 ueyudiud
 */
package com.example.block;

import nebula.client.gui.GuiBackground;
import nebula.client.gui.GuiContainer02TE;
import nebula.common.gui.Container03TileEntity;
import nebula.common.gui.ISlotInitalizer;
import nebula.common.gui.ItemSlot;
import nebula.common.gui.ItemSlotOutput;
import nebula.common.inventory.*;
import nebula.common.tile.ITilePropertiesAndBehavior.ITB_BlockActived;
import nebula.common.tile.TE06HasGui;
import nebula.common.util.Direction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@GuiBackground("textures/gui/container/furnace.png")
public class TEExampleFurnace extends TE06HasGui<Container03TileEntity>
implements ITB_BlockActived
{
	private ItemStack[] stacks;
	
	private int maxBurnTime;
	private int burnTime;
	private int cookTime;
	
	public TEExampleFurnace()
	{
		this.items = new ItemContainers<>(ItemContainerArraySimple.create(this.stacks = new ItemStack[3], 64));
		this.fluids = new FluidContainers<>(new IFluidContainer[0]);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.burnTime = compound.getInteger("BurnTime");
		this.maxBurnTime = compound.getInteger("MaxBurnTime");
		this.cookTime = compound.getInteger("CookTime");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		compound.setInteger("BurnTime", this.burnTime);
		compound.setInteger("MaxBurnTime", this.maxBurnTime);
		compound.setInteger("CookTime", this.cookTime);
		return compound;
	}
	
	@Override
	public EnumActionResult onBlockActivated(EntityPlayer player, EnumHand hand, ItemStack stack, Direction side,
			float hitX, float hitY, float hitZ)
	{
		if (isServer())
		{
			openGUI(player, 0);
		}
		return EnumActionResult.SUCCESS;
	}
	
	@Override
	protected void updateServer()
	{
		super.updateServer();
		ItemStack output = this.stacks[0] != null ? FurnaceRecipes.instance().getSmeltingResult(this.stacks[0]) : null;
		if (output != null && !InventoryHelper.taskInsertAll(this.stacks, 2, output, false).invoke())
		{
			output = null;
		}
		if (output == null)
		{
			this.cookTime = 0;
		}
		if (this.burnTime == 0 && output != null)
		{
			int fuelAmount = TileEntityFurnace.getItemBurnTime(this.stacks[1]);
			if (fuelAmount > 0)
			{
				this.maxBurnTime = this.burnTime = fuelAmount;
				InventoryHelper.decrSlotStack_(this.stacks, 1, 1);
				this.items.getContainer(1).decrStack(1, IContainer.PROCESS);
			}
		}
		if (this.burnTime > 0)
		{
			this.burnTime --;
			if (output != null)
			{
				if (++ this.cookTime >= 200)
				{
					InventoryHelper.decrSlotStack_(this.stacks, 0, 1);
					InventoryHelper.taskInsertAll(this.stacks, 2, output, true).invoke();
					this.cookTime = 0;
					markDirty();
				}
			}
		}
	}
	
	@Override
	public int getField(int id)
	{
		switch (id)
		{
		case 0:
			return this.burnTime;
		case 1:
			return this.maxBurnTime;
		case 2:
			return this.cookTime;
		default:
			return 0;
		}
	}
	
	@Override
	public void setField(int id, int value)
	{
		switch (id)
		{
		case 0:
			this.burnTime = value;
			break;
		case 1:
			this.maxBurnTime = value;
			break;
		case 2:
			this.cookTime = value;
			break;
		}
	}
	
	@Override
	public int getFieldCount()
	{
		return 3;
	}
	
	@Override
	public String getName()
	{
		return "inventory.ExampleFurnace";
	}
	
	@Override
	public void initalizeContainer(Container03TileEntity container, ISlotInitalizer initalizer)
	{
		initalizer.addSlot("input", new ItemSlot(getItemContainer(0), this, 0, 56, 17))
		.addLocation("player", false);
		initalizer.addSlot("fuel", new ItemSlot(getItemContainer(1), this, 1, 56, 53).setPredicate(TileEntityFurnace::isItemFuel))
		.addLocation("player", false);
		initalizer.addSlot("output", new ItemSlotOutput(getItemContainer(2), this, 2, 116, 35))
		.addLocation("player", false);
		initalizer.straegyPlayerBag()
		.addLocation("input", false).addLocation("fuel", false).addLocation("hand", false);
		initalizer.straegyPlayerHand()
		.addLocation("input", false).addLocation("fuel", false).addLocation("bag", false);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void drawBackgroundFirstLayer(GuiContainer02TE<?> gui, int x, int y, float partialTicks, int mouseX,
			int mouseY)
	{
		if (this.burnTime > 0)
		{
			int k = 13 * this.burnTime / this.maxBurnTime;
			gui.drawTexturedModalRect(x + 56, y + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}
		
		int l = 24 * this.cookTime / 200;
		gui.drawTexturedModalRect(x + 79, y + 34, 176, 14, l + 1, 16);
	}
}
