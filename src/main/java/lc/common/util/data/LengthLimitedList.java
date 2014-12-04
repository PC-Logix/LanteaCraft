package lc.common.util.data;

import java.util.ArrayList;

/**
 * Implementation of ArrayList<T> with a maximum size clamp.
 *
 * @author AfterLifeLochie
 *
 * @param <T>
 *            The type of the list.
 */
public class LengthLimitedList<T> extends ArrayList<T> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1925059441036970161L;
	private final int MAX_SIZE;

	/**
	 * Create a list with an immutable maximum size.
	 *
	 * @param size
	 *            The immutable maximum size of the list.
	 */
	public LengthLimitedList(int size) {
		this.MAX_SIZE = size;
	}

	/**
	 * Determines if this list is allowed to contain any further elements beyond
	 * it's current size. If at or above the limit, new items may not be added.
	 *
	 * @return If this list can accept more items.
	 */
	public final boolean canAddElements() {
		return !(size() >= MAX_SIZE);
	}

	@Override
	public final boolean add(T e) {
		if (size() >= MAX_SIZE)
			throw new IllegalStateException("List is full.");
		return super.add(e);
	}

}
