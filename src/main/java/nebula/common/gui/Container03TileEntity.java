/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;

import com.google.common.collect.ImmutableMap;

import nebula.base.collection.A;
import nebula.common.inventory.IItemContainers;
import nebula.common.tile.IGuiTile;
import nebula.common.tile.TE00Base;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author ueyudiud
 */
public class Container03TileEntity<T extends TE00Base & IGuiTile> extends Container02Opener
{
	public final T tile;
	
	public Container03TileEntity(T tile, EntityPlayer player)
	{
		super(tile, player);
		this.tile = tile;
		initalizeFromTileEntity();
	}
	
	protected void initalizeFromTileEntity()
	{
		Initalizer initalizer = new Initalizer(ImmutableMap.of("bag", this.locationBag, "hand", this.locationHand, "player", this.locationPlayer));
		initalizer.initalizeContainer(this.tile);
		initalizer.appendLocations(this.tsPlayerBag, initalizer.bag);
		initalizer.appendLocations(this.tsPlayerHand, initalizer.hand);
		initalizer.build();
	}
	
	protected void openInventory()
	{
		this.tile.openInventory(this.player);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		if (!this.isClosed)
		{
			this.tile.closeInventory(playerIn);
		}
		super.onContainerClosed(playerIn);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return playerIn.isEntityAlive() && this.tile.isInitialized() && !this.tile.isInvalid();
	}
	
	void addSlotsToContainer(ItemSlot[] slots)
	{
		((ArrayList) this.inventorySlots).ensureCapacity(this.inventorySlots.size() + slots.length);
		int id = this.inventorySlots.size();
		this.inventorySlots.addAll(A.argument(slots));
		for (ItemSlot slot : slots)
		{
			slot.slotNumber = id ++;
		}
	}
	
	@Override
	public void onRecieveGUIAction(byte type, long value)
	{
		this.tile.onDataRecieve(this, type, value);
	}
	
	protected class Initalizer implements ISlotInitalizer
	{
		class DelegatedTL extends TransferLocation
		{
			DelegatedTL(String name)
			{
				super(-1, -1);
			}
		}
		
		class TSI implements TransferStrategyInitializer
		{
			private String name;
			private List<Object[]> values = new ArrayList<>(4);
			
			TSI(String name)
			{
				this.name = name;
			}
			
			@Override
			public TransferStrategyInitializer addLocation(String category, boolean reserve)
			{
				this.values.add(new Object[] {getLocation(category), reserve});
				return this;
			}
		}
		
		private Map<String, TSI> initalizers = new HashMap<>(4);
		private Map<String, TransferLocation> transferLocations;
		protected TSI hand = new TSI("hand"), bag = new TSI("bag");
		
		protected Initalizer(Map<String, TransferLocation> map)
		{
			this.transferLocations = new HashMap<>(5);
			this.transferLocations.putAll(map);
		}
		
		@Override
		public <S> void addSlot(ISlot<S> slot)
		{
			if (slot instanceof ItemSlot)
			{
				addSlotToContainer((ItemSlot) slot);
			}
			else if (slot instanceof FluidSlot)
			{
				addSlotToContainer((FluidSlot) slot);
			}
			else throw new NotImplementedException("The container can not solve " + slot.getClass() + " to add.");
		}
		
		@Override
		public TransferStrategyInitializer addSlot(String category, ItemSlot slot)
		{
			assert !this.initalizers.containsKey(category);
			TransferLocation location = getLocation(category);
			location.to = (location.from = Container03TileEntity.this.inventorySlots.size()) + 1;
			addSlotToContainer(slot);
			TSI tsi = new TSI(category);
			this.initalizers.put(category, tsi);
			return tsi;
		}
		
		@Override
		public <S> void addSlots(ISlot<S>... slots)
		{
			for (ISlot slot : slots)
			{
				addSlot(slot);
			}
		}
		
		@Override
		public TransferStrategyInitializer addSlots(String category, ItemSlot... slots)
		{
			assert !this.initalizers.containsKey(category);
			TransferLocation location = getLocation(category);
			location.to = (location.from = Container03TileEntity.this.inventorySlots.size()) + slots.length;
			addSlotsToContainer(slots);
			TSI tsi = new TSI(category);
			this.initalizers.put(category, tsi);
			return tsi;
		}
		
		@Override
		public TransferLocation getLocation(String category)
		{
			return this.transferLocations.computeIfAbsent(category, DelegatedTL::new);
		}
		
		@Override
		public TransferStrategyInitializer straegyPlayerHand()
		{
			return this.hand;
		}
		
		@Override
		public TransferStrategyInitializer straegyPlayerBag()
		{
			return this.bag;
		}
		
		protected void build()
		{
			for (TSI tsi : this.initalizers.values())
			{
				TS ts = new TS(this.transferLocations.get(tsi.name));
				appendLocations(ts, tsi);
				Container03TileEntity.this.stragtegies.add(ts);
			}
		}
		
		protected void appendLocations(TS ts, TSI tsi)
		{
			for (Object[] values : tsi.values)
			{
				if ((boolean) values[1])
				{
					ts.addLocationReverse((TransferLocation) values[0]);
				}
				else
				{
					ts.addLocation((TransferLocation) values[0]);
				}
			}
		}
		
		protected void initalizeContainer(T tile)
		{
			tile.initalizeContainer(Container03TileEntity.this, this);
		}
		
		@Override
		public TransferStrategyInitializer addStandardSlots(IItemContainers containers, String category,
				int o, int r, int c, int x, int y, int w, int h)
		{
			final ItemSlot[] slots = new ItemSlot[r * c];
			
			int p = 0;
			int id = o;
			for (int i = 0; i < r; ++i)
			{
				for (int j = 0; j < c; ++i)
				{
					slots[p ++] = new ItemSlot(containers.getContainer(id), Container03TileEntity.this.tile, id, x + w * j, y + h * i);
					id ++;
				}
			}
			return addSlots(category, slots);
		}
		
		@Override
		public TransferStrategyInitializer addOutputSlots(IItemContainers containers, String category,
				int o, int r, int c, int x, int y, int w, int h)
		{
			final ItemSlot[] slots = new ItemSlot[r * c];
			
			int p = 0;
			int id = o;
			for (int i = 0; i < r; ++i)
			{
				for (int j = 0; j < c; ++i)
				{
					slots[p ++] = new ItemSlotOutput(containers.getContainer(id), Container03TileEntity.this.tile, id, x + w * j, y + h * i);
					id ++;
				}
			}
			return addSlots(category, slots);
		}
	}
}
