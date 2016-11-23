package lc.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import lc.api.rendering.ITileRenderInfo;
import lc.common.LCLog;
import lc.common.base.LCTile;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.util.data.StateMap;

public class TileLanteaDoor extends LCTile implements ITileRenderInfo {

	/** TODO: Port property */
	public boolean clientLastState;
	/** TODO: Port property */
	public int clientAnimation;

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public IInventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void thinkClient() {
		if (clientAnimation != 0)
			clientAnimation--;
	}

	@Override
	public void thinkServer() {
		// TODO Auto-generated method stub

	}

	@Override
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
		if (worldObj.isRemote) {
			if (clientLastState != getDoorState()) {
				clientLastState = getDoorState();
				clientAnimation += 20;
			}
		}

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
		recalculateState();
	}

	@Override
	public String[] debug(Side side) {
		return new String[] {
				String.format("hasBlockBelow: %s", ((compound == null || !compound.hasKey("hasBlockBelow")) ? "??"
						: compound.getBoolean("hasBlockBelow"))),
				String.format("isOpen: %s",
						((compound == null || !compound.hasKey("isOpen")) ? "??" : compound.getBoolean("isOpen"))),
				String.format("hasBlockAbove: %s", ((compound == null || !compound.hasKey("hasBlockAbove")) ? "??"
						: compound.getBoolean("hasBlockAbove"))), String.format("getRotation: %s", getRotation()) };
	}

	@Override
	public ITileRenderInfo renderInfoTile() {
		return (ITileRenderInfo) this;
	}

	@Override
	public StateMap tileRenderState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object tileAnimation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double tileAnimationProgress() {
		// TODO Auto-generated method stub
		return 0;
	}

	private void recalculateState() {
		if (compound == null)
			compound = new NBTTagCompound();
		compound.setBoolean("hasBlockBelow",
				worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof TileLanteaDoor);
		compound.setBoolean("hasBlockAbove",
				worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) instanceof TileLanteaDoor);
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
		default:
			LCLog.fatal("Invalid door state rotation!");
			break;
		}
	}

	public boolean getDoorState() {
		if (compound == null)
			compound = new NBTTagCompound();
		return compound.hasKey("isOpen") ? compound.getBoolean("isOpen") : false;
	}

	public boolean hasBlockBelow() {
		if (compound == null)
			compound = new NBTTagCompound();
		return compound.hasKey("hasBlockBelow") ? compound.getBoolean("hasBlockBelow") : false;
	}

	public boolean hasBlockAbove() {
		if (compound == null)
			compound = new NBTTagCompound();
		return compound.hasKey("hasBlockAbove") ? compound.getBoolean("hasBlockAbove") : false;
	}

	public ForgeDirection getMotionDirection() {
		switch (getRotation()) {
		case NORTH:
			return ForgeDirection.WEST;
		case EAST:
			return ForgeDirection.NORTH;
		case SOUTH:
			return ForgeDirection.EAST;
		case WEST:
			return ForgeDirection.SOUTH;
		}
		LCLog.fatal("Invalid door state rotation!");
		return null;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return getBoundingBox(false);
	}

	public AxisAlignedBB getBoundingBox(boolean clip) {
		float w = 0.085f;
		float d0 = 0.5f - w, d1 = 0.5f + w;
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(-0.4d, -0.4d, -0.4d, 1.4d, 1.4d, 1.4d);
		if (clientAnimation != 0)
			return (clip) ? null : AxisAlignedBB.getBoundingBox(xCoord + box.minX, yCoord + box.minY,
					zCoord + box.minZ, xCoord + box.maxX, yCoord + box.maxY, zCoord + box.maxZ);
		if (!getDoorState()) {
			switch (getRotation()) {
			case NORTH:
			case SOUTH:
				box = AxisAlignedBB.getBoundingBox(0, 0, d0, 1, 1, d1);
				break;
			case EAST:
			case WEST:
				box = AxisAlignedBB.getBoundingBox(d0, 0, 0, d1, 1, 1);
				break;
			default:
				LCLog.fatal("Invalid door state rotation!");
			}
		} else {
			switch (getRotation()) {
			case NORTH:
				box = AxisAlignedBB.getBoundingBox(0.11d, 0, 0.11d - d0, 0.135d + 0.15d, 1, 0.11d + d1);
				break;
			case SOUTH:
				box = AxisAlignedBB.getBoundingBox(0.85d - 0.135d, 0, d0 - 0.11d, 1.0d - 0.11d, 1, d1 + 0.711d);
				break;
			case EAST:
				box = AxisAlignedBB.getBoundingBox(d0 - 0.11d, 0, 0.11d, d1 + 0.711d, 1, 0.135d + 0.15d);
				break;
			case WEST:
				box = AxisAlignedBB.getBoundingBox(0.11d - d0, 0, 0.85d - 0.135d, d1 + 0.11d, 1, 1.0d - 0.11d);
				break;
			default:
				LCLog.fatal("Invalid door state rotation!");
			}
		}
		if (!clip)
			return AxisAlignedBB.getBoundingBox(xCoord + box.minX, yCoord + box.minY, zCoord + box.minZ, xCoord
					+ box.maxX, yCoord + box.maxY, zCoord + box.maxZ);
		return box;
	}

	public void setRedstoneState(int strength) {
		if (compound == null)
			compound = new NBTTagCompound();
		boolean flag0 = false, flag1 = false;
		if (!compound.hasKey("redstoneSignal")) {
			flag0 = true;
			compound.setInteger("redstoneSignal", strength);
		} else {
			int what = compound.getInteger("redstoneSignal");
			if (what != strength) {
				flag0 = true;
				compound.setInteger("redstoneSignal", strength);
				flag1 = (strength != 0);
			}
		}
		if (flag0)
			setDoorState(flag1);
	}

}
