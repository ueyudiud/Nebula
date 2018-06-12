/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.client.gui;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The auto-injection of background of GUI, used for GUI to
 * provide background resource location
 * injection for it.
 * @author ueyudiud
 * @see nebula.client.NebulaClientHandler#
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface GuiBackground
{
	/**
	 * The background domain and path.
	 * @see net.minecraft.util.ResourceLocation#ResourceLocation(String)
	 */
	String value();
}
