/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * For method, use to mark that this method is sensitive for mod, that the
 * {@link net.minecraftforge.fml.common.Loader#activeModContainer() active
 * mod container} may affect the state or result of environment.
 * 
 * @author ueyudiud
 */
@Retention(SOURCE)
@Target({METHOD, CONSTRUCTOR})
public @interface ModSensitive
{
	/**
	 * The exclusive modid array, for these mods, they shouldn't call
	 * this method.
	 * 
	 * @return the black list.
	 */
	String[] blacklist() default {};
}
