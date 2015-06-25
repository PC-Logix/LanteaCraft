package lc.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import lc.LCRuntime;
import lc.common.base.LCTile;
import lc.common.base.multiblock.LCMultiblockTile;
import lc.common.base.multiblock.MultiblockState;
import lc.common.base.multiblock.StructureConfiguration;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.util.game.BlockFilter;
import lc.common.util.math.Orientations;
import lc.common.util.math.Vector3;

public class TileTransportRing extends LCMultiblockTile {

	public final static StructureConfiguration structure = new StructureConfiguration() {

		private final BlockFilter[] filters = new BlockFilter[] {
				new BlockFilter(LCRuntime.runtime.blocks().frameBlock.getBlock(), 0),
				new BlockFilter(LCRuntime.runtime.blocks().transporterBlock.getBlock(), 0) };

		@Override
		public Vector3 getStructureDimensions() {
			return new Vector3(3, 1, 3);
		}

		@Override
		public Vector3 getStructureCenter() {
			return new Vector3(1, 0, 1);
		}

		@Override
		public int[][][] getStructureLayout() {
			return new int[][][] { { { 0, 0, 0 } }, { { 0, 1, 0 } }, { { 0, 0, 0 } } };
		}

		@Override
		public BlockFilter[] getBlockMappings() {
			return filters;
		}

	};

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public StructureConfiguration getConfiguration() {
		return TileTransportRing.structure;
	}

	@Override
	public void thinkMultiblock() {
		if (getState() == MultiblockState.NONE) {
			if (structure.test(getWorldObj(), xCoord, yCoord, zCoord, null)) {
				changeState(MultiblockState.FORMED);
				structure.apply(getWorldObj(), xCoord, yCoord, zCoord, null, this);
			}
		} else {
			if (!structure.test(getWorldObj(), xCoord, yCoord, zCoord, null)) {
				changeState(MultiblockState.NONE);
				structure.apply(getWorldObj(), xCoord, yCoord, zCoord, null, null);
			}
		}
	}

	@Override
	public IInventory getInventory() {
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
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldRender() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void save(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] debug(Side side) {
		return new String[] { String.format("Multiblock: %s", getState()) };
	}

}
