/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.gui;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import com.google.common.reflect.TypeToken;

import nebula.base.function.F;
import nebula.common.inventory.IStackHandler;
import nebula.common.network.PacketBufferExt;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
public abstract class StacksWatcher<T>
{
	private static final Map<Class<?>, IntFunction<?>> MAP = new HashMap<>();
	private static final byte ALL = 2, MULTI = 1, NONE = 0;
	
	public static StacksWatcher<ItemStack> newItemStackWatcher(List<ItemSlot> slots)
	{
		return new ItemStackWatcher(slots);
	}
	
	public static StacksWatcher<FluidStack> newFluidStackWatcher(List<FluidSlot> slots)
	{
		return new FluidStackWatcher(slots);
	}
	
	static class ItemStackWatcher extends StacksWatcher<ItemStack>
	{
		ItemStackWatcher(List<ItemSlot> slots)
		{
			super(IStackHandler.ITEMSTACK_HANDLER, slots);
		}
		
		@Override
		protected void serialize(PacketBufferExt buf, ItemStack value)
		{
			buf.writeItemStack(value);
		}
		
		@Override
		protected ItemStack deserialize(PacketBufferExt buf) throws IOException
		{
			return buf.readItemStack();
		}
	}
	
	static class FluidStackWatcher extends StacksWatcher<FluidStack>
	{
		FluidStackWatcher(List<FluidSlot> slots)
		{
			super(IStackHandler.FLUIDSTACK_HANDLER, slots);
		}
		
		@Override
		protected void serialize(PacketBufferExt buf, FluidStack value)
		{
			buf.writeFluidStack(value);
		}
		
		@Override
		protected FluidStack deserialize(PacketBufferExt buf) throws IOException
		{
			return buf.readFluidStack();
		}
	}
	
	private final IntFunction<T[]> supplier;
	private final IStackHandler<T> handler;
	private final List<? extends ISlot<T>> slots;
	private T[] array;
	
	@SuppressWarnings("serial")
	protected StacksWatcher(IStackHandler<T> handler, List<? extends ISlot<T>> slots)
	{
		this.handler = handler;
		this.slots = slots;
		this.supplier = (IntFunction<T[]>) MAP.computeIfAbsent(getClass(), c ->
		F.const1fi(Array::newInstance, new TypeToken<T>(c) { }.getRawType()));
	}
	
	public boolean serializeAll(PacketBufferExt buf)
	{
		buf.writeByte(ALL);
		for (ISlot<T> slot : this.slots)
		{
			serialize(buf, slot.getStack());
		}
		return true;
	}
	
	public boolean update(PacketBufferExt buf)
	{
		if (this.slots.size() == 0)
		{
			buf.writeByte(NONE);
			return false;
		}
		else if (this.array == null)
		{
			this.array = this.supplier.apply(this.slots.size());
			buf.writeByte(ALL);
			for (int i = 0; i < this.slots.size(); ++i)
			{
				serialize(buf, this.array[i] = this.slots.get(i).getStack());
			}
			return true;
		}
		else
		{
			ByteBuffer buffer = ByteBuffer.allocate(this.array.length);
			for (int i = 0; i < this.array.length; ++i)
			{
				T s = this.slots.get(i).getStack();
				if (!this.handler.isEqual(this.array[i], s))
				{
					buffer.put((byte) i);
					this.array[i] = this.handler.copy(s);
				}
			}
			buffer.flip();
			if (buffer.hasRemaining())
			{
				buf.writeByte(MULTI);
				do
				{
					int j = buffer.get();
					buf.writeByte(j);
					serialize(buf, this.array[j]);
				}
				while (buffer.hasRemaining());
				buf.writeByte(0xFF);
				return true;
			}
			else
			{
				buf.writeByte(NONE);
				return false;
			}
		}
	}
	
	public void deserializeAny(PacketBufferExt buf) throws IOException
	{
		if (this.array == null)
		{
			this.array = this.supplier.apply(this.slots.size());
		}
		int i;
		switch (buf.readByte())
		{
		case ALL:
			for (i = 0; i < this.slots.size(); ++i)
			{
				this.slots.get(i).putStack(this.array[i] = deserialize(buf));
			}
			break;
		case MULTI:
			while ((i = buf.readByte()) != -1)
			{
				this.slots.get(i).putStack(this.array[i] = deserialize(buf));
			}
			break;
		case NONE:
			break;
		default:
			throw new IOException("Illegal synch mode.");
		}
	}
	
	protected abstract void serialize(PacketBufferExt buf, T value);
	
	protected abstract T deserialize(PacketBufferExt buf) throws IOException;
}
