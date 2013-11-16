package gcewing.sg.util;

public class ImmutableTuple<A, B, C> {

	private final A a;
	private final B b;
	private final C c;

	public ImmutableTuple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

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
			ImmutableTuple<?, ?, ?> that = (ImmutableTuple) other;
			return (this.a == that.a && this.b == that.b && this.c == that.c);
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + a.toString() + ", " + b.toString() + ", " + c.toString() + ")";
	}

}
