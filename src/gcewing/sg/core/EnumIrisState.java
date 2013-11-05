package gcewing.sg.core;

public enum EnumIrisState {
	Error, Open, Closed, Opening, Closing;

	static EnumIrisState[] VALUES = values();

	public static EnumIrisState valueOf(int i) {
		try {
			return VALUES[i];
		} catch (IndexOutOfBoundsException e) {
			return Error;
		}
	}
}
