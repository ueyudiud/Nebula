/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

import java.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nebula.base.collection.A;
import nebula.common.inventory.IItemContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @author ueyudiud
 */
public abstract class Container01Transfer extends Container00Base
{
	protected final List<TransferStrategy> stragtegies = new ArrayList<>();
	
	public Container01Transfer()
	{
		
	}
	public Container01Transfer(IInventory inventory)
	{
		super(inventory);
	}
	
	@Override
	protected void transferStackInSlot(EntityPlayer player, ItemSlot slot)
	{
		for (TransferStrategy strategy : this.stragtegies)
		{
			if (strategy.access(slot))
			{
				transferStackInSlot(player, slot, strategy);
				return;
			}
		}
	}
	
	protected void transferStackInSlot(EntityPlayer player, ItemSlot slot, TransferStrategy strategy)
	{
		boolean flag = false;
		while (slot.hasStack())
		{
			ItemStack stack = slot.container.extractStack(Integer.MAX_VALUE, 0);
			int i = strategy.transferItemStack(stack, player);
			if (i > 0)
			{
				slot.container.extractStack(i, IItemContainer.SKIP_REFRESH | IItemContainer.PROCESS);
				slot.onSlotChanged();
				flag = true;
			}
		}
		if (flag)
		{
			slot.container.refresh();
		}
	}
	
	@ParametersAreNonnullByDefault
	protected interface TransferStrategy
	{
		boolean access(ItemSlot slot);
		
		int transferItemStack(ItemStack input, EntityPlayer player);
	}
	
	protected TransferLocation createLocation(int len) { return createLocation(this.inventorySlots.size(), this.inventorySlots.size() + len); }
	public static TransferLocation createLocation(int from, int to) { return new TransferLocation(from, to); }
	
	public static class TransferLocation
	{
		protected int from, to;
		
		TransferLocation(int from, int to)
		{
			this.from = from;
			this.to = to;
		}
	}
	
	protected class TS implements TransferStrategy
	{
		protected LinkedList<Collection<ItemSlot>> list = new LinkedList<>();
		protected int from, to;
		
		public TS(TransferLocation location)
		{
			this.from = location.from;
			this.to = location.to;
		}
		public TS(int from, int to)
		{
			this.from = from;
			this.to = to;
		}
		
		public TS addLocation(int id) { this.list.addLast(ImmutableList.of(getSlot(id))); return this; }
		public TS addLocation(TransferLocation location) { return addLocation(location.from, location.to); }
		public TS addLocation(int from, int to) { this.list.addLast((Collection) Container01Transfer.this.inventorySlots.subList(from, to)); return this; }
		public TS addLocationReverse(TransferLocation location) { return addLocationReverse(location.from, location.to); }
		public TS addLocationReverse(int from, int to) { this.list.addLast((Collection) Lists.reverse(Container01Transfer.this.inventorySlots.subList(from, to))); return this; }
		
		@Override
		public boolean access(ItemSlot slot)
		{
			return slot.slotNumber >= this.from && slot.slotNumber < this.to;
		}
		
		@Override
		public int transferItemStack(ItemStack input, EntityPlayer player)
		{
			Set<ItemSlot> changes = new HashSet<>();
			ItemStack i = input.copy();
			label: for (Collection<ItemSlot> collection : this.list)
			{
				Iterable<ItemSlot> itr = A.filter(collection, s -> s.canPutStack(player) && s.isItemExpected(input));
				for (ItemSlot slot : itr)
				{
					if (slot.hasStack())
					{
						int i1 = slot.container.incrStack(i, IItemContainer.PROCESS | IItemContainer.SKIP_REFRESH);
						if (i1 > 0)
						{
							i.stackSize -= i1;
							changes.add(slot);
							if (i.stackSize == 0)
							{
								break label;
							}
						}
					}
				}
				for (ItemSlot slot : itr)
				{
					if (!slot.hasStack())
					{
						int i1 = slot.container.incrStack(i, IItemContainer.PROCESS | IItemContainer.SKIP_REFRESH);
						if (i1 > 0)
						{
							i.stackSize -= i1;
							changes.add(slot);
							if (i.stackSize == 0)
							{
								break label;
							}
						}
					}
				}
			}
			for (ItemSlot slot : changes)
			{
				slot.container.refresh();
				slot.onSlotChanged();
			}
			return input.stackSize - i.stackSize;
		}
	}
}
