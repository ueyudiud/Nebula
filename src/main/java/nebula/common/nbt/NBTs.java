/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import nebula.V;
import nebula.base.collection.A;
import nebula.base.collection.ArrayParser;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public final class NBTs
{
	private NBTs() { }
	
	public static <E> NBTTagList serializeOrdered(E[] value, INBTWriter<E, NBTTagCompound> writer)
	{
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < value.length; ++i)
		{
			if (value[i] != null)
			{
				NBTTagCompound nbt = writer.writeTo(value[i]);
				list.appendTag(nbt);
				nbt.setShort("idx", (short) i);
			}
		}
		return list;
	}
	
	public static <E extends INBTSelfWriter<NBTTagCompound>> NBTTagList serializeOrdered(E[] value)
	{
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < value.length; ++i)
		{
			if (value[i] != null)
			{
				NBTTagCompound nbt = value[i].writeTo();
				list.appendTag(nbt);
				nbt.setShort("idx", (short) i);
			}
		}
		return list;
	}
	
	public static <E> void deserializeOrdered(NBTTagList list, E[] value, INBTReader<E, NBTTagCompound> reader)
	{
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			value[nbt.getShort("idx")] = reader.readFrom(nbt);
		}
	}
	
	public static <E extends INBTSelfReader<NBTTagCompound>> void deserializeOrdered(NBTTagList list, E[] value)
	{
		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			value[nbt.getShort("idx")].readFrom(nbt);
		}
	}
	
	public static <E, N extends NBTBase> NBTTagList serialize(E[] value, INBTWriter<E, N> writer)
	{
		NBTTagList list = new NBTTagList();
		for (E element : value)
		{
			list.appendTag(writer.writeTo(element));
		}
		return list;
	}
	
	public static <E extends INBTSelfWriter<N>, N extends NBTBase> NBTTagList serialize(E[] value)
	{
		NBTTagList list = new NBTTagList();
		for (E element : value)
		{
			list.appendTag(element.writeTo());
		}
		return list;
	}
	
	public static <E, N extends NBTBase> void deserialize(NBTTagList list, E[] value, INBTReader<E, N> reader)
	{
		for (int i = 0; i < list.tagCount(); ++i)
		{
			value[i] = reader.readFrom((N) list.get(i));
		}
	}
	
	public static <E extends INBTSelfReader<N>, N extends NBTBase> void deserialize(NBTTagList list, E[] value)
	{
		for (int i = 0; i < list.tagCount(); ++i)
		{
			value[i].readFrom((N) list.get(i));
		}
	}
	
	public static void setcs(NBTTagCompound nbt, String key, char[] value) { nbt.setString(key, new String(value)); }
	public static char[] getcs(NBTTagCompound nbt, String key) { return nbt.getString(key).toCharArray(); }
	
	public static <E> void set(NBTTagCompound nbt, String key, E[] value, INBTCompoundWriter<E> writer) { nbt.setTag(key, serializeOrdered(value, writer)); }
	public static <E> void get(NBTTagCompound nbt, String key, E[] value, INBTCompoundReader<E> reader) { deserializeOrdered(nbt.getTagList(key, NBT.TAG_COMPOUND), value, reader); }
	
	public static <E extends INBTSelfCompoundWriter> void set(NBTTagCompound nbt, String key, E[] value) { nbt.setTag(key, serializeOrdered(value)); }
	public static <E extends INBTSelfCompoundReader> void get(NBTTagCompound nbt, String key, E[] value) { deserializeOrdered(nbt.getTagList(key, NBT.TAG_COMPOUND), value); }
	
	public static <E> void setList(NBTTagCompound nbt, String key, E[] value, Function<E, ? extends NBTBase> writer, boolean ordered)
	{
		NBTTagList list = new NBTTagList();
		if (ordered)
		{
			for (int i = 0; i < value.length; ++i)
			{
				try
				{
					NBTBase nbt1 = writer.apply(value[i]);
					if (nbt1 == null) continue;
					NBTTagCompound compound = new NBTTagCompound();
					compound.setTag("element", nbt1);
					setNumber(compound, "idx", i);
					list.appendTag(compound);
				}
				catch (Exception exception)
				{
					V.catching(exception);
				}
			}
			nbt.setTag(key, list);
		}
		else
		{
			for (E element : value)
			{
				try
				{
					list.appendTag(writer.apply(element));
				}
				catch (Exception exception)
				{
					V.catching(exception);
				}
			}
			nbt.setTag(key, list);
		}
	}
	
	public static NBTTagCompound getOrCreate(NBTTagCompound nbt, String tag)
	{
		return getCompound(nbt, tag, true);
	}
	
	public static NBTTagCompound getCompound(NBTTagCompound nbt, String tag, boolean create)
	{
		if (!nbt.hasKey(tag))
		{
			if (!create) return NBTTagCompoundEmpty.INSTANCE;
			NBTTagCompound compound;
			nbt.setTag(tag, compound = new NBTTagCompound());
			return compound;
		}
		return nbt.getCompoundTag(tag);
	}
	
	public static int plusRemovableNumber(NBTTagCompound nbt, String key, int add)
	{
		int amount = nbt.getInteger(key) + add;
		setRemovableNumber(nbt, key, amount);
		return amount;
	}
	
	public static int plusRemovableNumber(NBTTagCompound nbt, String key, int add, int max)
	{
		int amount = Math.min(max, nbt.getInteger(key) + add);
		setRemovableNumber(nbt, key, amount);
		return amount;
	}
	
	public static long plusRemovableNumber(NBTTagCompound nbt, String key, long add)
	{
		long amount = nbt.getLong(key) + add;
		setRemovableNumber(nbt, key, amount);
		return amount;
	}
	
	public static void setRemovableNumber(NBTTagCompound nbt, String key, long number)
	{
		if (number == 0)
		{
			nbt.removeTag(key);
		}
		else
		{
			setNumber(nbt, key, number);
		}
	}
	
	public static void setNumber(NBTTagCompound nbt, String key, double number)
	{
		if ((float) number == number)
		{
			nbt.setFloat(key, (float) number);
		}
		else
		{
			nbt.setDouble(key, number);
		}
	}
	
	public static void setNumber(NBTTagCompound nbt, String key, long number)
	{
		if (number <= Byte.MAX_VALUE)
		{
			nbt.setByte(key, (byte) number);
		}
		else if (number <= Short.MAX_VALUE)
		{
			nbt.setShort(key, (short) number);
		}
		else if (number <= Integer.MAX_VALUE)
		{
			nbt.setInteger(key, (int) number);
		}
		else
		{
			nbt.setLong(key, number);
		}
	}
	
	public static void setStringArray(NBTTagCompound nbt, String key, String[] array)
	{
		NBTTagList list = new NBTTagList();
		for (String element : array)
		{
			list.appendTag(new NBTTagString(element));
		}
		nbt.setTag(key, list);
	}
	
	public static void setLongArray(NBTTagCompound nbt, String key, long[] array)
	{
		NBTTagList list = new NBTTagList();
		for (long element : array)
		{
			list.appendTag(new NBTTagLong(element));
		}
		nbt.setTag(key, list);
	}
	
	public static <E extends Enum<E>> void setEnum(NBTTagCompound nbt, String key, E value)
	{
		if (value == null)
		{
			nbt.removeTag(key);
		}
		else
		{
			nbt.setInteger(key, value.ordinal());
		}
	}
	
	public static void setItemStack(NBTTagCompound nbt, String key, ItemStack stack, boolean markEmpty)
	{
		if (stack != null)
		{
			nbt.setTag(key, stack.writeToNBT(new NBTTagCompound()));
		}
		else if (markEmpty)
		{
			nbt.setTag(key, new NBTTagCompound());// Mark for empty stack.
		}
	}
	
	public static void setFluidStack(NBTTagCompound nbt, String key, FluidStack stack, boolean markEmpty)
	{
		if (stack != null)
		{
			nbt.setTag(key, stack.writeToNBT(new NBTTagCompound()));
		}
		else if (markEmpty)
		{
			nbt.setTag(key, new NBTTagCompound());// Mark for empty stack.
		}
	}
	
	public static byte getByteOrDefault(NBTTagCompound nbt, String key, int def)
	{
		return nbt.hasKey(key) ? nbt.getByte(key) : (byte) def;
	}
	
	public static short getShortOrDefault(NBTTagCompound nbt, String key, int def)
	{
		return nbt.hasKey(key) ? nbt.getShort(key) : (short) def;
	}
	
	public static int getIntOrDefault(NBTTagCompound nbt, String key, int def)
	{
		return nbt.hasKey(key) ? nbt.getInteger(key) : def;
	}
	
	public static long getLongOrDefault(NBTTagCompound nbt, String key, long def)
	{
		return nbt.hasKey(key) ? nbt.getLong(key) : def;
	}
	
	public static float getFloatOrDefault(NBTTagCompound nbt, String key, float def)
	{
		return nbt.hasKey(key) ? nbt.getLong(key) : def;
	}
	
	public static double getDoubleOrDefault(NBTTagCompound nbt, String key, double def)
	{
		return nbt.hasKey(key) ? nbt.getLong(key) : def;
	}
	
	public static int[] getIntArrayOrDefault(NBTTagCompound nbt, String key, int[] def)
	{
		return nbt.hasKey(key, NBT.TAG_INT_ARRAY) ? nbt.getIntArray(key) : def;
	}
	
	public static long[] getLongArrayOrDefault(NBTTagCompound nbt, String key, long[] def)
	{
		if (nbt.hasKey(key, NBT.TAG_LIST))
		{
			NBTTagList list = nbt.getTagList(key, NBT.TAG_LONG);
			if (def != null && def.length != list.tagCount()) return def;
			long[] result = new long[list.tagCount()];
			for (int i = 0; i < result.length; ++i)
			{
				result[i] = ((NBTTagLong) list.get(i)).getLong();
			}
			return result;
		}
		return def;
	}
	
	public static char[] getCharArrayOrDefault(NBTTagCompound nbt, String key, char[] def)
	{
		if (nbt.hasKey(key, NBT.TAG_STRING))
		{
			return nbt.getString(key).toCharArray();
		}
		return def;
	}
	
	public static String[] getStringArrayOrDefault(NBTTagCompound nbt, String key, String[] def)
	{
		if (nbt.hasKey(key, NBT.TAG_LIST))
		{
			NBTTagList list = nbt.getTagList(key, NBT.TAG_STRING);
			if (def != null && def.length != list.tagCount()) return def;
			String[] result = new String[list.tagCount()];
			for (int i = 0; i < result.length; ++i)
			{
				result[i] = list.getStringTagAt(i);
			}
			return result;
		}
		return def;
	}
	
	public static ItemStack getItemStackOrDefault(NBTTagCompound nbt, String key, ItemStack def)
	{
		return nbt.hasKey(key, NBT.TAG_COMPOUND) ? ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(key)) : def;
	}
	
	public static FluidStack getFluidStackOrDefault(NBTTagCompound nbt, String key, FluidStack def)
	{
		return nbt.hasKey(key, NBT.TAG_COMPOUND) ? FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(key)) : def;
	}
	
	public static <E extends Enum<? extends E>> E getEnumOrDefault(NBTTagCompound nbt, String key, @Nonnull E def)
	{
		try
		{
			return nbt.hasKey(key) ? (E) def.getClass().getEnumConstants()[nbt.getByte(key)] : def;
		}
		catch (ArrayIndexOutOfBoundsException exception)
		{
			return def;
		}
	}
	
	public static <E> E getValueByByteOrDefault(NBTTagCompound nbt, String key, E[] values, @Nonnull E def)
	{
		try
		{
			return nbt.hasKey(key) ? (E) values[nbt.getByte(key)] : def;
		}
		catch (ArrayIndexOutOfBoundsException exception)
		{
			return def;
		}
	}
	
	public static NBTTagCompound createNBT(Object... formats)
	{
		return createNBT(new NBTTagCompound(), formats);
	}
	
	public static NBTTagCompound setNBTData(NBTTagCompound nbt, Object... formats)
	{
		ArrayParser parser = A.parser(formats);
		final BiConsumer<String, Object> consumer = (t, f) -> {
			NBTBase n = parse(f);
			if (n != null)
			{
				nbt.setTag(t, n);
			}
		};
		if (!parser.readToEnd(consumer))
		{
			throw new IllegalArgumentException();
		}
		return nbt;
	}
	
	private static NBTBase parse(Object format)
	{
		if (format == null) { return null; }
		else if (format instanceof Byte) { return new NBTTagByte((byte) format); }
		else if (format instanceof Short) { return new NBTTagShort((short) format); }
		else if (format instanceof Integer) { return new NBTTagInt((int) format); }
		else if (format instanceof Long) { return new NBTTagLong((long) format); }
		else if (format instanceof Float) { return new NBTTagFloat((float) format); }
		else if (format instanceof Double) { return new NBTTagDouble((double) format); }
		else if (format instanceof String) { return new NBTTagString((String) format); }
		else if (format instanceof int[]) { return new NBTTagIntArray((int[]) format); }
		else if (format instanceof byte[]) { return new NBTTagByteArray((byte[]) format); }
		else if (format instanceof INBTSelfWriter<?>) { return ((INBTSelfWriter) format).writeTo(); }
		else if (format instanceof INBTSelfWriter<?>[]) { return serialize((INBTSelfWriter[]) format); }
		else if (format instanceof NBTBase) { return (NBTBase) format; }
		else if (format instanceof INBTSerializable<?>) { return ((INBTSerializable<?>) format).serializeNBT(); }
		else if (format instanceof FluidStack) { return ((FluidStack) format).writeToNBT(new NBTTagCompound()); }
		else if (format instanceof Object[])
		{
			Object[] fs = (Object[]) format;
			switch (fs.length)
			{
			case 0 : return new NBTTagCompound();
			case 1 :
				if (fs[0] == null)
					throw new IllegalArgumentException("The null can not be formated.");;
					NBTTagList list = new NBTTagList();
					list.appendTag(parse(format));
					return list;
			default:
				if ((fs.length & 1) == 0 && fs[0] instanceof String && !(fs[1] instanceof String))
				{
					return createNBT(fs);
				}
				else
				{
					list = new NBTTagList();
					for (Object arg : fs)
					{
						list.appendTag(parse(arg));
					}
					return list;
				}
			}
		}
		else throw new IllegalArgumentException("The " + format + " can not be formated.");
	}
}
