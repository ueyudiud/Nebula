/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.data;

import java.util.function.Consumer;
import java.util.function.Function;

import nebula.V;
import nebula.base.function.Applicable;
import nebula.base.function.F;
import nebula.common.capability.CapabilityFactory;
import nebula.common.nbt.INBTSelfReaderAndWriter;
import nebula.common.util.Direction;
import nebula.common.util.Properties;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fluids.Fluid;

/**
 * 
 * @author ueyudiud
 *
 */
@Deprecated
public class Misc
{
	public static final IBlockState	AIR			= V.AIR;
	public static final Item		ITEM_AIR	= Item.getItemFromBlock(Blocks.AIR);
	
	public static final AxisAlignedBB[] AABB_LAYER;
	
	public static final int BUCKET_CAPACITY = Fluid.BUCKET_VOLUME;
	
	public static final PropertyBool				PROP_NORTH					= Properties.PROP_NORTH;
	public static final PropertyBool				PROP_EAST					= Properties.PROP_EAST;
	public static final PropertyBool				PROP_SOUTH					= Properties.PROP_SOUTH;
	public static final PropertyBool				PROP_WEST					= Properties.PROP_WEST;
	public static final PropertyBool				PROP_UP						= Properties.PROP_UP;
	public static final PropertyBool				PROP_DOWN					= Properties.PROP_DOWN;
	public static final PropertyBool[]				PROPS_SIDE					= { PROP_DOWN, PROP_UP, PROP_NORTH, PROP_SOUTH, PROP_WEST, PROP_EAST };
	public static final PropertyBool[]				PROPS_SIDE_HORIZONTALS		= { PROP_SOUTH, PROP_WEST, PROP_NORTH, PROP_EAST };
	public static final PropertyEnum<EnumFacing>	PROP_FACING_ALL				= Properties.PROP_FACING_ALL;
	public static final PropertyEnum<EnumFacing>	PROP_FACING_HORIZONTALS		= Properties.PROP_FACING_HORIZONTALS;
	public static final PropertyEnum<Direction>		PROP_DIRECTION_ALL			= Properties.PROP_DIRECTION_ALL;
	public static final PropertyEnum<Direction>		PROP_DIRECTION_HORIZONTALS	= Properties.PROP_DIRECTION_HORIZONTALS;
	public static final IProperty<Integer>			PROP_CUSTOM_DATA			= Properties.PROP_CUSTOM_DATA;
	
	public static final IAttribute PROJECTILE_DAMAGE = (new RangedAttribute((IAttribute) null, "nebula.projectile.damage", 0.0D, 0, Double.MAX_VALUE)).setShouldWatch(true);
	
	public static final Function	TO_NULL		= F.toNullf();
	public static final Consumer	NO_ACTION	= arg -> { };
	public static final Applicable	NO_APPLY	= () -> null;
	
	public static final boolean[]	BOOLS_EMPTY		= V.BOOLS_EMPTY;
	public static final byte[]		BYTES_EMPTY		= V.BYTES_EMPTY;
	public static final short[]		SHORTS_EMPTY	= V.SHORTS_EMPTY;
	public static final int[]		INTS_EMPTY		= V.INTS_EMPTY;
	public static final long[]		LONGS_EMPTY		= V.LONGS_EMPTY;
	public static final float[]		FLOATS_EMPTY	= V.FLOATS_EMPTY;
	public static final double[]	DOUBLES_EMPTY	= V.DOUBLES_EMPTY;
	
	public static final IStorage<? extends INBTSelfReaderAndWriter> STORAGE = CapabilityFactory.storage();
	
	@Deprecated
	public static <T, R> Function<T, R> anyTo(R result)
	{
		return F.anyf(result);
	}
	
	static
	{
		AABB_LAYER = new AxisAlignedBB[16];
		for (int i = 0; i < 16; ++i)
		{
			AABB_LAYER[i] = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, (i + 1) / 16.0F, 1.0F);
		}
	}
}
