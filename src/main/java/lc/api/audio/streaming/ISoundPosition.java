/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.audio.streaming;

/**
 * Interface for sound position descriptors at runtime.
 * 
 * @author AfterLifeLochie
 *
 */
public interface ISoundPosition {

	/**
	 * Get the object which represents the world associated with the playback
	 * location of the sound. This will usually be a World or WorldServer
	 * object.
	 * 
	 * @return The World or WorldServer object for this sound's playback
	 *         location.
	 */
	public Object getWorldObject();

	/**
	 * Get the object which represents the cartesian coordinates associated with
	 * the playback location of the sound. This will usually be a Vector3
	 * object.
	 * 
	 * @return The Vector3 location of this sound's playback location.
	 */
	public Object getPositionObject();

}
