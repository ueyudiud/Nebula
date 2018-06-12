/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import nebula.V;
import nebula.base.OptionLoader;

/**
 * @author ueyudiud
 */
public interface ILocalizationProvider
{
	boolean loadLocalization(LanguageManager manager, Map<String, String> localization);
}

enum CheckStatus
{
	FINE,
	LOCAL_ONLY,
	OUT_DATE,
	SKIP
}

class GitLocalizationProvider implements ILocalizationProvider
{
	/**
	 * The Gson used to parse SHA of localization files.
	 */
	static final Gson GSON = new GsonBuilder().registerTypeAdapter(String.class, SHAAdapter.INSTANCE).create();
	static final char[] CACHE = new char[1024];
	
	String modid;
	String key;
	String path;
	String branch;
	
	GitLocalizationProvider(String modid, String key, String path, String branch)
	{
		this.modid = modid;
		this.key = key;
		this.path = path;
		this.branch = branch;
	}
	
	private static BufferedReader openReader(URL url) throws IOException
	{
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(2000);
		connection.setReadTimeout(25000);
		return new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
	}
	
	private static void writeString(Writer writer, String string) throws IOException
	{
		int length = string.length();
		string.getChars(0, length, CACHE, 0);
		writer.write(length);
		writer.write(CACHE, 0, length);
	}
	
	@Nullable
	private static String readString(Reader reader) throws IOException
	{
		int length = reader.read();
		if (length == -1)
			return null;
		if (reader.read(CACHE, 0, length) != length)
			throw new IOException("Unexpected length!");
		return new String(CACHE, 0, length);
	}
	
	private CheckStatus checkLocalLocalizationLatest(File file, String sha)
	{
		if (!file.exists())
		{
			return CheckStatus.OUT_DATE;
		}
		try (FileReader reader = new FileReader(file))
		{
			return sha.equals(readString(reader)) ? CheckStatus.FINE : CheckStatus.OUT_DATE;
		}
		catch (IOException exception)
		{
			return CheckStatus.OUT_DATE;
		}
	}
	
	private Pair<CheckStatus, String> checkLocalizationLatest(File file)
	{
		if (!V.networkLocalizationUpdate)
		{
			return Pair.of(CheckStatus.SKIP, null);
		}
		
		try
		{
			URL url = new URL("https://api.github.com/repos/" + this.path + "/contents/lang?ref=" + this.branch);
			String key;
			try (JsonReader reader = new JsonReader(openReader(url)))
			{
				key = GSON.fromJson(reader, String.class);
			}
			return Pair.of(checkLocalLocalizationLatest(file, key), key);
		}
		catch (IOException exception)
		{
			return Pair.of(CheckStatus.LOCAL_ONLY, null);
		}
	}
	
	@Override
	public boolean loadLocalization(LanguageManager manager, Map<String, String> localization)
	{
		File file = new File(manager.saveFile(), manager.locale() + "_" + this.modid + "_lang");
		try
		{
			Pair<CheckStatus, String> tuple = checkLocalizationLatest(file);
			Map<String, String> map;
			switch (tuple.getLeft())
			{
			case LOCAL_ONLY :
				manager.info("Can not connect to server, use local file instead.");
			case FINE :
				try (BufferedReader reader = new BufferedReader(new FileReader(file)))
				{
					readString(reader);
					int keyCount = reader.read() << 16 | reader.read();
					for (int i = 0; i < keyCount; ++i)
					{
						String key = readString(reader);
						String value = readString(reader);
						localization.put(key, value);
					}
					manager.info("Loaded {} localization entries from {}", keyCount, file);
				}
				break;
			case OUT_DATE :
				URL url = new URL("https://raw.githubusercontent.com/" + this.path + "/" + this.branch + "/lang/" + manager.locale() + ".lang");
				manager.info("Downloading localization file from {}, it may takes some times, please waiting...", url);
				map = new HashMap<>();
				try (BufferedReader reader = openReader(url))
				{
					OptionLoader loader = new OptionLoader(reader);
					Pair<String, String> pair = loader.readPair();
					map.put(pair.getKey(), pair.getValue());
				}
				localization.putAll(map);
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
				{
					writer.write(tuple.getRight().length());
					writer.write(tuple.getRight());
					writer.write(map.size() >> 1);
					writer.write(map.size());
					for (Entry<String, String> pair : map.entrySet())
					{
						writeString(writer, pair.getKey());
						writeString(writer, pair.getValue());
					}
				}
				break;
			default:
				return false;
			}
			return true;
		}
		catch (IOException exception)
		{
			manager.info("Failed to connect localization file '{}' from {}", manager.locale(), this.path);
			return false;
		}
		catch (JsonParseException exception)
		{
			manager.info("The localization file '{}.lang' does not exist yet.", file);
			return false;
		}
	}
}

class SHAAdapter extends TypeAdapter<String>
{
	static final SHAAdapter INSTANCE = new SHAAdapter();
	
	String name;
	
	@Override
	public void write(JsonWriter out, String value) throws IOException
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String read(JsonReader in) throws IOException
	{
		in.beginArray();
		while (true)
		{
			if (in.peek() == JsonToken.END_ARRAY)
			{
				break;
			}
			in.beginObject();
			in.nextName();//name:
			if (this.name.equals(in.nextString()))
			{
				in.skipValue();//path:
				in.skipValue();
				in.skipValue();//sha:
				return in.nextString();
			}
			else
			{
				while (in.peek() != JsonToken.END_OBJECT)
				{
					in.skipValue();
					in.skipValue();
				}
				in.endObject();
			}
		}
		throw new IOException(this.name + " not found");
	}
}
