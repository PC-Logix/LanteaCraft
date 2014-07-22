package pcl.lc.client.audio;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.lc.util.Vector3;

public class AudioPosition {

	public World world;
	public Vector3 position;

	public static AudioPosition from(Object o) {
		if (o instanceof AudioPosition)
			return (AudioPosition) o;
		if (o instanceof Entity) {
			Entity e = (Entity) o;
			return new AudioPosition(e.worldObj, new Vector3(e));
		}
		if (o instanceof TileEntity) {
			TileEntity t = (TileEntity) o;
			return new AudioPosition(t.getWorldObj(), new Vector3(t));
		}
		return null;
	}

	public AudioPosition(World world, Vector3 position) {
		if (world == null || position == null)
			throw new IllegalArgumentException("Parameters world and position may not be null.");
		this.world = world;
		this.position = position;
	}

}
