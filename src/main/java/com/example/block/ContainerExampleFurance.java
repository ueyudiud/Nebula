/*
 * copyright 2016-2018 ueyudiud
 */
package com.example.block;

import nebula.common.gui.Container03TileEntity;
import nebula.common.gui.ItemSlot;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ueyudiud
 */
public class ContainerExampleFurance extends Container03TileEntity<TEExampleFurnace>
{
	public ContainerExampleFurance(TEExampleFurnace tile, EntityPlayer player)
	{
		super(tile, player);
		TransferLocation in = createLocation(1);
		this.addSlotToContainer(new ItemSlot(tile.getItemContainer(0), tile, 0, 56, 17));
		this.addSlotToContainer(new ItemSlot(tile.getItemContainer(1), tile, 1, 56, 53));
		TransferLocation out = createLocation(1);
		this.addSlotToContainer(new ItemSlot(tile.getItemContainer(2), tile, 2, 116, 35));
		this.stragtegies.add(new TS(in).addLocation(this.locationPlayer));
		this.stragtegies.add(new TS(out).addLocation(this.locationPlayer));
		this.tsPlayerBag.addLocation(in).addLocation(this.locationHand);
		this.tsPlayerHand.addLocation(in).addLocation(this.locationBag);
	}
}
