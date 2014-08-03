package pcl.lc.api.security;

public enum EnumSecurityAction {

	/**
	 * Place a block in a way which might modify a multi-block structure or
	 * other facility which is sensitive to block placements.
	 */
	PLACE_BLOCK,
	/**
	 * Remove a block in away which might modify a multi-block structure or
	 * other facility which is sensitive to block placements.
	 */
	REMOVE_BLOCK,

	/**
	 * View or open a GUI or screen element.
	 */
	VIEW_INTERFACE,
	/**
	 * Modify the configuration or inventory of a GUI. Does not affect the
	 * player's ability to tab through the screen(s) or press buttons in the
	 * interfaces which don't modify settings.
	 */
	MODIFY_INTERFACE,

	/**
	 * Modify the permissions in the manager.
	 */
	MODIFY_PERMISSIONS;

}
