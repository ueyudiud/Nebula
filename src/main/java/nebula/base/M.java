/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNullableByDefault;

import nebula.base.function.F;

/**
 * @author ueyudiud
 */
@ParametersAreNullableByDefault
public final class M
{
	private M() { }
	
	public static final Function<Object, Map<?, ?>> newHashMap = F.anyf(HashMap::new);
	
	public static <T, K, V> void put(@Nonnull Map<T, Map<K, V>> map, T tag, K key, V value) { put(map, tag, key, value, (Function) newHashMap); }
	public static <T, K, V> void put(@Nonnull Map<T, Map<K, V>> map, T tag, K key, V value, @Nonnull Function<? super T, ? extends Map<K, V>> supplier) { map.computeIfAbsent(tag, supplier).put(key, value); }
	
	public static <T, K, V> void put(@Nonnull Map<T, Map<K, V>> map, T tag, Map<? extends K, ? extends V> values) { put(map, tag, values, (Function) newHashMap); }
	public static <T, K, V> void put(@Nonnull Map<T, Map<K, V>> map, T tag, Map<? extends K, ? extends V> values, @Nonnull Function<T, ? extends Map<K, V>> supplier) { map.computeIfAbsent(tag, supplier).putAll(values); }
	
	public static <T, K, V> Map<K, V> get(@Nonnull Map<T, Map<K, V>> map, T tag) { return map.computeIfAbsent(tag, (Function) newHashMap); }
}
