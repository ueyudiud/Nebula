/*
 * copyright 2016-2018 ueyudiud
 */
package nebula;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NebulaLog
{
	private static Logger logLogger = LogManager.getLogger("Nebula Log");
	
	public static void catching(Throwable t)
	{
		logger().catching(t);
	}
	
	public static void error(String message, Object...formats)
	{
		logger().error(message, formats);
	}
	
	public static void error(String message, Throwable t)
	{
		logger().error(message, t);
	}
	
	public static void warn(String message, Object...formats)
	{
		logger().warn(message, formats);
	}
	
	public static void warn(String message, Throwable t)
	{
		logger().warn(message, t);
	}
	
	public static void info(String message, Object...formats)
	{
		logger().info(message, formats);
	}
	
	public static void debug(String message, Object...formats)
	{
		logger().debug(message, formats);
	}
	
	public static void trace(String message, Object...formats)
	{
		logger().trace(message, formats);
	}
	
	public static Logger logger;
	
	public static Logger logger()
	{
		return V.log != null ? V.log : logLogger;
	}
}
