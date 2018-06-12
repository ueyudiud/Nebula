/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.nbt;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.annotation.Nullable;

import nebula.V;
import nebula.base.collection.A;
import nebula.base.function.Judgable;
import nebula.common.nbt.NBTFCompound.Info;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * @author ueyudiud
 */
public class NBTFormat implements Judgable<NBTTagCompound>
{
	public static final byte
	RANGEI = 1,
	RANGEL = 2,
	RANGEF = 3,
	RANGED = 4,
	INDEXS = 11,
	INDEXI = 12,
	INDEXL = 13,
	STRING = 21,
	SET = 31,
	MAP = 32;
	
	public static final NBTFormat EMPTY = new NBTFormat();
	
	public static NBTFormat deserialize(byte[] key)
	{
		if (key.length == 0)
		{
			return EMPTY;
		}
		else
		{
			return deserialize(ByteBuffer.wrap(key));
		}
	}
	
	private static NBTFormat deserialize(ByteBuffer buffer)
	{
		NBTFormat result = new NBTFormat();
		result.root = (INBTFormatPart<NBTTagCompound>) deserialize1(NBT.TAG_COMPOUND, buffer);
		return result;
	}
	
	public static INBTFormatPart<?> deserialize1(int type, ByteBuffer buffer)
	{
		INBTFormatPart<?> part;
		switch (type)
		{
		case INDEXS	: part = new NBTFIndexS(); break;
		case INDEXI	: part = new NBTFIndexI(); break;
		case INDEXL	: part = new NBTFIndexL(); break;
		case RANGEI	: part = new NBTFInt(); break;
		case RANGEL	: part = new NBTFLong(); break;
		case RANGEF	: part = new NBTFFloat(); break;
		case RANGED	: part = new NBTFDouble(); break;
		case STRING	: part = new NBTFString(); break;
		case SET	: part = new NBTFList<>(buffer.get()); break;
		case MAP	: part = new NBTFCompound(); break;
		default		: throw new IllegalArgumentException();
		}
		part.deserialize(buffer);
		return part;
	}
	
	public static NBTFormat from(@Nullable NBTTagCompound nbt)
	{
		if (nbt == null)
			return EMPTY;
		NBTFormat format = new NBTFormat();
		format.root = (INBTFormatPart<NBTTagCompound>) from_(nbt);
		return format;
	}
	
	private static INBTFormatPart<?> from_(NBTBase nbt)
	{
		switch (nbt.getId())
		{
		case NBT.TAG_BYTE :
		case NBT.TAG_SHORT :
		{
			NBTFIndexS nbtfp = new NBTFIndexS();
			nbtfp.idx = ((NBTPrimitive) nbt).getShort();
			return nbtfp;
		}
		case NBT.TAG_INT :
		{
			NBTFIndexI nbtfp = new NBTFIndexI();
			nbtfp.idx = ((NBTPrimitive) nbt).getInt();
			return nbtfp;
		}
		case NBT.TAG_LONG :
		{
			NBTFIndexL nbtfp = new NBTFIndexL();
			nbtfp.idx = ((NBTPrimitive) nbt).getLong();
			return nbtfp;
		}
		case NBT.TAG_FLOAT :
		{
			NBTFFloat nbtfp = new NBTFFloat();
			nbtfp.min = nbtfp.max = ((NBTPrimitive) nbt).getFloat();
			return nbtfp;
		}
		case NBT.TAG_DOUBLE :
		{
			NBTFDouble nbtfp = new NBTFDouble();
			nbtfp.min = nbtfp.max = ((NBTPrimitive) nbt).getDouble();
			return nbtfp;
		}
		case NBT.TAG_STRING :
		{
			NBTFString nbtfp = new NBTFString();
			nbtfp.value = ((NBTTagString) nbt).getString();
			return nbtfp;
		}
		case NBT.TAG_LIST :
		{
			byte type = 0;
			switch (((NBTTagList) nbt).getTagType())
			{
			case NBT.TAG_BYTE		: type = INDEXS; break;
			case NBT.TAG_SHORT		: type = INDEXS; break;
			case NBT.TAG_INT		: type = INDEXI; break;
			case NBT.TAG_LONG		: type = INDEXL; break;
			case NBT.TAG_FLOAT		: type = RANGEF; break;
			case NBT.TAG_DOUBLE		: type = RANGED; break;
			case NBT.TAG_STRING		: type = STRING; break;
			case NBT.TAG_LIST		: type = SET; break;
			case NBT.TAG_COMPOUND	: type = MAP; break;
			default : return null;
			}
			NBTFList<?> nbtfp = new NBTFList((byte) ((NBTTagList) nbt).getTagType());
			NBTTagList list = (NBTTagList) nbt;
			nbtfp.parts = new INBTFormatPart[list.tagCount()];
			nbtfp.match = type;
			for (int i = 0; i < nbtfp.parts.length; ++i)
			{
				nbtfp.parts[i] = V.cast(from_(list.get(i)));
			}
			return nbtfp;
		}
		case NBT.TAG_COMPOUND :
		{
			NBTFCompound nbtfp = new NBTFCompound();
			List<Info> parts = new ArrayList<>();
			for (String key : ((NBTTagCompound) nbt).getKeySet())
			{
				NBTBase nbt1 = ((NBTTagCompound) nbt).getTag(key);
				INBTFormatPart<?> part = from_(nbt1);
				if (part != null)
				{
					Info info = nbtfp.new Info();
					info.k = key;
					info.t = nbt1.getId();
					info.p = part;
					parts.add(info);
				}
			}
			nbtfp.infos = parts.toArray(new Info[parts.size()]);
			return nbtfp;
		}
		default:
		{
			return null;
		}
		}
	}
	
	static class NBTFList<N extends NBTBase> implements INBTFormatPart<NBTTagList>
	{
		final byte type;
		byte match;
		INBTFormatPart<? super N>[] parts;
		
		NBTFList(byte type) { this.type = type; }
		
		@Override
		public boolean match(NBTTagList nbt)
		{
			if (nbt.getTagType() != this.type || nbt.tagCount() < this.parts.length)
			{
				return false;
			}
			BitSet set = new BitSet(nbt.tagCount());
			find: for (INBTFormatPart<? super N> part : this.parts)
			{
				for (int i = 0; i < nbt.tagCount(); ++i)
				{
					if (!set.get(i) && part.match((N) nbt))
					{
						set.set(i);
						continue find;
					}
				}
				return false;
			}
			return true;
		}
		
		@Override
		public void serialize(ByteBuffer buffer)
		{
			buffer.put(this.type);
			buffer.put(this.match);
			buffer.put((byte) this.parts.length);
			for (INBTFormatPart part : this.parts)
			{
				part.serialize(buffer);
			}
		}
		
		@Override
		public void deserialize(ByteBuffer buffer)
		{
			this.match = buffer.get();
			this.parts = new INBTFormatPart[Byte.toUnsignedInt(buffer.get())];
			for (int i = 0; i < this.parts.length; ++i)
			{
				this.parts[i] = (INBTFormatPart<? super N>) deserialize1(this.match, buffer);
			}
		}
		
		@Override
		public int length()
		{
			return 3 + A.sum(this.parts, INBTFormatPart::length);
		}
		
		@Override
		public NBTTagList template()
		{
			NBTTagList list = new NBTTagList();
			for (INBTFormatPart<? super N> part : this.parts)
			{
				list.appendTag(part.template());
			}
			return list;
		}
	}
	
	static class NBTFBool implements INBTFormatPart<NBTPrimitive>
	{
		boolean flag;
		@Override public boolean match(NBTPrimitive nbt) { return (nbt.getByte() != 0) == this.flag; }
		@Override public void serialize(ByteBuffer buffer) { buffer.put((byte) (this.flag ? 1 : 0)); }
		@Override public void deserialize(ByteBuffer buffer) { this.flag = buffer.get() != 0; }
		@Override public int length() { return 1; }
		@Override public NBTPrimitive template() { return new NBTTagByte((byte) (this.flag ? 1 : 0)); }
	}
	
	static class NBTFInt implements INBTFormatPart<NBTPrimitive>
	{
		int min, max;
		@Override public boolean match(NBTPrimitive nbt) { return this.min <= nbt.getInt() && nbt.getInt() <= this.max; }
		@Override public void serialize(ByteBuffer buffer) { buffer.putInt(this.min); buffer.putInt(this.max); }
		@Override public void deserialize(ByteBuffer buffer) { this.min = buffer.getInt(); this.max = buffer.getInt(); }
		@Override public int length() { return 8; }
		@Override public NBTPrimitive template() { return new NBTTagInt(this.min); }
	}
	
	static class NBTFFloat implements INBTFormatPart<NBTPrimitive>
	{
		float min, max;
		@Override public boolean match(NBTPrimitive nbt) { return this.min <= nbt.getFloat() && nbt.getFloat() <= this.max; }
		@Override public void serialize(ByteBuffer buffer) { buffer.putFloat(this.min); buffer.putFloat(this.max); }
		@Override public void deserialize(ByteBuffer buffer) { this.min = buffer.getFloat(); this.max = buffer.getFloat(); }
		@Override public int length() { return 8; }
		@Override public NBTPrimitive template() { return new NBTTagFloat(this.min); }
	}
	
	static class NBTFLong implements INBTFormatPart<NBTPrimitive>
	{
		long min, max;
		@Override public boolean match(NBTPrimitive nbt) { return this.min <= nbt.getLong() && nbt.getLong() <= this.max; }
		@Override public void serialize(ByteBuffer buffer) { buffer.putLong(this.min); buffer.putLong(this.max); }
		@Override public void deserialize(ByteBuffer buffer) { this.min = buffer.getLong(); this.max = buffer.getLong(); }
		@Override public int length() { return 16; }
		@Override public NBTPrimitive template() { return new NBTTagLong(this.min); }
	}
	
	static class NBTFDouble implements INBTFormatPart<NBTPrimitive>
	{
		double min, max;
		@Override public boolean match(NBTPrimitive nbt) { return this.min <= nbt.getDouble() && nbt.getDouble() <= this.max; }
		@Override public void serialize(ByteBuffer buffer) { buffer.putDouble(this.min); buffer.putDouble(this.max); }
		@Override public void deserialize(ByteBuffer buffer) { this.min = buffer.getDouble(); this.max = buffer.getDouble(); }
		@Override public int length() { return 16; }
		@Override public NBTPrimitive template() { return new NBTTagDouble(this.min); }
	}
	
	static class NBTFIndexL implements INBTFormatPart<NBTPrimitive>
	{
		long idx;
		@Override public boolean match(NBTPrimitive nbt) { return nbt.getLong() == this.idx; }
		@Override public void serialize(ByteBuffer buffer) { buffer.putLong(this.idx); }
		@Override public void deserialize(ByteBuffer buffer) { this.idx = buffer.getLong(); }
		@Override public int length() { return 8; }
		@Override public NBTPrimitive template() { return this.idx <= Integer.MAX_VALUE && this.idx >= Integer.MIN_VALUE ? new NBTTagInt((int) this.idx) : new NBTTagLong(this.idx); }
	}
	
	static class NBTFIndexI implements INBTFormatPart<NBTPrimitive>
	{
		int idx;
		@Override public boolean match(NBTPrimitive nbt) { return nbt.getInt() == this.idx; }
		@Override public void serialize(ByteBuffer buffer) { buffer.putInt(this.idx); }
		@Override public void deserialize(ByteBuffer buffer) { this.idx = buffer.getInt(); }
		@Override public int length() { return 4; }
		@Override public NBTPrimitive template() { return new NBTTagInt(this.idx); }
	}
	
	static class NBTFIndexS implements INBTFormatPart<NBTPrimitive>
	{
		short idx;
		@Override public boolean match(NBTPrimitive nbt) { return nbt.getShort() == this.idx; }
		@Override public void serialize(ByteBuffer buffer) { buffer.putShort(this.idx); }
		@Override public void deserialize(ByteBuffer buffer) { this.idx = buffer.getShort(); }
		@Override public int length() { return 2; }
		@Override public NBTPrimitive template() { return new NBTTagShort(this.idx); }
	}
	
	static class NBTFString implements INBTFormatPart<NBTTagString>
	{
		String value;
		@Override public boolean match(NBTTagString nbt) { return this.value.equals(nbt.getString()); }
		@Override public void serialize(ByteBuffer buffer) { buffer.putShort((short) this.value.length()); buffer.put(this.value.getBytes(V.ISO_8859_1)); }
		@Override public void deserialize(ByteBuffer buffer) { byte[] a = new byte[buffer.getShort()]; buffer.get(a); this.value = new String(a, V.ISO_8859_1); }
		@Override public int length() { return 2 + this.value.length(); }
		@Override public NBTTagString template() { return new NBTTagString(this.value); }
	}
	
	private INBTFormatPart<NBTTagCompound> root;
	
	NBTFormat() { }
	
	public boolean hasRules()
	{
		return this.root != null;
	}
	
	@Nullable
	public NBTTagCompound template()
	{
		return this.root == null ? null : this.root.template();
	}
	
	public byte[] serialize()
	{
		if (!hasRules())
		{
			return V.BYTES_EMPTY;
		}
		ByteBuffer buffer = ByteBuffer.allocate(this.root.length());
		this.root.serialize(buffer);
		return buffer.array();
	}
	
	@Override
	public boolean test(@Nullable NBTTagCompound nbt)
	{
		return this.root == null ? (nbt == null || nbt.hasNoTags()) : this.root.match(nbt);
	}
}