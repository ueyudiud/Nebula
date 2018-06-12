/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.tile;

import nebula.client.gui.GuiContainer02TE;
import nebula.common.gui.Container03TileEntity;
import nebula.common.gui.ISlotInitalizer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
public abstract class TE03StaticHasGui<C extends Container03TileEntity> extends TE02StaticInventory implements IGuiTile<C>
{
	public TE03StaticHasGui(int size)
	{
		super(size);
	}
	
	public C openContainer(int id, EntityPlayer player)
	{
		return (C) new Container03TileEntity<>(this, player);
	}
	
	@SideOnly(Side.CLIENT)
	public GuiContainer openGui(int id, EntityPlayer player)
	{
		return new GuiContainer02TE<>(openContainer(id, player));
	}
	
	public void initalizeContainer(C container, ISlotInitalizer initalizer)
	{
		
	}
	
	@SideOnly(Side.CLIENT)
	public void drawBackgroundFirstLayer(GuiContainer02TE<?> gui, int x, int y, float partialTicks, int mouseX, int mouseY)
	{
		
	}
	
	@SideOnly(Side.CLIENT)
	public void drawBackgroundSecondLayer(GuiContainer02TE<?> gui, int x, int y, float partialTicks, int mouseX, int mouseY)
	{
		
	}
	
	@SideOnly(Side.CLIENT)
	public void drawFrontground(GuiContainer02TE<?> gui, EntityPlayer player, int mouseX, int mouseY, FontRenderer fontRenderer)
	{
		String s = getDisplayName().getUnformattedText();
		fontRenderer.drawString(s, gui.xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		fontRenderer.drawString(player.inventory.getDisplayName().getUnformattedText(), 8, gui.ySize - 96 + 2, 4210752);
	}
}
