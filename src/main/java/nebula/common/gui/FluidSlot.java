/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

import nebula.client.gui.GuiContainer00Base;
import nebula.common.inventory.IFluidContainer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Fluid slot type.
 * 
 * @author ueyudiud
 */
public class FluidSlot implements ISlot<FluidStack>
{
	public IFluidContainer	container;
	protected boolean		shouldRender	= true;
	public int				x;
	public int				y;
	public int				u;
	public int				v;
	public int				slotNumber;
	public boolean			renderHorizontal;
	
	public FluidSlot(IFluidContainer container, int x, int y, int u, int v)
	{
		this.container = container;
		this.x = x;
		this.y = y;
		this.u = u;
		this.v = v;
	}
	
	public FluidSlot setRenderHorizontal()
	{
		this.renderHorizontal = true;
		return this;
	}
	
	public FluidSlot setNoRender()
	{
		this.shouldRender = false;
		return this;
	}
	
	/**
	 * Get slot capacity.
	 * @return
	 */
	public int getCapacity()
	{
		return this.container.getCapacity();
	}
	
	/**
	 * Get fluid stack current in slot.
	 * 
	 * @return
	 */
	public FluidStack getStack()
	{
		return this.container.getStackInContainer();
	}
	
	/**
	 * Set stack to slot.
	 * 
	 * @param stack
	 */
	public void putStack(FluidStack stack)
	{
		this.container.setStackInContainer(stack);
	}
	
	//	/**
	//	 * Called when player clicked slot.
	//	 *
	//	 * @param player The player.
	//	 * @param currentStack The current item stack.
	//	 */
	//	public void onSlotClick(EntityPlayer player, ItemStack currentStack)
	//	{
	//
	//	}
	
	/**
	 * Render fluid slot into GUI.
	 * 
	 * @param gui
	 */
	@SideOnly(Side.CLIENT)
	public void renderSlot(GuiContainer00Base gui, int guiLeft, int guiTop)
	{
		if (this.shouldRender)
		{
			gui.drawFluid(guiLeft + this.x, guiTop + this.y, new FluidTankInfo(getStack(), getCapacity()), this.u, this.v, this.renderHorizontal);
		}
	}
	
	/**
	 * Should slot visible to click or do others actions.
	 * 
	 * @return
	 */
	public boolean isVisible()
	{
		return true;
	}
}
