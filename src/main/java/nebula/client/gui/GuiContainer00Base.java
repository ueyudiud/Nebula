/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import nebula.Nebula;
import nebula.NebulaProxy;
import nebula.base.collection.A;
import nebula.client.ClientOverride;
import nebula.client.NebulaClientHandler;
import nebula.common.gui.Container00Base;
import nebula.common.gui.IGuiActionListener;
import nebula.common.network.packet.PacketGuiAction;
import nebula.common.util.L;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@SideOnly(Side.CLIENT)
public class GuiContainer00Base extends GuiContainer
{
	private final ResourceLocation location;
	
	public GuiContainer00Base(Container00Base container, ResourceLocation location)
	{
		super(container);
		this.location = location;
	}
	
	public GuiContainer00Base(Container00Base container)
	{
		super(container);
		if (hasBackground())
		{
			this.location = NebulaClientHandler.getBackgroundResourceLocation(getClass());
		}
		else
		{
			this.location = OPTIONS_BACKGROUND;
		}
	}
	
	public GuiContainer00Base(Container00Base container, int xSize, int ySize)
	{
		super(container);
		this.xSize = xSize;
		this.ySize = ySize;
		if (hasBackground())
		{
			this.location = NebulaClientHandler.getBackgroundResourceLocation(getClass());
		}
		else
		{
			this.location = OPTIONS_BACKGROUND;
		}
	}
	
	public GuiContainer00Base(Container00Base container, int xSize, int ySize, ResourceLocation location)
	{
		super(container);
		this.xSize = xSize;
		this.ySize = ySize;
		this.location = location;
	}
	
	protected void sendGuiData(int type, long code, boolean send)
	{
		((IGuiActionListener) this.inventorySlots).onRecieveGUIAction((byte) type, code);
		if (send)
		{
			Nebula.network.sendToServer(new PacketGuiAction<>((byte) type, code, L.castAny(this.inventorySlots)));
		}
	}
	
	protected boolean hasBackground()
	{
		return true;
	}
	
	protected void bindDefaultTexture()
	{
		bindTexture(this.location);
	}
	
	protected void bindTexture(ResourceLocation location)
	{
		this.mc.renderEngine.bindTexture(location);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (hasBackground())
		{
			bindDefaultTexture();
			drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		}
	}
	
	/**
	 * Check is key press down. This method will be used only if
	 * {@link net.minecraft.client.gui.GuiScreen#allowUserInput} is enabled.
	 * <p>
	 * The key checking is using for {@link #keyTyped(char, int)} method to use,
	 * checking the key registered at {@link nebula.common.NebulaKeyHandler} is
	 * pressed down.
	 * 
	 * @param key the key register name.
	 * @param keycode the typed keycode.
	 * @see nebula.common.NebulaKeyHandler
	 * @return <code>true</code> when key is pressed down.
	 */
	protected boolean matchKey(String key, int keycode)
	{
		return NebulaProxy.clientProxy().getBinding(key).getKeyCode() == keycode;
	}
	
	/**
	 * Return the inventory title name.
	 * @return
	 */
	protected String getTitleName()
	{
		return null;
	}
	
	protected boolean isTouchingMode()
	{
		return this.mc.gameSettings.touchscreen;
	}
	
	public TextureAtlasSprite getTexture(IBlockState state)
	{
		return this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
	}
	
	public TextureAtlasSprite getTexutre(ResourceLocation location)
	{
		return this.mc.getTextureMapBlocks().getAtlasSprite(location.toString());
	}
	
	/**
	 * @see #drawFluid(int, int, FluidTankInfo, int, int, boolean)
	 */
	public void drawFluid(int x, int y, FluidTankInfo tank, int width, int height)
	{
		drawFluid(x, y, tank, width, height, false);
	}
	
	/**
	 * Draw fluid icon to GUI.
	 * 
	 * @param x the start x position.
	 * @param y the start y position.
	 * @param info the render tank information.
	 * @param width the rendering width.
	 * @param height the rendering height.
	 * @param lay <tt>true</tt> for rendering fluid from left to right, and from
	 *            down to up else.
	 */
	public void drawFluid(int x, int y, FluidTankInfo info, int width, int height, boolean lay)
	{
		if (info.fluid == null) return;
		if (info.fluid.amount > 0)
		{
			TextureAtlasSprite fluidIcon = this.mc.getTextureMapBlocks().getAtlasSprite(info.fluid.getFluid().getStill(info.fluid).toString());
			if (fluidIcon != null)
			{
				bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				int color = info.fluid.getFluid().getColor(info.fluid);
				if (lay)
				{
					drawRepeated(fluidIcon, x, y, (double) (info.fluid.amount * width) / (double) info.capacity, height, this.zLevel, color);
				}
				else
				{
					drawRepeated(fluidIcon, x, y + height - (double) (info.fluid.amount * height) / (double) info.capacity, width, (double) (info.fluid.amount * height) / (double) info.capacity, this.zLevel, color);
				}
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				bindTexture(this.location);
			}
		}
	}
	
	/**
	 * Draw repeated icon to GUI.
	 * 
	 * @param icon the rendering icon.
	 * @param x the start x position.
	 * @param y the start y position.
	 * @param width the rendering width.
	 * @param height the rendering height.
	 * @param z the z level.
	 * @param color the rendering color.
	 */
	public void drawRepeated(TextureAtlasSprite icon, double x, double y, double width, double height, double z, int color)
	{
		double iconWidthStep = (icon.getMaxU() - icon.getMinU()) / 16.0;
		double iconHeightStep = (icon.getMaxV() - icon.getMinV()) / 16.0;
		float a = (color >>> 24 & 0xFF) / 255.0F;
		float r = (color >>> 16 & 0xFF) / 255.0F;
		float g = (color >>> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;
		
		GL11.glColor4f(r, g, b, a);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		for (double cy = y; cy < y + height; cy += 16D)
		{
			double quadHeight = Math.min(16D, (height + y) - cy);
			double maxY = cy + quadHeight;
			double maxV = icon.getMinV() + iconHeightStep * quadHeight;
			for (double cx = x; cx < x + width; cx += 16D)
			{
				double quadWidth = Math.min(16D, (width + x) - cx);
				double maxX = cx + quadWidth;
				double maxU = icon.getMinU() + iconWidthStep * quadWidth;
				buffer.pos(cx, maxY, z).tex(icon.getMinU(), maxV).endVertex();
				buffer.pos(maxX, maxY, z).tex(maxU, maxV).endVertex();
				buffer.pos(maxX, cy, z).tex(maxU, icon.getMinV()).endVertex();
				buffer.pos(cx, cy, z).tex(icon.getMinU(), icon.getMinV()).endVertex();
			}
		}
		tessellator.draw();
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}
	
	/**
	 * Draw item stack to GUI.
	 * 
	 * @param stack the rendered stack.
	 * @param x the start x position.
	 * @param y the start y position.
	 * @param renderOverlay should renderer render the overlay to GUI.
	 * @param altText the alt text.
	 * @param zLevel the z level.
	 */
	protected void drawItemStack(ItemStack stack, int x, int y, boolean renderOverlay, String altText, float zLevel)
	{
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		float oldZ = zLevel;
		this.zLevel = this.itemRender.zLevel = zLevel;
		FontRenderer font = null;
		if (stack != null) font = stack.getItem().getFontRenderer(stack);
		if (font == null) font = this.fontRendererObj;
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		if (renderOverlay)
		{
			ClientOverride.renderItemOverlay(this.itemRender, font, stack, x, y, altText);
		}
		this.zLevel = oldZ;
		this.itemRender.zLevel = 0.0F;
	}
	
	/**
	 * Draw a progress bar from up to down.
	 * @param p the progress current.
	 * @param mp the max progress.
	 */
	protected void drawProgressScaleUTD(int x, int y, int u, int v, int w, int h, int p, int mp)
	{
		int scale = (int) ((float) p / (float) mp * h);
		if (scale <= 0) return;
		if (scale > h)
		{
			scale = h;
		}
		drawTexturedModalRect(x, y, u, v, w, scale);
	}
	
	/**
	 * Draw a progress bar from down to up.
	 * @param p the progress current.
	 * @param mp the max progress.
	 */
	protected void drawProgressScaleDTU(int x, int y, int u, int v, int w, int h, int p, int mp)
	{
		int scale = (int) ((float) p / (float) mp * h);
		if (scale <= 0) return;
		if (scale > h)
		{
			scale = h;
		}
		drawTexturedModalRect(x, y + h - scale, u, v + h - scale, w, scale);
	}
	
	/**
	 * Draw a progress bar from left to right.
	 * @param p the progress current.
	 * @param mp the max progress.
	 */
	protected void drawProgressScaleLTR(int x, int y, int u, int v, int w, int h, int p, int mp)
	{
		int scale = (int) ((float) p / (float) mp * w);
		if (scale <= 0) return;
		if (scale > w)
		{
			scale = w;
		}
		drawTexturedModalRect(x, y, u, v, scale, h);
	}
	
	/**
	 * Draw a progress bar from right to left.
	 * @param p the progress current.
	 * @param mp the max progress.
	 */
	protected void drawProgressScaleRTL(int x, int y, int u, int v, int w, int h, int p, int mp)
	{
		int scale = (int) ((float) p / (float) mp * w);
		if (scale <= 0) return;
		if (scale > w)
		{
			scale = w;
		}
		drawTexturedModalRect(x + w - scale, y, u + w - scale, v, scale, h);
	}
	
	/**
	 * Draw tool tip only if mouse hovered on specific area.
	 * 
	 * @param mouseX the mouse x position.
	 * @param mouseY the mouse y position.
	 * @param tooltip the tool tip.
	 * @param x the start x position for area.
	 * @param y the start y position for area.
	 * @param u the width for area.
	 * @param v the height for area.
	 */
	protected void drawAreaTooltip(int mouseX, int mouseY, String tooltip, int x, int y, int u, int v)
	{
		if (mouseX >= x && mouseX <= (x + u) && mouseY >= y && mouseY <= (y + v))
		{
			drawTooltip(mouseX - this.guiLeft, mouseY - this.guiTop, tooltip);
		}
	}
	
	/**
	 * Draw tool tip into GUI.
	 * @param x
	 * @param y
	 * @param tooltip
	 */
	protected void drawTooltip(int x, int y, String tooltip)
	{
		drawTooltip(x, y, A.argument(tooltip));
	}
	
	/**
	 * Draw tool tip into GUI.
	 * @param x
	 * @param y
	 * @param tooltip
	 */
	protected void drawTooltip(int x, int y, List<String> tooltip)
	{
		if (!tooltip.isEmpty())
		{
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int k = tooltip.stream().mapToInt(this.fontRendererObj::getStringWidth).max().orElse(0);
			
			int j2 = x + 12;
			int k2 = y - 12;
			int i1 = 8;
			
			if (tooltip.size() > 1)
			{
				i1 += 2 + (tooltip.size() - 1) * 10;
			}
			
			if (j2 + k > this.width)
			{
				j2 -= 28 + k;
			}
			
			if (k2 + i1 + 6 > this.height)
			{
				k2 = this.height - i1 - 6;
			}
			
			this.zLevel = 300.0F;
			this.itemRender.zLevel = 300.0F;
			final int j1 = 0xF0100010;
			drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
			drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
			drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
			drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
			drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
			final int k1 = 0x505000FF;
			final int l1 = (k1 & 16711422) >> 1 | k1 & 0xFF000000;
			drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
			drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
			drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
			drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);
			
			for (int i2 = 0; i2 < tooltip.size(); ++i2)
			{
				String s1 = tooltip.get(i2);
				this.fontRendererObj.drawStringWithShadow(s1, j2, k2, -1);
				
				if (i2 == 0)
				{
					k2 += 2;
				}
				
				k2 += 10;
			}
			
			this.zLevel = 0.0F;
			this.itemRender.zLevel = 0.0F;
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}
}
