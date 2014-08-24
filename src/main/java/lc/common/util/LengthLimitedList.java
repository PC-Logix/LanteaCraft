package lc.common.util;

import java.util.ArrayList;

public class LengthLimitedList<T> extends ArrayList<T> {

	private final int MAX_SIZE;

	public LengthLimitedList(int size) {
		this.MAX_SIZE = size;
	}

	public final boolean canAddElements() {
		return !(this.size() >= MAX_SIZE);
	}

	@Override
	public final boolean add(T e) {
		if (this.size() >= MAX_SIZE)
			throw new IllegalStateException("List is full.");
		return super.add(e);
	}

}
