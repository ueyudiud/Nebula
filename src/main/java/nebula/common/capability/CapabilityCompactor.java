/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.capability;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;

import nebula.base.function.Applicable;
import nebula.base.function.F;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Will ignore facing.
 * 
 * @author ueyudiud
 */
public class CapabilityCompactor<S> implements ICapabilityProvider
{
	private Map<Capability<?>, Supplier<?>>	map;
	
	public CapabilityCompactor(S source, Object...appliers)
	{
		if ((appliers.length & 0x1) != 0)
		{
			this.map = ImmutableMap.of();
			return;
		}
		ImmutableMap.Builder<Capability<?>, Supplier<?>> builder = ImmutableMap.builder();
		for (int i = 0; i < appliers.length; i += 2)
		{
			Capability<?> capability = (Capability<?>) appliers[i];
			Object appliable = appliers[i + 1];
			Supplier<?> cache;
			if (appliable instanceof Applicable)
			{
				cache = Applicable.asCached((Applicable<?>) appliable);
			}
			else if (appliable instanceof Function)
			{
				final Function<S, ?> function = (Function<S, ?>) appliable;
				cache = Applicable.asCached(F.const1f(function, source));
			}
			else
			{
				cache = F.anys(Objects.requireNonNull(appliable));
			}
			builder.put(capability, cache);
		}
		this.map = builder.build();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return this.map.containsKey(capability);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return hasCapability(capability, facing) ? (T) this.map.get(capability).get() : null;
	}
}
