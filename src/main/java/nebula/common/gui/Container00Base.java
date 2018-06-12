/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import nebula.Nebula;
import nebula.V;
import nebula.common.inventory.IItemContainer;
import nebula.common.network.PacketBufferExt;
import nebula.common.network.packet.PacketContainerDataUpdate;
import nebula.common.stack.IS;
import nebula.common.util.ItemStacks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author ueyudiud
 */
public abstract class Container00Base extends Container
{
	private static final InvDataWatcher EMPTY = new InvDataWatcher(ItemSlot.INVENTORY);
	
	public EntityPlayer player;
	protected boolean isClosed;
	
	/** The current drag mode (0 : evenly split, 1 : one item by slot, 2 : not used ?) */
	private int dragMode = -1;
	/** The current drag event (0 : start, 1 : add slot : 2 : end) */
	private int dragEvent;
	/** The current drag item. */
	private ItemStack dragItem;
	/** The list of slots where the ItemStack holds will be distributed */
	private final Set<ItemSlot> dragSlots = Sets.newHashSet();
	
	protected InvDataWatcher invDataWatcher;
	
	protected StacksWatcher<ItemStack> itemDataWatcher = StacksWatcher.newItemStackWatcher((List) this.inventorySlots);
	
	public List<FluidSlot> fluidSlots = new ArrayList<>(4);
	protected StacksWatcher<FluidStack> fluidDataWatcher = StacksWatcher.newFluidStackWatcher(this.fluidSlots);
	
	protected List<EntityPlayerMP> openers = new ArrayList<>(1);
	
	public Container00Base()
	{
		this.invDataWatcher = EMPTY;
	}
	public Container00Base(IInventory inventory)
	{
		this.invDataWatcher = new InvDataWatcher(inventory);
	}
	
	@Override
	public List<ItemStack> getInventory()
	{
		List<ItemStack> list = new ArrayList<>(this.inventorySlots.size());
		this.inventorySlots.forEach(s -> list.add(s.getStack()));
		return list;
	}
	
	@Override
	public void addListener(IContainerListener listener)
	{
		if (listener instanceof EntityPlayerMP)
		{
			addListener((EntityPlayerMP) listener);
		}
		else
		{
			super.addListener(listener);
		}
	}
	
	protected void addListener(EntityPlayerMP player)
	{
		assert (this.openers.contains(player)) :
			new IllegalArgumentException("Player already listening");
		detectAndSendChanges();
		this.openers.add(player);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		PacketBufferExt buf = new PacketBufferExt(Unpooled.buffer());
		if (updatePacket(buf))
		{
			PacketContainerDataUpdate packet = new PacketContainerDataUpdate(this, buf.array());
			this.openers.forEach(p -> Nebula.network.sendToPlayer(packet, p));
		}
	}
	
	protected void serializeAllToPacket(PacketBufferExt buf)
	{
		this.invDataWatcher.serializeAll(buf);
		this.itemDataWatcher.serializeAll(buf);
		this.fluidDataWatcher.serializeAll(buf);
	}
	
	protected void deserializeAllFromPacket(PacketBufferExt buf) throws IOException
	{
		this.invDataWatcher.deserialize(buf);
		this.itemDataWatcher.deserializeAny(buf);
		this.fluidDataWatcher.deserializeAny(buf);
	}
	
	protected boolean updatePacket(PacketBufferExt buf)
	{
		boolean flag = false;
		flag |= this.invDataWatcher.update(buf);
		flag |= this.itemDataWatcher.update(buf);
		flag |= this.fluidDataWatcher.update(buf);
		return flag;
	}
	
	public void updateChangesData(byte[] data) throws IOException
	{
		deserializeAllFromPacket(new PacketBufferExt(data));
	}
	
	public byte[] applyAllData()
	{
		ByteBuf buf = Unpooled.buffer();
		serializeAllToPacket(new PacketBufferExt(buf));
		return buf.array();
	}
	
	@Override
	@Deprecated
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
	}
	
	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
	{
		ItemSlot slot;
		InventoryPlayer inv = player.inventory;
		switch (clickTypeIn)
		{
		case CLONE :
			if (player.capabilities.isCreativeMode && inv.getItemStack() == null && slotId >= 0)
			{
				slot = getSlot(slotId);
				if (slot.canTakeStack(player) && slot.hasStack())
				{
					inv.setItemStack(slot.getStack());
				}
			}
			break;
		case THROW :
			if (inv.getItemStack() == null && slotId >= 0)
			{
				slot = getSlot(slotId);
				if (slot.canTakeStack(player) && slot.hasStack())
				{
					if (dragType == 0)
					{
						player.dropItem(slot.decrStackSize(1), false);
					}
					else
					{
						for (ItemStack stack : slot.container.stacks())
						{
							player.dropItem(stack, false);
						}
						slot.container.clear();
					}
					slot.onSlotChanged();
				}
			}
			break;
		case QUICK_MOVE :
			if (slotId < 0)
			{
				dropPlayerItem(player, dragType);
			}
			else
			{
				if (slotId < 0)
				{
					break;
				}
				slot = getSlot(slotId);
				if (slot.canTakeStack(player) && slot.hasStack())
				{
					transferStackInSlot(player, slot);
				}
			}
			break;
		case PICKUP :
			if (slotId < 0)
			{
				dropPlayerItem(player, dragType);
			}
			else if (dragType == 0 || dragType == 1)
			{
				ItemStack remain;
				slot = getSlot(slotId);
				ItemStack stack = inv.getItemStack();
				if (slot.canOperateStack(player))
				{
					ActionResult<ItemStack> result = slot.container.clickContainer(stack, IItemContainer.PROCESS);
					if (result.getType() != EnumActionResult.PASS)
					{
						inv.setItemStack(result.getResult());
						slot.onSlotChanged();
						break;
					}
				}
				if (slot.canPutStack(player) && !slot.hasStack())
				{
					if (dragType == 0)
					{
						remain = slot.container.insertStack(stack, IItemContainer.PROCESS);
						if (remain != stack)
						{
							slot.onSlotChanged();
							player.inventory.setItemStack(remain);
						}
					}
					else
					{
						remain = slot.container.insertStack(IS.copy(stack, 1), IItemContainer.PROCESS);
						if (remain == null)
						{
							if (stack.stackSize == 1)
							{
								inv.setItemStack(null);
							}
							else
							{
								stack.stackSize --;
							}
							slot.onSlotChanged();
						}
					}
				}
				else
				{
					ItemStack contain;
					if (stack == null)
					{
						if (slot.canTakeStack(player))
						{
							contain = slot.getStack();
							int size = dragType == 0 ? contain.stackSize : (contain.stackSize + 1 >> 1);
							inv.setItemStack(slot.container.decrStack(size, IItemContainer.PROCESS));
							slot.onSlotChanged();
						}
					}
					else if (slot.canPutStack(player)) label:
					{
						if (dragType == 0)
						{
							remain = slot.container.insertStack(stack, IItemContainer.PROCESS);
							if (remain != stack)
							{
								inv.setItemStack(remain);
								slot.onSlotChanged();
								break label;
							}
						}
						else
						{
							remain = slot.container.insertStack(IS.copy(stack, 1), IItemContainer.PROCESS);
							if (remain == null)
							{
								if (stack.stackSize == 1)
								{
									inv.setItemStack(null);
								}
								else
								{
									stack.stackSize --;
								}
								slot.onSlotChanged();
								break label;
							}
						}
						if (slot.canTakeStack(player) && (contain = slot.getStack()).stackSize <= inv.getInventoryStackLimit())
						{
							IItemContainer simulated = slot.container.simulated();
							simulated.clear();
							if (simulated.insertStack(stack, IItemContainer.PROCESS | IItemContainer.FULLY) == null)
							{
								simulated.merge();
								inv.setItemStack(contain);
								slot.onSlotChanged();
							}
						}
					}
				}
			}
			break;
		case SWAP :
			if (dragType >= 0 && dragType < 9)
			{
				slot = getSlot(slotId);
				if (!slot.isHere(inv, dragType))
				{
					ItemStack stack = inv.getStackInSlot(dragType);
					if (!slot.hasStack())
					{
						if (slot.canOperateStack(player))
						{
							ActionResult<ItemStack> result = slot.container.clickContainer(stack, IItemContainer.PROCESS);
							if (result.getType() != EnumActionResult.PASS)
							{
								inv.setInventorySlotContents(dragType, result.getResult());
								slot.onSlotChanged();
							}
							else if (slot.canPutStack(player) && stack != null)
							{
								ItemStack remain = slot.container.insertStack(stack, IItemContainer.PROCESS);
								if (remain != stack)
								{
									inv.setInventorySlotContents(dragType, remain);
									slot.onSlotChanged();
								}
							}
						}
					}
					else if (slot.canTakeStack(player) && (stack == null || slot.canPutStack(player)) && slot.container.stacks().size() == 1)
					{
						ItemStack contained = slot.getStack();
						if (contained.stackSize <= V.GENERAL_MAX_STACK_SIZE)
						{
							if (stack == null)
							{
								inv.setInventorySlotContents(dragType, contained);
								slot.container.clear();
							}
							else
							{
								IItemContainer simulated = slot.container.simulated();
								simulated.clear();
								if (simulated.insertStack(stack, IItemContainer.PROCESS | IItemContainer.FULLY) == null)
								{
									simulated.merge();
									inv.setInventorySlotContents(dragType, contained);
									slot.onSlotChanged();
								}
							}
						}
					}
				}
			}
			break;
		case PICKUP_ALL :
			if (slotId >= 0)
			{
				slot = getSlot(slotId);
				if (inv.getItemStack() != null)
				{
					ItemStack matcher = inv.getItemStack();
					int max = matcher.getMaxStackSize();
					int size = IS.size(inv.getItemStack()), old = size;
					matcher = IS.copy(matcher, 1);
					if (dragType == 0)
					{
						for (int i = 0; i < this.inventorySlots.size(); ++i)
						{
							matcher.stackSize = max - size;
							ItemSlot slot2 = getSlot(i);
							if (slot2.canTakeStack(player))
							{
								int out = slot2.container.decrStack(matcher, IItemContainer.PROCESS);
								if (out > 0)
								{
									size += out;
									slot2.onSlotChanged();
									if (size == max)
									{
										break;
									}
								}
							}
						}
					}
					else
					{
						for (int i = this.inventorySlots.size(); i >= 0; --i)
						{
							matcher.stackSize = max - size;
							ItemSlot slot2 = getSlot(i);
							if (slot2.canTakeStack(player) && canMergeSlot(matcher, slot2))
							{
								ItemStack out = slot2.container.extractStack(matcher, IItemContainer.PROCESS);
								if (out != null)
								{
									size += out.stackSize;
									slot2.onSlotChanged();
									if (size == max)
									{
										break;
									}
								}
							}
						}
					}
					if (size != old)
					{
						matcher.stackSize = size;
						inv.setItemStack(matcher);
						detectAndSendChanges();
					}
				}
			}
			break;
		case QUICK_CRAFT :
		{
			int old = this.dragEvent;
			this.dragEvent = getDragEvent(dragType);
			if (inv.getItemStack() == null || (this.dragItem != null && !ItemStacks.areItemAndTagEqual(this.dragItem, inv.getItemStack())))
			{
				resetDrag();
			}
			else switch (this.dragEvent)
			{
			case 0 :
				this.dragMode = extractDragMode(dragType);
				if (isValidDragMode(this.dragMode, player))
				{
					this.dragEvent = 1;
					this.dragSlots.clear();
				}
				else
				{
					resetDrag();
					break;
				}
			case 1 :
				if (old != 1)
					break;
				slot = getSlot(slotId);
				if (this.dragSlots.isEmpty())
				{
					this.dragItem = IS.copy(inv.getItemStack(), 1);
				}
				if (slot.canPutStack(player) && canDragIntoSlot(slot) && slot.container.insertStack(this.dragItem, 0) == null)
				{
					this.dragSlots.add(slot);
				}
				break;
			case 2 :
				if (old != 1)
					break;
				if (!this.dragSlots.isEmpty())
				{
					ItemStack stack1 = inv.getItemStack();
					int insert = 0;
					for (ItemSlot slot2 : this.dragSlots)
					{
						ItemStack stack2 = stack1.copy();
						stack2.stackSize = computeItemStackSize(this.dragSlots, this.dragMode, stack2);
						insert += stack2.stackSize - IS.size(slot2.container.insertStack(stack2, IItemContainer.PROCESS | IItemContainer.SKIP_REFRESH));
					}
					stack1.stackSize -= insert;
					if (stack1.stackSize == 0)
					{
						inv.setItemStack(null);
					}
					for (ItemSlot slot2 : this.dragSlots)
					{
						slot2.container.refresh();
						slot2.onSlotChanged();
					}
				}
				resetDrag();
				break;
			default :
				resetDrag();
				break;
			}
			break;
		}
		}
		return null;
	}
	
	protected void transferStackInSlot(EntityPlayer player, ItemSlot slot)
	{
	}
	
	protected static int computeItemStackSize(Set<? extends Slot> dragSlotsIn, int dragModeIn, ItemStack stack)
	{
		switch (dragModeIn)
		{
		case 0:
			return stack.stackSize / dragSlotsIn.size();
		case 1:
			return 1;
		case 2:
			return stack.getMaxStackSize();
		default :
			return 0;
		}
	}
	
	protected void dropPlayerItem(EntityPlayer player, int dragType)
	{
		ItemStack stack = player.inventory.getItemStack();
		if (stack != null)
		{
			switch (dragType)
			{
			case 0 :
				player.dropItem(stack, true);
				player.inventory.setItemStack(null);
				break;
			case 1 :
				player.dropItem(stack.splitStack(1), true);
				if (stack.stackSize == 0)
				{
					player.inventory.setItemStack(null);
				}
				break;
			}
		}
	}
	
	@Override
	protected void resetDrag()
	{
		this.dragEvent = 0;
		this.dragSlots.clear();
		this.dragItem = null;
	}
	
	@Override
	public ItemSlot getSlot(int idx)
	{
		return (ItemSlot) this.inventorySlots.get(idx);
	}
	
	public FluidSlot getFluidSlot(int idx)
	{
		return this.fluidSlots.get(idx);
	}
	
	@Override
	protected Slot addSlotToContainer(Slot slotIn)
	{
		assert slotIn instanceof ItemSlot;
		return super.addSlotToContainer(slotIn);
	}
	
	protected void addStandardSlots(IItemContainer[] containers, IInventory inventory, int off, int row, int column, int x, int y, int w, int h)
	{
		int id = off;
		for (int i = 0; i < row; ++i)
		{
			for (int j = 0; j < column; ++j)
			{
				addSlotToContainer(new ItemSlot(containers[id], inventory, id, x + j * w, y + i * h));
				id ++;
			}
		}
	}
	
	protected void addOutputSlots(IItemContainer[] containers, IInventory inventory, int off, int row, int column, int x, int y, int w, int h)
	{
		int id = off;
		for (int i = 0; i < row; ++i)
		{
			for (int j = 0; j < column; ++j)
			{
				addSlotToContainer(new ItemSlot(containers[id], inventory, id, x + j * w, y + i * h));
				id ++;
			}
		}
	}
	
	protected FluidSlot addSlotToContainer(FluidSlot slot)
	{
		slot.slotNumber = this.fluidSlots.size();
		this.fluidSlots.add(slot);
		return slot;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);
		this.isClosed = true;
	}
	
	public void onRecieveGUIAction(byte type, long value)
	{
		
	}
}
