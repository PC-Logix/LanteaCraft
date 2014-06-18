package pcl.lc.multiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.common.multiblock.GenericMultiblock;
import pcl.common.multiblock.MultiblockPart;
import pcl.common.network.ModPacket;

public class TransporterRingMultiblock extends GenericMultiblock {

	public TransporterRingMultiblock(TileEntity host) {
		super(host);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isValidStructure(World worldAccess, int baseX, int baseY, int baseZ) {
		// TODO Auto-generated method stub
		return false;
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
