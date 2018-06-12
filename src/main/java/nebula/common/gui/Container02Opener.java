/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

/**
 * @author ueyudiud
 */
public abstract class Container02Opener extends Container01Transfer
{
	protected TS tsPlayerBag, tsPlayerHand;
	public final TransferLocation locationBag, locationHand, locationPlayer;
	
	public Container02Opener(EntityPlayer player)
	{
		this.player = player;
		int off = this.inventorySlots.size();
		this.tsPlayerBag = new TS(off, off + 27);
		this.locationPlayer = createLocation(36);
		this.locationBag = createLocation(27);
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				addSlotToContainer(new ItemSlot(this.player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		this.locationHand = createLocation(9);
		this.tsPlayerHand = new TS(off + 27, off + 36);
		for (int k = 0; k < 9; ++k)
		{
			addSlotToContainer(new ItemSlot(this.player.inventory, k, 8 + k * 18, 142));
		}
		this.stragtegies.add(this.tsPlayerBag);
		this.stragtegies.add(this.tsPlayerHand);
	}
	
	public Container02Opener(IInventory inventory, EntityPlayer player)
	{
		super(inventory);
		this.player = player;
		int off = this.inventorySlots.size();
		this.tsPlayerBag = new TS(off, off + 27);
		this.locationPlayer = createLocation(36);
		this.locationBag = createLocation(27);
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				addSlotToContainer(new ItemSlot(this.player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		this.locationHand = createLocation(9);
		this.tsPlayerHand = new TS(off + 27, off + 36);
		for (int k = 0; k < 9; ++k)
		{
			addSlotToContainer(new ItemSlot(this.player.inventory, k, 8 + k * 18, 142));
		}
		this.stragtegies.add(this.tsPlayerBag);
		this.stragtegies.add(this.tsPlayerHand);
	}
}
