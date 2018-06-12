/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

import nebula.common.gui.Container01Transfer.TransferLocation;
import nebula.common.inventory.IItemContainers;

/**
 * @author ueyudiud
 */
public interface ISlotInitalizer
{
	<T> void addSlot(ISlot<T> slot);
	
	TransferStrategyInitializer addSlot(String category, ItemSlot slot);
	
	<T> void addSlots(ISlot<T>...slots);
	
	TransferStrategyInitializer addSlots(String category, ItemSlot...slots);
	
	/**
	 * Provide the standard slots matrix.
	 * @param category the slots category.
	 * @param o the offset index.
	 * @param r the row.
	 * @param c the column.
	 * @param x the x start.
	 * @param y the y start.
	 * @param w the x step.
	 * @param h the y step.
	 * @return the initializer of transfer strategy.
	 */
	TransferStrategyInitializer addStandardSlots(IItemContainers containers, String category, int o, int r, int c, int x, int y, int w, int h);
	
	/**
	 * Provide the output slots matrix whose stack is only able to be taken from slot.
	 * @param category the slots category.
	 * @param o the offset index.
	 * @param r the row.
	 * @param c the column.
	 * @param x the x start.
	 * @param y the y start.
	 * @param w the x step.
	 * @param h the y step.
	 * @return the initializer of transfer strategy.
	 */
	TransferStrategyInitializer addOutputSlots(IItemContainers containers, String category, int o, int r, int c, int x, int y, int w, int h);
	
	TransferLocation getLocation(String category);
	
	TransferStrategyInitializer straegyPlayerHand();
	
	TransferStrategyInitializer straegyPlayerBag();
	
	interface TransferStrategyInitializer
	{
		TransferStrategyInitializer addLocation(String category, boolean reserve);
	}
}
