package gcewing.sg.core;

public enum SGIrisState {
	Error, Open, Closed, Opening, Closing;

	static SGIrisState[] VALUES = values();

	public static SGIrisState valueOf(int i) {
		try {
			return VALUES[i];
		} catch (IndexOutOfBoundsException e) {
			return Error;
		}
	}
}
