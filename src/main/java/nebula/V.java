/*
 * copyright 2016-2018 ueyudiud
 */
package nebula;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * @author ueyudiud
 */
public class V
{
	static Logger log;
	
	public static final String MODID_MC = "minecraft";
	public static final String MODID_JEI = "JEI";
	
	public static final boolean[]	BOOLS_EMPTY		= {};
	public static final byte[]		BYTES_EMPTY		= {};
	public static final short[]		SHORTS_EMPTY	= {};
	public static final int[]		INTS_EMPTY		= {};
	public static final long[]		LONGS_EMPTY		= {};
	public static final float[]		FLOATS_EMPTY	= {};
	public static final double[]	DOUBLES_EMPTY	= {};
	public static final char[]		CHARS_EMPTY		= {};
	public static final Object[]	OBJECTS_EMPTY	= {};
	
	public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	
	public static final IBlockState AIR = Blocks.AIR.getDefaultState();
	
	public static final Capability<IItemHandler> CAPABILITY_ITEM = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	public static final Capability<IFluidHandler> CAPABILITY_FLUID = CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	
	public static final int GENERAL_MAX_STACK_SIZE = 64;
	
	public static DateFormat dateFormat = DateFormat.getTimeInstance();
	
	public static boolean networkLocalizationUpdate;
	
	/**
	 * Char formatting.
	 * @author ueyudiud
	 */
	public static enum CF
	{
		BLACK('0'),
		DARK_BLUE('1'),
		DARK_GREEN('2'),
		DARK_AQUA('3'),
		DARK_RED('4'),
		DARK_PURPLE('5'),
		GOLD('6'),
		GRAY('7'),
		DARK_GRAY('8'),
		BLUE('9'),
		GREEN('a'),
		AQUA('b'),
		RED('c'),
		LIGHT_PURPLE('d'),
		YELLOW('e'),
		WHITE('f'),
		OBFUSCATED('k', true),
		BOLD('l', true),
		STRIKETHROUGH('m', true),
		UNDERLINE('n', true),
		ITALIC('o', true),
		RESET('r');
		
		private final char		controlCode;
		private final boolean	isNotColor;
		private final String	opcodeName;
		
		CF(char controlCode                    ) { this(controlCode, false); }
		CF(char controlCode, boolean isNotColor)
		{
			this.controlCode = controlCode;
			this.isNotColor = isNotColor;
			this.opcodeName = "\u00a7" + controlCode;
		}
		
		public char getControlCode()
		{
			return this.controlCode;
		}
		
		public boolean isNotColor() { return this.isNotColor; }
		public boolean isColor   () { return !this.isNotColor && this != RESET; }
		
		@Override
		public String toString()
		{
			return this.opcodeName;
		}
	}
	
	public static String time() { return dateFormat.format(new Date()); }
	
	public static void debug(String msg, Object...formats) { log.debug(msg, formats); }
	public static void info (String msg, Object...formats) { log.info (msg, formats); }
	public static void warn (String msg, Object...formats) { log.warn (msg, formats); }
	public static void error(String msg, Object...formats) { log.error(msg, formats); }
	
	public static void catching(Throwable throwable) { log.catching(throwable); }
	
	public static <A> A cast(Object any) { return (A) any; }
}
