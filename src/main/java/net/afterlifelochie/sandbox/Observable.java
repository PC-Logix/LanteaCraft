package net.afterlifelochie.sandbox;

public class Observable {

	private volatile boolean modified;
	private volatile Observable parent;

	public Observable(Observable parent) {
		this.parent = parent;
	}

	protected void modify() {
		this.modified = true;
		if (this.parent != null)
			this.parent.modify();
	}

	public boolean modified() {
		return this.modified;
	}

	public void clearModified() {
		this.modified = false;
	}

}
