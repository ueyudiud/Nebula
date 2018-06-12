/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.gui;

import java.io.IOException;

import nebula.client.NebulaClientHandler;
import nebula.common.gui.Container03TileEntity;
import nebula.common.tile.IGuiTile;
import nebula.common.tile.TE00Base;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public class GuiContainer02TE<T extends TE00Base & IGuiTile> extends GuiContainer01Slots
{
	protected final T tile;
	
	public GuiContainer02TE(Container03TileEntity<T> container)
	{
		super(container, NebulaClientHandler.getBackgroundResourceLocation(container.tile.getClass()));
		this.tile = container.tile;
	}
	
	public GuiContainer02TE(Container03TileEntity<T> container, int xSize, int ySize)
	{
		super(container, xSize, ySize, NebulaClientHandler.getBackgroundResourceLocation(container.tile.getClass()));
		this.tile = container.tile;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		this.tile.initalizeGui(this, this.guiLeft, this.guiTop, this.xSize, this.ySize, this.buttonList);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		super.actionPerformed(button);
		this.tile.onButtonClicked(this, button.id);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer1(float partialTicks, int mouseX, int mouseY)
	{
		this.tile.drawBackgroundFirstLayer(this, this.guiLeft, this.guiTop, partialTicks, mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer2(float partialTicks, int mouseX, int mouseY)
	{
		this.tile.drawBackgroundSecondLayer(this, this.guiLeft, this.guiTop, partialTicks, mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.tile.drawFrontground(this, ((Container03TileEntity) this.inventorySlots).player, mouseX, mouseY, this.fontRendererObj);
	}
	
	public void setZLevel(float level)
	{
		this.zLevel = level;
	}
	
	@Override
	public void bindDefaultTexture()
	{
		super.bindDefaultTexture();
	}
	
	@Override
	public void bindTexture(ResourceLocation location)
	{
		super.bindTexture(location);
	}
	
	@Override
	public void drawAreaTooltip(int mouseX, int mouseY, String tooltip, int x, int y, int u, int v)
	{
		super.drawAreaTooltip(mouseX, mouseY, tooltip, x, y, u, v);
	}
	
	@Override
	public void drawItemStack(ItemStack stack, int x, int y, boolean renderOverlay, String altText, float zLevel)
	{
		super.drawItemStack(stack, x, y, renderOverlay, altText, zLevel);
	}
	
	@Override
	public void drawProgressScaleDTU(int x, int y, int u, int v, int w, int h, int p, int mp)
	{
		super.drawProgressScaleDTU(x, y, u, v, w, h, p, mp);
	}
	
	@Override
	public void drawProgressScaleLTR(int x, int y, int u, int v, int w, int h, int p, int mp)
	{
		super.drawProgressScaleLTR(x, y, u, v, w, h, p, mp);
	}
	
	@Override
	public void drawProgressScaleRTL(int x, int y, int u, int v, int w, int h, int p, int mp)
	{
		super.drawProgressScaleRTL(x, y, u, v, w, h, p, mp);
	}
	
	@Override
	public void drawProgressScaleUTD(int x, int y, int u, int v, int w, int h, int p, int mp)
	{
		super.drawProgressScaleUTD(x, y, u, v, w, h, p, mp);
	}
	
	@Override
	public void sendGuiData(int type, long code, boolean send)
	{
		super.sendGuiData(type, code, send);
	}
}
