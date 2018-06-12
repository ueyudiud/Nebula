/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

import nebula.base.function.F;
import nebula.common.G;
import nebula.common.annotations.ModSensitive;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public class CapabilityMatcherRegistry
{
	private static final Map<String, Supplier<ICapabilityMatcher<?>>> MAP = new HashMap<>();
	
	@ModSensitive
	public static void registerMatcher(String name, Class<? extends ICapabilityMatcher<?>> clazz)
	{
		Lookup lookup = MethodHandles.publicLookup();
		MethodHandle handle;
		try
		{
			handle = lookup.findConstructor(clazz, MethodType.methodType(void.class));
		}
		catch (IllegalAccessException | NoSuchMethodException exception)
		{
			throw new IllegalArgumentException("Can not get constructor for " + clazz);
		}
		registerMatcher(name, (Supplier) F.saftys(handle::invokeExact));
	}
	
	@ModSensitive
	public static void registerMatcher(String name, Supplier<ICapabilityMatcher<?>> supplier)
	{
		registerMatcher(G.activeModid(), name, supplier);
	}
	
	public static void registerMatcher(String modid, String name, Supplier<ICapabilityMatcher<?>> supplier)
	{
		String value = modid + ":" + name;
		if (MAP.containsKey(value))
			throw new IllegalArgumentException(value + " already registered!");
		MAP.put(value, supplier);
	}
	
	public static ICapabilityMatcher<?> loadFrom(NBTTagCompound nbt)
	{
		ICapabilityMatcher<?> matcher = MAP.get(nbt.getString("id")).get();
		matcher.deserializeNBT(nbt);
		return matcher;
	}
}
