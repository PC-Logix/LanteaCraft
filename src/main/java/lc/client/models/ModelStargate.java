package lc.client.models;

import lc.client.opengl.BufferDisplayList;

/**
 * Stargate model factory
 * 
 * @author AfterLifeLochie
 * 
 */
public class ModelStargate {

	/** Display buffer for outer shell */
	private final BufferDisplayList listShell = new BufferDisplayList();
	/** Display buffer for chevron */
	private final BufferDisplayList listChevron = new BufferDisplayList();
	/** Display buffer for inner ring */
	private final BufferDisplayList listRing = new BufferDisplayList();

	public ModelStargate() {

	}
}
