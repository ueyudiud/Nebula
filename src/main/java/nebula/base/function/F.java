/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import nebula.base.collection.A;

/**
 * @author ueyudiud
 */
@ParametersAreNonnullByDefault
public final class F
{
	private F() { }
	
	public static final Judgable
	P_T = True.INSTANCE,
	P_F = False.INSTANCE,
	P_ANY = NonNull.INSTANCE,
	P_NULL = Null.INSTANCE;
	public static final BooleanSupplier
	BS_T = () -> true,
	BS_F = () -> false;
	
	public static <T> Judgable<T> equal(@Nullable T t) { return t == null ? P_NULL : new ObjEqual(t); }
	public static <T> Judgable<T> and(Predicate<? super T>... predicates)
	{
		List<Predicate<? super T>> list = new ArrayList<>(predicates.length);
		for (Predicate<? super T> predicate : predicates)
		{
			if (predicate instanceof And)
			{
				list.addAll(A.argument(((And) predicate).predicates));
			}
			else
			{
				list.add(predicate);
			}
		}
		return new And(list.toArray(new Predicate[list.size()]));
	}
	public static <T> Judgable<T> or(Predicate<? super T>... predicates)
	{
		List<Predicate<? super T>> list = new ArrayList<>(predicates.length);
		for (Predicate<? super T> predicate : predicates)
		{
			if (predicate instanceof Or)
			{
				list.addAll(A.argument(((Or) predicate).predicates));
			}
			else
			{
				list.add(predicate);
			}
		}
		return new Or(list.toArray(new Predicate[list.size()]));
	}
	
	/**
	 * Create a predicate with source of Iterable, use <tt>and</tt> for testing.<p>
	 * Different from {@link #and(Predicate...)}, the predicate will use source from
	 * source predicates instead of create a copied data from source.
	 * @param iterable the source predicates.
	 * @return the combined predicate.
	 */
	public static <T> Judgable<T> and(Iterable<? extends Judgable<? super T>> iterable)
	{
		Objects.requireNonNull(iterable);
		return t -> { for (Judgable<? super T> p : iterable) if (!p.test(t)) return false; return true; };
	}
	/**
	 * Create a predicate with source of Iterable, use <tt>or</tt> for testing.<p>
	 * Different from {@link #or(Predicate...)}, the predicate will use source from
	 * source predicates instead of create a copied data from source.
	 * @param iterable the source predicates.
	 * @return the combined predicate.
	 */
	public static <T> Judgable<T> or(Iterable<? extends Judgable<? super T>> iterable)
	{
		Objects.requireNonNull(iterable);
		return t -> { for (Judgable<? super T> p : iterable) if (p.test(t)) return true; return false; };
	}
	
	public static <I, O> Function<I, O> cast(com.google.common.base.Function<I, O> function) { return function::apply; }
	public static <I, O> com.google.common.base.Function<I, O> cast(Function<I, O> function) { return function::apply; }
	
	public static <O> Supplier<O> anys(@Nullable O result) { return Applicable.to(result); }
	public static <I, O> Function<I, O> anyf(@Nullable O result) { return any -> result; }
	public static <I, O> Function<I, O> anyf(Supplier<? extends O> result) { return any -> result.get(); }
	
	public static <O> Supplier<O> saftys(UnsafeSupplier<? extends O> supplier) { return () -> { try { return supplier.get(); } catch (RuntimeException e) { throw e; } catch (Throwable t) { throw new InternalError(t);} }; }
	public static <I, O> Function<I, O> saftyf1(UnsafeFunction<? super I, ? extends O> function) { return i -> { try { return function.apply(i); } catch (RuntimeException e) { throw e; } catch (Throwable t) { throw new InternalError(t);} }; }
	
	public static <I, O> Supplier<O> const1f(Function<I, O> function, @Nullable I constant) { return () -> function.apply(constant);}
	public static <I1, I2, O> Supplier<O> const1f(BiFunction<I1, I2, O> function, @Nullable I1 constant1, @Nullable I2 constant2) { return () -> function.apply(constant1, constant2); }
	public static <I ,     O> IntFunction<O> const1fi(ObjIntFunction<I, O>  function, @Nullable I  constant) { return in -> function.apply(constant, in); }
	public static <I1, I2, O> Function<I2, O> const1f(BiFunction<I1, I2, O> function, @Nullable I1 constant) { return in -> function.apply(constant, in); }
	public static <I1, I2, O> Function<I1, O> const2f(BiFunction<I1, I2, O> function, @Nullable I2 constant) { return in -> function.apply(in, constant); }
	public static <I1, I2> Consumer<I2> const1c(BiConsumer<I1, I2> function, @Nullable I1 constant) { return in -> function.accept(constant, in); }
	public static <I1, I2> Consumer<I1> const2c(BiConsumer<I1, I2> function, @Nullable I2 constant) { return in -> function.accept(in, constant); }
	
	public static <I, O> Function<Function<I, O>, O> func(I constant) { return const2f(Function::apply, constant); }
	
	public static <I1, I2> Judgable<I2> const1p(BiPredicate<I1, I2> function, @Nullable I1 constant) { return in -> function.test(constant, in); }
	public static <I1, I2> Judgable<I1> const2p(BiPredicate<I1, I2> function, @Nullable I2 constant) { return in -> function.test(in, constant); }
	
	public static <I, O> Function<I, O> toNullf() { return in -> null; }
}
