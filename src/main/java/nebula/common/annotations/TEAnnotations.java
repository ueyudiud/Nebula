/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author ueyudiud
 */
public interface TEAnnotations
{
	@Target(FIELD)
	@Retention(CLASS)
	@interface NBTStore
	{
		String key();
		
		Class<?> format() default Object.class;
	}
	
	@Target(FIELD)
	@Retention(CLASS)
	@interface Synchable
	{
		
	}
}
