package pcl.lc.multiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.common.multiblock.GenericMultiblock;
import pcl.common.multiblock.MultiblockPart;
import pcl.common.network.ModPacket;
import pcl.lc.tileentity.TileEntityTransporterRing;

public class TransporterRingMultiblock extends GenericMultiblock {

	private boolean modified = false;

	public TransporterRingMultiblock(TileEntity host) {
		super(host);
	}

	@Override
	public void tick() {
		super.tick();
		if (!isClient && modified) {
			modified = !modified;
			host.getDescriptionPacket();
		}
	}

	@Override
	public boolean isValidStructure(World worldAccess, int baseX, int baseY, int baseZ) {
		int dx = -1, dz = -1;
		for (int x = 0; x < 10; x++) {
			for (int z = 0; z < 10; z++) {
				TileEntity tile = worldAccess.getTileEntity(x - 4, baseY, z - 4);
				if (tile != null && tile instanceof TileEntityTransporterRing) {
					dx = x;
					dz = z;
					break;
				}

			}
		}
		
		if (dx == -1 || dz == -1)
			return false;
		for (int x = 0; x < 5; x++) 
			for (int z = 0; z < 5; z++) {
				TileEntity tile = worldAccess.getTileEntity(dx + x, baseY, dz + z);
				if (tile == null || !(tile instanceof TileEntityTransporterRing))
					return false;
			}
		return true;
	}

	@Override
	public boolean collectStructure(World worldAccess, int baseX, int baseY, int baseZ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void freeStructure() {
		// TODO Auto-generated method stub

	}

	@Override
	public MultiblockPart getPart(Object reference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MultiblockPart[] getAllParts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validated(boolean oldState, boolean newState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disband() {
		// TODO Auto-generated method stub

	}

	@Override
	public ModPacket pack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpack(ModPacket packet) {
		// TODO Auto-generated method stub

	}

	@Override
	public ModPacket pollForUpdate() {
		// TODO Auto-generated method stub
		return null;
	}

}
