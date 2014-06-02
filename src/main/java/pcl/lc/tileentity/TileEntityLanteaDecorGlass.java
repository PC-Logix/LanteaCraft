package pcl.lc.tileentity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import pcl.common.network.IPacketHandler;
import pcl.common.network.ModPacket;
import pcl.common.network.TinyModPacket;
import pcl.common.util.Vector3;
import pcl.common.util.WorldLocation;
import pcl.lc.LanteaCraft;

public class TileEntityLanteaDecorGlass extends TileEntity implements IPacketHandler {

	private final static int[][] rotation_matrix = { // 0 DOWN, 1 UP, 2 NORTH, 3
														// SOUTH, 4 WEST, 5 EAST
			{ 2, 5, 3, 4 }, // Down
			{ 2, 4, 3, 5 }, // Up

			{ 0, 5, 1, 4 }, // North
			{ 0, 4, 1, 5 }, // South

			{ 0, 3, 1, 2 }, // East
			{ 0, 2, 1, 3 } // West
	};

	private boolean[] state = new boolean[6];
	private boolean[][] edges = new boolean[6][4];
	private int[] edges_count = new int[6];
	private int[] tile_rotation = new int[6];

	public TileEntityLanteaDecorGlass getGlassAt(int x, int y, int z) {
		if (y >= 0 && worldObj.getHeight() > y) {
			TileEntity tile = worldObj.getBlockTileEntity(x, y, z);
			if (tile instanceof TileEntityLanteaDecorGlass)
				return (TileEntityLanteaDecorGlass) tile;
		}
		return null;
	}

	public void neighbourChanged() {
		if (!worldObj.isRemote) {
			Vector3 location = new Vector3(this);

			for (int dir = 0; dir < 6; dir++) {
				Vector3 fd = location.add(new Vector3(ForgeDirection.getOrientation(dir)));
				TileEntityLanteaDecorGlass that = getGlassAt(fd.floorX(), fd.floorY(), fd.floorZ());
				state[dir] = (that != null);
			}

			for (int dir = 0; dir < 6; dir++) {
				int[] matrix = rotation_matrix[dir];
				edges_count[dir] = 0;
				for (int perp = 0; perp < 4; perp++) {
					int vd = matrix[perp];
					Vector3 fd = location.add(new Vector3(ForgeDirection.getOrientation(vd)));
					TileEntityLanteaDecorGlass that = getGlassAt(fd.floorX(), fd.floorY(), fd.floorZ());
					edges[dir][perp] = (that != null);
					if (that == null)
						edges_count[dir] += 1;
				}
			}

			for (int dir = 0; dir < 6; dir++) {
				int renderEdges = edges_count[dir];

				if (renderEdges == 1) {
					for (int i = 0; i < 4; i++)
						if (!edges[dir][i]) {
							tile_rotation[dir] = i;
							break;
						}
				} else if (renderEdges == 2) {
					int i = -1, j = -1;
					for (int k = 0; k < 4; k++)
						for (int l = 0; l < 4; l++)
							if (!edges[dir][l])
								if (i == -1)
									i = l;
								else
									j = l;
					if (i == 0 && j == 1)
						tile_rotation[dir] = 0;
					else if (i == 1 && j == 2)
						tile_rotation[dir] = 1;
					else if (i == 2 && j == 3)
						tile_rotation[dir] = 2;
					else if (i == 3 && j == 0)
						tile_rotation[dir] = 3;
					else if (i == 0 && j == 2) {
						tile_rotation[dir] = 1;
						edges_count[dir] = 5;
					} else if (i == 1 && j == 3) {
						tile_rotation[dir] = 0;
						edges_count[dir] = 5;
					} else
						LanteaCraft.getLogger().log(Level.INFO, "Don't know how to handle this!");
				} else if (renderEdges == 3) {
					for (int i = 0; i < 4; i++)
						if (edges[dir][i]) {
							tile_rotation[dir] = i;
							break;
						}
				} else if (renderEdges == 4) {
					tile_rotation[dir] = 0;
					continue;
				}
			}
			getDescriptionPacket();
		}
	}

	public boolean renderSide(int dir) {
		if (0 > dir || dir > 6)
			return false;
		return !state[dir];
	}

	public int sideType(int dir) {
		if (0 > dir || dir > 6)
			return 0;
		return edges_count[dir];
	}

	public int sideRotation(int dir) {
		if (0 > dir || dir > 6)
			return 0;
		return tile_rotation[dir];
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("seamData")) {
			NBTTagCompound root = tag.getCompoundTag("seamData");
			for (int i = 0; i < 6; i++) {
				state[i] = root.getBoolean(String.format("state_%s", i));
				edges_count[i] = root.getInteger(String.format("edges_count_%s", i));
				tile_rotation[i] = root.getInteger(String.format("tile_rotation_%s", i));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound root = new NBTTagCompound();
		for (int i = 0; i < 6; i++) {
			root.setBoolean(String.format("state_%s", i), state[i]);
			root.setInteger(String.format("edges_count_%s", i), edges_count[i]);
			root.setInteger(String.format("tile_rotation_%s", i), tile_rotation[i]);
		}
		tag.setTag("seamData", root);
	}

	@Override
	public void updateEntity() {
	}

	@Override
	public Packet getDescriptionPacket() {
		try {
			TinyModPacket packet = new TinyModPacket(new WorldLocation(this));
			DataOutputStream stream = packet.getOut();
			for (int i = 0; i < 6; i++) {
				stream.write((state[i]) ? 1 : 0);
				stream.writeInt(edges_count[i]);
				stream.writeInt(tile_rotation[i]);
			}
			LanteaCraft.getProxy().sendToAllPlayers(packet);
			return null;
		} catch (IOException ioex) {
			LanteaCraft.getLogger().log(Level.WARNING, "Error creating description packet.", ioex);
			return null;
		}
	}

	@Override
	public void handlePacket(ModPacket packetOf) {
		if (worldObj.isRemote)
			if (packetOf instanceof TinyModPacket)
				try {
					TinyModPacket descriptor = (TinyModPacket) packetOf;
					DataInputStream stream = descriptor.getIn();
					for (int i = 0; i < 6; i++) {
						state[i] = (stream.readByte() == 1);
						edges_count[i] = stream.readInt();
						tile_rotation[i] = stream.readInt();
					}
				} catch (IOException ioex) {
					LanteaCraft.getLogger().log(Level.WARNING, "Error unpacking description packet.", ioex);
				}
	}

}
