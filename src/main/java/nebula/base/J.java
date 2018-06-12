/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import nebula.base.function.ObjIntFunction;

/**
 * JSON helper methods.
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public final class J
{
	private J() { }
	
	public static Optional<String> getString(JsonObject object, String key)
	{
		return object.has(key) ? Optional.of(object.get(key).getAsString()) : Optional.empty();
	}
	
	public static OptionalInt getInt(JsonObject object, String key)
	{
		try
		{
			return object.has(key) ? OptionalInt.of(object.get(key).getAsInt()) : OptionalInt.empty();
		}
		catch (UnsupportedOperationException | NumberFormatException exception)
		{
			throw new JsonParseException(String.format("The key '%s' is not a number.", key));
		}
	}
	
	public static OptionalLong getLong(JsonObject object, String key)
	{
		try
		{
			return object.has(key) ? OptionalLong.of(object.get(key).getAsLong()) : OptionalLong.empty();
		}
		catch (UnsupportedOperationException | NumberFormatException exception)
		{
			throw new JsonParseException(String.format("The key '%s' is not a number.", key));
		}
	}
	
	public static OptionalDouble getDouble(JsonObject object, String key)
	{
		try
		{
			return object.has(key) ? OptionalDouble.of(object.get(key).getAsDouble()) : OptionalDouble.empty();
		}
		catch (UnsupportedOperationException | NumberFormatException exception)
		{
			throw new JsonParseException(String.format("The key '%s' is not a number.", key));
		}
	}
	
	public static Optional<Boolean> getBoolean(JsonObject object, String key)
	{
		try
		{
			return object.has(key) ? Optional.of(object.get(key).getAsBoolean()) : Optional.empty();
		}
		catch (UnsupportedOperationException exception)
		{
			throw new JsonParseException(String.format("The key '%s' is not a boolean value.", key));
		}
	}
	
	public static String getOrDefault(JsonObject object, String key, String def)
	{
		return getString(object, key).orElse(def);
	}
	
	public static int getOrDefault(JsonObject object, String key, int def) throws JsonParseException
	{
		return getInt(object, key).orElse(def);
	}
	
	public static long getOrDefault(JsonObject object, String key, long def) throws JsonParseException
	{
		return getLong(object, key).orElse(def);
	}
	
	public static float getOrDefault(JsonObject object, String key, float def) throws JsonParseException
	{
		return (float) getDouble(object, key).orElse(def);
	}
	
	public static double getOrDefault(JsonObject object, String key, double def) throws JsonParseException
	{
		return getDouble(object, key).orElse(def);
	}
	
	public static boolean getOrDefault(JsonObject object, String key, boolean def) throws JsonParseException
	{
		return getBoolean(object, key).orElse(def);
	}
	
	@SuppressWarnings("hiding")
	public static <R> List<R> getAsList(JsonArray array, Function<JsonElement, ? extends R> function)
	{
		List<R> list = new ArrayList<>(array.size());
		for (JsonElement j : array)
		{
			list.add(function.apply(j));
		}
		return list;
	}
	
	@SuppressWarnings("hiding")
	public static <R> List<R> getAsList(JsonArray array, ObjIntFunction<JsonElement, ? extends R> function)
	{
		List<R> list = new ArrayList<>(array.size());
		function.andThen((Consumer<R>) list::add).accept(array);
		return list;
	}
	
	@SuppressWarnings("hiding")
	public static <R, J extends JsonElement> Map<String, R> getAsMap(JsonObject object, Function<J, ? extends R> function)
	{
		Map<String, R> map = new HashMap<>(object.entrySet().size(), 1.0F);
		for (Entry<String, JsonElement> entry : object.entrySet())
		{
			map.put(entry.getKey(), function.apply((J) entry.getValue()));
		}
		return map;
	}
	
	private static void arraySizeCheck(JsonArray array, int size)
	{
		if (array.size() != size)
			throw new JsonParseException("Wrong array size! got: " + array.size() + ", expected: " + size);
	}
	
	public static float[] getFloat3(JsonElement arrayRaw) { JsonArray array = arrayRaw.getAsJsonArray(); arraySizeCheck(array, 3); return new float[] { array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat() }; }
	public static float[] getFloat4(JsonElement arrayRaw) { JsonArray array = arrayRaw.getAsJsonArray(); arraySizeCheck(array, 4); return new float[] { array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat() }; }
	public static float[] getFloat5(JsonElement arrayRaw) { JsonArray array = arrayRaw.getAsJsonArray(); arraySizeCheck(array, 5); return new float[] { array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat(), array.get(4).getAsFloat() }; }
	public static float[] getFloat6(JsonElement arrayRaw) { JsonArray array = arrayRaw.getAsJsonArray(); arraySizeCheck(array, 6); return new float[] { array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat(), array.get(4).getAsFloat(), array.get(5).getAsFloat() }; }
}
