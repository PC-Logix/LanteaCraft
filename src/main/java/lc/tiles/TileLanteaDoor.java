package lc.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import lc.common.base.LCTile;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;

public class TileLanteaDoor extends LCTile {

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
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldRender() {
		return true;
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
		return new String[] {
				String.format("hasBlockBelow: %s", ((compound == null || !compound.hasKey("hasBlockBelow")) ? "??"
						: compound.getBoolean("hasBlockBelow"))),
				String.format("isOpen: %s",
						((compound == null || !compound.hasKey("isOpen")) ? "??" : compound.getBoolean("isOpen")))

		};
	}

	private void recalculateState() {
		if (compound == null)
			compound = new NBTTagCompound();
		compound.setBoolean("hasBlockBelow",
				worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof TileLanteaDoor);
		markNbtDirty();
	}

	@Override
	public void blockPlaced() {
		super.blockPlaced();
		recalculateState();
	};

	@Override
	public void neighborChanged() {
		super.neighborChanged();
		recalculateState();
	}

	public void openOrCloseDoor() {
		setDoorState(!getDoorState());
	}

	public void setDoorState(boolean state) {
		if (getDoorState() == state)
			return;
		if (compound == null)
			compound = new NBTTagCompound();
		compound.setBoolean("isOpen", state);
		markNbtDirty();
		switch (getRotation()) {
		case NORTH:
		case SOUTH:
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					TileEntity tile = worldObj.getTileEntity(xCoord + x, yCoord + y, zCoord);
					if (tile instanceof TileLanteaDoor)
						((TileLanteaDoor) tile).setDoorState(state);
				}
			}
			break;
		case EAST:
		case WEST:
			for (int z = -1; z <= 1; z++) {
				for (int y = -1; y <= 1; y++) {
					TileEntity tile = worldObj.getTileEntity(xCoord, yCoord + y, zCoord + z);
					if (tile instanceof TileLanteaDoor)
						((TileLanteaDoor) tile).setDoorState(state);
				}
			}
			break;
		}
	}

	public boolean getDoorState() {
		if (compound == null)
			compound = new NBTTagCompound();
		return compound.hasKey("isOpen") ? compound.getBoolean("isOpen") : false;
	}

	public AxisAlignedBB getBoundingBox() {
		float w = 0.085f;
		float d0 = 0.5f - w, d1 = 0.5f + w;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);
		switch (getRotation()) {
		case NORTH:
		case SOUTH:
			box = AxisAlignedBB.getBoundingBox(0, 0, d0, 1, 1, d1);
			if (getDoorState())
				box.offset(0.85, 0, 0);
			break;
		case EAST:
		case WEST:
			box = AxisAlignedBB.getBoundingBox(d0, 0, 0, d1, 1, 1);
			if (getDoorState())
				box.offset(0, 0, 0.85);
			break;
		}
		return box;
	}

}
