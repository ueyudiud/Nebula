/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.function;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

/**
 * @author ueyudiud
 */
@FunctionalInterface
@ParametersAreNullableByDefault
public interface TriFunction<I1, I2, I3, O>
{
	@Nullable
	O apply(I1 i1, I2 i2, I3 i3);
}
