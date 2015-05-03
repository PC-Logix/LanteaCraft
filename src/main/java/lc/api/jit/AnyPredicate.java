package lc.api.jit;

/**
 * Represents a predicate (boolean-valued function) of any number of untyped or
 * typed arguments. This is a functional interface whose functional method is
 * test(Object[]).
 * 
 * @author AfterLifeLochie
 */
public interface AnyPredicate {

	/**
	 * The identity true predicate
	 */
	public static final AnyPredicate IDENTITY_TRUE = new AnyPredicate() {
		@Override
		public boolean test(Object[] t) {
			return true;
		}
	};

	/**
	 * The identity false predicate
	 */
	public static final AnyPredicate IDENTITY_FALSE = new AnyPredicate() {
		@Override
		public boolean test(Object[] t) {
			return false;
		}
	};

	/**
	 * Evaluates this predicate on the given argument.
	 * 
	 * @param t
	 *            the input argument(s), if any
	 * @return true if the input argument(s) matches the predicate, otherwise
	 *         false
	 */
	public boolean test(Object[] t);

}
