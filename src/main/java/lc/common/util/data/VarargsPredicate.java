package lc.common.util.data;

/**
 * Represents a predicate (boolean-valued function) of multiple arguments. This
 * is a functional interface whose functional method is test(Object[]).
 * 
 * @author AfterLifeLochie
 *
 * @param <T>
 *            the type of the input(s) to the predicate
 */
public interface VarargsPredicate<T> {

	/**
	 * Evaluates this predicate on the given argument.
	 * 
	 * @param t
	 *            the input argument(s)
	 * @return true if the input argument(s) matches the predicate, otherwise
	 *         false
	 */
	public boolean test(T[] t);

}
