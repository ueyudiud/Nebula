/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.data;

import java.util.function.Function;

import nebula.common.fluid.FluidStackExt;
import nebula.common.nbt.INBTCompoundReaderAndWriter;
import nebula.common.nbt.INBTReaderAndWriter;
import nebula.common.util.ItemStacks;
import nebula.common.util.NBTs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
public class NBTLSs
{
	public static final INBTCompoundReaderAndWriter<ItemStack>			RW_ITEMSTACK			= new INBTCompoundReaderAndWriter<ItemStack>()
	{
		@Override
		public ItemStack readFrom(NBTTagCompound nbt)
		{
			return ItemStack.loadItemStackFromNBT(nbt);
		}
		
		@Override
		public void writeTo(ItemStack target, NBTTagCompound nbt)
		{
			target.writeToNBT(nbt);
		}
		
		public Class<ItemStack> type()
		{
			return ItemStack.class;
		}
	};
	public static final INBTCompoundReaderAndWriter<FluidStack>		RW_FLUIDSTACK			= new INBTCompoundReaderAndWriter<FluidStack>()
	{
		@Override
		public FluidStack readFrom(NBTTagCompound nbt)
		{
			return FluidStackExt.loadFluidStackFromNBT(nbt);
		}
		
		@Override
		public void writeTo(FluidStack target, NBTTagCompound nbt)
		{
			target.writeToNBT(nbt);
		}
		
		public Class<FluidStack> type()
		{
			return FluidStack.class;
		}
	};
	public static final INBTReaderAndWriter<ItemStack[], NBTTagList>	RW_UNORDERED_ITEMSTACKS	= NBTs.wrapAsUnorderedArrayWriterAndReader(RW_ITEMSTACK);
	public static final INBTReaderAndWriter<Integer, NBTTagInt>		RW_INT					= new INBTReaderAndWriter<Integer, NBTTagInt>()
	{
		public NBTTagInt writeTo(Integer target)
		{
			return new NBTTagInt(target);
		}
		
		public Integer readFrom(NBTTagInt nbt)
		{
			return nbt.getInt();
		}
		
		public Class<Integer> type()
		{
			return int.class;
		}
	};
	public static final INBTReaderAndWriter<Float, NBTTagFloat>		RW_FLOAT				= new INBTReaderAndWriter<Float, NBTTagFloat>()
	{
		public NBTTagFloat writeTo(Float target)
		{
			return new NBTTagFloat(target);
		}
		
		public Float readFrom(NBTTagFloat nbt)
		{
			return nbt.getFloat();
		}
		
		public Class<Float> type()
		{
			return float.class;
		}
	};
	public static final INBTReaderAndWriter<byte[], NBTTagByteArray>		RW_BYTE_ARRAY				= new INBTReaderAndWriter<byte[], NBTTagByteArray>()
	{
		public NBTTagByteArray writeTo(byte[] target)
		{
			return new NBTTagByteArray(target);
		}
		
		public byte[] readFrom(NBTTagByteArray nbt)
		{
			return nbt.getByteArray();
		}
		
		public Class<byte[]> type()
		{
			return byte[].class;
		}
	};
	
	public static final Function<ItemStack, NBTTagCompound>	ITEMSTACK_WRITER	= ItemStacks::writeItemStackToNBT;
	public static final Function<NBTTagCompound, ItemStack>	ITEMSTACK_READER	= ItemStack::loadItemStackFromNBT;
}
