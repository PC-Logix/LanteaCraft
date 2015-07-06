package lc.common.util.data;

import java.util.ArrayList;

public class WindowedArrayList<T> extends ArrayList<T> {

	private final int MAX_SIZE;

	/**
	 * Create a list with an immutable maximum size.
	 *
	 * @param size
	 *            The immutable maximum size of the list.
	 */
	public WindowedArrayList(int size) {
		super();
		this.MAX_SIZE = size;
	}

	@Override
	public final boolean add(T e) {
		if (size() + 1 >= MAX_SIZE)
			super.remove(0);
		return super.add(e);
	}

}
