package pcl.lc.core;

public class AddressingError extends Exception {
	public AddressingError() {
		super();
	}

	public AddressingError(String reason) {
		super(reason);
	}

	public AddressingError(String reason, Throwable thrown) {
		super(reason, thrown);
	}

	public static class CoordRangeError extends AddressingError {
		public CoordRangeError() {
			super();
		}

		public CoordRangeError(String reason) {
			super(reason);
		}

		public CoordRangeError(String reason, Throwable thrown) {
			super(reason, thrown);
		}
	}

	public static class DimensionRangeError extends AddressingError {
		public DimensionRangeError() {
			super();
		}

		public DimensionRangeError(String reason) {
			super(reason);
		}

		public DimensionRangeError(String reason, Throwable thrown) {
			super(reason, thrown);
		}
	}
}
