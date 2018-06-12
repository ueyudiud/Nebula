/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public final class S
{
	private S() { }
	
	public static String[]		from(List<String> list) { return list.toArray(new String[list.size()]); }
	
	public static String 		validate(@Nullable String string) { return string == null ? "" : string; }
	
	public static String[] 		splitstoa(@Nullable String string, char splitor			  ) { return splitstoa(string, splitor, Integer.MAX_VALUE); }
	public static String[] 		splitstoa(@Nullable String string, char splitor, int limit) { return string == null ? new String[0] : from(splitstol(string, splitor, limit)); }
	public static List<String>	splitstol(@Nullable String string, char splitor           ) { return splitstol(string, splitor, Integer.MAX_VALUE); }
	public static List<String>	splitstol(@Nullable String string, char splitor, int limit)
	{
		if (string == null)
		{
			return ImmutableList.of();
		}
		List<String> result = new ArrayList<>(4);
		int last = 0;
		for (int i = 0; i < string.length(); ++i)
		{
			if (string.charAt(i) == splitor)
			{
				result.add(string.substring(last, i));
				last = i + 1;
				if (result.size() >= limit)
				{
					break;
				}
			}
		}
		result.add(string.substring(last));
		return result;
	}
	
	public static Iterable<String> splitstoi(@Nullable String string, char splitor           ) { return splitstoi(string, splitor, Integer.MAX_VALUE); }
	public static Iterable<String> splitstoi(@Nullable String string, char splitor, int limit)
	{
		return string == null ? ImmutableList.of() : () -> new StringSplitIterator(string, splitor, limit);
	}
	
	public static String		replaceatos(String string, char replacer, Object...     values) { return replaceatos(string, replacer, Iterators.forArray(values)); }
	public static String		replaceatos(String string, char replacer, Iterable<?> iterable) { return replaceatos(string, replacer, iterable.iterator()); }
	public static String		replaceatos(String string, char replacer, Iterator<?> iterator)
	{
		StringBuilder builder = new StringBuilder();
		int current, last = 0;
		while ((current = string.indexOf(replacer, last)) >= 0)
		{
			if (!iterator.hasNext())
			{
				throw new IllegalArgumentException("Wrong parameter count.");
			}
			builder.append(string, last, current).append(iterator.next());
			last = current + 1;
		}
		builder.append(string, last, string.length());
		return new String(builder);
	}
	
	public static String		replacestos(String string, char replacer, String...              values) { return replacestos(string, replacer, ImmutableList.copyOf(values)); }
	public static String		replacestos(String string, char replacer, Collection<String> collection)
	{
		int length = string.length() - collection.size();
		if (length < 0)
			throw new IllegalArgumentException();
		for (String replacement : collection)
		{
			length += replacement.length();
		}
		Iterator<String> itr = collection.iterator();
		char[] builder = new char[length];
		int current, last = 0, off = 0;
		while ((current = string.indexOf(replacer, last)) >= 0)
		{
			if (!itr.hasNext())
			{
				throw new IllegalArgumentException("Wrong parameter count.");
			}
			string.getChars(last, current, builder, off);
			off += current - last;
			String value = itr.next();
			value.getChars(0, value.length(), builder, off);
			off += value.length();
			last = current + 1;
		}
		if (itr.hasNext())
		{
			throw new IllegalArgumentException("Wrong parameter count.");
		}
		string.getChars(last, string.length(), builder, off);
		return new String(builder);
	}
}
