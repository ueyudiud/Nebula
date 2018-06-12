/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.function;

import javax.annotation.Nullable;

/**
 * @author ueyudiud
 */
@FunctionalInterface
public interface UnsafeSupplier<T>
{
	@Nullable T get() throws Throwable;
}
