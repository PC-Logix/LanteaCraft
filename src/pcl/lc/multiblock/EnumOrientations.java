package pcl.lc.multiblock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import pcl.lc.network.IStreamPackable;
import pcl.lc.network.LanteaPacket;
import pcl.lc.util.Vector3;

/**
 * Declaration of all valid multi-block orientation types and their ordinals.
 * 
 * @author AfterLifeLochie
 * 
 */
public enum EnumOrientations {
	NORTH(1, "North"), EAST(2, "East"), SOUTH(3, "South"), WEST(4, "West"), // Cardinals
	NORTH_SOUTH(5, "North-South"), EAST_WEST(6, "East-West"); // Through XX YY

	private final int id;
	private final String name;

	/**
	 * Constructs an orientation.
	 * 
	 * @param id
	 *            The ID of the orientation.
	 * @param name
	 *            The string name of the orientation.
	 */
	EnumOrientations(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Gets the UID for this orientation object.
	 * 
	 * @return The orientation's UID.
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Gets an orientation or null from a UID.
	 * 
	 * @param id
	 *            The ID to search for.
	 * @return The orientation, or, null.
	 */
	public static EnumOrientations getOrientationFromID(int id) {
		for (EnumOrientations orientation : values())
			if (orientation.getID() == id) return orientation;
		return null;
	}

	/**
	 * Gets the name for this orientation object.
	 * 
	 * @return The orientation's human-readable name.
	 */
	public String getName() {
		return this.name;
	}

}
