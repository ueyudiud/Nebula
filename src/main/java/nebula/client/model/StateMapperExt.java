/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.model;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The extended block state map, included vanilla state map abilities.<br>
 * Also can make custom variant key for block for sub blocks.
 * 
 * @author ueyudiud
 * @see net.minecraft.client.renderer.block.statemap.StateMapperBase
 * @see nebula.client.util.Renders#registerCompactModel(StateMapperExt,
 *      net.minecraft.block.Block, int)
 * @see nebula.client.util.Renders#registerCompactModel(StateMapperExt,
 *      net.minecraft.block.Block, IProperty)
 */
@SideOnly(Side.CLIENT)
public class StateMapperExt extends StateMapperBase implements IStateMapperExt
{
	private static final Comparator<IProperty<?>> PROPERTY_COMPARATOR = (property1, property2) -> property1.getName().compareTo(property2.getName());
	
	private String			path;
	private IProperty<?>	fileProperty;
	private List<IProperty>	ignore;
	private String			variantsKey;
	private String			variantsValue;
	
	IProperty<String> fakeProperty;
	
	/**
	 * Create a new extended block state map.
	 * 
	 * @param modid the mod belong.
	 * @param path the state file path.
	 * @param property1 the file split property, use this property value name as
	 *            file name, no property means state mapper don't split file.
	 * @param excludes the ignore properties, those properties will not
	 *            present in model state.
	 */
	public StateMapperExt(String modid, String path, @Nullable IProperty property1, IProperty...excludes)
	{
		this(modid + ":" + path, property1, excludes);
	}
	
	public StateMapperExt(String path, @Nullable IProperty property1, IProperty...excludes)
	{
		this.path = path;
		this.fileProperty = property1;
		this.ignore = ImmutableList.copyOf(excludes);
	}
	
	/** Now it is only an internal method, use to create a fake property. */
	public void markVariantProperty()
	{
		markVariantProperty(createFakeProperty(this.variantsKey, this.variantsValue));
	}
	
	public void markVariantProperty(IProperty<String> property)
	{
		this.fakeProperty = property;
	}
	
	/**
	 * The block may is a sub block in a block group (But for different id), set
	 * the variant entry for each block to identify them.
	 * 
	 * @param key the property name.
	 * @param value the state mapper variant name.
	 */
	public void setVariants(String key, String value)
	{
		this.variantsKey = key;
		this.variantsValue = value;
	}
	
	/**
	 * The map will create a instance ModelResourceLocation for selected block
	 * state.
	 * 
	 * @param state the state for location.
	 * @return the mapping location.
	 */
	@Override
	public ModelResourceLocation getLocationFromState(IBlockState state)
	{
		Map<IProperty<?>, Comparable<?>> map = new HashMap(state.getProperties());
		
		String path = modifyMap(map);
		
		map = ImmutableSortedMap.copyOf(map, PROPERTY_COMPARATOR);
		
		ModelResourceLocation location = new ModelLocation(path, getPropertyKey(map));
		return location;
	}
	
	/**
	 * For it was start as {@link #getLocationFromState} and this method is not
	 * exist. But it is reported that game crashed on obf environment without
	 * this method, so I split two method.
	 */
	@Override
	protected final ModelResourceLocation getModelResourceLocation(IBlockState state)
	{
		return getLocationFromState(state);
	}
	
	/**
	 * Modify property map.
	 * 
	 * @param map
	 * @return
	 */
	protected String modifyMap(Map<IProperty<?>, Comparable<?>> map)
	{
		if (this.variantsKey != null)
		{
			if (this.fakeProperty == null)
			{
				markVariantProperty();
			}
			map.put(this.fakeProperty, this.variantsValue);
		}
		
		StringBuilder key = new StringBuilder().append(this.path);
		
		if (this.fileProperty != null)
		{
			key.append('/').append(removeAndGetName(this.fileProperty, map));
		}
		
		map.keySet().removeAll(this.ignore);
		
		return key.toString();
	}
	
	/**
	 * Create a fake property, only contain a single value.
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static IProperty<String> createFakeProperty(String key, String...values)
	{
		Set<String> set = ImmutableSet.copyOf(values);
		return new PropertyHelper<String>(key, String.class)
		{
			public Collection<String> getAllowedValues()
			{
				return set;
			}
			
			public Optional<String> parseValue(String value)
			{
				return set.contains(value) ? Optional.of(value) : Optional.absent();
			}
			
			public String getName(String value)
			{
				return value;
			}
		};
	}
	
	/**
	 * Get key name of properties mapping.
	 * 
	 * @param values the properties map.
	 * @return the key name.
	 */
	public static String getPropertyKey(Map<IProperty<?>, Comparable<?>> values)
	{
		if (!(values instanceof SortedMap) || ((SortedMap<IProperty<?>, Comparable<?>>) values).comparator() != PROPERTY_COMPARATOR)
		{
			values = ImmutableSortedMap.copyOf(values, PROPERTY_COMPARATOR);
		}
		
		if (values.isEmpty())
		{
			return "normal";
		}
		else
		{
			StringBuilder builder = new StringBuilder();
			Entry<IProperty<?>, Comparable<?>> entry;
			IProperty property;
			Iterator<Entry<IProperty<?>, Comparable<?>>> values$itr = values.entrySet().iterator();
			entry = values$itr.next();
			property = entry.getKey();
			builder.append(property.getName()).append('=').append(property.getName(entry.getValue()));
			while (values$itr.hasNext())
			{
				entry = values$itr.next();
				property = entry.getKey();
				builder.append(',').append(property.getName()).append('=').append(property.getName(entry.getValue()));
			}
			return builder.toString();
		}
	}
	
	/** Helper method. */
	public static <T extends Comparable<T>> String removeAndGetName(IProperty<T> property, Map<IProperty<?>, Comparable<?>> map)
	{
		return property.getName((T) map.remove(property));
	}
	
	/** Helper method. */
	public static <T extends Comparable<T>> T removeAndGetValue(IProperty<T> property, Map<IProperty<?>, Comparable<?>> map)
	{
		return (T) map.remove(property);
	}
}
