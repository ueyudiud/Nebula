/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.function;

import javax.annotation.Nullable;

/**
 * @author ueyudiud
 */
@FunctionalInterface
public interface UnsafeFunction<T, R>
{
	@Nullable R apply(@Nullable T t) throws Throwable;
}
