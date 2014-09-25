package lc.common.util.data;

/**
 * A instance of an immutable tuple. The A-B-C pair represented by this object
 * are immutable, but may not be immutable themselves.
 * 
 * @author AfterLifeLochie
 * 
 * @param <A>
 *            The first type of value in the immutable tuple
 * @param <B>
 *            The second type of value in the immutable tuple
 * @param <C>
 *            The third type of value in the immutable tuple
 */
public class ImmutableTuple<A, B, C> {

	/** The A value */
	private final A a;
	/** The B value */
	private final B b;
	/** The C value */
	private final C c;

	/**
	 * Create a new tuple
	 * 
	 * @param a
	 *            The a value
	 * @param b
	 *            The b value
	 * @param c
	 *            The c value
	 */
	public ImmutableTuple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	/**
	 * Fetch the A value of this tuple
	 * 
	 * @return The first value of this tuple
	 */
	public A getA() {
		return a;
	}

	/**
	 * Fetch the B value of this tuple
	 * 
	 * @return The second value of this tuple
	 */
	public B getB() {
		return b;
	}

	/**
	 * Fetch the C value of this tuple
	 * 
	 * @return The third value of this tuple
	 */
	public C getC() {
		return c;
	}

	@Override
	public int hashCode() {
		int aa = a != null ? a.hashCode() : 0;
		int bb = b != null ? b.hashCode() : 0;
		int cc = c != null ? c.hashCode() : 0;
		return (aa * bb * cc) * (aa + bb + cc);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ImmutableTuple) {
			ImmutableTuple<?, ?, ?> that = (ImmutableTuple<?, ?, ?>) other;
			return (this.a.equals(that.a) && this.b.equals(that.b) && this.c.equals(that.c));
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + a.toString() + ", " + b.toString() + ", " + c.toString() + ")";
	}

}
