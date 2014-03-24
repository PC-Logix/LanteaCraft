package pcl.lc.tileentity;

import java.io.DataOutputStream;
import java.io.IOException;

import pcl.common.network.ModPacket;
import pcl.common.network.TinyModPacket;
import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityLanteaDecorGlass extends TileEntity {

	private boolean[] state = new boolean[6];

	public TileEntityLanteaDecorGlass getGlassAt(int x, int y, int z) {
		if (y >= 0 && worldObj.getHeight() > y) {
			TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
			if (tile instanceof TileEntityLanteaDecorGlass) {
				return (TileEntityLanteaDecorGlass) tile;
			}
		}
		return null;
	}

	public void neighbourChanged() {
		Vector3 location = new Vector3(this);
		for (int dir = 0; dir < 6; dir++) {
			Vector3 fd = location.add(new Vector3(ForgeDirection.getOrientation(dir)));
			TileEntityLanteaDecorGlass that = getGlassAt(fd.floorX(), fd.floorY(), fd.floorZ());
			state[dir] = (that != null);
		}
	}

	public boolean renderSide(int dir) {
		if (0 > dir || dir > 6)
			return false;
		return !state[dir];
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}

	@Override
	public void updateEntity() {
	}

	@Override
	public Packet getDescriptionPacket() {
		try {
			TinyModPacket packet = new TinyModPacket();
			DataOutputStream stream = packet.getOut();
			stream.writeInt(xCoord);
			stream.writeInt(yCoord);
			stream.writeInt(zCoord);
			for (int i = 0; i < 6; i++)
				stream.write((state[i]) ? 1 : 0);
			return packet.toPacket();
		} catch (IOException ioex) {
			LanteaCraft.getLogger().log(Level.WARNING, "Error creating description packet.", ioex);
			return null;
		}
	}

}
