/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.util;

import javax.annotation.Nonnull;

/**
 * Deprecated, use {@link nebula.base.register.IRegisteredNamable} instead.
 * @author ueyudiud
 */
@Deprecated
public interface IRegisteredNameable extends nebula.base.register.IRegisteredNameable
{
	@Nonnull String getRegisteredName();
}
