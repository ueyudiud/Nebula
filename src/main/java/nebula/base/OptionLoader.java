/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base;

import java.io.BufferedReader;
import java.io.IOException;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author ueyudiud
 */
public class OptionLoader
{
	protected final BufferedReader reader;
	protected final char commentChar;
	
	public OptionLoader(BufferedReader reader)
	{
		this(reader, '#');
	}
	public OptionLoader(BufferedReader reader, char commentChar)
	{
		this.reader = reader;
		this.commentChar = commentChar;
	}
	
	/**
	 * Read next non-comment line, return <code>null</code> when
	 * stream have been reached.
	 * 
	 * @return the next non-comment line.
	 * @throws IOException if an I/O error occurs
	 */
	@Nullable
	public String readOption() throws IOException
	{
		synchronized (this.reader)
		{
			String line;
			while ((line = this.reader.readLine()) != null &&
					(line.length() == 0 || line.charAt(0) == '#'));
			return line;
		}
	}
	
	/**
	 * Read next non-comment key-value pair, return <code>null</code> when
	 * stream have been reached.
	 * 
	 * @return the next non-comment key-value pair.
	 * @throws IOException if an I/O error occurs
	 */
	@Nullable
	public Pair<String, String> readPair() throws IOException, IllegalArgumentException
	{
		String line = readOption();
		if (line == null)
			return null;
		int idx = line.indexOf('=');
		if (idx == -1)
			throw new IllegalArgumentException("'=' not found!");
		return Pair.of(line.substring(0, idx - 1), line.substring(idx + 1));
	}
}
