package pcl.common.util;

/**
 * A instance of an immutable pair. The A-B pair represented by this object are
 * immutable, but may not be immutable themselves (such as List). ALL GLORY TO
 * THE IMMUTABLE PAIR.
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

	public ImmutablePair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return a;
	}

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
			return (this.a.equals(that.a) && this.b.equals(that.b));
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + a.toString() + ", " + b.toString() + ")";
	}
}