package pcl.lc.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import pcl.common.base.GenericTileEntity;
import pcl.common.helpers.ScanningHelper;
import pcl.common.network.IPacketHandler;
import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import pcl.common.util.Vector3;
import pcl.common.util.WorldLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumRingPlatformState;

public class TileEntityRingPlatform extends GenericTileEntity implements IPacketHandler {

	private final double ringExtended = 2.5d;

	private EnumRingPlatformState state = EnumRingPlatformState.Idle;
	private int timeout;
	private Vector3 connectionTo;

	private double ringPosition, nextRingPosition;

	private ArrayList<Entity> boundingEntities;

	public TileEntityRingPlatform() {
		boundingEntities = new ArrayList<Entity>();
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			updateRendering();
		else if (state != EnumRingPlatformState.Idle || timeout != 0)
			if (timeout > 0)
				timeout--;
			else if (state == EnumRingPlatformState.Connecting) {
				updateState(EnumRingPlatformState.Transmitting, 2);
				buildTeleportingEntityList();
			} else if (state == EnumRingPlatformState.Transmitting) {
				updateState(EnumRingPlatformState.Connected, 8);
				teleportEntitiesInList();
			} else if (state == EnumRingPlatformState.Connected)
				updateState(EnumRingPlatformState.Disconnecting, 20);
			else if (state == EnumRingPlatformState.Disconnecting) {
				clearConnection();
				updateState(EnumRingPlatformState.Idle, 0);
			}
	}

	private void updateRendering() {
		if (0 > ringPosition)
			ringPosition = 0;
		ringPosition += nextRingPosition;
		if (ringPosition > ringExtended)
			ringPosition = ringExtended;
		if (timeout > 0)
			if (state == EnumRingPlatformState.Connecting)
				nextRingPosition = (ringExtended / 20.0d);
			else if (state == EnumRingPlatformState.Disconnecting)
				nextRingPosition = -(ringExtended / 20.0d);
			else
				nextRingPosition = 0;
	}

	public double getRingPosition(float partialticks) {
		double next = ringPosition + partialticks * nextRingPosition;
		if (next > ringExtended)
			return ringExtended;
		return next;
	}

	public void performConnection(Vector3 slaveObject) {
		connectionTo = slaveObject;
		updateState(EnumRingPlatformState.Connecting, 20);
	}

	public void clearConnection() {
		connectionTo = null;
	}

	public boolean isBusy() {
		return state != EnumRingPlatformState.Idle;
	}

	private void updateState(EnumRingPlatformState state, int timeout) {
		this.state = state;
		this.timeout = timeout;
		markBlockForUpdate();
	}

	public void getStateFromPacket(ModPacket packet) {
		StandardModPacket packetOf = (StandardModPacket) packet;
		timeout = (Integer) packetOf.getValue("timeout");
		state = (EnumRingPlatformState) packetOf.getValue("state");
	}

	public ModPacket getPacketFromState() {
		StandardModPacket packet = new StandardModPacket(new WorldLocation(this));
		packet.setIsForServer(false);
		packet.setType("LanteaPacket.TileUpdate");
		packet.setValue("timeout", timeout);
		packet.setValue("state", state);
		return packet;
	}

	@Override
	public Packet getDescriptionPacket() {
		LanteaCraft.getProxy().sendToAllPlayers(getPacketFromState());
		return null;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord - 4, yCoord, zCoord - 4, xCoord + 4, yCoord + 7, zCoord + 4);
	}

	public void connect() {
		ArrayList<Vector3> others = ScanningHelper.findAllTileEntitesOf(worldObj, TileEntityRingPlatform.class, xCoord,
				yCoord, zCoord, AxisAlignedBB.getBoundingBox(-10, -yCoord, -10, 10, worldObj.getHeight(), 10));
		Vector3 vectorHere = new Vector3(this);
		for (Vector3 other : others) {
			TileEntity at = worldObj.getBlockTileEntity(xCoord + (int) Math.floor(other.x),
					yCoord + (int) Math.floor(other.y), zCoord + (int) Math.floor(other.z));
			if ((at instanceof TileEntityRingPlatform) && !at.equals(this)) {
				TileEntityRingPlatform that = (TileEntityRingPlatform) at;
				if (!that.isBusy()) {
					performConnection(other.add(vectorHere));
					that.performConnection(vectorHere);
					return;
				}
			}
		}
	}

	private TileEntityRingPlatform getSlave() {
		return (TileEntityRingPlatform) worldObj.getBlockTileEntity((int) Math.floor(connectionTo.x),
				(int) Math.floor(connectionTo.y), (int) Math.floor(connectionTo.z));
	}

	public void buildTeleportingEntityList() {
		boundingEntities.clear();
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(xCoord - 2, yCoord, zCoord - 2, xCoord + 2, yCoord + 3,
				zCoord + 2);
		List<Entity> ents = worldObj.getEntitiesWithinAABB(Entity.class, bounds);
		for (Entity entity : ents)
			if (!entity.isDead && entity.ridingEntity == null)
				boundingEntities.add(entity);
	}

	private void teleportEntitiesInList() {
		for (Entity entity : boundingEntities)
			entityInPortal(entity);
		boundingEntities.clear();
	}

	private void entityInPortal(Entity entity) {
		TileEntityRingPlatform dte = getSlave();
		if (dte != null) {
			while (entity.ridingEntity != null)
				entity = entity.ridingEntity;
			Vector3 host = new Vector3(this);
			Vector3 entPos = new Vector3(entity);
			Vector3 dest = new Vector3(dte);
			Vector3 output = dest.add(entPos.sub(host));
			teleportEntityAndRider(entity, output);
		}
	}

	Entity teleportEntityAndRider(Entity entity, Vector3 destination) {
		Entity rider = entity.riddenByEntity;
		if (rider != null)
			rider.mountEntity(null);
		entity = teleportWithinDimension(entity, destination);
		if (rider != null) {
			rider = teleportEntityAndRider(rider, destination);
			rider.mountEntity(entity);
			entity.forceSpawn = false;
		}
		return entity;
	}

	Entity teleportWithinDimension(Entity entity, Vector3 destination) {
		if (entity instanceof EntityPlayerMP)
			((EntityPlayerMP) entity).setPositionAndUpdate(destination.x, destination.y, destination.z);
		else
			entity.setPosition(destination.x, destination.y, destination.z);
		System.out.println("done!");
		entity.worldObj.updateEntityWithOptionalForce(entity, false);
		return entity;
	}

	@Override
	public void handlePacket(ModPacket packetOf) {
		getStateFromPacket(packetOf);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

}
