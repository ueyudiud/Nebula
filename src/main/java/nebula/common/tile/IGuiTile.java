/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.tile;

import java.util.List;

import nebula.client.gui.GuiContainer02TE;
import nebula.common.gui.ISlotInitalizer;
import nebula.common.world.ICoord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
public interface IGuiTile<C extends Container> extends ICoord, IInventory
{
	C openContainer(int id, EntityPlayer player);
	
	@SideOnly(Side.CLIENT)
	GuiContainer openGui(int id, EntityPlayer player);
	
	void initalizeContainer(C container, ISlotInitalizer initalizer);
	
	default void onDataRecieve(C container, byte type, long value)
	{
		
	}
	
	@SideOnly(Side.CLIENT)
	default void initalizeGui(GuiContainer02TE<?> gui, int x, int y, int w, int h, List<GuiButton> buttons)
	{
		
	}
	
	@SideOnly(Side.CLIENT)
	default void onButtonClicked(GuiContainer02TE<?> gui, int id)
	{
		
	}
	
	@SideOnly(Side.CLIENT)
	default void drawBackgroundFirstLayer(GuiContainer02TE<?> gui, int x, int y, float partialTicks, int mouseX, int mouseY)
	{
		
	}
	
	@SideOnly(Side.CLIENT)
	default void drawBackgroundSecondLayer(GuiContainer02TE<?> gui, int x, int y, float partialTicks, int mouseX, int mouseY)
	{
		
	}
	
	@SideOnly(Side.CLIENT)
	default void drawFrontground(GuiContainer02TE<?> gui, EntityPlayer player, int mouseX, int mouseY, FontRenderer fontRenderer)
	{
		
	}
}
