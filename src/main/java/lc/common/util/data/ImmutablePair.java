package lc.common.util.data;

/**
 * A instance of an immutable pair. The A-B pair represented by this object are
 * immutable, but may not be immutable themselves.
 *
 * @author AfterLifeLochie
 *
 * @param <A>
 *            The first type of value in the immutable pair
 * @param <B>
 *            The second type of value in the immutable pair
 */
public class ImmutablePair<A, B> {
	private final A a;
	private final B b;

	/**
	 * Creates an ImmutablePair
	 *
	 * @param a
	 *            The A object
	 * @param b
	 *            The B object
	 */
	public ImmutablePair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Fetch the A object
	 *
	 * @return The A object
	 */
	public A getA() {
		return a;
	}

	/**
	 * Fetch the B object
	 *
	 * @return The B object
	 */
	public B getB() {
		return b;
	}

	@Override
	public int hashCode() {
		int aa = a != null ? a.hashCode() : 0;
		int bb = b != null ? b.hashCode() : 0;
		return (aa + bb) * aa + bb;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ImmutablePair) {
			ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) other;
			return this.a.equals(that.a) && this.b.equals(that.b);
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + a.toString() + ", " + b.toString() + ")";
	}
}