package pcl.common.audio;

import pcl.common.util.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class AudioPosition {
	
	public World world;
	public Vector3 position;
	
	public static AudioPosition from(Object o) {
		if (o instanceof AudioPosition)
			return (AudioPosition) o;
		if (o instanceof Entity) {
			Entity e = (Entity) o;
			return new AudioPosition(e.worldObj, new Vector3(e.posX, e.posY, e.posZ));
		}
		if (o instanceof TileEntity) {
			TileEntity t = (TileEntity) o;
			return new AudioPosition(t.worldObj, new Vector3(t.xCoord + 0.5f, t.yCoord + 0.5f, t.zCoord + 0.5f));
		}
		return null;
	}
	
	public AudioPosition(World world, Vector3 position) {
		this.world = world;
		this.position = position;
	}
	

}
