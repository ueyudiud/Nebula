/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.gui;

import nebula.common.gui.Container00Base;
import nebula.common.gui.FluidSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public class GuiContainer01Slots extends GuiContainer00Base
{
	public int xSize;
	public int ySize;
	
	public GuiContainer01Slots(Container00Base container)
	{
		super(container);
	}
	
	public GuiContainer01Slots(Container00Base container, ResourceLocation location)
	{
		super(container, location);
	}
	
	public GuiContainer01Slots(Container00Base container, int xSize, int ySize)
	{
		super(container, xSize, ySize);
	}
	
	public GuiContainer01Slots(Container00Base container, int xSize, int ySize, ResourceLocation location)
	{
		super(container, xSize, ySize, location);
	}
	
	@Override
	protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		this.xSize = super.xSize;
		this.ySize = super.ySize;
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		drawGuiContainerBackgroundLayer1(partialTicks, mouseX, mouseY);
		drawSlots();
		drawGuiContainerBackgroundLayer2(partialTicks, mouseX, mouseY);
	}
	
	protected void drawSlots()
	{
		for (FluidSlot slot : ((Container00Base) this.inventorySlots).fluidSlots)
		{
			slot.renderSlot(this, this.guiLeft, this.guiTop);
		}
	}
	
	protected void drawGuiContainerBackgroundLayer1(float partialTicks, int mouseX, int mouseY)
	{
		
	}
	
	protected void drawGuiContainerBackgroundLayer2(float partialTicks, int mouseX, int mouseY)
	{
		
	}
}
