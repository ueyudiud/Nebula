/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import nebula.V;
import nebula.base.M;
import nebula.base.OptionLoader;
import nebula.common.annotations.ModSensitive;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * The language manager.
 * @author ueyudiud
 */
public class LanguageManager
{
	/** The default locale of manager. */
	public static final String ENGLISH	= "en_US";
	/** Localization map loaded from language file. */
	static final Map<String, String> MAP1 = new HashMap<>();
	/** Localization map loaded from byte code. */
	static final Map<String, Map<String, String>> MAP2 = new HashMap<>();
	
	static final List<Pair<String, ILocalizationProvider>> PROVIDERS = new ArrayList<>(4);
	
	private static String locale = null;
	
	@ModSensitive
	public static void registerLocal(String unlocalization, String localization)
	{
		registerLocal(G.activeModid(), unlocalization, localization);
	}
	
	public static void registerLocal(String modid, String unlocalization, String localization)
	{
		M.put(MAP2, modid, unlocalization, localization);
	}
	
	public static void registerMultiLocal(String modid, String unlocalization, String...localizations)
	{
		Map<String, String> map = M.get(MAP2, modid);
		Map<String, String> values = new HashMap<>(localizations.length, 1.0F);
		for (int i = 0; i < localizations.length; ++i)
		{
			values.put(unlocalization + '.' + i, localizations[i]);
		}
		map.putAll(values);
	}
	
	@ModSensitive
	public static void registerGitLocalProvider(String key, String path, String branch)
	{
		String modid = G.activeModid();
		if (modid == V.MODID_MC)
		{
			throw new IllegalStateException();
		}
		PROVIDERS.add(Pair.of(modid, new GitLocalizationProvider(modid, key, path, branch)));
	}
	
	@ModSensitive
	public static void registerLocalProvider(ILocalizationProvider provider)
	{
		String modid = G.activeModid();
		if (modid == V.MODID_MC)
		{
			throw new IllegalStateException();
		}
		PROVIDERS.add(Pair.of(modid, provider));
	}
	
	public static String translateLocal(String unlocalized, Object...formats)
	{
		String localized = MAP1.get(unlocalized);
		if (localized != null)
		{
			try
			{
				return String.format(localized, formats);
			}
			catch (Exception exception)
			{
				return "<translated error: " + unlocalized + ">";
			}
		}
		else
		{
			return I18n.translateToLocalFormatted(unlocalized, formats);
		}
	}
	
	public static String translateLocal(String unlocalized)
	{
		String localized = MAP1.get(unlocalized);
		if (localized != null)
		{
			return localized;
		}
		else
		{
			return I18n.translateToLocal(unlocalized);
		}
	}
	
	@Nullable
	public static String translateLocalWithIgnoreUnmapping(String unlocalized, Object...formats)
	{
		String localized = MAP1.get(unlocalized);
		if (localized != null)
		{
			try
			{
				return String.format(localized, formats);
			}
			catch (Exception exception)
			{
				return "<translated error: " + unlocalized + ">";
			}
		}
		else if (I18n.canTranslate(unlocalized))
		{
			return I18n.translateToLocalFormatted(unlocalized, formats);
		}
		else
		{
			return null;
		}
	}
	
	private final File file;
	private final PrintStream stream;
	private String owner = null;
	
	@SuppressWarnings("resource")
	public LanguageManager(File file)
	{
		file.mkdirs();
		this.file = file;
		File f1 = new File(G.logFile(), "lang.log");
		PrintStream stream;
		try
		{
			f1.createNewFile();
			stream = new PrintStream(f1);
		}
		catch (IOException exception)
		{
			V.error("Can not create language log.");
			stream = System.out;
		}
		this.stream = stream;
	}
	
	public String locale()
	{
		return FMLCommonHandler.instance().getCurrentLanguage();
	}
	
	public File saveFile()
	{
		return this.file;
	}
	
	private String owner()
	{
		return this.owner == null ? "LM" : this.owner;
	}
	
	public void info(String msg, Object...formats)
	{
		V.info(msg, formats);
		this.stream.print("[" + V.time() + "] [" + owner() + "/INFO]: ");
		this.stream.println(ParameterizedMessageFactory.INSTANCE.newMessage(msg, formats).getFormattedMessage());
	}
	
	public void warn(String msg, Object...formats)
	{
		V.info(msg, formats);
		this.stream.print("[" + V.time() + "] [" + owner() + "/WARN]: ");
		this.stream.println(ParameterizedMessageFactory.INSTANCE.newMessage(msg, formats).getFormattedMessage());
	}
	
	public void catching(Throwable throwable)
	{
		V.catching(throwable);
		throwable.printStackTrace(this.stream);
	}
	
	public void save(boolean force)
	{
		File source = new File(this.file, ENGLISH + ".lang");
		if (!force && source.exists())
		{
			return;
		}
		
		info("Saving localization files.");
		
		try
		{
			source.createNewFile();
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(source)))
			{
				for (Entry<String, Map<String, String>> entry : MAP2.entrySet())
				{
					writer.write("# ");
					writer.write(entry.getKey());
					writer.newLine();
					for (Entry<String, String> entry2 : new TreeMap<>(entry.getValue()).entrySet())
					{
						writer.write(entry2.getKey());
						writer.write('=');
						writer.write(entry2.getValue());
						writer.newLine();
					}
				}
			}
		}
		catch (IOException exception)
		{
			catching(exception);
		}
		
		info("Saved {} localization entries to files.", MAP2.size());
	}
	
	public void load(boolean force)
	{
		final String current = locale();
		
		if (!force && current.equals(locale))
		{
			return;
		}
		
		info("Loading localization files.");
		
		synchronized (MAP1)
		{
			MAP1.clear();
			
			if (ENGLISH.equals(current))
			{
				for (Map<String, String> map : MAP2.values())
				{
					MAP1.putAll(map);
				}
			}
			
			for (Pair<String, ILocalizationProvider> pair : PROVIDERS)
			{
				this.owner = pair.getKey();
				pair.getValue().loadLocalization(this, MAP1);
			}
			this.owner = null;
			
			File source = new File(this.file, locale() + ".lang");
			if (source.canRead())
			{
				try
				{
					try (BufferedReader reader = new BufferedReader(new FileReader(source)))
					{
						OptionLoader loader = new OptionLoader(reader);
						Pair<String, String> pair;
						while ((pair = loader.readPair()) != null)
						{
							MAP1.put(pair.getKey(), pair.getValue());
						}
					}
				}
				catch (IOException exception)
				{
					catching(exception);
				}
			}
			
			info("Loaded {} localization entries from files.", MAP1.size());
		}
		
		locale = current;
	}
}
