/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.stack;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import nebula.common.item.ITool;
import nebula.common.tool.EnumToolType;
import nebula.common.util.L;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public final class IS
{
	private IS() { }
	
	public static ItemStack[] from(List<ItemStack> list) { return list.toArray(new ItemStack[list.size()]); }
	
	public static List<ItemStack> getStackOf(List<Subitem> collection, int size)
	{
		return ImmutableList.copyOf(Collections2.transform(collection, i -> i.stack(size)));
	}
	
	/**
	 * Damage tool and take effect on source stack.
	 * @param stack the tool to damage.
	 * @param amount the damage amount.
	 * @param user the tool user.
	 * @param type the type of used.
	 */
	public static void damageTool(ItemStack stack, float amount, EntityLivingBase user, EnumToolType type)
	{
		if (stack.getItem() instanceof ITool)
		{
			((ITool) stack.getItem()).onToolUse(user, stack, type, amount);
		}
		else
		{
			stack.damageItem(MathHelper.ceil(amount), user);
		}
	}
	
	/**
	 * Copy a new stack, return <code>null</code> if input argument is <code>null</code>.
	 * @param stack
	 * @return
	 */
	@Nullable
	public static ItemStack copy(@Nullable ItemStack stack)
	{
		return ItemStack.copyItemStack(stack);
	}
	
	/**
	 * Copy a new stack with same data of old stack but different size.
	 * 
	 * @param stack
	 * @param size
	 * @return
	 */
	public static ItemStack copy(ItemStack stack, int size)
	{
		ItemStack ret;
		(ret = stack.copy()).stackSize = size;
		return ret;
	}
	
	/**
	 * Get size of stack.
	 * @param stack the item stack.
	 * @return the stack size.
	 */
	public static int size(@Nullable ItemStack stack)
	{
		return stack != null ? stack.stackSize : 0;
	}
	
	/**
	 * Validate stack, the result will be non-null if and only if:
	 * <li>
	 * the stack is non-null.
	 * <li>
	 * the stack size is a positive integer.
	 * </li>
	 * @param stack the checked stack.
	 * @return the result stack.
	 */
	@Nullable
	public static ItemStack validate(@Nullable ItemStack stack)
	{
		return stack != null && stack.stackSize > 0 ? stack : null;
	}
	
	/**
	 * Return <code>true</code> if two stack are similar.
	 * Two {@link ItemStack} are similar if and only if:
	 * <li>
	 * their {@link ItemStack#getItem()} are same.
	 * <li>
	 * their {@link ItemStack#getItemDamage()} are same
	 * (If item method {@link net.minecraft.item.Item#getDamage(ItemStack)} is not override).
	 * <li>
	 * their {@link ItemStack#getTagCompound()} are same.
	 * <li>
	 * the {@link ItemStack#areCapsCompatible(ItemStack)} called result is true.
	 * </li>
	 * @param s1 the first stack.
	 * @param s2 the second stack.
	 * @return <code>true</code> if two stack are similar.
	 * @see #equal(ItemStack, ItemStack)
	 * @see #similar_(ItemStack, ItemStack)
	 */
	public static boolean similar(@Nullable ItemStack s1, @Nullable ItemStack s2)
	{
		return s1 == s2 || (s1 != null && similar_(s1, s2));
	}
	
	/**
	 * The non-null version of {@link #similar(ItemStack, ItemStack)} checking.
	 * @see #similar(ItemStack, ItemStack)
	 */
	public static boolean similar_(ItemStack s1, ItemStack s2)
	{
		return s1.isItemEqual(s2) &&
				L.equals(s1.getTagCompound(), s2.getTagCompound()) &&
				s1.areCapsCompatible(s2);
	}
	
	/**
	 * Return <code>true</code> if two stack are equal.
	 * Two {@link ItemStack} are equal if and only if:
	 * <li>
	 * the statement <code>IS.similar(s1, s2) == true</code> is true.
	 * <li>
	 * their stack size are same.
	 * </li>
	 * @param s1 the first stack.
	 * @param s2 the second stack.
	 * @return <code>true</code> if two stack are equal.
	 * @see #similar(ItemStack, ItemStack)
	 * @see #equal_(ItemStack, ItemStack)
	 */
	public static boolean equal(@Nullable ItemStack s1, @Nullable ItemStack s2)
	{
		return s1 == s2 || (s1 != null && equal_(s1, s2));
	}
	
	/**
	 * The non-null version of {@link #equal(ItemStack, ItemStack)} checking.
	 * @see #equal(ItemStack, ItemStack)
	 */
	public static boolean equal_(ItemStack s1, ItemStack s2)
	{
		return similar_(s1, s2) && s1.stackSize == s2.stackSize;
	}
}
