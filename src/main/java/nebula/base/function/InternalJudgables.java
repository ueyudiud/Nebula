/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.function;

import java.util.function.Predicate;

import com.google.common.collect.ObjectArrays;

final class True implements Judgable<Object>
{
	static final True INSTANCE = new True();
	@Override public boolean test(Object t) { return true; }
	@Override public Judgable<Object> negate() { return False.INSTANCE; }
}

final class False implements Judgable<Object>
{
	static final False INSTANCE = new False();
	@Override public boolean test(Object t) { return false; }
	@Override public Judgable<Object> negate() { return True.INSTANCE; }
}

final class Null implements Judgable<Object>
{
	static final Null INSTANCE = new Null();
	@Override public boolean test(Object t) { return t == null; }
	@Override public Judgable<Object> negate() { return NonNull.INSTANCE; }
}

final class NonNull implements Judgable<Object>
{
	static final NonNull INSTANCE = new NonNull();
	@Override public boolean test(Object t) { return t != null; }
	@Override public Judgable<Object> negate() { return Null.INSTANCE; }
}

final class ObjEqual<T> implements Judgable<T>
{
	final T constant;
	ObjEqual(T constant) { this.constant = constant; }
	@Override public boolean test(T t) { return this.constant.equals(t); }
}

final class And<T> implements Judgable<T>
{
	final Predicate<? super T>[] predicates;
	And(Predicate<? super T>...predicates) { this.predicates = predicates; }
	@Override public boolean test(T t) { for (Predicate<? super T> p : this.predicates) if (!p.test(t)) return false; return true; }
	@Override public And<T> and(Predicate<? super T> other) { return other instanceof And ? new And(ObjectArrays.concat(this.predicates, ((And) other).predicates, Predicate.class)) : new And(ObjectArrays.concat(this.predicates, other)); }
}

final class Or<T> implements Judgable<T>
{
	final Predicate<? super T>[] predicates;
	Or(Predicate<? super T>...predicates) { this.predicates = predicates; }
	@Override public boolean test(T t) { for (Predicate<? super T> p : this.predicates) if (p.test(t)) return true; return false; }
	@Override public Or<T> or(Predicate<? super T> other) { return other instanceof Or ? new Or(ObjectArrays.concat(this.predicates, ((Or) other).predicates, Predicate.class)) : new Or(ObjectArrays.concat(this.predicates, other)); }
}
