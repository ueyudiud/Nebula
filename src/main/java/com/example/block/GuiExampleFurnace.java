/*
 * copyright 2016-2018 ueyudiud
 */
package com.example.block;

import nebula.client.gui.GuiBackground;
import nebula.client.gui.GuiContainer02TE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
@GuiBackground("textures/gui/container/furnace.png")
@SideOnly(Side.CLIENT)
public class GuiExampleFurnace extends GuiContainer02TE<TEExampleFurnace>
{
	public GuiExampleFurnace(TEExampleFurnace tile, EntityPlayer player)
	{
		super(new ContainerExampleFurance(tile, player));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer1(float partialTicks, int mouseX, int mouseY)
	{
		if (this.tile.getField(1) > 0)
		{
			int k = 13 * this.tile.getField(0) / this.tile.getField(1);
			this.drawTexturedModalRect(this.guiLeft + 56, this.guiTop + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}
		
		int l = 24 * this.tile.getField(2) / 200;
		this.drawTexturedModalRect(this.guiLeft + 79, this.guiTop + 34, 176, 14, l + 1, 16);
	}
}
