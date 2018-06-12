/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nebula.common.block.property.PropertyInt;
import nebula.common.block.property.PropertyString;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

/**
 * @author ueyudiud
 */
public class Properties
{
	public static final PropertyBool				PROP_NORTH					= create("north");
	public static final PropertyBool				PROP_EAST					= create("east");
	public static final PropertyBool				PROP_SOUTH					= create("south");
	public static final PropertyBool				PROP_WEST					= create("west");
	public static final PropertyBool				PROP_UP						= create("up");
	public static final PropertyBool				PROP_DOWN					= create("down");
	public static final PropertyBool[]				PROPS_SIDE					= { PROP_DOWN, PROP_UP, PROP_NORTH, PROP_SOUTH, PROP_WEST, PROP_EAST };
	public static final PropertyBool[]				PROPS_SIDE_HORIZONTALS		= { PROP_SOUTH, PROP_WEST, PROP_NORTH, PROP_EAST };
	public static final PropertyEnum<EnumFacing>	PROP_FACING_ALL				= PropertyEnum.create("facing", EnumFacing.class, EnumFacing.VALUES);
	public static final PropertyEnum<EnumFacing>	PROP_FACING_HORIZONTALS		= PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);
	public static final PropertyEnum<Direction>		PROP_DIRECTION_ALL			= PropertyEnum.create("direction", Direction.class, Direction.DIRECTIONS_3D);
	public static final PropertyEnum<Direction>		PROP_DIRECTION_HORIZONTALS	= PropertyEnum.create("direction", Direction.class, Direction.DIRECTIONS_2D);
	public static final IProperty<Integer>			PROP_CUSTOM_DATA			= create("data", 0, 16);
	
	private static final Map<Class<?>, PropertyEnum<?>> PROPERTIES = new HashMap<>();
	
	/**
	 * Get a enumeration property.
	 * 
	 * @param enumClass
	 * @return
	 */
	public static <E extends Enum<E> & IStringSerializable> PropertyEnum<E> get(Class<E> enumClass)
	{
		if (PROPERTIES.containsKey(enumClass)) return (PropertyEnum<E>) PROPERTIES.get(enumClass);
		EnumStateName name = enumClass.getAnnotation(EnumStateName.class);
		if (name == null) throw new IllegalArgumentException("The enum class does not contain a state name, check if a EnumStateName annotation is presented!");
		PropertyEnum<E> property = PropertyEnum.create(name.value(), enumClass);
		PROPERTIES.put(enumClass, property);
		return property;
	}
	
	/**
	 * Create a {@link Integer} property, from min (include) value to max
	 * (include) value.
	 * 
	 * @param name the name of property.
	 * @param min the minimum value.
	 * @param max the max value.
	 * @return the property.
	 * @see net.minecraft.block.properties.PropertyInteger
	 */
	public static IProperty<Integer> create(String name, int min, int max)
	{
		return new PropertyInt(name, min, max);
	}
	
	public static PropertyBool create(String name)
	{
		return PropertyBool.create(name);
	}
	
	public static PropertyString create(String name, String...values)
	{
		return new PropertyString(name, values);
	}
	
	public static PropertyString create(String name, Collection<String> values)
	{
		return new PropertyString(name, values);
	}
	
	public static <V extends Comparable<V>> V value(IBlockState state, IProperty<V> property)
	{
		return state.getValue(property);
	}
	
	public static <V extends Comparable<V>> String name(IBlockState state, IProperty<V> property)
	{
		return property.getName(state.getValue(property));
	}
	
	/**
	 * The state annotation, mark on Enum class and provide auto property.
	 * 
	 * @author ueyudiud
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE })
	public static @interface EnumStateName
	{
		/**
		 * The name of enum property key.
		 * 
		 * @return
		 */
		String value();
	}
}
