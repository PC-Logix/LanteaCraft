package gcewing.sg.core;

public class AddressingException extends Exception {

	public class AddressNotFoundException extends Exception {

	}

	public AddressingException(String reason) {
		super(reason);
	}

	public AddressingException(String reason, Throwable cause) {
		super(reason, cause);
	}

	public static class DatabaseException extends AddressingException {
		public DatabaseException(String reason) {
			super(reason);
		}

		public DatabaseException(String reason, Throwable cause) {
			super(reason, cause);
		}
	}

	public static class AddressAlreadyInUseException extends AddressingException {
		public AddressAlreadyInUseException(String reason) {
			super(reason);
		}

		public AddressAlreadyInUseException(String reason, Throwable cause) {
			super(reason, cause);
		}
	}

}
