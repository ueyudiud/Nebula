/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.stack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;

import nebula.common.block.IExtendedDataBlock;
import nebula.common.nbt.NBTFormat;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

@ParametersAreNonnullByDefault
public class BaseStack implements AbstractStack
{
	public static final BaseStack EMPTY = new BaseStack(new Subitem((Item) null), 0);
	
	public static BaseStack sizeOf(BaseStack stack, int size)
	{
		return size <= 0 ? null : new BaseStack(stack.item, size);
	}
	
	public Subitem			item;
	public int				size;
	
	private static int getMeta(IBlockState state)
	{
		Block block = state.getBlock();
		return block instanceof IExtendedDataBlock ?
				((IExtendedDataBlock) block).getDataFromState(state) : block.getMetaFromState(state);
	}
	
	public BaseStack(String modid, String name                    ) { this(modid, name, 1); }
	public BaseStack(String modid, String name, int size          ) { this(modid, name, size, -1); }
	public BaseStack(String modid, String name, int size, int meta) { this(Item.REGISTRY.getObject(new ResourceLocation(modid, name)), size, meta, NBTFormat.EMPTY); }
	
	public BaseStack(IBlockState state          ) { this(state, 1); }
	public BaseStack(IBlockState state, int size) { this(state.getBlock(), size, getMeta(state)); }
	
	public BaseStack(@Nullable Block block                    ) { this(block, 1); }
	public BaseStack(@Nullable Block block, int size          ) { this(block, size, -1); }
	public BaseStack(@Nullable Block block, int size, int meta) { this(Item.getItemFromBlock(block), size, meta); }
	
	public BaseStack(@Nullable Item item                                                  ) { this(item, 1); }
	public BaseStack(@Nullable Item item, int size                                        ) { this(item, size, -1); }
	public BaseStack(@Nullable Item item, int size, int meta                              ) { this(item, size, meta, NBTFormat.EMPTY); }
	public BaseStack(@Nullable Item item, int size, int meta, @Nullable NBTTagCompound nbt) { this(new Subitem(item, meta, NBTFormat.from(nbt), ImmutableList.of()), size); }
	public BaseStack(@Nullable Item item, int size, int meta,           NBTFormat format  ) { this(new Subitem(item, meta, format, ImmutableList.of()), size); }
	
	public BaseStack(ItemStack stack          ) { this(stack, 1); }
	public BaseStack(ItemStack stack, int size) { this(new Subitem(stack), size); }
	
	public BaseStack(SubitemStack stack) { this(stack.getSubitem(), stack.size); }
	
	public BaseStack(Subitem item, int size)
	{
		this.item = item;
		this.size = size;
	}
	
	@Override
	public boolean similar(ItemStack stack)
	{
		return this.item.match(stack);
	}
	
	@Override
	public boolean contain(ItemStack stack)
	{
		return similar(stack) && (this.item == null || this.size <= stack.stackSize);
	}
	
	@Override
	public int size(ItemStack stack)
	{
		return this.size;
	}
	
	@Override
	public AbstractStack split(ItemStack stack)
	{
		return sizeOf(this, this.size - stack.stackSize);
	}
	
	@Override
	public AbstractStack copyWithSize(int size)
	{
		return sizeOf(this, size);
	}
	
	@Override
	public ItemStack instance()
	{
		return instance(this.size);
	}
	
	/**
	 * Create a instance with specific size.
	 * @param size the size of created stack.
	 * @return the ItemStack.
	 */
	public ItemStack instance(int size)
	{
		return this.item.stack(size);
	}
	
	@Override
	public ImmutableList<ItemStack> display()
	{
		return this.item != null ? ImmutableList.of(instance()) : ImmutableList.of();
	}
	
	@Override
	public boolean valid()
	{
		return this.item != null;
	}
	
	@Override
	public ItemStack extract(ItemStack stack)
	{
		return stack.stackSize > this.size ? IS.copy(stack, stack.stackSize - this.size) : null;
	}
	
	@Override
	public boolean extract(ItemStack[] stacks, int index)
	{
		return this.item == null ? true : AbstractStack.super.extract(stacks, index);
	}
	
	public void extract_(@Nonnull ItemStack[] stacks, int index)
	{
		ItemStack stack = stacks[index];
		if (stack.stackSize == this.size)
		{
			stacks[index] = null;
		}
		else
		{
			stacks[index].stackSize -= this.size;
		}
	}
	
	@Override
	public String toString()
	{
		return this.item == null ? "NULL" : "[" + this.item + "]" + "x" + this.size;
	}
	
	@Override
	public int hashCode()
	{
		return this.item == null ? 31 : this.item.hashCode() * 31 + this.size;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if ((obj == this) || (this.item == null && obj == EMPTY))
			return true;
		if (!(obj instanceof BaseStack))
			return false;
		BaseStack stack = (BaseStack) obj;
		return this.item.equals(stack.item) && this.size == stack.size;
	}
}
