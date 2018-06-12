/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.stack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import nebula.V;
import nebula.base.function.F;
import nebula.common.nbt.INBTSelfReaderAndWriter;
import nebula.common.nbt.NBTFormat;
import nebula.common.util.CapabilityMatcherRegistry;
import nebula.common.util.ICapabilityMatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public class Subitem implements INBTSelfReaderAndWriter<NBTTagCompound>
{
	private static final RegistryDelegate<Item> EMPTY = new RegistryDelegate.Delegate<>(null, Item.class);
	
	public static Subitem loadFromNBT(NBTTagCompound nbt)
	{
		return new Subitem(nbt);
	}
	
	private RegistryDelegate<Item> item;
	private short meta;
	private NBTFormat format;
	private Collection<ICapabilityMatcher<?>> matchers;
	
	public Subitem(String modid, String name                                                                        ) { this(modid, name, -1); }
	public Subitem(String modid, String name, int meta                                                              ) { this(modid, name, meta, NBTFormat.EMPTY); }
	public Subitem(String modid, String name, int meta, NBTFormat format                                            ) { this(modid, name, meta, format, ImmutableList.of()); }
	public Subitem(String modid, String name, int meta,                   Collection<ICapabilityMatcher<?>> matchers) { this(modid, name, meta, NBTFormat.EMPTY, matchers); }
	public Subitem(String modid, String name, int meta, NBTFormat format, Collection<ICapabilityMatcher<?>> matchers) { this(Item.REGISTRY.getObject(new ResourceLocation(modid, name)), meta, format, matchers); }
	
	public Subitem(ItemStack stack)  { this(stack.getItem(), (short) stack.getItemDamage(), NBTFormat.from(stack.getTagCompound())); }
	
	public Subitem(@Nullable Item item                                                                        ) { this(item, -1); }
	public Subitem(@Nullable Item item, int meta                                                              ) { this(item, meta, NBTFormat.EMPTY); }
	public Subitem(@Nullable Item item, int meta, NBTFormat format                                            ) { this(item, meta, format, ImmutableList.of()); }
	public Subitem(@Nullable Item item, int meta,                   Collection<ICapabilityMatcher<?>> matchers) { this(item, meta, NBTFormat.EMPTY, matchers); }
	public Subitem(@Nullable Item item, int meta, NBTFormat format, Collection<ICapabilityMatcher<?>> matchers)
	{
		this.item = item == null ? EMPTY : item.delegate;
		this.meta = (short) meta;
		this.format = format;
		this.matchers = matchers;
		assert check_();
	}
	Subitem(NBTTagCompound nbt)
	{
		readFrom(nbt);
	}
	
	private boolean check_()
	{
		ItemStack stack = new ItemStack(this.item.get(), 1, this.meta);
		return this.matchers.stream().map(ICapabilityMatcher::target).allMatch(F.const2p(stack::hasCapability, null));
	}
	
	public Item getItem()
	{
		return this.item.get();
	}
	
	private int maxStackSize = -1;
	
	public int getMaxStackSize()
	{
		if (this.maxStackSize < 0)
		{
			this.maxStackSize = stack(1).getMaxStackSize();
		}
		return this.maxStackSize;
	}
	
	public ItemStack stack(int size)
	{
		ItemStack stack = new ItemStack(this.item.get(), size, this.meta, null);
		for (ICapabilityMatcher matcher : this.matchers)
		{
			matcher.adjust(stack.getCapability(matcher.target(), null));
		}
		stack.setTagCompound(this.format.template());
		return stack;
	}
	
	public boolean match(ItemStack stack)
	{
		return stack == null ? this.item.get() == null :
			(stack != null && getItem() == stack.getItem() &&
			(this.meta == -1 || (stack.getItemDamage() & 0xFFFF) == this.meta) &&
			this.format.test(stack.getTagCompound()) &&
			matchCapability(stack));
	}
	
	private boolean matchCapability(ItemStack stack)
	{
		try
		{
			return this.matchers.stream().allMatch(m -> m.test(V.cast(stack.getCapability(m.target(), null))));
		}
		catch (Throwable throwable)
		{
			return false;
		}
	}
	
	@Override
	public void readFrom(NBTTagCompound nbt)
	{
		if (nbt.hasKey("item"))
		{
			this.item = Item.getByNameOrId(nbt.getString("item")).delegate;
			this.meta = nbt.getShort("meta");
			if (nbt.hasKey("nbt"))
			{
				this.format = NBTFormat.deserialize(nbt.getByteArray("nbt"));
			}
			if (nbt.hasKey("capabilities"))
			{
				NBTTagList list = nbt.getTagList("capabilities", NBT.TAG_COMPOUND);
				ImmutableCollection.Builder<ICapabilityMatcher<?>> builder = ImmutableList.builder();
				for (int i = 0; i < list.tagCount(); ++i)
				{
					builder.add(CapabilityMatcherRegistry.loadFrom(nbt));
				}
				this.matchers = builder.build();
			}
			else
			{
				this.matchers = ImmutableList.of();
			}
		}
	}
	
	@Override
	public NBTTagCompound writeTo()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		Item item = getItem();
		if (item != null)
		{
			nbt.setString("item", item.getRegistryName().toString());
			if (this.meta >= 0)
			{
				nbt.setShort("meta", this.meta);
			}
			if (this.format.hasRules())
			{
				nbt.setByteArray("nbt", this.format.serialize());
			}
			if (!this.matchers.isEmpty())
			{
				NBTTagList list = new NBTTagList();
				for (ICapabilityMatcher<?> matcher : this.matchers)
				{
					list.appendTag(matcher.serializeNBT());
				}
				nbt.setTag("capabilities", list);
			}
		}
		return nbt;
	}
	
	@Override
	public String toString()
	{
		return Objects.toString(this.item.name(), "<missing item>") + "@" + this.meta;
	}
	
	@Override
	public int hashCode()
	{
		return 31 * (31 * (31 * (31 + Objects.hashCode(getItem())) + this.meta) + this.format.hashCode()) + this.matchers.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		if (!(obj instanceof Subitem)) return false;
		Subitem subitem = (Subitem) obj;
		return getItem() == subitem.getItem() && this.meta == subitem.meta &&
				Arrays.equals(this.format.serialize(), subitem.format.serialize()) &&
				this.matchers.equals(subitem.matchers);
	}
}