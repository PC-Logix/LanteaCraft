package lc.tiles;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import lc.api.components.IntegrationType;
import lc.api.drivers.DeviceDrivers.DriverCandidate;
import lc.common.base.multiblock.LCMultiblockTile;
import lc.common.base.multiblock.MultiblockState;
import lc.common.base.multiblock.StructureConfiguration;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.util.game.BlockFilter;
import lc.common.util.math.Orientations;
import lc.common.util.math.Vector3;
import lc.core.LCRuntime;

/**
 * Stargate Base tile implementation.
 * 
 * @author AfterLifeLochie
 * 
 */
@DriverCandidate(types = { IntegrationType.POWER })
public class TileStargateBase extends LCMultiblockTile {

	private final static StructureConfiguration structure = new StructureConfiguration() {

		private final BlockFilter[] filters = new BlockFilter[] { new BlockFilter(Blocks.air),
				new BlockFilter(LCRuntime.runtime.blocks().stargateRingBlock.getBlock(), 0),
				new BlockFilter(LCRuntime.runtime.blocks().stargateRingBlock.getBlock(), 1),
				new BlockFilter(LCRuntime.runtime.blocks().stargateBaseBlock.getBlock()) };

		@Override
		public Vector3 getStructureDimensions() {
			return new Vector3(1, 7, 7);
		}

		@Override
		public Vector3 getStructureCenter() {
			return new Vector3(0, 0, 4);
		}

		@Override
		public int[][][] getStructureLayout() {
			return new int[][][] { { { 1, 2, 1, 3, 1, 2, 1 }, { 2, 0, 0, 0, 0, 0, 2 }, { 1, 0, 0, 0, 0, 0, 1 },
					{ 1, 0, 0, 0, 0, 0, 1 }, { 2, 0, 0, 0, 0, 0, 2 }, { 1, 0, 0, 0, 0, 0, 1 }, { 1, 2, 1, 2, 1, 2, 1 } } };
		}

		@Override
		public BlockFilter[] getBlockMappings() {
			return filters;
		}
	};

	@Override
	public StructureConfiguration getConfiguration() {
		return structure;
	}

	@Override
	public void thinkMultiblock() {
		if (getState() == MultiblockState.NONE) {
			Orientations rotation = Orientations.from(getRotation());
			if (structure.test(getWorldObj(), xCoord, yCoord, zCoord, rotation))
				changeState(MultiblockState.FORMED);
		} else {
			Orientations rotation = Orientations.from(getRotation());
			if (!structure.test(getWorldObj(), xCoord, yCoord, zCoord, rotation))
				changeState(MultiblockState.NONE);
		}
	}

	@Override
	public IInventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void thinkClient() {
		// TODO Auto-generated method stub

	}

	@Override
	public void thinkServer() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendPackets(List<LCPacket> packets) throws LCNetworkException {
		super.sendPackets(packets);
	}

	@Override
	public String[] debug(Side side) {
		return new String[] { String.format("Rotation: %s", getRotation()), String.format("Multiblock: %s", getState()) };
	}

}
